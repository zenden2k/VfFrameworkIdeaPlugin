package com.zenden2k.VfFrameworkIdeaPlugin.config;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.zenden2k.VfFrameworkIdeaPlugin.config.ui.VfPluginSettingsForm;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class VfIdeaPluginConfigurable implements SearchableConfigurable/*, NonDefaultProjectConfigurable*/ {
    private VfPluginSettingsForm mySettingsPane;
    private final Project myProject;

    public VfIdeaPluginConfigurable(Project project) {
        myProject = project;
    }

    public String getDisplayName() {
        return "Vf Framework Idea integration";
    }

    @NotNull
    public String getId() {
        return "VfFrameworkIdeaPlugin.settings";
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (mySettingsPane == null) {

            mySettingsPane = new VfPluginSettingsForm();
        }

        reset();
        return mySettingsPane.getPanel();
    }

    public boolean isModified() {
        return mySettingsPane != null;
    }

    public void apply() throws ConfigurationException{
        if (mySettingsPane != null) {
            final boolean modified = isModified();
            boolean enablePlugin = mySettingsPane.getEnablePlugin();
            boolean enableDatabaseReferences = mySettingsPane.getEnableDatabaseReferences();
            boolean enableGetStaticDatasourceTypeProviding = mySettingsPane.getEnableGetStaticDatasourceTypeProviding();
            final PropertiesComponent projectProperties = PropertiesComponent.getInstance(myProject);
            projectProperties.setValue(VfPluginSettings.ENABLE_PLUGIN_KEY, enablePlugin, VfPluginSettings.getEnablePluginDefaultValue(myProject));
            projectProperties.setValue(VfPluginSettings.ENABLE_DATABASE_REFERENCES, enableDatabaseReferences, VfPluginSettings.ENABLE_DATABASE_REFERENCES_DEFAULT_VALUE);
            projectProperties.setValue(
                    VfPluginSettings.ENABLE_GETSTATICDATASOURCE_TYPE_PROVIDING_KEY,
                    enableGetStaticDatasourceTypeProviding,
                    VfPluginSettings.ENABLE_GETSTATICDATASOURCE_TYPE_PROVIDING_DEFAULT_VALUE
            );
            //if (modified) {
                //reparseProjectFiles(myProject);
            //}
        }
    }

    public void reset() {
        if (mySettingsPane != null) {
            final PropertiesComponent projectProperties = PropertiesComponent.getInstance(myProject);
            final boolean enablePlugin = projectProperties.getBoolean(VfPluginSettings.ENABLE_PLUGIN_KEY, VfPluginSettings.getEnablePluginDefaultValue(myProject));
            final boolean enableDatabaseReferences = projectProperties.getBoolean(VfPluginSettings.ENABLE_DATABASE_REFERENCES, VfPluginSettings.ENABLE_DATABASE_REFERENCES_DEFAULT_VALUE);
            final boolean enableGetStaticDatasourceTypeProviding = projectProperties.getBoolean(
                    VfPluginSettings.ENABLE_GETSTATICDATASOURCE_TYPE_PROVIDING_KEY,
                    VfPluginSettings.ENABLE_GETSTATICDATASOURCE_TYPE_PROVIDING_DEFAULT_VALUE
            );

            mySettingsPane.setEnablePlugin(enablePlugin);
            mySettingsPane.setEnableDatabaseReferences(enableDatabaseReferences);
            mySettingsPane.setEnableGetStaticDatasourceTypeProviding(enableGetStaticDatasourceTypeProviding);
        }
    }

    public void disposeUIResources() {
        mySettingsPane = null;
    }

    public Runnable enableSearch(String option) {
        return null;
    }



}