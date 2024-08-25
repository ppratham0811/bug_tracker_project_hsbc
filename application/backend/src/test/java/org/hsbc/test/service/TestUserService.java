package org.hsbc.test.service;

import org.hsbc.exceptions.UserNotFoundException;
import org.hsbc.model.User;
import org.hsbc.model.enums.UserRole;
import org.hsbc.service.user.UserService;
import org.junit.*;

import static org.junit.Assert.assertTrue;

public class TestUserService {
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
    public void testViewUserDetails() throws UserNotFoundException {
        UserService service = new UserService();
        User user = new User("ZIDusername","ZID","ZID@hbc.co.in", UserRole.MANAGER);
        User testUser = service.viewUserDetails(user.getUserId());
        assertTrue(testUser.getUserId()==user.getUserId());
    }
    @Test(expected = UserNotFoundException.class)
    public void testViewUserDetailException()throws UserNotFoundException{
        UserService service = new UserService();
        User user = new User("ZIDusername","ZID","ZID@hbc.co.in", UserRole.MANAGER);
        User testUser = service.viewUserDetails(user.getUserId());
        assertTrue(testUser.getUserId()==100);
    }
}
