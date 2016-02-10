package org.dasein.cloud.azurearm.network.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmNetworksModel {
    @JsonProperty("value")
    private List<ArmNetworkModel> armNetworkModels;

    public List<ArmNetworkModel> getArmNetworkModels() {
        return armNetworkModels;
    }

    public void setArmNetworkModels(List<ArmNetworkModel> armNetworkModels) {
        this.armNetworkModels = armNetworkModels;
    }
}
