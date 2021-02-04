package com.zenden2k.VfFrameworkIdeaPlugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import com.zenden2k.VfFrameworkIdeaPlugin.config.VfPluginSettings;
import com.zenden2k.VfFrameworkIdeaPlugin.utils.XmlUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

public class GetStaticDatasourceFactoryTypeProvider implements PhpTypeProvider4 {
    @Override
    public char getKey() {
        return '\u0247';
    }

    @Override
    public @Nullable PhpType getType(PsiElement e) {
        if (e instanceof MethodReference) {
            final Project project = e.getProject();
            final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
            final boolean enablePlugin = propertiesComponent.getBoolean(
                    VfPluginSettings.ENABLE_PLUGIN_KEY, VfPluginSettings.getEnablePluginDefaultValue(project)
            );

            if (!enablePlugin) {
                return null;
            }

            final boolean enableGetStaticDatasourceTypeProviding = propertiesComponent.getBoolean(
                    VfPluginSettings.ENABLE_GETSTATICDATASOURCE_TYPE_PROVIDING_KEY,
                    VfPluginSettings.ENABLE_GETSTATICDATASOURCE_TYPE_PROVIDING_DEFAULT_VALUE
            );

            if (!enableGetStaticDatasourceTypeProviding) {
                return null;
            }

            final MethodReference funcRef = (MethodReference)e;
            final String funcName = funcRef.getName();
            if (funcName != null) {
                if (funcName.toLowerCase(Locale.ROOT).equals("getstaticdatasource")) {
                    final PsiElement[] parameters = funcRef.getParameters();
                    if (parameters.length > 0) {
                        final PsiElement parameter = parameters[0];
                        if (parameter instanceof StringLiteralExpression) {
                            final String param = ((StringLiteralExpression) parameter).getContents();

                            if (StringUtil.isNotEmpty(param)) {
                                final PhpExpression classRef = funcRef.getClassReference();
                                if (classRef != null) {
                                    final PhpType classRefType = classRef.getType();

                                    if (classRefType.toString().startsWith("\\")) {
                                        return new PhpType().add("#" + this.getKey() + classRefType.toString() + this.getKey() + param);
                                    }

                                    for (String type : classRefType.getTypes()) {
                                        if (type.startsWith("#" + FactoryTypeProvider.TYPE_KEY)) {
                                            return new PhpType().add("#" + this.getKey() + type + this.getKey() + param);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable PhpType complete(String s, Project project) {
        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> set, int i, Project project) {
        final int delimiterPos = expression.indexOf(this.getKey());
        final String fqn = expression.substring(0, delimiterPos);
        final PhpIndex index = PhpIndex.getInstance(project);
        final String datasourceName = expression.substring(delimiterPos+1);
        Collection<? extends PhpNamedElement> classes;
        if (fqn.startsWith("\\")) {
            classes = index.getClassesByFQN(fqn);
        } else {
            classes = index.getBySignature(fqn, set, i);
        }

        if (!classes.isEmpty()) {
            final PhpNamedElement el = classes.iterator().next();
            final PsiFile file = el.getContainingFile();
            final String fileName = file.getName();
            final PsiDirectory dir = file.getContainingDirectory();
            final PsiFile xmlPsiFile = dir.findFile(fileName.replace(".php", ".xml"));
            if (xmlPsiFile instanceof XmlFile) {
                XmlTag tag = XmlUtils.findDataSource((XmlFile) xmlPsiFile, datasourceName);
                if (tag != null) {
                    String tagName = tag.getName();
                    if (tagName.startsWith("datasource.")) {
                        String prefix = tagName.replace("datasource.", "");
                        return index.getAnyByFQN("\\C" + prefix + "DataSource");
                    }
                }
            }
        }

        return index.getAnyByFQN("\\CORMDataSource");
    }
}
