package com.bioinnovate.grh.views.RequestDaysOff;

import com.bioinnovate.grh.data.entity.DaysOff;
import com.bioinnovate.grh.data.entity.Department;
import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.entity.RequestDayOff;
import com.bioinnovate.grh.data.service.*;
import com.bioinnovate.grh.data.utils.UpdateSoldeDaysOff;
import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.model.Header;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterPart;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterItem;
import org.vaadin.stefan.fullcalendar.model.HeaderFooterPartPosition;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Route(value = "daysOffManagement", layout = MainView.class)
@PageTitle("Congés")
@CssImport("./styles/views/main/main-view.css")
@Secured({"ADMIN","SUPER USER"})
public class RequestDayOffManagementView extends Div {

    private final Div container;
    private Div content;
    private RequestDayOff requestDayOff = new RequestDayOff();
    private final Grid<RequestDayOff> requestDayOffGrid = new Grid<>(RequestDayOff.class,false);
    private final Employee employee;
    private FullCalendar calendar;

    public RequestDayOffManagementView(@Autowired EmployeeService employeeService, @Autowired RequestDayOffService requestDayOffService,
                                       @Autowired DepartmentService departmentService, @Autowired DelaysService delaysService,
                                       @Autowired AbsenceService absenceService, @Autowired DaysOffService daysOffService){
//        Select User
        VaadinSession session = VaadinSession.getCurrent();
        employee = employeeService.findEmployeeByEmail(session.getAttribute("username").toString());

//        Create UI
        container = new Div();    // container of the calendar
        Div container2 = new Div();  // container of the grid and the request details
        content = new Div();    // container of the request details
        //  Initial view should contain the grid
        requestDayOffGrid.setVisible(true);
        content.setVisible(false);

        H1 title = new H1("Demande de Congé");
        title.getStyle().set("margin","10px auto");
        container2.add(title);
        container2.add(requestDayOffGrid,content);
//        Style UI
        container.setWidth("55%");
        container.getElement().getStyle().set("display", "flex");
        container.getElement().getStyle().set("flex-direction", "column");
        container.getElement().getStyle().set("height", "100%").set("margin","25px auto");
        container2.setWidth("35%");
        container2.getElement().getStyle().set("display", "flex");
        container2.getElement().getStyle().set("flex-direction", "column");
        container2.getElement().getStyle().set("height", "100%").set("margin","25px auto");
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.add(container, container2);
        add(mainLayout);

        configureCalendar(requestDayOffService);
        configureGrid(requestDayOffService,delaysService,absenceService,employeeService,daysOffService);
        configureZoneRouge(departmentService);

    }

    private void configureCalendar(RequestDayOffService requestDayOffService){
        // Create a new calendar instance and attach it to our layout
        calendar = FullCalendarBuilder.create().build();
        calendar.setWidthFull();
        calendar.getStyle().set("height","100vh");
        calendar.setColumnHeader(true);
        Header header = new Header();
        HeaderFooterPart headerFooterPart = new HeaderFooterPart(HeaderFooterPartPosition.CENTER);
        headerFooterPart.addItem(HeaderFooterItem.BUTTON_PREVIOUS);
        headerFooterPart.addItem(HeaderFooterItem.BUTTON_TODAY);
        headerFooterPart.addItem(HeaderFooterItem.BUTTON_NEXT);
        headerFooterPart.addItem(HeaderFooterItem.TITLE);
        header.addPart(headerFooterPart);
        calendar.setHeaderToolbar(header);
        calendar.setLocale(new Locale("fr"));
        calendar.setWeekNumbersVisible(false);
        container.add(calendar);

        addEntriesToCalendar(requestDayOffService);

        addDaySelectedListener(requestDayOffService);
    }

    private void addEntriesToCalendar(RequestDayOffService requestDayOffService){
        //        Fill Calendar with data
        List<RequestDayOff> data = requestDayOffService.findRequestDayOffByDepartmentId(employee.getDepartment().getId());
        for (RequestDayOff requestDayOff: data){
            // Create an initial sample entry
            Entry entry = new Entry();
            entry.setEditable(false);
            entry.setTitle(requestDayOff.getEmployee().getFirstName()+" "+requestDayOff.getEmployee().getLastName() );
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
            entry.setAllDay(true);
            calendar.addEntry(entry);
        }
    }

    private void addDaySelectedListener(RequestDayOffService requestDayOffService){
        //        Add on click effect when user click on the calendar date
        calendar.addTimeslotsSelectedListener(event -> {
            List<RequestDayOff> dataGrid = requestDayOffService.findRequestDayOffByDepartmentIdAndDateBegin(employee.getDepartment().getId(),Date.valueOf(event.getStart().toLocalDate()));
            requestDayOffGrid.setDataProvider(new ListDataProvider<>(dataGrid));
            content.setVisible(false);
            requestDayOffGrid.setVisible(true);
        });
    }

    private void configureGrid(RequestDayOffService requestDayOffService,DelaysService delaysService,
                               AbsenceService absenceService,EmployeeService employeeService,DaysOffService daysOffService){

        requestDayOffGrid.addColumn(new ComponentRenderer<>(requestDayOff -> new Text(requestDayOff.getEmployee().getName()))).setHeader("Employée");
        requestDayOffGrid.addColumn("dateBegin");
        requestDayOffGrid.addColumn("duration");
        requestDayOffGrid.getColumnByKey("dateBegin").setHeader("Date de début");
        requestDayOffGrid.getColumnByKey("duration").setHeader("Durée");

        addGridsRowSelectionClickListener(requestDayOffService,delaysService,absenceService,employeeService,daysOffService);

    }

    private void addGridsRowSelectionClickListener(RequestDayOffService requestDayOffService,DelaysService delaysService,
                                                   AbsenceService absenceService,EmployeeService employeeService,DaysOffService daysOffService){

        requestDayOffGrid.asSingleSelect().addValueChangeListener(event -> {
            requestDayOffGrid.setVisible(false);
            content.removeAll();
            content.setVisible(true);

            if (event.getValue() != null) {
                Optional<RequestDayOff> requestDayOffFromBackend = requestDayOffService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (requestDayOffFromBackend.isEmpty()) {
                    requestDayOffGrid.getDataProvider().refreshAll();
                }
                requestDayOff = requestDayOffFromBackend.get();
            } else {
                requestDayOffGrid.getDataProvider().refreshAll();
            }

            createDaysOffRequestDetailsUI(requestDayOffService,delaysService,absenceService,employeeService,daysOffService);
        });
    }

    private void createDaysOffRequestDetailsUI(RequestDayOffService requestDayOffService,DelaysService delaysService,
                                               AbsenceService absenceService,EmployeeService employeeService,DaysOffService daysOffService){
        Span name = new Span("Nom : " + requestDayOff.getEmployee().getName());
        name.setWidth("100%");
        name.getStyle().set("margin","10px auto").set("display","inline-block");

        Span dateBegin = new Span("Date : " + requestDayOff.getDateBegin().toLocalDate());
        dateBegin.setWidth("100%");
        dateBegin.getStyle().set("margin","10px auto").set("display","inline-block");

        Span duration = new Span("Durée : " + requestDayOff.getDuration()+" jours");
        duration.getStyle().set("margin","10px auto").set("width","100%").set("display","inline-block");

        Span reason = new Span("Raison : " + requestDayOff.getReason());
        reason.setWidth("100%");
        reason.getStyle().set("margin", "10px auto");

        TextArea comment = new TextArea("Commentaire");
        comment.setWidth("90%");
        comment.getStyle().set("margin","10px auto");
        try{
            comment.setValue(requestDayOff.getComment());
        }catch(Exception ignored){}

        VerticalLayout verticalLayout = new VerticalLayout(name,dateBegin,duration,reason,comment);

//            Create Button Layout
        Button accept = new Button("Accepter");
        accept.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        accept.setIcon(new Icon(VaadinIcon.CHECK));
        accept.setWidthFull();
        accept.addClickListener(event1 -> acceptButton(requestDayOffService,delaysService,absenceService,employeeService,daysOffService));

        Button deny = new Button("Refuser");
        deny.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deny.setIcon(new Icon(VaadinIcon.TRASH));
        deny.setWidthFull();
        deny.addClickListener(event1 -> denyButton(requestDayOffService) );

        Button cancel = new Button("Annuler");
        cancel.setWidthFull();
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.setIcon(new Icon(VaadinIcon.CLOSE));
        cancel.addClickListener(event1 -> cancelButton());

//            Denying access to crud operations when days off date is already passed
        if (requestDayOff.getDateBegin().toLocalDate().compareTo(LocalDate.now()) < 0 ){
            accept.setEnabled(false);
            deny.setEnabled(false);
        }

        HorizontalLayout buttonLayout = new HorizontalLayout(accept,cancel,deny);

        content.add(verticalLayout,buttonLayout);
    }

    private void acceptButton(RequestDayOffService requestDayOffService,DelaysService delaysService,
                              AbsenceService absenceService,EmployeeService employeeService,DaysOffService daysOffService){
        requestDayOff.setStatus("Accepté");

        DaysOff daysOff = new DaysOff();
        if (requestDayOff.getDateBegin().toLocalDate().compareTo(LocalDate.now()) == 0){
            daysOff.setReason(requestDayOff.getReason());
            daysOff.setDateBegin(requestDayOff.getDateBegin());
            daysOff.setDateEnd(Date.valueOf(requestDayOff.getDateBegin().toLocalDate().plusDays(requestDayOff.getDuration())));
            Employee employee = requestDayOff.getEmployee();
//            UpdateSoldeDaysOff.update(delaysService,absenceService,employeeService,employee);
            employee.setInDaysOff(true);
            employee.setDaysOffLeft(employee.getDaysOffLeft()-requestDayOff.getDuration());
            daysOff.setEmployee(employee);
            daysOffService.update(daysOff);
            employeeService.update(employee);
        }
        requestDayOffService.update(requestDayOff);
        UI.getCurrent().getPage().reload();
    }

    private void denyButton(RequestDayOffService requestDayOffService){
        requestDayOff.setStatus("Refusée");
        requestDayOffService.update(requestDayOff);
        UI.getCurrent().getPage().reload();
    }

    private void cancelButton(){
        content.setVisible(false);
        requestDayOffGrid.setVisible(true);
        requestDayOffGrid.getDataProvider().refreshAll();
    }

    private void configureZoneRouge(DepartmentService departmentService){
        //        Add zone rouge to the calendar
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
        }catch (Exception ignored){}
//        Add zone rouge picker
        DatePicker dateRedZone = new DatePicker("Date du zone rouge");
        dateRedZone.setWidth("80%");
        dateRedZone.getStyle().set("margin","10px auto");
        IntegerField durationRedZone = new IntegerField("Durée");
        try {
            dateRedZone.setValue(departmentService.getDepartmentByName(employee.getDepartment().getName()).getDateRedZone().toLocalDate());
            durationRedZone.setValue(departmentService.getDepartmentByName(employee.getDepartment().getName()).getDurationRedZone());
        }catch (Exception ignored){}
        durationRedZone.setHasControls(true);
        durationRedZone.getStyle().set("margin","10px 10%");
        HorizontalLayout horizontalLayout = new HorizontalLayout(dateRedZone,durationRedZone);
        horizontalLayout.getStyle().set("width","80%").set("margin","20px auto");
        Button save = new Button("Sauvgarder");
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.setIcon(new Icon(VaadinIcon.CHECK));
        save.setWidthFull();
        save.addClickListener(event -> {
            Department department = employee.getDepartment();
            department.setDateRedZone(Date.valueOf(dateRedZone.getValue()));
            department.setDurationRedZone(durationRedZone.getValue());
            departmentService.update(department);
            UI.getCurrent().getPage().reload();
        });
        container.add(horizontalLayout,save);
    }

}
