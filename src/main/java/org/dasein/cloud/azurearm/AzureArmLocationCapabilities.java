/*
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

package org.dasein.cloud.azurearm;

import org.dasein.cloud.AbstractCapabilities;
import org.dasein.cloud.dc.DataCenterCapabilities;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * Configuration of DC Capabilities
 * @author Drew Lyall (drew.lyall@imaginary.com)
 * @since 2015.06.1
 * @version 2015.06.1
 */
public class AzureArmLocationCapabilities extends AbstractCapabilities<AzureArm> implements DataCenterCapabilities {
    public AzureArmLocationCapabilities( @Nonnull AzureArm provider ) {
        super(provider);
    }

    @Override
    public String getProviderTermForDataCenter( Locale locale ) {
        return "data center";
    }

    @Override
    public String getProviderTermForRegion( Locale locale ) {
        return "region";
    }

    @Override
    public boolean supportsAffinityGroups() {
        return true;
    }

    @Override
    public boolean supportsResourcePools() {
        return false;
    }

    /**
     * Specifies whether the given cloud supports the concept of storage pools
     */
    @Override
    public boolean supportsStoragePools() {
        return false;
    }

    @Override
    public boolean supportsFolders() {
        return false;
    }
}
