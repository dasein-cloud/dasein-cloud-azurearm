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

package org.dasein.cloud.azurearm.tests;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.apache.commons.collections.IteratorUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dasein.cloud.Cloud;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.ResourceNotFoundException;
import org.dasein.cloud.ResourceType;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmLocation;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.ci.AzureArmConvergedInfrastructureSupport;
import org.dasein.cloud.azurearm.ci.model.ArmConvergedInfrastructureResponseModel;
import org.dasein.cloud.azurearm.ci.model.ArmConvergedInfrastructuresResponseModel;
import org.dasein.cloud.azurearm.ci.model.ArmTemplateDeploymentOperationResponseModel;
import org.dasein.cloud.azurearm.ci.model.ArmTemplateDeploymentOperationsResponseModel;
import org.dasein.cloud.ci.ConvergedInfrastructure;
import org.dasein.cloud.ci.ConvergedInfrastructureFilterOptions;
import org.dasein.cloud.ci.ConvergedInfrastructureProvisionOptions;
import org.dasein.cloud.ci.ConvergedInfrastructureResource;
import org.dasein.cloud.ci.ConvergedInfrastructureState;
import org.dasein.cloud.dc.ResourcePool;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: daniellemayne
 * Date: 16/03/2016
 * Time: 08:45
 */
@RunWith(JUnit4.class)
public class AzureArmConvergedInfrastructureSupportTests {
    @Mocked
    AzureArm armProviderMock;
    @Mocked
    ProviderContext providerContextMock;
    @Mocked
    Cloud cloudMock;
    @Mocked
    HttpClientBuilder httpClientBuilderMock;
    @Mocked
    AzureArmLocation armLocationMock;

    final String TEST_ACCOUNT_NO = "12323232323";
    final String TEST_REGION = "East US";
    final String TEST_ENDPOINT = "http://testendpoint/";
    final String TEST_RESOURCE_GROUP = "rg1";
    final String TEST_DEPLOYMENT_NAME = "testDeploymentName";
    final String TEST_TEMPLATE_CONTENT = "{template content goes here}";
    final String TEST_PARAMETERS_CONTENT = "{parameter content goes here}";

    @Before
    public void setUp() throws CloudException, InternalException {
        new NonStrictExpectations() {
            {   armProviderMock.getContext(); result = providerContextMock; }
            {   armProviderMock.getAzureArmClientBuilder(); result = httpClientBuilderMock; }
            {   armProviderMock.getAzureClientBuilderWithPooling(); result = httpClientBuilderMock; }
            {   providerContextMock.getCloud(); result = cloudMock; }
            {   providerContextMock.getAccountNumber(); result = TEST_ACCOUNT_NO; }
            {   providerContextMock.getRegionId(); result = TEST_REGION; }
            {   cloudMock.getEndpoint(); result = TEST_ENDPOINT; }
            {   armProviderMock.getDataCenterServices(); result = armLocationMock; }
            {   armLocationMock.listResourcePools(anyString); result = getTestResourcePoolList(); }
        };

        new MockUp<AzureArmRequester>(){
            @Mock
            String getAuthenticationToken(AzureArm provider){
                return "myauthenticationtoken";
            }
        };
    }

    private List<ResourcePool> getTestResourcePoolList() {
        ResourcePool rp = new ResourcePool();
        rp.setProvideResourcePoolId(TEST_RESOURCE_GROUP + "-id");
        rp.setName(TEST_RESOURCE_GROUP);
        rp.setDataCenterId(TEST_REGION + "-dc");

        ResourcePool rp2 = new ResourcePool();
        rp2.setProvideResourcePoolId(TEST_RESOURCE_GROUP + "2-id");
        rp2.setName(TEST_RESOURCE_GROUP+"2");
        rp2.setDataCenterId(TEST_REGION + "-dc");

        final List<ResourcePool> rpList = new ArrayList<ResourcePool>();
        rpList.add(rp);
        rpList.add(rp2);
        return rpList;
    }

    private ArmConvergedInfrastructuresResponseModel getTestArmConvergedInfrastructuresModel() {
        ArmConvergedInfrastructureResponseModel acim1 = new ArmConvergedInfrastructureResponseModel();
        acim1.setId("acim1");
        acim1.setName("deployment1");

        ArmConvergedInfrastructureResponseModel acim2 = new ArmConvergedInfrastructureResponseModel();
        acim2.setId("acim2");
        acim2.setName("deployment2");

        List<ArmConvergedInfrastructureResponseModel> armConvergedInfrastructureModelList = new ArrayList<ArmConvergedInfrastructureResponseModel>();
        armConvergedInfrastructureModelList.add(acim1);
        armConvergedInfrastructureModelList.add(acim2);

        final ArmConvergedInfrastructuresResponseModel armConvergedInfrastructuresResponseModel = new ArmConvergedInfrastructuresResponseModel();
        armConvergedInfrastructuresResponseModel.setArmConvergedInfrastructureModels(armConvergedInfrastructureModelList);

        return armConvergedInfrastructuresResponseModel;
    }

    @Test
    public void listTemplateDeployments_shouldReturnAllAvailableTDs() throws CloudException, InternalException{
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructuresResponseModel>(getTestArmConvergedInfrastructuresModel());
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 1;
            }
        };

        Iterable<ConvergedInfrastructure> actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            ConvergedInfrastructureFilterOptions options = ConvergedInfrastructureFilterOptions.getInstance().withResourceGroupId(TEST_RESOURCE_GROUP+"-id");
            actualResult = convergedInfrastructureSupport.listConvergedInfrastructures(options);
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments?api-version=2016-02-01"));
        assertNotNull(actualResult);
        List<ConvergedInfrastructure> actualResultAsList = IteratorUtils.toList(actualResult.iterator());
        assertTrue(actualResultAsList.size() == 2);
    }

    @Test
    public void listTemplateDeployments_shouldReturnEmptyListIfNoResourceGroupsFound() throws CloudException, InternalException {
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<>("ok");
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 0;
            }
            {   armLocationMock.listResourcePools(anyString); result = Collections.EMPTY_LIST; }
        };

        Iterable<ConvergedInfrastructure> actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.listConvergedInfrastructures(null);
        } catch (Exception e) {}

        assertFalse(closeableHttpClient.isExecuteCalled());
        assertNotNull(actualResult);
        List<ConvergedInfrastructure> actualResultAsList = IteratorUtils.toList(actualResult.iterator());
        assertTrue(actualResultAsList.size() == 0);
    }

    @Test
    public void listTemplateDeployments_shouldReturnEmptyListIfInvalidResourceGroup() throws CloudException, InternalException {
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<>("ok");
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 0;
            }
        };

        Iterable<ConvergedInfrastructure> actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            ConvergedInfrastructureFilterOptions options = ConvergedInfrastructureFilterOptions.getInstance().withResourceGroupId("random_id");
            actualResult = convergedInfrastructureSupport.listConvergedInfrastructures(options);
        } catch (Exception e) {}

        assertFalse(closeableHttpClient.isExecuteCalled());
        assertNotNull(actualResult);
        List<ConvergedInfrastructure> actualResultAsList = IteratorUtils.toList(actualResult.iterator());
        assertTrue(actualResultAsList.size() == 0);
    }

    @Test
    public void listTemplateDeployments_shouldReturnEmptyListIfNoFilterMatch() throws CloudException, InternalException {
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructuresResponseModel>(getTestArmConvergedInfrastructuresModel());
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 1;
            }
        };

        Iterable<ConvergedInfrastructure> actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            ConvergedInfrastructureFilterOptions options = ConvergedInfrastructureFilterOptions.getInstance("filter").withResourceGroupId(TEST_RESOURCE_GROUP+"-id");
            actualResult = convergedInfrastructureSupport.listConvergedInfrastructures(options);
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments?api-version=2016-02-01"));
        assertNotNull(actualResult);
        List<ConvergedInfrastructure> actualResultAsList = IteratorUtils.toList(actualResult.iterator());
        assertTrue(actualResultAsList.size() == 0);
    }

    @Test
    public void getTemplateDeployment_shouldReturnCorrectTD() throws InternalException, CloudException{
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructuresResponseModel>(getTestArmConvergedInfrastructuresModel());
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 2;  // 2 available resource groups to search
            }
        };

        ConvergedInfrastructure actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.getConvergedInfrastructure("acim1");
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments?api-version=2016-02-01"));
        assertNotNull(actualResult);
        assertTrue(actualResult.getName().equals("deployment1"));
    }

    @Test
    public void convergedInfrastructureFrom_shouldReturnObjectWithCorrectAttributes() throws CloudException, InternalException{
        ArmConvergedInfrastructureResponseModel acim1 = new ArmConvergedInfrastructureResponseModel();
        acim1.setId("acim1");
        acim1.setName("deployment1");
        acim1.setProviderResourceGroupId(TEST_RESOURCE_GROUP+"-id");
        acim1.setProviderRegionId(TEST_REGION);
        acim1.setProviderDatacenterId(TEST_REGION+"-dc");

        ArmConvergedInfrastructureResponseModel.Properties properties = new ArmConvergedInfrastructureResponseModel.Properties();
        properties.setCiState("Accepted");
        properties.setProvisioningTimestamp("2015-01-01T18:26:20.6229141Z");

        List<ArmConvergedInfrastructureResponseModel.Properties.Dependency> dependencies = new ArrayList<ArmConvergedInfrastructureResponseModel.Properties.Dependency>();
        ArmConvergedInfrastructureResponseModel.Properties.Dependency resource1 = new ArmConvergedInfrastructureResponseModel.Properties.Dependency();
        resource1.setOutputResourceId("microsoft.compute/virtualmachines/vm_001");
        dependencies.add(resource1);

        properties.setDependencies(dependencies);
        acim1.setProperties(properties);

        List<ArmConvergedInfrastructureResponseModel> armConvergedInfrastructureModelList = new ArrayList<ArmConvergedInfrastructureResponseModel>();
        armConvergedInfrastructureModelList.add(acim1);

        final ArmConvergedInfrastructuresResponseModel armConvergedInfrastructuresResponseModel = new ArmConvergedInfrastructuresResponseModel();
        armConvergedInfrastructuresResponseModel.setArmConvergedInfrastructureModels(armConvergedInfrastructureModelList);

        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructuresResponseModel>(armConvergedInfrastructuresResponseModel);
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 2;
            }
        };

        ConvergedInfrastructure actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.getConvergedInfrastructure("acim1");
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments?api-version=2016-02-01"));
        assertNotNull(actualResult);
        assertTrue(actualResult.getName().equals("deployment1"));
        assertTrue(actualResult.getProviderResourcePoolId().equals(TEST_RESOURCE_GROUP + "-id"));
        assertTrue(actualResult.getProviderRegionId().equals(TEST_REGION));
        assertTrue(actualResult.getProviderDatacenterId().equals(TEST_REGION + "-dc"));
        assertTrue(actualResult.getCiState().equals(ConvergedInfrastructureState.ACCEPTED));

        String testTimestamp = "2015-01-01T18:26:20.6229141Z";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            calendar.setTime(sdf.parse(testTimestamp));
        }
        catch ( ParseException ignore ) {}
        assertTrue(actualResult.getProvisioningTimestamp() == calendar.getTimeInMillis());

        assertNotNull(actualResult.getResources());
        List<ConvergedInfrastructureResource> resources = actualResult.getResources();
        assertTrue(resources.size() == 1);
        ConvergedInfrastructureResource resource = resources.iterator().next();
        assertTrue(resource.getResourceId().equals("vm_001"));
        assertTrue(resource.getResourceType().equals(ResourceType.VIRTUAL_MACHINE));
    }

    @Test
    public void convergedInfrastructureFrom_shouldHandleTemplateDeploymentsInErrorState() throws CloudException, InternalException{
        ArmConvergedInfrastructureResponseModel acim1 = new ArmConvergedInfrastructureResponseModel();
        acim1.setId("acim1");
        acim1.setName("deployment1");
        acim1.setProviderResourceGroupId(TEST_RESOURCE_GROUP+"-id");
        acim1.setProviderRegionId(TEST_REGION);
        acim1.setProviderDatacenterId(TEST_REGION+"-dc");

        ArmConvergedInfrastructureResponseModel.Properties properties = new ArmConvergedInfrastructureResponseModel.Properties();
        properties.setCiState("Failed");
        properties.setProvisioningTimestamp("2015-01-01T18:26:20.6229141Z");

        List<ArmConvergedInfrastructureResponseModel.Properties.Dependency> dependencies = new ArrayList<ArmConvergedInfrastructureResponseModel.Properties.Dependency>();
        ArmConvergedInfrastructureResponseModel.Properties.Dependency resource1 = new ArmConvergedInfrastructureResponseModel.Properties.Dependency();
        resource1.setOutputResourceId("microsoft.compute/virtualmachines/vm_001");
        dependencies.add(resource1);

        properties.setDependencies(dependencies);

        ArmConvergedInfrastructureResponseModel.Properties.DeploymentError deploymentError = new ArmConvergedInfrastructureResponseModel.Properties.DeploymentError();
        ArmConvergedInfrastructureResponseModel.Properties.DeploymentError.ErrorDetails errorDetails = new ArmConvergedInfrastructureResponseModel.Properties.DeploymentError.ErrorDetails();
        errorDetails.setCode("Conflict Error");
        errorDetails.setMessage("Unable to create due to conflict");
        List<ArmConvergedInfrastructureResponseModel.Properties.DeploymentError.ErrorDetails> errorList = new ArrayList<ArmConvergedInfrastructureResponseModel.Properties.DeploymentError.ErrorDetails>();
        errorList.add(errorDetails);
        deploymentError.setErrorDetails(errorList);
        properties.setDeploymentError(deploymentError);

        acim1.setProperties(properties);

        List<ArmConvergedInfrastructureResponseModel> armConvergedInfrastructureModelList = new ArrayList<ArmConvergedInfrastructureResponseModel>();
        armConvergedInfrastructureModelList.add(acim1);

        final ArmConvergedInfrastructuresResponseModel armConvergedInfrastructuresResponseModel = new ArmConvergedInfrastructuresResponseModel();
        armConvergedInfrastructuresResponseModel.setArmConvergedInfrastructureModels(armConvergedInfrastructureModelList);

        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructuresResponseModel>(armConvergedInfrastructuresResponseModel);
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                times = 2;
            }
        };

        ConvergedInfrastructure actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.getConvergedInfrastructure("acim1");
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments?api-version=2016-02-01"));
        assertNotNull(actualResult);
        assertTrue(actualResult.getName().equals("deployment1"));
        assertTrue(actualResult.getProviderResourcePoolId().equals(TEST_RESOURCE_GROUP + "-id"));
        assertTrue(actualResult.getProviderRegionId().equals(TEST_REGION));
        assertTrue(actualResult.getProviderDatacenterId().equals(TEST_REGION + "-dc"));
        assertTrue(actualResult.getCiState().equals(ConvergedInfrastructureState.FAILED));

        String testTimestamp = "2015-01-01T18:26:20.6229141Z";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            calendar.setTime(sdf.parse(testTimestamp));
        }
        catch ( ParseException ignore ) {}
        assertTrue(actualResult.getProvisioningTimestamp() == calendar.getTimeInMillis());

        assertNotNull(actualResult.getResources());
        List<ConvergedInfrastructureResource> resources = actualResult.getResources();
        assertTrue(resources.size() == 1);
        ConvergedInfrastructureResource resource = resources.iterator().next();
        assertTrue(resource.getResourceId().equals("vm_001"));
        assertTrue(resource.getResourceType().equals(ResourceType.VIRTUAL_MACHINE));
        assertNotNull(actualResult.getTags());
        assertNotNull(actualResult.getTag("error1"));
        assertTrue(actualResult.getTag("error1").equals("Unable to create due to conflict"));
    }

    @Test
    public void convergedInfrastructureFrom_shouldHandleTemplateDeploymentOperationsInErrorState() throws CloudException, InternalException{
        ArmConvergedInfrastructureResponseModel acim1 = new ArmConvergedInfrastructureResponseModel();
        acim1.setId("acim1");
        acim1.setName("deployment1");
        acim1.setProviderResourceGroupId(TEST_RESOURCE_GROUP+"-id");
        acim1.setProviderRegionId(TEST_REGION);
        acim1.setProviderDatacenterId(TEST_REGION+"-dc");

        ArmConvergedInfrastructureResponseModel.Properties properties = new ArmConvergedInfrastructureResponseModel.Properties();
        properties.setCiState("Failed");
        properties.setProvisioningTimestamp("2015-01-02T18:26:20.6229141Z");

        List<ArmConvergedInfrastructureResponseModel.Properties.Dependency> dependencies = new ArrayList<ArmConvergedInfrastructureResponseModel.Properties.Dependency>();
        ArmConvergedInfrastructureResponseModel.Properties.Dependency resource1 = new ArmConvergedInfrastructureResponseModel.Properties.Dependency();
        resource1.setOutputResourceId("microsoft.compute/virtualmachines/vm_001");
        dependencies.add(resource1);

        properties.setDependencies(dependencies);

        ArmTemplateDeploymentOperationResponseModel.Properties operationProperties = new ArmTemplateDeploymentOperationResponseModel.Properties();
        ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage statusMessage = new ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage();
        ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage.StatusError statusError = new ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage.StatusError();
        ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage.StatusError.ErrorDetails errorDetails = new ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage.StatusError.ErrorDetails();
        errorDetails.setCode("Conflict Error");
        errorDetails.setMessage("Unable to create due to conflict");
        List<ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage.StatusError.ErrorDetails> errorList = new ArrayList<ArmTemplateDeploymentOperationResponseModel.Properties.StatusMessage.StatusError.ErrorDetails>();
        errorList.add(errorDetails);
        statusError.setErrorDetails(errorList);
        statusMessage.setStatusError(statusError);
        operationProperties.setStatusMessage(statusMessage);
        operationProperties.setStatusCode("Conflict");
        operationProperties.setProvisioningState("Failed");

        ArmTemplateDeploymentOperationResponseModel atdom1 = new ArmTemplateDeploymentOperationResponseModel();
        atdom1.setProperties(operationProperties);

        List<ArmTemplateDeploymentOperationResponseModel> armTemplateDeploymentOperationModelList = new ArrayList<ArmTemplateDeploymentOperationResponseModel>();
        armTemplateDeploymentOperationModelList.add(atdom1);

        final ArmTemplateDeploymentOperationsResponseModel armTemplateDeploymentOperationsResponseModel = new ArmTemplateDeploymentOperationsResponseModel();
        armTemplateDeploymentOperationsResponseModel.setArmTemplateDeploymentOperationModels(armTemplateDeploymentOperationModelList);

        final TestCloseableHttpClient closeableHttpClient2 = new TestCloseableHttpClient<ArmTemplateDeploymentOperationsResponseModel>(armTemplateDeploymentOperationsResponseModel);


        acim1.setProperties(properties);

        List<ArmConvergedInfrastructureResponseModel> armConvergedInfrastructureModelList = new ArrayList<ArmConvergedInfrastructureResponseModel>();
        armConvergedInfrastructureModelList.add(acim1);

        final ArmConvergedInfrastructuresResponseModel armConvergedInfrastructuresResponseModel = new ArmConvergedInfrastructuresResponseModel();
        armConvergedInfrastructuresResponseModel.setArmConvergedInfrastructureModels(armConvergedInfrastructureModelList);

        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructuresResponseModel>(armConvergedInfrastructuresResponseModel);
        new NonStrictExpectations(){
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
                result = closeableHttpClient2;
                result = closeableHttpClient;
                result = closeableHttpClient2;
            }
        };

        ConvergedInfrastructure actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.getConvergedInfrastructure("acim1");
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments?api-version=2016-02-01"));
        assertNotNull(actualResult);
        assertTrue(actualResult.getName().equals("deployment1"));
        assertTrue(actualResult.getProviderResourcePoolId().equals(TEST_RESOURCE_GROUP + "-id"));
        assertTrue(actualResult.getProviderRegionId().equals(TEST_REGION));
        assertTrue(actualResult.getProviderDatacenterId().equals(TEST_REGION + "-dc"));
        assertTrue(actualResult.getCiState().equals(ConvergedInfrastructureState.FAILED));

        String testTimestamp = "2015-01-02T18:26:20.6229141Z";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            calendar.setTime(sdf.parse(testTimestamp));
        }
        catch ( ParseException ignore ) {}
        assertTrue(actualResult.getProvisioningTimestamp() == calendar.getTimeInMillis());

        assertNotNull(actualResult.getResources());
        List<ConvergedInfrastructureResource> resources = actualResult.getResources();
        assertTrue(resources.size() == 1);
        ConvergedInfrastructureResource resource = resources.iterator().next();
        assertTrue(resource.getResourceId().equals("vm_001"));
        assertTrue(resource.getResourceType().equals(ResourceType.VIRTUAL_MACHINE));
        assertNotNull(actualResult.getTags());
        assertNotNull(actualResult.getTag("opError1"));
        assertTrue(actualResult.getTag("opError1").equals("Conflict: Unable to create due to conflict"));
    }

    @Test
    public void terminateTemplateDeployment_shouldDoADeleteToTerminateTDWithTDId(){
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<String>("ok");
        new NonStrictExpectations(){{ httpClientBuilderMock.build(); result = closeableHttpClient; } };

        String testTDId = "testtdid";
        String expectedDeleteUri = String.format("%s?api-version=2016-02-01", testTDId);
        try{
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            convergedInfrastructureSupport.terminate(testTDId, "");
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("delete"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().endsWith(expectedDeleteUri));

    }

    @Test
    public void cancelTemplateDeployment_shouldDoAPostToCancelTDWithTDId(){
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<String>("ok");
        new NonStrictExpectations(){{ httpClientBuilderMock.build(); result = closeableHttpClient; } };

        String testTDId = "testtdid";
        String expectedCancelUri = String.format("%s/cancel?api-version=2016-02-01", testTDId);
        try{
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            convergedInfrastructureSupport.cancelDeployment(testTDId, "");
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("post"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().endsWith(expectedCancelUri));
    }

    @Test
    public void createTemplateDeployment_shouldDoAPutToCreateTDWithJsonEntity() throws CloudException, InternalException{
        ConvergedInfrastructureProvisionOptions options = ConvergedInfrastructureProvisionOptions.getInstance(TEST_DEPLOYMENT_NAME,
                TEST_RESOURCE_GROUP+"-id", "Incremental", TEST_TEMPLATE_CONTENT, TEST_PARAMETERS_CONTENT, true);

        ArmConvergedInfrastructureResponseModel acim1 = new ArmConvergedInfrastructureResponseModel();
        acim1.setId("acim1");
        acim1.setName(TEST_DEPLOYMENT_NAME);
        acim1.setProviderResourceGroupId(TEST_RESOURCE_GROUP+"-id");
        acim1.setProviderRegionId(TEST_REGION);
        acim1.setProviderDatacenterId(TEST_REGION+"-dc");

        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructureResponseModel>(acim1);
        new NonStrictExpectations(){
            { armLocationMock.getResourcePool(anyString);
                result = getTestResourcePoolList().get(0);
            }
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
            }
        };

        ConvergedInfrastructure actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.provision(options);
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("put"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments/" + options.getName() + "?api-version=2016-02-01"));
        assertNotNull(actualResult);
        assertTrue(actualResult.getName().equals(options.getName()));
        assertTrue(actualResult.getProviderResourcePoolId().equals(TEST_RESOURCE_GROUP + "-id"));
    }

    @Test(expected = InternalException.class)
    public void createTemplateDeployment_shouldThrowExceptionIfOptionsIsNull() throws CloudException, InternalException{
        AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
        convergedInfrastructureSupport.provision(null);
    }

    @Test(expected = InternalException.class)
    public void createTemplateDeployment_shouldThrowExceptionIfResourceGroupIdIsNull() throws CloudException, InternalException{
        ConvergedInfrastructureProvisionOptions options = ConvergedInfrastructureProvisionOptions.getInstance(TEST_DEPLOYMENT_NAME,
                null, "Incremental", TEST_TEMPLATE_CONTENT, TEST_PARAMETERS_CONTENT, true);

        AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
        convergedInfrastructureSupport.provision(options);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void createTemplateDeployment_shouldThrowExceptionIfResourceGroupNotValid() throws CloudException, InternalException{
        ConvergedInfrastructureProvisionOptions options = ConvergedInfrastructureProvisionOptions.getInstance(TEST_DEPLOYMENT_NAME,
                "myFakeId", "Incremental", TEST_TEMPLATE_CONTENT, TEST_PARAMETERS_CONTENT, true);

        new NonStrictExpectations(){
            { armLocationMock.getResourcePool(anyString);
                result = null;
            }
            { httpClientBuilderMock.build();
                times = 0;
            }
        };

        AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
        convergedInfrastructureSupport.provision(options);
    }

    @Test
    public void validateTemplateDeployment_shouldDoAPostToValidateTDWithJsonEntity() throws CloudException, InternalException{
        ConvergedInfrastructureProvisionOptions options = ConvergedInfrastructureProvisionOptions.getInstance(TEST_DEPLOYMENT_NAME,
                TEST_RESOURCE_GROUP+"-id", "Incremental", TEST_TEMPLATE_CONTENT, TEST_PARAMETERS_CONTENT, true);

        ArmConvergedInfrastructureResponseModel acim1 = new ArmConvergedInfrastructureResponseModel();
        acim1.setId("acim1");
        acim1.setName(TEST_DEPLOYMENT_NAME);
        acim1.setProviderResourceGroupId(TEST_RESOURCE_GROUP+"-id");
        acim1.setProviderRegionId(TEST_REGION);
        acim1.setProviderDatacenterId(TEST_REGION+"-dc");

        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmConvergedInfrastructureResponseModel>(acim1);
        new NonStrictExpectations(){
            { armLocationMock.getResourcePool(anyString);
                result = getTestResourcePoolList().get(0);
            }
            { httpClientBuilderMock.build();
                result = closeableHttpClient;
            }
        };

        ConvergedInfrastructure actualResult = null;
        try {
            AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
            actualResult = convergedInfrastructureSupport.validateDeployment(options);
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("post"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_RESOURCE_GROUP));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Resources/deployments/" + options.getName() + "/validate?api-version=2016-02-01"));
        assertNotNull(actualResult);
        assertTrue(actualResult.getName().equals(options.getName()));
        assertTrue(actualResult.getProviderResourcePoolId().equals(TEST_RESOURCE_GROUP + "-id"));
    }

    @Test(expected = InternalException.class)
    public void validateTemplateDeployment_shouldThrowExceptionIfOptionsIsNull() throws CloudException, InternalException{
        AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
        convergedInfrastructureSupport.validateDeployment(null);
    }

    @Test(expected = InternalException.class)
    public void validateTemplateDeployment_shouldThrowExceptionIfResourceGroupIdIsNull() throws CloudException, InternalException{
        ConvergedInfrastructureProvisionOptions options = ConvergedInfrastructureProvisionOptions.getInstance(TEST_DEPLOYMENT_NAME,
                null, "Incremental", TEST_TEMPLATE_CONTENT, TEST_PARAMETERS_CONTENT, true);

        AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
        convergedInfrastructureSupport.validateDeployment(options);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void validateTemplateDeployment_shouldThrowExceptionIfResourceGroupNotValid() throws CloudException, InternalException{
        ConvergedInfrastructureProvisionOptions options = ConvergedInfrastructureProvisionOptions.getInstance(TEST_DEPLOYMENT_NAME,
                "myFakeId", "Incremental", TEST_TEMPLATE_CONTENT, TEST_PARAMETERS_CONTENT, true);

        new NonStrictExpectations(){
            { armLocationMock.getResourcePool(anyString);
                result = null;
            }
            { httpClientBuilderMock.build();
                times = 0;
            }
        };

        AzureArmConvergedInfrastructureSupport convergedInfrastructureSupport = new AzureArmConvergedInfrastructureSupport(armProviderMock);
        convergedInfrastructureSupport.validateDeployment(options);
    }
}
