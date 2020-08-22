package com.zenden2k.VfFrameworkIdeaPlugin.dom.block;

import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface Block extends com.intellij.util.xml.DomElement {
    @SubTag("form.link")
    FormLink getFormLink();

    @SubTagList("orm.link")
    List<OrmLink> getOrmLink();
}