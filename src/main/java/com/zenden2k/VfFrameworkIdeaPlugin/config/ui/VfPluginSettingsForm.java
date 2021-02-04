package com.zenden2k.VfFrameworkIdeaPlugin.config.ui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class VfPluginSettingsForm {
    private JPanel panel;
    private JCheckBox enablePluginCheckbox;
    private JCheckBox enableDatabaseReferencesCheckBox;
    private JCheckBox enableGetStaticDatasourceTypeProviding;

    public VfPluginSettingsForm() {
        enablePluginCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                //(itemEvent.getStateChange() == ItemEvent.SELECTED){
                enableDatabaseReferencesCheckBox.setEnabled(enablePluginCheckbox.isSelected());
                enableGetStaticDatasourceTypeProviding.setEnabled(enablePluginCheckbox.isSelected());
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

    public boolean getEnableGetStaticDatasourceTypeProviding(){
        return enableGetStaticDatasourceTypeProviding.isSelected();
    }

    public void setEnableGetStaticDatasourceTypeProviding(boolean enable) {
        enableGetStaticDatasourceTypeProviding.setSelected(enable);
    }
}
