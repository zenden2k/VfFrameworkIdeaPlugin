package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class DataViewReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String viewName;
    protected final String objectName;
    protected final String directoryName;

    public DataViewReference(String viewName, String objectName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.project = project;
        this.viewName = viewName;
        this.objectName = objectName;

        final int delimPos = objectName.indexOf(":");

        if (delimPos != -1) {
            directoryName = objectName.substring(0, delimPos);
        } else {
            directoryName = objectName;
        }
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    @Override public boolean isReferenceTo(@NotNull PsiElement element) {
        return resolve() == element;
    }

    @Override @NotNull
    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + directoryName + "/views/dataview/" + viewName + ".tpl");

            if (vf != null) {
                return PsiManager.getInstance(project).findFile(vf);
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return viewName;
    }
}
