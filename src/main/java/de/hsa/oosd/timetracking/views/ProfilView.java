package de.hsa.oosd.timetracking.views;




import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.*;
import de.hsa.oosd.timetracking.forms.NotificationTypes;
import de.hsa.oosd.timetracking.forms.Notifications;
import de.hsa.oosd.timetracking.security.SecurityService;

import javax.annotation.security.PermitAll;
@PermitAll
@Route(value ="profil", layout = MainLayout.class)
@PageTitle("ProfilPage")
@CssImport("./styles/profileView.css")
public class ProfilView extends VerticalLayout {
    private final EmployeeRepository employeeRepository;
    private final SecurityService securityService;
    private final CustomUserDetailsService customUserDetailsService;
    private Employee emp;

    public ProfilView(EmployeeRepository employeeRepository, SecurityService securityService, CustomUserDetailsService customUserDetailsService){
         final HorizontalLayout headerLayout = new HorizontalLayout();
         final HorizontalLayout bodyLayout = new HorizontalLayout();
        this.employeeRepository = employeeRepository;
        this. securityService = securityService;
        this.customUserDetailsService = customUserDetailsService;

        Label error2 = new Label();
        VerticalLayout headerContent = new VerticalLayout();
        headerLayout.add(headerContent);

        Paragraph title = new Paragraph("User profile page");
        title.setClassName("header-view");
        headerContent.add(title);

        add(headerLayout);
        bodyLayout.setWidth("100%");
        bodyLayout.setHeight("100%");
        bodyLayout.add(error2, createLayout(error2));
        add(bodyLayout);
    }
    public VerticalLayout createLayout(Label error2){
        var username = securityService.getAuthenticatedUser().getUsername();
        emp = employeeRepository.findEmployeeByCustomUser_Username(username);
        if (emp == null){
            CustomUser user = customUserDetailsService.loadCustomUserByUsername(username);
            emp = new Employee("","","",user);
        }
        Binder <Employee> binder = new Binder<>();
        VerticalLayout horizontalLayout = new VerticalLayout();

        TextField firstName = new TextField("Firstname",emp.getFirstName(),"Type in your firstname");
        firstName.addClassName("text-field");

        TextField lastName = new TextField("Lastname",emp.getLastName(),"Type in your lastname");
        lastName.addClassName("text-field");

        EmailField email = new EmailField("Email", emp.getEmail());
        email.addClassNames("text-field", "e-mail");

        TextField position = new TextField("Position",emp.getPosition(),"Type in your Position");
        position.addClassNames("text-field", "position");

        email.setValue(emp.getEmail());
        binder.forField(firstName)
                .asRequired("Firstname must not be empty")
                .bind(Employee::getFirstName, Employee::setFirstName);
        binder.forField(lastName)
                .asRequired("LastName must not be empty")
                .bind(Employee::getLastName, Employee::setLastName);
        binder.forField(email)
                .asRequired("Email must not be empty")
                .bind(Employee::getEmail, Employee::setEmail);
        binder.forField(position)
                .asRequired("Position must not be empty")
                .bind(Employee::getPosition, Employee::setPosition);

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e ->{
            try{
                binder.writeBean(emp);
                saveEmployeeDetails(emp);
                Notifications.showNotification("Saved successfully", 5, NotificationTypes.Success);
            }catch (Exception ex){
                error2.setText(ex.getMessage());
            }
        });
        horizontalLayout.add(firstName,lastName,email, position,save);
        return horizontalLayout;
    }

    public void saveEmployeeDetails(Employee emp){
        employeeRepository.save(emp);
    }


}
