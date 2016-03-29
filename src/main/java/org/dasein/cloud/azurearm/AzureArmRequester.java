package org.dasein.cloud.azurearm;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.GeneralCloudException;
import org.dasein.cloud.util.requester.fluent.DaseinRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Provides simpler constructor for the underlying Dasein request object
 * @author Drew Lyall (drew.lyall@imaginary.com)
 * @since 2015.06.1
 * @version 2015.06.1
 */
public class AzureArmRequester extends DaseinRequest {
    private static final String LIST_VM_RESOURCES = "%s/subscriptions/%s/providers/Microsoft.Compute/virtualMachines?api-version=2015-06-15";
    private static final String CREATE_VM = "%s/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Compute/virtualMachines/%s?api-version=2015-06-15";
    private static final String STOP_VM = "%s/stop?api-version=2015-06-15";
    private static final String LIST_LOCATIONS = "%s/subscriptions/%s/providers/Microsoft.Compute?api-version=2015-01-01";
    private static final String AUTH_TOKEN_ENPOINT = "https://login.windows.net/%s/oauth2/token";
    private static final String RESOURCE_ENDPOINT = "https://management.core.windows.net/";

    public AzureArmRequester(AzureArm provider, HttpUriRequest httpUriRequest) throws CloudException {
        this(provider, provider.getAzureArmClientBuilder(), httpUriRequest);
    }
    public AzureArmRequester(CloudProvider provider, HttpClientBuilder httpClientBuilder, HttpUriRequest httpUriRequestBuilder) {
        super(provider, httpClientBuilder, httpUriRequestBuilder);
    }






    public static void addCommonHeaders(AzureArm provider, RequestBuilder requestBuilder) throws CloudException {
        requestBuilder.addHeader("Content-Type", "application/json");
        String authenticationToken = getAuthenticationToken(provider);
        requestBuilder.addHeader("Authorization", String.format("Bearer %s", authenticationToken));
    }

    public static String getAuthenticationToken(AzureArm provider) throws CloudException{
        ExecutorService service = Executors.newFixedThreadPool(1);;
        try {
            String username = (String)provider.getContext().getConfigurationValue("username");
            String password = (String)provider.getContext().getConfigurationValue("password");
            String adTenantId = (String)provider.getContext().getConfigurationValue("adTenantId");
            String applicationId = (String)provider.getContext().getConfigurationValue("applicationId");

            AuthenticationContext context = new AuthenticationContext(String.format(AUTH_TOKEN_ENPOINT, adTenantId), true, service);
            Future<AuthenticationResult> future = context.acquireToken(RESOURCE_ENDPOINT, applicationId, username, password, null);
            AuthenticationResult result = future.get();
            return result.getAccessToken();
        } catch(Exception ex){
            throw new GeneralCloudException("Could not obtain authentication token. " + ex.getMessage());
        }
        finally {
            service.shutdown();
        }
    }
}
