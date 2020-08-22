package com.zenden2k.VfFrameworkIdeaPlugin;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttributeValue;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class MyPsiReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        MyPsiReferenceProvider provider = new MyPsiReferenceProvider();
        XmlReferenceProvider xmlRefProvider = new XmlReferenceProvider();

        registrar.registerReferenceProvider(StandardPatterns.instanceOf(StringLiteralExpression.class), provider);
        registrar.registerReferenceProvider(StandardPatterns.instanceOf(XmlAttributeValue.class), xmlRefProvider);
    }
}
