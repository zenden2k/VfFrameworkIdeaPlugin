package com.zenden2k.VfFrameworkIdeaPlugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.css.resolve.HtmlCssClassOrIdReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.zenden2k.VfFrameworkIdeaPlugin.config.VfPluginSettings;
import com.zenden2k.VfFrameworkIdeaPlugin.reference.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlReferenceProvider extends PsiReferenceProvider {

    final Pattern pattern, pattern2;

    public XmlReferenceProvider() {
        this.pattern = Pattern.compile("\\S+");
        pattern2 = Pattern.compile("system/application/vf_controllers/([^/]+)/");
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull final ProcessingContext context) {
        final Project project = element.getProject();
        final PropertiesComponent projectProperties = PropertiesComponent.getInstance(project);
        final boolean enablePlugin = projectProperties.getBoolean(VfPluginSettings.ENABLE_PLUGIN_KEY, VfPluginSettings.getEnablePluginDefaultValue(project));

        if (!enablePlugin) {
            return PsiReference.EMPTY_ARRAY;
        }

        final boolean enableDataBaseReferences = projectProperties.getBoolean(VfPluginSettings.ENABLE_DATABASE_REFERENCES, VfPluginSettings.ENABLE_DATABASE_REFERENCES_DEFAULT_VALUE);

        if (element instanceof XmlAttributeValue) {
            XmlAttributeValue xmlAttrValue = (XmlAttributeValue)element;
            PsiElement parentEl = xmlAttrValue.getParent();
            String attrValue = xmlAttrValue.getValue();
            if (parentEl instanceof XmlAttribute) {
                XmlAttribute xmlAttr = (XmlAttribute)parentEl;
                String name = xmlAttr.getName();
                XmlTag parent =  xmlAttr.getParent();

                String parentName = parent.getName();
                final boolean isDepended = parentName.equals("depended");
                if (name.equals("method") && parentName.equals("datasource.orm")) {
                    TextRange textRange = xmlAttrValue.getTextRange();
                    TextRange valueTextRange = xmlAttrValue.getValueTextRange();
                    valueTextRange = valueTextRange.shiftLeft(textRange.getStartOffset());
                    PsiReference ref = new DatasourceMethodReference(null, xmlAttrValue.getValue(), element, valueTextRange, project);
                    return new PsiReference[]{ref};
                } else if (parentName.equals("datasource.link") || isDepended) {
                    String dsType = parent.getAttributeValue("static");
//                    final boolean isStatic = dsType != null && dsType.equals("true");
                    final boolean isMethod = dsType != null && dsType.equals("method");

                    if (name.equals("datasource")) {
                        if (!isDepended || !isMethod){
                            String linkType = parent.getAttributeValue("type");
                            if (linkType == null || linkType.equals("object")){
                                String objectName = parent.getAttributeValue("object");
                                if (objectName != null) {
                                    PsiReference ref = new StaticDataSourceReference(xmlAttrValue.getValue(), element, getTextRange(xmlAttrValue), project, objectName);
                                    return new PsiReference[]{ref};
                                }
                            }
                        } else {
                            String objectName = parent.getAttributeValue("object");
                            if (objectName != null) {
                                PsiReference ref = new DatasourceMethodReference(objectName, xmlAttrValue.getValue(), element, getTextRange(xmlAttrValue), project);
                                return new PsiReference[]{ref};
                            }
                        }

                    } else if (name.equals("object")) {
                        PsiReference ref = new XmlObjectReference(xmlAttrValue.getValue(), element, getTextRange(xmlAttrValue), project);
                        return new PsiReference[]{ref};
                    }

                } else if (name.equals("controller") && parentName.equals("cms.controller")) {
                    // Попалась ссылка на контроллер на странице
                    String moduleName = parent.getAttributeValue("module");
                    if (moduleName != null) {
                        String controllerName = xmlAttrValue.getValue();
                        PsiReference ref = new XmlControllerReference(moduleName, controllerName, element, getTextRange(xmlAttrValue), project);
                        return new PsiReference[]{ref};
                    }
                } else if ( name.equals("form") && parentName.equals("form.link")) {
                    // Попалась ссылка на форму
                    String moduleName = parent.getAttributeValue("module");
                    if (moduleName != null) {
                        String controllerName = xmlAttrValue.getValue();
                        PsiReference ref = new XmlFormReference(moduleName, controllerName, element, getTextRange(xmlAttrValue), project);
                        return new PsiReference[]{ref};
                    }
                } else if (parentName.equals("orm.link")) {
                    if (name.equals("object")) {
                        PsiReference ref = new XmlObjectReference(xmlAttrValue.getValue(), element, getTextRange(xmlAttrValue), project);
                        return new PsiReference[]{ref};
                    } else if (name.equals("method")) {
                        String objectName = parent.getAttributeValue("object");
                        if (objectName != null) {
                            PsiReference ref = new DatasourceMethodReference(objectName, xmlAttrValue.getValue(), element, getTextRange(xmlAttrValue), project);
                            return new PsiReference[]{ref};
                        }
                    }
                } else if (name.equals("guide") && (parentName.equals("field") || parentName.startsWith("form.control"))) {
                    String guideName = xmlAttrValue.getValue();
                    if (!guideName.isEmpty()) {
                        PsiReference ref = new XmlGuideReference(guideName, element, getTextRange(xmlAttrValue), project);
                        return new PsiReference[]{ref};
                    }
                } else if (name.equals("view") && parentName.equals("field")) {
                    String path = xmlAttrValue.getContainingFile().getVirtualFile().getPath();
                    Matcher matcher = pattern2.matcher(path);
                    if (matcher.find()) {
                        String objectName = matcher.group(1);
                        PsiReference ref = new DataViewReference(xmlAttrValue.getValue(), objectName, element, getTextRange(xmlAttrValue), project);
                        return new PsiReference[]{ref};
                    }

                } else if (name.equals("class")) {
                    String value = xmlAttrValue.getValue();
                    TextRange range = getTextRange(xmlAttrValue);
                    final ArrayList<PsiReference> referenceList = new ArrayList<PsiReference>();

                    Matcher matcher = pattern.matcher(value);
                    while (matcher.find()) {
                        PsiReference ref = new HtmlCssClassOrIdReference(element, range.getStartOffset() + matcher.start(),
                                range.getStartOffset() + matcher.end(), true, true);
                        referenceList.add(ref);
                    }
                    return referenceList.toArray(new PsiReference[referenceList.size()]);
                } else if (enableDataBaseReferences && name.equals("table") && parentName.equals("object")) {
                    // Reference to database table
                    // TODO: use DbTableNameInfo class
                    String[] tokens = attrValue.split("\\s+");
                    if (tokens.length !=0 ) {
                        TextRange range = getTextRange(xmlAttrValue);
                        String fullTableName = tokens[0];
                        String[] tokens2 = fullTableName.split("\\.", 2);
                        String tableName = fullTableName;

                        String schemaName = null;

                        if (tokens2.length > 1 ) {
                            // Provide reference for db schema and table separately
                            schemaName = tokens2[0];
                            tableName = tokens2[1];
                            int offset = schemaName.length()+1;
                            PsiReference tableRef = new DbTableReference(tableName, schemaName, element, new TextRange(range.getStartOffset()+offset, range.getStartOffset() + offset + tableName.length()) , project);
                            PsiReference schemaRef = new DbSchemaReference(schemaName, element, new TextRange(range.getStartOffset(), range.getStartOffset() + schemaName.length()) , project);
                            PsiReference[] refs = new PsiReference[2];
                            refs[0] = schemaRef;
                            refs[1] = tableRef;
                            return refs;
                        } else {
                            PsiReference ref = new DbTableReference(tableName, schemaName, element, new TextRange(range.getStartOffset(), range.getStartOffset() + fullTableName.length()) , project);
                            return new PsiReference[]{ref};
                        }
                    }
                } else if (enableDataBaseReferences && name.equals("name") && parentName.equals("field")) {
                    // Reference to database column
                    XmlTag fieldsTag = parent.getParentTag();
                    if (fieldsTag != null && fieldsTag.getName().equals("fields")) {
                        XmlTag objectTag = fieldsTag.getParentTag();
                        if (objectTag != null && objectTag.getName().equals("object")) {
                            String table = objectTag.getAttributeValue("table");
                            if (table != null && !table.isEmpty()) {
                                PsiReference ref = new DbFieldReference(attrValue, table, element, getTextRange(xmlAttrValue) , project);
                                return new PsiReference[]{ref};
                            }
                        }
                    }
                }
            }
        }

        return PsiReference.EMPTY_ARRAY;
    }

    protected static TextRange getTextRange(XmlAttributeValue xmlAttrValue) {
        TextRange textRange = xmlAttrValue.getTextRange();
        TextRange valueTextRange = xmlAttrValue.getValueTextRange();
        return valueTextRange.shiftLeft(textRange.getStartOffset());
    }

}
