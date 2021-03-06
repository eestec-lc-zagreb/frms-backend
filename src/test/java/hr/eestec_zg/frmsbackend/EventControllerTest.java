package hr.eestec_zg.frmsbackend;

import hr.eestec_zg.frmscore.domain.models.Company;
import hr.eestec_zg.frmscore.domain.models.CompanyType;
import hr.eestec_zg.frmscore.domain.models.Event;
import hr.eestec_zg.frmscore.domain.models.Role;
import hr.eestec_zg.frmscore.domain.models.SponsorshipType;
import hr.eestec_zg.frmscore.domain.models.Task;
import hr.eestec_zg.frmscore.domain.models.TaskStatus;
import hr.eestec_zg.frmscore.domain.models.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.DUMMY_VALUE;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_COMPANY_NAME_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_COMPANY_SHORT_NAME_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_NAME_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_NAME_2;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_NAME_3;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_SHORT_NAME_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_SHORT_NAME_2;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_SHORT_NAME_3;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_YEAR_1;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_YEAR_2;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_EVENT_YEAR_3;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_FIRST_NAME;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_LAST_NAME;
import static hr.eestec_zg.frmsbackend.utils.TestDataUtils.TEST_USER_MAIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class EventControllerTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(EventControllerTest.class);

    private Event event;

    @Before
    public void setTestData() {
        event = new Event(TEST_EVENT_NAME_1, TEST_EVENT_SHORT_NAME_1, TEST_EVENT_YEAR_1);
        Event event2 = new Event(TEST_EVENT_NAME_2, TEST_EVENT_SHORT_NAME_2, TEST_EVENT_YEAR_2);

        eventRepository.createEvent(event);
        eventRepository.createEvent(event2);

        User user = new User(
                TEST_USER_FIRST_NAME,
                TEST_USER_LAST_NAME,
                TEST_USER_MAIL,
                DUMMY_VALUE,
                DUMMY_VALUE,
                Role.USER,
                null
        );
        userRepository.createUser(user);

        Company company = new Company(TEST_COMPANY_NAME_1, TEST_COMPANY_SHORT_NAME_1, CompanyType.COMPUTING);
        companyRepository.createCompany(company);

        Task task = new Task(
                event,
                company,
                user,
                SponsorshipType.MATERIAL,
                null, null, null,
                TaskStatus.IN_PROGRESS,
                ""
        );
        taskRepository.createTask(task);
    }

    @Test
    @WithMockUser
    public void testReadingSingleEvent() throws Exception {
        final String url = "/events/" + event.getId();

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        Event event = jacksonService.readJson(response.getContentAsString(), Event.class);
        assertEquals(TEST_EVENT_NAME_1, event.getName());
        assertEquals(TEST_EVENT_SHORT_NAME_1, event.getShortName());
        assertEquals(TEST_EVENT_YEAR_1, event.getYear());
    }

    @Test
    @WithMockUser
    public void testReadingNonExistingEvent() throws Exception {
        final String url = "/events/" + -1L;

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    public void testReadingAllEvents() throws Exception {
        final String url = "/events";

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        List<Event> events = jacksonService.readListOfObjects(response.getContentAsString(), Event.class);
        assertEquals(2, events.size());

        Event testEvent = events.get(1);
        assertEquals(event, events.get(0));
        assertEquals(TEST_EVENT_NAME_2, testEvent.getName());
        assertEquals(TEST_EVENT_SHORT_NAME_2, testEvent.getShortName());
        assertEquals(TEST_EVENT_YEAR_2, testEvent.getYear());
    }

    @Test
    @WithMockUser
    public void testDeletingEvent() throws Exception {
        Event testEvent1 = eventRepository.getEventByName(TEST_EVENT_NAME_1);
        String url = "/events/" + testEvent1.getId();

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = delete(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        Event testEvent2 = eventRepository.getEventByName(TEST_EVENT_NAME_1);
        assertNull(testEvent2);
    }

    @Test
    @WithMockUser
    public void testDeletingNonExistingEvent() throws Exception {
        String url = "/events/" + 1L;

        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = delete(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    public void testCreationOfEvents() throws Exception {
        Event testEvent = new Event(TEST_EVENT_NAME_3, TEST_EVENT_SHORT_NAME_3, TEST_EVENT_YEAR_3);
        String url = "/events";

        String testEventJson = jacksonService.asJson(testEvent);
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = post(url, testEventJson);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(201, response.getStatus());

        List<Event> events = eventRepository.getEvents();
        assertEquals(3, events.size());
        Event event = events.get(2);

        assertEquals(TEST_EVENT_NAME_3, event.getName());
        assertEquals(TEST_EVENT_SHORT_NAME_3, event.getShortName());
        assertEquals(TEST_EVENT_YEAR_3, event.getYear());
    }

    @Test
    @WithMockUser
    public void testUpdatingEvent() throws Exception {
        Event testEvent = eventRepository.getEventByName(TEST_EVENT_NAME_1);

        testEvent.setName(TEST_EVENT_NAME_3);
        testEvent.setShortName(TEST_EVENT_SHORT_NAME_3);

        String url = "/events/" + testEvent.getId();
        String testEventJson = jacksonService.asJson(testEvent);

        logger.debug("Sending PUT request on {}", url);

        MockHttpServletResponse response = put(url, testEventJson);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        testEvent = eventRepository.getEventByName(TEST_EVENT_NAME_3);
        assertEquals(TEST_EVENT_NAME_3, testEvent.getName());
        assertEquals(TEST_EVENT_SHORT_NAME_3, testEvent.getShortName());
        assertEquals(TEST_EVENT_YEAR_3, testEvent.getYear());
    }

    @Test
    @WithMockUser
    public void testUpdatingNonExistingEvent() throws Exception {
        Event testEvent = new Event(TEST_EVENT_YEAR_3, TEST_EVENT_SHORT_NAME_3, TEST_EVENT_YEAR_3);
        String url = "/events/" + -1L;

        String testEventJson = jacksonService.asJson(testEvent);
        logger.debug("Sending request on {}", url);

        MockHttpServletResponse response = put(url, testEventJson);

        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    public void testGettingTasksForEvent() throws Exception {
        String url = "/events/" + event.getId() + "/tasks";

        logger.debug("Sending GET request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        List<Task> tasks = jacksonService.readListOfObjects(response.getContentAsString(), Task.class);

        assertEquals(1, tasks.size());

        Task testTask = tasks.get(0);

        Event testEvent = testTask.getEvent();
        assertEquals(TEST_EVENT_NAME_1, testEvent.getName());
        assertEquals(TEST_EVENT_SHORT_NAME_1, testEvent.getShortName());
        assertEquals(TEST_EVENT_YEAR_1, testEvent.getYear());

        User user1 = testTask.getAssignee();
        assertEquals(TEST_USER_FIRST_NAME, user1.getFirstName());
        assertEquals(TEST_USER_LAST_NAME, user1.getLastName());
    }

    @Test
    @WithMockUser
    public void testGettingUnassignedCompaniesForEvent() throws Exception {
        String url = "/events/" + event.getId() + "/tasks/assign";

        logger.debug("Sending GET request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        List<Company> companies = jacksonService.readListOfObjects(response.getContentAsString(), Company.class);

        int size = companies.size();

        Company testCompany = new Company("CompanyName", "CompanyShortName", CompanyType.COMPUTING);
        companyRepository.createCompany(testCompany);

        response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        companies = jacksonService.readListOfObjects(response.getContentAsString(), Company.class);
        assertEquals(size + 1, companies.size());

        response = get(url + "?name=omp&type=COMPUTING");
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        companies = jacksonService.readListOfObjects(response.getContentAsString(), Company.class);
        assertEquals(size + 1, companies.size());
    }

    @Test
    @WithMockUser
    public void testGettingTasksForNonExistingEvent() throws Exception {
        String url = "/events/" + -1L + "/tasks";

        logger.debug("Sending GET request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser
    public void testGettingUsersForEvent() throws Exception {
        String url = "/events/" + event.getId() + "/users";

        logger.debug("Sending GET request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(200, response.getStatus());

        List<User> users = jacksonService.readListOfObjects(response.getContentAsString(), User.class);

        assertEquals(1, users.size());

        User testUser = users.get(0);

        assertEquals(TEST_USER_FIRST_NAME, testUser.getFirstName());
        assertEquals(TEST_USER_LAST_NAME, testUser.getLastName());
    }

    @Test
    @WithMockUser
    public void testGetUsersForNonExistingEvent() throws Exception {
        String url = "/events/" + -1L + "/events";

        logger.debug("Sending GET request on {}", url);

        MockHttpServletResponse response = get(url);
        logger.debug("Response: {}", response.getContentAsString());

        assertEquals(404, response.getStatus());
    }

}