package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/*
  Reference to an interface

  Example:
  <buttons>
    <button interface="[REFERENCE]"/>
  </buttons>
 */
public class InterfaceReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String path;

    public InterfaceReference(String path, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.project = project;
        this.path = path;
    }

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        final XmlFile xmlFile = findXmlFile();
        if (xmlFile != null) {
            return getInterfaceList(xmlFile);
        }

        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        final XmlFile xmlFile = findXmlFile();
        if (xmlFile != null) {
            return findInterface(xmlFile);
        }
        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }

    protected XmlFile findXmlFile() {
        String objectName = detectObjectName();

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

    protected XmlTag findInterface(XmlFile xmlFile) {
        final XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag != null) {
            final XmlTag dataSourcesTag = rootTag.findFirstSubTag("interfaces");
            if (dataSourcesTag != null) {
                final XmlTag[] tags = dataSourcesTag.getSubTags();

                for (XmlTag t : tags) {
                    if (t.getName().equals("interface")) {
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

    protected String[] getInterfaceList(XmlFile xmlFile) {
        final XmlTag rootTag = xmlFile.getRootTag();
        final ArrayList<String> dataSources = new ArrayList<String>();
        if (rootTag != null) {
            final XmlTag dataSourcesTag = rootTag.findFirstSubTag("interfaces");
            if (dataSourcesTag != null) {
                final XmlTag[] tags = dataSourcesTag.getSubTags();
                for (XmlTag t : tags) {
                    if (t.getName().equals("interface")) {
                        final String name = t.getAttributeValue("name");
                        dataSources.add(name);
                    }
                }
            }
        }
        return dataSources.toArray(new String[0]);
    }

    @Nullable
    protected String detectObjectName() {
        final XmlAttribute interfaceAttr = (XmlAttribute)myElement.getParent();
        final XmlTag buttonTag = interfaceAttr.getParent();
        final XmlTag dataviewToolbarTag = buttonTag.getParentTag();
        XmlTag dataviewGridTag = null;
        if (dataviewToolbarTag != null) {
            String name = dataviewToolbarTag.getName();
            if (name.equals("dataview.toolbar")) {
                dataviewGridTag = dataviewToolbarTag.getParentTag();
            } else if (name.equals("buttons")) {
                XmlTag fieldTag = dataviewToolbarTag.getParentTag();
                if (fieldTag != null && fieldTag.getName().equals("field")) {
                    XmlTag fieldsTag = fieldTag.getParentTag();
                    if (fieldsTag != null && fieldsTag.getName().equals("fields")) {
                        XmlTag gridTag = fieldsTag.getParentTag();
                        if (gridTag != null && gridTag.getName().equals("dataview.grid")) {
                            dataviewGridTag = gridTag;
                        }
                    }
                }
            }
        }

        if (dataviewGridTag != null) {
            final XmlTag dataSourceLinkTag = dataviewGridTag.findFirstSubTag("datasource.link");
            if (dataSourceLinkTag != null) {
                final String typeAttr = dataSourceLinkTag.getAttributeValue("type");
                if ( typeAttr != null && typeAttr.equals("object")) {
                    return dataSourceLinkTag.getAttributeValue("object");
                }
            }
        }
        return null;
    }
}
