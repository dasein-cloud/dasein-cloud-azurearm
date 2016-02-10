package org.dasein.cloud.azurearm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Model for Azure ARM Resource Types
 * @author Drew Lyall (drew.lyall@imaginary.com)
 * @since 2015.06.1
 * @version 2015.06.1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmResourceTypeModel {
    @JsonProperty("resourceType")
    private String resourceType;
    @JsonProperty("locations")
    private List<String> locations = new ArrayList<String>();
    @JsonProperty("apiVersions")
    private List<String> apiVersions = new ArrayList<String>();

    public void setResourceType(String resourceType){
        this.resourceType = resourceType;
    }

    public String getResourceType(){
        return this.resourceType;
    }

    public void setLocations(ArrayList<String> locations){
        this.locations = locations;
    }

    public List<String> getLocations(){
        return this.locations;
    }

    public void setApiVersions(ArrayList<String> apiVersions){
        this.apiVersions = apiVersions;
    }

    public List<String> getApiVersions(){
        return this.apiVersions;
    }
}
