package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DatasourceMethodReference implements /*PsiPolyVariantReference*/ PsiReference {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String path;
    protected final String objectName;

    public DatasourceMethodReference(@Nullable String objectName, String path, PsiElement element, TextRange textRange, Project project) {
        this.element = element;
        this.textRange = textRange;
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
        PsiFile phpFile = null;
        String fileName = "";
        if (objectName == null) {
            PsiFile file = element.getContainingFile();
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
            int pos = fileNameNoExt.lastIndexOf("");
            if (pos > 0 && pos < (fileNameNoExt.length() - 1)) {
                fileNameNoExt = fileNameNoExt.substring(0, pos);
            }
            final Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + fileNameNoExt);

            for (PhpClass el : classes) {
                if (el.getContainingFile() == phpFile) {
                    return el.findMethodByName(path);
                }
            }
        }

        /*String expression = path;
        int delimiterPos = expression.indexOf(':');
        if (delimiterPos == -1) {
            Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + expression);
            if (!classes.isEmpty()) {
                return classes.iterator().next();
            }
        } else {
            String path = expression.substring(0, delimiterPos);
            String objectName = expression.substring(delimiterPos + 1);
            Collection<PhpClass> res = PhpIndex.getInstance(project).getClassesByFQN("\\C" + objectName);
            for (PhpNamedElement el : res) {
                String filePath = el.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
                if (filePath.contains(path)) {
                    return el;
                }
            }
        }*/

        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }

}
