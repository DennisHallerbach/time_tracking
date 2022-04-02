package de.hsa.oosd.timetracking.views;


import com.vaadin.flow.component.dependency.CssImport;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("login")
@CssImport("./styles/dashboard.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        VerticalLayout verticalLayout = new VerticalLayout();
        final VerticalLayout vl = new VerticalLayout();
        add(verticalLayout);
        verticalLayout.add(loginForm);


        var width = loginForm.getElement().getAttribute("Width");
        loginForm.setAction("login");
        Paragraph title = new Paragraph("Noch kein Admin Account?");
        vl.add(title);
        RouterLink listLink = new RouterLink("Registieren", RegisterView.class);
        vl.add(listLink);
        vl.setWidth("360px");
        verticalLayout.add(vl);
        vl.addClassName("BackgroundWhite");
        verticalLayout.addClassName("centerLayout");


    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
