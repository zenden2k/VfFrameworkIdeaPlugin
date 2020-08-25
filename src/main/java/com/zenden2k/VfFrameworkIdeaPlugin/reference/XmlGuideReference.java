package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlGuideReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String guideName;

    public XmlGuideReference(String guideName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange,false);
        this.project = project;
        this.guideName = guideName;
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    @Override @NotNull
    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/guide/" + guideName.toLowerCase() + ".xml");
            if (vf == null) {
                vf = vFiles[0].findFileByRelativePath("system/application/guide/" + guideName.toLowerCase() + ".php");
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
                } else {
                    return psiFile;
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
