package org.dasein.cloud.azurearm.compute.vm;

import org.apache.http.client.methods.RequestBuilder;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;

import java.net.URI;
import java.net.URL;

/**
 * Created by vmunthiu on 1/21/2016.
 */
public class AzureArmVirtualMachineRequests {
    private static final String LIST_VM_PRODUCTS = "%s/subscriptions/%s/providers/Microsoft.Compute/locations/%s/vmSizes?api-version=2014-04-01";

    private AzureArm provider;

    public AzureArmVirtualMachineRequests(AzureArm provider){
        this.provider = provider;
    }

    public RequestBuilder listVirtualMachinesProducts() throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_VM_PRODUCTS, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber(), this.provider.getContext().getRegionId()));
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
