package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.frontend.model.Entries;

import java.util.HashMap;


public class HashStorageFeedEntryDAO implements StorageFeedEntryDAO {

    HashMap<Integer,Entries> hashMap = new HashMap<>();

    public void loadStorage(String filename) {
        hashMap = SerializeHashMap.loadMap(filename);
    }

    public void put(Integer key, Entries entry){
        hashMap.put(key, entry);
    }

    @Override
    public Entries entry(int key,Entries entry) {
        return hashMap.get(key);
    }

    public void persistStorage(String filename){
        SerializeHashMap.persistMap(hashMap, filename);
    }
}
