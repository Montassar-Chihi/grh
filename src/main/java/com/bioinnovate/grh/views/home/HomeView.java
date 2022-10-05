package com.bioinnovate.grh.views.home;

import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;


@Route(value = "home", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("./styles/views/main/main-view.css")
@Secured({"ADMIN","USER","SUPER USER"})
public class HomeView extends Div {


}
