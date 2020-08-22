package com.zenden2k.VfFrameworkIdeaPlugin.config.ui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class VfPluginSettingsForm {
    private JPanel panel;
    private JCheckBox enablePluginCheckbox;
    private JCheckBox enableDatabaseReferencesCheckBox;

    public VfPluginSettingsForm() {
        enablePluginCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                //(itemEvent.getStateChange() == ItemEvent.SELECTED){
                    enableDatabaseReferencesCheckBox.setEnabled(enablePluginCheckbox.isSelected());
                //}
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public boolean getEnablePlugin() {
        return enablePluginCheckbox.isSelected();
    }

    public void setEnablePlugin(boolean enablePlugin){
        enablePluginCheckbox.setSelected(enablePlugin);
    }

    public boolean getEnableDatabaseReferences() {
        return enableDatabaseReferencesCheckBox.isSelected();
    }

    public void setEnableDatabaseReferences(boolean enable) {
        enableDatabaseReferencesCheckBox.setSelected(enable);
    }
}
