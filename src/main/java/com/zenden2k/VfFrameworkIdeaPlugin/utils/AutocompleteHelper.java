package com.zenden2k.VfFrameworkIdeaPlugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.google.common.io.Files;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class AutocompleteHelper {
    public static Collection<String> getGuideList(Project project) {
        final ArrayList<String> result = new ArrayList<>();
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
        if (vFiles.length != 0) {
            final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/guide");
            if (vf != null && vf.isDirectory()) {
                for(VirtualFile child: vf.getChildren()) {
                    String name = child.getName();
                    if (name.endsWith(".php") || name.endsWith(".xml")) {
                        int pos = name.lastIndexOf(".");
                        if (pos > 0 && pos < (name.length() - 1)) {
                            name = name.substring(0, pos);
                        }
                        result.add(name);
                    }
                }
            }
        }
        return result;
    }

    public static Collection<String> getFormList(Project project, @NotNull String moduleName) {
        final HashSet<String> result = new HashSet<>();
        if (moduleName.isEmpty()) {
            return result;
        }
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
        if (vFiles.length != 0) {
            final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/forms");
            if (vf != null && vf.isDirectory()) {
                for(VirtualFile child: vf.getChildren()) {
                    final String name = child.getName();
                    if (name.endsWith(".xml")) {
                        result.add(Files.getNameWithoutExtension(name));
                    }
                }
            }
        }
        return result;
    }

    public static Collection<String> getControllerList(Project project, @NotNull String moduleName) {
        final HashSet<String> result = new HashSet<>();
        if (moduleName.isEmpty()) {
            return result;
        }
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
        if (vFiles.length != 0) {
            final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/controllers");
            if (vf != null && vf.isDirectory()) {
                for(VirtualFile child: vf.getChildren()) {
                    final String name = child.getName();
                    if (name.endsWith(".xml") || name.endsWith(".php")) {
                        result.add(Files.getNameWithoutExtension(child.getName()));
                    }
                }
            }
        }
        return result;
    }

    public static Collection<String> getDataViewList(Project project, @NotNull String moduleName) {
        ArrayList<String> result = new ArrayList<>();
        if (moduleName.isEmpty()) {
            return result;
        }
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
        if (vFiles.length != 0) {
            final VirtualFile vf = vFiles[0].findFileByRelativePath("system/application/vf_controllers/" + moduleName + "/views/dataview");
            if (vf != null && vf.isDirectory()) {
                for(VirtualFile child: vf.getChildren()) {
                    final String name = child.getName();
                    if (name.endsWith(".tpl")) {
                        result.add(Files.getNameWithoutExtension(child.getName()));
                    }
                }
            }
        }
        return result;
    }

    public static Collection<String> getUserRightsAliases(Project project) {
        ArrayList<String> result = new ArrayList<>();
        final VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();

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
                            if (aliasValue != null) {
                                result.add(aliasValue);
                            }
                        }

                    }
                }
            }
        }
        return result;
    }
}
