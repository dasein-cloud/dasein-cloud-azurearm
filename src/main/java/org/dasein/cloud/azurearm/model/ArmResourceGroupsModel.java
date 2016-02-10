package org.dasein.cloud.azurearm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/20/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmResourceGroupsModel {
    @JsonProperty("value")
    private List<ArmResourceGroupModel> armResourceGroupModels;

    public List<ArmResourceGroupModel> getArmResourceGroupModels() {
        return armResourceGroupModels;
    }

    public void setArmResourceGroupModels(List<ArmResourceGroupModel> armResourceGroupModels) {
        this.armResourceGroupModels = armResourceGroupModels;
    }
}
