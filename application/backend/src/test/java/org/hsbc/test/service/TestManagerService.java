package org.hsbc.test.service;

import org.hsbc.dao.manager.ManagerDao;
import org.hsbc.exceptions.NotAuthorizedException;
import org.hsbc.exceptions.ProjectLimitExceededException;
import org.hsbc.exceptions.WrongProjectDateException;
import org.hsbc.model.Project;
import org.hsbc.service.manager.ManagerService;
import org.junit.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

public class TestManagerService {
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
    public void testAddNewProject() throws WrongProjectDateException, ProjectLimitExceededException, NotAuthorizedException {
        ManagerService service = new ManagerService();

        Project project = new Project("ZidProject", 1234, LocalDate.now().minusDays(1));
        boolean result =service.addNewProject(project.getProjectId(), project);
        assertTrue(result);
    }


}
