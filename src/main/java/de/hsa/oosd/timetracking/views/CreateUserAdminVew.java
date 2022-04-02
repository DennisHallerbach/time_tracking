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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import de.hsa.oosd.timetracking.entities.CustomUser;
import de.hsa.oosd.timetracking.entities.CustomUserDetailsService;
import de.hsa.oosd.timetracking.security.SecurityService;

import java.util.List;
// Editor by https://vaadin.com/docs/latest/ds/components/grid/#inline-editing-java-only
@Route(value = "CreateUsers", layout = MainLayout.class)
@CssImport("./styles/views.css")
public class CreateUserAdminVew extends VerticalLayout {
        private final Grid<CustomUser> UserGrid = new Grid<>(CustomUser.class,false);
        private List<CustomUser> USerList;
        private final CustomUserDetailsService userDetailsService;
        private final SecurityService securityService;

        public CreateUserAdminVew(CustomUserDetailsService userDetailsService,SecurityService securityService) {
            this.userDetailsService = userDetailsService;
            this.securityService = securityService;

            buildGrid();
            USerList = userDetailsService.getAllUsersFromOrganization();
            UserGrid.setItems(USerList);
        }

        private void buildGrid(){
            UserGrid.setColumnReorderingAllowed(true);
            Editor <CustomUser> editor = UserGrid.getEditor();
            Grid.Column<CustomUser> UsernameColumn = UserGrid
                    .addColumn(CustomUser::getUsername).setHeader("Username")
                    .setWidth("200px").setFlexGrow(0);
            Grid.Column<CustomUser> RoleColumn = UserGrid.addColumn(CustomUser::getRole)
                    .setHeader("Role").setWidth("200px").setFlexGrow(0);
            UserGrid.addColumn(CustomUser::getOrganization)
                    .setHeader("Organization")
                    .setWidth("30%");


            Grid.Column<CustomUser> editColumn = UserGrid.addComponentColumn(item -> {
                Button editButton = new Button("Edit");
                editButton.addClickListener(e -> {
                    if (editor.isOpen())
                        editor.cancel();
                    UserGrid.getEditor().editItem(item);
                });
                return editButton;
            }).setWidth("150px").setFlexGrow(0);

            Binder<CustomUser> binder = new Binder<>(CustomUser.class);
            editor.setBinder(binder);
            editor.setBuffered(true);



            TextField UsernameColumnField = new TextField();
            UsernameColumnField.setWidthFull();
            binder.forField(UsernameColumnField)
                    .asRequired("Username must not be empty")
                    .bind(CustomUser::getUsername, CustomUser::setUsername);
            UsernameColumn.setEditorComponent(UsernameColumnField);
            Select<String> RoleSelect = new Select<>("Role_Admin", "Role_User");
            RoleSelect.setWidthFull();
            binder.forField(RoleSelect)
                    .asRequired("Role must not be empty")
                    .bind(CustomUser::getRole, CustomUser::setRole);
            RoleColumn.setEditorComponent(RoleSelect);

            Button saveButton = new Button("Save", e -> {
                CustomUser user = editor.getItem();
                try {
                    binder.writeBean(user);
                }catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
                userDetailsService.save(user);
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
                this.userDetailsService.delete(item);
                USerList = userDetailsService.getAllUsersFromOrganization();
                UserGrid.setItems(USerList);
            })).setWidth("150px").setFlexGrow(0);
        }
        private Component buildForm() {
            TextField username = new TextField("Username");
            PasswordField passwordField = new PasswordField("Password");
            Select<String> Role = new Select<>("Role_Admin", "Role_User");

            Button saveButton = new Button("Save");
            Div errorsLayout = new Div();
            Binder<CustomUser> binder = new Binder<>();
            // Configure UI components
            saveButton.setThemeName("primary");
            binder.bind(username, CustomUser::getUsername, CustomUser::setUsername);
            binder.bind(passwordField, CustomUser::getPassword, CustomUser::setPassword);
            binder.bind(Role, CustomUser::getRole, CustomUser::setRole);
            saveButton.addClickListener(click -> {
                try {
                    errorsLayout.setText("");
                    CustomUser user = new CustomUser();
                    binder.writeBean(user);
                    addUser(user);
                } catch (Exception e){
                    errorsLayout.add(new Paragraph(e.getMessage()));
                }
            });

            // Wrap components in layouts (3)
            //   HorizontalLayout formLayout = new HorizontalLayout(dateField, startTimeField, endTimeField, typeOfWorkSelect, nameOfProjectSelect, saveButton);
            HorizontalLayout formLayout = new HorizontalLayout(username, passwordField, Role, saveButton);
            Div wrapperLayout = new Div(formLayout, errorsLayout);
            formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
            wrapperLayout.setWidth("100%");

            return wrapperLayout;
        }

        private void addUser(CustomUser user) throws Exception {
            String OrgaUsername = securityService.getAuthenticatedUser().getUsername();
            CustomUser userforOrgname = userDetailsService.loadCustomUserByUsername(OrgaUsername);
            userDetailsService.createUser(user.getUsername(), user.getPassword(), user.getRole(), userforOrgname.getOrganization().getName());
            USerList = userDetailsService.getAllUsersFromOrganization();
            UserGrid.setItems(USerList);
        }
    }

