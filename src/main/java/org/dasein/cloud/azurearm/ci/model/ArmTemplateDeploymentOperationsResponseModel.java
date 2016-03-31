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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * User: daniellemayne
 * Date: 31/03/2016
 * Time: 12:48
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmTemplateDeploymentOperationsResponseModel {
    @JsonProperty("value")
    private List<ArmTemplateDeploymentOperationResponseModel> armTemplateDeploymentOperationModels;

    public List<ArmTemplateDeploymentOperationResponseModel> getArmTemplateDeploymentOperationModels() {
        return armTemplateDeploymentOperationModels;
    }

    public void setArmTemplateDeploymentOperationModels(List<ArmTemplateDeploymentOperationResponseModel> armTemplateDeploymentOperationModels) {
        this.armTemplateDeploymentOperationModels = armTemplateDeploymentOperationModels;
    }
}
