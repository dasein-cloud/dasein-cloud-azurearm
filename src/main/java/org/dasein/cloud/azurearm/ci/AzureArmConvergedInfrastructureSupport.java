/**
 * Copyright (C) 2009-2016 Dell, Inc.
 * See annotations for authorship information
 * <p>
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.dasein.cloud.azurearm.ci;

import org.apache.log4j.Logger;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ResourceNotFoundException;
import org.dasein.cloud.ResourceType;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmLocation;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.ci.model.ArmConvergedInfrastructureRequestModel;
import org.dasein.cloud.azurearm.ci.model.ArmConvergedInfrastructureResponseModel;
import org.dasein.cloud.azurearm.ci.model.ArmConvergedInfrastructuresResponseModel;
import org.dasein.cloud.ci.AbstractConvergedInfrastructureSupport;
import org.dasein.cloud.ci.ConvergedInfrastructure;
import org.dasein.cloud.ci.ConvergedInfrastructureCapabilities;
import org.dasein.cloud.ci.ConvergedInfrastructureFilterOptions;
import org.dasein.cloud.ci.ConvergedInfrastructureProvisionOptions;
import org.dasein.cloud.ci.ConvergedInfrastructureResource;
import org.dasein.cloud.ci.ConvergedInfrastructureState;
import org.dasein.cloud.dc.ResourcePool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * User: daniellemayne
 * Date: 14/03/2016
 * Time: 11:29
 */
public class AzureArmConvergedInfrastructureSupport extends AbstractConvergedInfrastructureSupport<AzureArm> {
    static private final Logger logger       = Logger.getLogger(AzureArmConvergedInfrastructureSupport.class);

    public AzureArmConvergedInfrastructureSupport(AzureArm provider) {
        super(provider);
    }

    @Nonnull
    @Override
    public ConvergedInfrastructureCapabilities getCapabilities() throws InternalException, CloudException {
        return new AzureArmConvergedInfrastructureCapabilities(this.getProvider());
    }

    @Override
    public boolean isSubscribed() throws CloudException, InternalException {
        return true;
    }

    @Nonnull
    @Override
    public Iterable<ConvergedInfrastructure> listConvergedInfrastructures(@Nullable ConvergedInfrastructureFilterOptions options) throws CloudException, InternalException {
        final ArrayList<ConvergedInfrastructure> cis = new ArrayList<ConvergedInfrastructure>();

        String resourceGroupName = "";
        boolean continueSearchingResourceGroups = true;
        AzureArmLocation locationSupport = this.getProvider().getDataCenterServices();
        Iterable<ResourcePool> resourceGroups = locationSupport.listResourcePools("ignore");

        for (ResourcePool resourceGroup : resourceGroups) {
            if (continueSearchingResourceGroups) {
                if ( options == null || options.getResourceGroupId() == null ) {
                    resourceGroupName = resourceGroup.getName();
                } else {
                    if ( resourceGroup.getProvideResourcePoolId().equals(options.getResourceGroupId()) ) {
                        resourceGroupName = resourceGroup.getName();
                        continueSearchingResourceGroups = false;
                    }
                    else {
                        continue;
                    }
                }
                ArmConvergedInfrastructuresResponseModel armConvergedInfrastructuresResponseModel =
                        new AzureArmRequester(this.getProvider(),
                                new AzureArmConvergedInfrastructureRequests(this.getProvider()).listTemplateDeployments(resourceGroupName).build())
                                .withJsonProcessor(ArmConvergedInfrastructuresResponseModel.class).execute();


                for (ArmConvergedInfrastructureResponseModel acim : armConvergedInfrastructuresResponseModel.getArmConvergedInfrastructureModels()) {
                    acim.setProviderDatacenterId(resourceGroup.getDataCenterId());
                    acim.setProviderResourceGroupId(resourceGroup.getProvideResourcePoolId());
                    acim.setProviderRegionId(resourceGroup.getDataCenterId().substring(0, resourceGroup.getDataCenterId().lastIndexOf("-")));
                    ConvergedInfrastructure ci = convergedInfrastructureFrom(acim);
                    if ( options == null || options.matches(ci) ) {
                        cis.add(ci);
                    }
                }
            }
        }

        return cis;
    }

    @Nonnull
    @Override
    public ConvergedInfrastructure provision(@Nonnull ConvergedInfrastructureProvisionOptions options) throws CloudException, InternalException {
        if (options == null) {
            throw new InternalException("ConvergedInfrastructureProvisionOptions parameter cannot be null");
        }
        if (options.getResourceGroupId() == null) {
            throw new InternalException("Resource group is required for provisioning of converged infrastructure");
        }
        AzureArmLocation locationSupport = this.getProvider().getDataCenterServices();
        ResourcePool resourceGroup = locationSupport.getResourcePool(options.getResourceGroupId());
        String resourceGroupName = null;
        if ( resourceGroup != null ) {
            resourceGroupName = resourceGroup.getName();
        }
        else {
            throw new ResourceNotFoundException("Resource group", options.getResourceGroupId());
        }

        ArmConvergedInfrastructureRequestModel armConvergedInfrastructureModel = new ArmConvergedInfrastructureRequestModel();
        ArmConvergedInfrastructureRequestModel.Properties ciProperties = new ArmConvergedInfrastructureRequestModel.Properties();
        if (options.getMode() == null) {
            ciProperties.setMode("Incremental");
        }
        else {
            ciProperties.setMode(options.getMode());
        }
        if (options.isTemplateContentProvided()) {
            ciProperties.setTemplate(options.getTemplate());
            ciProperties.setParameters(options.getParameters());
        }
        else {
            ciProperties.setTemplateLink(options.getTemplate());
            ciProperties.setParametersLink(options.getParameters());
        }
        armConvergedInfrastructureModel.setProperties(ciProperties);

        ArmConvergedInfrastructureResponseModel armConvergedInfrastructureModelResult =
                new AzureArmRequester(this.getProvider(), new AzureArmConvergedInfrastructureRequests(this.getProvider())
                        .createTemplateDeployment(armConvergedInfrastructureModel, resourceGroupName, options.getName()).build())
                        .withJsonProcessor(ArmConvergedInfrastructureResponseModel.class).execute();

        armConvergedInfrastructureModelResult.setProviderDatacenterId(resourceGroup.getDataCenterId());
        armConvergedInfrastructureModelResult.setProviderResourceGroupId(resourceGroup.getProvideResourcePoolId());
        armConvergedInfrastructureModelResult.setProviderRegionId(resourceGroup.getDataCenterId().substring(0, resourceGroup.getDataCenterId().lastIndexOf("-")));

        return convergedInfrastructureFrom(armConvergedInfrastructureModelResult);
    }

    @Override
    public void terminate(@Nonnull String ciId, @Nullable String explanation) throws CloudException, InternalException {
        new AzureArmRequester(this.getProvider(), new AzureArmConvergedInfrastructureRequests(this.getProvider()).deleteTemplateDeployment(ciId).build()).execute();
    }

    @Override
    public void cancelDeployment(@Nonnull String ciId, @Nullable String explanation) throws CloudException, InternalException {
        new AzureArmRequester(this.getProvider(), new AzureArmConvergedInfrastructureRequests(this.getProvider()).cancelTemplateDeployment(ciId).build()).execute();
    }

    @Override
    public ConvergedInfrastructure validateDeployment(@Nonnull ConvergedInfrastructureProvisionOptions options) throws CloudException, InternalException {
        if (options == null) {
            throw new InternalException("ConvergedInfrastructureProvisionOptions parameter cannot be null");
        }
        if (options.getResourceGroupId() == null) {
            throw new InternalException("Resource group is required for provisioning of converged infrastructure");
        }

        AzureArmLocation locationSupport = this.getProvider().getDataCenterServices();
        ResourcePool resourceGroup = locationSupport.getResourcePool(options.getResourceGroupId());
        String resourceGroupName = "" ;
        if ( resourceGroup != null ) {
            resourceGroupName = resourceGroup.getName();
        }
        else {
            throw new ResourceNotFoundException("Resource group", options.getResourceGroupId());
        }

        ArmConvergedInfrastructureRequestModel armConvergedInfrastructureModel = new ArmConvergedInfrastructureRequestModel();
        ArmConvergedInfrastructureRequestModel.Properties ciProperties = new ArmConvergedInfrastructureRequestModel.Properties();
        ciProperties.setMode("Incremental");
        if (options.isTemplateContentProvided()) {
            ciProperties.setTemplate(options.getTemplate());
            ciProperties.setParameters(options.getParameters());
        }
        else {
            ciProperties.setTemplateLink(options.getTemplate());
            ciProperties.setParametersLink(options.getParameters());
        }
        armConvergedInfrastructureModel.setProperties(ciProperties);

        ArmConvergedInfrastructureResponseModel armConvergedInfrastructureModelResult =
                new AzureArmRequester(this.getProvider(), new AzureArmConvergedInfrastructureRequests(this.getProvider())
                        .validateTemplateDeployment(armConvergedInfrastructureModel, resourceGroupName, options.getName()).build())
                        .withJsonProcessor(ArmConvergedInfrastructureResponseModel.class).execute();

        armConvergedInfrastructureModelResult.setProviderDatacenterId(resourceGroup.getDataCenterId());
        armConvergedInfrastructureModelResult.setProviderResourceGroupId(resourceGroup.getProvideResourcePoolId());
        armConvergedInfrastructureModelResult.setProviderRegionId(resourceGroup.getDataCenterId().substring(0, resourceGroup.getDataCenterId().lastIndexOf("-")));

        return convergedInfrastructureFrom(armConvergedInfrastructureModelResult);
    }

    private ConvergedInfrastructure convergedInfrastructureFrom(ArmConvergedInfrastructureResponseModel armConvergedInfrastructureModel) throws InternalException{
        String id = armConvergedInfrastructureModel.getId();
        String name = armConvergedInfrastructureModel.getName();
        String regionId = armConvergedInfrastructureModel.getProviderRegionId();
        String datacenterID = armConvergedInfrastructureModel.getProviderDatacenterId();
        String resourceGroupName = armConvergedInfrastructureModel.getProviderResourceGroupId();
        Map<String,String> tags = new HashMap<String,String>();

        ConvergedInfrastructureState state = ConvergedInfrastructureState.PENDING;
        long timestamp = System.currentTimeMillis();
        List<ConvergedInfrastructureResource> list = new ArrayList<ConvergedInfrastructureResource>();

        if (armConvergedInfrastructureModel.getProperties() != null) {
            tags = armConvergedInfrastructureModel.getProperties().getTags();
            if (armConvergedInfrastructureModel.getProperties().getCiState() != null) {
                String ciState = armConvergedInfrastructureModel.getProperties().getCiState();
                switch (ciState.toLowerCase()) {
                    case "accepted":
                        state = ConvergedInfrastructureState.ACCEPTED;
                        break;
                    case "ready":
                        state = ConvergedInfrastructureState.READY;
                        break;
                    case "cancelled":
                        state = ConvergedInfrastructureState.CANCELLED;
                        break;
                    case "failed":
                        state = ConvergedInfrastructureState.FAILED;
                        break;
                    case "deleted":
                        state = ConvergedInfrastructureState.DELETED;
                        break;
                    case "succeeded":
                        state = ConvergedInfrastructureState.SUCCEEDED;
                        break;
                }
            }

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            fmt.setCalendar(Calendar.getInstance(new SimpleTimeZone(0, "GMT")));
            String provisioningTimestamp = armConvergedInfrastructureModel.getProperties().getProvisioningTimestamp();

            try {
                timestamp = fmt.parse(provisioningTimestamp).getTime();
            }
            catch ( ParseException e ) {
                logger.warn(e);
                timestamp = System.currentTimeMillis();
            }
            if (armConvergedInfrastructureModel.getProperties().getDependencies() != null) {
                List<ArmConvergedInfrastructureResponseModel.Properties.Dependency> resources = armConvergedInfrastructureModel.getProperties().getDependencies();
                for (ArmConvergedInfrastructureResponseModel.Properties.Dependency d : resources) {
                    String r = d.getOutputResourceId();
                    String[] tokens = r.split("/");
                    String subtype = "";
                    String resourceId = "";
                    if (tokens.length == 3) {
                        subtype = tokens[1];
                        resourceId = tokens[2];
                    }
                    else {
                        subtype = tokens[tokens.length-2];
                        resourceId = tokens[tokens.length-1];
                    }
                    ResourceType type = null;
                    switch (subtype.toLowerCase()) {
                        case "virtualmachines":
                            type = ResourceType.VIRTUAL_MACHINE;
                            break;
                        case "loadbalancers":
                            type = ResourceType.LOAD_BALANCER;
                            break;
                        case "inboundnatrules":
                            type = ResourceType.IP_FORWARDING_RULE;
                            break;
                        case "virtualnetworks":
                            type = ResourceType.VLAN;
                            break;
                        case "publicipaddresses":
                            type = ResourceType.IP_ADDRESS;
                            break;
                        case "networksecuritygroups":
                            type = ResourceType.FIREWALL;
                            break;
                    }
                    if (type != null) {

                        ConvergedInfrastructureResource resource = ConvergedInfrastructureResource.getInstance(type, resourceId);
                        list.add(resource);
                    }

                }
            }
        }
        ConvergedInfrastructure ci = ConvergedInfrastructure.getInstance(id, name, null, state, timestamp, datacenterID, regionId, resourceGroupName);
        ConvergedInfrastructureResource[] resources = new ConvergedInfrastructureResource[list.size()];
        resources = list.toArray(resources);
        ci.withResources(resources);
        ci.setTags(tags);
        return ci;
    }
}
