package teammates.test.cases.ui.browsertests;

import java.lang.reflect.Constructor;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.StudentCourseJoinConfirmationPage;
import teammates.test.pageobjects.StudentHomePage;

public class StudentCourseJoinConfirmationPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private static StudentCourseJoinConfirmationPage confirmationPage;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentCourseJoinConfirmationPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);

        browser = BrowserPool.getBrowser(true);
        browser.driver.manage().deleteAllCookies();
        AppPage.logout(browser);
    }

    @Test
    public void testAll() throws Exception {
        
        testContent();
        testJoinNewConfirmation();
        // TODO: remove this test by 21/09/2014
        testJoinConfirmation();
    }
    
    
    private void testJoinNewConfirmation() {
        String expectedMsg;
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.STUDENT_COURSE_JOIN_NEW;
        String homePageActionUrl = TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.STUDENT_HOME_PAGE;
        String joinLink;
        StudentHomePage studentHomePage;
        
        ______TS("click join link, skips confirmation and asks for login");

        String courseId = testData.courses.get("SCJConfirmationUiT.CS2104").id;
        String studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2104").email;
        joinLink = new Url(joinActionUrl)
                        .withRegistrationKey(BackDoor.getKeyForStudent(courseId, studentEmail))
                        .withCourseId(courseId)
                        .withStudentEmail(studentEmail)
                        .toString();
        
        browser.driver.get(joinLink);
        studentHomePage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsStudent(TestProperties.inst().TEST_STUDENT1_ACCOUNT,
                                                  TestProperties.inst().TEST_STUDENT1_PASSWORD);
        studentHomePage.verifyStatus(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, courseId));
        
        ______TS("test student confirmation page content");
        
        courseId = testData.courses.get("SCJConfirmationUiT.CS2103").id;
        studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2103").email;
        joinLink = Url.addParamToUrl(joinActionUrl,Const.ParamsNames.REGKEY,
                BackDoor.getKeyForStudent(courseId, studentEmail));
        
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        // this test uses accounts from test.properties. 
        // NOTE: the logout link at the bottom of the page has to be changed to {*}
        //       since the link is different in dev and staging servers
        confirmationPage.verifyHtml("/studentCourseJoinConfirmationHTML.html");
        
        ______TS("Cancelling goes to login page");
        createCorrectLoginPageType(confirmationPage.clickCancelButtonAndGetSourceOfDestination());
        
        ______TS("Confirming goes to home page");
        browser.driver.get(homePageActionUrl);
        studentHomePage = createCorrectLoginPageType(browser.driver.getPageSource())
                            .loginAsStudent(TestProperties.inst().TEST_STUDENT1_ACCOUNT,
                                       TestProperties.inst().TEST_STUDENT1_PASSWORD);
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();
        studentHomePage = createNewPage(browser, StudentHomePage.class);
        studentHomePage.verifyStatus(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, courseId));

        ______TS("already joined, no confirmation page");

        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();
        
        studentHomePage = createNewPage(browser, StudentHomePage.class);
        expectedMsg = "You (" + TestProperties.inst().TEST_STUDENT1_ACCOUNT + ") have already joined this course";
        studentHomePage.verifyStatus(expectedMsg);

        assertTrue(browser.driver.getCurrentUrl().contains(Const.ParamsNames.ERROR + "=true"));
        studentHomePage.logout();
    }


    private void testContent(){
        
        /*covered in testJoinConfirmation() 
         *case: click join link then confirm: success: valid key
         */
    }
     
    
    private void testJoinConfirmation() throws Exception {
        AppPage.logout(browser);
        removeAndRestoreTestDataOnServer(testData);
        String expectedMsg;
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.STUDENT_COURSE_JOIN;
        String homePageActionUrl = TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.STUDENT_HOME_PAGE;
        String joinLink;
        StudentHomePage studentHomePage;
        
        ______TS("click join link, skips confirmation and asks for login");

        String courseId = testData.courses.get("SCJConfirmationUiT.CS2104").id;
        String studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2104").email;
        joinLink = Url.addParamToUrl(joinActionUrl,Const.ParamsNames.REGKEY,
                                     BackDoor.getKeyForStudent(courseId, studentEmail));
        
        browser.driver.get(joinLink);
        studentHomePage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsStudent(TestProperties.inst().TEST_STUDENT1_ACCOUNT,
                                                  TestProperties.inst().TEST_STUDENT1_PASSWORD);
        studentHomePage.verifyStatus(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, courseId));
        
        
        ______TS("test student confirmation page content");
        
        courseId = testData.courses.get("SCJConfirmationUiT.CS2103").id;
        studentEmail = testData.students.get("alice.tmms@SCJConfirmationUiT.CS2103").email;
        joinLink = Url.addParamToUrl(joinActionUrl,Const.ParamsNames.REGKEY,
                BackDoor.getKeyForStudent(courseId, studentEmail));
        
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        // this test uses accounts from test.properties
        confirmationPage.verifyHtml("/studentCourseJoinConfirmationHTML.html");
        
        ______TS("Cancelling goes to login page");
        createCorrectLoginPageType(confirmationPage.clickCancelButtonAndGetSourceOfDestination());
        
        ______TS("Confirming goes to home page");
        browser.driver.get(homePageActionUrl);
        studentHomePage = createCorrectLoginPageType(browser.driver.getPageSource())
                            .loginAsStudent(TestProperties.inst().TEST_STUDENT1_ACCOUNT,
                                       TestProperties.inst().TEST_STUDENT1_PASSWORD);
        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();
        studentHomePage = createNewPage(browser, StudentHomePage.class);
        studentHomePage.verifyStatus(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, courseId));

        ______TS("already joined, no confirmation page");

        browser.driver.get(joinLink);
        confirmationPage = createNewPage(browser, StudentCourseJoinConfirmationPage.class);
        confirmationPage.clickConfirmButton();
        
        studentHomePage = createNewPage(browser, StudentHomePage.class);
        expectedMsg = "You (" + TestProperties.inst().TEST_STUDENT1_ACCOUNT + ") have already joined this course";
        studentHomePage.verifyStatus(expectedMsg);

        assertTrue(browser.driver.getCurrentUrl().contains(Const.ParamsNames.ERROR + "=true"));
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BackDoor.removeDataBundleFromDb(testData);
        BrowserPool.release(browser);
    }

    private LoginPage createCorrectLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :" + pageSource);
        }
    }

    private <T extends AppPage> T createNewPage(Browser browser, Class<T> typeOfPage) {
        Constructor<T> constructor;
        try {
            constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(browser);
            PageFactory.initElements(browser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
