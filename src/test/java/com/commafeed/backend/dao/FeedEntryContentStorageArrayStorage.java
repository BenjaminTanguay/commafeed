package com.commafeed.backend.dao;

import java.util.HashMap;

import com.commafeed.backend.model.QFeedEntry;
import com.commafeed.backend.model.QFeedEntryContent;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.FeedEntry;

public class FeedEntryContentStorageArrayStorage extends FeedEntryContentHashStorage{
	
	int size = 999;

	
	FeedEntryContent[] contentTempArray;
	
	public void ArrayStorageTagEntry()
	{
		contentTempArray = new FeedEntryContent[size];
	}
	
	@Override
	public void loadStorage(String filename) {
		super.loadStorage(filename);
	}

	@Override
	public void put(Integer key, FeedEntryContent content) {
		super.put(key, content);
	}

	@Override
	public FeedEntryContent contents(int key) {
		return super.contents(key);
	}

	@Override
	public void persistStorage(String filename) {
		super.persistStorage(filename);
	}
	
	public void forklift() 
	{
	    for (Integer key : hashMap.keySet())
	    {
	    	contentTempArray[key] = hashMap.get(key);
	    }
	}
	
	public int checkConsistency() {
		int inconsistencies = 0;
		
		for (Integer i : hashMap.keySet()) {
			
			FeedEntryContent expected = hashMap.get(i);
			
			FeedEntryContent actual = contentTempArray[i];
			
			if (!expected.equals(actual)) {
				inconsistencies++;
			}
		
		}
		return inconsistencies;
	}
}
