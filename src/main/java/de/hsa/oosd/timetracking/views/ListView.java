package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;

import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.CustomUser;
import de.hsa.oosd.timetracking.entities.CustomUserDetailsService;


import java.util.ArrayList;

import java.util.List;

@Route(value = "listview", layout = MainLayout.class)
@PageTitle("Employees")
public class ListView extends VerticalLayout {
    final Grid<CustomUser> grid = new Grid<>(CustomUser.class);
    final TextField filterText = new TextField();
    final CustomUserDetailsService service;


    public ListView(CustomUserDetailsService customUserDetailsService) {
        this.service = customUserDetailsService;
        final HorizontalLayout headerLayout = new HorizontalLayout();
        final VerticalLayout bodyLayout = new VerticalLayout();
        VerticalLayout headerContent = new VerticalLayout();
        headerLayout.add(headerContent);

        Paragraph title = new Paragraph("Find your colleagues");
        title.setClassName("header-view");
        headerContent.add(title);

        Paragraph text = new Paragraph("For detailed information click on User Profile button");
        text.addClassName("simple-text");
        headerContent.add(text);
        add(headerContent);

        addClassName("list-view");
        setSizeFull();
        updateList();

        bodyLayout.add(getToolbar());
        bodyLayout.add(configureGrid());
        bodyLayout.setWidth("100%");
        bodyLayout.setHeight("100%");
        add(bodyLayout);
    }

    private Grid configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("username", "role");
        grid.addComponentColumn(item -> {
            Button editButton = new Button("User Profile");
            editButton.addClickListener(e -> UI.getCurrent().navigate(EmployeeDetailsView.class, item.getUsername()));
            return editButton;
        });
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        return grid;

    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by username...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());


        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private void updateList() {
        List<CustomUser> userl = new ArrayList<>();
        List<CustomUser> list = service.getAllUsersFromOrganization();
        for (CustomUser user  :list) {
            if (user.getUsername().contains(filterText.getValue())){
                userl.add((user));
            }
        }
        if (filterText.getValue().equals("")){
            userl = list;
        }
      grid.setItems(userl);
    }
}