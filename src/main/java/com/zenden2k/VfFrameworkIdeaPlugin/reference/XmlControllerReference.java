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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlControllerReference implements /*PsiPolyVariantReference*/ PsiReference {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String moduleName;
    protected final String controllerName;

    public XmlControllerReference(String moduleName, String controllerName, PsiElement element, TextRange textRange, Project project) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.moduleName = moduleName;
        this.controllerName = controllerName;
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
        VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/controllers/" + controllerName + ".xml");
            if (vf == null) {
                vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/controllers/" + controllerName + ".php");
            }
            if (vf != null) {
                // Это может быть php файл или xml файл
                PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof XmlFile) {
                    XmlFile xmlFile = (XmlFile) psiFile;
                    XmlTag tag = xmlFile.getRootTag();
                    if (tag != null) {
                        return tag;
                    }

                }
                return psiFile;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return controllerName;
    }

}
