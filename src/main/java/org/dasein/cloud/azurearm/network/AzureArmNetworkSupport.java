package org.dasein.cloud.azurearm.network;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.network.model.ArmNetworkModel;
import org.dasein.cloud.azurearm.network.model.ArmNetworksModel;
import org.dasein.cloud.azurearm.network.model.ArmSubnetModel;
import org.dasein.cloud.azurearm.network.model.ArmSubnetsModel;
import org.dasein.cloud.dc.ResourcePool;
import org.dasein.cloud.network.AbstractVLANSupport;
import org.dasein.cloud.network.Subnet;
import org.dasein.cloud.network.SubnetCreateOptions;
import org.dasein.cloud.network.SubnetState;
import org.dasein.cloud.network.VLAN;
import org.dasein.cloud.network.VLANCapabilities;
import org.dasein.cloud.network.VlanCreateOptions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vmunthiu on 1/21/2016.
 */
public class AzureArmNetworkSupport extends AbstractVLANSupport<AzureArm> {
    protected AzureArmNetworkSupport(AzureArm provider) {
        super(provider);
    }

    @Override
    public @Nonnull VLAN createVlan(@Nonnull VlanCreateOptions vco) throws CloudException, InternalException {
        if(vco == null)
            throw new InternalException("VlanCreateOptions parameter cannot be null");

        ArmNetworkModel armNetworkModel = new ArmNetworkModel();
        armNetworkModel.setName(vco.getName());
        armNetworkModel.setLocation(this.getProvider().getContext().getRegionId());
        ArmNetworkModel.Properties networkProperties = new ArmNetworkModel.Properties();
        if(vco.getCidr() != null) {
            ArmNetworkModel.AddressSpace addressSpace = new ArmNetworkModel.AddressSpace();
            addressSpace.setAddressPrefixes(Arrays.asList(vco.getCidr()));
            networkProperties.setAddressSpace(addressSpace);
        }

        if(vco.getDnsServers() != null) {
            ArmNetworkModel.DHCPOptions dhcpOptions = new ArmNetworkModel.DHCPOptions();
            dhcpOptions.setDnsServers(Arrays.asList(vco.getDnsServers()));
            networkProperties.setDhcpOptions(dhcpOptions);
        }
        armNetworkModel.setProperties(networkProperties);

        ResourcePool resourceGroup = this.getProvider().getDataCenterServices().getResourcePool(vco.getResourcePoolId());
        ArmNetworkModel armNetworkModelResult = new AzureArmRequester(this.getProvider(), new AzureArmNetworkRequests(this.getProvider()).createNetwork(armNetworkModel, resourceGroup.getName()).build())
                .withJsonProcessor(ArmNetworkModel.class).execute();

        return vlanFrom(armNetworkModelResult);
    }

    public void removeVlan(final String vlanId) throws CloudException, InternalException {
        new AzureArmRequester(this.getProvider(), new AzureArmNetworkRequests(this.getProvider()).deleteNetwork(vlanId).build()).execute();
    }

    @Override
    public @Nonnull Iterable<VLAN> listVlans() throws CloudException, InternalException {
        ArmNetworksModel armNetworksModel =
                new AzureArmRequester(this.getProvider(), new AzureArmNetworkRequests(this.getProvider()).listNetworks().build())
                        .withJsonProcessor(ArmNetworksModel.class).execute();

        final ArrayList<VLAN> vlans = new ArrayList<VLAN>();
        for (ArmNetworkModel armNetworkModel : armNetworksModel.getArmNetworkModels()) {
            vlans.add(vlanFrom(armNetworkModel));
        }

        return vlans;
    }

    private VLAN vlanFrom(ArmNetworkModel armNetworkModel) throws CloudException, InternalException {
        VLAN vlan = new VLAN();
        vlan.setName(armNetworkModel.getName());
        vlan.setProviderVlanId(armNetworkModel.getId());
        vlan.setProviderDataCenterId(this.getProvider().getDataCenterServices().listDataCenters(armNetworkModel.getLocation()).iterator().next().getProviderDataCenterId());
        vlan.setProviderRegionId(armNetworkModel.getLocation());

        if(armNetworkModel.getProperties() != null) {
            if(armNetworkModel.getProperties().getAddressSpace() != null) {
                vlan.setCidr(armNetworkModel.getProperties().getAddressSpace().getAddressPrefixes().get(0));
            }

            if(armNetworkModel.getProperties().getDhcpOptions() != null) {
                vlan.setDnsServers(armNetworkModel.getProperties().getDhcpOptions().getDnsServers().toArray(new String[armNetworkModel.getProperties().getDhcpOptions().getDnsServers().size()]));
            }
        }

        return vlan;
    }

    @Override
    public @Nonnull Subnet createSubnet(@Nonnull SubnetCreateOptions options) throws CloudException, InternalException {
        if(options == null)
            throw new InternalException("SubnetCreateOptions parameter cannot be null");

        VLAN vlan = this.getVlan(options.getProviderVlanId());
        if(vlan == null)
            throw new InternalException("Invalid network id provided");

        ArmSubnetModel armSubnetModel = new ArmSubnetModel();
        armSubnetModel.setName(options.getName());
        ArmSubnetModel.Properties properties = new ArmSubnetModel.Properties();
        if(options.getCidr() != null) {
            properties.setAddressPrefix(options.getCidr());
        }
        armSubnetModel.setProperties(properties);

        ArmSubnetModel armSubnetModelResult = new AzureArmRequester(this.getProvider(),
                new AzureArmNetworkRequests(this.getProvider()).createSubnet(armSubnetModel,options.getProviderVlanId()).build())
                .withJsonProcessor(ArmSubnetModel.class).execute();

        return subnetFrom(armSubnetModelResult, options.getProviderVlanId());
    }


    @Override
    public @Nonnull Iterable<Subnet> listSubnets(@Nonnull final String vlanId) throws CloudException, InternalException {
        if(vlanId == null)
            throw new InternalException("vlanId cannot be null");

        VLAN vlan = this.getVlan(vlanId);
        if(vlan == null)
            throw new InternalException("Invalid network id provided");

        ArmSubnetsModel armSubnetsModel =
                new AzureArmRequester(this.getProvider(),
                        new AzureArmNetworkRequests(this.getProvider()).listSubnets(vlanId).build())
                        .withJsonProcessor(ArmSubnetsModel.class).execute();

        final ArrayList<Subnet> subnets = new ArrayList<Subnet>();
        for(ArmSubnetModel armSubnetModel : armSubnetsModel.getArmNetworkModels()) {
            subnets.add(subnetFrom(armSubnetModel, vlanId));
        }

        return subnets;
    }

    private Subnet subnetFrom(ArmSubnetModel armSubnetModel, String vlanId) throws InternalException {
        return Subnet.getInstance(this.getContext().getAccountNumber(),
                this.getContext().getRegionId(),
                vlanId,
                armSubnetModel.getId(),
                armSubnetModel.getProperties().getProvisioningState().equalsIgnoreCase("succeeded") ? SubnetState.AVAILABLE : SubnetState.PENDING,
                armSubnetModel.getName(),
                armSubnetModel.getName(),
                armSubnetModel.getProperties().getAddressPrefix());
    }

    @Override
    public void removeSubnet(final String providerSubnetId) throws CloudException, InternalException {
        new AzureArmRequester(this.getProvider(), new AzureArmNetworkRequests(this.getProvider()).deleteSubnet(providerSubnetId).build()).execute();
    }

    @Override
    public VLANCapabilities getCapabilities() throws CloudException, InternalException {
        return new AzureArmNetworkCapabilities(this.getProvider());
    }

    @Override
    public boolean isSubscribed() throws CloudException, InternalException {
        return true;
    }
}
