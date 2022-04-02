package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.*;
import de.hsa.oosd.timetracking.entities.CustomUser;
import de.hsa.oosd.timetracking.entities.CustomUserDetailsService;
import de.hsa.oosd.timetracking.entities.Employee;
import de.hsa.oosd.timetracking.entities.EmployeeRepository;


@Route(value ="detail", layout = MainLayout.class)
@PageTitle("EmployeeDetail")
public class EmployeeDetailsView extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver  {
    private final EmployeeRepository employeeRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private String username;


    private  TextField firstname;
    private TextField lastName;
    private EmailField email;
    private TextField position;
    private H1 errors;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        this.username=s;


    }
    public EmployeeDetailsView(EmployeeRepository employeeRepository,  CustomUserDetailsService customUserDetailsService){
        HorizontalLayout headerLayout = new HorizontalLayout();
        this.employeeRepository = employeeRepository;
        this.customUserDetailsService = customUserDetailsService;
        VerticalLayout headerContent = new VerticalLayout();
        headerLayout.add(headerContent);

        Paragraph title = new Paragraph("Employee Details");
        title.setClassName("header-view");
        headerContent.add(title);
        add(headerContent);

        add(createLayout());
    }

    public VerticalLayout createLayout(){
        VerticalLayout verticalLayout = new VerticalLayout();

        errors = new H1();
        errors.setVisible(false);

        firstname = new TextField("Firstname");
        firstname.setReadOnly(true);
        firstname.setVisible(false);
        firstname.addClassName("text-field");

        lastName = new TextField("Lastname");
        lastName.setReadOnly(true);
        lastName.setVisible(false);
        lastName.addClassName("text-field");

        email = new EmailField("Email");
        email.setReadOnly(true);
        email.setVisible(false);
        email.addClassNames("text-field", "email");

        position = new TextField("Position");
        position.setReadOnly(true);
        position.setVisible(false);
        position.addClassNames("text-field", "position");

        verticalLayout.add(errors, firstname, lastName, email, position);

        return verticalLayout;

    }


    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {

        CustomUser user;
        try {
            user = customUserDetailsService.loadCustomUserByUsername(username);
        } catch (Exception e) {

            errors.setText(String.format("No User found for username: %s . Please look for another User", username));
            errors.setVisible(true);
            return;
        }

         var emp = employeeRepository.findEmployeeByCustomUser_Username(username);
        if (emp == null) {
            emp = new Employee("", "", "", user);
            errors.setText(String.format("User %s has not updated his profile, so the profile is empty :/", user.getUsername()));
            errors.setVisible(true);

        }
        firstname.setVisible(true);
        firstname.setValue(emp.getFirstName());

        lastName.setVisible(true);
        lastName.setValue(emp.getLastName());

        email.setVisible(true);
        email.setValue(emp.getEmail());

        position.setVisible(true);
        position.setValue(emp.getPosition());
    }
}

