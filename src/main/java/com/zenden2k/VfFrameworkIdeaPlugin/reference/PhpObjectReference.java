package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PhpObjectReference extends PsiReferenceBase<PsiElement>  {
    protected final Project project;
    protected final String path;

    public PhpObjectReference(String path, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.project = project;
        this.path = path;
    }

    @Override public PsiElement handleElementRename(@NotNull String newElementName)
            throws IncorrectOperationException {
        // TODO: Implement this method
        throw new IncorrectOperationException();
    }

    @Override @NotNull
    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
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
            for (PhpClass el : res) {
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
