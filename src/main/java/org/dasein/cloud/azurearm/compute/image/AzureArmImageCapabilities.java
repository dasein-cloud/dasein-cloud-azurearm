package org.dasein.cloud.azurearm.compute.image;

import org.dasein.cloud.*;
import org.dasein.cloud.azurearm.AzureArm;
import org.dasein.cloud.compute.*;
import org.dasein.cloud.util.NamingConstraints;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Created by vmunthiu on 10/15/2015.
 */
public class AzureArmImageCapabilities extends AbstractCapabilities<AzureArm> implements ImageCapabilities {
    public AzureArmImageCapabilities(@Nonnull AzureArm provider) {
        super(provider);
    }

    @Override
    public boolean canBundle(@Nonnull VmState fromState) throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean canImage(@Nonnull VmState fromState) throws CloudException, InternalException {
        return false;
    }

    @Nonnull
    @Override
    public String getProviderTermForImage(@Nonnull Locale locale, @Nonnull ImageClass cls) {
        return null;
    }

    @Nonnull
    @Override
    public String getProviderTermForCustomImage(@Nonnull Locale locale, @Nonnull ImageClass cls) {
        return null;
    }

    @Nullable
    @Override
    public VisibleScope getImageVisibleScope() {
        return null;
    }

    @Nonnull
    @Override
    public Requirement identifyLocalBundlingRequirement() throws CloudException, InternalException {
        return null;
    }

    @Nonnull
    @Override
    public Iterable<MachineImageFormat> listSupportedFormats() throws CloudException, InternalException {
        return null;
    }

    @Nonnull
    @Override
    public Iterable<MachineImageFormat> listSupportedFormatsForBundling() throws CloudException, InternalException {
        return null;
    }

    @Nonnull
    @Override
    public Iterable<ImageClass> listSupportedImageClasses() throws CloudException, InternalException {
        return null;
    }

    @Nonnull
    @Override
    public Iterable<MachineImageType> listSupportedImageTypes() throws CloudException, InternalException {
        return null;
    }

    @Override
    public boolean supportsDirectImageUpload() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsImageCapture(@Nonnull MachineImageType type) throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsImageCopy() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsImageRemoval() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsImageSharing() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsImageSharingWithPublic() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsListingAllRegions() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean supportsPublicLibrary(@Nonnull ImageClass cls) throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean imageCaptureDestroysVM() throws CloudException, InternalException {
        return false;
    }

    @Nonnull
    @Override
    public NamingConstraints getImageNamingConstraints() throws CloudException, InternalException {
        return null;
    }
}
