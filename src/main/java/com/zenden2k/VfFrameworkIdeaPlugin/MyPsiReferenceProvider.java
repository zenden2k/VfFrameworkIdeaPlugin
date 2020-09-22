package com.zenden2k.VfFrameworkIdeaPlugin;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.zenden2k.VfFrameworkIdeaPlugin.config.VfPluginSettings;
import com.zenden2k.VfFrameworkIdeaPlugin.reference.PhpObjectReference;
import com.zenden2k.VfFrameworkIdeaPlugin.reference.PhpGuideReference;
import com.zenden2k.VfFrameworkIdeaPlugin.reference.StaticDataSourceReference;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MyPsiReferenceProvider extends PsiReferenceProvider {

    public MyPsiReferenceProvider() {
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

        if (element instanceof StringLiteralExpression) {
            final StringLiteralExpression literalExpression = (StringLiteralExpression)element;
            final String objectName = literalExpression.getContents();

            if (isGetObjectCall(literalExpression)) {
                final PsiReference ref = new PhpObjectReference(objectName, element, literalExpression.getValueRange(), project);
                return new PsiReference[]{ref};
            } else  {
                //  check if is getStaticDataSource call
                PsiElement prevEl = element.getParent();
                if (prevEl == null) {
                    return PsiReference.EMPTY_ARRAY;
                }
                prevEl = prevEl.getParent();

                if (prevEl instanceof MethodReference) {
                    final MethodReference methodRef = (MethodReference)prevEl;
                    final PsiElement[] parameters = methodRef.getParameters();
                    // Check if it is first argument
                    if (parameters.length !=0 && parameters[0] == literalExpression) {
                        final String methodName = methodRef.getName();
                        if (methodName!= null) {
                            final String methodNameLower = methodName.toLowerCase(Locale.ROOT);
                            if (methodNameLower.equals("getstaticdatasource")) {
                                final PhpExpression classRef = methodRef.getClassReference();
                                if (classRef != null) {
                                    PhpType type = classRef.getType();
                                    PsiReference ref = new StaticDataSourceReference(objectName, element, literalExpression.getValueRange(), project, type);
                                    return new PsiReference[]{ref};

                                }
                            } else if (methodNameLower.equals("getassociative")
                                    || methodNameLower.equals("getassociativevalue")
                                    || methodNameLower.equals("getassociativeobject"
                            )) {
                                final PsiReference ref = new PhpGuideReference(objectName, element, literalExpression.getValueRange(), project, methodRef);
                                return new PsiReference[]{ref};
                            }
                        }
                    }
                }

            }
        }

        return PsiReference.EMPTY_ARRAY;
    }

    public static boolean isGetObjectCall(@NotNull StringLiteralExpression element) {
        PsiElement prevEl = element.getParent();
        if (prevEl == null) {
            return false;
        }
        prevEl = prevEl.getParent();

        if (prevEl instanceof FunctionReference && !(prevEl instanceof MethodReference)) {
            final FunctionReference funcRef = (FunctionReference) prevEl;
            final String funcName = funcRef.getName();
            return funcName != null && funcRef.getName().toLowerCase(Locale.ROOT).equals("getobject");
        }
        return false;
    }
}
