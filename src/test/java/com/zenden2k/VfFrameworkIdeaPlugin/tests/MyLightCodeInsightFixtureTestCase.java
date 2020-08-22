package com.zenden2k.VfFrameworkIdeaPlugin.tests;

import com.intellij.psi.*;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.openapi.fileTypes.FileType;
import org.jetbrains.annotations.NotNull;
import com.intellij.patterns.ElementPattern;

public abstract class MyLightCodeInsightFixtureTestCase extends BasePlatformTestCase {

    public void assertReferenceMatch(@NotNull FileType fileType, @NotNull String contents, @NotNull ElementPattern<?> pattern) {
        myFixture.configureByText(fileType, contents);
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        if(psiElement == null) {
            fail("Fail to find element in caret");
        }

        assertReferences(pattern, psiElement);
    }

    private void assertReferences(@NotNull ElementPattern<?> pattern, PsiElement psiElement) {
        final PsiReference[] ref = psiElement.getReferences();
        for (PsiReference reference : ref) {
            // single resolve; should also match first multi by design
            PsiElement element = reference.resolve();
            if (pattern.accepts(element)) {
                return;
            }

            // multiResolve support
            if(reference instanceof PsiPolyVariantReference) {
                for (ResolveResult resolveResult : ((PsiPolyVariantReference) reference).multiResolve(true)) {
                    if (pattern.accepts(resolveResult.getElement())) {
                        return;
                    }
                }
            }
        }

        fail(String.format("Fail that '%s' match given pattern", psiElement.toString()));
    }

    public void assertReferenceMatchOnParent(@NotNull String file, @NotNull String contents, @NotNull ElementPattern<?> pattern) {
        myFixture.configureByText(file, contents);
        assertReferenceMatchOnParent(pattern);
    }

    public void assertReferenceMatchOnParent(@NotNull FileType fileType, @NotNull String contents, @NotNull ElementPattern<?> pattern) {
        myFixture.configureByText(fileType, contents);
        assertReferenceMatchOnParent(pattern);
    }

    private void assertReferenceMatchOnParent(@NotNull ElementPattern<?> pattern) {
        PsiElement psiElement = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        if(psiElement == null) {
            fail("Fail to find element in caret");
        }

        PsiElement parent = psiElement.getParent();
        if(parent == null) {
            fail("Fail to find parent element in caret");
        }

        assertReferences(pattern, parent);
    }
}
