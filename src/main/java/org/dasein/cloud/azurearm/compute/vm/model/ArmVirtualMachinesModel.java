package org.dasein.cloud.azurearm.compute.vm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 8/10/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmVirtualMachinesModel {
    @JsonProperty("value")
    private List<ArmVirtualMachineModel> armVirtualMachineModels;

    public List<ArmVirtualMachineModel> getArmVirtualMachineModels() {
        return armVirtualMachineModels;
    }

    public void setArmVirtualMachineModels(List<ArmVirtualMachineModel> armVirtualMachineModels) {
        this.armVirtualMachineModels = armVirtualMachineModels;
    }
}
