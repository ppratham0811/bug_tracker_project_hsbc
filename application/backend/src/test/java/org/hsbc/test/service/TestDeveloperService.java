package org.hsbc.test.service;

import org.hsbc.dao.manager.ManagerDao;
import org.hsbc.exceptions.NoBugsAssignedException;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.model.Bug;
import org.hsbc.model.User;
import org.hsbc.model.enums.SeverityLevel;
import org.hsbc.service.developer.DeveloperService;
import org.junit.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestDeveloperService {
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
    public void testGetAssignedBugsDeveloper() throws NoBugsAssignedException, NotAuthorizedException {
        DeveloperService service = new DeveloperService();
        ManagerDao dao = new ManagerDao();
        User user = new User();
        Bug bug = new Bug("ZidBug", 1001, LocalDateTime.now().minusDays(1), SeverityLevel.HIGH, 711);
        dao.assignBugToDeveloper(user.getUserId(),bug, user);
        assertTrue(service.getAssignedBugs(user).size()==1);
    }
}
