package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.database.model.basic.BasicTable;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.DatabaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DbTableReference implements /*PsiPolyVariantReference*/ PsiReference {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String tableName;
    protected final String schemaName;

    public DbTableReference(String tableName, @Nullable String schemaName, PsiElement element, TextRange textRange, Project project) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.tableName = tableName;
        this.schemaName = schemaName;
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
        DbPsiFacade facade = DbPsiFacade.getInstance(project);
        BasicTable tbl = DatabaseUtils.findTableByName(tableName, schemaName, project);
        return facade.findElement(tbl);
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return tableName;
    }

}
