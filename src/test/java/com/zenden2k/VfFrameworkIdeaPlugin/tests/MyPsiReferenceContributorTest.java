package com.zenden2k.VfFrameworkIdeaPlugin.tests;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.XmlPatterns;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import com.jetbrains.php.PhpIndex;
import com.zenden2k.VfFrameworkIdeaPlugin.config.VfPluginSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static com.intellij.testFramework.LightProjectDescriptor.EMPTY_PROJECT_DESCRIPTOR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MainThreadInvocationInterceptor.class)
public class MyPsiReferenceContributorTest extends MyLightCodeInsightFixtureTestCase {
    @BeforeEach
    public void setUp() throws Exception {
        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
        IdeaProjectTestFixture testFixture = factory.createLightFixtureBuilder(EMPTY_PROJECT_DESCRIPTOR).getFixture();
        myFixture = factory.createCodeInsightFixture(testFixture, new LightTempDirTestFixtureImpl(true));

        final String dataPath = new File("").getAbsolutePath() + File.separatorChar + getTestDataPath();

        myFixture.setTestDataPath(dataPath);

        myFixture.setUp();

        final PropertiesComponent projectProperties = PropertiesComponent.getInstance(myFixture.getProject());
        projectProperties.setValue(VfPluginSettings.ENABLE_PLUGIN_KEY, true);

        myFixture.copyFileToProject("user.php");
        myFixture.copyFileToProject("user.xml");
        myFixture.copyFileToProject("guide.php", "vf-common-git/system/application/vf_controllers/guide/guide.php");
        myFixture.copyFileToProject("guide/test-guide.xml", "system/application/guide/test-guide.xml");
        myFixture.copyFileToProject("guide/test-guide.xml", "system/application/guide/guide2.xml");
    }

    protected String getTestDataPath() {
        return "src/test/java/com/zenden2k/VfFrameworkIdeaPlugin/tests/fixtures";
    }

    @Test
    public void testThatStaticDatasourceReferenceIsProvided() {
        PhpIndex index = PhpIndex.getInstance(myFixture.getProject());

        final VirtualFile[] vFiles =  ProjectRootManager.getInstance(myFixture.getProject()).getContentRoots();
        Collection<?> s = index.getAllClassFqns(null);
        /*"class CUser extends CXMLObject {\n" +
                        "  public function testMethod() {\n" +
                        "     $this->getStaticDatasource('t<caret>est');\n" +
                        "  }\n" +
                        "}\n"*/
        assertReferenceMatchOnParent(
                "test.php",
                """
                        <?php
                        $user = new \\CUser();
                        $user->getStaticDatasource('t<caret>est');"""
                ,
                XmlPatterns.xmlTag().withName("datasource.orm").withAttributeValue("name", "test")
//                PlatformPatterns.psiElement().withName("datasource.orm")
        );
    }

    @Test
    public void testGetGuideValueReference() {
        assertReferenceMatchOnParent(
                "test2.php",
                """
                        <?php
                        $guide = GetObject('guide');
                        $guide->getAssociative('t<caret>est-guide');"""
                ,
                XmlPatterns.xmlTag().withName("guide").withAttributeValue("name", "test-guide")
        );
    }

    @Test
    public void testGetGuideValueReferenceAutocomplete() {
        myFixture.configureByText(
                "test2.php",
                """
                        <?php
                        $guide = GetObject('guide');
                        $guide->getAssociative('<caret>');"""
        );
        myFixture.completeBasic();
        List<String> completion = myFixture.getLookupElementStrings();
        assertNotNull(completion);
        assertTrue(completion.contains("test-guide"));
        assertTrue(completion.contains("guide2"));
    }

    @AfterEach
    public void tearDown() throws Exception {
        myFixture.tearDown();
    }
}
