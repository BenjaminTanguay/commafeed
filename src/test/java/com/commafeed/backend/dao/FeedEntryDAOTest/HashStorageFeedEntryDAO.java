package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.backend.model.FeedEntry;
import com.commafeed.frontend.model.Entries;

import java.util.HashMap;


public class HashStorageFeedEntryDAO implements StorageFeedEntryDAO {

    HashMap<Integer,FeedEntry> hashMap = new HashMap<>();

    public void loadStorage(String filename) {
        hashMap = SerializeHashMap.loadMap(filename);
    }

    public void put(Integer key, FeedEntry feedEntry){
        hashMap.put(key, feedEntry);
    }

    @Override
    public FeedEntry feedEntry(int key, FeedEntry feedEntry) {
        return hashMap.get(key);
    }

    public void persistStorage(String filename){
        SerializeHashMap.persistMap(hashMap, filename);
    }
}
