package com.bioinnovate.grh.views.login;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("login")
@PageTitle("Login | Bio Service")
@CssImport("./styles/views/main/main-view.css")

public class LoginView extends Div {

    private VaadinSession session = VaadinSession.getCurrent();
    public static final String ROUTE = "login";

    //private LoginOverlay login = new LoginOverlay();
    private LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        getStyle().set("width","100%").set(
                "height","100%").set(
                "display","flex").set(
                "background","#efefef");

        login.setI18n(createLoginI18n());
        login.setAction("login");
        login.addLoginListener(event -> session.setAttribute("username", event.getUsername()));
        VerticalLayout loginLayout = new VerticalLayout(login);
        loginLayout.setWidth("50%");

        Image logo = new Image("images/logo.png", "Bio Service logo");
        VerticalLayout imageLayout = new VerticalLayout(logo);
        imageLayout.getStyle().set("align-items","end");
        imageLayout.setWidth("50%");
        imageLayout.setPadding(false);

        HorizontalLayout mainLayout = new HorizontalLayout(imageLayout,loginLayout);
        mainLayout.getStyle().set("border","1px solid green").set(
                "background","white").set(
                "box-shadow","4px 4px 15px 0px #6c6868, 5px 5px 15px 5px rgb(0 0 0 / 0%)").set(
                "justify-content","center").set(
                "align-items","center").set(
                "margin","auto");
        mainLayout.setPadding(false);
        add(mainLayout);
    }


    private LoginI18n createLoginI18n(){
        LoginI18n i18n = LoginI18n.createDefault();

	/*  not sure if needed
	i18n.setHeader(new LoginI18n.Header());
	i18n.setForm(new LoginI18n.Form());
	i18n.setErrorMessage(new LoginI18n.ErrorMessage());
	*/

        // define all visible Strings to the values you want
        // this code is copied from above-linked example codes for Login
        // in a truly international application you would use i.e. `getTranslation(USERNAME)` instead of hardcoded string values. Make use of your I18nProvider
        //i18n.getHeader().setTitle("Nome do aplicativo");
        //i18n.getHeader().setDescription("Descrição do aplicativo");
        i18n.getForm().setUsername("Email"); // this is the one you asked for.
        i18n.getForm().setTitle("Bio Service HR Manager");
        i18n.getForm().setSubmit("Login");
        i18n.getForm().setPassword("Mot de passe");
        i18n.getErrorMessage().setTitle("Mauvais Email ou/et Mot de passe!");
        i18n.getErrorMessage()
                .setMessage("Mauvais Email ou/et Mot de passe!");
//
        return i18n;
    }
}
