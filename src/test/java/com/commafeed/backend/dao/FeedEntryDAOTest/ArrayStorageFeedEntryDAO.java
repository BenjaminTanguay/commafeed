package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.frontend.model.Entries;

public class ArrayStorageFeedEntryDAO extends HashStorageFeedEntryDAO {

    //Size of the array of storage
    int size = 1000;

    Entries[] array;

    public ArrayStorageFeedEntryDAO() {
        array = new Entries[size];
    }

//    public void forklift() {
//        for (Entries entry : hashMap.values()){
//            array[]
//        }
//    }
}
