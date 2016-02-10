package org.dasein.cloud.azurearm.network;

import org.apache.http.client.methods.RequestBuilder;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.network.model.ArmNetworkModel;
import org.dasein.cloud.azurearm.network.model.ArmSubnetModel;
import org.dasein.cloud.util.requester.entities.DaseinObjectToJsonEntity;

import java.net.URI;
import java.net.URL;

/**
 * Created by vmunthiu on 1/22/2016.
 */
public class AzureArmNetworkRequests {
    private static final String LIST_NETWORKS = "%s/subscriptions/%s/providers/Microsoft.Network/virtualnetworks?api-version=2015-06-15";
    private static final String VM_NETTWORK = "%s/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/virtualnetworks/%s?api-version=2015-06-15";
    private static final String VM_SUBNET = "%s/%s/subnets/%s?api-version=2015-06-15";
    private static final String LIST_SUBNETS = "%s/%s/subnets?api-version=2015-06-15";
    private static final String RESOURCE_WITH_ID = "%s/%s?api-version=2015-06-15";
    private AzureArm provider;

    public AzureArmNetworkRequests(AzureArm provider){
        this.provider = provider;
    }

    public RequestBuilder listNetworks() throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_NETWORKS, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber()));
        return requestBuilder;
    }

    public RequestBuilder createNetwork(ArmNetworkModel armNetworkModel, String resourceGroup) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.put();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(VM_NETTWORK, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber(), resourceGroup, armNetworkModel.getName()));
        requestBuilder.setEntity(new DaseinObjectToJsonEntity<ArmNetworkModel>(armNetworkModel));
        return requestBuilder;
    }

    public RequestBuilder deleteNetwork(String vlanId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.delete();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(RESOURCE_WITH_ID, this.provider.getContext().getCloud().getEndpoint(), vlanId));
        return requestBuilder;
    }

    public RequestBuilder createSubnet(ArmSubnetModel armSubnetModel, String vlanId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.put();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(VM_SUBNET, this.provider.getContext().getCloud().getEndpoint(), vlanId, armSubnetModel.getName()));
        requestBuilder.setEntity(new DaseinObjectToJsonEntity<ArmSubnetModel>(armSubnetModel));
        return requestBuilder;
    }

    public RequestBuilder listSubnets(String vlanId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_SUBNETS, this.provider.getContext().getCloud().getEndpoint(), vlanId));
        return requestBuilder;
    }

    public RequestBuilder deleteSubnet(String subnetId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.delete();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(RESOURCE_WITH_ID, this.provider.getContext().getCloud().getEndpoint(), subnetId));
        return requestBuilder;
    }

    private String getEncodedUri(String urlString) throws InternalException {
        try {
            URL url = new URL(urlString);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()).toString();
        } catch (Exception e) {
            throw new InternalException(e.getMessage());
        }
    }
}
