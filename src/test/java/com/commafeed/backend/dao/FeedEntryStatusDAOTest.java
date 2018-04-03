package com.commafeed.backend.dao;

import com.commafeed.CommaFeedConfiguration;
import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.dao.newstorage.GenericStorage;
import com.commafeed.backend.dao.newstorage.IStorageModelDAO;
import com.commafeed.backend.model.*;
import org.junit.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.commafeed.backend.dao.GenericDAOTest.getUser;
import static com.commafeed.backend.dao.GenericDAOTest.getUserSettings;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeedEntryStatusDAOTest extends AbstractDAOTest {

    private static UserSettingsDAO userSettingsDAO;
    private static UserDAO userDAO;
    private static UserSettings userSettings1;
    private static UserSettings userSettings2;
    private static UserSettings userSettings3;
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;
    private GenericStorage storage;
    private static FeedEntryStatusStorage feedEntryStatusStorage;

    private static FeedEntryStatus status;
    private static FeedSubscription subscription;
    private static FeedEntry entry;
    private static FeedEntryDAO entryDAO;
    private static FeedEntryTagDAO entryTagDAO;
    private static FeedEntryStatusDAO feedEntryStatusDAO;
    private static CommaFeedConfiguration config;

    @BeforeClass
    public static void beforeClass(){
        userDAO = new UserDAO(createSessionFactory(User.class));
        List<Class> classList = new ArrayList<>();
        classList.add(UserSettings.class);
        classList.add(User.class);
        userSettingsDAO = new UserSettingsDAO(createSessionFactory(classList, this.entryDAO, this.entryTagDAO, this.config));
        MigrationToggles.turnAllTogglesOff();
        // Creating all the users in the database
        user1 = getUser("Hello", "Hello@gmail.com");
        user2 = getUser("Hi", "Hi@gmail.com");
        user3 = getUser("Bonjour", "Bonjour@gmail.com");
        user4 = getUser("Salut", "Salut@gmail.com");
        user5 = getUser("Bonjourno", "Bonjourno@gmail.com");

        // DB TRANSACTIONS
        beginTransaction();
        userDAO.saveOrUpdate(user1);
        closeTransaction();
        beginTransaction();
        userDAO.saveOrUpdate(user2);
        closeTransaction();
        beginTransaction();
        userDAO.saveOrUpdate(user3);
        closeTransaction();
        beginTransaction();
        userDAO.saveOrUpdate(user4);
        closeTransaction();
        beginTransaction();
        userDAO.saveOrUpdate(user5);
        closeTransaction();
        beginTransaction();
        userSettings1 = getUserSettings(user1, "English",
                true);
        userSettings2 = getUserSettings(user2, "English",
                false);
        userSettings3 = getUserSettings(user3, "French",
                false);

        userSettingsDAO.saveOrUpdate(userSettings1);
        closeTransaction();
        beginTransaction();
        userSettingsDAO.saveOrUpdate(userSettings2);
        closeTransaction();
        beginTransaction();
        userSettingsDAO.saveOrUpdate(userSettings3);
        closeTransaction();
    }

    @Before
    public void beforeEachTest() {
        // Here we inject an empty storage map to be used only for the test
        // so that each test is independent of each other
        this.storage = GenericStorage.getTestInstance();
        userSettingsDAO.supercedeIStorageModelDAOForTests((IStorageModelDAO) storage);

        List<Class> classList = new ArrayList<>();
        classList.add(UserSettings.class);
        classList.add(User.class);

        this.status = mock(FeedEntryStatus.class);
        this.subscription = mock(FeedSubscription.class);
        this.entry = mock(FeedEntry.class);
        this.entryDAO = mock(FeedEntryDAO.class);
        this.entryTagDAO = mock(FeedEntryTagDAO.class);
        this.config = mock(CommaFeedConfiguration.class);
        feedEntryStatusDAO = new FeedEntryStatusDAO(createSessionFactory(classList, this.entryDAO, this.entryTagDAO, this.config));
    }

    @AfterClass
    public static void afterClass() {
        beginTransaction();
        userDAO.delete(user1);
        closeTransaction();
        beginTransaction();
        userDAO.delete(user2);
        closeTransaction();
        beginTransaction();
        userDAO.delete(user3);
        closeTransaction();
        beginTransaction();
        userDAO.delete(user4);
        closeTransaction();
        beginTransaction();
        userDAO.delete(user5);
        closeTransaction();
        beginTransaction();
        userSettingsDAO.delete(userSettings1);
        closeTransaction();
        beginTransaction();
        userSettingsDAO.delete(userSettings2);
        closeTransaction();
        beginTransaction();
        userSettingsDAO.delete(userSettings3);
        closeTransaction();
    }

    @Test
    public void testOldStatus(){
        Date testDate = new Date();
        List<FeedEntryStatus> tempStatus = new ArrayList<>();
        
        when(feedEntryStatusDAO.getOldStatuses(testDate, 10)).thenReturn(tempStatus);

        Assert.assertEquals(tempStatus, feedEntryStatusDAO.getOldStatuses(testDate, 10));
    }
}
