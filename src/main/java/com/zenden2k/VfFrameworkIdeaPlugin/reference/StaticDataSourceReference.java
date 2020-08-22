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
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.PhpIndexUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class StaticDataSourceReference implements /*PsiPolyVariantReference*/ PsiReference {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String path;
    protected PhpType type;
    protected String objectName;

    public StaticDataSourceReference(String path, PsiElement element, TextRange textRange, Project project, PhpType type) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.path = path;
        this.type = type;
    }

    public StaticDataSourceReference(String path, PsiElement element, TextRange textRange, Project project, String objectName) {
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
        if (type != null) {
            PhpIndex phpIndex = PhpIndex.getInstance(project);
            Collection<PhpClass> phpClasses = PhpIndexUtils.getByType(type, phpIndex);

            PsiFile firstXmlPsiFile = null;
            for (PhpClass cl : phpClasses) {
                PsiFile file = cl.getContainingFile();
                String fileName = file.getName();
                PsiDirectory dir = file.getContainingDirectory();
                PsiFile xmlPsiFile = dir.findFile(fileName.replace(".php", ".xml"));
                if (xmlPsiFile instanceof XmlFile) {
                    XmlFile xmlFile = (XmlFile) xmlPsiFile;

                    XmlTag tag = findDataSource(xmlFile);
                    if (tag != null) {
                        return tag;
                    }
                    if (firstXmlPsiFile == null) {
                        firstXmlPsiFile = xmlPsiFile;
                    }
                }
            }
            return firstXmlPsiFile;
        }

        if (objectName != null) {
            VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
            if (vFiles.length != 0 ) {
                int delimPos = objectName.indexOf(":");
                String xmlFileName;
                String directoryName;
                if (delimPos != -1) {
                    directoryName = objectName.substring(0, delimPos);
                    xmlFileName = objectName.substring(delimPos + 1);
                } else {
                    directoryName = objectName;
                    xmlFileName = objectName;
                }

                VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + directoryName + "/" + xmlFileName + ".xml");
                if (vf != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                    if (psiFile instanceof XmlFile) {
                        return findDataSource((XmlFile)psiFile);
                    }
                }
            }

            /*VirtualFile baseDir = project.getBaseDir();
            if (baseDir != null) {
                VirtualFile webDir = baseDir.findFileByRelativePath("web/");*/
        }
        return null;

    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }

    protected XmlTag findDataSource(XmlFile xmlFile) {
        XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag != null) {
            XmlTag dataSourcesTag = rootTag.findFirstSubTag("datasources");
            if (dataSourcesTag != null) {
                XmlTag[] dataSourceTags = dataSourcesTag.findSubTags("datasource.orm");
                for (XmlTag t : dataSourceTags) {
                    String name = t.getAttributeValue("name");
                    if (name != null && name.equals(path)) {
                        return t;
                    }
                }
            }
        }
        return null;
    }
}
