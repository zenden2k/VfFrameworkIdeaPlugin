package com.zenden2k.VfFrameworkIdeaPlugin.dom.block;

import com.intellij.util.xml.*;

public interface FormLink extends com.intellij.util.xml.DomElement {
    @Attribute("name")
    //@NameValue
    @Required
    //@Referencing(value = MyCustomReferenceConverter.class, soft = true)
    GenericAttributeValue<String> getName();

    @SubTag("orm.link")
    OrmLink getOrmLink();
}
