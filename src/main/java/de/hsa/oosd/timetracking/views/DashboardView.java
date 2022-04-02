package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import de.hsa.oosd.timetracking.entities.EmployeeService;
import de.hsa.oosd.timetracking.forms.NotificationTypes;
import de.hsa.oosd.timetracking.models.DashboardInfo;
import de.hsa.oosd.timetracking.models.IdNameItem;
import javassist.NotFoundException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import static de.hsa.oosd.timetracking.forms.Notifications.showNotification;
import static de.hsa.oosd.timetracking.utils.DateTimePeriodUtils.isPeriodValid;


@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/dashboard.css")
public class DashboardView extends VerticalLayout {

    private final HorizontalLayout headerLayout = new HorizontalLayout();
    private final HorizontalLayout bodyLayout = new HorizontalLayout();
    private final VerticalLayout  infoLayout = new VerticalLayout();
    private final VerticalLayout  mainContentLayout = new VerticalLayout();
    private final VerticalLayout currentInfoLayout = new VerticalLayout();
    private final HorizontalLayout timeEntryLayout = new HorizontalLayout();

    private final EmployeeService employeeService;

    private DashboardInfo data;

    public DashboardView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        var dataLoaded = loadData();
        initializeLayout();

        if (dataLoaded)
            setData();
    }

    private void initializeLayout() {
        setClassName("dashboard");
        headerLayout.setClassName("dashboard-header");

        bodyLayout.add(mainContentLayout);
        bodyLayout.add(infoLayout);
        bodyLayout.setClassName("dashboard-body");

        mainContentLayout.setClassName("main-content");

        mainContentLayout.add(currentInfoLayout);
        currentInfoLayout.setClassName("current-info");

        mainContentLayout.add(timeEntryLayout);
        timeEntryLayout.setClassName("dynamic-content");
        timeEntryLayout.add(getTimeEntryArea());

        infoLayout.setClassName("info");

        add(headerLayout);
        add(bodyLayout);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.ENGLISH).format(new Date());
    }

    private boolean loadData() {
        try {
            this.data = employeeService.getDashboardInfo();
            return true;
        } catch (NotFoundException e) {
            showNotification(e.getMessage(), NotificationTypes.Error);
            return false;
        }
    }

    private void setData() {
        VerticalLayout headerContent = new VerticalLayout();
        headerLayout.add(headerContent);

        Paragraph title = new Paragraph(String.format("Welcome, %s", this.data.fullName));
        title.setClassName("welcome-user");
        headerContent.add(title);

        Paragraph quoteOfTheDay = new Paragraph(String.format("\"%s\"",this.data.quoteOfTheDay));
        quoteOfTheDay.addClassName("quote-of-the-day");
        headerContent.add(quoteOfTheDay);

        Paragraph currentDate = new Paragraph(getCurrentDate());
        currentDate.addClassNames("current-date");
        headerContent.add(currentDate);

        Paragraph workedAndTargetHours = new Paragraph(String.format("You've worked %d hours of %d target hours this month", this.data.workedHours, this.data.targetHours));
        workedAndTargetHours.addClassName("current-info-entry");

        var leftHoursValue = this.data.targetHours - this.data.workedHours;

        var leftHoursText = "";
        var leftHoursClass = "";

        if (leftHoursValue == 0) {
            leftHoursText = "Well done! You've got your target hours ;-)";
        }
        else if (leftHoursValue > 0) {
            leftHoursText = String.format("You still have to work %s hours :-(", leftHoursValue);
            leftHoursClass = "negative";
        }
        else {
            leftHoursText = String.format("Relaaax! You are %s hours over your target! 8-)", Math.abs(leftHoursValue));
            leftHoursClass = "positive";
        }

        Paragraph leftHours = new Paragraph(leftHoursText);
        leftHours.setClassName("current-info-entry");
        if (!leftHoursClass.isEmpty())
            leftHours.addClassNames(leftHoursClass);

        currentInfoLayout.add(workedAndTargetHours, leftHours);

        Div staticInfo = new Div();
        infoLayout.add(staticInfo);

        Label labelOrganization = new Label("Your organization");
        Paragraph organization = new Paragraph(this.data.organization);
        organization.addClassNames("info-entry");

        Label labelPosition = new Label("Your position");
        Paragraph position = new Paragraph(this.data.position);
        position.addClassNames("info-entry");

        Label labelNextWorkingDay = new Label("Your next working day");
        Paragraph nextWorkingDay = new Paragraph(this.data.nextWorkingDay);
        nextWorkingDay.addClassNames("info-entry");

        Label labelHolidaysLeft = new Label(String.format("Days of holiday left in %d", LocalDate.now().getYear()));
        Paragraph holidaysLeft = new Paragraph(Integer.toString(this.data.holidaysLeft));
        holidaysLeft.setClassName("info-entry");

        Button applyForAbsence = new Button("Apply for absence");
        applyForAbsence.setVisible(this.data.holidaysLeft > 0);
        applyForAbsence.setClassName("button-apply-for-absence");
        applyForAbsence.addClickListener(e -> applyAbsence());

        staticInfo.add(
                labelOrganization, organization,
                labelPosition, position,
                labelNextWorkingDay, nextWorkingDay,
                labelHolidaysLeft, holidaysLeft,
                applyForAbsence);


    }

    private VerticalLayout getTimeEntryArea() {
        VerticalLayout timeEntryArea = new VerticalLayout();

        H2 timeEntriesTitle = new H2("Enter your working hours");
        HorizontalLayout timeEntries = new HorizontalLayout();

        TimePicker timePickerWorkStart = new TimePicker();
        timePickerWorkStart.setLabel("Start");
        timePickerWorkStart.addClassName("time-picker");

        TimePicker timePickerWorkEnd = new TimePicker();
        timePickerWorkEnd.setLabel("End");
        timePickerWorkStart.addClassName("time-picker");

        timeEntries.add(timePickerWorkStart, timePickerWorkEnd);

        Button buttonSaveTime = new Button("Save");
        buttonSaveTime.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSaveTime.setEnabled(false);

        timePickerWorkEnd.addValueChangeListener(l -> buttonSaveTime.setEnabled(isPeriodValid(timePickerWorkStart.getValue(), timePickerWorkEnd.getValue(), false)));
        timePickerWorkStart.addValueChangeListener(l -> buttonSaveTime.setEnabled(isPeriodValid(timePickerWorkStart.getValue(), timePickerWorkEnd.getValue(), false)));

        Select<IdNameItem> selectProjects = new Select<>();
        selectProjects.setWidth("100%");
        selectProjects.setItems(data.projects);
        selectProjects.setItemLabelGenerator(IdNameItem::getName);
        selectProjects.setLabel("Project");
        selectProjects.setValue(data.projects.get(0));

        buttonSaveTime.addClickListener(e -> {
            try {
                employeeService.addTimeEntry(LocalDate.now(), timePickerWorkStart.getValue(), timePickerWorkEnd.getValue(), selectProjects.getValue().getId());
                UI.getCurrent().getPage().reload();
            } catch (Exception ex) {
                showNotification(ex.getMessage(), NotificationTypes.Error);
            }
        });

        RouterLink timeEntryView = new RouterLink("Show all time entries", TimeEntryView.class);

        timeEntryArea.add(timeEntriesTitle, timeEntries, selectProjects, buttonSaveTime, timeEntryView);

        return timeEntryArea;
    }

    private void applyAbsence() {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Create new employee");

        VerticalLayout dialogLayout = createAbsenceDialogLayout(dialog);
        dialog.add(dialogLayout);

        dialog.open();
    }

    private VerticalLayout createAbsenceDialogLayout(Dialog dialog) {
        H2 headline = new H2("Enter your absence period");

        DatePicker startDate = new DatePicker("From");
        DatePicker endDate = new DatePicker("To");
        VerticalLayout fieldLayout = new VerticalLayout(startDate, endDate);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        Button applyButton = new Button("Apply", e -> {
            try {
                employeeService.applyAbsence(startDate.getValue(), endDate.getValue());
                dialog.close();
                UI.getCurrent().getPage().reload();
            }
            catch (Exception ex) {
                showNotification(ex.getMessage(), NotificationTypes.Error);
            }
        });

        endDate.addValueChangeListener(l -> applyButton.setEnabled(isPeriodValid(startDate.getValue(), endDate.getValue(), true)));
        startDate.addValueChangeListener(l -> applyButton.setEnabled(isPeriodValid(startDate.getValue(), endDate.getValue(), true)));

        applyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        applyButton.setEnabled(false);
        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, applyButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return dialogLayout;
    }

}
