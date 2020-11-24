package com.zenden2k.VfFrameworkIdeaPlugin;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;

import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.smarty.SmartyFile;
import com.jetbrains.smarty.lang.SmartyTokenTypes;
import com.jetbrains.smarty.lang.psi.SmartyTag;
import org.jetbrains.annotations.NotNull;

public class MyLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces places) {

        if (!(host.getContainingFile() instanceof SmartyFile)) {
            return;
        }
        if (!(host instanceof SmartyTag)) {
            return;
        }

        SmartyTag tag = (SmartyTag)host;
        ASTNode[] children = tag.getNode().getChildren(null);
        PsiElement el = tag.getAttributeValue("code");
        Language  lang = Language.findLanguageByID("JavaScript");
        for(ASTNode node: children) {
            IElementType type = node.getElementType();


            if (PhpPsiUtil.isOfType(node, SmartyTokenTypes.STRING_LITERAL)) {
                places.addPlace(lang, new TextRange(node.getStartOffsetInParent(), node.getStartOffsetInParent()+node.getTextLength()), "", "" );
                return;
            }
        }



    }
}
