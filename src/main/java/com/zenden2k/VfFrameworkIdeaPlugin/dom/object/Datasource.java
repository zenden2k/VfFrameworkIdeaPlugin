package com.zenden2k.VfFrameworkIdeaPlugin.dom.object;

import com.intellij.util.xml.*;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.MyCustomReferenceConverter;

public interface Datasource extends com.intellij.util.xml.DomElement {
    @Attribute("name")
    @NameValue
    @Required
    @Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getName();
}
