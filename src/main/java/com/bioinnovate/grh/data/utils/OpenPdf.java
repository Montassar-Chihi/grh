package com.bioinnovate.grh.data.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import java.io.File;
import java.io.InputStream;

import static com.helger.commons.io.file.FileChannelHelper.getInputStream;


public class OpenPdf extends Dialog {

    private IFrame iFrame;
    private String fileName ;
    private String destination;

    public OpenPdf(String fileName,String destination) {

        this.setHeight("calc(100vh - (2*var(--lumo-space-m)))");
        this.setWidth("calc(100vw - (4*var(--lumo-space-m)))");
        this.fileName = fileName;
        this.destination = destination;
        buildLayout();
    }

    public void buildLayout() {
        // HEADER
        HorizontalLayout header = new HorizontalLayout();
        header.setMaxHeight("1em");
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle().set("margin-top", "-1em");


        Icon closeIcon = new Icon(VaadinIcon.CLOSE);
        Button closeButton = new Button();
        closeButton.setIcon(closeIcon);
        closeButton.getStyle().set("border", "none");
        closeButton.getStyle().set("background", "transparent").set("color","red");
        closeButton.addClickListener(click -> this.close());

        header.add(closeButton);
        this.add(header);

        // PDF-VIEW
        iFrame = new IFrame();
        iFrame.setSizeFull();
        String pathName = "C:\\Users\\ASUS\\Desktop\\my shit\\work shit\\Smart Database\\resources\\" ;
        File file = new File(pathName + destination + "\\" + fileName);
        InputStream input = getClass().getClassLoader().getResourceAsStream(pathName + destination);
        StreamResource resource = new StreamResource(""+fileName, () -> getInputStream(file));
        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        iFrame.setSrc(registration.getResourceUri().toString());

        this.add(iFrame);
        this.open();
    }


}
