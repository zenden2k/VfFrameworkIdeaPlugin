package com.zenden2k.VfFrameworkIdeaPlugin.dom.block;

import com.intellij.util.xml.*;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.object.Object;

public interface OrmLink extends com.intellij.util.xml.DomElement {
    @Attribute("object")
    GenericAttributeValue<Object> getObject();

    /*@Attribute("name")
    @NameValue
    GenericAttributeValue<String> getName();*/
}
