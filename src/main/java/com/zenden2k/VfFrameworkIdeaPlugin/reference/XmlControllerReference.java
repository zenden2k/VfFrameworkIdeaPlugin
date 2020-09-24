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

public class XmlControllerReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String moduleName;
    protected final String controllerName;

    public XmlControllerReference(String moduleName, String controllerName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.project = project;
        this.moduleName = moduleName;
        this.controllerName = controllerName;
    }

    @Override @NotNull
    public Object[] getVariants() {
        return AutocompleteHelper.getControllerList(project, moduleName).toArray();
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/controllers/" + controllerName + ".xml");
            if (vf == null) {
                vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/controllers/" + controllerName + ".php");
            }
            if (vf != null) {
                // Это может быть php файл или xml файл
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof XmlFile) {
                    final XmlFile xmlFile = (XmlFile) psiFile;
                    final XmlTag tag = xmlFile.getRootTag();
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
