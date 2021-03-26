package com.zenden2k.VfFrameworkIdeaPlugin.reference;

import com.intellij.database.model.basic.BasicTable;
import com.intellij.database.model.basic.BasicTableOrViewColumn;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;

import com.zenden2k.VfFrameworkIdeaPlugin.utils.DatabaseUtils;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.DbTableNameInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Reference to database table field
 *
 * Example:
 *
 * <object name="user">
 *     <fields>
 *         <field name="[REFERENCE]" key="true" autoincrement="true"/>
 *     </fields>
 * </object>
 */
public class DbFieldReference extends PsiReferenceBase<PsiElement> {
    protected final PsiElement element;
    protected final TextRange textRange;
    protected final Project project;
    protected final String path;
    protected final String table;

    public DbFieldReference(String path, String table, PsiElement element, TextRange textRange, Project project) {
        super(element, textRange, true);
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.path = path;
        this.table = table;
    }

    @Override @NotNull
    public Object @NotNull [] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }


    @Override
    @Nullable
    public PsiElement resolve() {
        final DbTableNameInfo info = DbTableNameInfo.createFromString(table);
        if (info == null) {
            return null;
        }
        final BasicTable tbl = DatabaseUtils.findTableByName(info.getTableName(), info.getSchemaName(), project);
        if (tbl == null) {
            return null;
        }
        for (BasicTableOrViewColumn column : tbl.getColumns()) {
            if (column.getName().equals(path)) {
                DbPsiFacade facade = DbPsiFacade.getInstance(project);
                return facade.findElement(column);
            }
        }

        return null;
    }

    @Override
    @NotNull
    public String getCanonicalText() {
        return path;
    }
}
