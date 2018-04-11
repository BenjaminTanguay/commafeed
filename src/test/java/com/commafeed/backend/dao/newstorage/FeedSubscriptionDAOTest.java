package com.commafeed.backend.dao.newstorage;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.commafeed.backend.dao.AbstractDAOTest;
import com.commafeed.backend.dao.FeedSubscriptionDAO;
import com.commafeed.backend.dao.UserDAO;
import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedEntryStatus;
import com.commafeed.backend.model.FeedSubscription;
import com.commafeed.backend.model.User;




public class FeedSubscriptionDAOTest extends AbstractDAOTest {
	private static List<Class> classes = new ArrayList<>();
	private static UserDAO userDAO;
	private static FeedSubscriptionDAO feedSubscriptionDAO;
	private FeedSubscriptionStorage feedSubscriptionStorage;
	
	private static User user;
	private static Feed feed1;
	private static Feed feed2;
	private static FeedSubscription feedSub1;
	private static FeedSubscription feedSub2;
	
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Classes needed to create a FeedSubscriptionDAO
		classes.add(User.class);
		classes.add(Feed.class);
		classes.add(FeedCategory.class);
		classes.add(FeedEntryStatus.class);
		feedSubscriptionDAO = new FeedSubscriptionDAO(createSessionFactory(classes));
		
		MigrationToggles.turnAllTogglesOff();
	}

	 @Before
	    public void beforeEachTest() {
	        beginTransaction();
	        this.feedSubscriptionStorage = FeedSubscriptionStorage.getInstance();
	        feedSubscriptionDAO.supercedeIStorageModelDAOForTests(this.feedSubscriptionStorage);
	        
	    }

	    @After
	    public void afterEachTest() {
	        // This closes up the session with the database after each
	        // individual tests.
	        closeTransaction();
	    }

	@Test
	public void testForklift() {
		MigrationToggles.turnForkLiftOn();
		
		user = new User();
		feed1 = new Feed();
		feed2 = new Feed();
		
		// Add subscriptions
		feedSub1 = addFeedSubscription(user, feed1, "Sub1");
		feedSub2 = addFeedSubscription(user, feed2, "Sub2");
		
		feedSubscriptionDAO.saveOrUpdate(feedSub1);
		feedSubscriptionDAO.saveOrUpdate(feedSub2);
		
		feedSubscriptionDAO.forkLift();
		
		feedSubscriptionDAO.delete(feedSub1);
		feedSubscriptionDAO.delete(feedSub2);
		
		 assert(this.feedSubscriptionStorage.exists(feedSub1));
		 assert(this.feedSubscriptionStorage.exists(feedSub2));
		 assert(this.feedSubscriptionStorage.read(feedSub1).equals(feedSub1));
		 assert(this.feedSubscriptionStorage.read(feedSub2).equals(feedSub2));
 
	}
	
	@Test
	public void testConsistency() {
		MigrationToggles.turnConsistencyCheckerOn();
		
		user = new User();
		feed1 = new Feed();
		feed2 = new Feed();
		
		// Add subscriptions
		feedSub1 = addFeedSubscription(user, feed1, "Sub1");
		feedSub2 = addFeedSubscription(user, feed2, "Sub2");
		
		feedSubscriptionDAO.saveOrUpdate(feedSub1);
		feedSubscriptionDAO.saveOrUpdate(feedSub2);
		
		feedSubscriptionDAO.forkLift();
		
		 assert(this.feedSubscriptionStorage.exists(feedSub1));
		 assert(this.feedSubscriptionStorage.exists(feedSub2));
		 assert(this.feedSubscriptionStorage.read(feedSub1).equals(feedSub1));
		 assert(this.feedSubscriptionStorage.read(feedSub2).equals(feedSub2));
		 
		 // Corrupt data by updating storage with a different subscription
		 Feed feed3 = new Feed();
		 FeedSubscription feedSub3 = addFeedSubscription(user, feed3, "Sub3");
		 feedSub3.setId(feedSub2.getId());
		 
		 this.feedSubscriptionStorage.update(feedSub3);
		 
		 // Check that the method catches the inconsistency
		 assertEquals(1, feedSubscriptionDAO.checkInconsistencies());
		 
		 // Check that the method updated the model (no inconsistency)
		 assertEquals(0, feedSubscriptionDAO.checkInconsistencies());
		 
		 feedSubscriptionDAO.delete(feedSub2);
	}
	
	
	private FeedSubscription addFeedSubscription(User user, Feed feed, String title) {
		FeedSubscription newSub = new FeedSubscription();
		
		newSub.setUser(user);
		newSub.setFeed(feed);
		newSub.setTitle(title);
		newSub.setCategory(null);
		newSub.setStatuses(null);
		newSub.setPosition(0);
		newSub.setFilter("Random");
		return newSub;
	}

}
