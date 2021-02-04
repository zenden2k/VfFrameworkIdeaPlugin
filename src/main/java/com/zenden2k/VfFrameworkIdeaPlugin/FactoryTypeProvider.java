package com.zenden2k.VfFrameworkIdeaPlugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import com.zenden2k.VfFrameworkIdeaPlugin.config.VfPluginSettings;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FactoryTypeProvider /*extends CompletionContributor*/ implements PhpTypeProvider4 {
    //private final static Logger LOG = Logger.getInstance(FactoryTypeProvider.class);
    public static final char TYPE_KEY = '\u0246';

    @Override
    public char getKey() {
        return TYPE_KEY;
    }

    @Override
    public @Nullable PhpType getType(PsiElement e) {
        if (e instanceof FunctionReference && !(e instanceof MethodReference) ) {
            final Project project = e.getProject();
            final boolean enablePlugin = PropertiesComponent.getInstance(project).getBoolean(VfPluginSettings.ENABLE_PLUGIN_KEY, VfPluginSettings.getEnablePluginDefaultValue(project));
            if (!enablePlugin) {
                return null;
            }
            final FunctionReference funcRef = (FunctionReference)e;
            final String funcName = funcRef.getName();
            if (funcName != null) {
                if (funcName.toLowerCase(Locale.ROOT).equals("getobject")) {
                    final PsiElement[] parameters = funcRef.getParameters();
                    if (parameters.length > 0) {
                        PsiElement parameter = parameters[0];
                        if (parameter instanceof StringLiteralExpression) {
                            final String param = ((StringLiteralExpression) parameter).getContents();

                            if (StringUtil.isNotEmpty(param)) {
                                return new PhpType().add("#" + getKey() + param);
                            }
                        }
                    }
                } else if (funcName.toLowerCase(Locale.ROOT).equals("get_instance")) {
                    return new PhpType().add("#" + getKey() + "\\Vf");
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
        final int delimiterPos = expression.indexOf(':');
        if (delimiterPos  == -1) {
            if (!expression.isEmpty() && expression.charAt(0) == '\\' ) {
                return PhpIndex.getInstance(project).getAnyByFQN(expression);
            } else {
                return PhpIndex.getInstance(project).getAnyByFQN("\\C" + expression);
            }

        } else {
            final String path = expression.substring(0, delimiterPos);
            final String objectName = expression.substring(delimiterPos+1);
            final Collection<? extends PhpNamedElement> res = PhpIndex.getInstance(project).getAnyByFQN("\\C" + objectName);
            for(PhpNamedElement el: res) {
                String filePath = el.getContainingFile().getContainingDirectory().getVirtualFile().getPath();
                if(filePath.contains(path)) {
                    return Collections.singletonList(el);
                }
            }
            return res;
        }
    }
}
