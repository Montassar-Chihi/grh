package com.bioinnovate.grh.views.user;

import com.bioinnovate.grh.data.entity.*;
import com.bioinnovate.grh.data.service.*;
import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

import java.util.Optional;

@Route(value = "users", layout = MainView.class)
@PageTitle("Utilisateurs")
@CssImport("./styles/views/user/user-view.css")
@Secured({"ADMIN"})
public class UserView extends Div {

    private Grid<User> grid = new Grid<>(User.class, false);

    private TextField name;
    private TextField email;

    private PasswordField password;
    private PasswordField confirmPassword;
    private Checkbox active;

    private ComboBox<UserRole> roles ;

    private Button cancel = new Button("Annuler");
    private Button save = new Button("Sauvgarder");

    private BeanValidationBinder<User> binder;

    private User user;

    public UserView(@Autowired UserService userService ) {
        setId("societe-view");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout,userService);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true).setHeader("Nom");
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("userRoles.name").setAutoWidth(true).setHeader("Occupation");
        grid.setDataProvider(new CrudServiceDataProvider<>(userService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<User> userFromBackend = userService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if (userFromBackend.isPresent()) {
                    populateForm(userFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(User.class);

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.setIcon(new Icon(VaadinIcon.CHECK));
        save.addClickListener(e -> {
            try {
                if (this.user == null) {
                    this.user = new User();
                }
                binder.writeBean(this.user);
                UserRole userRoles = roles.getValue();
                this.user.setUserRoles(userRoles);
                if (!password.isEmpty()){
                    this.user.setPasswordHash(userService.encodePassword(password.getValue()));
                }
                userService.update(this.user);
                clearForm();
                refreshGrid();
                Notification.show("Les données de l'utilisateur sont sauvgardées!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (ValidationException validationException) {
                Notification.show("Les données de l'utilisateur ne sont pas sauvgardées!").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });




    }


    private void createEditorLayout(SplitLayout splitLayout,UserService userService) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);
        FormLayout formLayout = new FormLayout();
        name = new TextField("Nom d'utilisateur " );
        email = new TextField("Email");

        roles = new ComboBox<>("Role");
        roles.setItems(userService.findAllRoles());
        roles.setItemLabelGenerator(UserRole :: getName);
        roles.setClearButtonVisible(true);

        password = new PasswordField("Mot de Passe");
        password.setValue("123456");
        confirmPassword = new PasswordField("Confirmer le mot de passe");
        confirmPassword.setValue("123456");
        active = new Checkbox("Active");

        Component[] fields = new Component[]{name, email,password,confirmPassword,active,roles};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        name.clear();
        email.clear();
        active.clear();
        password.clear();
        confirmPassword.clear();
        roles.clear();
    }

    private void populateForm(User value) {
        password.clear();
        confirmPassword.clear();
        this.user = value;
        roles.setValue(value.getUserRoles());
        binder.readBean(this.user);

    }
}
