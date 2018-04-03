package com.commafeed.backend.dao;

import com.commafeed.CommaFeedConfiguration;
import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.dao.newstorage.FeedEntryStatusStorage;
import com.commafeed.backend.dao.newstorage.GenericStorage;
import com.commafeed.backend.dao.newstorage.IStorageModelDAO;
import com.commafeed.backend.model.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testForklift() {
        MigrationToggles.turnForkLiftOn();
        // Forklifting the data from the database to the storage
        feedEntryStatusDAO.forklift();
        // Checking that the data in the storage is ok
        assert(this.storage.exists(user1));
        assert(this.storage.exists(user2));
        assert(this.storage.read(user1).equals(user1));
        assert(this.storage.read(user2).equals(user2));
    }

    @Test
    public void testShadowWrite() {
        MigrationToggles.turnShadowWritesOn();

        FeedEntry feedEntry1 = getFeedEntry("1111", "url1");
        FeedEntry feedEntry2 = getFeedEntry("2222", "url2");

        beginTransaction();
        feedEntryStatusDAO.saveOrUpdate((Collection<FeedEntryStatus>) feedEntry1);
        closeTransaction();
        beginTransaction();
        feedEntryStatusDAO.saveOrUpdate((Collection<FeedEntryStatus>) feedEntry2);
        closeTransaction();

        FeedEntry storageFeedEntry1 = (FeedEntry) this.storage.read(feedEntry1);
        FeedEntry storageFeedEntry2 = (FeedEntry) this.storage.read(feedEntry2);

        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e ){
            System.err.println(e);
        }

        assert (storageFeedEntry1.equals(feedEntry1));
        assert (storageFeedEntry2.equals(feedEntry2));

        MigrationToggles.turnAllTogglesOff();
    }

    public static Feed getFeed(){
        Feed feed = new Feed();
        Date date = new Date();

        feed.setUrl("Url");
        feed.setUrlAfterRedirect("UrlAfter");
        feed.setNormalizedUrl("NormalizedUrl");
        feed.setNormalizedUrlHash("NormalizedUrlHash");
        feed.setLink("Link");
        feed.setLastUpdated(date);
        feed.setLastPublishedDate(date);
        feed.setLastEntryDate(date);
        feed.setMessage("Message");
        feed.setErrorCount(0);
        feed.setDisabledUntil(date);
        feed.setLastModifiedHeader("LastModifiedHeader");
        feed.setEtagHeader("EtagHeader");
        feed.setAverageEntryInterval((long)3000);
        feed.setLastContentHash("LastContentHash");
        feed.setPushHub("PushHub");
        feed.setPushTopic("PushTopic");
        feed.setPushTopicHash("PushTopicHash");
        feed.setPushLastPing(date);
        return feed;
    }

    public static FeedEntryContent getFeedEntryContent(){
        FeedEntryContent feedEntryContent = new FeedEntryContent();
        Set<FeedEntry> entries = new HashSet<>(Arrays.asList());
        entries.add(getFeedEntry("1234", "url1"));
        entries.add(getFeedEntry("1235", "url2"));
        entries.add(getFeedEntry("1236", "url3"));

        feedEntryContent.setTitle("Title");
        feedEntryContent.setTitleHash("TitleHash");
        feedEntryContent.setContent("content");
        feedEntryContent.setContentHash("contentHash");
        feedEntryContent.setAuthor("Author");
        feedEntryContent.setEnclosureUrl("enclosureUrl");
        feedEntryContent.setEnclosureType("enclosureType");
        feedEntryContent.setCategories("categories");
        feedEntryContent.setEntries(entries);

        return feedEntryContent;
    }

    public static FeedCategory getFeedCategory(String name){

        FeedCategory feedCategory = new FeedCategory();
        User user = GenericDAOTest.getUser("Bob", "bob@gmail.com");

        Set<FeedCategory> childs = new HashSet<>(Arrays.asList());
        FeedCategory child = getFeedCategory("child");
        childs.add(child);

        Set<FeedSubscription> subscriptions = new HashSet<>(Arrays.asList());
        FeedSubscription subscription = getFeedSubrcritpion(user, "random");
        subscriptions.add(subscription);


        feedCategory.setName(name);
        feedCategory.setUser(user);
        feedCategory.setParent(getFeedCategory("parent"));
        feedCategory.setChildren(childs);
        feedCategory.setSubscriptions(subscriptions);
        feedCategory.setCollapsed(true);
        feedCategory.setPosition(1);

        return feedCategory;
    }

    public static FeedSubscription getFeedSubrcritpion(User user, String title){
        FeedSubscription feedSubscription = new FeedSubscription();

        FeedCategory feedCategory = getFeedCategory("main");

        Set<FeedEntryStatus> feedEntryStatusSet = new HashSet<>(Arrays.asList());
        FeedEntryStatus feedEntryStatus = new FeedEntryStatus(user, getFeedSubrcritpion(user, "some title") , getFeedEntry("3214", "url23"));
        feedEntryStatusSet.add(feedEntryStatus);

        feedSubscription.setUser(user);
        feedSubscription.setFeed(getFeed());
        feedSubscription.setTitle(title);
        feedSubscription.setCategory(feedCategory);
        feedSubscription.setStatuses(feedEntryStatusSet);
        feedSubscription.setPosition(2);
        feedSubscription.setFilter("filter");

        return feedSubscription;
    }

    public static FeedEntry getFeedEntry(String guid, String url){
        FeedEntry feedEntry = new FeedEntry();
        Date date = new Date();
        User user = GenericDAOTest.getUser("Bob", "bob@gmail.com");

        Set<FeedEntryStatus> feedEntryStatusSet = new HashSet<>(Arrays.asList());
        FeedEntryStatus feedEntryStatus = new FeedEntryStatus(user, getFeedSubrcritpion(user, "some title") , getFeedEntry("3214", "url23"));
        feedEntryStatusSet.add(feedEntryStatus);

        Set<FeedEntryTag> feedEntryTags = new HashSet<>(Arrays.asList());
        FeedEntryTag feedEntryTag = new FeedEntryTag(user , getFeedEntry("4321", "url23"), "name");
        feedEntryTags.add(feedEntryTag);

        feedEntry.setGuid(guid);
        feedEntry.setGuidHash(guid + "Hash");
        feedEntry.setFeed(getFeed());
        feedEntry.setContent(getFeedEntryContent());
        feedEntry.setUrl(url);
        feedEntry.setInserted(date);
        feedEntry.setUpdated(date);
        feedEntry.setStatuses(feedEntryStatusSet);
        feedEntry.setTags(feedEntryTags);

        return feedEntry;
    }
}
