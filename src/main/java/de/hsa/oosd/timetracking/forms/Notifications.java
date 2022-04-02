package de.hsa.oosd.timetracking.forms;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class Notifications {
    public static void showNotification(String text) {
        showNotification(text, 5, NotificationTypes.Info);
    }

    public static void showNotification(String text, NotificationTypes type) {
        showNotification(text, 5, type);
    }

    public static void showNotification(String text, int duration, NotificationTypes type) {
        Notification notification = Notification.show(text);
        switch (type) {
            case Info:
                break;
            case Error:
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                break;
            case Contrast:
                notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                break;
            case Success:
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                break;
        }
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(duration * 1000);
    }
}

