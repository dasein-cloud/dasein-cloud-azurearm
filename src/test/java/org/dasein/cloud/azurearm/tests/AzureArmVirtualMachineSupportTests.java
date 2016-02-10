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
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.compute.vm.AzureArmVirtualMachineSupport;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachineModel;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachinesModel;
import org.dasein.cloud.compute.VirtualMachine;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by vmunthiu on 8/13/2015.
 */
public class AzureArmVirtualMachineSupportTests {
    @Mocked AzureArm armProviderMock;
    @Mocked ProviderContext providerContextMock;
    @Mocked Cloud cloudMock;
    @Mocked HttpClientBuilder httpClientBuilderMock;

    final String TEST_ACCOUNT_NO = "12323232323";
    final String TEST_REGION = "East US";
    final String TEST_ENDPOINT = "http://testendpont/";

    @Before
    public void setUp() throws CloudException {
        new NonStrictExpectations() {
            {   armProviderMock.getContext(); result = providerContextMock; }
            {   armProviderMock.getAzureArmClientBuilder(); result = httpClientBuilderMock; }
            {   providerContextMock.getCloud(); result = cloudMock; }
            {   providerContextMock.getAccountNumber(); result = TEST_ACCOUNT_NO; }
            {   providerContextMock.getRegionId(); result = TEST_REGION; }
            {   cloudMock.getEndpoint(); result = TEST_ENDPOINT; }
        };

        new MockUp<AzureArmRequester>(){
            @Mock
            String getAuthenticationToken(AzureArm provider){
                return "myauthenticationtocken";
            }
        };
    }

    @Test
    public void listVirtualMachines_shouldreturnallavailablevms() {
        ArmVirtualMachineModel armVirtualMachineModel1 = new ArmVirtualMachineModel();
        armVirtualMachineModel1.setName("vlad1");
        ArmVirtualMachineModel armVirtualMachineModel2 = new ArmVirtualMachineModel();
        armVirtualMachineModel2.setName("vlad2");
        ArrayList<ArmVirtualMachineModel> armVirtualMachineModelList = new ArrayList<ArmVirtualMachineModel>();
        armVirtualMachineModelList.add(armVirtualMachineModel1);
        armVirtualMachineModelList.add(armVirtualMachineModel2);

        final ArmVirtualMachinesModel armVirtualMachinesModel = new ArmVirtualMachinesModel();
        armVirtualMachinesModel.setArmVirtualMachineModels(armVirtualMachineModelList);

        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<ArmVirtualMachinesModel>(armVirtualMachinesModel);
        new NonStrictExpectations(){{ httpClientBuilderMock.build(); result = closeableHttpClient; } };

        Iterable<VirtualMachine> actualResult = null;
        try {
            AzureArmVirtualMachineSupport vmSupport = new AzureArmVirtualMachineSupport(armProviderMock);
            actualResult = vmSupport.listVirtualMachines();
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("get"));
        assertTrue(actualHttpRequest.getURI().toString().startsWith(TEST_ENDPOINT));
        assertTrue(actualHttpRequest.getURI().toString().contains(TEST_ACCOUNT_NO));
        assertTrue(actualHttpRequest.getURI().toString().endsWith("providers/Microsoft.Compute/virtualMachines?api-version=2015-06-15"));
        assertNotNull(actualResult);
        List<VirtualMachine> actualResultAsList = IteratorUtils.toList(actualResult.iterator());
        assertTrue(actualResultAsList.size() == 2);
    }

    @Test
    public void stopVirtualMachines_shouldDoAPostToStopVMWithVMId(){
        final TestCloseableHttpClient closeableHttpClient = new TestCloseableHttpClient<String>("ok");
        new NonStrictExpectations(){{ httpClientBuilderMock.build(); result = closeableHttpClient; } };

        String testVMId = "testvmid";
        String expectedDeleteUri = String.format("%s/stop?api-version=2015-06-15", testVMId);
        try{
            AzureArmVirtualMachineSupport vmSupport = new AzureArmVirtualMachineSupport(armProviderMock);
            vmSupport.stop(testVMId, true);
        } catch (Exception e) {}

        assertTrue(closeableHttpClient.isExecuteCalled());
        HttpUriRequest actualHttpRequest = closeableHttpClient.getActualHttpUriRequest();
        assertTrue(actualHttpRequest.getMethod().equalsIgnoreCase("post"));
        assertTrue(actualHttpRequest.getURI().toString().equalsIgnoreCase(expectedDeleteUri));
    }
}
