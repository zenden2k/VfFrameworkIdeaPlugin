package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.zenden2k.VfFrameworkIdeaPlugin.dom.object.Object;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlObjectReference extends PsiReferenceBase<PsiElement> {
    protected final Project project;
    protected final String objectName;

    public XmlObjectReference(String objectName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, false);
        this.project = project;
        this.objectName = objectName;
    }

    @Override @NotNull
    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        if (objectName != null) {
            final VirtualFile[] vFiles = ProjectRootManager.getInstance(this.project).getContentRoots();
            if (vFiles.length != 0 ) {
                final int delimPos = objectName.indexOf(":");
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

                    final PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                    if (psiFile instanceof XmlFile) {

                        final XmlFile xmlFile = (XmlFile)psiFile;
                        /*DomManager manager = DomManager.getDomManager(project);
                        DomFileElement<Object> el = manager.getFileElement(xmlFile, Object.class);
                        if(el!=null) {
                            Object root = el.getRootElement();
                            return root.getXmlElement();
                            /*Fields fd = root.getFields();
                            List<Field> fds = fd.getFields();*/
                            /*DomTarget domTarget = DomTarget.getTarget(root);

                            if (domTarget != null) {
                                return PomService.convertToPsi(domTarget);
                            }*
                        }*/

                        //Fields f = el.getFields();
                        final XmlTag tag = xmlFile.getRootTag();
                        if (tag != null) {
                            return tag;
                        }
                        return xmlFile;
                    }
                }
            }

        }
        return null;

    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return objectName;
    }

}
