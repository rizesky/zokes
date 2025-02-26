package xyz.zes.zokes.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import xyz.zes.zokes.setting.ZokesSettingState;

import javax.swing.*;
import java.awt.*;

public class NotificationStyleUtil {

    // Create and style a joke notification based on the current settings
    public static Notification createJokeNotification(String title, String setup, String delivery, Project project) {
        ZokesSettingState settings = ZokesSettingState.getInstance();

        // Format content based on display mode
        String content;
        String subtitle = "Enjoy a little break";

        if ("MINIMALISTIC".equals(settings.displayMode)) {
            // True minimalistic mode - significantly more compact
            if (delivery != null && !delivery.isEmpty()) {
                // Two-part joke
                content = "<div style='font-size: 10px; line-height: 1.2; max-width: 200px; white-space: normal; margin: 0; padding: 0;'>"
                        + setup + " <b>" + delivery + "</b></div>";
            } else {
                // Single joke
                content = "<div style='font-size: 10px; line-height: 1.2; max-width: 200px; white-space: normal; margin: 0; padding: 0;'>"
                        + setup + "</div>";
            }
        } else {
            // Normal mode with fun formatting
            // Add some color and styling to make it more playful
            String jokeColor = getRandomPastelColor();
            String deliveryColor = getContrastColor(jokeColor);

            if (delivery != null && !delivery.isEmpty()) {
                // Two-part joke
                content = "<div style='font-size: 13px; line-height: 1.4;'>"
                        + "<span style='color: " + jokeColor + ";'>" + setup + "</span> "
                        + "<b style='color: " + deliveryColor + ";'>" + delivery + "</b></div>";
            } else {
                // Single joke
                content = "<div style='font-size: 13px; line-height: 1.4;'>"
                        + "<span style='color: " + jokeColor + ";'>" + setup + "</span></div>";
            }

            // Apply theme matching for subtitle if enabled (only in normal mode)
            if (settings.matchIDETheme) {
                boolean isDarkTheme = isDarkTheme();
                String subtitleColor = isDarkTheme ? "#A9B7C6" : "#808080";
                subtitle = "<div style='color: " + subtitleColor + ";'>" + subtitle + " 😄</div>";
                content += "<br/><small>" + subtitle + "</small>";
            }
        }

        // Use custom notification display with positioning
        return CustomNotificationDisplay.showCustomPositionNotification(
                project, title, content, NotificationType.INFORMATION);
    }

    // Determine if current theme is dark
    private static boolean isDarkTheme() {
        try {
            // Best approach is to check a key UI color - this works across all IntelliJ versions
            Color panelBg = UIManager.getColor("Panel.background");
            if (panelBg != null) {
                // Determine if it's a dark color by calculating brightness
                float[] hsb = Color.RGBtoHSB(panelBg.getRed(), panelBg.getGreen(), panelBg.getBlue(), null);
                return hsb[2] < 0.5f; // brightness less than 50% suggests a dark theme
            }

            // As a fallback, check if the current LAF name contains "dark"
            String currentLafName = UIManager.getLookAndFeel().getName();
            return currentLafName != null && currentLafName.toLowerCase().contains("dark");
        } catch (Exception e) {
            // Default to false if there's an error
            return false;
        }
    }

    // Get the notification position based on settings
    public static String getNotificationPosition(Project project) {
        ZokesSettingState settings = ZokesSettingState.getInstance();
        return settings.notificationPosition;
    }

    // Get the display time in milliseconds
    public static long getDisplayTimeMillis() {
        ZokesSettingState settings = ZokesSettingState.getInstance();
        return settings.notificationDisplayTime * 1000L;
    }

    // Get a fun random pastel color for jokes
    private static String getRandomPastelColor() {
        String[] funColors = {
                "#FFB6C1", // Light Pink
                "#FFD700", // Gold
                "#98FB98", // Pale Green
                "#87CEFA", // Light Sky Blue
                "#FFA07A", // Light Salmon
                "#DDA0DD", // Plum
                "#FFFACD", // Lemon Chiffon
                "#B0E0E6", // Powder Blue
                "#FFDAB9", // Peach Puff
                "#E6E6FA"  // Lavender
        };

        // Get a random index
        int randomIndex = (int) (Math.random() * funColors.length);
        return funColors[randomIndex];
    }

    // Get a contrasting color for better readability
    private static String getContrastColor(String hexColor) {
        // Parse the hex color
        Color color = Color.decode(hexColor);

        // Calculate brightness
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        // If the color is bright, return a darkened version, otherwise brighten it
        if (hsb[2] > 0.7) {
            return darkenColor(hexColor);
        } else {
            return brightenColor(hexColor);
        }
    }

    // Brighten a color by 20%
    private static String brightenColor(String hexColor) {
        Color color = Color.decode(hexColor);

        int r = Math.min(255, (int)(color.getRed() * 1.2));
        int g = Math.min(255, (int)(color.getGreen() * 1.2));
        int b = Math.min(255, (int)(color.getBlue() * 1.2));

        return String.format("#%02X%02X%02X", r, g, b);
    }

    // Darken a color by 20%
    private static String darkenColor(String hexColor) {
        Color color = Color.decode(hexColor);

        int r = Math.max(0, (int)(color.getRed() * 0.8));
        int g = Math.max(0, (int)(color.getGreen() * 0.8));
        int b = Math.max(0, (int)(color.getBlue() * 0.8));

        return String.format("#%02X%02X%02X", r, g, b);
    }
}