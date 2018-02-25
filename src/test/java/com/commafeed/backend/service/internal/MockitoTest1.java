package com.commafeed.backend.service.internal;

import static org.junit.Assert.*;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.commafeed.backend.model.User;
import com.commafeed.backend.model.UserRole.Role;
import com.commafeed.backend.service.FeedSubscriptionService;
import com.commafeed.backend.service.PasswordEncryptionService;
import com.commafeed.backend.service.UserService;
import com.ibm.icu.util.Calendar;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;

import com.commafeed.CommaFeedConfiguration;
import com.commafeed.backend.dao.UserDAO;


public class MockitoTest1 {
	
	UserDAO mockUserDAO;
	FeedSubscriptionService mockFeedSubscriptionService;
	CommaFeedConfiguration mockConfig;
	User testUser;
	PostLoginActivities postLoginaActivity;
	Date now = new Date();
	Date lastLogin;
	Calendar myCal;
	
	@Before
	public void setUp() 
	{ 
		mockUserDAO = mock(UserDAO.class);
		mockFeedSubscriptionService = mock(FeedSubscriptionService.class);
		mockConfig = mock(CommaFeedConfiguration.class);

		testUser = mock(User.class);
		//User testUser = new User();
		postLoginaActivity = new PostLoginActivities(mockUserDAO, mockFeedSubscriptionService, mockConfig);
		
		Calendar myCal = Calendar.getInstance();
		myCal.set(Calendar.YEAR, 2018);
		myCal.set(Calendar.MONTH, 02);
		myCal.set(Calendar.DAY_OF_MONTH, 11);
		lastLogin = myCal.getTime();
		
	}	
	
	@Test
	public void testNullCondition() {
		
		//when(testUser.getLastLogin()).thenReturn(null);
		System.out.println(testUser.getLastLogin());
		
		postLoginaActivity.executeFor(testUser);
		
		System.out.println(testUser.getLastLogin());
		
		assertEquals(testUser.getLastLogin(), now);
		
	}
	
	@Test
	public void testSecondCondition() {
		testUser.setLastLogin(lastLogin);
		//System.out.println(testUser.getLastLogin());
		
		testUser.shouldRefreshFeedsAt(now);
		when(mockConfig.getApplicationSettings().getHeavyLoad()).thenReturn(true);
		postLoginaActivity.executeFor(testUser);
		assertEquals(testUser.getLastFullRefresh(), now);
		System.out.println("hey there");
	}
	
}
