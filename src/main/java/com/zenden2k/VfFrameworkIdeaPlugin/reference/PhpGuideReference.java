package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PhpGuideReference implements PsiReference {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String guideName;
    protected final MethodReference methodRef;

    public PhpGuideReference(String guideName, PsiElement element, TextRange textRange, Project project, MethodReference methodRef) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.guideName = guideName;
        this.methodRef = methodRef;
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
        PsiElement res = methodRef.resolve();
        if (!(res instanceof MethodImpl)) {
            return null;
        }
        MethodImpl methodImpl = (MethodImpl)res;
        PhpClass cls = methodImpl.getContainingClass();
        if (cls == null) {
            return null;
        }

        if (!cls.getName().equals("CGuide") ){
            return null;
        }

        VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/guide/" + guideName.toLowerCase() + ".xml");
            if (vf == null) {
                vFiles[0].findFileByRelativePath("system/application/guide/" + guideName.toLowerCase() + ".php");
            }
            if (vf != null) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof XmlFile) {
                    XmlFile xmlFile = (XmlFile) psiFile;
                    XmlTag tag = xmlFile.getRootTag();
                    if (tag != null) {
                        return tag;
                    }
                    return xmlFile;
                }
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return guideName;
    }

}
