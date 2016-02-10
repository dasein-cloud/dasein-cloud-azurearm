/**
 * Copyright (C) 2013-2014 Dell, Inc
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.dasein.cloud.azurearm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Model for Azure ARM Locations
 * @author Drew Lyall (drew.lyall@imaginary.com)
 * @since 2015.06.1
 * @version 2015.06.1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArmProviderModel {
    @JsonProperty("id")
    private String id;
    @JsonProperty("namespace")
    private String namespace;
    @JsonProperty("resourceTypes")
    private List<ArmResourceTypeModel> azureArmResourceTypes = new ArrayList<ArmResourceTypeModel>();
    @JsonProperty("registrationState")
    private String registrationState;

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setNamespace(String namespace){
        this.namespace = namespace;
    }

    public String getNamespace(){
        return this.namespace;
    }

    public void setAzureArmResourceTypes(ArrayList<ArmResourceTypeModel> azureArmResourceTypes){
        this.azureArmResourceTypes = azureArmResourceTypes;
    }

    public List<ArmResourceTypeModel> getAzureArmResourceTypes(){
        return this.azureArmResourceTypes;
    }

    public void setRegistrationState(String registrationState){
        this.registrationState = registrationState;
    }

    public String getRegistrationState(){
        return this.registrationState;
    }
}
