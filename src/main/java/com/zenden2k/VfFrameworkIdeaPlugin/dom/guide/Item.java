package com.zenden2k.VfFrameworkIdeaPlugin.dom.guide;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Referencing;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.MyCustomReferenceConverter;

public interface Item extends com.intellij.util.xml.DomElement {
    @NameValue
    @Attribute("alias")
    @Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getAlias();
}
