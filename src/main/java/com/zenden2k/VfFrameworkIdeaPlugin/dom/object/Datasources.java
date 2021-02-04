package com.zenden2k.VfFrameworkIdeaPlugin.dom.object;

import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface Datasources extends com.intellij.util.xml.DomElement {
    @SubTagList("datasource.orm")
    List<Datasource> getOrmDatasources();

    @SubTagList("datasource.merge")
    List<Datasource> getMergeDatasources();
}
