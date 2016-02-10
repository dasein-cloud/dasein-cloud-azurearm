package org.dasein.cloud.azurearm.compute;

import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.compute.image.AzureArmImageSupport;
import org.dasein.cloud.azurearm.compute.vm.AzureArmVirtualMachineSupport;
import org.dasein.cloud.compute.AbstractComputeServices;
import org.dasein.cloud.compute.MachineImageSupport;
import org.dasein.cloud.compute.VirtualMachineSupport;

import javax.annotation.Nonnull;

/**
 * Created by vmunthiu on 8/10/2015.
 */
public class AzureArmComputeService extends AbstractComputeServices<AzureArm> {
    private AzureArm provider;

    public AzureArmComputeService(@Nonnull AzureArm provider) {
        super(provider);
        this.provider = provider;
    }

    @Override
    public MachineImageSupport getImageSupport() {
        return new AzureArmImageSupport(provider);
    }

    @Override
    public VirtualMachineSupport getVirtualMachineSupport() {
        return new AzureArmVirtualMachineSupport(provider);
    }
}
