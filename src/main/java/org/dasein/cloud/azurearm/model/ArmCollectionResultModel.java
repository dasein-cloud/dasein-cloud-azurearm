package org.dasein.cloud.azurearm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmCollectionResultModel<T> {
    @JsonProperty("value")
    private List<T> values;

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }
}
