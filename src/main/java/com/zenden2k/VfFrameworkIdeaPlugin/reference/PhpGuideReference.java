package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PhpGuideReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String guideName;
    protected final MethodReference methodRef;

    public PhpGuideReference(String guideName, PsiElement element, TextRange textRange, Project project, MethodReference methodRef) {
        super(element, textRange, false);
        this.project = project;
        this.guideName = guideName;
        this.methodRef = methodRef;
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
