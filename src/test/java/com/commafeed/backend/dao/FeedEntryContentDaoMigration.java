package com.commafeed.backend.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;

import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.QFeedEntry;
import com.commafeed.backend.model.QFeedEntryContent;
import com.commafeed.backend.model.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import java.io.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.commafeed.backend.dao.FeedEntryContentDAO;


public class FeedEntryContentDaoMigration extends AbstractDAOTest{
	private QFeedEntryContent content = QFeedEntryContent.feedEntryContent;
	private QFeedEntry entry = QFeedEntry.feedEntry;
	
	@BeforeClass
    public static void beforeClass() {	
		FeedEntryContentDAO feeds = new FeedEntryContentDAO(createSessionFactory(FeedEntryContent.class));
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
	public void test() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
				
	}

}
