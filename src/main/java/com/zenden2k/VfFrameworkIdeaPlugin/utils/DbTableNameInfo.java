package com.zenden2k.VfFrameworkIdeaPlugin.utils;

import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DbTableNameInfo {
    private final String tableName;
    private final TextRange tableNameRange;
    private final String schemaName;
    private final TextRange schemaNameRange;

    public DbTableNameInfo(@NotNull String tableName, @NotNull TextRange tableNameRange, @Nullable String schemaName, @Nullable TextRange schemaNameRange) {
        this.tableName = tableName;
        this.tableNameRange = tableNameRange;
        this.schemaName = schemaName;
        this.schemaNameRange = schemaNameRange;
    }

    public static DbTableNameInfo createFromString(String value) {
        final String[] tokens = value.split("\\s+");
        if (tokens.length != 0) {
            String fullTableName = tokens[0];
            String[] tokens2 = fullTableName.split("\\.", 2);
            String tableName = fullTableName;

            String schemaName = null;
            TextRange tableNameRange, schemaNameRange = null;

            if (tokens2.length > 1) {
                // Provide reference for db schema and table separately
                schemaName = tokens2[0];
                tableName = tokens2[1];
                final int offset = schemaName.length() + 1;
                tableNameRange = new TextRange(offset, offset + tableName.length());
                schemaNameRange = new TextRange(0, schemaName.length());
            } else {
                tableNameRange = new TextRange(0, fullTableName.length());
            }
            return new DbTableNameInfo(tableName, tableNameRange, schemaName, schemaNameRange);
        }
        return null;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public TextRange getTableNameRange() {
        return tableNameRange;
    }

    public TextRange getSchemaNameRange() {
        return schemaNameRange;
    }
}
