package xyz.zes.zokes.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static xyz.zes.zokes.setting.Constant.MAX_INTERVAL_SECONDS;
import static xyz.zes.zokes.setting.Constant.MIN_INTERVAL_SECONDS;

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
        return
                settingUIComp.getGenJokeStatusCheckbox()!= settingState.isGenerateJoke
                        || !settingUIComp.getInterval().equals(settingState.intervalSeconds)
        ;
    }

    @Override
    public void apply() throws ConfigurationException {
        ZokesSettingState settingState = ZokesSettingState.getInstance();
        settingState.isGenerateJoke = settingUIComp.getGenJokeStatusCheckbox();

        int newInterval = settingUIComp.getInterval();
        if(settingState.intervalSeconds!=newInterval){
            if(newInterval> MAX_INTERVAL_SECONDS || newInterval< MIN_INTERVAL_SECONDS){
                throw new ConfigurationException("Jokes interval must be between " + MIN_INTERVAL_SECONDS + " and  " + MAX_INTERVAL_SECONDS + " seconds");
            }
            settingState.intervalSeconds = settingUIComp.getInterval();
        }


    }

    @Override
    public void disposeUIResources() {
        settingUIComp=null;
    }
}
