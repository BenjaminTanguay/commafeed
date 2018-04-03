package Milestone3Tests;

import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import com.commafeed.CommaFeedConfiguration;
import com.commafeed.backend.dao.*;
import com.commafeed.backend.model.AbstractModel;
import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.FeedEntryStatus;
import com.commafeed.backend.model.FeedEntryTag;
import com.commafeed.backend.model.FeedSubscription;
import com.commafeed.backend.model.User;
import com.commafeed.backend.model.UserRole;
import com.commafeed.backend.model.UserSettings;
import com.commafeed.backend.service.StartupService;
import com.commafeed.backend.service.UserService;

import io.dropwizard.hibernate.HibernateBundle;
import lombok.RequiredArgsConstructor;



public class UserConsistencyChecking {

	UserDAO userDAO;
	User dummyUser;
	SessionFactory session;
	StartupService services;
	HibernateBundle<CommaFeedConfiguration> hibernateBundle;
	@Before
	public void setUp() throws Exception
	{
		//userDAO = new UserDAO(session);
		//services.start();
		User dummyUser = new User();
		
		hibernateBundle = new HibernateBundle<CommaFeedConfiguration>(AbstractModel.class, Feed.class,
				FeedCategory.class, FeedEntry.class, FeedEntryContent.class, FeedEntryStatus.class, FeedEntryTag.class,
				FeedSubscription.class, User.class, UserRole.class, UserSettings.class)
	}
	@Test
	public void test() {
		dummyUser = userDAO.findByName("admin");
		
		
	}

}
