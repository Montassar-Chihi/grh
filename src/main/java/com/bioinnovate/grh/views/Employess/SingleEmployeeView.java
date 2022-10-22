package com.bioinnovate.grh.views.Employess;

import com.bioinnovate.grh.data.entity.*;
import com.bioinnovate.grh.data.service.*;
import com.bioinnovate.grh.data.utils.OpenPdf;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@CssImport("./styles/views/main/main-view.css")
public class SingleEmployeeView extends Div {
    private final HorizontalLayout horizontalLayoutForDelaysAndAbsences;
    private final HorizontalLayout horizontalLayoutForDaysOffAndOvertime;
    private  Span codeCnss;
    private  Span cin;
    private  Span email;
    private  Span phone;
    private  Span salary;
    private  Span daysOffLeft;
    private  Span position;
    private  Span department;
    private  Div titleContainerAbsences;
    private  H3 titleAbsences;
    private  ListDataProvider<Absences> dataProvider;
    private  Div titleContainerDelays;
    private  H3 titleDelays;
    private  Grid<Delays> delaysGrid;
    private  ListDataProvider<Delays> delaysDataProvider;
    private  Div titleContainerOvertime;
    private  H3 titleOvertime;
    private  Grid<Overtime> overtimeGrid;
    private  ListDataProvider<Overtime> overtimeDataProvider;
    private  Div titleContainerDaysOff;
    private  H3 titleDaysOff;
    private  Grid<DaysOff> daysOffGrid;
    private  ListDataProvider<DaysOff> daysOffDataProvider;
    private Button edit;
    private Button print;
    private Div personalInformation;
    private Div titleContainerPersonalInfo;
    private TextField codeCnssField;
    private TextField cinField;
    private TextField phoneField;
    private EmailField emailField;
    private TextField salaryField;
    private TextField positionField;
    private ComboBox<Department> departmentField;
    private Button save;
    private Button cancel ;
    private H3 titlePersonalInfo;
    private MemoryBuffer pictureMemoryBuffer;
    private Upload pictureMemoryUpload;
    private Button uploadButton;
    private Div dinarPrefix;
    private TextField lastNameField;
    private TextField firstNameField;
    private Image picture;
    private H1 fullName;
    private Div absences;
    private Div delays;
    private Div overtime;
    private Div daysOff;
    private Grid<Absences> absenceGrid;
    private DatePicker beginAt ;
    private DatePicker endAt;
    private TextArea reason ;
    private Button saveDaysOff ;
    private Button cancelDaysOff ;
    private Button deleteDaysOff;
    private HorizontalLayout daysOffButtonLayout;
    private HorizontalLayout overtimeButtonLayout;
    private DatePicker dateOvertime;
    private Button saveOvertime;
    private Button cancelOvertime;
    private Button deleteOvertime;
    private IntegerField durationOvertimeMin;
    private IntegerField durationOvertimeHours;
    private IntegerField durationOvertimeSec;
    private DatePicker dateDelays;
    private Button saveDelays;
    private Button cancelDelays;
    private Button deleteDelays;
    private HorizontalLayout delaysButtonLayout;
    private DatePicker dateAbsence;
    private IntegerField durationAbsence;
    private Button saveAbsence;
    private Button cancelAbsence;
    private Button deleteAbsence;
    private HorizontalLayout absenceButtonLayout;
    private IntegerField durationDelaysMin;
    private IntegerField durationDelaysHours;
    private IntegerField durationDelaysSec;
    private final Div endStylingDiv = new Div();
    private HorizontalLayout personalInformationButtonLayout;
    private final Employee theEmployee;
    private int totalSecondsOvertime;
    private int totalSeconds;
    private Span daysOffAvailable;
    private Span contract;
    private ComboBox<String> contractField;
    private VerticalLayout totalContainerAbsences;
    private VerticalLayout totalContainerOvertime;
    private VerticalLayout totalContainerDaysOff;
    private VerticalLayout totalContainerDelays;
    private TextField total;
    private TextField totalDelays;
    private Span substitute;
    private ComboBox<Employee> substituteField;
    private Button openCv;
    private Checkbox justified;

    public SingleEmployeeView(@Autowired Employee employee, @Autowired EmployeeService employeeService, @Autowired AbsenceService absenceService,
                              @Autowired DelaysService delaysService, @Autowired DaysOffService daysOffService,
                              @Autowired DepartmentService departmentService, @Autowired OvertimeService overtimeService,
                              @Autowired RequestDayOffService requestDayOffService){

        theEmployee = employee;
//        create main UI
        personalInformation = new Div();
        titleContainerPersonalInfo = new Div();
        titlePersonalInfo = new H3("Renseignements personnels");
        titleContainerPersonalInfo.add(titlePersonalInfo);

        fillPersonalInformationSpans(employee,employeeService,departmentService,requestDayOffService);
        horizontalLayoutForDelaysAndAbsences = new HorizontalLayout();
        horizontalLayoutForDelaysAndAbsences.setWidthFull();
        add(horizontalLayoutForDelaysAndAbsences);
        fillAbsencesDiv(employee,absenceService);
        fillDelaysDiv(employee,delaysService);
        horizontalLayoutForDaysOffAndOvertime = new HorizontalLayout();
        horizontalLayoutForDaysOffAndOvertime.setWidthFull();
        add(horizontalLayoutForDaysOffAndOvertime);
        fillDaysOffDiv(employee,daysOffService);
        fillOvertimeDiv(employee,overtimeService);

    }

    private void fillOvertimeDiv(Employee employee, OvertimeService overtimeService){
        overtime = new Div();
        titleContainerOvertime = new Div();
        titleOvertime = new H3("Heures supplémentaires");
        titleContainerOvertime.add(titleOvertime);
        Button addOvertime = new Button(new Icon(VaadinIcon.PLUS));
        addOvertime.getStyle().set("color","white").set("margin-left","15px");
        Overtime newOvertime = new Overtime();
        addOvertime.addClickListener(event -> {
            createEditOvertime(newOvertime,overtimeService);
            titleContainerOvertime.remove(addOvertime);
        });
        titleContainerOvertime.add(addOvertime);
        overtimeGrid = new Grid<>(Overtime.class);
        overtimeGrid.setColumns("date");
        try {
            totalSecondsOvertime = overtimeService.findTotalOvertimeByEmployee(employee.getId());
        }catch (Exception e){
            totalSeconds = 0;
        }
        overtimeGrid.addColumn(new ComponentRenderer<>(item -> {
            int s = item.getDuration();
            int sec = s % 60;
            int min = (s / 60)%60;
            int hours = (s/60)/60;
            return new Text(hours+"h : "+min+"min : "+sec+"sec");
        })).setHeader("Durée").setKey("duration");
        overtimeDataProvider = new ListDataProvider<>(overtimeService.findOvertimeByEmployee(employee.getId()));
        overtimeGrid.setDataProvider(overtimeDataProvider);
        overtimeGrid.asSingleSelect().addValueChangeListener(event -> {
            Optional<Overtime> overtime = overtimeService.get(event.getValue().getId());
            if (overtime.isPresent()){
                createEditOvertime(overtime.get(),overtimeService);
            } else {
                overtimeGrid.getDataProvider().refreshAll();
            }
        });
        overtime.add(titleContainerOvertime,overtimeGrid);
        styleOvertime();
        total = new TextField("Heures supplémentaires totales");
        total.setReadOnly(true);
        total.setValue((totalSecondsOvertime/60)/60+"h : "+(totalSecondsOvertime/60)%60+"min : "+totalSecondsOvertime%60+"sec");
        total.setWidth("90%");
        total.getStyle().set("margin","0 auto");
        totalContainerOvertime = new VerticalLayout(overtime,total);
        totalContainerOvertime.setWidth("50%");
        horizontalLayoutForDaysOffAndOvertime.add(totalContainerOvertime);
        add(endStylingDiv);
    }

    private void fillDaysOffDiv(Employee employee,DaysOffService daysOffService){
        daysOff = new Div();
        titleContainerDaysOff = new Div();
        titleDaysOff = new H3("Congés");
        titleContainerDaysOff.add(titleDaysOff);
        Button addDaysOff = new Button(new Icon(VaadinIcon.PLUS));
        addDaysOff.getStyle().set("color","white").set("margin-left","15px");
        DaysOff newDaysOff = new DaysOff();
        addDaysOff.addClickListener(event -> {
            createEditDaysOff(newDaysOff,daysOffService);
            titleContainerDaysOff.remove(addDaysOff);
        });
        titleContainerDaysOff.add(addDaysOff);
        daysOffGrid = new Grid<>(DaysOff.class,false);
        daysOffGrid.addColumn("dateBegin").setHeader("Date de début");
        daysOffGrid.addColumn("dateEnd").setHeader("Date de fin");
        daysOffGrid.addColumn("reason").setHeader("Raison");
        daysOffDataProvider = new ListDataProvider<>(daysOffService.findDaysOffByEmployee(employee.getId()));
        daysOffGrid.setDataProvider(daysOffDataProvider);
        daysOffGrid.asSingleSelect().addValueChangeListener(event -> {
            Optional<DaysOff> daysOff = daysOffService.get(event.getValue().getId());
            if (daysOff.isPresent()){
                createEditDaysOff(daysOff.get(),daysOffService);
            } else {
                daysOffGrid.getDataProvider().refreshAll();
            }
        });
        daysOff.add(titleContainerDaysOff,daysOffGrid);
        styleDaysOff();
        totalContainerDaysOff = new VerticalLayout(daysOff);
        totalContainerDaysOff.setWidth("50%");
        horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff);    }

    private void fillDelaysDiv(Employee employee,DelaysService delaysService){
        delays = new Div();
        titleContainerDelays = new Div();
        titleDelays = new H3("Retards");
        titleContainerDelays.add(titleDelays);
        Button addDelays = new Button(new Icon(VaadinIcon.PLUS));
        addDelays.getStyle().set("color","white").set("margin-left","15px");
        Delays newDelays = new Delays();
        addDelays.addClickListener(event -> {
            createEditDelays(newDelays,delaysService);
            titleContainerDelays.remove(addDelays);
        });
        titleContainerDelays.add(addDelays);
        delaysGrid = new Grid<>(Delays.class);
        delaysGrid.setColumns("date");
        try{
            totalSeconds = delaysService.findTotalDelaysByEmployeeId(employee.getId());
        }catch (Exception e){
            totalSeconds = 0;
        }
        delaysGrid.addColumn(new ComponentRenderer<>(item -> {
            int s = item.getDuration();
            int sec = s % 60;
            int min = (s / 60)%60;
            int hours = (s/60)/60;
            return new Text(hours+"h : "+min+"min : "+sec+"sec");
        })).setHeader("Durée").setKey("duration");
        delaysDataProvider = new ListDataProvider<>(delaysService.findDelaysByEmployee(employee.getId()));
        delaysGrid.setDataProvider(delaysDataProvider);
        delaysGrid.asSingleSelect().addValueChangeListener(event -> {
            Optional<Delays> delays = delaysService.get(event.getValue().getId());
            if (delays.isPresent()){
                createEditDelays(delays.get(),delaysService);
            } else {
                delaysGrid.getDataProvider().refreshAll();
            }
        });
        delays.add(titleContainerDelays,delaysGrid);
        styleDelays();
        totalDelays = new TextField("Retard totale");
        totalDelays.setValue((totalSeconds/60)/60+"h : "+(totalSeconds/60)%60+"min : "+totalSeconds%60+"sec");
        totalDelays.setReadOnly(true);
        totalDelays.setWidth("90%");
        totalDelays.getStyle().set("margin","0 auto");
        totalContainerDelays = new VerticalLayout(delays,totalDelays);
        totalContainerDelays.setWidth("50%");
        horizontalLayoutForDelaysAndAbsences.add(totalContainerDelays);
    }

    private void fillAbsencesDiv(Employee employee,AbsenceService absenceService){
        absences = new Div();
        titleContainerAbsences = new Div();
        titleAbsences = new H3("Absences");
        titleContainerAbsences.add(titleAbsences);
        Button addAbsences = new Button(new Icon(VaadinIcon.PLUS));
        addAbsences.getStyle().set("color","white").set("margin-left","15px");
        Absences newAbsence = new Absences();
        addAbsences.addClickListener(event -> {
            createEditAbsence(newAbsence,absenceService);
            titleContainerAbsences.remove(addAbsences);
        });
        titleContainerAbsences.add(addAbsences);
        absenceGrid = new Grid<>(Absences.class);
        absenceGrid.setColumns("date","duration","justified");
        absenceGrid.getColumnByKey("duration").setHeader("Durée");
        absenceGrid.getColumnByKey("justified").setHeader("Justifiée");
        dataProvider = new ListDataProvider<>(absenceService.findAbsencesByEmployee(employee.getId()));
        absenceGrid.setDataProvider(dataProvider);
        absenceGrid.asSingleSelect().addValueChangeListener(event -> {
            Optional<Absences> absences = absenceService.get(event.getValue().getId());
            if (absences.isPresent()){
                createEditAbsence(absences.get(),absenceService);
            } else {
                absenceGrid.getDataProvider().refreshAll();
            }
        });
        absences.add(titleContainerAbsences,absenceGrid);
        styleAbsences();
        totalContainerAbsences = new VerticalLayout(absences);
        totalContainerAbsences.setWidth("50%");
        horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences);
    }
    
    private void fillPersonalInformationSpans(Employee newEmployee, EmployeeService employeeService,DepartmentService departmentService,RequestDayOffService requestDayOffService){
        if (newEmployee.getPicture() == null){
            if (newEmployee.getGender()){
                picture = new Image("images/female.png", newEmployee.getFirstName()+" "+ newEmployee.getLastName()+" picture");
            }else {
                picture = new Image("images/male.png", newEmployee.getFirstName()+" "+ newEmployee.getLastName()+" picture");
            }
        }else{
            picture = new Image("images/"+newEmployee.getPicture(), newEmployee.getFirstName()+" "+ newEmployee.getLastName()+" picture");
        }
        fullName = new H1(newEmployee.getFirstName()+" "+ newEmployee.getLastName());
        codeCnss = new Span("Code Cnss : " + newEmployee.getCodeCnss());
        cin = new Span("Cin: " + (String.valueOf(newEmployee.getCin()).length() == 8 ? newEmployee.getCin() : "0"+newEmployee.getCin()));
        phone = new Span("Téléphone : " + newEmployee.getPhone());
        email = new Span("Email : " + newEmployee.getEmail());
        salary = new Span("Salaire : " + newEmployee.getSalary() + " TND");
        position = new Span("Poste : " + newEmployee.getPosition());
        department = new Span("Départment : " + newEmployee.getDepartment().getName());
        substitute = new Span();
        try {
            substitute.setText("Sub : " + newEmployee.getSubstitute().getName());
        }catch (Exception e){
            substitute.setText("Sub : Pas de Sub encore!");
        }
        double joursRestants = newEmployee.getDaysOffLeft() ;
        for (RequestDayOff r:requestDayOffService.findRequestDayOffByEmployee(newEmployee.getId())){
            if(r.getStatus().equalsIgnoreCase("Accepté")){
                joursRestants -= r.getDuration();
            }
        }
        daysOffLeft = new Span("Jours de Congés restants : " + joursRestants + " jours");
        daysOffAvailable = new Span("Jours de Congés autorisés : " + newEmployee.getDaysOffLeft() + " jours");
        contract = new Span("Type de contrat : " + newEmployee.getContract());
        edit = new Button(new Icon(VaadinIcon.EDIT));
        edit.addClickListener(event -> createEditPersonalInfo(newEmployee,employeeService,departmentService,requestDayOffService));
        print = new Button(new Icon(VaadinIcon.PRINT));
        print.addClickListener(event -> print(newEmployee));
        openCv = new Button(new Icon(VaadinIcon.EYE));
        openCv.addClickListener(event -> new OpenPdf(newEmployee.getContractPapers(), "contracts"));
        personalInformation.add(titleContainerPersonalInfo,codeCnss,cin,phone,email,salary,daysOffAvailable,daysOffLeft,position,department,substitute,contract,edit,openCv);
        stylePersonalInfoSpans();
        add(picture,fullName,personalInformation);
    }

    private void styleAbsenceFields(){
        dateAbsence.getStyle().set("width","95%").set("margin","10px 2.5%");
        durationAbsence.getStyle().set("width","95%").set("margin","10px 2.5%");
        justified.getStyle().set("width","95%").set("margin","10px 2.5%");
        saveAbsence.setIcon(new Icon(VaadinIcon.CHECK));
        saveAbsence.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveAbsence.setWidth("30%");
        cancelAbsence.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelAbsence.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelAbsence.setWidth("30%");
        deleteAbsence.setIcon(new Icon(VaadinIcon.TRASH));
        deleteAbsence.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteAbsence.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAbsence.setWidth("30%");
    }

    private void fillAbsenceFields(Absences absence){
        dateAbsence.setValue(absence.getDate().toLocalDate());
        durationAbsence.setValue(absence.getDuration());
        justified.setValue(absence.getJustified());
    }

    private void createEditAbsence(Absences absenceDetails, AbsenceService absenceService){

//        Remove spans to replace them with fields
        horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
        absences.remove(titleContainerAbsences,absenceGrid);

        dateAbsence = new DatePicker("Date");
        durationAbsence = new IntegerField("Durée");
        justified = new Checkbox("Justifiée");
        saveAbsence = new Button("Sauvgarder");
        cancelAbsence = new Button("Annuler");
        deleteAbsence = new Button("Supprimer");
        if (absenceDetails .getId() != null){
            absenceButtonLayout = new HorizontalLayout(saveAbsence, cancelAbsence, deleteAbsence);
            fillAbsenceFields(absenceDetails);
        }else {
            absenceButtonLayout = new HorizontalLayout(saveAbsence, cancelAbsence);
            saveAbsence.setWidthFull();
            cancelAbsence.setWidthFull();
        }
        absenceButtonLayout.setId("buttons-layout");

        absences.add(titleContainerAbsences,dateAbsence,durationAbsence,justified,absenceButtonLayout);
        horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);

//        Style the fields
        styleAbsenceFields();

//        Manage the buttons
        saveAbsence.addClickListener(event -> {
//            Save the update
            saveAbsence(absenceDetails,absenceService);
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
            absences.remove(titleContainerAbsences,dateAbsence,durationAbsence,justified,absenceButtonLayout);
//            Fill the days off grid
            fillAbsencesDiv(theEmployee,absenceService);
//            Add the layouts again after updating
            horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);
        });

        deleteAbsence.addClickListener(event -> {
//            Save the update
            absenceService.deleteAbsence(absenceDetails.getId());
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
            absences.remove(titleContainerAbsences,dateAbsence,durationAbsence,justified,absenceButtonLayout);
//            Fill the days off grid
            fillAbsencesDiv(theEmployee,absenceService);
//            Add the layouts again after updating
            horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);
        });

        cancelAbsence.addClickListener(event -> {
//            remove fields from main div to replace it with the spans
            horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
            absences.remove(titleContainerAbsences,dateAbsence,durationAbsence,justified,absenceButtonLayout);
//            Fill the days off grid
            fillAbsencesDiv(theEmployee,absenceService);
//            Add the layouts again after updating
            horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);
        });
    }

    private void saveAbsence(Absences absenceDetails, AbsenceService absenceService) {
        absenceDetails.setDate(Date.valueOf(dateAbsence.getValue()));
        absenceDetails.setDuration(durationAbsence.getValue());
        absenceDetails.setJustified(justified.getValue());
        absenceDetails.setEmployee(theEmployee);
        absenceService.update(absenceDetails);
    }

    private void styleDelaysFields(){
        dateDelays.getStyle().set("width","95%").set("margin","10px 2.5%");
        durationDelaysMin.getStyle().set("width","30%").set("margin","10px 0.8%");
        durationDelaysHours.getStyle().set("width","30%").set("margin","10px 0.8% 10px 3.2%");
        durationDelaysSec.getStyle().set("width","30%").set("margin","10px 0.8%").set("margin-right","3.2%");
        saveDelays.setIcon(new Icon(VaadinIcon.CHECK));
        saveDelays.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveDelays.setWidth("30%");
        cancelDelays.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelDelays.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelDelays.setWidth("30%");
        deleteDelays.setIcon(new Icon(VaadinIcon.TRASH));
        deleteDelays.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteDelays.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteDelays.setWidth("30%");
    }

    private void fillDelaysFields(Delays delays){
        dateDelays.setValue(delays.getDate().toLocalDate());
        durationDelaysMin.setValue((delays.getDuration()/ 60)%60);
        durationDelaysHours.setValue((delays.getDuration()/60)/60);
        durationDelaysSec.setValue(delays.getDuration() % 60);
    }

    private void createEditDelays(Delays delaysDetails, DelaysService delaysService){

//        Remove spans to replace them with fields
        horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
        delays.remove(titleContainerDelays,delaysGrid);

        dateDelays = new DatePicker("Date");

        durationDelaysMin = new IntegerField();
        durationDelaysMin.setMin(0);
        durationDelaysMin.setMax(59);
        durationDelaysMin.setPlaceholder("Min");
        durationDelaysMin.setHasControls(true);
        durationDelaysHours = new IntegerField("Durée");
        durationDelaysHours.setMin(0);
        durationDelaysHours.setMax(24);
        durationDelaysHours.setPlaceholder("Heures");
        durationDelaysHours.setHasControls(true);
        durationDelaysSec = new IntegerField();
        durationDelaysSec.setMin(0);
        durationDelaysSec.setMax(59);
        durationDelaysSec.setPlaceholder("Secondes");
        durationDelaysSec.setHasControls(true);

        saveDelays = new Button("Sauvgarder");
        cancelDelays = new Button("Annuler");
        deleteDelays = new Button("Supprimer");
        if (delaysDetails.getId() != null){
            delaysButtonLayout = new HorizontalLayout(saveDelays, cancelDelays, deleteDelays);
        }else {
            delaysButtonLayout = new HorizontalLayout(saveDelays, cancelDelays);
            saveDelays.setWidthFull();
            cancelDelays.setWidthFull();
        }
        delaysButtonLayout.setId("buttons-layout");

        if (delaysDetails.getId() != null){
            fillDelaysFields(delaysDetails);
        }

        delays.add(titleContainerDelays,dateDelays,durationDelaysHours,durationDelaysMin,durationDelaysSec,delaysButtonLayout);
        horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);

//        Style the fields
        styleDelaysFields();

//        Manage the buttons
        saveDelays.addClickListener(event -> {
//            Save the update
            saveDelays(delaysDetails,delaysService);
            try {
                totalSeconds = delaysService.findTotalDelaysByEmployeeId(theEmployee.getId());
            }catch (Exception e){
                totalSeconds = 0;
            }
            totalDelays.setValue((totalSeconds/60)/60+"h : "+(totalSeconds/60)%60+"min : "+totalSeconds%60+"sec");
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
            delays.remove(titleContainerDelays,dateDelays,durationDelaysHours,durationDelaysMin,durationDelaysSec,delaysButtonLayout);
//            Fill the days off grid
            fillDelaysDiv(theEmployee,delaysService);
//            Add the layouts again after updating
            horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);
        });

        deleteDelays.addClickListener(event -> {
//            Save the update
            delaysService.deleteDelays(delaysDetails.getId());
            try {
                totalSeconds = delaysService.findTotalDelaysByEmployeeId(theEmployee.getId());
            }catch (Exception e){
                totalSeconds = 0;
            }
            totalDelays.setValue((totalSeconds/60)/60+"h : "+(totalSeconds/60)%60+"min : "+totalSeconds%60+"sec");
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
            delays.remove(titleContainerDelays,dateDelays,durationDelaysHours,durationDelaysMin,durationDelaysSec,delaysButtonLayout);
//            Fill the days off grid
            fillDelaysDiv(theEmployee,delaysService);
//            Add the layouts again after updating
            horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);
        });

        cancelDelays.addClickListener(event -> {
//            remove fields from main div to replace it with the spans
            horizontalLayoutForDelaysAndAbsences.remove(totalContainerAbsences,totalContainerDelays);
            delays.remove(titleContainerDelays,dateDelays,durationDelaysHours,durationDelaysMin,durationDelaysSec,delaysButtonLayout);
//            Fill the days off grid
            fillDelaysDiv(theEmployee,delaysService);
//            Add the layouts again after updating
            horizontalLayoutForDelaysAndAbsences.add(totalContainerAbsences,totalContainerDelays);
        });
    }

    private void saveDelays(Delays delaysDetails, DelaysService delaysService) {
        delaysDetails.setDate(Date.valueOf(dateDelays.getValue()));
        delaysDetails.setDuration(durationDelaysHours.getValue()*3600+durationDelaysMin.getValue()*60+durationDelaysSec.getValue());
        delaysDetails.setEmployee(theEmployee);
        delaysService.update(delaysDetails);
    }

    private void styleOvertimeFields(){
        dateOvertime.getStyle().set("width","95%").set("margin","10px 2.5%");
        durationOvertimeMin.getStyle().set("width","30%").set("margin","10px 0.8%");
        durationOvertimeHours.getStyle().set("width","30%").set("margin","10px 0.8% 10px 3.2%");
        durationOvertimeSec.getStyle().set("width","30%").set("margin","10px 0.8%").set("margin-right","3.2%");
        saveOvertime.setIcon(new Icon(VaadinIcon.CHECK));
        saveOvertime.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveOvertime.setWidth("30%");
        cancelOvertime.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelOvertime.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelOvertime.setWidth("30%");
        deleteOvertime.setIcon(new Icon(VaadinIcon.TRASH));
        deleteOvertime.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteOvertime.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteOvertime.setWidth("30%");
    }

    private void fillOvertimeFields(Overtime overtime){
        dateOvertime.setValue(overtime.getDate().toLocalDate());
        durationOvertimeMin.setValue((overtime.getDuration()/ 60)%60);
        durationOvertimeHours.setValue((overtime.getDuration()/60)/60);
        durationOvertimeSec.setValue(overtime.getDuration() % 60);
    }

    private void createEditOvertime(Overtime overtimeDetails, OvertimeService overtimeService){

//        Remove spans to replace them with fields
        horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
        overtime.remove(titleContainerOvertime,overtimeGrid);

        dateOvertime = new DatePicker("Date");

        durationOvertimeMin = new IntegerField();
        durationOvertimeMin.setMin(0);
        durationOvertimeMin.setMax(59);
        durationOvertimeMin.setPlaceholder("Min");
        durationOvertimeMin.setHasControls(true);
        durationOvertimeHours = new IntegerField("Durée");
        durationOvertimeHours.setMin(0);
        durationOvertimeHours.setMax(24);
        durationOvertimeHours.setPlaceholder("Heures");
        durationOvertimeHours.setHasControls(true);
        durationOvertimeSec = new IntegerField();
        durationOvertimeSec.setMin(0);
        durationOvertimeSec.setMax(59);
        durationOvertimeSec.setPlaceholder("Secondes");
        durationOvertimeSec.setHasControls(true);

        saveOvertime = new Button("Sauvgarder");
        cancelOvertime = new Button("Annuler");
        deleteOvertime = new Button("Supprimer");
        if (overtimeDetails .getId() != null){
            overtimeButtonLayout = new HorizontalLayout(saveOvertime, cancelOvertime, deleteOvertime);
            fillOvertimeFields(overtimeDetails);
        }else {
            overtimeButtonLayout = new HorizontalLayout(saveOvertime, cancelOvertime);
            cancelOvertime.setWidthFull();
            saveOvertime.setWidthFull();
        }
        overtimeButtonLayout.setId("buttons-layout");

        overtime.add(titleContainerOvertime,dateOvertime,durationOvertimeHours,durationOvertimeMin,durationOvertimeSec,overtimeButtonLayout);
        horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);

//        Style the fields
        styleOvertimeFields();

//        Manage the buttons
        saveOvertime.addClickListener(event -> {
//            Save the update
            saveOvertime(overtimeDetails,overtimeService);
            try {
                totalSecondsOvertime = overtimeService.findTotalOvertimeByEmployee(theEmployee.getId());
            }catch (Exception e){
                totalSecondsOvertime = 0;
            }
            total.setValue((totalSecondsOvertime/60)/60+"h : "+(totalSecondsOvertime/60)%60+"min : "+totalSecondsOvertime%60+"sec");
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
            overtime.remove(titleContainerOvertime,dateOvertime,durationOvertimeHours,durationOvertimeMin,durationOvertimeSec,overtimeButtonLayout);
//            Fill the days off grid
            fillOvertimeDiv(theEmployee,overtimeService);
//            Add the layouts again after updating
            horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);
        });

        deleteOvertime.addClickListener(event -> {
//            Save the update
            overtimeService.deleteOvertime(overtimeDetails.getId());
            try {
                totalSecondsOvertime = overtimeService.findTotalOvertimeByEmployee(theEmployee.getId());
            }catch (Exception e){
                totalSecondsOvertime = 0;
            }
            total.setValue((totalSecondsOvertime/60)/60+"h : "+(totalSecondsOvertime/60)%60+"min : "+totalSecondsOvertime%60+"sec");
//          remove fields from main div to replace it with the grid
            horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
            overtime.remove(titleContainerOvertime,dateOvertime,durationOvertimeHours,durationOvertimeMin,durationOvertimeSec,overtimeButtonLayout);
//            Fill the days off grid
            fillOvertimeDiv(theEmployee,overtimeService);
//            Add the layouts again after updating
            horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);
        });

        cancelOvertime.addClickListener(event -> {
//            remove fields from main div to replace it with the spans
            horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
            overtime.remove(titleContainerOvertime,dateOvertime,durationOvertimeHours,durationOvertimeMin,durationOvertimeSec,overtimeButtonLayout);
//            Fill the days off grid
            fillOvertimeDiv(theEmployee,overtimeService);
//            Add the layouts again after updating
            horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);
        });
    }

    private void saveOvertime(Overtime overtimeDetails, OvertimeService overtimeService) {
        overtimeDetails.setDate(Date.valueOf(dateOvertime.getValue()));
        overtimeDetails.setDuration(durationOvertimeHours.getValue()*3600+durationOvertimeMin.getValue()*60+durationOvertimeSec.getValue());
        overtimeDetails.setEmployee(theEmployee);
        overtimeService.update(overtimeDetails);
    }

    private void styleDaysOffFields(){
        beginAt.getStyle().set("width","95%").set("margin","10px 2.5%");
        endAt.getStyle().set("width","95%").set("margin","10px 2.5%");
        reason.getStyle().set("width","95%").set("margin","10px 2.5%");
        saveDaysOff.setIcon(new Icon(VaadinIcon.CHECK));
        saveDaysOff.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveDaysOff.setWidth("30%");
        cancelDaysOff.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelDaysOff.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelDaysOff.setWidth("30%");
        deleteDaysOff.setIcon(new Icon(VaadinIcon.TRASH));
        deleteDaysOff.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        deleteDaysOff.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteDaysOff.setWidth("30%");
    }

    private void fillDaysOffFields(DaysOff daysOff){
        beginAt.setValue(daysOff.getDateBegin().toLocalDate());
        endAt.setValue(daysOff.getDateBegin().toLocalDate());
        reason.setValue(daysOff.getReason());
    }

    private void createEditDaysOff(DaysOff daysOffDetails, DaysOffService daysOffService){

//        Remove spans to replace them with fields
        horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
        daysOff.remove(titleContainerDaysOff,daysOffGrid);

        beginAt = new DatePicker("Date de début");
        endAt = new DatePicker("Date de fin");
        reason = new TextArea("Raison");
        saveDaysOff = new Button("Sauvgarder");
        cancelDaysOff = new Button("Annuler");
        deleteDaysOff = new Button("Supprimer");
        if (daysOffDetails .getId() != null){
            daysOffButtonLayout = new HorizontalLayout(saveDaysOff, cancelDaysOff, deleteDaysOff);
        }else{
            daysOffButtonLayout = new HorizontalLayout(saveDaysOff, cancelDaysOff);
            cancelDaysOff.setWidthFull();
            saveDaysOff.setWidthFull();
        }
        daysOffButtonLayout.setId("buttons-layout");

        if (daysOffDetails .getId() != null){
            fillDaysOffFields(daysOffDetails);
        }

        daysOff.add(titleContainerDaysOff,beginAt,endAt,reason,daysOffButtonLayout);
        horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);

//        Style the fields
        styleDaysOffFields();

//        Manage the buttons
        saveDaysOff.addClickListener(event -> {
//            Save the update
            saveDaysOff(daysOffDetails,daysOffService);
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
            daysOff.remove(titleContainerDaysOff,beginAt,endAt,reason,daysOffButtonLayout);
//            Fill the days off grid
            fillDaysOffDiv(theEmployee,daysOffService);
//            Add the layouts again after updating
            horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);
        });

        deleteDaysOff.addClickListener(event -> {
//            Save the update
            daysOffService.deleteDaysOff(daysOffDetails.getId());
//            remove fields from main div to replace it with the grid
            horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
            daysOff.remove(titleContainerDaysOff,beginAt,endAt,reason,daysOffButtonLayout);
//            Fill the days off grid
            fillDaysOffDiv(theEmployee,daysOffService);
//            Add the layouts again after updating
            horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);
        });

        cancelDaysOff.addClickListener(event -> {
//            remove fields from main div to replace it with the spans
            horizontalLayoutForDaysOffAndOvertime.remove(totalContainerDaysOff,totalContainerOvertime);
            daysOff.remove(titleContainerDaysOff,beginAt,endAt,reason,daysOffButtonLayout);
//            Fill the days off grid
            fillDaysOffDiv(theEmployee,daysOffService);
//            Add the layouts again after updating
            horizontalLayoutForDaysOffAndOvertime.add(totalContainerDaysOff,totalContainerOvertime);
        });
    }

    private void saveDaysOff(DaysOff daysOffDetails, DaysOffService daysOffService) {
        daysOffDetails.setDateBegin(Date.valueOf(beginAt.getValue()));
        daysOffDetails.setDateBegin(Date.valueOf(endAt.getValue()));
        daysOffDetails.setReason(reason.getValue());
        daysOffDetails.setEmployee(theEmployee);
        daysOffService.update(daysOffDetails);
    }

    private void createEditPersonalInfo(Employee employee, EmployeeService employeeService,DepartmentService departmentService,RequestDayOffService requestDayOffService){

//        Remove spans to replace them with fields
        remove(picture,fullName,personalInformation,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        personalInformation.remove(titleContainerPersonalInfo,codeCnss,cin,phone,email,salary,daysOffAvailable,daysOffLeft,position,department,substitute,contract,edit,openCv);

//        Create fields
        personalInformation = new Div();
        titleContainerPersonalInfo = new Div();
        titlePersonalInfo = new H3("Renseignements personnels");
        pictureMemoryBuffer = new MemoryBuffer();
        pictureMemoryUpload = new Upload(pictureMemoryBuffer);
        uploadButton = new Button("Importer L'image");
        firstNameField = new TextField("Prénom");
        lastNameField = new TextField("Nom de la famille");
        codeCnssField = new TextField("Code Cnss");
        cinField = new TextField("Cin");
        phoneField = new TextField("Téléphone");
        emailField = new EmailField("Email");
        salaryField = new TextField("Salaire");
        dinarPrefix = new Div();
        dinarPrefix.setText("TND");
        salaryField.setPrefixComponent(dinarPrefix);
        positionField = new TextField("Poste");
        substituteField = new ComboBox<>("Sub");
        substituteField.setItems(employeeService.findSubs(employee.getEmail(),employee.getDepartment()));
        substituteField.setItemLabelGenerator(Employee::getName);
        departmentField = new ComboBox<>("Départment");
        departmentField.setItems(departmentService.findAll());
        departmentField.setItemLabelGenerator(Department::getName);
        contractField = new ComboBox<>("Type de contrat");
        contractField.setItems("CDI","CDD","SIVP","Stagiare");
        save = new Button(new Icon(VaadinIcon.CHECK));
        save.setText("Sauvgarder");
        cancel = new Button(new Icon(VaadinIcon.CLOSE));
        cancel.setText("Annuler");
        personalInformationButtonLayout = new HorizontalLayout(save,cancel);
        personalInformationButtonLayout.setId("buttons-layout");

//        Fill the fields with the employee data
        fillFields(employee);
//        Add the components to their respective layouts
        titleContainerPersonalInfo.add(titlePersonalInfo);
        personalInformation.add(titleContainerPersonalInfo, pictureMemoryUpload, firstNameField, lastNameField, codeCnssField, cinField,
                phoneField, emailField, salaryField, positionField, substituteField,personalInformationButtonLayout);
        add(picture,fullName,personalInformation,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        //        Style the fields
        styleFields();

//        Manage the buttons
        save.addClickListener(event -> {
//            Save the update
            save(employee,employeeService);
//            remove fields from main div to replace it with the spans
            remove(picture,fullName,personalInformation,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
            personalInformation.remove(titleContainerPersonalInfo, pictureMemoryUpload, firstNameField, lastNameField, codeCnssField, cinField,
                    phoneField, emailField, salaryField, positionField, substituteField,personalInformationButtonLayout);
//            Fill the personal info spans
            fillPersonalInformationSpans(employee,employeeService,departmentService,requestDayOffService);
            add(horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        });

        cancel.addClickListener(event -> {
//            remove fields from main div to replace it with the spans
            remove(picture,fullName,personalInformation,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
            personalInformation.remove(titleContainerPersonalInfo, pictureMemoryUpload, firstNameField, lastNameField, codeCnssField, cinField,
                    phoneField, emailField, salaryField, positionField, substituteField,personalInformationButtonLayout);
//            Fill the personal info spans
            fillPersonalInformationSpans(employee,employeeService,departmentService,requestDayOffService);
            add(horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        });

    }
    private void save(Employee employee,EmployeeService employeeService){
        employee.setFirstName(firstNameField.getValue());
        employee.setLastName(lastNameField.getValue());
        employee.setCin(Integer.parseInt(cinField.getValue()));
        employee.setCodeCnss(Integer.parseInt(codeCnssField.getValue()));
        employee.setEmail(emailField.getValue());
        employee.setSalary(Double.parseDouble(salaryField.getValue()));
        employee.setPosition(positionField.getValue());
        employee.setPhone(Integer.parseInt(phoneField.getValue()));
        employee.setDepartment(departmentField.getValue());
        employee.setContract(contractField.getValue());
        employee.setSubstitute(substituteField.getValue());
        String fileName = pictureMemoryBuffer.getFileName();
        if (!fileName.equals("")) {
            try {
                employee.setPicture(employee.getId() + fileName);
                InputStream fileData = pictureMemoryBuffer.getInputStream();
                byte[] array = fileData.readAllBytes();
                FileOutputStream output = new FileOutputStream("src\\main\\resources\\META-INF\\resources\\images\\" + employee.getPicture());
                output.write(array);
                output.close();
            }catch(IOException error){
                Notification.show("Impossible de télécharger l'image correctement");
                error.printStackTrace();
            }
        }
        employeeService.update(employee);

    }

    private void fillFields(Employee employee){
        firstNameField.setValue(employee.getFirstName());
        lastNameField.setValue(employee.getLastName());
        codeCnssField.setValue(String.valueOf(employee.getCodeCnss()));
        cinField.setValue(String.valueOf(employee.getCin()));
        phoneField.setValue(String.valueOf(employee.getPhone()));
        emailField.setValue(employee.getEmail());
        salaryField.setValue(String.valueOf(employee.getSalary()));
        positionField.setValue(employee.getPosition());
        departmentField.setValue(employee.getDepartment());
        contractField.setValue(employee.getContract());
        substituteField.setValue(employee.getSubstitute());
    }

    private void styleDaysOff(){
        daysOff.setWidth("90%");
        daysOff.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px").set("margin-bottom","0");
        titleDaysOff.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
        titleContainerDaysOff.getStyle().set("border"," 1px solid").set(
                "background","var(--theme-color)").set(
                "width"," 80%").set(
                "height"," fit-content").set(
                "margin","-50px auto 20px").set(
                "border-radius"," 30px").set(
                "text-align"," center").set("padding","20px");
    }

    private void styleAbsences(){
        absences.setWidth("90%");
        absences.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("display","inline-block").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px");
        titleContainerAbsences.getStyle().set("border"," 1px solid").set(
                "background","var(--theme-color)").set(
                "width"," 80%").set(
                "height"," fit-content").set(
                "margin","-50px auto 20px").set(
                "border-radius"," 30px").set(
                "text-align"," center").set("padding","20px");
        titleAbsences.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
    }

    private  void styleDelays(){
        delays.setWidth("90%");
        delays.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("display","inline-block").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px").set("margin-bottom","0");
        titleContainerDelays.getStyle().set("border"," 1px solid").set(
                "background","var(--theme-color)").set(
                "width"," 80%").set(
                "height"," fit-content").set(
                "margin","-50px auto 20px").set(
                "border-radius"," 30px").set(
                "text-align"," center").set("padding","20px");
        titleDelays.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
    }

    private  void styleOvertime(){
        overtime.setWidth("90%");
        overtime.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("display","inline-block").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px").set("margin-bottom","0");
        titleContainerOvertime.getStyle().set("border"," 1px solid").set(
                "background","var(--theme-color)").set(
                "width"," 80%").set(
                "height"," fit-content").set(
                "margin","-50px auto 20px").set(
                "border-radius"," 30px").set(
                "text-align"," center").set("padding","20px");
        titleOvertime.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
        endStylingDiv.setMinHeight("50px");
        endStylingDiv.setWidthFull();
    }

    private void stylePersonalInfoSpans(){
        personalInformation.setWidth("80%");
        personalInformation.getStyle().set("font-family","cursive").set("min-height","20.625rem").set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("margin","100px auto").set("margin-bottom","20px").set("border","1px solid black").set("padding","10px").set("border-radius","20px");
        titleContainerPersonalInfo.getStyle().set("border"," 1px solid").set(
                "background","var(--theme-color)").set(
                "width"," 80%").set(
                "height"," fit-content").set(
                "margin","-50px auto 20px").set(
                "border-radius"," 30px").set(
                "text-align"," center").set("padding","20px");
        titlePersonalInfo.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
        fullName.getStyle().set("text-align","center");
        picture.setMaxWidth("200px");
        picture.getStyle().set("box-shadow","rgb(108 104 104) 4px 4px 15px 0px").set("border-radius","100px").set("text-align","center").set("display","block" ).set("margin","25px auto");
        codeCnss.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        cin.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        phone.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        email.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        salary.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        daysOffAvailable.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        daysOffLeft.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        position.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        department.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        substitute.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        contract.getStyle().set("margin-left","20px").set("width","40%").set("display","block").set("margin-bottom","15px");
        edit.setText("Modifier les informations personnels");
        edit.getStyle().set("float","right").set("margin","20px 10px");
        print.setText("Imprimer la fiche de paie");
        print.getStyle().set("float","left").set("margin","20px 10px");
        openCv.setText("Voir le contrat");
        openCv.getStyle().set("float","left").set("margin","20px 10px");
    }

    private void styleFields() {
        // Style box
        personalInformation.setWidth("80%");
        personalInformation.getStyle().set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("margin","100px auto").set("margin-bottom","10px").set("border","1px solid black").set("padding","10px").set("border-radius","20px");
        titleContainerPersonalInfo.getStyle().set("border","1px solid").set(
                "background","var(--theme-color)").set(
                "width"," 80%").set(
                "height"," fit-content").set(
                "margin","-50px auto 20px").set(
                "border-radius"," 30px").set(
                "text-align"," center").set("padding","20px");
        titlePersonalInfo.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
//        Style form
        //        Style Image uploader
        uploadButton.setIcon(new Icon(VaadinIcon.FILE_ADD));
        uploadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Span dropLabel = new Span("Drag and Drop File HERE!");
        Span dropIcon = new Span("");
        pictureMemoryUpload.setUploadButton(uploadButton);
        pictureMemoryUpload.setDropLabel(dropLabel);
        pictureMemoryUpload.setDropLabelIcon(dropIcon);
        pictureMemoryUpload.setWidth("30%");
        pictureMemoryUpload.getStyle().set("margin","0 auto");
//        Style rest of the form
        firstNameField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        lastNameField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        codeCnssField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        cinField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        phoneField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        emailField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        dinarPrefix.setText("TND");
        salaryField.setPrefixComponent(dinarPrefix);
        salaryField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        positionField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        departmentField.getStyle().set("max-height","55px").set("margin","5% 2%").set("display","inline-block").set("width","45%");
        substituteField.getStyle().set("max-height","55px").set("margin","5% 2%").set("display","inline-block").set("width","45%").set("position","relative").set("top","0.3rem");
        contractField.getStyle().set("max-height","55px").set("margin","5% 2%").set("display","inline-block").set("width","45%");

//        Style buttons
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.getStyle().set("margin-left","10%").set("float","left").set("margin-bottom"," 20px");
        save.setWidthFull();
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.getStyle().set("margin-right","10%").set("float","right").set("margin-left"," 30%").set("margin-bottom"," 20px");
        cancel.setWidthFull();
    }

    private void print(Employee employee){
        String script = "var WinPrint = window.open('', '', 'width=900,height=650');" +
                "WinPrint.document.write(\"<style>    .title{        align-content: center;    }    div{        border: 1px solid black;        padding: 0;    }    .bold {        font-weight: bold;    }    .capital{        text-transform : uppercase;    }    .center{        text-align: center;    }    .tableColumn{        display: inline-block;        position: relative;        top: 0px;        margin: -2px;    }    #salaryElement{        width: 40.3%;    }    #daysNumber{        width: 19%;    }    #renum{        width: 20%;    }    #retenues{        width: 20%;    }    p {        margin-left: 5px;    }    .whiteSpace{        color: transparent;    }    #personalInfo p {        font-size: 12px;    }    #headerTitle{        font-size: 25px;    }</style><div id='container'>    <div id='header'>        <H1 id='headerTitle' style='margin-left: 3px;'>International Bio Service</H1>        <p>3 Impasse Abdelaziz EL SAOUD - 2092 - EL Manar</p>        <p>CNSS : 206203-78</p>    </div>    <div class='title'>        <h1 class='center'>Bulletin de paie</h1>    </div>    <div id='personalInfo'>        <p class='capital'>Cin : "+employee.getCin()+"</p>        <p class='capital'>Nom Et Prénom : "+employee.getName()+"</p>        <p class='capital'>Qualification : "+employee.getPosition()+"</p>        <p class='capital'>Immat CNSS : "+employee.getCodeCnss()+"</p>        <p class='capital'>SIT FAM : </p>        <p class='capital'>ENF : </p>        <p class='capital'>Mois : "+ LocalDate.now().getMonth().name()+" "+LocalDate.now().getYear()+"</p>    </div>    <div id='tableOfPay'>        <div class='tableColumn' id='salaryElement'>            <div class='title'>                <h5 class='capital bold center'>éléments de salaire</h5>            </div>            <div>               <p>salaire de base</p>               <p>Prime de présence</p>               <p>prime de transport</p>               <p class='whiteSpace'>white space</p>               <p class='whiteSpace'>white space</p>               <p class='center capital'>salaire brut</p>               <p>cotisations CNSS</p>               <p class='whiteSpace'>white space</p>               <p class='center capital'>salaire imposable</p>               <p>IRPP</p>               <p>CSS</p>            </div>        </div>        <div class='tableColumn' id='daysNumber'>            <div class='title'>                <h5 class='capital bold center'>Nombre de jours</h5>            </div>            <div>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>            </div>        </div>        <div class='tableColumn' id='renum'>            <div class='title'>                <h5 class='capital bold center'>rémunérations</h5>            </div>            <div>                <p>1090.173</p>                <p>11.643</p>                <p>61.808</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p>1163.624</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p>1056.803</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>            </div>        </div>        <div class='tableColumn' id='retenues'>            <div class='title'>                <h5 class='capital bold center'>retenues</h5>            </div>            <div>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p>106.821</p>                <p class='whiteSpace'>white space</p>                <p class='whiteSpace'>white space</p>                <p>147.292</p>                <p>9.511</p>            </div>        </div>    </div>    <div style='width: 100%;border: none;'>        <p class='bold capital' style='display: inline-block;'> Net a payer</p>        <p style='float:right; display: inline-block; margin-right: 7px;'>"+employee.getSalary()+"</p>    </div></div>\");" +
                "WinPrint.document.close();" +
                "WinPrint.focus();" +
                "WinPrint.print();";
        getElement().executeJs(script);
    }
}
