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

package org.dasein.cloud.azurearm.ci;

import org.dasein.cloud.AbstractCapabilities;
import org.dasein.cloud.Requirement;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.ci.ConvergedInfrastructureCapabilities;
import org.dasein.cloud.util.NamingConstraints;

import javax.annotation.Nonnull;

/**
 * User: daniellemayne
 * Date: 24/03/2016
 * Time: 11:59
 */
public class AzureArmConvergedInfrastructureCapabilities extends AbstractCapabilities<AzureArm> implements ConvergedInfrastructureCapabilities {
    public AzureArmConvergedInfrastructureCapabilities(@Nonnull AzureArm provider) {super(provider);}

    @Nonnull
    @Override
    public Requirement identifyResourcePoolLaunchRequirement() {
        return Requirement.REQUIRED;
    }

    @Nonnull
    @Override
    public Requirement identifyTemplateContentLaunchRequirement() {
        return Requirement.OPTIONAL;
    }

    @Nonnull
    @Override
    public NamingConstraints getConvergedInfrastructureNamingConstraints() {
        return NamingConstraints.getAlphaNumeric(1, 63)
                .withRegularExpression("^[a-z][-a-z0-9]{0,61}[a-z0-9]$")
                .lowerCaseOnly()
                .withNoSpaces()
                .withLastCharacterSymbolAllowed(false)
                .constrainedBy('-');
    }
}
