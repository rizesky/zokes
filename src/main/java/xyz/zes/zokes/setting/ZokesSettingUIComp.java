package xyz.zes.zokes.setting;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ZokesSettingUIComp {
    private final JPanel myMainPanel;
    private final JBTextField intervalTextField = new JBTextField();
    private final JBCheckBox genJokeStatusCheckbox = new JBCheckBox("Generate jokes?");


    public ZokesSettingUIComp() {
        ZokesSettingState settingState = ZokesSettingState.getInstance();
        intervalTextField.setText(String.valueOf(settingState.intervalSeconds));
        genJokeStatusCheckbox.setSelected(settingState.isGenerateJoke);


        intervalTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                intervalTextField.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9');
            }
        });



        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Jokes interval (seconds) : "), intervalTextField, 1, false)
                .addComponent(genJokeStatusCheckbox, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();


    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return intervalTextField;
    }

    @NotNull
    public Integer getInterval() {
        return Integer.valueOf(intervalTextField.getText());
    }

    public void setIntervalTextField(@NotNull String newText) {
        intervalTextField.setText(newText);
    }

    public boolean getGenJokeStatusCheckbox() {
        return genJokeStatusCheckbox.isSelected();
    }

    public void setJokeStatusCheckbox(boolean newStatus) {
        genJokeStatusCheckbox.setSelected(newStatus);
    }
}
