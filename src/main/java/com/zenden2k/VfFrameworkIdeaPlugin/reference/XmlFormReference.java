package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.AutocompleteHelper;
import groovyjarjarpicocli.AutoComplete;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlFormReference extends PsiReferenceBase<PsiElement> {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String moduleName;
    protected final String formName;

    public XmlFormReference(String moduleName, String formName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.moduleName = moduleName;
        this.formName = formName;
    }

    @Override @NotNull
    public Object[] getVariants() {
        return AutocompleteHelper.getFormList(project, moduleName).toArray();
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/forms/" + formName.toLowerCase() + ".xml");
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
        return formName;
    }
}
