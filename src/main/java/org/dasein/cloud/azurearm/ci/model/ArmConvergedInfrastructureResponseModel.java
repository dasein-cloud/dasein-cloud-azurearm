/**
 * Copyright (C) 2009-2016 Dell, Inc.
 * See annotations for authorship information
 * <p>
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.dasein.cloud.azurearm.ci.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: daniellemayne
 * Date: 14/03/2016
 * Time: 11:41
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArmConvergedInfrastructureResponseModel {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("properties")
    private Properties properties;

    private String providerDatacenterId;
    private String providerRegionId;
    private String providerResourceGroupId;

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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getProviderDatacenterId() {
        return providerDatacenterId;
    }

    public void setProviderDatacenterId(String providerDatacenterId) {
        this.providerDatacenterId = providerDatacenterId;
    }

    public String getProviderRegionId() {
        return providerRegionId;
    }

    public void setProviderRegionId(String providerRegionId) {
        this.providerRegionId = providerRegionId;
    }

    public String getProviderResourceGroupId() {
        return providerResourceGroupId;
    }

    public void setProviderResourceGroupId(String providerResourceGroupId) {
        this.providerResourceGroupId = providerResourceGroupId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Properties {
        @JsonProperty("mode")
        private String mode;
        @JsonProperty("provisioningState")
        private String ciState;
        @JsonProperty("timestamp")
        private String provisioningTimestamp;
        @JsonProperty("outputResources")
        private List<Dependency> dependencies;
        private Map<String,String> tags;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getCiState() {
            return ciState;
        }

        public void setCiState(String ciState) {
            this.ciState = ciState;
        }

        public String getProvisioningTimestamp() {
            return provisioningTimestamp;
        }

        public void setProvisioningTimestamp(String provisioningTimestamp) {
            this.provisioningTimestamp = provisioningTimestamp;
        }

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        public Map<String, String> getTags() {
            if (tags == null) {
                tags = new HashMap<String, String>();
            }
            return tags;
        }

        public void setTags(Map<String, String> tags) {
            this.tags = tags;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Dependency {
            @JsonProperty("id")
            private String resourceId;

            public String getOutputResourceId() {
                return resourceId;
            }

            public void setOutputResourceId(String resourceId) {
                this.resourceId = resourceId;
            }
        }
    }
}
