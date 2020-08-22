package com.zenden2k.VfFrameworkIdeaPlugin.dom.form;

import com.intellij.util.xml.*;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.MyCustomReferenceConverter;

public interface FormObject extends DomElement{
    @Attribute("name")
    @NameValue
    @Required
    @Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getName();
}
