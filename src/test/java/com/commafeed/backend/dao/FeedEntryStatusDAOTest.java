package com.commafeed.backend.dao;

import com.commafeed.backend.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;

public class FeedEntryStatusDAOTest extends AbstractDAOTest {

    private static UserDAO userDAO;
    private static FeedEntryStatusStorage feedEntryStatusStorage;
    private static List<Class> classes = new ArrayList<>();
    private static User testUser;

    @BeforeClass
    public static void beforeClass(){
        userDAO = new UserDAO(createSessionFactory(User.class));
        beginTransaction();
        testUser = userDAO.findByEmail("test@gmail.com");			//retrieve testing account
        closeTransaction();

        classes.add(User.class);
        classes.add(FeedCategory.class);
        classes.add(FeedSubscription.class);
        classes.add(Feed.class);
        classes.add(FeedEntryStatus.class);
        classes.add(FeedEntry.class);
        classes.add(FeedEntryContent.class);
        classes.add(FeedEntryTag.class);
        feedEntryStatusStorage = new FeedEntryStatusStorage(createSessionFactory(classes), testUser);
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
}
