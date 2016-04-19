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

import java.util.List;

/**
 * User: daniellemayne
 * Date: 31/03/2016
 * Time: 12:49
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArmTemplateDeploymentOperationResponseModel {
    @JsonProperty("properties")
    private Properties properties;

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
        @JsonProperty("statusCode")
        private String statusCode;
        @JsonProperty("statusMessage")
        private StatusMessage statusMessage;

        public String getProvisioningState() {
            return provisioningState;
        }

        public void setProvisioningState(String provisioningState) {
            this.provisioningState = provisioningState;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public StatusMessage getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(StatusMessage statusMessage) {
            this.statusMessage = statusMessage;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class StatusMessage {
            @JsonProperty("status")
            private String status;
            @JsonProperty("error")
            private StatusError statusError;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public StatusError getStatusError() {
                return statusError;
            }

            public void setStatusError(StatusError statusError) {
                this.statusError = statusError;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class StatusError {
                @JsonProperty("code")
                private String code;
                @JsonProperty("message")
                private String message;
                @JsonProperty("details")
                private List<ErrorDetails> errorDetails;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getMessage() {
                    return message;
                }

                public void setMessage(String message) {
                    this.message = message;
                }

                public List<ErrorDetails> getErrorDetails() {
                    return errorDetails;
                }

                public void setErrorDetails(List<ErrorDetails> errorDetails) {
                    this.errorDetails = errorDetails;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class ErrorDetails {
                    @JsonProperty("code")
                    private String code;
                    @JsonProperty("message")
                    private String message;

                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public String getMessage() {
                        return message;
                    }

                    public void setMessage(String message) {
                        this.message = message;
                    }
                }
            }
        }
    }
}
