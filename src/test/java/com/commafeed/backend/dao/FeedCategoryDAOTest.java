package com.commafeed.backend.dao;

import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.dao.newstorage.FeedCategoryStorage;
import com.commafeed.backend.dao.newstorage.GenericStorage;
import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.FeedEntryStatus;
import com.commafeed.backend.model.FeedEntryTag;
import com.commafeed.backend.model.FeedSubscription;
import com.commafeed.backend.model.User;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FeedCategoryDAOTest extends AbstractDAOTest{
	@SuppressWarnings("rawtypes")
	private static List<Class> classes = new ArrayList<>();
	private static UserDAO userDAO;
	private static FeedCategoryDAO feedCategoryDAO;
	private FeedCategoryStorage feedCategoryStorage;
	private static User testUser;
	private static FeedCategory category1;
	private static FeedCategory category2;
	private static GenericStorage<Long, FeedCategory> storage;
	
	@BeforeClass
    public static void beforeClass() {
		userDAO = new UserDAO(createSessionFactory(User.class));
		
		beginTransaction();
		testUser = userDAO.findByEmail("admin@commafeed.com");
		closeTransaction();
		/* cannot create FeedCategoryDAO without following classes*/
		classes.add(User.class);
		classes.add(FeedCategory.class);
		classes.add(FeedSubscription.class);
		classes.add(Feed.class);
		classes.add(FeedEntryStatus.class);
		classes.add(FeedEntry.class);
		classes.add(FeedEntryContent.class);
		classes.add(FeedEntryTag.class);
		feedCategoryDAO = new FeedCategoryDAO(createSessionFactory(classes));
		
		MigrationToggles.turnAllTogglesOff();
    }

    @Before
    public void beforeEachTest() {
        // This opens up the session with the database before each individual
        // tests.
        // It can run more than one query at a time.
    	beginTransaction();
    	this.feedCategoryStorage = FeedCategoryStorage.getInstance();
        feedCategoryDAO.supercedeIStorageModelDAOForTests(this.feedCategoryStorage);
    }

    @After
    public void afterEachTest() {
        // This closes up the session with the database after each
        // individual tests.
    	closeTransaction();
    }

    @Test
    public void testForkLift(){
    	MigrationToggles.turnForkLiftOn();
    	category1 = createCategory("Sports");
    	category2 = createCategory("News");
        feedCategoryDAO.saveOrUpdate(category1);
        feedCategoryDAO.saveOrUpdate(category2);
        feedCategoryDAO.forklift(testUser);

        feedCategoryDAO.delete(category1);
        feedCategoryDAO.delete(category2);

        assert(this.feedCategoryStorage.exists(category1));
        assert(this.feedCategoryStorage.exists(category2));
        assert(this.feedCategoryStorage.read(category1).equals(category1));
        assert(this.feedCategoryStorage.read(category2).equals(category2));
    }
    
    @Test
    public void testConsistencyCheck() {
        MigrationToggles.turnConsistencyCheckerOn();

        //construct and insert data
        category1 = createCategory("Sports");
    	category2 = createCategory("News");
    	feedCategoryDAO.saveOrUpdate(category1);
        feedCategoryDAO.saveOrUpdate(category2);
        feedCategoryDAO.forklift(testUser);

        //validate data
        assert(this.feedCategoryStorage.exists(category1));
        assert(this.feedCategoryStorage.exists(category2));
        assert(this.feedCategoryStorage.read(category1).equals(category1));
        assert(this.feedCategoryStorage.read(category2).equals(category2));
    
        //Corrupt datastorage
        FeedCategory category3 = createCategory("Sport1234");
        category3.setId(category1.getId());
        FeedCategory category4 = createCategory("news1234");
        category4.setId(category2.getId());


        this.feedCategoryStorage.update(category3);
        this.feedCategoryStorage.update(category4);

        // First time, there should be two inconsistencies
        assertEquals(2, feedCategoryDAO.consistencyChecker(testUser));

        // validate consistency checker fixes inconsistencies
        assertEquals(0, feedCategoryDAO.consistencyChecker(testUser));

        feedCategoryDAO.delete(category1);
        feedCategoryDAO.delete(category2);
    }
    @Test
    public void testShadowWrites() {
        MigrationToggles.turnShadowWritesOn();

        //construct and insert data
        category1 = createCategory("Sports");
    	category2 = createCategory("News");
    	feedCategoryDAO.saveOrUpdate(category1);
        feedCategoryDAO.saveOrUpdate(category2);

        // validate data
        assert(this.feedCategoryStorage.exists(category1));
        assert(this.feedCategoryStorage.exists(category2));
        assert(this.feedCategoryStorage.read(category1).equals(category1));
        assert(this.feedCategoryStorage.read(category2).equals(category2));
    
        feedCategoryDAO.delete(category1);
        feedCategoryDAO.delete(category2);
    }
    @Test
    public void testShadowReads() {
        MigrationToggles.turnShadowReadsOn();

        //construct and insert data
        category1 = createCategory("Sports");
    	category2 = createCategory("News");
    	feedCategoryDAO.saveOrUpdate(category1);
        feedCategoryDAO.saveOrUpdate(category2);

        //validate data
        assert(this.feedCategoryStorage.exists(category1));
        assert(this.feedCategoryStorage.exists(category2));
        assert(this.feedCategoryStorage.read(category1).equals(category1));
        assert(this.feedCategoryStorage.read(category2).equals(category2));
    

        //retrieve data from database
        FeedCategory category3 = feedCategoryDAO.findById(category1.getId());

        assert(category3.equals(category1));

        // Corrupting the data & validate whether data is fixed
        FeedCategory category4 = createCategory("TestData");
        category4.setId(category2.getId());
        this.feedCategoryStorage.update(category4);

        assertNotEquals(category4, category2);

        FeedCategory category5 = feedCategoryDAO.findById(category2.getId());

        //validate whether corrupted data has corrected
        assertEquals(category2, category5);

        // Waiting for the asynchronous call to finish
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(this.feedCategoryStorage.read(category4.getId()), category5);

        feedCategoryDAO.delete(category1);
        feedCategoryDAO.delete(category2);
    }
    
    @Test
    public void testReadAndWriteMigration() {
        MigrationToggles.turnShadowReadsOn();

        //construct and insert data
        category1 = createCategory("Sports");
    	category2 = createCategory("News");
    	feedCategoryDAO.saveOrUpdate(category1);
        feedCategoryDAO.saveOrUpdate(category2);

        //retrieve total entries within database based on specific user
        int totalEntry = feedCategoryDAO.findAll(testUser).size();

        //validate entries within database
        assert(this.feedCategoryStorage.exists(category1));
        assert(this.feedCategoryStorage.exists(category2));
        assert(this.feedCategoryStorage.read(category1).equals(category1));
        assert(this.feedCategoryStorage.read(category2).equals(category2));
    
        //corrupt data within data storage
        FeedCategory category3 = createCategory("Test1");
        FeedCategory category4 = createCategory("Test2");

        category3.setId(category1.getId());
        category4.setId(category2.getId());
        this.feedCategoryStorage.update(category3);
        this.feedCategoryStorage.update(category4);

        int inconsistencyCounter = 0;
        double threshold = 1;

        // First time, there should be two inconsistencies
        assertEquals(4, inconsistencyCounter = feedCategoryDAO.consistencyChecker(testUser));
        do {
            threshold = inconsistencyCounter / totalEntry;
            // Other times, there should be no inconsistency
            assertEquals(0, inconsistencyCounter = feedCategoryDAO.consistencyChecker(testUser));
        } while(threshold > 0.01);

        // Now that the inconsistencies are below a certain threshold, we can
        // discard the old database
        MigrationToggles.turnReadAndWriteOn();

        // Removing first category from the storage
        feedCategoryDAO.delete(category1);
        assert(!this.feedCategoryStorage.exists(category1));

        // Turning off the toggles to check that the first category in the db wasn't
        // affected
        MigrationToggles.turnAllTogglesOff();

        //retrieve first category from database
        FeedCategory category6 = feedCategoryDAO.findById(category1.getId());
        //validate new category is the same as initial
        assertEquals(category1, category6);

        feedCategoryDAO.delete(category1);
        feedCategoryDAO.delete(category2);
    }
    @Test
    public void testLongTermConsistencyCheck() {
        MigrationToggles.turnLongTermConsistencyOn();

        //construct and insert data
        category1 = createCategory("Sports");
    	category2 = createCategory("News");
    	feedCategoryDAO.saveOrUpdate(category1);
        feedCategoryDAO.saveOrUpdate(category2);

        HashMap<Long, FeedCategory> longTermHashMapConsistencyChecker = new
                HashMap<Long, FeedCategory>();

        longTermHashMapConsistencyChecker.put(category1.getId(), category1);
        longTermHashMapConsistencyChecker.put(category2.getId(), category2);

        feedCategoryDAO.setLongTermHashMap(longTermHashMapConsistencyChecker);

        //validate data within data storage
        assert(this.feedCategoryStorage.exists(category1));
        assert(this.feedCategoryStorage.exists(category2));
        assert(this.feedCategoryStorage.read(category1).equals(category1));
        assert(this.feedCategoryStorage.read(category2).equals(category2));
    
        //corrupt data within data storage
        FeedCategory category3 = createCategory("Test1");
        FeedCategory category4 = createCategory("Test2");
        category3.setId(category1.getId());
        category4.setId(category2.getId());
        this.feedCategoryStorage.update(category3);
        this.feedCategoryStorage.update(category4);

        // First time, there should be two inconsistencies
        assertEquals(2, feedCategoryDAO.consistencyChecker(testUser));

        // Second time, there should be no inconsistency
        assertEquals(0, feedCategoryDAO.consistencyChecker(testUser));
    }

    private FeedCategory createCategory(String name){
    	FeedCategory newCategory = new FeedCategory();
    	newCategory.setName(name);
    	newCategory.setParent(null);
    	newCategory.setPosition(0);
    	newCategory.setSubscriptions(null);
    	newCategory.setUser(testUser);
    	newCategory.setCollapsed(false);
    	return newCategory;
    }   
    	
}
