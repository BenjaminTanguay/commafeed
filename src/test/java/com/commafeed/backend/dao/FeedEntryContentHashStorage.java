package com.commafeed.backend.dao;

import com.commafeed.backend.model.QFeedEntry;
import com.commafeed.backend.model.QFeedEntryContent;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.FeedEntry;

import java.util.HashMap;

import com.commafeed.backend.model.FeedEntry;

public class FeedEntryContentHashStorage {
	HashMap<Integer, FeedEntryContent> hashMap = new HashMap<>();
	
	public FeedEntryContent Content(int key) {
		return this.hashMap.get(key);
	}
	public void LoadStorage(String filename) {
		hashMap = (HashMap<Integer, FeedEntryContent>) SerializeHashMap.loadMap(filename);
	}
	
	public void put(Integer key, FeedEntryContent content) {
		hashMap.put(key, content);
	}
		
	public void persistStorage(String filename) {
		SerializeHashMap.persistMap(hashMap, filename);
	}
}
