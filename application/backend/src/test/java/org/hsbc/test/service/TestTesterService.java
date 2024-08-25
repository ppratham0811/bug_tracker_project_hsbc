package org.hsbc.test.service;

import org.hsbc.exceptions.BugsNotFoundException;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.Bug;
import org.hsbc.model.Project;
import org.hsbc.model.User;
import org.hsbc.model.enums.SeverityLevel;
import org.hsbc.service.manager.ManagerService;
import org.hsbc.service.tester.TesterService;
import org.junit.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

public class TestTesterService {
    @Before
    public void setUp(){
        System.out.println("Before Test");
    }
    @After
    public void tearDown(){
        System.out.println("After Test");
    }
    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }
    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }

    @Test
    public void testBugReport() throws UserNotFoundException, NotAuthorizedException {
        TesterService service = new TesterService();
        Project project = new Project("ZidProject", 1234, LocalDate.now().minusDays(1));
        Bug bug = new Bug("ZidBug", 1001, LocalDateTime.now().minusDays(1), SeverityLevel.HIGH, 711);
        User user = new User();
        boolean result =service.bugReport(user.getUserId(),bug,project);
        assertTrue(result);
    }

}
