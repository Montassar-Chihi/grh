package com.bioinnovate.grh.views.Employess;

import com.bioinnovate.grh.data.entity.Department;
import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.service.DepartmentService;
import com.bioinnovate.grh.data.service.EmployeeService;
import com.bioinnovate.grh.data.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;

@CssImport("./styles/views/main/main-view.css")
public class AddEmployeeView extends Div {

    private final Div personalInformation;
    private final Div titleContainerPersonalInfo;
    private final H3 titlePersonalInfo;
    private final Div endStylingDiv;
    private final ComboBox<String> contractField;
    private final MemoryBuffer pictureMemoryBuffer;
    private final Upload pictureMemoryUpload ;
    private final Button uploadButton ;
    private final TextField firstName ;
    private final TextField lastName;
    private final TextField codeCnss;
    private final TextField cin ;
    private final TextField phone ;
    private final EmailField email ;
    private final TextField salary ;
    private final Div dinarPrefix ;
    private final IntegerField daysOffLeft;
    private final TextField position ;
    private final ComboBox<Department> department ;
    private final ComboBox<String> gender ;
    private final Button save;
    private final Button cancel ;
    private final ComboBox<Employee> substitute;
    private VaadinSession session = VaadinSession.getCurrent();
    private Employee user;

    public AddEmployeeView(@Autowired EmployeeService employeeService, @Autowired DepartmentService departmentService,@Autowired UserService userService){

        user = employeeService.findEmployeeByEmail(session.getAttribute("username").toString());

        //        Create UI
        personalInformation = new Div();
        titleContainerPersonalInfo = new Div();
        titlePersonalInfo = new H3("Renseignements personnels");
        pictureMemoryBuffer = new MemoryBuffer();
        pictureMemoryUpload = new Upload(pictureMemoryBuffer);
        uploadButton = new Button("Ajouter une image");
        firstName = new TextField("Prénom");
        lastName = new TextField("Nom de la famille");
        codeCnss = new TextField("Code Cnss");
        cin = new TextField("Cin");
        phone = new TextField("Téléphone");
        email = new EmailField("Email");
        salary = new TextField("Salaire");
        dinarPrefix = new Div();
        dinarPrefix.setText("TND");
        salary.setPrefixComponent(dinarPrefix);
        daysOffLeft = new IntegerField("Congés");
        daysOffLeft.setHasControls(true);
        position = new TextField("Poste");
        department = new ComboBox<>("Départment");
        department.setItems(departmentService.findAll());
        department.setItemLabelGenerator(Department::getName);
        if (user.getUserRoles().getRole().equalsIgnoreCase("super user")){
            department.setValue(user.getDepartment());
            department.setReadOnly(true);
        }
        substitute = new ComboBox<>("Sub");
        substitute.setItems(employeeService.findAll());
        substitute.setItemLabelGenerator(Employee::getName);
        contractField = new ComboBox<>("Type de contrat");
        contractField.setItems("CDI","CDD","SIVP","Stagiare");
        gender = new ComboBox<>("Sexe");
        save = new Button(new Icon(VaadinIcon.CHECK));
        cancel = new Button(new Icon(VaadinIcon.CLOSE));

        titleContainerPersonalInfo.add(titlePersonalInfo);
        personalInformation.add(titleContainerPersonalInfo,pictureMemoryUpload,firstName,lastName,codeCnss,cin,
                phone,email,salary,position,department,substitute,gender,contractField);
        add(personalInformation,save,cancel);
        endStylingDiv = new Div();
        add(endStylingDiv);

//        Configure buttons
        save.setText("Sauvgarder");
        save.addClickListener(event -> save(employeeService,userService));

        cancel.setText("Annuler");
        cancel.addClickListener(event -> cancel());

//        Style the page
        stylePage();

    }

    private void cancel(){
        firstName.setValue("");
        lastName.setValue("");
        codeCnss.setValue("");
        cin.setValue("");
        phone.setValue("");
        email.setValue("");
        salary.setValue("");
        daysOffLeft.setValue(0);
        position.setValue("");
        department.setValue(null);
        gender.setValue("");
        contractField.setValue(null);
        substitute.setValue(null);
    }

    private void save(EmployeeService employeeService, UserService userService){
        Employee employee = new Employee();
        employee.setStartWorkDate(Date.valueOf(LocalDate.now()));
        employee.setFirstName(firstName.getValue());
        employee.setLastName(lastName.getValue());
        employee.setCin(Integer.parseInt(cin.getValue()));
        employee.setCodeCnss(Integer.parseInt(codeCnss.getValue()));
        employee.setSalary(Double.parseDouble(salary.getValue()));
        employee.setEmail(email.getValue());
        employee.setPosition(position.getValue());
        employee.setPhone(Integer.parseInt(phone.getValue()));
        employee.setDepartment(department.getValue());
        employee.setDaysOffLeft(daysOffLeft.getValue());
        employee.setGender(gender.getValue().equals("female"));
        employee.setActive(true);
        employee.setInDaysOff(false);
        employee.setSubstitute(substitute.getValue());
        employee.setContract(contractField.getValue());
        employee.setPasswordHash(userService.encodePassword("123456"));
        String fileName = pictureMemoryBuffer.getFileName();
        if (!fileName.equals("")) {
            try {
                byte[] array;
                employee.setPicture(employee.getId() + fileName);
                InputStream fileData = pictureMemoryBuffer.getInputStream();
                array = fileData.readAllBytes();
                FileOutputStream output = new FileOutputStream("C:\\Users\\ASUS\\Desktop\\resources\\" + employee.getPicture());
                output.write(array);
                output.close();
            }catch(IOException error){
                Notification.show("Impossible de télécharger l'image correctement");
            }
        }
        try{
            employeeService.update(employee);
        }catch (Exception exception){
            Notification.show("Email déja utilisé !");
        }
    }

    private void stylePage() {
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
        titlePersonalInfo.getStyle().set("color","white").set("margin","auto");
//        Style form
//        Style Image uploader
        uploadButton.setIcon(new Icon(VaadinIcon.FILE_ADD));
        uploadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Span dropLabel = new Span("Drag and Drop File HERE!");
        Span dropIcon = new Span("");
        pictureMemoryUpload.setUploadButton(uploadButton);
        pictureMemoryUpload.setDropLabel(dropLabel);
        pictureMemoryUpload.setDropLabelIcon(dropIcon);
        pictureMemoryUpload.setWidth("50%");
        pictureMemoryUpload.getStyle().set("margin","0 auto");
//        Style rest of the form
        firstName.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        lastName.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        codeCnss.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        cin.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        phone.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        email.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        dinarPrefix.setText("TND");
        salary.setPrefixComponent(dinarPrefix);
        salary.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        daysOffLeft.getStyle().set("width","20%").set("margin","0% 2%").set("display","inline-block");
        daysOffLeft.setHasControls(true);
        position.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block");
        department.getStyle().set("width","45%").set("max-height","55px").set("margin","0% 2%").set("display","inline-block")
                .set("position","relative").set("top","32px");
        gender.getStyle().set("width","45%").set("margin","0% 2%").set("display","inline-block");
        gender.setItems("Female","Male");
        contractField.getStyle().set("width","45%").set("margin","0% 2%").set("display","inline-block");
        substitute.getStyle().set("width","45%").set("margin","0% 2%").set("display","inline-block");
//        Style buttons
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.getStyle().set("margin-left","10%").set("float","left");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.getStyle().set("margin-right","10%").set("float","right");

        endStylingDiv.setMinHeight("100px");
        endStylingDiv.setWidthFull();
    }
}
