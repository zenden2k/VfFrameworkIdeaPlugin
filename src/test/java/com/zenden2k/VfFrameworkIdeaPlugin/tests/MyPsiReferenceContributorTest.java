package com.zenden2k.VfFrameworkIdeaPlugin.tests;


import com.intellij.patterns.PlatformPatterns;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

@ExtendWith(MainThreadInvocationInterceptor.class)
public class MyPsiReferenceContributorTest extends MyLightCodeInsightFixtureTestCase {
    @BeforeEach
    public void setUp() throws Exception {
        final IdeaTestFixtureFactory fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();
        final TestFixtureBuilder<IdeaProjectTestFixture> testFixtureBuilder = fixtureFactory.createFixtureBuilder("kadam_old", true);
        myFixture = fixtureFactory.createCodeInsightFixture(testFixtureBuilder.getFixture());
        final String dataPath = new File("").getAbsolutePath() + File.separatorChar + getTestDataPath();
        myFixture.setTestDataPath(dataPath);
        myFixture.setUp();

        myFixture.copyFileToProject("GetStaticDatasourceFixture.php");
        myFixture.copyFileToProject("user.xml");
        //myFixture.enableInspections();
    }

    protected String getTestDataPath() {
        return "src/test/java/com/zenden2k/VfFrameworkIdeaPlugin/tests/fixtures";
    }

    @Test
    public void testThatStaticDatasourceReferenceIsProvided() {
        assertReferenceMatchOnParent(
                "user2.php",
                "<?php \n" +
                        /*"class CUser extends CXMLObject {\n" +
                        "  public function testMethod() {\n" +
                        "     $this->getStaticDatasource('t<caret>est');\n" +
                        "  }\n" +
                        "}\n"*/

                    "$user = new CUser();\n" +
                    "$user->getStaticDatasource('t<caret>est');"
                ,
                PlatformPatterns.psiElement().withName("test")
        );
    }


}
