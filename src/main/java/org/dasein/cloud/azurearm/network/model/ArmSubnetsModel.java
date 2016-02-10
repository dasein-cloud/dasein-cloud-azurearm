package org.dasein.cloud.azurearm.network.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/26/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmSubnetsModel {
    @JsonProperty("value")
    private List<ArmSubnetModel> armNetworkModels;

    public List<ArmSubnetModel> getArmNetworkModels() {
        return armNetworkModels;
    }

    public void setArmNetworkModels(List<ArmSubnetModel> armNetworkModels) {
        this.armNetworkModels = armNetworkModels;
    }
}
