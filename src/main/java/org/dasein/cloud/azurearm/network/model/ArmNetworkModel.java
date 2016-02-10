package org.dasein.cloud.azurearm.network.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by vmunthiu on 1/22/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArmNetworkModel {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("etag")
    private String etag = "W/\"00000000-0000-0000-0000-000000000000\"";
    @JsonProperty("location")
    private String location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        @JsonProperty("resourceGuid")
        private String resourceGuid;
        @JsonProperty("addressSpace")
        private AddressSpace addressSpace;
        @JsonProperty("dhcpOptions")
        private DHCPOptions dhcpOptions;
        @JsonProperty("subnets")
        private List<ArmSubnetModel> subnets;

        public String getProvisioningState() {
            return provisioningState;
        }

        public void setProvisioningState(String provisioningState) {
            this.provisioningState = provisioningState;
        }

        public String getResourceGuid() {
            return resourceGuid;
        }

        public void setResourceGuid(String resourceGuid) {
            this.resourceGuid = resourceGuid;
        }

        public AddressSpace getAddressSpace() {
            return addressSpace;
        }

        public void setAddressSpace(AddressSpace addressSpace) {
            this.addressSpace = addressSpace;
        }

        public DHCPOptions getDhcpOptions() {
            return dhcpOptions;
        }

        public void setDhcpOptions(DHCPOptions dhcpOptions) {
            this.dhcpOptions = dhcpOptions;
        }

        public List<ArmSubnetModel> getSubnets() {
            return subnets;
        }

        public void setSubnets(List<ArmSubnetModel> subnets) {
            this.subnets = subnets;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressSpace {
        @JsonProperty("addressPrefixes")
        private List<String> addressPrefixes;

        public List<String> getAddressPrefixes() {
            return addressPrefixes;
        }

        public void setAddressPrefixes(List<String> addressPrefixes) {
            this.addressPrefixes = addressPrefixes;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DHCPOptions {
        @JsonProperty("dnsServers")
        private List<String> dnsServers;

        public List<String> getDnsServers() {
            return dnsServers;
        }

        public void setDnsServers(List<String> dnsServers) {
            this.dnsServers = dnsServers;
        }
    }
}
