package com.bioinnovate.grh.views.main;

import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.service.EmployeeService;
import com.bioinnovate.grh.views.EmployeeProfile.EmployeeProfileView;
import com.bioinnovate.grh.views.Employess.EmployeesView;
import com.bioinnovate.grh.views.RequestDaysOff.RequestDayOffManagementView;
import com.bioinnovate.grh.views.RequestDaysOff.RequestDaysOffView;
import com.bioinnovate.grh.views.home.HomeView;
import com.bioinnovate.grh.views.user.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "Human Resources Management", shortName = "HRM", enableInstallPrompt = false)
@JsModule("./styles/shared-styles.js")
public class MainView extends AppLayout {

    private final Tabs menu;
    private H1 viewTitle;
    private VaadinSession session = VaadinSession.getCurrent();
    private Employee employee;

    public MainView(@Autowired EmployeeService employeeService) {
        employee = employeeService.findEmployeeByEmail(session.getAttribute("username").toString());
        setPrimarySection(Section.DRAWER);
        menu = createMenu();
        addToNavbar(true, createHeaderContent(menu));

    }

    private Component createHeaderContent(Tabs menu) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setId("navbar");

        HorizontalLayout layout = new HorizontalLayout();
        Anchor logout = new Anchor("logout", "");
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        viewTitle = new H1();
        layout.add(viewTitle);
        Div avatar = new Div();
        avatar.setWidthFull();
        layout.add(avatar);
        Button logoutButton = new Button(new Icon(VaadinIcon.SIGN_OUT_ALT));
        logoutButton.getStyle().set("color","white");
        logoutButton.addClickListener(event -> {
            VaadinSession session = VaadinSession.getCurrent();
            session.setAttribute("username", null);
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logout.add(logoutButton);
        layout.add(logout);

        HorizontalLayout layout2 = new HorizontalLayout();
        layout2.setId("menu");
        layout2.getThemeList().set("dark", true);
        layout2.setWidthFull();
        layout2.setSpacing(false);
        layout2.setAlignItems(FlexComponent.Alignment.CENTER);
        layout2.add(menu);
        verticalLayout.add(layout,layout2);
        return verticalLayout;
    }


    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        //tabs.add(createMenuItems());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        if(employee.getUserRoles().getRole().equalsIgnoreCase("Admin")){
//            tabs.add(createTab("Dashboard", HomeView.class));
            tabs.add(createTab("Profile", EmployeeProfileView.class));
            tabs.add(createTab("Employées", EmployeesView.class));
            tabs.add(createTab("Utilisateurs" , UserView.class));
        }else if (employee.getUserRoles().getRole().equalsIgnoreCase("user")){
            tabs.add(createTab("Profile", EmployeeProfileView.class));
            tabs.add(createTab("Congés", RequestDaysOffView.class));
        }else{
            tabs.add(createTab("Profile", EmployeeProfileView.class));
            tabs.add(createTab("Employées", EmployeesView.class));
            tabs.add(createTab("Congés", RequestDayOffManagementView.class));
        }


        return tabs;
    }



    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        Tab tab = new Tab();
        switch (text) {
            case "Utilisateurs":
                tab = new Tab(VaadinIcon.USER.create());
                break;
            case "Employées":
                tab = new Tab(VaadinIcon.WORKPLACE.create());
                break;
            case "Home":
                tab = new Tab(VaadinIcon.HOME.create());
                break;
            case "Profile":
                tab = new Tab(VaadinIcon.USER_CARD.create());
                break;
            case "Congés":
                tab = new Tab(VaadinIcon.DATE_INPUT.create());
                break;
        }
        tab.add(new RouterLink(text,navigationTarget));
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        ComponentUtil.setData(tab, Class.class, navigationTarget);

        return tab;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
        viewTitle.setText(getCurrentPageTitle());
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }
}
