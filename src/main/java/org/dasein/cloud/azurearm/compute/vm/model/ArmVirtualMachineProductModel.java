package org.dasein.cloud.azurearm.compute.vm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vmunthiu on 1/21/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmVirtualMachineProductModel {
    @JsonProperty("name")
    private String name;
    @JsonProperty("numberOfCores")
    private String numberOfCores;
    @JsonProperty("osDiskSizeInMB")
    private String osDiskSizeInMB;
    @JsonProperty("resourceDiskSizeInMB")
    private String resourceDiskSizeInMB;
    @JsonProperty("memoryInMB")
    private String memoryInMB;
    @JsonProperty("maxDataDiskCount")
    private String maxDataDiskCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfCores() {
        return numberOfCores;
    }

    public void setNumberOfCores(String numberOfCores) {
        this.numberOfCores = numberOfCores;
    }

    public String getOsDiskSizeInMB() {
        return osDiskSizeInMB;
    }

    public void setOsDiskSizeInMB(String osDiskSizeInMB) {
        this.osDiskSizeInMB = osDiskSizeInMB;
    }

    public String getResourceDiskSizeInMB() {
        return resourceDiskSizeInMB;
    }

    public void setResourceDiskSizeInMB(String resourceDiskSizeInMB) {
        this.resourceDiskSizeInMB = resourceDiskSizeInMB;
    }

    public String getMemoryInMB() {
        return memoryInMB;
    }

    public void setMemoryInMB(String memoryInMB) {
        this.memoryInMB = memoryInMB;
    }

    public String getMaxDataDiskCount() {
        return maxDataDiskCount;
    }

    public void setMaxDataDiskCount(String maxDataDiskCount) {
        this.maxDataDiskCount = maxDataDiskCount;
    }
}
