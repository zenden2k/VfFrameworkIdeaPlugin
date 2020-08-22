package com.zenden2k.VfFrameworkIdeaPlugin.dom.object;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

public interface Field extends com.intellij.util.xml.DomElement {
    @Attribute("name")
    GenericAttributeValue<String> getName();
}
