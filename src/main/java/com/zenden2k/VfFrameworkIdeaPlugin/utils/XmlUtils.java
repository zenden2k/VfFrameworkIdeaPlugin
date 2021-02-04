package com.zenden2k.VfFrameworkIdeaPlugin.utils;

import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

public class XmlUtils {

    public static XmlTag findDataSource(XmlFile xmlFile, String datasourceName) {
        final XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag != null) {
            final XmlTag dataSourcesTag = rootTag.findFirstSubTag("datasources");
            if (dataSourcesTag != null) {
                final XmlTag[] tags = dataSourcesTag.getSubTags();

                for (XmlTag t : tags) {
                    if (t.getName().contains("datasource.")) {
                        final String name = t.getAttributeValue("name");
                        if (name != null && name.equals(datasourceName)) {
                            return t;
                        }
                    }
                }
            }
        }
        return null;
    }
}
