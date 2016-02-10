package org.dasein.cloud.azurearm.network;

import org.dasein.cloud.*;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.network.IPVersion;
import org.dasein.cloud.network.VLANCapabilities;
import org.dasein.cloud.util.NamingConstraints;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Created by vmunthiu on 1/21/2016.
 */
public class AzureArmNetworkCapabilities extends AbstractCapabilities<AzureArm> implements VLANCapabilities {
    public AzureArmNetworkCapabilities(@Nonnull AzureArm provider) {
        super(provider);
    }

    @Override
    public boolean allowsNewNetworkInterfaceCreation() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean allowsNewVlanCreation() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean allowsNewRoutingTableCreation() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean allowsNewSubnetCreation() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean allowsMultipleTrafficTypesOverSubnet() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean allowsMultipleTrafficTypesOverVlan() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean allowsDeletionOfReservedSubnets() throws CloudException, InternalException {
        return false;
    }

    @Override
    public int getMaxNetworkInterfaceCount() throws CloudException, InternalException {
        return 0;
    }

    @Override
    public int getMaxVlanCount() throws CloudException, InternalException {
        return 0;
    }

    @Nonnull
    @Override
    public String getProviderTermForNetworkInterface(@Nonnull Locale locale) {
        return null;
    }

    @Nonnull
    @Override
    public String getProviderTermForSubnet(@Nonnull Locale locale) {
        return null;
    }

    @Nonnull
    @Override
    public String getProviderTermForVlan(@Nonnull Locale locale) {
        return null;
    }

    @Nonnull
    @Override
    public Requirement getRoutingTableSupport() throws CloudException, InternalException {
        return null;
    }

    @Nonnull
    @Override
    public Requirement getSubnetSupport() throws CloudException, InternalException {
        return null;
    }

    @Nullable
    @Override
    public VisibleScope getVLANVisibleScope() {
        return null;
    }

    @Nonnull
    @Override
    public Requirement identifySubnetDCRequirement() throws CloudException, InternalException {
        return null;
    }

    @Override
    public boolean isNetworkInterfaceSupportEnabled() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean isSubnetDataCenterConstrained() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean isVlanDataCenterConstrained() throws CloudException, InternalException {
        return false;
    }

    @Nonnull
    @Override
    public Iterable<IPVersion> listSupportedIPVersions() throws CloudException, InternalException {
        return null;
    }

    @Override
    public boolean supportsInternetGatewayCreation() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsRawAddressRouting() throws CloudException, InternalException {
        return false;
    }

    @Nonnull
    @Override
    public NamingConstraints getVlanNamingConstraints() throws CloudException, InternalException {
        return null;
    }
}
