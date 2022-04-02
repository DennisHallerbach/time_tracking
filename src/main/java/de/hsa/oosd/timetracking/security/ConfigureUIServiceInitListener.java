package de.hsa.oosd.timetracking.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import de.hsa.oosd.timetracking.views.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
    public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::authenticateNavigation);
            ui.addBeforeEnterListener(this::checkAdmin);
        });
    }
    private void checkAdmin(BeforeEnterEvent event){
        if(CreateUserAdminVew.class.equals((event.getNavigationTarget()))|| ProjectPanelView.class.equals((event.getNavigationTarget()))|| EmployeeInfoAdminView.class.equals((event.getNavigationTarget()))){
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            Object[] auth = authorities.toArray();
            if (!auth[0].toString().equals("Role_Admin")){
                event.forwardTo(DashboardView.class);

            }
        }
    }
    private void authenticateNavigation(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget())
                && !SecurityUtils.isUserLoggedIn()) {
            if (!RegisterView.class.equals(event.getNavigationTarget())) {
                event.rerouteTo(LoginView.class);
            }
        }


    }
}