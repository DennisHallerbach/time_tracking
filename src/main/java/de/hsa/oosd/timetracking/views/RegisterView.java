package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.*;

import javax.annotation.security.PermitAll;
@PermitAll
@Route("register")
@PageTitle("Registration")

public class RegisterView extends FormLayout {
    private final CustomUserDetailsService customUserDetailsService;
    private final OrganizationService organizationService;
    private final EmployeeInfoRepository employeeInfoRepository;

    final Binder<CustomUser> binder = new Binder<>(CustomUser.class);
    final Binder<Organization> binder2 = new Binder<>(Organization.class);
    private final CustomUser user;
    private final Organization org;
    public RegisterView(CustomUserDetailsService customUserDetailsService, OrganizationService organizationService, EmployeeInfoRepository employeeInfoRepository){
        this.organizationService = organizationService;
        this.customUserDetailsService = customUserDetailsService;
        this.employeeInfoRepository = employeeInfoRepository;
        this.user = new CustomUser();
        this.org = new Organization();
         final HorizontalLayout horizontalLayout = new HorizontalLayout();
         final VerticalLayout  verticalLayout = new VerticalLayout();
        H1 headline = new H1("Admin Registration Page");
        Label error = new Label();
        TextField username = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        TextField organization = new TextField("Organization");

        binder.bind(username, CustomUser::getUsername, CustomUser::setUsername);
        binder.bind(passwordField, CustomUser::getPassword, CustomUser::setPassword);
        binder2.bind(organization, Organization::getName, Organization::setName);

        Button saveButton = new Button("Register");
        saveButton.addClickListener(click -> {
            try {
                binder2.writeBean(org);
                binder.writeBean(user);

                this.organizationService.createOrga(org.getName());
                this.customUserDetailsService.createUser(user.getUsername(),user.getPassword(), "Role_Admin"
                        ,org.getName());
                var savedorg = this.organizationService.getOrgbyName(org.getName());
                var empinfo = new EmployeeInfo(savedorg);
                this.employeeInfoRepository.save(empinfo);
                UI.getCurrent().navigate(LoginView.class);
            } catch (Exception e) {
                error.setText(e.getMessage());
                binder.removeBean();
                binder.removeBean();
            }
        });
        horizontalLayout.add(username,passwordField);
        horizontalLayout.add(organization);
        verticalLayout.add(headline,error,horizontalLayout,saveButton);

        verticalLayout.setSizeFull();
        verticalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        add(verticalLayout);
    }

}
