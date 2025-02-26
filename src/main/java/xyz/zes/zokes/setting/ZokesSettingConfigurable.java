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
                || settingUIComp.getDisplayTime() != settingState.notificationDisplayTime;
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
    }

    @Override
    public void disposeUIResources() {
        settingUIComp = null;
    }
}