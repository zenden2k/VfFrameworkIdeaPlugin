package com.zenden2k.VfFrameworkIdeaPlugin.dom.object;

import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface Interfaces extends com.intellij.util.xml.DomElement {
    @SubTagList("interface")
    List<Interface> getInterfaces();
}
