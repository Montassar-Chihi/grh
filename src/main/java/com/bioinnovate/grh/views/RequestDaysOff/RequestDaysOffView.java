package com.bioinnovate.grh.views.RequestDaysOff;

import com.bioinnovate.grh.data.entity.Department;
import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.entity.RequestDayOff;
import com.bioinnovate.grh.data.service.EmployeeService;
import com.bioinnovate.grh.data.service.RequestDayOffService;
import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.model.Header;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterItem;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterPart;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterPartPosition;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Route(value = "daysOff", layout = MainView.class)
@PageTitle("Congés")
@CssImport("./styles/views/main/main-view.css")
@Secured({"ADMIN","USER"})
public class RequestDaysOffView extends Div {

    private final Employee employee;
    private double joursRestants;
    private List<RequestDayOff> data;

    public RequestDaysOffView(@Autowired EmployeeService employeeService, @Autowired RequestDayOffService requestDayOffService){
        VaadinSession session = VaadinSession.getCurrent();
        employee = employeeService.findEmployeeByEmail(session.getAttribute("username").toString());

        Div container = new Div();
        container.getElement().getStyle().set("display", "flex");
        container.getElement().getStyle().set("flex-direction", "column");
        container.getElement().getStyle().set("height", "100%").set("width","75%").set("margin","25px auto");

        data = requestDayOffService.findRequestDayOffByEmployee(employee.getId());

        // Create a new calendar instance and attach it to our layout
        FullCalendar calendar = FullCalendarBuilder.create().build();
        calendar.setWidthFull();
        calendar.setLocale(new Locale("fr"));
        calendar.setWeekNumbersVisible(false);
        calendar.getStyle().set("height","100vh");
        joursRestants = employee.getDaysOffLeft() ;
        for (RequestDayOff r:requestDayOffService.findRequestDayOffByEmployee(employee.getId())){
            if(r.getStatus().equalsIgnoreCase("Accepté")){
                joursRestants -= r.getDuration();
            }
        }
        calendar.addTimeslotClickedListener(event -> {
            if ((event.getDate().compareTo(employee.getDepartment().getDateRedZone().toLocalDate()) >= 0)
            && (event.getDate().compareTo(employee.getDepartment().getDateRedZone().toLocalDate().plusDays(employee.getDepartment().getDurationRedZone()-1)) <= 0)){
                Notification.show("Zone Rouge ! Pas de congés pendant cette période").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }else if(joursRestants == 0){
                Notification.show("Solde de congé épuisé !").addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (employee.getSubstitute().getInDaysOff()){
                Notification.show("Vous ne pouvez pas prendre un congé quand votre Sub est en congé !").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }else {
                Dialog dialog = new Dialog();
                dialog.setWidth("50%");
                Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
                closeDialog.addClickListener(e -> {
                    data = requestDayOffService.findRequestDayOffByEmployee(employee.getId());
                    dialog.close();
                });
                closeDialog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                closeDialog.getStyle().set("float", "right").set("color", "red");
                dialog.add(closeDialog);
                H1 title = new H1("Demander un jour de congé");
                title.getStyle().set("margin", "10px auto");
                dialog.add(title);
                Div content = new Div();
                DatePicker dateBegin = new DatePicker("Date");
                dateBegin.setValue(event.getDate());
                dateBegin.setWidth("80%");
                dateBegin.getStyle().set("margin", "10px auto");
                IntegerField duration = new IntegerField("Durée");
                duration.setHasControls(true);
                duration.getStyle().set("margin", "10px 10%");
                duration.setMax((int) employee.getDaysOffLeft()+1);
                ComboBox<String> reason = new ComboBox<>("Raison");
                reason.setItems(" maladie ", " vacances ", " maternité ", "paternité");
                reason.setWidth("80%");
                reason.getStyle().set("margin", "10px auto");
                VerticalLayout verticalLayout = new VerticalLayout(dateBegin, duration, reason);
                Button send = new Button("Envoyer");
                send.setWidthFull();
                send.setIcon(new Icon(VaadinIcon.CHECK));
                send.addClickListener(event1 -> {
                    RequestDayOff requestDayOff = new RequestDayOff();
                    requestDayOff.setDateBegin(Date.valueOf(dateBegin.getValue()));
                    requestDayOff.setDuration(duration.getValue());
                    requestDayOff.setEmployee(employee);
                    requestDayOff.setDepartment(employee.getDepartment());
                    requestDayOff.setReason(reason.getValue());
                    requestDayOff.setStatus("En attente");
                    requestDayOffService.update(requestDayOff);
                    dialog.close();
                    UI.getCurrent().getPage().reload();
                });
                Button cancel = new Button("Annuler");
                cancel.setWidthFull();
                cancel.setIcon(new Icon(VaadinIcon.CLOSE));
                cancel.addClickListener(event1 -> dialog.close());
                HorizontalLayout buttonLayout = new HorizontalLayout(send, cancel);
                content.add(verticalLayout, buttonLayout);
                dialog.add(content);
                add(dialog);
                dialog.open();
            }
        });
        calendar.setColumnHeader(true);
        container.add(calendar);
        Header header = new Header();
        HeaderFooterPart headerFooterPart = new HeaderFooterPart(HeaderFooterPartPosition.CENTER);
        headerFooterPart.addItem(HeaderFooterItem.BUTTON_PREVIOUS);
        headerFooterPart.addItem(HeaderFooterItem.BUTTON_TODAY);
        headerFooterPart.addItem(HeaderFooterItem.BUTTON_NEXT);
        headerFooterPart.addItem(HeaderFooterItem.TITLE);
        header.addPart(headerFooterPart);
        calendar.setHeaderToolbar(header);

        add(container);

        try {
            Department department = employee.getDepartment();
            Entry redZone = new Entry();
            redZone.setColor("red");
            redZone.setTitle("Zone Rouge ! Pas de congés pendant cette période");
            redZone.setStart(department.getDateRedZone().toLocalDate());
            redZone.setEnd(redZone.getStart().plusDays(department.getDurationRedZone()));
            redZone.setEditable(false);
            redZone.setAllDay(true);
            calendar.addEntry(redZone);
        }catch (Exception ignored){

        }
        for (RequestDayOff requestDayOff:data){
            // Create an initial sample entry
            Entry entry = new Entry();
            entry.setGroupId(String.valueOf(requestDayOff.getId()));
            entry.setTitle(requestDayOff.getReason());
            entry.setEditable(false);
            entry.setAllDay(true);
            if (requestDayOff.getStatus().equalsIgnoreCase("En attente")){
                entry.setColor("orange");
            } else if (requestDayOff.getStatus().equalsIgnoreCase("Accepté")){
                entry.setColor("green");
            }else{
                entry.setColor("red");
            }
            // the given times will be interpreted as utc based - useful when the times are fetched from your database
            entry.setStart(requestDayOff.getDateBegin().toLocalDate());
            entry.setEnd(entry.getStart().plusDays(requestDayOff.getDuration()));
            calendar.addEntry(entry);
        }
        calendar.addEntryClickedListener(event -> {
            RequestDayOff requestDayOff = requestDayOffService.get(Integer.parseInt(event.getEntry().getGroupId())).get();
            Dialog dialog = new Dialog();
            dialog.setWidth("50%");
            Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
            closeDialog.addClickListener(e -> {
                data = requestDayOffService.findRequestDayOffByEmployee(employee.getId());
                dialog.close();
            });
            closeDialog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeDialog.getStyle().set("float","right").set("color","red");
            dialog.add(closeDialog);
            H1 title = new H1("Demander un jour de congé");
            title.getStyle().set("margin","10px auto");
            dialog.add(title);

            Div content = new Div();

            DatePicker dateBegin = new DatePicker("Date");
            dateBegin.setWidth("80%");
            dateBegin.getStyle().set("margin","10px auto");
            dateBegin.setValue(requestDayOff.getDateBegin().toLocalDate());

            IntegerField duration = new IntegerField("Durée");
            duration.getStyle().set("margin","10px 10%");
            duration.setHasControls(true);
            duration.setValue(requestDayOff.getDuration());

            ComboBox<String> reason = new ComboBox<>("Raison");
            reason.setItems(" maladie "," vacances "," maternité ","paternité");
            reason.setWidth("80%");
            reason.getStyle().set("margin","10px auto");
            reason.setValue(requestDayOff.getReason());

            Span status = new Span("Status : "+requestDayOff.getStatus());
            status.setWidth("80%");
            status.getStyle().set("margin","10px auto");

            VerticalLayout verticalLayout = new VerticalLayout(dateBegin,duration,reason,status);

            if (requestDayOff.getComment() != null){
                Span comment = new Span("Commentaire : " + requestDayOff.getComment());
                comment.setWidth("80%");
                comment.getStyle().set("margin", "10px auto");
                verticalLayout.add(comment);
            }

            Button save = new Button("Sauvgarder");
            save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            save.setIcon(new Icon(VaadinIcon.CHECK));
            save.setWidthFull();
            save.addClickListener(event1 -> {
                requestDayOff.setDateBegin(Date.valueOf(dateBegin.getValue()));
                requestDayOff.setDuration(duration.getValue());
                requestDayOff.setEmployee(employee);
                requestDayOff.setDepartment(employee.getDepartment());
                requestDayOff.setReason(reason.getValue());
                requestDayOff.setStatus("En attente");
                requestDayOffService.update(requestDayOff);
                dialog.close();
                UI.getCurrent().getPage().reload();
            });

            Button delete = new Button("Supprimer");
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.setIcon(new Icon(VaadinIcon.TRASH));
            delete.setWidthFull();
            delete.addClickListener(event1 -> {
                requestDayOffService.deleteRequestDayOff(requestDayOff.getId());
                UI.getCurrent().getPage().reload();
            });

            Button cancel = new Button("Annuler");
            cancel.setWidthFull();
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            cancel.setIcon(new Icon(VaadinIcon.CLOSE));
            cancel.addClickListener(event1 -> dialog.close());

            if ((requestDayOff.getDateBegin().toLocalDate().compareTo(LocalDate.now()) < 0) || !(requestDayOff.getStatus().equalsIgnoreCase("en attente"))){
                save.setEnabled(false);
                delete.setEnabled(false);
            }
            HorizontalLayout buttonLayout = new HorizontalLayout(save,cancel,delete);

            content.add(verticalLayout,buttonLayout);
            dialog.add(content);
            add(dialog);
            dialog.open();
        });

    }


}
