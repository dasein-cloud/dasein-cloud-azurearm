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

import org.apache.http.client.methods.RequestBuilder;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.ci.model.ArmConvergedInfrastructureRequestModel;
import org.dasein.cloud.util.requester.entities.DaseinObjectToJsonEntity;

/**
 * User: daniellemayne
 * Date: 15/03/2016
 * Time: 08:40
 */
public class AzureArmConvergedInfrastructureRequests {
    private static final String LIST_TEMPLATE_DEPLOYMENTS   = "%s/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Resources/deployments?api-version=2016-02-01";
    private static final String CREATE_TEMPLATE_DEPLOYMENT  = "%s/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Resources/deployments/%s?api-version=2016-02-01";
    private static final String RESOURCE_WITH_ID = "%s/%s?api-version=2016-02-01";
    private static final String CANCEL_TEMPLATE_DEPLOYMENT = "%s/%s/cancel?api-version=2016-02-01";
    private static final String VALIDATE_TEMPLATE_DEPLOYMENT  = "%s/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Resources/deployments/%s/validate?api-version=2016-02-01";
    private static final String LIST_TEMPLATE_DEPLOYMENT_OPERATIONS = "%s/%s/operations?api-version=2016-02-01";

    private AzureArm provider;

    public AzureArmConvergedInfrastructureRequests(AzureArm provider) {
        this.provider = provider;
    }

    public RequestBuilder getTemplateDeployment(String tdId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(RESOURCE_WITH_ID, this.provider.getContext().getCloud().getEndpoint(), tdId));
        return requestBuilder;
    }

    public RequestBuilder listTemplateDeployments(String resourceGroup) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_TEMPLATE_DEPLOYMENTS, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber(), resourceGroup));
        return requestBuilder;
    }

    public RequestBuilder createTemplateDeployment(ArmConvergedInfrastructureRequestModel armConvergedInfrastructureModel, String resourceGroup, String deploymentName) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.put();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(CREATE_TEMPLATE_DEPLOYMENT, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber(), resourceGroup, deploymentName));
        requestBuilder.setEntity(new DaseinObjectToJsonEntity<ArmConvergedInfrastructureRequestModel>(armConvergedInfrastructureModel));
        return requestBuilder;
    }

    public RequestBuilder deleteTemplateDeployment(String tdId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.delete();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(RESOURCE_WITH_ID, this.provider.getContext().getCloud().getEndpoint(), tdId));
        return requestBuilder;
    }

    public RequestBuilder cancelTemplateDeployment(String tdId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.post();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(CANCEL_TEMPLATE_DEPLOYMENT, this.provider.getContext().getCloud().getEndpoint(), tdId));
        return requestBuilder;
    }

    public RequestBuilder validateTemplateDeployment(ArmConvergedInfrastructureRequestModel armConvergedInfrastructureModel, String resourceGroup, String deploymentName) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.post();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(VALIDATE_TEMPLATE_DEPLOYMENT, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber(), resourceGroup, deploymentName));
        requestBuilder.setEntity(new DaseinObjectToJsonEntity<ArmConvergedInfrastructureRequestModel>(armConvergedInfrastructureModel));
        return requestBuilder;
    }

    public RequestBuilder listTemplateDeploymentOperations(String tdId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_TEMPLATE_DEPLOYMENT_OPERATIONS, this.provider.getContext().getCloud().getEndpoint(), tdId));
        return requestBuilder;
    }
}
