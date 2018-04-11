package com.commafeed.backend.dao;

import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.dao.newstorage.FeedEntryStorage;
import com.commafeed.backend.model.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FeedEntryDAOTest extends AbstractDAOTest {

    private static FeedEntryDAO feedEntryDAO;
    private static FeedEntry feedEntry;
    public static Feed feed;
    private static FeedEntryContent feedEntryContent;

    private static FeedEntry feedEntry1;
    private static FeedEntry feedEntry2;
    private static FeedEntry feedEntry3;

    private FeedEntryStorage storage;

    @BeforeClass
    public static void beforeClass() {
        feedEntryDAO = new FeedEntryDAO(createSessionFactory(FeedEntryDAO.class));

        MigrationToggles.turnAllTogglesOff();
        //Create the feed Entries
        feedEntry1 = getFeedEntry("1111", "urlEntry1");
        feedEntry2 = getFeedEntry("2222", "urlEntry2");
        feedEntry3 = getFeedEntry("3333", "urlEntry3");

        //DB Transactions
        beginTransaction();
        feedEntryDAO.saveOrUpdate(feedEntry1);
        closeTransaction();
        beginTransaction();
        feedEntryDAO.saveOrUpdate(feedEntry2);
        closeTransaction();
        beginTransaction();
        feedEntryDAO.saveOrUpdate(feedEntry3);
        closeTransaction();

    }

    @Before
    public void beforeEachTest() {
        this.storage = FeedEntryStorage.getTestInstance();
        feedEntryDAO.supercedeIStorageModelDAOForTests(storage);
    }

    @AfterClass
    public static void afterClass(){
        beginTransaction();
        feedEntryDAO.delete(feedEntry1);
        closeTransaction();
        beginTransaction();
        feedEntryDAO.delete(feedEntry2);
        closeTransaction();
        beginTransaction();
        feedEntryDAO.delete(feedEntry3);
        closeTransaction();
    }

    @Test
    public void testForklift() {
        MigrationToggles.turnForkLiftOn();

        feedEntry1 = getFeedEntry("3333", "url34");
        feedEntryDAO.saveOrUpdate(feedEntry1);
        feedEntry2 = getFeedEntry("223322", "url342");
        feedEntryDAO.saveOrUpdate(feedEntry2);

        feedEntryDAO.forklift();

        feedEntryDAO.delete(feedEntry1);
        feedEntryDAO.delete(feedEntry2);

        assert(this.storage.exists(feedEntry1));
        assert(this.storage.exists(feedEntry2));
        assert(this.storage.read(feedEntry1).equals(feedEntry1));
        assert(this.storage.read(feedEntry1).equals(feedEntry2));
    }

    @Test
    public void testConsistencyCheck() {
        MigrationToggles.turnConsistencyCheckerOn();

        // Putting some feedEntry in the database
        feedEntry1 = getFeedEntry("guid1", "url1");
        feedEntryDAO.saveOrUpdate(feedEntry1);
        feedEntry2 = getFeedEntry("guid2", "url2");
        feedEntryDAO.saveOrUpdate(feedEntry2);

        // Forklifting the data from the database to the storage
        feedEntryDAO.forklift();

        // Checking that the data in the storage is ok
        assert(this.storage.exists(feedEntry1));
        assert(this.storage.exists(feedEntry2));
        assert(this.storage.read(feedEntry1).equals(feedEntry1));
        assert(this.storage.read(feedEntry2).equals(feedEntry2));

        //the data in the datastorage get corrupt
        FeedEntry feedEntry3 = getFeedEntry("guid3", "url3");
        feedEntry3.setId(feedEntry1.getId());
        this.storage.update(feedEntry3);
        FeedEntry feedEntry4 = getFeedEntry("url4", "url4");
        feedEntry4.setId(feedEntry2.getId());
        this.storage.update(feedEntry4);

        //there should be two inconsistencies on the first time
        assertEquals(2, feedEntryDAO.consistencyChecker());

        //there should be no inconsistency on the second time
        assertEquals(0, feedEntryDAO.consistencyChecker());

        feedEntryDAO.delete(feedEntry1);
        feedEntryDAO.delete(feedEntry2);
    }

    @Test
    public void testShadowWrite() {
        MigrationToggles.turnShadowWritesOn();

        FeedEntry feedEntry1 = getFeedEntry("4444", "url4");
        FeedEntry feedEntry2 = getFeedEntry("5555", "url5");

        beginTransaction();
        feedEntryDAO.saveOrUpdate(feedEntry1);
        closeTransaction();
        beginTransaction();
        feedEntryDAO.saveOrUpdate(feedEntry2);
        closeTransaction();

        FeedEntry storageFeedEntry1 = this.storage.read(feedEntry1);
        FeedEntry storageFeedEntry2 = this.storage.read(feedEntry2);

        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e ){
            System.err.println(e);
        }

        assert (storageFeedEntry1.equals(feedEntry1));
        assert (storageFeedEntry2.equals(feedEntry2));

        MigrationToggles.turnAllTogglesOff();
    }

    @Test
    public void testShadowReads() {
        MigrationToggles.turnShadowReadsOn();

        // Putting some feedEntry in the database and in the storage
        feedEntry1 = getFeedEntry("guid1", "url1");
        feedEntryDAO.saveOrUpdate(feedEntry1);
        feedEntry2 = getFeedEntry("guid2", "url2");
        feedEntryDAO.saveOrUpdate(feedEntry2);

        // is the storage ok
        assert(this.storage.exists(feedEntry1));
        assert(this.storage.exists(feedEntry2));
        assert(this.storage.read(feedEntry1).equals(feedEntry1));
        assert(this.storage.read(feedEntry2).equals(feedEntry2));

        // Pulling data from the db with the read method
        FeedEntry feedEntry3 = feedEntryDAO.findById(feedEntry1.getId());

        assert(feedEntry3.equals(feedEntry1));

        // Corrupting the data from the storage and checking that the error
        // is automatically corrected
        FeedEntry feedEntry4 = getFeedEntry("url4", "url4");
        feedEntry4.setId(feedEntry2.getId());
        this.storage.update(feedEntry4);

        assertNotEquals(feedEntry4, feedEntry2);

        // Reading the feedEntry
        FeedEntry feedEntry5 = feedEntryDAO.findById(feedEntry4.getId());

        // Now check if correct for the corrupted data should have been
        assertEquals( feedEntry2, feedEntry5);

        // Waiting for the asynchronous call to finish
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(this.storage.read(feedEntry4.getId()), feedEntry5);

        feedEntryDAO.delete(feedEntry1);
        feedEntryDAO.delete(feedEntry2);
    }

    public static Feed getFeed(){
        feed = new Feed();
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
        feedEntryContent = new FeedEntryContent();
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
        feedEntry = new FeedEntry();
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
