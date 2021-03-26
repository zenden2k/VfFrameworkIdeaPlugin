package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/*
  Reference to an xml object file

  Example:

    GetObject("[REFERENCE]")
 */
public class PhpObjectReference extends PsiReferenceBase<PsiElement>  {
    protected final Project project;
    protected final String path;
    private final String objectName;
    private final String moduleName;

    public PhpObjectReference(String path, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.project = project;
        this.path = path;

        int delimiterPos = path.indexOf(':');
        if (delimiterPos == -1) {
            objectName = path;
            moduleName = null;
        } else {
            moduleName = path.substring(0, delimiterPos);
            objectName = path.substring(delimiterPos + 1);
        }
    }

    @Override public PsiElement handleElementRename(@NotNull String newElementName)
            throws IncorrectOperationException {
        // TODO: Implement this method
        throw new IncorrectOperationException();
    }

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        if (moduleName == null) {
            final Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + objectName);
            if (!classes.isEmpty()) {
                return classes.iterator().next();
            }
        } else {
            Collection<PhpClass> res = PhpIndex.getInstance(project).getClassesByFQN("\\C" + objectName);
            for (PhpClass el : res) {
                final String filePath = el.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
                if (filePath.contains("/" + moduleName)) {
                    return el;
                }
            }
        }

        // If class has not been not found, seek for xml file
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
        if (vFiles.length != 0) {
            final String directoryName = moduleName == null ? objectName : moduleName;
            final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + directoryName + "/" + objectName + ".xml");
            if (vf != null) {
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof XmlFile) {
                    final XmlFile xmlFile = (XmlFile) psiFile;
                    final XmlTag tag = xmlFile.getRootTag();
                    if (tag != null) {
                        return tag;
                    }
                    return psiFile;
                }
            }
        }

        return null;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        PsiElement res = resolve();
        if (res == null ) {
            return false;
        }
        // We need additional check to make possible finding usages of php file
        // in which the class is living. Finding these usages of the class itself is not possible
        // due to word scanner limitation
        return this.getElement().getManager().areElementsEquivalent(res, element)
                || this.getElement().getManager().areElementsEquivalent(res.getContainingFile(), element);
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return "\\C" + objectName;
    }

    /*@Override @NotNull
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        String expression = path;
        int delimiterPos = expression.indexOf(':');
        if (delimiterPos  == -1) {
            Collection<PhpClass> classes = PhpIndex.getInstance(project).getClassesByFQN("\\C" + expression);
            return PsiElementResolveResult.createResults(classes);
        } else {
            String path = expression.substring(0, delimiterPos);
            String objectName = expression.substring(delimiterPos+1);
            Collection<PhpClass> res = PhpIndex.getInstance(project).getClassesByFQN("\\C" + objectName);
            for(PhpNamedElement el: res) {
                String filePath = el.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
                if(filePath.contains(path)) {
                    return new ResolveResult[]{ new PsiElementResolveResult(el)};
                }
            }
            return PsiElementResolveResult.createResults(res);
        }
    }*/
}
