package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.*;




import java.util.List;

// Editor by https://vaadin.com/docs/latest/ds/components/grid/#inline-editing-java-only
@Route(value = "ProjectPanel", layout = MainLayout.class)
@CssImport("./styles/views.css")
public class ProjectPanelView extends VerticalLayout {
    private final Grid<Project> UserGrid = new Grid<>(Project.class,false);
    private List<Project> ProjectList;
    private final ProjectService projectService;


    public ProjectPanelView(ProjectService projectService) {
        this.projectService = projectService;


        buildGrid();
        ProjectList = projectService.getAllProjectsFromOrganization();
        UserGrid.setItems(ProjectList);
    }

    private void buildGrid(){
        UserGrid.setColumnReorderingAllowed(true);
        Editor <Project> editor = UserGrid.getEditor();
        Grid.Column<Project> ProjectNameColumn = UserGrid
                .addColumn(Project::getName).setHeader("Project name")
                .setWidth("40%").setFlexGrow(0);
        UserGrid.addColumn(Project::getOrganization)
                .setHeader("Organization")
                .setWidth("20%");


        Grid.Column<Project> editColumn = UserGrid.addComponentColumn(item -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> {
                if (editor.isOpen())
                    editor.cancel();
                UserGrid.getEditor().editItem(item);
            });
            return editButton;
        }).setWidth("150px").setFlexGrow(0);

        Binder<Project> binder = new Binder<>(Project.class);
        editor.setBinder(binder);
        editor.setBuffered(true);



        TextField ProjectnameColumn = new TextField();
        ProjectnameColumn.setWidthFull();
        binder.forField(ProjectnameColumn)
                .asRequired("Projectname must not be empty")
                .bind(Project::getName, Project::setName);
        ProjectNameColumn.setEditorComponent(ProjectnameColumn);

        Button saveButton = new Button("Save", e -> {
            Project user = editor.getItem();
            try {
                binder.writeBean(user);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
            projectService.save(user);
            editor.save();

        });
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        add(buildForm(),
                UserGrid);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);

        UserGrid.addComponentColumn(item -> new Button("Delete", click -> {
            this.projectService.delete(item);
            ProjectList = projectService.getAllProjectsFromOrganization();
            UserGrid.setItems(ProjectList);
        })).setWidth("150px").setFlexGrow(0);
    }
    private Component buildForm() {
        TextField projectname = new TextField("Projectname");

        Button saveButton = new Button("Save");
        Div errorsLayout = new Div();
        Binder<Project> binder = new Binder<>();
        // Configure UI components
        saveButton.setThemeName("primary");
        binder.bind(projectname, Project::getName, Project::setName);
        saveButton.addClickListener(click -> {
            try {
                errorsLayout.setText("");
                Project project = new Project();
                binder.writeBean(project);
                addProject(project);
            } catch (Exception e){
                errorsLayout.add(new Paragraph(e.getMessage()));
            }
        });

        // Wrap components in layouts (3)
        //   HorizontalLayout formLayout = new HorizontalLayout(dateField, startTimeField, endTimeField, typeOfWorkSelect, nameOfProjectSelect, saveButton);
        HorizontalLayout formLayout = new HorizontalLayout(projectname, saveButton);
        Div wrapperLayout = new Div(formLayout, errorsLayout);
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        wrapperLayout.setWidth("100%");

        return wrapperLayout;
    }

    private void addProject(Project project) throws Exception {
        projectService.createProject(project.getName());
        ProjectList = projectService.getAllProjectsFromOrganization();
        UserGrid.setItems(ProjectList);
    }
}

