package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.EmployeeService;
import de.hsa.oosd.timetracking.entities.Project;
import de.hsa.oosd.timetracking.entities.ProjectService;
import de.hsa.oosd.timetracking.entities.TimeEntry;
import de.hsa.oosd.timetracking.forms.NotificationTypes;
import de.hsa.oosd.timetracking.forms.Notifications;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static de.hsa.oosd.timetracking.forms.Notifications.showNotification;
import static de.hsa.oosd.timetracking.utils.DateTimePeriodUtils.isPeriodValid;
// Editor by https://vaadin.com/docs/latest/ds/components/grid/#inline-editing-java-only

@Route(value = "timeentry", layout = MainLayout.class)
@CssImport("./styles/views.css")
public class TimeEntryView extends VerticalLayout {
    private final Grid<TimeEntry> timeEntryGrid = new Grid<>(TimeEntry.class, false);
    private  List<TimeEntry> timeEntryList;
    private final EmployeeService employeeService;
    private final ProjectService projectService;



    public TimeEntryView(
            EmployeeService employeeService,
            ProjectService projectService) {
        this.employeeService = employeeService;
        this.projectService = projectService;

        VerticalLayout headerContent = new VerticalLayout();
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.add(headerContent);

        Paragraph title = new Paragraph("Track your time");
        title.setClassName("header-view");
        headerContent.add(title);
        add(headerContent);

        HorizontalLayout bodyLayout = new HorizontalLayout();
        bodyLayout.add(buildGrid());
        bodyLayout.setHeight("100%");
        bodyLayout.setWidth("100%");
        try {
            timeEntryList = employeeService.getAllTimeEntriesByEmployee();
            timeEntryGrid.setItems(timeEntryList);
        } catch (Exception e) {
            showNotification(e.getMessage(), NotificationTypes.Error);
        }

        add(bodyLayout);
    }

    private Grid<TimeEntry> buildGrid() {
        timeEntryGrid.setColumnReorderingAllowed(true);

        Editor<TimeEntry> editor = timeEntryGrid.getEditor();
        Grid.Column<TimeEntry> dateOfWorkColumn = timeEntryGrid
                .addColumn(TimeEntry::getDateOfWork).setHeader("Date")
                .setWidth("150px").setFlexGrow(0);
        Grid.Column<TimeEntry> startTimeColumn = timeEntryGrid.addColumn(TimeEntry::getStartTime)
                .setHeader("Start time")
                .setWidth("120px").setFlexGrow(0);
        Grid.Column<TimeEntry> endTimeColumn = timeEntryGrid.addColumn(TimeEntry::getEndTime)
                .setHeader("End time")
                .setWidth("120px").setFlexGrow(0);
        Grid.Column<TimeEntry> projectColumn = timeEntryGrid.addColumn(TimeEntry::getProjectName)
                .setHeader("Project")
                .setWidth("300px").setFlexGrow(0);

        Grid.Column<TimeEntry> editColumn = timeEntryGrid.addComponentColumn(entry -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                timeEntryGrid.getEditor().editItem(entry);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        Binder<TimeEntry> binder = new Binder<>(TimeEntry.class);
        editor.setBinder(binder);
        editor.setBuffered(true);

        DatePicker dateField = new DatePicker();
        dateField.setWidth("120px");
        binder.forField(dateField)
                .asRequired("Date is required")
                .bind(TimeEntry::getDateOfWork, TimeEntry::setDateOfWork);
        dateOfWorkColumn.setEditorComponent(dateField);

        TimePicker startTimeField = new TimePicker();
        startTimeField.setWidth("80px");
        startTimeField.setStep(Duration.ofMinutes(30));
        binder.forField(startTimeField)
                        .asRequired("Start time is required")
                                .bind(TimeEntry::getStartTime, TimeEntry::setStartTime);
        startTimeColumn.setEditorComponent(startTimeField);

        TimePicker endTimeField = new TimePicker();
        endTimeField.setWidth("80px");
        endTimeField.setStep(Duration.ofMinutes(30));
        binder.forField(endTimeField)
                .asRequired("End time is required")
                .bind(TimeEntry::getEndTime, TimeEntry::setEndTime);
        endTimeColumn.setEditorComponent(endTimeField);

        Select<Project> projectSelection = new Select<>();

        projectSelection.setItems(projectService.getAllProjectsFromOrganization());

        binder.forField(projectSelection)
                        .bind(TimeEntry::getProject, TimeEntry::setProject);
        projectColumn.setEditorComponent(projectSelection);

        Button saveButton = new Button("Save", e -> {
            TimeEntry entry = editor.getItem();
            try {
                binder.writeBean(entry);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
            try {
                employeeService.updateTimeEntry(entry);
                editor.save();
            } catch (Exception ex) {
                editor.cancel();
                //TODO if time entry was not updated, then old time entry must be showed
                Notifications.showNotification(ex.getMessage(), NotificationTypes.Contrast);
            }
        });

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        add(buildForm(),
                timeEntryGrid);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        timeEntryGrid.addComponentColumn(item -> new Button("Delete", click -> {
            this.employeeService.deleteTimeEntry(item.getId());
            try {
                timeEntryList = employeeService.getAllTimeEntriesByEmployee();
            }catch (Exception e){
                showNotification(e.getMessage(), NotificationTypes.Error);
            }
            timeEntryGrid.setItems(timeEntryList);
        })).setWidth("150px").setFlexGrow(0);
        return timeEntryGrid;
    }

    private Component buildForm() {
        Div errorsLayout = new Div();
        // Create UI components (2)
        DatePicker dateField = new DatePicker();
        dateField.setLabel("Date");

        TimePicker startTimeField = new TimePicker();
        startTimeField.setLabel("From");
        startTimeField.setStep(Duration.ofMinutes(30));

        TimePicker endTimeField = new TimePicker();
        endTimeField.setLabel("To");
        endTimeField.setStep(Duration.ofMinutes(30));

          Select<Project> project = new Select<>();
        project.setItems(projectService.getAllProjectsFromOrganization());
        project.setPlaceholder("Choose Project");
        Button saveButton = new Button("Save");

        // Configure UI components
        saveButton.setThemeName("primary");
        saveButton.setEnabled(false);

        dateField.addValueChangeListener(l -> saveButton.setEnabled(isDateAndTimeValid(dateField, startTimeField, endTimeField)));
        startTimeField.addValueChangeListener(l -> saveButton.setEnabled(isDateAndTimeValid(dateField, startTimeField, endTimeField)));
        endTimeField.addValueChangeListener(l -> saveButton.setEnabled(isDateAndTimeValid(dateField, startTimeField, endTimeField)));

        startTimeField.addValueChangeListener(l -> validateTimeFields(startTimeField, endTimeField));
        endTimeField.addValueChangeListener(l -> validateTimeFields(startTimeField, endTimeField));

        Binder<TimeEntry> binder = new Binder<>(TimeEntry.class);
        binder.forField(dateField)
                .asRequired("Date is required")
                .bind(TimeEntry::getDateOfWork, TimeEntry::setDateOfWork);

        binder.forField(startTimeField).asRequired("Start time is required").bind(TimeEntry::getStartTime, TimeEntry::setStartTime);

        binder.forField(endTimeField).asRequired("End time is required").bind(TimeEntry::getEndTime, TimeEntry::setEndTime);

        binder.bind(project, TimeEntry::getProject, TimeEntry::setProject);

        binder.readBean(new TimeEntry());

        saveButton.addClickListener(click -> {
            try {
                errorsLayout.setText("");
                TimeEntry savedTimeEntry = new TimeEntry();
                binder.writeBean(savedTimeEntry);
                addTimeEntry(savedTimeEntry);
                binder.readBean(new TimeEntry());
            } catch (ValidationException e) {
                errorsLayout.add(new Html(e.getValidationErrors().stream()
                        .map(res -> "<p>" + res.getErrorMessage() + "</p>")
                        .collect(Collectors.joining("\n"))));
            }catch (Exception e){
                errorsLayout.add(new Paragraph(e.getMessage()));
            }
        });

        // Wrap components in layouts (3)
        HorizontalLayout formLayout = new HorizontalLayout(dateField, startTimeField, endTimeField, project, saveButton);
        Div wrapperLayout = new Div(formLayout, errorsLayout);
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        wrapperLayout.setWidth("100%");

        return wrapperLayout;
    }

    private void addTimeEntry(TimeEntry timeEntry) throws Exception {
        try {
            employeeService.addTimeEntry(timeEntry.getDateOfWork(), timeEntry.getStartTime(), timeEntry.getEndTime(),
                    timeEntry.getProject() != null ? timeEntry.getProject().getId() : -1);

        }
        catch (Exception ex) {
            showNotification(ex.getMessage(), NotificationTypes.Error);
        }
        timeEntryList = employeeService.getAllTimeEntriesByEmployee();
        timeEntryGrid.setItems(timeEntryList);
    }

    private void validateTimeFields(TimePicker startTimePicker, TimePicker endTimePicker) {
        var isValid = startTimePicker.isEmpty() || endTimePicker.isEmpty() || startTimePicker.getValue().isBefore(endTimePicker.getValue());
        startTimePicker.setInvalid(!isValid);
        endTimePicker.setInvalid(!isValid);
    }

    private boolean isDateAndTimeValid(DatePicker dateField, TimePicker startTimeField, TimePicker endTimeField) {
        return dateField.getValue() != null && isPeriodValid(startTimeField.getValue(), endTimeField.getValue(), false);
    }
}
