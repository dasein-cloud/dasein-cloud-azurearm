package org.dasein.cloud.azurearm.compute.image.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vmunthiu on 1/29/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDetails {
    @JsonProperty("location")
    private String location;
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}