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
import com.zenden2k.VfFrameworkIdeaPlugin.utils.AutocompleteHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;


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

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        return AutocompleteHelper.getGuideList(this.project).toArray();
    }


    @Override
    @Nullable
    public PsiElement resolve() {
        final PsiElement res = methodRef.resolve();
        if (!(res instanceof MethodImpl)) {
            return null;
        }
        final MethodImpl methodImpl = (MethodImpl)res;
        final PhpClass cls = methodImpl.getContainingClass();
        if (cls == null) {
            return null;
        }

        if (!cls.getName().equals("CGuide") ){
            return null;
        }

        final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();

        if (vFiles.length != 0) {
            final String guideNameLower = guideName.toLowerCase(Locale.ROOT);
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/guide/" + guideNameLower + ".xml");
            if (vf == null) {
                vf = vFiles[0].findFileByRelativePath("system/application/guide/" + guideNameLower + ".php");
            }
            if (vf != null) {
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof XmlFile) {
                    final XmlFile xmlFile = (XmlFile) psiFile;
                    final XmlTag tag = xmlFile.getRootTag();
                    if (tag != null) {
                        return tag;
                    }
                    return xmlFile;
                }
                return psiFile;
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
