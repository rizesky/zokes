package xyz.zes.zokes.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
        name = "org.intellij.sdk.settings.AppSettingsState",
        storages = @Storage("ZokesSettings.xml")
)
public class ZokesSettingState implements PersistentStateComponent<ZokesSettingState> {

    public boolean isGenerateJoke = true;
    public int intervalSeconds = Constant.MIN_INTERVAL_SECONDS;

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
