package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.PhpIndexUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class StaticDataSourceReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String path;
    protected PhpType type;
    protected String objectName;

    public StaticDataSourceReference(String path, PsiElement element, TextRange textRange, Project project, PhpType type) {
        super(element, textRange, false);
        this.project = project;
        this.path = path;
        this.type = type;
    }

    public StaticDataSourceReference(String path, PsiElement element, TextRange textRange, Project project, String objectName) {
        super(element, textRange, false);
        this.project = project;
        this.path = path;
        this.objectName = objectName;
    }

    @Override @NotNull
    public Object[] getVariants() {
        final XmlFile xmlFile = findXmlFile();
        if (xmlFile != null) {
            return getDatasourceList(xmlFile);
        }

        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        final XmlFile xmlFile = findXmlFile();
        if (xmlFile != null) {
            return findDataSource(xmlFile);
        }
        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }

    protected XmlFile findXmlFile() {
        if (type != null) {
            final PhpIndex phpIndex = PhpIndex.getInstance(project);
            final Collection<PhpClass> phpClasses = PhpIndexUtils.getByType(type, phpIndex);

            for (PhpClass cl : phpClasses) {
                PsiFile file = cl.getContainingFile();
                String fileName = file.getName();
                PsiDirectory dir = file.getContainingDirectory();
                PsiFile xmlPsiFile = dir.findFile(fileName.replace(".php", ".xml"));
                if (xmlPsiFile instanceof XmlFile) {
                    return (XmlFile) xmlPsiFile;
                }
            }
        }

        if (objectName != null) {
            final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
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

                final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + directoryName + "/" + xmlFileName + ".xml");
                if (vf != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                    if (psiFile instanceof XmlFile) {
                        return (XmlFile)psiFile;
                    }
                }
            }
        }

        return null;
    }

    protected XmlTag findDataSource(XmlFile xmlFile) {
        final XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag != null) {
            final XmlTag dataSourcesTag = rootTag.findFirstSubTag("datasources");
            if (dataSourcesTag != null) {
                final XmlTag[] tags = dataSourcesTag.getSubTags();

                for (XmlTag t : tags) {
                    if (t.getName().contains("datasource.")) {
                        final String name = t.getAttributeValue("name");
                        if (name != null && name.equals(path)) {
                            return t;
                        }
                    }
                }
            }
        }
        return null;
    }

    protected String[] getDatasourceList(XmlFile xmlFile) {
        final XmlTag rootTag = xmlFile.getRootTag();
        final ArrayList<String> dataSources = new ArrayList<String>();
        if (rootTag != null) {
            final XmlTag dataSourcesTag = rootTag.findFirstSubTag("datasources");
            if (dataSourcesTag != null) {
                final XmlTag[] tags = dataSourcesTag.getSubTags();
                for (XmlTag t : tags) {
                    if (t.getName().contains("datasource.")) {
                        final String name = t.getAttributeValue("name");
                        dataSources.add(name);
                    }
                }
            }
        }
        return dataSources.toArray(new String[0]);
    }
}
