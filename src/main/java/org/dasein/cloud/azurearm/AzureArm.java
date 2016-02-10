/*
 * Copyright (C) 2013-2014 Dell, Inc
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.dasein.cloud.azurearm;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.dasein.cloud.AbstractCloud;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.ContextRequirements;
import org.dasein.cloud.azurearm.compute.AzureArmComputeService;
import org.dasein.cloud.azurearm.network.AzureArmNetworkServices;
import org.dasein.cloud.dc.DataCenterServices;
import org.dasein.cloud.network.NetworkServices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Core cloud provider implementation for the Microsoft Azure cloud.
 * @author Drew Lyall (drew.lyall@imaginary.com)
 * @since 2015.06.1
 * @version 2015.06.1
 */
public class AzureArm extends AbstractCloud {
    private String apiVersion = "2015-01-01";
    public String accessToken = "";
    public String refreshToken = "";

    public AzureArm(){}

    public HttpClientBuilder getAzureArmClientBuilder() throws CloudException {
        try {
            HttpClientBuilder builder = HttpClientBuilder.create();
            HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager();
            builder.setConnectionManager(ccm);
            return builder;
        } catch (Exception e) {
            throw new CloudException(e.getMessage());
        }
    }

    public HttpClientBuilder getAzureClientBuilderWithPooling() throws CloudException {
        try {

            HttpClientBuilder builder = HttpClientBuilder.create();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);
            builder.setConnectionManager(connManager);
            return builder;
        } catch (Exception e) {
            throw new CloudException(e.getMessage());
        }
    }

    @Override
    public @Nonnull String getCloudName() {
        return "Azure";
    }

    @Override
    public @Nonnull ContextRequirements getContextRequirements() {
        return new ContextRequirements(
                new ContextRequirements.Field("username", "Active Directory Username", ContextRequirements.FieldType.TEXT, true),
                new ContextRequirements.Field("password", "Active Directory Password", ContextRequirements.FieldType.TEXT, true),
                new ContextRequirements.Field("adTenantId", "Active Directory Client Application Authentication Endpoint Id", ContextRequirements.FieldType.TEXT, true),
                new ContextRequirements.Field("applicationId", "Active Directory Client Application Id", ContextRequirements.FieldType.TEXT, true),
                new ContextRequirements.Field("proxyHost", "Proxy host", ContextRequirements.FieldType.TEXT, false),
                new ContextRequirements.Field("proxyPort", "Proxy port", ContextRequirements.FieldType.TEXT, false)
        );
    }

    @Override
    public @Nonnull AzureArmComputeService getComputeServices() { return new AzureArmComputeService(this); }

    @Override
    public @Nonnull DataCenterServices getDataCenterServices() {
        return new AzureArmLocation(this);
    }

    @Override
    public @Nullable NetworkServices getNetworkServices() { return new AzureArmNetworkServices(this);  }

    @Override
    public @Nonnull String getProviderName() {
        return "Microsoft";
    }


    public String getAPIVersion(){
        return apiVersion;
    }

    @Override
    public @Nullable String testContext(){
        String tenantId = "";
        String clientId = "";

        List<ContextRequirements.Field> fields = getContextRequirements().getConfigurableValues();
        for(ContextRequirements.Field f : fields ) {
            if(f.name.equals("apiKeyShared"))tenantId = (String)getContext().getConfigurationValue(f);
            else if(f.name.equals("apiKeySecret"))clientId = (String)getContext().getConfigurationValue(f);
        }
        //TODO: Fix me
        return "";
    }
}
