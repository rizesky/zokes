package xyz.zes.zokes.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@State(
        name = "org.intellij.sdk.settings.AppSettingsState",
        storages = @Storage("ZokesSettings.xml")
)
public class ZokesSettingState implements PersistentStateComponent<ZokesSettingState> {

    public boolean isGenerateJoke = true;
    public int intervalSeconds = Constant.MIN_INTERVAL_SECONDS;
    // Default to all categories enabled
    public Set<String> enabledCategories = new HashSet<>(Arrays.asList(
            "programming", "pun", "misc", "dark", "spooky", "christmas"
    ));

    // UI Settings
    public String displayMode = "NORMAL"; // Options: NORMAL, MINIMALISTIC
    public String notificationPosition = "BOTTOM_RIGHT"; // Options: TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT
    public boolean matchIDETheme = true;
    public int notificationDisplayTime = 5; // In seconds

    // Joke type settings
    public String jokeType = "Any"; // Options: Any, single, twopart

    public static ZokesSettingState getInstance(){
        return ApplicationManager.getApplication().getService(ZokesSettingState.class);
    }

    @Override
    public @Nullable ZokesSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ZokesSettingState state) {
        XmlSerializerUtil.copyBean(state,this);
    }
}