package org.dasein.cloud.azurearm.compute.vm;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.RequestsDsl;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachineModel;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachineProductModel;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachineProductsModel;
import org.dasein.cloud.azurearm.compute.vm.model.ArmVirtualMachinesModel;
import org.dasein.cloud.compute.*;
import org.dasein.cloud.util.requester.DriverToCoreMapper;
import org.dasein.util.uom.storage.Megabyte;
import org.dasein.util.uom.storage.Storage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmunthiu on 8/10/2015.
 */
public class AzureArmVirtualMachineSupport extends AbstractVMSupport<AzureArm> {
    private AzureArm provider;

    public AzureArmVirtualMachineSupport(AzureArm provider) {
        super(provider);
        this.provider = provider;
    }

    @Nonnull
    @Override
    public VirtualMachineCapabilities getCapabilities() throws InternalException, CloudException {
        return new AzureArmVirtualMachineCapabilities(this.getProvider());
    }

    @Override
    public boolean isSubscribed() throws CloudException, InternalException {
        return true;
    }

    @Nonnull
    @Override
    public VirtualMachine launch(@Nonnull VMLaunchOptions withLaunchOptions) throws CloudException, InternalException {
        ArmVirtualMachineModel armVirtualMachineModel = new ArmVirtualMachineModel();
        armVirtualMachineModel =
                RequestsDsl.createPutRequest(provider)
                        .forVirtualMachine(armVirtualMachineModel)
                        .withJsonProcessor(ArmVirtualMachineModel.class)
                        .execute();

        return  virtualMachineFrom(armVirtualMachineModel);
    }

    @Override
    public void stop( @Nonnull String vmId, boolean force ) throws InternalException, CloudException {
        RequestsDsl.createPostRequest(provider).forStopVirtualMachine(vmId).execute();
    }

    @Override
    public void terminate(@Nonnull String vmId, String explanation) throws InternalException, CloudException {

    }

    @Override
    public @Nonnull Iterable<VirtualMachine> listVirtualMachines() throws InternalException, CloudException {
        //this.provider.getComputeServices().getImageSupport().searchPublicImages("", null, null, null);
        return RequestsDsl.createGetRequest(provider)
                .forVirtualMachines()
                .withJsonProcessor(new DriverToCoreMapper<ArmVirtualMachinesModel, List<VirtualMachine>>() {
                    @Override
                    public List<VirtualMachine> mapFrom(ArmVirtualMachinesModel entity) {
                        final ArrayList<VirtualMachine> result = new ArrayList<VirtualMachine>();
                        CollectionUtils.forAllDo(entity.getArmVirtualMachineModels(),new Closure() {
                            @Override
                            public void execute(Object input) {
                                result.add(virtualMachineFrom((ArmVirtualMachineModel) input));
                            }
                        } );
                        return result;
                    }
                }, ArmVirtualMachinesModel.class).execute();

//    if(virtualMachineModels == null || virtualMachineModels.getArmVirtualMachineModels() == null)
//            return Collections.emptyList();
//
//    final ArrayList<VirtualMachine> virtualMachines = new ArrayList<VirtualMachine>();
//    CollectionUtils.forAllDo(virtualMachineModels.getArmVirtualMachineModels(), new Closure() {
//        @Override
//        public void execute(Object input) {
//            virtualMachines.add(virtualMachineFrom((ArmVirtualMachineModel) input));
//        }
//    });
//
//    return virtualMachines;
    }

    @Override
    public @Nonnull Iterable<VirtualMachineProduct> listProducts(@Nonnull String machineImageId, @Nonnull final VirtualMachineProductFilterOptions options) throws InternalException, CloudException {
        ArrayList<VirtualMachineProduct> products = (ArrayList<VirtualMachineProduct>)listAllProducts();
        CollectionUtils.filter(products, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return options.matches((VirtualMachineProduct)object);
            }
        });

        return products;
    }

    @Override
    public @Nonnull Iterable<VirtualMachineProduct> listAllProducts() throws InternalException, CloudException{
        ArmVirtualMachineProductsModel armVirtualMachineProductsModel = new AzureArmRequester(this.provider, new AzureArmVirtualMachineRequests(this.provider).listVirtualMachinesProducts().build())
                .withJsonProcessor(ArmVirtualMachineProductsModel.class).execute();

        final ArrayList<VirtualMachineProduct> products = new ArrayList<VirtualMachineProduct>();
        CollectionUtils.forAllDo(armVirtualMachineProductsModel.getArmVirtualMachineProductModels(), new Closure() {
            @Override
            public void execute(Object input) {
                ArmVirtualMachineProductModel armVirtualMachineProductModel = (ArmVirtualMachineProductModel)input;

                VirtualMachineProduct product = new VirtualMachineProduct();
                product.setName(armVirtualMachineProductModel.getName());
                product.setProviderProductId(armVirtualMachineProductModel.getName());
                product.setCpuCount(Integer.parseInt(armVirtualMachineProductModel.getNumberOfCores()));
                product.setRamSize(new Storage<Megabyte>(Integer.parseInt(armVirtualMachineProductModel.getMemoryInMB()), Storage.MEGABYTE));
                product.setRootVolumeSize(new Storage<Megabyte>(Integer.parseInt(armVirtualMachineProductModel.getOsDiskSizeInMB()), Storage.MEGABYTE));
                product.setArchitectures(Architecture.I64);
                products.add(product);
            }
        });

        return products;
    }

    private VirtualMachine virtualMachineFrom(ArmVirtualMachineModel armVirtualMachineModel) {
        VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setProviderVirtualMachineId(armVirtualMachineModel.getId());
        virtualMachine.setProviderRegionId(armVirtualMachineModel.getLocation());
        //virtualMachine.setProviderDataCenterId(virtualMachineModel.getStampId());
        virtualMachine.setName(armVirtualMachineModel.getName());
        //virtualMachine.setCurrentState(getVmState(virtualMachineModel.getStatusString()));
        //virtualMachine.setProviderOwnerId(virtualMachineModel.getOwner().getRoleID());
        virtualMachine.setPersistent(true);

        return virtualMachine;
    }
}
