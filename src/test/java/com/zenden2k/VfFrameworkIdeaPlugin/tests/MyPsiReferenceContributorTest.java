package com.zenden2k.VfFrameworkIdeaPlugin.tests;


import com.intellij.patterns.PlatformPatterns;



public class MyPsiReferenceContributorTest extends MyLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();

        myFixture.copyFileToProject("GetStaticDatasourceFixture.php");
        myFixture.copyFileToProject("user.xml");
    }

    protected String getTestDataPath() {
        return "src/test/com/zenden2k/VfFrameworkIdeaPlugin/tests/fixtures";
    }

    public void testThatStaticDatasourceReferenceIsProvided() {
        assertReferenceMatchOnParent(
                "user.php",
                "<?php \n" +
                        "class MyClass extends CXMLObject {\n" +
                        "  public function test() {\n" +
                        "     $this->getStaticDatasource('t<caret>est');\n" +
                        "  }\n" +
                        "}\n"
                        ,
                PlatformPatterns.psiElement().withName("test")
        );
    }


}
