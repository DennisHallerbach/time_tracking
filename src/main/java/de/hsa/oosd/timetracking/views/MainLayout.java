package de.hsa.oosd.timetracking.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import de.hsa.oosd.timetracking.security.SecurityService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;


@CssImport("./styles/main.css")
public class MainLayout extends AppLayout {
    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 title = new H1("Awesome Time Tracking");
        title.setClassName("title");

        Button logout = new Button("Logout", e -> securityService.logout());
        logout.setClassName("button-logout");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), title, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setClassName("header");

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);
        dashboardLink.setHighlightCondition(HighlightConditions.sameLocation());
        dashboardLink.setClassName("router-link");

        RouterLink timeEntryLink = new RouterLink("Time Entries", TimeEntryView.class);
        timeEntryLink.setHighlightCondition(HighlightConditions.sameLocation());
        timeEntryLink.setClassName("router-link");

        RouterLink contactsLink = new RouterLink("Contacts", ListView.class);
        contactsLink.setHighlightCondition(HighlightConditions.sameLocation());
        contactsLink.setClassName("router-link");

        RouterLink profileLink = new RouterLink("Profile", ProfilView.class);
        profileLink.setHighlightCondition(HighlightConditions.always());
        profileLink.setClassName("router-link");

        RouterLink createUsersLink = new RouterLink("Manage Users", CreateUserAdminVew.class);
        createUsersLink.setHighlightCondition(HighlightConditions.sameLocation());
        createUsersLink.setClassName("router-link");

        RouterLink projectsLink = new RouterLink("Projects", ProjectPanelView.class);
        projectsLink.setHighlightCondition(HighlightConditions.sameLocation());
        projectsLink.setClassName("router-link");

        RouterLink employeeInfoLink = new RouterLink("Employee Info", EmployeeInfoAdminView.class);
        employeeInfoLink.setHighlightCondition(HighlightConditions.sameLocation());
        employeeInfoLink.setClassName("router-link");

        var sideBar = new VerticalLayout();
        sideBar.setClassName("side-bar");

        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Object[] auth = authorities.toArray();
        if (auth[0].toString().equals("Role_Admin")){
            var adminPanelLabel = new Label("Admin Functions");
            adminPanelLabel.setClassName("admin-panel-label");

            var adminPanel = new VerticalLayout(
                    adminPanelLabel,
                    createUsersLink,
                    projectsLink,
                    employeeInfoLink
            );
            adminPanel.setClassName("admin-panel");
            sideBar.add(adminPanel);
        }

        var userPanel = new VerticalLayout(
                dashboardLink,
                timeEntryLink,
                profileLink,
                contactsLink
        );
        userPanel.setClassName("user-panel");
        sideBar.add(userPanel);

        addToDrawer(sideBar);
    }
}
