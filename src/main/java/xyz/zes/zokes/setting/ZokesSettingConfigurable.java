package xyz.zes.zokes.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static xyz.zes.zokes.setting.Constant.*;

public class ZokesSettingConfigurable implements Configurable {
    private ZokesSettingUIComp settingUIComp;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Zokes Setting";
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return settingUIComp.getPreferredFocusedComponent();
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingUIComp = new ZokesSettingUIComp();
        return settingUIComp.getPanel();
    }

    @Override
    public boolean isModified() {
        ZokesSettingState settingState = ZokesSettingState.getInstance();
        return settingUIComp.getGenJokeStatusCheckbox() != settingState.isGenerateJoke
                || !settingUIComp.getInterval().equals(settingState.intervalSeconds)
                || !settingUIComp.getEnabledCategories().equals(settingState.enabledCategories)
                || !settingUIComp.getDisplayMode().equals(settingState.displayMode)
                || !settingUIComp.getNotificationPosition().equals(settingState.notificationPosition)
                || !settingUIComp.getJokeType().equals(settingState.jokeType)
                || settingUIComp.getMatchThemeCheckbox() != settingState.matchIDETheme
                || settingUIComp.getDisplayTime() != settingState.notificationDisplayTime
                // Add smart timing checks
                || settingUIComp.getSmartTimingCheckbox() != settingState.useSmartTiming
                || settingUIComp.getFocusThreshold() != settingState.focusThresholdSeconds
                || settingUIComp.getBreakThreshold() != settingState.breakThresholdSeconds
                || settingUIComp.getPrioritizeBreaksCheckbox() != settingState.prioritizeBreaks;
    }

    @Override
    public void apply() throws ConfigurationException {
        ZokesSettingState settingState = ZokesSettingState.getInstance();
        settingState.isGenerateJoke = settingUIComp.getGenJokeStatusCheckbox();

        int newInterval = settingUIComp.getInterval();
        if(settingState.intervalSeconds != newInterval){
            if(newInterval > MAX_INTERVAL_SECONDS || newInterval < MIN_INTERVAL_SECONDS){
                throw new ConfigurationException("Jokes interval must be between " + MIN_INTERVAL_SECONDS + " and  " + MAX_INTERVAL_SECONDS + " seconds");
            }
            settingState.intervalSeconds = settingUIComp.getInterval();
        }

        // Apply category settings
        settingState.enabledCategories = settingUIComp.getEnabledCategories();

        // Apply UI settings
        settingState.displayMode = settingUIComp.getDisplayMode();
        settingState.notificationPosition = settingUIComp.getNotificationPosition();
        settingState.jokeType = settingUIComp.getJokeType();
        settingState.matchIDETheme = settingUIComp.getMatchThemeCheckbox();

        // Validate and apply display time
        int newDisplayTime = settingUIComp.getDisplayTime();
        if (newDisplayTime < MIN_DISPLAY_TIME || newDisplayTime > MAX_DISPLAY_TIME) {
            throw new ConfigurationException("Display time must be between " + MIN_DISPLAY_TIME + " and " + MAX_DISPLAY_TIME + " seconds");
        }
        settingState.notificationDisplayTime = newDisplayTime;


        // Apply smart timing settings
        settingState.useSmartTiming = settingUIComp.getSmartTimingCheckbox();

        // Validate and apply focus threshold
        int newFocusThreshold = settingUIComp.getFocusThreshold();
        if (newFocusThreshold < 10) {
            throw new ConfigurationException("Focus threshold must be at least 10 seconds");
        }
        if (newFocusThreshold > 600) {
            throw new ConfigurationException("Focus threshold cannot exceed 600 seconds (10 minutes)");
        }
        settingState.focusThresholdSeconds = newFocusThreshold;

        // Validate and apply break threshold
        int newBreakThreshold = settingUIComp.getBreakThreshold();
        if (newBreakThreshold < 10) {
            throw new ConfigurationException("Break threshold must be at least 10 seconds");
        }
        if (newBreakThreshold > 300) {
            throw new ConfigurationException("Break threshold cannot exceed 300 seconds (5 minutes)");
        }
        settingState.breakThresholdSeconds = newBreakThreshold;

        settingState.prioritizeBreaks = settingUIComp.getPrioritizeBreaksCheckbox();
    }

    @Override
    public void disposeUIResources() {
        settingUIComp = null;
    }
}