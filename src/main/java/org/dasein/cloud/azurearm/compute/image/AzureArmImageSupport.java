package org.dasein.cloud.azurearm.compute.image;

import org.apache.http.client.methods.HttpUriRequest;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.azurearm.AzureArmRequester;
import org.dasein.cloud.azurearm.compute.image.model.ImageDetails;
import org.dasein.cloud.compute.*;
import org.dasein.cloud.util.requester.fluent.DaseinParallelRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmunthiu on 10/15/2015.
 */
public class AzureArmImageSupport extends AbstractImageSupport<AzureArm> {
    public AzureArmImageSupport(AzureArm provider) {
        super(provider);
    }

    @Override
    public ImageCapabilities getCapabilities() throws CloudException, InternalException {
        return new AzureArmImageCapabilities(this.getProvider());
    }

    @Nullable
    @Override
    public MachineImage getImage(@Nonnull String providerImageId) throws CloudException, InternalException {
        return null;
    }

    @Override
    public boolean isSubscribed() throws CloudException, InternalException {
        return false;
    }

    @Nonnull
    @Override
    public Iterable<MachineImage> listImages(@Nullable ImageFilterOptions options) throws CloudException, InternalException {
        return null;
    }

    @Override
    public void remove(@Nonnull String providerImageId, boolean checkState) throws CloudException, InternalException {

    }

    @Nonnull
    @Override
    public Iterable<MachineImage> searchPublicImages(@Nullable String keyword, @Nullable Platform platform, @Nullable Architecture architecture, @Nullable ImageClass... imageClasses) throws CloudException, InternalException {
        ArrayList<MachineImage> images = new ArrayList<MachineImage>();

        final ImageDetails[] publishers = new AzureArmRequester(this.getProvider(),
                new AzureArmImageRequests(this.getProvider()).listPublishersByLocation(this.getProvider().getContext().getRegionId()).build())
                .withJsonProcessor(ImageDetails[].class).execute();

        //List<String> commonPublishers = Arrays.asList("OpenLogic", "CoreOS", "MicrosoftDynamicsNAV", "MicrosoftSharePoint", "Microsoft", "MicrosoftSQLServer", "Canonical", "MicrosoftWindowsServer", "MicrosoftWindowsServerEssentials", "MicrosoftWindowsServerHPCPack");
        //List<String> commonPublishers = Arrays.asList("OpenLogic");

        //for (String pub : commonPublishers) {
        //    offerRequests.add(new AzureArmImageRequests(this.getProvider()).listOffers(pub).build());
        //}

        ArrayList<HttpUriRequest> offerRequests = new ArrayList<HttpUriRequest>();
        for(int i = 0; i<publishers.length; i++) {
            offerRequests.add(new AzureArmImageRequests(this.getProvider()).listOffers(publishers[i].getId()).build());
        }

        final List<ImageDetails[]> offers = new DaseinParallelRequest(this.getProvider(), this.getProvider().getAzureClientBuilderWithPooling(), offerRequests).withJsonProcessor(ImageDetails[].class).execute();
        ArrayList<HttpUriRequest> skusRequests = new ArrayList<HttpUriRequest>();
        for (ImageDetails[] offer : offers) {
            for (int i = 0; i<offer.length;i++) {
                skusRequests.add(new AzureArmImageRequests(this.getProvider()).listSkus(offer[i].getId()).build());
            }
        }

        final List<ImageDetails[]> skus = new DaseinParallelRequest(this.getProvider(), this.getProvider().getAzureClientBuilderWithPooling(), skusRequests).withJsonProcessor(ImageDetails[].class).execute();
        ArrayList<HttpUriRequest> versionRequests = new ArrayList<HttpUriRequest>();
        for (ImageDetails[] sku : skus) {
            for (int i = 0; i<sku.length;i++) {
                versionRequests.add(new AzureArmImageRequests(this.getProvider()).listVersions(sku[i].getId()).build());
            }
        }
        final List<ImageDetails[]> versions = new DaseinParallelRequest(this.getProvider(), this.getProvider().getAzureClientBuilderWithPooling(), versionRequests).withJsonProcessor(ImageDetails[].class).execute();
        ArrayList<HttpUriRequest> imageRequests = new ArrayList<HttpUriRequest>();
        for (ImageDetails[] version : versions) {
            for (int i = 0; i<version.length;i++) {
                imageRequests.add(new AzureArmImageRequests(this.getProvider()).getImage(version[i].getId()).build());
            }
        }

        final List<ImageDetails> imagesDetails = new DaseinParallelRequest(this.getProvider(), this.getProvider().getAzureClientBuilderWithPooling(), imageRequests).withJsonProcessor(ImageDetails.class).execute();
        return images;
    }

}
