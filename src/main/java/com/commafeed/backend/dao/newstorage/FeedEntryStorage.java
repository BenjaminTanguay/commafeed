package com.commafeed.backend.dao.newstorage;

import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.UserSettings;

public class FeedEntryStorage implements
        IStorageModelDAO<FeedEntry> {

    private GenericStorage<Long, FeedEntry> storage;
    private static FeedEntryStorage instance;

    private FeedEntryStorage(){
        this.storage = new GenericStorage<Long, FeedEntry>("FeedEntry");
    }

    public static FeedEntryStorage getInstance(){
        if(instance == null){
            instance = new FeedEntryStorage();
        }
        return instance;
    }

    public static FeedEntryStorage getTestInstance(){
        return new FeedEntryStorage();
    }

    @Override
    public boolean exists(FeedEntry model) {
        return this.storage.exists(model.getFeed().getId());
    }

    @Override
    public void create(FeedEntry model) {
        this.storage.create(model.getFeed().getId(), model);

    }

    @Override
    public FeedEntry read(FeedEntry model) {
        return read(model.getFeed().getId());
    }

    @Override
    public FeedEntry read(Long id) {
        return this.storage.read(id);
    }

    @Override
    public FeedEntry update(FeedEntry model) {
        return this.storage.update(model.getFeed().getId(), model);
    }

    @Override
    public FeedEntry delete(FeedEntry model) {
        return this.storage.delete(model.getFeed().getId(), model);
    }

    @Override
    public void serialize() {
        this.storage.saveStorage();
    }

    @Override
    public void deserialize() {
        this.storage.loadStorage();
    }

    @Override
    public boolean isModelConsistent(FeedEntry model) {
        FeedEntry modelFromStorage = read(model);
        if(model.equals(modelFromStorage)){
            return true;
        }
        else{
            update(model);
            verification(model, modelFromStorage);
            return false;
        }
    }

    public void verification(FeedEntry expected, FeedEntry received) {
        System.out.println("Inconsistency found!\n\nObject in real database: " +
                "" + expected +
                "\n\nObject found in new storage: " + received);
    }
}
