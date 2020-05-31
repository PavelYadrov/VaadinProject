package com.netcracker.components;

import com.netcracker.dto.AdvertisementImage;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO Check again
@EqualsAndHashCode(callSuper = true)
@Data
public class ImagesField extends CustomField<AdvertisementImage> {

    private AdvertisementImage image;

    private List<AdvertisementImage> images = new ArrayList<>();
    
    private ByteArrayOutputStream outputStream;

    private Upload upload;

    public ImagesField() {


        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();

        upload = new Upload(multiFileMemoryBuffer);

        upload.addFinishedListener(e->{
            AdvertisementImage image = new AdvertisementImage();
            image.setName(e.getFileName());
            image.setExtension("."+e.getMIMEType().substring(6));
            image.setValue(multiFileMemoryBuffer.getOutputBuffer(e.getFileName()).toByteArray());
            images.add(image);
        });

        upload.getElement().addEventListener("file-remove", event -> {
            String fileName = event.getEventData().getString("event.detail.file.name");
            images = images.stream()
                    .filter(advertisementImage-> !advertisementImage.getName().equals(fileName))
                        .collect(Collectors.toList());
        }).addEventData("event.detail.file.name");

        UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(
                new UploadI18N.DropFiles().setOne("Drag file here...")
                        .setMany("Drag files here..."))
                .setAddFiles(new UploadI18N.AddFiles()
                        .setOne("Select file").setMany("Add images"))
                .setCancel("Cancel")
                .setError(new UploadI18N.Error()
                        .setTooManyFiles("Too many files.")
                        .setFileIsTooBig("File too large.")
                        .setIncorrectFileType("Incorrect file type."))
                .setUploading(new UploadI18N.Uploading()
                        .setStatus(new UploadI18N.Uploading.Status()
                                .setConnecting("Connecting...")
                                .setStalled("Stalled.")
                                .setProcessing("Processing..."))
                        .setRemainingTime(
                                new UploadI18N.Uploading.RemainingTime()
                                        .setPrefix("Remaining time: ")
                                        .setUnknown(
                                                "Remaining time unknown"))
                        .setError(new UploadI18N.Uploading.Error()
                                .setServerUnavailable("Server Unavailable")
                                .setUnexpectedServerError(
                                        "Unexpected server error")
                                .setForbidden("Forbidden")))
                .setUnits(Stream
                        .of("B", "KB", "MB", "GB", "TB", "PB")
                        .collect(Collectors.toList()));
        upload.setI18n(i18n);

        upload.getStyle().set("flex-grow", "1");

        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");

        upload.setMaxFiles(10);

        upload.setMaxFileSize(1 * 1024 * 1024);

        Div wrapper = new Div();
        wrapper.add(upload);
        wrapper.getStyle().set("display", "flex");
        add(wrapper);

        upload.addFailedListener(e -> setFailed(e.getReason().getMessage()));
        upload.addFileRejectedListener(e -> setFailed(e.getErrorMessage()));
    }

    @Override
    protected AdvertisementImage generateModelValue() {
        return image;
    }

    @Override
    protected void setPresentationValue(AdvertisementImage advertisementImage) {

    }

    private void setFailed(String message) {
        setInvalid(true);
        setErrorMessage(message);
    }

}




