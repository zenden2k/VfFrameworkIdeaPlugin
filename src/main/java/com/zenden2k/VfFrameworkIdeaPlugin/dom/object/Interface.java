package com.zenden2k.VfFrameworkIdeaPlugin.dom.object;

import com.intellij.util.xml.*;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.MyCustomReferenceConverter;

public interface Interface extends DomElement {
    @Attribute("name")
    @NameValue
    @Required
    @Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getName();
}
