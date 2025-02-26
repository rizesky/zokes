package xyz.zes.zokes.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import xyz.zes.zokes.setting.ZokesSettingState;

import javax.swing.*;
import java.awt.*;

public class CustomNotificationDisplay {

    public static Notification showCustomPositionNotification(
            Project project, String title, String content, NotificationType type) {

        // Create notification using the proper API
        NotificationGroup notifGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup("Zokes");
        Notification notification = notifGroup.createNotification(content, type);
        notification.setTitle(title);

        // Get position setting
        String positionSetting = ZokesSettingState.getInstance().notificationPosition;

        // Show in custom position
        SwingUtilities.invokeLater(() -> {
            try {
                IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(project);
                if (ideFrame == null) return;

                JComponent component = ideFrame.getComponent();
                Dimension size = component.getSize();
                Point targetPoint = new Point();

                // Set point based on position setting
                switch (positionSetting) {
                    case "TOP_RIGHT":
                        targetPoint.setLocation(size.width - 10, 10);
                        break;
                    case "TOP_LEFT":
                        targetPoint.setLocation(10, 10);
                        break;
                    case "BOTTOM_LEFT":
                        targetPoint.setLocation(10, size.height - 10);
                        break;
                    case "BOTTOM_RIGHT":
                    default:
                        targetPoint.setLocation(size.width - 10, size.height - 10);
                        break;
                }

                // Determine background color based on theme
                Color backgroundColor = JBColor.background();

                // Create balloon with notification content
                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder(content, null, backgroundColor, null)
                        .setTitle(title)
                        .setFadeoutTime(NotificationStyleUtil.getDisplayTimeMillis())
                        .createBalloon()
                        .show(new RelativePoint(component, targetPoint),
                                positionToBalloonPosition(positionSetting));

            } catch (Exception e) {
                // Fallback to standard notification if custom positioning fails
                notification.notify(project);
            }
        });

        return notification;
    }

    private static Balloon.Position positionToBalloonPosition(String position) {
        switch (position) {
            case "TOP_RIGHT":
                return Balloon.Position.atRight;
            case "TOP_LEFT":
                return Balloon.Position.atLeft;
            case "BOTTOM_LEFT":
                return Balloon.Position.below;  // Use standard positions
            case "BOTTOM_RIGHT":
            default:
                return Balloon.Position.below;  // Use standard positions
        }
    }
}