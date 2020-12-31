package com.zenden2k.VfFrameworkIdeaPlugin.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

public class ToggleXmlAndPhpAction extends AnAction  {

    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        final VirtualFile vf = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

        if (vf == null) {
            return;
        }

        final String fileName = vf.getName();
        String newFileName;
        if (fileName.endsWith(".php")) {
            newFileName = fileName.replace(".php", ".xml");
        } else if (fileName.endsWith(".xml")) {
            newFileName = fileName.replace(".xml", ".php");
        } else {
            return;
        }

        VirtualFile fileToSwitch = vf.getParent().findChild(newFileName);

        if (fileToSwitch == null) {
            return;
        }
        PsiFile psiFile = PsiManager.getInstance(project).findFile(fileToSwitch);

        if (psiFile == null) {
            return;
        }
        if (psiFile.canNavigate()) {
            psiFile.navigate(true);
        }
    }
}
