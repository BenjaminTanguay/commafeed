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
	private GenericStorage<Long, FeedCategory> storage;
	
	public FeedCategoryStorage(SessionFactory sessionFactory, User user) {
		super(sessionFactory);
		this.user = user;
		testingList = new HashMap<Long, FeedCategory>();
		String serializationFilename = "TestStorage";
		storage = new GenericStorage<Long, FeedCategory>(serializationFilename);
	}

	public void loadStorage(GenericStorage<Long, FeedCategory> storage){
		this.storage = storage;
	}
	
	
	public GenericStorage<Long, FeedCategory> getStorage(){
		return storage;
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
			List<FeedCategory> originList = new ArrayList<FeedCategory>(super.findAll(user));
	    	Iterator<FeedCategory> iterator = originList.iterator();
	    	while(iterator.hasNext()){
	    		FeedCategory current = iterator.next();
	    		if(current.getId().equals(newCategory.getId())){
	    			current.setName(newCategory.getName());
	    			current.setParent(newCategory.getParent());
	    			current.setPosition(newCategory.getPosition());
	    			current.setChildren(newCategory.getChildren());
	    			
	    			//shadow write
	    			storage.create(current.getId(), current);
	    			storage.saveStorage();
	    			
	    			//actual write to old data store
	    			super.saveOrUpdate(originList);
	    		}
	    	}
	}

	private int checkAndFixInconsistency(FeedCategory expectedCategory){
		int inconsistencies = 0;
		if(!storage.exists(expectedCategory.getId())){
			//object doesn't exist; create category
			storage.create(expectedCategory.getId(), expectedCategory);
		}else{
			//fix any internal inconsistency
			if(!expectedCategory.getName().equals(storage.read(expectedCategory.getId()).getName())){
				storage.read(expectedCategory.getId()).setName(expectedCategory.getName());
				inconsistencies ++;
			}
			if(expectedCategory.getParent()!= null){
				if(!expectedCategory.getParent().getId().equals(storage.read(expectedCategory.getId()).getParent().getId())){
					storage.read(expectedCategory.getId()).setName(expectedCategory.getName());
					inconsistencies ++;
				}
			}
			if(!expectedCategory.getPosition().equals(storage.read(expectedCategory.getId()).getPosition())){
				storage.read(expectedCategory.getId()).setPosition(expectedCategory.getPosition());
				inconsistencies ++;
			}
			storage.saveStorage();
		} 	
		return inconsistencies;
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
		int inconsistencies = 0;
		List<FeedCategory> expectedList = super.findAll(user);
		Iterator<FeedCategory> expectedCategories = expectedList.iterator();
		while (expectedCategories.hasNext())
		{
		    FeedCategory expected = expectedCategories.next();
		    inconsistencies += checkAndFixInconsistency(expected);
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
