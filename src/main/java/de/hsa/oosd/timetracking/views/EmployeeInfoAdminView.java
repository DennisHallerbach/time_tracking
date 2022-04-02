package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.textfield.NumberField;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.*;
import de.hsa.oosd.timetracking.forms.NotificationTypes;
import de.hsa.oosd.timetracking.forms.Notifications;
import de.hsa.oosd.timetracking.security.SecurityService;

@Route(value ="employeeinfo", layout = MainLayout.class)
@PageTitle("EmployeeInfo")

public class EmployeeInfoAdminView extends VerticalLayout {
    private final SecurityService securityService;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmployeeInfoRepository employeeInfoRepository;

    private EmployeeInfo empinfo;



    public EmployeeInfoAdminView(EmployeeInfoRepository employeeInfoRepository, SecurityService securityService, CustomUserDetailsService customUserDetailsService){
         final HorizontalLayout headerLayout = new HorizontalLayout();
         final HorizontalLayout bodyLayout = new HorizontalLayout();
        this.employeeInfoRepository = employeeInfoRepository;
        this. securityService = securityService;
        this.customUserDetailsService = customUserDetailsService;

        Label error2 = new Label();
        VerticalLayout headerContent = new VerticalLayout();
        headerLayout.add(headerContent);

        Paragraph title = new Paragraph("Employee Info");
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
        var user = customUserDetailsService.loadCustomUserByUsername(username);
        empinfo = employeeInfoRepository.findEmployeeInfoByOrganization(user.getOrganization());
        if (empinfo == null){
            error2.setText("There went something wrong");
        }
        Binder<EmployeeInfo> binder = new Binder<>();
        VerticalLayout verticalLayout = new VerticalLayout();
        NumberField workingHours = new NumberField("Working hours per week");
        workingHours.setWidth("15em");
        workingHours.setMax(60);
        workingHours.setMin(0);
        workingHours.setStep(1);
        workingHours.setValue(empinfo.getWorkingHoursPerWeek());

        NumberField vacationDays = new NumberField("Vacation days");
        vacationDays.setWidth("15em");
        vacationDays.setMax(50);
        vacationDays.setMin(0);
        vacationDays.setStep(1);
        vacationDays.setValue(empinfo.getVacationDays());

        NumberField workingDaysPerWeek = new NumberField("Working days per week");
        workingDaysPerWeek.setWidth("15em");
        workingDaysPerWeek.setMax(7);
        workingDaysPerWeek.setMin(0);
        workingDaysPerWeek.setStep(1);
        workingDaysPerWeek.setValue(empinfo.getNumDaysPerWeek());
        var horizontalLayout = new HorizontalLayout();

        Checkbox isMonday = new Checkbox("Monday", empinfo.isMonday());

        Checkbox isTuesday = new Checkbox("Tuesday", empinfo.isTuesday());

        Checkbox isWednesday = new Checkbox("Wednesday", empinfo.isWednesday());

        Checkbox isThursday = new Checkbox("Thursday", empinfo.isThursday());

        Checkbox isFriday = new Checkbox("Friday", empinfo.isFriday());

        Checkbox isSaturday = new Checkbox("Saturday", empinfo.isSaturday());

        Checkbox isSunday = new Checkbox("Sunday", empinfo.isSunday());
        horizontalLayout.add(isMonday,isTuesday,isWednesday,isThursday,isFriday,isSaturday,isSunday);


        binder.bind(isMonday, EmployeeInfo::isMonday, EmployeeInfo::setMonday);
        binder.bind(isTuesday, EmployeeInfo::isTuesday, EmployeeInfo::setTuesday);
        binder.bind(isWednesday, EmployeeInfo::isWednesday, EmployeeInfo::setWednesday);
        binder.bind(isThursday, EmployeeInfo::isThursday, EmployeeInfo::setThursday);
        binder.bind(isFriday, EmployeeInfo::isFriday, EmployeeInfo::setFriday);
        binder.bind(isSaturday, EmployeeInfo::isSaturday, EmployeeInfo::setSaturday);
        binder.bind(isSunday, EmployeeInfo::isSunday, EmployeeInfo::setSunday);
        binder.bind(workingHours,EmployeeInfo::getWorkingHoursPerWeek,EmployeeInfo::setWorkingHoursPerWeek);
        binder.bind(vacationDays,EmployeeInfo::getVacationDays,EmployeeInfo::setVacationDays);
        binder.bind(workingDaysPerWeek,EmployeeInfo::getNumDaysPerWeek,EmployeeInfo::setNumDaysPerWeek);





        Button save = new Button("save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e ->{
            try{
                binder.writeBean(empinfo);
                employeeInfoRepository.save(empinfo);
                Notifications.showNotification("Saved successfully", 5, NotificationTypes.Success);
            }catch (Exception ex){
                error2.setText(ex.getMessage());
            }
        });
        verticalLayout.add(workingHours,vacationDays,workingDaysPerWeek,horizontalLayout,save);
        return verticalLayout;
    }



}

