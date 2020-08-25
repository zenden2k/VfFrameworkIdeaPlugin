package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DatasourceMethodReference extends PsiReferenceBase<PsiElement> {
    protected final PsiElement element;
    protected final Project project;
    protected final String path;
    protected final String objectName;

    public DatasourceMethodReference(@Nullable String objectName, String path, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange);
        this.element = element;
        this.project = project;
        this.path = path;
        this.objectName = objectName;
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
    public Object[] getVariants() {
        PhpClass cls = findClass();
        if (cls != null) {
            return cls.getMethods().toArray();
        }

        return new Object[0];
    }

    @Override public boolean isSoft() {
        return false;
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        PhpClass cls = findClass();
        if (cls != null) {
            return cls.findMethodByName(path);
        }

        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }

    protected PhpClass findClass() {
        PsiFile phpFile = null;
        String fileName = "";
        if (objectName == null) {
            PsiFile file = element.getContainingFile().getOriginalFile();
            fileName = file.getName();
            PsiDirectory dir = file.getContainingDirectory();
            if (dir != null) {
                phpFile = dir.findFile(fileName.replace(".xml", ".php"));
            }
        } else {
            final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
            if (vFiles.length != 0) {
                int delimPos = objectName.indexOf(":");
                String phpFileName;
                String directoryName;
                if (delimPos != -1) {
                    directoryName = objectName.substring(0, delimPos);
                    phpFileName = objectName.substring(delimPos + 1);
                } else {
                    directoryName = objectName;
                    phpFileName = objectName;
                }
                fileName = phpFileName;

                final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + directoryName + "/" + phpFileName + ".php");
                if (vf != null) {
                    phpFile = PsiManager.getInstance(project).findFile(vf);
                }
            }
        }

        if (phpFile != null) {
            String fileNameNoExt = fileName;
            int pos = fileNameNoExt.lastIndexOf(".");
            if (pos > 0 && pos < (fileNameNoExt.length() - 1)) {
                fileNameNoExt = fileNameNoExt.substring(0, pos);
            }
            final Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + fileNameNoExt);

            for (PhpClass el : classes) {
                if (el.getContainingFile() == phpFile) {
                    return el;
                }
            }
        }
        return null;
    }
}
