package com.commafeed.backend.feed;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;

import com.commafeed.backend.feed.FeedEntryKeyword.Mode;

public class FeedEntryKeywordTest {

	FeedEntryKeyword feedEntryKeywords;
	Mode mode;
	
	@Before
	public void Setup()
	{
	 feedEntryKeywords = mock(FeedEntryKeyword.class);
	 mode = mock(Mode.class);
	}
	
	@Test
	public void NullKeywordsTest() {
		String empty = "";
		List<FeedEntryKeyword> result;
		List<FeedEntryKeyword> expected = new ArrayList<>();
		
		result = feedEntryKeywords.fromQueryString(empty);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void HashKeywordStartingWithDash()
	{
		String substring1 = "-word-word2";
		List<FeedEntryKeyword> result1;
		List<FeedEntryKeyword> result2;
		
		 result1 = feedEntryKeywords.fromQueryString(substring1);
		 result2 = feedEntryKeywords.fromQueryString(substring1);

		 assertNotEquals(result1, result2);
	}
	
	@Test
	public void HashKeywordStartingWithExclamation()
	{
		String substring2 = "!word!word2";
		List<FeedEntryKeyword> result1;
		List<FeedEntryKeyword> result2;
		
		result1 = feedEntryKeywords.fromQueryString(substring2);
		result2 = feedEntryKeywords.fromQueryString(substring2);
		
		 assertNotEquals(result1, result2);
	}

}
