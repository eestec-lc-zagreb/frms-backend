package hr.eestec_zg.frmsbackend;

import hr.eestec_zg.frmscore.domain.dto.TaskStatisticsDto;
import hr.eestec_zg.frmscore.domain.models.Company;
import hr.eestec_zg.frmscore.domain.models.CompanyType;
import hr.eestec_zg.frmscore.domain.models.Event;
import hr.eestec_zg.frmscore.domain.models.Role;
import hr.eestec_zg.frmscore.domain.models.SponsorshipType;
import hr.eestec_zg.frmscore.domain.models.Task;
import hr.eestec_zg.frmscore.domain.models.TaskStatus;
import hr.eestec_zg.frmscore.domain.models.User;
import hr.eestec_zg.frmscore.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.DUMMY_VALUE;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_FIRST_NAME_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_FIRST_NAME_2;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_LAST_NAME_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_LAST_NAME_2;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_MAIL_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_MAIL_2;
import static org.junit.Assert.assertEquals;

public class UserControllerTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private UserService userService;
    private User testUser1;

    @Before
    public void setTestData() {
        testUser1 = new User(
                TEST_USER_FIRST_NAME_1,
                TEST_USER_LAST_NAME_1,
                TEST_USER_MAIL_1,
                DUMMY_VALUE,
                DUMMY_VALUE,
                Role.USER,
                null
        );

        User testUser2 = new User(
                TEST_USER_FIRST_NAME_2,
                TEST_USER_LAST_NAME_2,
                TEST_USER_MAIL_2,
                DUMMY_VALUE,
                DUMMY_VALUE,
                Role.COORDINATOR,
                null
        );

        userService.createUser(testUser1);
        userService.createUser(testUser2);
    }

    @Test
    @WithMockUser
    public void testReadingSingleUser() throws Exception {
        final String url = "/users/" + testUser1.getId();
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());
        User user = jacksonService.readJson(response.getContentAsString(), User.class);

        assertEquals(testUser1.getId(), user.getId());
        assertEquals(TEST_USER_FIRST_NAME_1, user.getFirstName());
        assertEquals(TEST_USER_LAST_NAME_1, user.getLastName());
        assertEquals(TEST_USER_MAIL_1, user.getEmail());
    }

    @Test
    @WithMockUser
    public void testReadingSingleNonExistingUser() throws Exception {
        final String url = "/users/" + -1L;
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    public void testReadingAllUsers() throws Exception {
        final String url = "/users";
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        List<User> users = jacksonService.readListOfObjects(response.getContentAsString(), User.class);
        assertEquals(2, users.size());

        User testUser2 = users.get(1);
        assertEquals(testUser1, users.get(0));
        assertEquals(TEST_USER_FIRST_NAME_1, testUser2.getFirstName());
        assertEquals(TEST_USER_LAST_NAME_1, testUser2.getLastName());
    }

    @Test
    @WithMockUser
    public void testDeletingUser() throws Exception {
        long userId = userService.getUserByEmail(TEST_USER_MAIL_1).getId();
        String url = "/users/" + userId;

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());
        logger.debug("Sending request on {}", url);

        response = delete(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        logger.debug("Sending request on {}", url);

        response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }


    @Test
    @WithMockUser
    public void testCreationOfUser() throws Exception {
        User testUser1 = new User("Fifo", "Lifo", "email@example.com", "pass", "02001", Role.USER, null);

        String url = "/users";
        String c2Json = jacksonService.asJson(testUser1);
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = post(url, c2Json);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(201, response.getStatus());

        url = "/users";
        response = get(url);
        List<User> users = jacksonService.readListOfObjects(response.getContentAsString(), User.class);
        assertEquals(3, users.size());
        User testUser2 = users.get(2);

        assertEquals("Fifo", testUser2.getFirstName());
        assertEquals("Lifo", testUser2.getLastName());
    }

    @Test
    @WithMockUser
    public void testUpdatingCompany() throws Exception {
        User testUser = userService.getUserByEmail(TEST_USER_MAIL_1);

        String url = "/users/" + testUser.getId();

        logger.debug("Sending GET request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        testUser.setFirstName("FF");
        testUser.setPhoneNumber("3232323");

        String testUserJson = jacksonService.asJson(testUser);

        logger.debug("Sending PUT request on {}", url);

        response = put(url, testUserJson);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        testUser = userService.getUserByEmail(TEST_USER_MAIL_1);
        logger.debug("Sending GET request on {}", url);
        url = "/users/" + testUser.getId();
        response = get(url);
        logger.debug("Response: {}", response.getContentAsString());
        User user = jacksonService.readJson(response.getContentAsString(), User.class);

        assertEquals("FF", user.getFirstName());
        assertEquals(TEST_USER_LAST_NAME_1, user.getLastName());
        assertEquals("3232323", user.getPhoneNumber());
    }

    @Test
    @WithMockUser
    public void testUpdatingNonExistingUser() throws Exception {
        User testUser = new User("Fifo", "Lifo", "email@example.com", "pass", "02001", Role.USER, null);

        String url = "/users/" + -1L;
        String testUserJson = jacksonService.asJson(testUser);

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = put(url, testUserJson);

        logger.debug("Response: {}", response.getContentAsString());
        url = "/users/" + -1L;

        logger.debug("Sending GET request on {}", url);

        response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    public void testGettingUserTasks() throws Exception {
        User testUser = userService.getUserByEmail(TEST_USER_MAIL_1);
        Event testEvent = new Event("EV", "EV", "2017");

        eventRepository.createEvent(testEvent);

        Company testCompany = new Company("CP", "CP", CompanyType.COMPUTING);
        companyRepository.createCompany(testCompany);

        Task task = new Task(
                testEvent,
                testCompany,
                testUser,
                SponsorshipType.FINANCIAL,
                null, null, null,
                TaskStatus.IN_PROGRESS,
                ""
        );
        taskRepository.createTask(task);

        final String url = "/users/" + testUser.getId() + "/tasks";
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());
        List<Task> tasks = jacksonService.readListOfObjects(response.getContentAsString(), Task.class);

        assertEquals(1, tasks.size());

    }

    @Test
    @WithMockUser
    public void testGettingNonExistingUserTasks() throws Exception {
        User testUser = userService.getUserByEmail(TEST_USER_MAIL_1);

        final String url = "/users/" + testUser.getId() + "/tasks";
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());
        List<Task> tasks = jacksonService.readListOfObjects(response.getContentAsString(), Task.class);

        assertEquals(0, tasks.size());
    }

    @Test
    @WithMockUser
    public void testGetStatisticsForUser() throws Exception {
        User testUser = userService.getUserByEmail(TEST_USER_MAIL_1);

        final String url = "/users/" + testUser.getId() + "/statistics";
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());
        TaskStatisticsDto statistics = jacksonService.readJson(response.getContentAsString(), TaskStatisticsDto.class);

        assertEquals(0, (long) statistics.getSuccessful());
        assertEquals(0, (long) statistics.getUnsuccessful());
        assertEquals(0, (long) statistics.getNumberOfEvents());
    }
}
