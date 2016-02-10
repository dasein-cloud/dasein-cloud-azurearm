package org.dasein.cloud.azurearm.network.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArmSubnetModel {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("etag")
    private String etag = "W/\"00000000-0000-0000-0000-000000000000\"";
    @JsonProperty("properties")
    private Properties properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Properties {
        @JsonProperty("provisioningState")
        private String provisioningState;
        @JsonProperty("addressPrefix")
        private String addressPrefix;
        @JsonProperty("networkSecurityGroup")
        private SubnetPropertyObject networkSecurityGroup;
        @JsonProperty("routeTable")
        private SubnetPropertyObject routeTable;
        @JsonProperty("ipConfigurations")
        private List<SubnetPropertyObject> ipConfigurations;

        public String getProvisioningState() {
            return provisioningState;
        }

        public void setProvisioningState(String provisioningState) {
            this.provisioningState = provisioningState;
        }

        public String getAddressPrefix() {
            return addressPrefix;
        }

        public void setAddressPrefix(String addressPrefix) {
            this.addressPrefix = addressPrefix;
        }

        public SubnetPropertyObject getNetworkSecurityGroup() {
            return networkSecurityGroup;
        }

        public void setNetworkSecurityGroup(SubnetPropertyObject networkSecurityGroup) {
            this.networkSecurityGroup = networkSecurityGroup;
        }

        public SubnetPropertyObject getRouteTable() {
            return routeTable;
        }

        public void setRouteTable(SubnetPropertyObject routeTable) {
            this.routeTable = routeTable;
        }

        public List<SubnetPropertyObject> getIpConfigurations() {
            return ipConfigurations;
        }

        public void setIpConfigurations(List<SubnetPropertyObject> ipConfigurations) {
            this.ipConfigurations = ipConfigurations;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubnetPropertyObject {
        @JsonProperty("id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
