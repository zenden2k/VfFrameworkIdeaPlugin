package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.database.model.basic.BasicTable;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.DatabaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DbTableReference extends PsiReferenceBase<PsiElement> {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String tableName;
    protected final String schemaName;

    public DbTableReference(String tableName, @Nullable String schemaName, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, true);
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.tableName = tableName;
        this.schemaName = schemaName;
    }

    @Override @NotNull
    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        final DbPsiFacade facade = DbPsiFacade.getInstance(project);
        final BasicTable tbl = DatabaseUtils.findTableByName(tableName, schemaName, project);
        return facade.findElement(tbl);
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return tableName;
    }

}
