package com.bioinnovate.grh.views.Employess;


import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.entity.User;
import com.bioinnovate.grh.data.service.*;
import com.bioinnovate.grh.data.utils.UpdateSoldeDaysOff;
import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route(value = "employees", layout = MainView.class)
@PageTitle("Employées")
@CssImport("./styles/views/main/main-view.css")
@Secured({"ADMIN","SUPER USER"})
public class EmployeesView extends Div {

    private ListBox<Employee> employees = new ListBox<>();
    private TextField filterText = new TextField();
    private Employee user;
    private VaadinSession session = VaadinSession.getCurrent();


    public EmployeesView(@Autowired EmployeeService employeeService, @Autowired AbsenceService absenceService,
                         @Autowired DelaysService delaysService, @Autowired DaysOffService daysOffService,
                         @Autowired OvertimeService overtimeService, @Autowired DepartmentService departmentService,
                         @Autowired RequestDayOffService requestDayOffService,@Autowired UserService userService){

        user = employeeService.findEmployeeByEmail(session.getAttribute("username").toString());

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.getStyle().set("margin","auto");
        verticalLayout.setWidth("80%");


        employees.setHeightFull();
        employees.setWidthFull();
        employees.setRenderer(new ComponentRenderer<>(employee -> {

            if(LocalDate.now().getDayOfMonth() == 15){
                UpdateSoldeDaysOff.update(delaysService,absenceService,employeeService,employee);
            }

            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            Avatar avatar = new Avatar();
            avatar.setName(employee.getFirstName()+" "+employee.getLastName());
            if (employee.getPicture() == null){
                if (employee.getGender()){
                    avatar.setImage("images/female.png");
                }else {
                    avatar.setImage("images/male.png");
                }
            }else{
                avatar.setImage("images/"+employee.getPicture());
            }

            Span name = new Span(employee.getFirstName()+" "+employee.getLastName());
            Span profession = new Span(employee.getPosition()+" @ "+ employee.getDepartment().getName()+" Départment");
            Span phone = new Span(String.valueOf(employee.getPhone()));
            profession.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            phone.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout personalDetails = new VerticalLayout(name, profession,phone);
            personalDetails.setPadding(false);
            personalDetails.setSpacing(false);
            personalDetails.setWidth("90%");

            Button delete = new Button(new Icon(VaadinIcon.TRASH));
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                employee.setActive(false);
                User thisUser = employee;
                userService.update(thisUser);
                if (user.getUserRoles().getRole().equalsIgnoreCase("admin")){
                    employees.setItems(employeeService.findAll());
                } else {
                    employees.setItems(employeeService.findAllForOneDepartment(user.getDepartment()));
                }
            });
            VerticalLayout operations = new VerticalLayout(delete);
            operations.setPadding(false);
            operations.setSpacing(false);
            operations.setWidth("auto");

            row.add(avatar, personalDetails, operations);
            row.getStyle().set("line-height", "var(--lumo-line-height-m)");
            row.add(new Hr());

            Dialog dialog = new Dialog();
            dialog.setSizeFull();
            Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
            closeDialog.addClickListener(e -> {
                if (user.getUserRoles().getRole().equalsIgnoreCase("admin")){
                    employees.setItems(employeeService.findAll());
                } else {
                    employees.setItems(employeeService.findAllForOneDepartment(user.getDepartment()));
                }
                dialog.close();
            });
            dialog.add(closeDialog);
            closeDialog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeDialog.getStyle().set("float","right").set("color","red");
            SingleEmployeeView singleEmployeeView = new SingleEmployeeView(employee,employeeService,absenceService,delaysService,daysOffService,departmentService,overtimeService,requestDayOffService);
            dialog.add(singleEmployeeView);
            personalDetails.add(dialog);
            personalDetails.addClickListener(e -> dialog.open());
            return row;
        }));
        if (user.getUserRoles().getRole().equalsIgnoreCase("admin")){
            employees.setItems(employeeService.findAll());
        } else {
            employees.setItems(employeeService.findAllForOneDepartment(user.getDepartment()));
        }

        filterText.setPlaceholder("Rechercher par nom...");
        filterText.setWidth("100%");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> updateList(employeeService));
        filterText.setPrefixComponent(VaadinIcon.SEARCH.create());
        Button addEmployee = new Button(new Icon(VaadinIcon.PLUS));
        addEmployee.setWidth("225px");
        addEmployee.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEmployee.setText("Ajouter Employée");
        addEmployee.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setSizeFull();
            Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
            closeDialog.addClickListener(e -> {
                if (user.getUserRoles().getRole().equalsIgnoreCase("admin")){
                    employees.setItems(employeeService.findAll());
                } else {
                    employees.setItems(employeeService.findAllForOneDepartment(user.getDepartment()));
                }
                dialog.close();
            });
            closeDialog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeDialog.getStyle().set("float","right").set("color","red");
            dialog.add(closeDialog);
            AddEmployeeView addEmployeeView = new AddEmployeeView(employeeService,departmentService,userService);
            dialog.add(addEmployeeView);
            add(dialog);
            dialog.open();
        });
        HorizontalLayout container = new HorizontalLayout(filterText,addEmployee);
        container.setWidthFull();
        verticalLayout.add(container,employees);

        add(verticalLayout);
    }

    public void updateList(EmployeeService employeeService) {
        List<Employee> newList = new ArrayList<>();
        List<Employee> listEmployees;
        if (user.getUserRoles().getRole().equalsIgnoreCase("admin")){
            listEmployees = employeeService.findAll();
        } else {
            listEmployees = employeeService.findAllForOneDepartment(user.getDepartment());
        }
        for (Employee employee : listEmployees) {
            if ((employee.getFirstName().contains(filterText.getValue())) || (employee.getLastName().contains(filterText.getValue())) ){
                newList.add(employee);
            }
        }
        employees.setItems(newList);
    }

}
