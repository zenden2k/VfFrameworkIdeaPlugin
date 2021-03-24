package com.zenden2k.VfFrameworkIdeaPlugin.dom.guide;

import com.intellij.util.xml.*;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.MyCustomReferenceConverter;

import java.util.List;

public interface Guide extends DomElement{
    @Attribute("name")
    @NameValue
    @Required
    @Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getName();

    List<Item> getItems();
}
