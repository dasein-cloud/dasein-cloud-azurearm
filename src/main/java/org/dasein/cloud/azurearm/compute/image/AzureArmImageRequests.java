package org.dasein.cloud.azurearm.compute.image;

import org.apache.http.client.methods.RequestBuilder;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;

import java.net.URI;
import java.net.URL;

/**
 * Created by vmunthiu on 1/20/2016.
 */
public class AzureArmImageRequests {
    private static final String LIST_PUBLISHERS = "%s/Subscriptions/%s/Providers/Microsoft.Compute/Locations/%s/Publishers?api-version=2015-06-15";
    private static final String LIST_OFFERS = "%s/%s/ArtifactTypes/VMImage/Offers?api-version=2015-06-15";
    private static final String LIST_SKUS = "%s/%s/Skus?api-version=2015-06-15";
    private static final String LIST_VERSIONS = "%s/%s/Versions?api-version=2015-06-15";
    private static final String GET_IMAGE = "%s/%s?api-version=2015-06-15";
    private AzureArm provider;

    public AzureArmImageRequests( AzureArm provider ) {
        this.provider = provider;
    }

    public RequestBuilder listPublishersByLocation(String locationId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_PUBLISHERS, this.provider.getContext().getCloud().getEndpoint(), this.provider.getContext().getAccountNumber(), locationId));
        return requestBuilder;
    }

    public RequestBuilder listOffers(String publisherId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_OFFERS, this.provider.getContext().getCloud().getEndpoint(), publisherId));
        return requestBuilder;
    }

    public RequestBuilder listSkus(String skuId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_SKUS, this.provider.getContext().getCloud().getEndpoint(), skuId));
        return requestBuilder;
    }

    public RequestBuilder listVersions(String versionId) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri(String.format(LIST_VERSIONS, this.provider.getContext().getCloud().getEndpoint(), versionId));
        return requestBuilder;
    }

    public RequestBuilder getImage(String imageId) throws CloudException, InternalException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(this.provider, requestBuilder);
        requestBuilder.setUri( getEncodedUri(String.format(GET_IMAGE, this.provider.getContext().getCloud().getEndpoint(), imageId)));
        return requestBuilder;
    }

    private static String getEncodedUri(String urlString) throws InternalException {
        try {
            URL url = new URL(urlString);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef()).toString();
        } catch (Exception e) {
            throw new InternalException(e.getMessage());
        }
    }
}
