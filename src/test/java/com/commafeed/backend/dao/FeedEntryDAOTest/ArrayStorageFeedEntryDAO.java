package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.backend.model.FeedEntry;

public class ArrayStorageFeedEntryDAO extends HashStorageFeedEntryDAO {

    //Size of the array of storage
    int size = 1000;

    FeedEntry[] array;

    public ArrayStorageFeedEntryDAO() {
        array = new FeedEntry[size];
    }

    public void forklift() {
        for (Integer key : hashMap.keySet()){
            array[key] = hashMap.get(key);
        }
    }

    @Override
    public void loadStorage(String filename) {
        super.loadStorage(filename);

    }

    @Override
    public void put(Integer key, FeedEntry feedEntry) {
        super.put(key, feedEntry);
    }

    @Override
    public FeedEntry feedEntry(int key, FeedEntry feedEntry) {
        return super.feedEntry(key, feedEntry);
    }

    @Override
    public void persistStorage(String filename) {
        super.persistStorage(filename);
    }
}
