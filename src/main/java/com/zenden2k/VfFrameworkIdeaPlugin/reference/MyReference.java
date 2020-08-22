package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MyReference implements /*PsiPolyVariantReference*/ PsiReference {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String path;

    public MyReference(String path, PsiElement element, TextRange textRange, Project project) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.path = path;
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    @Override @NotNull
    public PsiElement getElement() {
        return this.element;
    }

    @Override @NotNull
    public TextRange getRangeInElement() {
        return textRange;
    }

    @Override public PsiElement handleElementRename(@NotNull String newElementName)
            throws IncorrectOperationException {
        // TODO: Implement this method
        throw new IncorrectOperationException();
    }

    @Override public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        // TODO: Implement this method
        throw new IncorrectOperationException();
    }

    @Override public boolean isReferenceTo(@NotNull PsiElement element) {
        return resolve() == element;
    }

    @Override @NotNull
    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override public boolean isSoft() {
        return false;
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        String expression = path;
        int delimiterPos = expression.indexOf(':');
        if (delimiterPos == -1) {
            Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + expression);
            if (!classes.isEmpty()) {
                return classes.iterator().next();
            }
        } else {
            String path = expression.substring(0, delimiterPos);
            String objectName = expression.substring(delimiterPos + 1);
            Collection<PhpClass> res = PhpIndex.getInstance(project).getClassesByFQN("\\C" + objectName);
            for (PhpNamedElement el : res) {
                String filePath = el.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
                if (filePath.contains(path)) {
                    return el;
                }
            }
        }

        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }

    /*@Override @NotNull
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        String expression = path;
        int delimiterPos = expression.indexOf(':');
        if (delimiterPos  == -1) {
            Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + expression);
            return PsiElementResolveResult.createResults(classes);
        } else {
            String path = expression.substring(0, delimiterPos);
            String objectName = expression.substring(delimiterPos+1);
            Collection<PhpClass> res = PhpIndex.getInstance(project).getClassesByFQN("\\C" + objectName);
            for(PhpNamedElement el: res) {
                String filePath = el.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
                if(filePath.contains(path)) {
                    return new ResolveResult[]{ new PsiElementResolveResult(el)};
                }
            }
            return PsiElementResolveResult.createResults(res);
        }
    }*/
}
