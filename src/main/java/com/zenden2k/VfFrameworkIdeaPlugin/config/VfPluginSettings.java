package com.zenden2k.VfFrameworkIdeaPlugin.config;

import com.intellij.openapi.project.Project;

public class VfPluginSettings {
    public static final String ENABLE_PLUGIN_KEY = "VfFrameworkIdeaPlugin.settings.enablePlugin";
    public static final String ENABLE_DATABASE_REFERENCES = "VfFrameworkIdeaPlugin.settings.enableDataBaseReferences";

    public static boolean getEnablePluginDefaultValue(Project project) {
        return project.getName().contains("\u006b\u0061\u0064\u0061\u006d\u005f\u006f\u006c\u0064");
    }
}
