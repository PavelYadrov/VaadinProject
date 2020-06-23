package com.netcracker.components;

import com.netcracker.dto.AdvertisementImage;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@EqualsAndHashCode(callSuper = true)
@Data
public class AvatarField extends CustomField<AdvertisementImage> {

    private AdvertisementImage value;
    private ByteArrayOutputStream outputStream;

    private Image currentAvatar;
    private Upload upload;

    public AvatarField() {
        currentAvatar = new Image();
        currentAvatar.setAlt("avatar image");
        currentAvatar.setMaxHeight("100px");
        currentAvatar.setMaxWidth("100px");
        currentAvatar.setSizeFull();
        currentAvatar.getStyle().set("margin-right", "15px");
        currentAvatar.setVisible(false);

        upload = new Upload(this::receiveUpload);
        upload.getStyle().set("flex-grow", "1");

        upload.addSucceededListener(e -> uploadSuccess(e));

        upload.addFailedListener(e -> setFailed(e.getReason().getMessage()));
        upload.addFileRejectedListener(e -> setFailed(e.getErrorMessage()));
        upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png");

        upload.setMaxFiles(1);

        upload.setMaxFileSize(1 * 1024 * 1024);

        Div wrapper = new Div();
        wrapper.add(currentAvatar, upload);
        wrapper.getStyle().set("display", "flex");
        add(wrapper);
    }

    @Override
    protected AdvertisementImage generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(AdvertisementImage newPresentationValue) {
        value = newPresentationValue;
        updateImage();
    }

    private OutputStream receiveUpload(String fileName, String mimeType) {

        setInvalid(false);

        value = new AdvertisementImage();
        value.setName(fileName);
        value.setExtension("." + mimeType.substring(6));

        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    private void uploadSuccess(SucceededEvent e) {
        value.setValue(outputStream.toByteArray());
        setModelValue(value, true);
        updateImage();
        upload.getElement().executeJs("this.files=[]");
    }

    private void setFailed(String message) {
        setInvalid(true);
        setErrorMessage(message);
    }

    private void updateImage() {
        if (value != null && value.getValue() != null) {
            currentAvatar.setSrc(new StreamResource("image", () -> new ByteArrayInputStream(value.getValue())));
            currentAvatar.setVisible(true);
        } else {
            currentAvatar.setSrc("");
            currentAvatar.setVisible(false);
        }
    }

}
