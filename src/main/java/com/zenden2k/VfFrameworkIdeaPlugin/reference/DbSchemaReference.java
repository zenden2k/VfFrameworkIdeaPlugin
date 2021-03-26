package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.DatabaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Reference to database schema
 *
 * Example:
 *
 * <join type="left" table="[REFERENCE].table"/>
 */
public class DbSchemaReference  extends PsiReferenceBase<PsiElement> {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String schemaName;

    public DbSchemaReference(String schemaName,  PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, true);
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.schemaName = schemaName;
    }

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        return DatabaseUtils.findSchemaByName(schemaName, project);
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return schemaName;
    }

}
