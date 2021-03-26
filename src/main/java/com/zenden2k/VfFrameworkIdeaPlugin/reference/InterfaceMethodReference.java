package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/*
  Reference to object's method from an interface

  Example:

 <interface name="group" method="[REFERENCE]"/>
 */
public class InterfaceMethodReference extends PsiReferenceBase<PsiElement> {
    protected final PsiElement element;
    protected final Project project;
    protected final String path;

    public InterfaceMethodReference(String path, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.element = element;
        this.project = project;
        this.path = path;
    }

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        PhpClass cls = findClass();
        if (cls != null) {
            // Filtering out php magic methods
            return cls.getMethods().stream().filter(m -> !m.getName().startsWith("__")).toArray();
        }

        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        final PhpClass cls = findClass();
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

        final PsiFile file = element.getContainingFile().getOriginalFile();
        fileName = file.getName();
        final PsiDirectory dir = file.getContainingDirectory();
        if (dir != null) {
            phpFile = dir.findFile(fileName.replace(".xml", ".php"));
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
