package com.commafeed.backend.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.glassfish.hk2.utilities.reflection.Logger;
import org.hibernate.SessionFactory;

import com.commafeed.backend.dao.newstorage.GenericStorage;
import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.FeedEntryStatus;
import com.commafeed.backend.model.FeedEntryTag;
import com.commafeed.backend.model.FeedSubscription;
import com.commafeed.backend.model.QUser;
import com.commafeed.backend.model.User;
import com.querydsl.core.types.Predicate;

public class FeedCategoryStorage extends FeedCategoryDAO{

	private HashMap<Long, FeedCategory> testingList;
	static FeedCategoryStorage feedCategoryStorage;
	private User user;
	private int inconsistencies = 0;
	private GenericStorage<Long, FeedCategory> storage;
	
	public FeedCategoryStorage(SessionFactory sessionFactory, User user) {
		super(sessionFactory);
		this.user = user;
		testingList = new HashMap<Long, FeedCategory>();
		String serializationFilename = "TestStorage";
		storage = new GenericStorage<Long, FeedCategory>(serializationFilename);
	}
	
	public int getReadInconsistencies() {
		return inconsistencies;
	}
	
	public void loadStorage(GenericStorage<Long, FeedCategory> storage){
		this.storage = storage;
	}
	
    public void forklift(){
    	List<FeedCategory> originList = super.findAll(user);
    	for(FeedCategory i : originList){
    		storage.create(i.getId(),i);
    		testingList.put(i.getId(),i);
    	}
    	storage.saveStorage();
    }
    
    public void updateOnlyDatabase(){
    	List<FeedCategory> updatedCategory = new ArrayList<FeedCategory>(super.findAll(user));
    	updatedCategory.get(0).setName("Testing News");
    	super.saveOrUpdate(updatedCategory);
    }
    
    public void testDeletion(){
    	Iterator<FeedCategory> children = testingList.get(0).getChildren().iterator();
    	while(children.hasNext()){
    		FeedCategory current = children.next();
    		if(current.getName().equals("CNN")){
    			System.out.println("Deleting " + current.getName());
    			super.delete(current);
    		}
    	}
    }
	
	@Override
	public void saveOrUpdate(FeedCategory newCategory) {
			//shadow write
			storage.create(newCategory.getId(), newCategory);
			storage.saveStorage();
			//actual write to old data store
			super.saveOrUpdate(newCategory);
	}

	private void checkAndFixInconsistency(FeedCategory expectedCategory){
		if(!storage.exists(expectedCategory.getId()) || 
				!expectedCategory.equals(storage.read(expectedCategory.getId()))){
				//fix any inconsistency
				storage.create(expectedCategory.getId(), expectedCategory);
				storage.saveStorage();
		}
	}
	
	@Override
	public FeedCategory findById(User user, Long id) {
		FeedCategory expectedCategory = super.findById(user,id);
		
		//shadow read & validate
		checkAndFixInconsistency(expectedCategory);
		return expectedCategory;
	}
	
	@Override
	public FeedCategory findByName(User user, String name, FeedCategory parent) { 
		FeedCategory expectedCategory = super.findByName(user, name, parent);
		
		//shadow read & validate
		checkAndFixInconsistency(expectedCategory);
		return expectedCategory;
	}

	@Override
	public List<FeedCategory> findByParent(User user, FeedCategory parent) {

		List<FeedCategory> expectedCategory = super.findByParent(user, parent);
		//shadow read
		Iterator<FeedCategory> expected = expectedCategory.iterator();
		while(expected.hasNext()){
			FeedCategory current = expected.next();
			checkAndFixInconsistency(current);
		}
		return expectedCategory;
	}

	@Override
	public List<FeedCategory> findAllChildrenCategories(User user, FeedCategory parent) {

		List<FeedCategory> expectedCategory = super.findAllChildrenCategories(user, parent);
		//shadow read
		Iterator<FeedCategory> expected = expectedCategory.iterator();
		while(expected.hasNext()){
			FeedCategory current = expected.next();
			checkAndFixInconsistency(current);
		}
		return expectedCategory;
	}

	//new should be the same as the old
	public int checkConsistency() {
		List<FeedCategory> expectedList = super.findAll(user);
		Iterator<FeedCategory> expectedCategories = expectedList.iterator();
		while (expectedCategories.hasNext())
		{
		    FeedCategory expected = expectedCategories.next();
		    if(!storage.exists(expected.getId()) || 
					!expected.equals(storage.read(expected.getId()))){
					//fix any inconsistency
		    	System.out.println("expected: " + expected);
		    	System.out.println("actual: " + storage.read(expected.getId()));
					//storage.create(expected.getId(), expected);
					//storage.saveStorage();
					inconsistencies++;
			}
		}
		return inconsistencies;
	}

	public void print(String title){
		System.out.println(title);
    	System.out.println("------------------------------------------");
		for(Long i : testingList.keySet()){
			System.out.println(storage.read(i).getName());
		}
    	System.out.println("------------------------------------------");
	}
}
