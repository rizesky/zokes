package xyz.zes.zokes.setting;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static xyz.zes.zokes.setting.Constant.*;

public class ZokesSettingUIComp {
    private final JPanel myMainPanel;
    private final JBTextField intervalTextField = new JBTextField();
    private final JBCheckBox genJokeStatusCheckbox = new JBCheckBox("🔄 Generate jokes?");

    // Category checkboxes
    private final Map<String, JBCheckBox> categoryCheckboxes = new HashMap<>();

    // UI Components
    private final JComboBox<String> displayModeComboBox = new JComboBox<>();
    private final JComboBox<String> positionComboBox = new JComboBox<>();
    private final JComboBox<String> jokeTypeComboBox = new JComboBox<>();
    private final JBCheckBox matchThemeCheckbox = new JBCheckBox("🎨 Match IDE theme");
    private final JBTextField displayTimeTextField = new JBTextField();

    public ZokesSettingUIComp() {
        ZokesSettingState settingState = ZokesSettingState.getInstance();
        intervalTextField.setText(String.valueOf(settingState.intervalSeconds));
        genJokeStatusCheckbox.setText("🔄 Generate jokes?");
        genJokeStatusCheckbox.setSelected(settingState.isGenerateJoke);

        // Set up display mode combo box
        for (String mode : DISPLAY_MODES) {
            displayModeComboBox.addItem(DISPLAY_MODE_NAMES.getOrDefault(mode, mode));
        }
        displayModeComboBox.setSelectedItem(DISPLAY_MODE_NAMES.getOrDefault(settingState.displayMode, settingState.displayMode));

        // Set up position combo box
        for (String position : NOTIFICATION_POSITIONS) {
            positionComboBox.addItem(POSITION_NAMES.getOrDefault(position, position));
        }
        positionComboBox.setSelectedItem(POSITION_NAMES.getOrDefault(settingState.notificationPosition, settingState.notificationPosition));

        // Set up joke type combo box
        for (String jokeType : JOKE_TYPES) {
            jokeTypeComboBox.addItem(JOKE_TYPE_NAMES.getOrDefault(jokeType, jokeType));
        }
        jokeTypeComboBox.setSelectedItem(JOKE_TYPE_NAMES.getOrDefault(settingState.jokeType, settingState.jokeType));

        // Set up other UI components
        matchThemeCheckbox.setText("🎨 Match IDE theme");
        matchThemeCheckbox.setSelected(settingState.matchIDETheme);
        displayTimeTextField.setText(String.valueOf(settingState.notificationDisplayTime));

        // Create category panel with fun emoji
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new GridLayout(0, 2));
        categoriesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🤣 Joke Categories", TitledBorder.LEFT, TitledBorder.TOP));

        // Add category checkboxes with emoji
        addCategoryCheckbox(categoriesPanel, "💻 Programming", "programming", settingState);
        addCategoryCheckbox(categoriesPanel, "😏 Pun", "pun", settingState);
        addCategoryCheckbox(categoriesPanel, "🎭 Misc", "misc", settingState);
        addCategoryCheckbox(categoriesPanel, "🌚 Dark", "dark", settingState);
        addCategoryCheckbox(categoriesPanel, "👻 Spooky", "spooky", settingState);
        addCategoryCheckbox(categoriesPanel, "🎄 Christmas", "christmas", settingState);

        // Create joke style panel
        JPanel jokeStylePanel = new JPanel();
        jokeStylePanel.setLayout(new GridLayout(0, 2));
        jokeStylePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🎭 Joke Style", TitledBorder.LEFT, TitledBorder.TOP));

        // Add joke type selector
        jokeStylePanel.add(new JBLabel("📝 Joke Type:"));
        jokeStylePanel.add(jokeTypeComboBox);

        // Create appearance panel with fun emoji
        JPanel appearancePanel = new JPanel();
        appearancePanel.setLayout(new GridLayout(0, 2));
        appearancePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "✨ Appearance", TitledBorder.LEFT, TitledBorder.TOP));

        // Add UI components to the appearance panel with fun labels
        appearancePanel.add(new JBLabel("🎭 Display Mode:"));
        appearancePanel.add(displayModeComboBox);
        appearancePanel.add(new JBLabel("📍 Notification Position:"));
        appearancePanel.add(positionComboBox);
        appearancePanel.add(new JBLabel("⏱️ Display Time (seconds):"));
        appearancePanel.add(displayTimeTextField);
        appearancePanel.add(matchThemeCheckbox);
        appearancePanel.add(new JPanel()); // Empty panel for grid layout

        // Input validation for numeric fields
        intervalTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                intervalTextField.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9'
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyCode() == KeyEvent.VK_DELETE);
            }
        });

        displayTimeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                displayTimeTextField.setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9'
                        || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyCode() == KeyEvent.VK_DELETE);
            }
        });

        // Create fun title label
        JBLabel titleLabel = new JBLabel("🎯 Zokes - Your Joke Break Companion");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14));

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(titleLabel, 1)
                .addSeparator()
                .addLabeledComponent(new JBLabel("⏰ Jokes interval (seconds): "), intervalTextField, 1, false)
                .addComponent(genJokeStatusCheckbox, 1)
                .addComponent(categoriesPanel, 1)
                .addComponent(jokeStylePanel, 1)
                .addComponent(appearancePanel, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private void addCategoryCheckbox(JPanel panel, String label, String category, ZokesSettingState state) {
        JBCheckBox checkbox = new JBCheckBox(label);
        checkbox.setSelected(state.enabledCategories.contains(category));
        categoryCheckboxes.put(category, checkbox);
        panel.add(checkbox);
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

    public Set<String> getEnabledCategories() {
        Set<String> enabledCategories = new HashSet<>();
        for (Map.Entry<String, JBCheckBox> entry : categoryCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                enabledCategories.add(entry.getKey());
            }
        }
        return enabledCategories;
    }

    // UI getters
    public String getDisplayMode() {
        String displayText = (String) displayModeComboBox.getSelectedItem();
        for (Map.Entry<String, String> entry : DISPLAY_MODE_NAMES.entrySet()) {
            if (entry.getValue().equals(displayText)) {
                return entry.getKey();
            }
        }
        return DISPLAY_MODES[0]; // Default to first option
    }

    public String getNotificationPosition() {
        String positionText = (String) positionComboBox.getSelectedItem();
        for (Map.Entry<String, String> entry : POSITION_NAMES.entrySet()) {
            if (entry.getValue().equals(positionText)) {
                return entry.getKey();
            }
        }
        return NOTIFICATION_POSITIONS[0]; // Default to first option
    }

    public String getJokeType() {
        String jokeTypeText = (String) jokeTypeComboBox.getSelectedItem();
        for (Map.Entry<String, String> entry : JOKE_TYPE_NAMES.entrySet()) {
            if (entry.getValue().equals(jokeTypeText)) {
                return entry.getKey();
            }
        }
        return JOKE_TYPES[0]; // Default to first option
    }

    public boolean getMatchThemeCheckbox() {
        return matchThemeCheckbox.isSelected();
    }

    public int getDisplayTime() {
        try {
            return Integer.parseInt(displayTimeTextField.getText());
        } catch (NumberFormatException e) {
            return Constant.MIN_DISPLAY_TIME; // Default value
        }
    }
}