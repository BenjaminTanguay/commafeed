package com.commafeed.backend.dao;

import com.commafeed.backend.model.FeedEntryTag;

public class ArrayStorageTagEntry extends HashStorageFeedTag{
	int size = 999;
	
	FeedEntryTag[] tagArray;
	
	public ArrayStorageTagEntry()
	{
		tagArray = new FeedEntryTag[size];
	}
	
	@Override
	public void loadStorage(String filename) {
		// TODO Auto-generated method stub
		super.loadStorage(filename);
	}

	@Override
	public void put(Integer key, FeedEntryTag tag) {
		// TODO Auto-generated method stub
		super.put(key, tag);
	}

	@Override
	public FeedEntryTag tag(int key) {
		// TODO Auto-generated method stub
		return super.tag(key);
	}

	@Override
	public void persistStorage(String filename) {
		// TODO Auto-generated method stub
		super.persistStorage(filename);
	}
	
	public void forklift() 
	{
	    for (Integer key : hashMap.keySet())
	    {
	    	tagArray[key] = hashMap.get(key);
	    }
	}
	
	public int checkConsistency() {
		int inconsistencies = 0;
		for (Integer tag : hashMap.keySet()) {
			FeedEntryTag expected = hashMap.get(tag);
			FeedEntryTag actual = tagArray[tag];
			if (!expected.equals(actual)) {
				inconsistencies++;
			}
		}
		return inconsistencies;
	}


}
