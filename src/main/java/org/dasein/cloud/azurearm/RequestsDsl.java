package org.dasein.cloud.azurearm;

import org.apache.http.client.methods.RequestBuilder;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachineModel;
import org.dasein.cloud.util.requester.entities.DaseinEntity;
import org.dasein.cloud.util.requester.entities.DaseinObjectToJsonEntity;

import java.net.URI;
import java.net.URL;

/**
 * Created by vmunthiu on 12/7/2015.
 */

public class RequestsDsl {
    private static final String LIST_VM_RESOURCES = "%s/subscriptions/%s/providers/Microsoft.Compute/virtualMachines?api-version=2015-06-15";
    private static final String CREATE_VM = "%s/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s?api-version=2015-06-15";
    private static final String STOP_VM = "%s/stop?api-version=2015-06-15";
    private static final String LIST_LOCATIONS = "%s/subscriptions/%s/providers/Microsoft.Compute?api-version=2015-01-01";
    private static final String AUTH_TOKEN_ENPOINT = "https://login.windows.net/%s/oauth2/token";
    private static final String RESOURCE_ENDPOINT = "https://management.core.windows.net/";

    public static abstract class RequestUrlBuilder {
        protected RequestBuilder requestBuilder;
        protected AzureArm provider;

        private RequestUrlBuilder(RequestBuilder requestBuilder, AzureArm provider) {
            this.requestBuilder = requestBuilder;
            this.provider = provider;
        }

        protected RequestBuilder setRequestUri(String uri) {
            return requestBuilder.setUri(String.format(uri, provider.getContext().getCloud().getEndpoint(), provider.getContext().getAccountNumber()));
        }

        protected RequestBuilder setRequestEntity(DaseinEntity entity) {
            return requestBuilder.setEntity(entity);
        }

        protected AzureArmRequester createRequester() throws CloudException {
            return new AzureArmRequester(provider, requestBuilder.build());
        }
    }

    public static class GetRequestUrlBuilder extends RequestUrlBuilder {
        private GetRequestUrlBuilder(RequestBuilder requestBuilder, AzureArm provider) {
            super(requestBuilder, provider);
        }

        public AzureArmRequester forLocations() throws CloudException {
            setRequestUri(LIST_LOCATIONS);
            return createRequester();
        }

        public AzureArmRequester forVirtualMachines() throws CloudException {
            setRequestUri(LIST_VM_RESOURCES);
            return createRequester();
        }
    }

    public static class PostRequestUrlBuilder extends RequestUrlBuilder {
        private PostRequestUrlBuilder(RequestBuilder requestBuilder, AzureArm provider) {
            super(requestBuilder, provider);
        }

        public AzureArmRequester forStopVirtualMachine(String id) throws CloudException {
            setRequestUri(String.format(STOP_VM, id));
            return createRequester();
        }
    }

    public static class PutRequestUrlBuilder extends RequestUrlBuilder {
        private PutRequestUrlBuilder(RequestBuilder requestBuilder, AzureArm provider) {
            super(requestBuilder, provider);
        }

        public AzureArmRequester forVirtualMachine(ArmVirtualMachineModel armVirtualMachineModel) throws CloudException, InternalException {
            String uri = getEncodedUri(String.format(CREATE_VM, provider.getContext().getCloud().getEndpoint(), provider.getContext().getAccountNumber(), armVirtualMachineModel.getResourceGroupName(), armVirtualMachineModel.getName()));
            setRequestUri(uri);
            setRequestEntity(new DaseinObjectToJsonEntity<ArmVirtualMachineModel>(armVirtualMachineModel));
            return createRequester();
        }
    }

    public static class DeleteRequestUrlBuilder extends RequestUrlBuilder {
        private DeleteRequestUrlBuilder(RequestBuilder requestBuilder, AzureArm provider) {
            super(requestBuilder, provider);
        }
    }

    public static GetRequestUrlBuilder createGetRequest(AzureArm provider) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.get();
        AzureArmRequester.addCommonHeaders(provider, requestBuilder);
        return new GetRequestUrlBuilder(requestBuilder, provider);
    }

    public static PostRequestUrlBuilder createPostRequest(AzureArm provider) throws CloudException {
        RequestBuilder requestBuilder = RequestBuilder.post();
        AzureArmRequester.addCommonHeaders(provider, requestBuilder);
        return new PostRequestUrlBuilder(requestBuilder, provider);
    }

    public static PutRequestUrlBuilder createPutRequest(AzureArm provider) throws InternalException, CloudException {
        RequestBuilder requestBuilder = RequestBuilder.put();
        AzureArmRequester.addCommonHeaders(provider, requestBuilder);
        return new PutRequestUrlBuilder(requestBuilder, provider);
    }

    public static DeleteRequestUrlBuilder createDeleteRequest(AzureArm provider) throws InternalException, CloudException {
        RequestBuilder requestBuilder = RequestBuilder.delete();
        AzureArmRequester.addCommonHeaders(provider, requestBuilder);
        return new DeleteRequestUrlBuilder(requestBuilder, provider);
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
