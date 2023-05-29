package com.bioinnovate.grh.views.EmployeeProfile;

import com.bioinnovate.grh.data.entity.*;
import com.bioinnovate.grh.data.service.*;
import com.bioinnovate.grh.data.utils.*;
import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Route(value = "profile", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Profile")
@CssImport("./styles/views/main/main-view.css")
@Secured({"ADMIN","USER","SUPER USER"})
public class EmployeeProfileView extends Div {
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
    private  Div titleContainerDelays;
    private  H3 titleDelays;
    private  Div titleContainerOvertime;
    private  H3 titleOvertime;
    private  Div titleContainerDaysOff;
    private  H3 titleDaysOff;
    private Button edit;
    private Div personalInformation;
    private Div titleContainerPersonalInfo;
    private TextField codeCnssField;
    private TextField cinField;
    private TextField phoneField;
    private EmailField emailField;
    private Button save;
    private Button cancel ;
    private H3 titlePersonalInfo;
    private MemoryBuffer pictureMemoryBuffer;
    private Upload pictureMemoryUpload;
    private Button uploadButton;
    private TextField lastNameField;
    private TextField firstNameField;
    private Image picture;
    private H1 fullName;
    private Div absences;
    private Div delays;
    private Div overtime;
    private Div daysOff;
    private final Div endStylingDiv = new Div();
    private HorizontalLayout personalInformationButtonLayout;
    private Employee employee;
    private VaadinSession session = VaadinSession.getCurrent();
    private PasswordField password;
    private PasswordField confirmPassword;
    private int totalSeconds;
    private int totalSecondsOvertime;
    private Span daysOffAvailable;
    private Span contract;
    private ComboBox<String> contractField;
    private Button reclamer;
    private Button openCv;
    private HorizontalLayout personalInfoMainLayout;

    public EmployeeProfileView(@Autowired EmployeeService employeeService, @Autowired AbsenceService absenceService,
                               @Autowired DelaysService delaysService, @Autowired DaysOffService daysOffService,
                               @Autowired DepartmentService departmentService,@Autowired OvertimeService overtimeService,
                               @Autowired UserService userService,@Autowired RequestDayOffService requestDayOffService,
                               @Autowired ReclamationService reclamationService){

        employee = employeeService.findEmployeeByEmail(session.getAttribute("username").toString());
        if(LocalDate.now().getDayOfMonth() == 24){
//            UpdateSoldeDaysOff.update(delaysService,absenceService,employeeService,employee);
        }
        //        create main UI
        personalInformation = new Div();
        titleContainerPersonalInfo = new Div();
        titlePersonalInfo = new H3("Renseignements personnels");
        titleContainerPersonalInfo.add(titlePersonalInfo);

        fillPersonalInformationSpans(employee, reclamationService, employeeService,departmentService,userService,requestDayOffService);
        horizontalLayoutForDelaysAndAbsences = new HorizontalLayout();
        horizontalLayoutForDelaysAndAbsences.setWidthFull();
        add(horizontalLayoutForDelaysAndAbsences);
        fillAbsencesDiv(employee,absenceService);
        fillDelaysDiv(employee,delaysService);
        horizontalLayoutForDaysOffAndOvertime = new HorizontalLayout();
        horizontalLayoutForDaysOffAndOvertime.setWidthFull();
        add(horizontalLayoutForDaysOffAndOvertime);
        fillDaysOffDiv(employee,daysOffService,requestDayOffService);
        fillOvertimeDiv(employee,overtimeService);

    }

    private void fillOvertimeDiv(Employee employee, OvertimeService overtimeService){
        overtime = new Div();
        titleContainerOvertime = new Div();
        titleOvertime = new H3("Heures supplémentaires");
        titleContainerOvertime.add(titleOvertime);
        Grid<Overtime> overtimeGrid = new Grid<>(Overtime.class);
        overtimeGrid.setColumns("date");
        totalSecondsOvertime = 0;
        List<Overtime> overtimes = employee.getOvertime();
        for (Overtime overtime : overtimes){
            if(overtime.getDate().getMonth() == LocalDate.now().getMonthValue()-1){
                totalSecondsOvertime += overtime.getDuration();
            }
        }
        overtimeGrid.addColumn(new ComponentRenderer<>(item -> {
            int s = item.getDuration();
            int sec = s % 60;
            int min = (s / 60)%60;
            int hours = (s/60)/60;
            return new Text(hours+"h : "+min+"min : "+sec+"sec");
        })).setHeader("Durée").setKey("duration");
        ListDataProvider<Overtime> overtimeDataProvider = new ListDataProvider<>(employee.getOvertime());
        overtimeGrid.setDataProvider(overtimeDataProvider);
        overtime.add(titleContainerOvertime, overtimeGrid);
        styleOvertime();
        TextField total = new TextField("Heures supplémentaires totales");
        total.setReadOnly(true);
        total.setValue((totalSecondsOvertime/60)/60+"h : "+(totalSecondsOvertime/60)%60+"min : "+totalSecondsOvertime%60+"sec");
        total.setWidth("90%");
        total.getStyle().set("margin","0 auto");
        VerticalLayout totalContainer = new VerticalLayout(overtime,total);
        totalContainer.setWidth("50%");
        horizontalLayoutForDaysOffAndOvertime.add(totalContainer);
        add(endStylingDiv);
    }

    private void fillDaysOffDiv(Employee employee,DaysOffService daysOffService,RequestDayOffService requestDayOffService){
        daysOff = new Div();
        titleContainerDaysOff = new Div();
        titleDaysOff = new H3("Congés");
        titleContainerDaysOff.add(titleDaysOff);
        Grid<DaysOff> daysOffGrid = new Grid<>(DaysOff.class, false);
        daysOffGrid.addColumn("dateBegin").setHeader("Date de debut");
        daysOffGrid.addColumn("dateEnd").setHeader("Date de fin");
        daysOffGrid.addColumn("reason").setHeader("Raison");
        ListDataProvider<DaysOff> daysOffDataProvider = new ListDataProvider<>(employee.getDaysOff());
        daysOffGrid.setDataProvider(daysOffDataProvider);
        daysOff.add(titleContainerDaysOff, daysOffGrid);
        styleDaysOff();
        Button requestTimeOff = new Button("Demander Un congé");
        requestTimeOff.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        requestTimeOff.setWidth("40%");
        requestTimeOff.getStyle().set("margin","10px auto");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            requestTimeOff.getStyle().set("background","var(--nadia-color)");
        }
        requestTimeOff.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setWidth("50%");
            Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
            closeDialog.addClickListener(e -> {
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
            IntegerField duration = new IntegerField("Durée");
            duration.setHasControls(true);
            duration.getStyle().set("margin","10px 10%");
            duration.setMax((int) employee.getDaysOffLeft()+1);
            ComboBox<String> reason = new ComboBox<>("Raison");
            reason.setItems(" maladie "," vacances "," maternité ","paternité");
            reason.setWidth("80%");
            reason.getStyle().set("margin","10px auto");
            VerticalLayout verticalLayout = new VerticalLayout(dateBegin,duration,reason);
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
            });
            Button cancel = new Button("Annuler");
            cancel.setWidthFull();
            cancel.setIcon(new Icon(VaadinIcon.CLOSE));
            cancel.addClickListener(event1 -> dialog.close());
            HorizontalLayout buttonLayout = new HorizontalLayout(send,cancel);
            content.add(verticalLayout,buttonLayout);
            dialog.add(content);
            add(dialog);
            dialog.open();
        });
        VerticalLayout totalContainer = new VerticalLayout(daysOff,requestTimeOff);
        totalContainer.setWidth("50%");
        horizontalLayoutForDaysOffAndOvertime.add(totalContainer);
    }

    private void fillDelaysDiv(Employee employee,DelaysService delaysService){
        delays = new Div();
        titleContainerDelays = new Div();
        titleDelays = new H3("Retards");
        titleContainerDelays.add(titleDelays);
        Grid<Delays> delaysGrid = new Grid<>(Delays.class);
        delaysGrid.setColumns("date");
        totalSeconds = 0;
        List<Delays> delaysList = employee.getDelays();
        for (Delays delay:delaysList){
            if(delay.getDate().getMonth() == LocalDate.now().getMonthValue()-1){
                totalSeconds += delay.getDuration();
            }
        }
        delaysGrid.addColumn(new ComponentRenderer<>(item -> {
            int s = item.getDuration();
            int sec = s % 60;
            int min = (s / 60)%60;
            int hours = (s/60)/60;
            return new Text(hours+"h : "+min+"min : "+sec+"sec");
        })).setHeader("Durée").setKey("duration");
        ListDataProvider<Delays> delaysDataProvider = new ListDataProvider<>(employee.getDelays());
        delaysGrid.setDataProvider(delaysDataProvider);
        delays.add(titleContainerDelays, delaysGrid);
        styleDelays();
        TextField total = new TextField("Retard totale");
        total.setValue((totalSeconds/60)/60+"h : "+(totalSeconds/60)%60+"min : "+totalSeconds%60+"sec");
        total.setReadOnly(true);
        total.setWidth("90%");
        total.getStyle().set("margin","0 auto");
        VerticalLayout totalContainer = new VerticalLayout(delays,total);
        totalContainer.setWidth("50%");
        horizontalLayoutForDelaysAndAbsences.add(totalContainer);
    }

    private void fillAbsencesDiv(Employee employee,AbsenceService absenceService){
        absences = new Div();
        titleContainerAbsences = new Div();
        titleAbsences = new H3("Absences");
        titleContainerAbsences.add(titleAbsences);
        Grid<Absences> absenceGrid = new Grid<>(Absences.class);
        absenceGrid.setColumns("date","duration","justified");
        absenceGrid.getColumnByKey("duration").setHeader("Durée");
        absenceGrid.getColumnByKey("justified").setHeader("Justifiée");
        ListDataProvider<Absences> dataProvider = new ListDataProvider<>(employee.getAbsences());
        absenceGrid.setDataProvider(dataProvider);
        absences.add(titleContainerAbsences, absenceGrid);
        styleAbsences();
        VerticalLayout totalContainer = new VerticalLayout(absences);
        totalContainer.setWidth("50%");
        horizontalLayoutForDelaysAndAbsences.add(totalContainer);

    }

    private void fillPersonalInformationSpans(Employee newEmployee,ReclamationService reclamationService, EmployeeService employeeService, DepartmentService departmentService,UserService userService,RequestDayOffService requestDayOffService){
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
        salary = new Span("Salaire : " + newEmployee.getSalary()+" TND");
        position = new Span("Poste : " + newEmployee.getPosition());
        department = new Span("Départment : " + newEmployee.getDepartment().getName());
        double joursRestants = newEmployee.getDaysOffLeft() ;
        for (RequestDayOff r:requestDayOffService.findRequestDayOffByEmployee(newEmployee.getId())){
            joursRestants -= r.getDuration();
        }
        daysOffLeft = new Span("Jours de Congés restants : " + joursRestants + " jours");
        daysOffAvailable = new Span("Jours de Congés autorisés : " + newEmployee.getDaysOffLeft() + " jours");
        contract = new Span("Type de contrat : " + newEmployee.getContract());
        edit = new Button(new Icon(VaadinIcon.EDIT));
        edit.addClickListener(event -> createEditPersonalInfo(newEmployee,reclamationService,employeeService,departmentService,userService,requestDayOffService));
        reclamer = new Button(new Icon(VaadinIcon.FILE));
        reclamer.addClickListener(event -> reclame(reclamationService));
        openCv = new Button(new Icon(VaadinIcon.EYE));
        openCv.addClickListener(event -> new OpenPdf(employee.getContractPapers(), "contracts"));
        personalInformation.add(titleContainerPersonalInfo,codeCnss,cin,phone,email,salary,contract,daysOffAvailable,daysOffLeft,position,department,edit,reclamer,openCv);
        stylePersonalInfoSpans();
        VerticalLayout pictureNameLayout = new VerticalLayout(picture,fullName);
        pictureNameLayout.getStyle().set("width","30%").set("flex-flow","column-reverse");
        personalInfoMainLayout = new HorizontalLayout(pictureNameLayout, personalInformation);
        personalInfoMainLayout.setWidthFull();
        add(personalInfoMainLayout);
    }

    private void createEditPersonalInfo(Employee employee,ReclamationService reclamationService, EmployeeService employeeService,DepartmentService departmentService, UserService userService,RequestDayOffService requestDayOffService){

//        Remove spans to replace them with fields
        remove(personalInfoMainLayout,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        personalInformation.remove(titleContainerPersonalInfo,codeCnss,cin,phone,email,salary,contract,daysOffAvailable,daysOffLeft,position,department,edit,reclamer,openCv);

//        Create fields
        personalInformation = new Div();
        titleContainerPersonalInfo = new Div();
        titlePersonalInfo = new H3("Renseignements personnels");
        pictureMemoryBuffer = new MemoryBuffer();
        pictureMemoryUpload = new Upload(pictureMemoryBuffer);
        uploadButton = new Button("Ajouter une image");
        firstNameField = new TextField("Prénom");
        lastNameField = new TextField("Nom de la famille");
        codeCnssField = new TextField("Code Cnss");
        cinField = new TextField("Cin");
        phoneField = new TextField("Téléphone");
        emailField = new EmailField("Email");
        password = new PasswordField("Mot de passe");
        confirmPassword = new PasswordField("Mot de passe");
        contractField = new ComboBox<>("Type de contrat");
        contractField.setItems("CDI","CDD","SIVP","Stagiare");
        save = new Button(new Icon(VaadinIcon.CHECK));
        save.setText("Sauvgarder");
        cancel = new Button(new Icon(VaadinIcon.CLOSE));
        cancel.setText("Annuler");
        personalInformationButtonLayout = new HorizontalLayout(save,cancel);
        personalInformationButtonLayout.setId("buttons-layout");
        personalInformationButtonLayout.getStyle().set("margin-top","50px");

//        Fill the fields with the employee data
        fillFields(employee);
//        Add the components to their respective layouts
        titleContainerPersonalInfo.add(titlePersonalInfo);
        personalInformation.add(titleContainerPersonalInfo, password , confirmPassword , personalInformationButtonLayout);
        VerticalLayout pictureNameLayout = new VerticalLayout(fullName, pictureMemoryUpload);
        pictureNameLayout.getStyle().set("width","30%").set("flex-flow","column-reverse");
        personalInfoMainLayout = new HorizontalLayout(pictureNameLayout, personalInformation);
        personalInfoMainLayout.setWidthFull();
        add(personalInfoMainLayout,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        //        Style the fields
        styleFields();

//        Manage the buttons
        save.addClickListener(event -> {
//            Save the update
            if(!password.isEmpty()){
                employee.setPasswordHash(userService.encodePassword(password.getValue()));
            }
            save(employee,employeeService);
//            remove fields from main div to replace it with the spans
            remove(personalInfoMainLayout,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
            personalInformation.remove(titleContainerPersonalInfo, password , confirmPassword , personalInformationButtonLayout);
//            Fill the personal info spans
            fillPersonalInformationSpans(employee, reclamationService, employeeService,departmentService,userService,requestDayOffService);
            add(horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        });

        cancel.addClickListener(event -> {
//            remove fields from main div to replace it with the spans
            remove(personalInfoMainLayout,horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
            personalInformation.remove(titleContainerPersonalInfo, password , confirmPassword , personalInformationButtonLayout);
//            Fill the personal info spans
            fillPersonalInformationSpans(employee, reclamationService, employeeService,departmentService,userService,requestDayOffService);
            add(horizontalLayoutForDelaysAndAbsences,horizontalLayoutForDaysOffAndOvertime,endStylingDiv);
        });

    }
    private void save(Employee employee,EmployeeService employeeService){
        employee.setFirstName(firstNameField.getValue());
        employee.setLastName(lastNameField.getValue());
        employee.setCin(Integer.parseInt(cinField.getValue()));
        employee.setCodeCnss(Integer.parseInt(codeCnssField.getValue()));
        employee.setEmail(emailField.getValue());
        employee.setPhone(Integer.parseInt(phoneField.getValue()));
        employee.setContract(contractField.getValue());
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
            }
        }
        employeeService.update(employee);
        session.setAttribute("username",employee.getEmail());
        Notification.show("Profil Modifié").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void fillFields(Employee employee){
        firstNameField.setValue(employee.getFirstName());
        lastNameField.setValue(employee.getLastName());
        codeCnssField.setValue(String.valueOf(employee.getCodeCnss()));
        cinField.setValue("0"+ employee.getCin());
        phoneField.setValue(String.valueOf(employee.getPhone()));
        emailField.setValue(employee.getEmail());
        contractField.setValue(employee.getContract());
    }

    private void styleDaysOff(){
        daysOff.setWidth("100%");
        daysOff.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px").set("margin-bottom","0");
        titleDaysOff.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            titleContainerDaysOff.getStyle().set("border"," 1px solid").set(
                    "background","var(--nadia-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }else{
            titleContainerDaysOff.getStyle().set("border"," 1px solid").set(
                    "background","var(--theme-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }
    }

    private void styleAbsences(){
        absences.setWidth("100%");
        absences.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("display","inline-block").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            titleContainerAbsences.getStyle().set("border"," 1px solid").set(
                    "background","var(--nadia-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }else{
            titleContainerAbsences.getStyle().set("border"," 1px solid").set(
                    "background","var(--theme-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }
        titleAbsences.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
    }

    private  void styleDelays(){
        delays.setWidth("100%");
        delays.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("display","inline-block").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px").set("margin-bottom","0");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            titleContainerDelays.getStyle().set("border"," 1px solid").set(
                    "background","var(--nadia-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }else{
            titleContainerDelays.getStyle().set("border"," 1px solid").set(
                    "background","var(--theme-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }
        titleDelays.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
    }

    private  void styleOvertime(){
        overtime.setWidth("100%");
        overtime.getStyle().set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("display","inline-block").set("margin","100px auto").set("border","1px solid black").set("padding","10px").set("border-radius","20px").set("margin-bottom","0");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            titleContainerOvertime.getStyle().set("border"," 1px solid").set(
                    "background","var(--nadia-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }else{
            titleContainerOvertime.getStyle().set("border"," 1px solid").set(
                    "background","var(--theme-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }
        titleOvertime.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
        endStylingDiv.setMinHeight("50px");
        endStylingDiv.setWidthFull();
    }

    private void stylePersonalInfoSpans(){
        personalInformation.setWidth("65%");
        personalInformation.getStyle().set("min-height","18.625rem").set("background","#f1f1f1").set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("margin","100px auto").set("margin-bottom","20px").set("border","1px solid black").set("padding","10px").set("border-radius","20px");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            titleContainerPersonalInfo.getStyle().set("border"," 1px solid").set(
                    "background","var(--nadia-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }else{
            titleContainerPersonalInfo.getStyle().set("border"," 1px solid").set(
                    "background","var(--theme-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }
        titlePersonalInfo.getStyle().set("color","white").set("margin","auto").set("display","inline-block");
        fullName.getStyle().set("text-align","center").set("width","100%");
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
        contract.getStyle().set("margin-left","20px").set("width","40%").set("display","inline-block").set("margin-bottom","15px");
        edit.setText("Modifier les informations personnels");
        edit.getStyle().set("float","right").set("margin","20px 10px");
        reclamer.setText("Reclamer");
        reclamer.getStyle().set("float","left").set("margin","20px 10px");
        openCv.setText("Voir le contrat");
        openCv.getStyle().set("float","left").set("margin","20px 10px");
    }

    private void styleFields() {
        // Style box
        personalInformation.setWidth("65%");
        personalInformation.getStyle().set("box-shadow"," inset 4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set("margin","100px auto").set("margin-bottom","10px").set("border","1px solid black").set("padding","10px").set("border-radius","20px");
        if (employee.getEmail().equalsIgnoreCase("nadia@gmail.com")){
            titleContainerPersonalInfo.getStyle().set("border"," 1px solid").set(
                    "background","var(--nadia-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }else{
            titleContainerPersonalInfo.getStyle().set("border"," 1px solid").set(
                    "background","var(--theme-color)").set(
                    "width"," 80%").set(
                    "height"," fit-content").set(
                    "margin","-50px auto 20px").set(
                    "border-radius"," 30px").set(
                    "text-align"," center").set("padding","20px");
        }
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
        pictureMemoryUpload.setWidth("80%");
        pictureMemoryUpload.getStyle().set("margin","0.75rem auto");
//        Style rest of the form
        firstNameField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        lastNameField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        codeCnssField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        cinField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        phoneField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        emailField.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        confirmPassword.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        password.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        contractField.getStyle().set("max-height","55px").set("margin","5% 2%").set("display","inline-block").set("width","45%");
        //        Style buttons
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.getStyle().set("margin-left","10%").set("float","left").set("margin-bottom"," 20px");
        save.setWidthFull();
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.getStyle().set("margin-right","10%").set("float","right").set("margin-left"," 30%").set("margin-bottom"," 20px");
        cancel.setWidthFull();
    }

    private void reclame(ReclamationService reclamationService){
        Dialog dialog = new Dialog();
        dialog.setWidth("50%");
        Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
        closeDialog.addClickListener(e -> {
            dialog.close();
        });
        closeDialog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeDialog.getStyle().set("float","right").set("color","red");
        dialog.add(closeDialog);
        H3 title = new H3("Reclamation");
        title.getStyle().set("margin","1rem auto");

        TextArea message = new TextArea("Message");
        message.setWidthFull();

        Button button = new Button("Envoyer");
        button.getStyle().set("width","10rem").set("margin","1rem auto");

        button.addClickListener(event -> {
            Reclamation reclamation = new Reclamation();
            reclamation.setEmployee(employee);
            reclamation.setDate(Date.valueOf(LocalDate.now()));
            reclamation.setMessage(message.getValue());
            reclamation.setDone(false);
            reclamationService.update(reclamation);
            message.clear();
            Notification.show("Reclamation enovyé avec succées!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        dialog.add(title,message,button);
        add(dialog);
        dialog.open();
    }
}
