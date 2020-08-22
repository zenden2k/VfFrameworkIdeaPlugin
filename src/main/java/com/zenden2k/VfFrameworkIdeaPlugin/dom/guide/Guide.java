package com.zenden2k.VfFrameworkIdeaPlugin.dom.guide;

import com.intellij.util.xml.*;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.MyCustomReferenceConverter;

public interface Guide extends DomElement{
    @Attribute("name")
    @NameValue
    @Required
    @Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getName();
}
