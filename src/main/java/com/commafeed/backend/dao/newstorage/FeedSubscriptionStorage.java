package com.commafeed.backend.dao.newstorage;


import com.commafeed.backend.dao.datamigrationtoggles.MigrationToggles;
import com.commafeed.backend.dao.newstorage.GenericStorage;
import com.commafeed.backend.model.FeedSubscription;


public class FeedSubscriptionStorage implements IStorageModelDAO<FeedSubscription> {


	
	private GenericStorage<Long, FeedSubscription> storage;
	private static FeedSubscriptionStorage instance;
	
	private FeedSubscriptionStorage() {
	    // Provide the name of the file it will be serialized to
	    this.storage = new GenericStorage<Long, FeedSubscription>("FeedSubscriptions");
	}
	
	public static FeedSubscriptionStorage getInstance() {
	    if (instance == null) {
	        instance = new FeedSubscriptionStorage();
	    }
	    return instance;
	}
	
	public static FeedSubscriptionStorage getTestInstance() {
	    return new FeedSubscriptionStorage();
	}

	@Override
	public boolean exists(FeedSubscription feedSub) {
		return this.storage.exists(feedSub.getId());
	}

	@Override
	public void create(FeedSubscription feedSub) {
		this.storage.create(feedSub.getId(), feedSub);
		
	}

	@Override
	public FeedSubscription read(FeedSubscription feedSub) {
		return this.storage.read(feedSub.getId());
	}

	@Override
	public FeedSubscription read(Long id) {
		return this.storage.read(id);
	}

	@Override
	public FeedSubscription update(FeedSubscription feedSub) {
		return this.storage.update(feedSub.getId(), feedSub);
	}

	@Override
	public FeedSubscription delete(FeedSubscription feedSub) {
		return this.storage.delete(feedSub.getId(), feedSub);
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
	public boolean isModelConsistent(FeedSubscription model) {
		if (MigrationToggles.isConsistencyCheckerOn()) {
		FeedSubscription fsImport = read(model);
        if(model.equals(fsImport)){
            return true;
        }else{
            update(model);
            verification(model, fsImport);
		return false;
        }
       }
		return true;
	}
	
    private void verification(FeedSubscription expectedSubFeed, FeedSubscription importSubFeed) {
        System.out.println("Inconsistency found!\n\nObject in real database: " +
                "" + expectedSubFeed +
                "\n\nObject found in new storage: " + importSubFeed);
    }

	

}
