package org.dasein.cloud.azurearm.compute.vm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/21/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmVirtualMachineProductsModel {
    @JsonProperty("value")
    private List<ArmVirtualMachineProductModel> armVirtualMachineProductModels;

    public List<ArmVirtualMachineProductModel> getArmVirtualMachineProductModels() {
        return armVirtualMachineProductModels;
    }

    public void setArmVirtualMachineProductModels(List<ArmVirtualMachineProductModel> armVirtualMachineProductModels) {
        this.armVirtualMachineProductModels = armVirtualMachineProductModels;
    }
}
