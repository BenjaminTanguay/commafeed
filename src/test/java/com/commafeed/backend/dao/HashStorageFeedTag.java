package com.commafeed.backend.dao;

import java.util.HashMap;

import com.commafeed.backend.model.FeedEntryTag;
import com.commafeed.backend.model.QFeedEntryTag;


public class HashStorageFeedTag implements StorageFeedEntryTag  {
	HashMap<Integer,FeedEntryTag> hashMap = new HashMap<>();

	@Override
	public FeedEntryTag tag(int key) {
		return this.hashMap.get(key);
	}
	public void loadStorage(String filename) {
		hashMap = SerializaHashMap.loadMap(filename);
	}
	
	public void put(Integer key, FeedEntryTag tag) {
		hashMap.put(key, tag);
	}
		
	public void persistStorage(String filename) {
		SerializaHashMap.persistMap(hashMap, filename);
	}
	
/*	
	public void check() {
		QFeedEntryTag tagTb = QFeedEntryTag.feedEntryTag;
		System.out.println("check");			
		for (Integer tag : hashMap.keySet()) 
		{
			
			
			String actual = return query().selectFrom(tag).where(tag.user.eq(user), tag.entry.eq(entry)).fetch();
			if (!actual.equals(hashMap.get(tag))) 
			{
				//inconsistency
			System.out.println("Consistency Violation!\n" + "tag = " + tag + 
					"\n\t expected = " + hashMap.get(tag) + "\n\t actual = " + actual);
			}
		}			
	}
	*/
}

