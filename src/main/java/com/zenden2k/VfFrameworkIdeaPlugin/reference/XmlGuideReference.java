package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.AutocompleteHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class XmlGuideReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String guideName;

    public XmlGuideReference(String guideName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange,false);
        this.project = project;
        this.guideName = guideName;
    }

    @Override @NotNull
    public Object[] getVariants() {
        return AutocompleteHelper.getGuideList(this.project).toArray();
    }

    @Override
    @Nullable
    public PsiElement resolve() {
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
