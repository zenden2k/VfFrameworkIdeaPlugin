package com.zenden2k.VfFrameworkIdeaPlugin.dom;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.object.Object;
import org.jetbrains.annotations.NotNull;

/**
 * Конвертер нужен, чтобы можно было искать использования по названию xml-объекта
 * Он возвращает ссылку на сам объект (self-reference).
 */
public class MyCustomReferenceConverter implements CustomReferenceConverter<String> {
    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<String> genericDomValue, PsiElement psiElement, ConvertContext convertContext) {
        final PsiReferenceBase<PsiElement> ref = new PsiReferenceBase<PsiElement>(psiElement) {

            @Override
            public PsiElement resolve() {
                return genericDomValue.getParent().getXmlTag();
            }

            @Override
            public boolean isSoft() {
                return true;
            }

            // do nothing. the element will be renamed via PsiMetaData
            @Override
            public PsiElement handleElementRename(@NotNull final String newElementName) throws IncorrectOperationException {
                return getElement();
            }

            @Override
            public Object[] getVariants() {
                return new Object[0];
            }
        };


        return new PsiReference[]{ref};
    }
}
