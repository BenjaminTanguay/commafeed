package com.commafeed.backend.dao;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FeedCategoryDAOTest extends AbstractDAOTest{


	private static UserDAO userDAO;					
	private static FeedCategoryStorage feedCategoryStorage;
	private static List<Class> classes = new ArrayList<>();
	private static User testUser;
	private static GenericStorage<Long, FeedCategory> storage;
	@BeforeClass
    public static void beforeClass() {
		userDAO = new UserDAO(createSessionFactory(User.class));
		beginTransaction();
		testUser = userDAO.findByEmail("test@gmail.com");			//retrieve testing account
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
		feedCategoryStorage = new FeedCategoryStorage(createSessionFactory(classes), testUser);
		
		String serializationFilename = "TestStorage";
		storage = new GenericStorage<Long, FeedCategory>(serializationFilename);
    }

    @Before
    public void beforeEachTest() {
        // This opens up the session with the database before each individual
        // tests.
        // It can run more than one query at a time.
    	beginTransaction();
    }

    @After
    public void afterEachTest() {
        // This closes up the session with the database after each
        // individual tests.
    	closeTransaction();
    }

    @Test
    public void ConsistencyCheckingTest() {
    	FeedCategory newCategory = new FeedCategory();
    	
    	//fork lift
    	feedCategoryStorage.forklift();
    	assertEquals(0, feedCategoryStorage.checkConsistency());
    	
    	//validate consistency checking
    	feedCategoryStorage.updateOnlyDatabase();
    	storage.loadStorage();
    	feedCategoryStorage.loadStorage(storage);
		assertEquals(1, feedCategoryStorage.checkConsistency());
    	
    	//shadow writes: any changes are written directly to old
		//consistency should be checked after each write
		newCategory = storage.read(1000L);
		newCategory.setName("American News");
		feedCategoryStorage.saveOrUpdate(newCategory);
		newCategory = feedCategoryStorage.findById(testUser, 1000L);
		assertEquals("American News", newCategory.getName());
		storage.loadStorage();;
		assertEquals("American News", storage.read(1000L).getName());
		
		//Shadow Reads for Validation (read will access both old and new)
		// change the hash only
    	storage.loadStorage();
    	assertEquals("American News", storage.read(1000L).getName());
    	storage.read(1000L).setName("Canadian News");
    	feedCategoryStorage.loadStorage(storage);
    	newCategory = feedCategoryStorage.findById(testUser, 1000L);
		assertEquals("American News", newCategory.getName());
    }
    	
}
