package com.commafeed.backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.commafeed.backend.model.FeedEntryTag;
import com.commafeed.backend.model.User;



public class FeedEntryTagDAOTest extends AbstractDAOTest {
	 private static FeedEntryTagDAO feedTagDAO;
	 private static StorageFeedEntryTag feedEntryTagStorage;
	 private static  List<String> tags;

	 @BeforeClass
	    public static void beforeClass() {
		 
		 	List<Class> classes = new ArrayList<>();
		 	classes.add(User.class);
		 	classes.add(FeedEntryTag.class);
		 
	        feedTagDAO = new FeedEntryTagDAO(createSessionFactory(classes));
	 
	        tags = new ArrayList<String>();
	        User user = new User();
	    	user.setId((long) 2000);
	    	System.out.println("test");
	    	tags = feedTagDAO.findByUser(user);
	    	
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
	    public void test() {
		 beforeEachTest();
	    	System.out.println(tags.get(1));
	    	afterEachTest();
	    }
	 
}
