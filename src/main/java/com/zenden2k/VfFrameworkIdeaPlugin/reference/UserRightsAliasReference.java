package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.AutocompleteHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
    Reference to user role alias from php and xml files

    Example:

    $user->checkAccess('[REFERENCE],[REFERENCE],...')
 */
public class UserRightsAliasReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String alias;
    protected final MethodReference methodRef;

    public UserRightsAliasReference(String alias, PsiElement element, TextRange textRange, Project project, MethodReference methodRef) {
        super(element, textRange, false);
        this.project = project;
        this.alias = alias;
        this.methodRef = methodRef;
    }

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        return AutocompleteHelper.getUserRightsAliases(this.project).toArray();
    }


    @Override
    @Nullable
    public PsiElement resolve() {
        if (methodRef != null) {
            final PsiElement res = methodRef.resolve();
            if (!(res instanceof MethodImpl)) {
                return null;
            }
            final MethodImpl methodImpl = (MethodImpl)res;
            final PhpClass cls = methodImpl.getContainingClass();
            if (cls == null) {
                return null;
            }

            String className = cls.getName();

            if (!className.equals("CDBUser") && !className.equals("CUser")){
                return null;
            }
        }

        final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();

        if (vFiles.length != 0) {
            VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/guide/user-rights.xml");
            if (vf != null) {
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                if (psiFile instanceof XmlFile) {
                    final XmlFile xmlFile = (XmlFile) psiFile;
                    final XmlTag tag = xmlFile.getRootTag();
                    if (tag != null) {
                        XmlTag[] itemTags = tag.findSubTags("item");
                        for(XmlTag item: itemTags) {
                            String aliasValue = item.getAttributeValue("alias");
                            if (aliasValue != null && aliasValue.equals(alias)) {
                                return item;
                            }
                        }
                        return tag;
                    }
                }
            }
        }
        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return alias;
    }
}
