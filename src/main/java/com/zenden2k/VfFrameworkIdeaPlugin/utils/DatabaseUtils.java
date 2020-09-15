package com.zenden2k.VfFrameworkIdeaPlugin.utils;

import com.intellij.database.model.DasObject;
import com.intellij.database.model.basic.BasicSchema;
import com.intellij.database.model.basic.BasicTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DatabaseUtils {

    public static BasicTable findTableByName(String table, @Nullable String schemaName, Project project) {
        if(table == null || table.isEmpty()){
            return null;
        }
        final DbPsiFacade facade = DbPsiFacade.getInstance(project);
        final List<DbDataSource> dataSources = facade.getDataSources();

        for (DbDataSource source : dataSources) {
            DasObject root = source.getModel().getCurrentRootNamespace();
            if (schemaName != null ) {
                for(DasObject obj : source.getModel().getModelRoots()){
                    if (obj instanceof BasicSchema) {
                        BasicSchema schema = (BasicSchema)obj;
                        if (schema.getName().equals(schemaName)) {
                            root = schema;
                        }
                    }
                }
            }

            for (Object item : source.getModel().traverser().children(root)) {
                if (item instanceof BasicTable && ((BasicTable) item).getName().equals(table)) {
                    return (BasicTable)item;
                }
            }
        }
        return null;
    }

    public static PsiElement findSchemaByName(String schemaName, Project project) {
        if(schemaName == null || schemaName.isEmpty()){
            return null;
        }
        final DbPsiFacade facade = DbPsiFacade.getInstance(project);
        final List<DbDataSource> dataSources = facade.getDataSources();

        for (DbDataSource source : dataSources) {
            for(DasObject obj : source.getModel().getModelRoots()){
                if (obj instanceof BasicSchema) {
                    final BasicSchema schema = (BasicSchema)obj;
                    if (schema.getName().equals(schemaName)) {
                        return facade.findElement(schema);
                    }
                }
            }

        }
        return null;
    }

}
