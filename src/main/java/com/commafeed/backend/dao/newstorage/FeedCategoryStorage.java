package com.commafeed.backend.dao.newstorage;

import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.dao.newstorage.GenericStorage;
import com.commafeed.backend.model.FeedCategory;

public class FeedCategoryStorage implements
IStorageModelDAO<FeedCategory>{

	private GenericStorage<Long, FeedCategory> storage;
    private static FeedCategoryStorage instance;
	
    private FeedCategoryStorage(){
    	this.storage = new GenericStorage<Long, FeedCategory>("FeedCategoryStorage");
    }
    
    public static FeedCategoryStorage getInstance(){
    	if(instance == null){
    		instance = new FeedCategoryStorage();
    	}
    	return instance;
    }

    public static FeedCategoryStorage getTestInstance(){
    	return new FeedCategoryStorage();
    }
    
    @Override
    public boolean exists(FeedCategory category){
    	return this.storage.exists(category.getId());
    }
    
    @Override
    public void create(FeedCategory category){
    	this.storage.create(category.getId(), category);
    }
	
    @Override
    public FeedCategory read(Long id){
    	return this.storage.read(id);
    }
    
	@Override
	public FeedCategory read(FeedCategory category) {
		return read(category.getId());
	}
    
    @Override
    public FeedCategory update(FeedCategory category){
    	return this.storage.update(category.getId(), category);
    }
    
    @Override
    public FeedCategory delete(FeedCategory category) {
        return this.storage.delete(category.getId());
    }

    @Override
    public void serialize() {
        this.storage.saveStorage();
    }

    @Override
    public void deserialize() {
        this.storage.loadStorage();
    }

    /**
     * This method will act as a consistency checker
     * @param category
     * @return true -> if consistency is ok or was corrected, false if failure to fix
     */
    @Override
    public boolean isModelConsistent(FeedCategory category) {
        if (MigrationToggles.isConsistencyCheckerOn()) {
            FeedCategory modelFromStorage = read(category);
            if (category.equals(modelFromStorage)) {
                return true;
            } else {
                update(category);
                verification(category, modelFromStorage);
                return false;
            }
        }
        return true;
    }

    public void verification(FeedCategory expected, FeedCategory received) {
        System.out.println("Inconsistency found!\n\nObject in real database: " +
                "" + expected.toString() +
                "\n\nObject found in new storage: " + received.toString());
    }
}
