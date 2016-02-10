package org.dasein.cloud.azurearm.network;

import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.network.AbstractNetworkServices;
import org.dasein.cloud.network.VLANSupport;

import javax.annotation.Nullable;

/**
 * Created by vmunthiu on 1/21/2016.
 */
public class AzureArmNetworkServices extends AbstractNetworkServices<AzureArm> {
    private AzureArm provider;

    public AzureArmNetworkServices(AzureArm provider) {
        super(provider);
        this.provider = provider;
    }

    @Nullable
    @Override
    public VLANSupport getVlanSupport() {
        return new AzureArmNetworkSupport(provider);
    }
}
