package com.commafeed.backend.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.SessionFactory;

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

	private static  List<FeedCategory> testingList; 
	static FeedCategoryStorage feedCategoryStorage;
	private User user;
	private int readInconsistencies = 0;
	
	@Inject
	public FeedCategoryStorage(SessionFactory sessionFactory, User user) {
		
		super(sessionFactory);
		this.user = user;
		testingList = new ArrayList<>();
	}
	
	public int getReadInconsistencies() {
		return readInconsistencies;
	}

	public void loadStorage(){
		forklift();
	}
	
    public void forklift(){
    	testingList = super.findAll(user);
    }
	
	@Override
	public void saveOrUpdate(FeedCategory newCategory) {
			//shadow write
			testingList.add(newCategory);
		
			//actual write to old data store
			super.saveOrUpdate(newCategory);
	}

	@Override
	public List<FeedCategory> findAll(User user) {
		return super.findAll(user);
	}

	@Override
	public FeedCategory findById(User user, Long id) {
		FeedCategory expectedCategory = super.findById(user,id);
		
		//shadow read
		boolean exist = false;
		for(FeedCategory item : testingList){
			if(item.getId() == id && item.getUser() == user){
				exist = true;
				if(expectedCategory != item)	//fix any inconsistency	
					item = expectedCategory;
			}
		}
		if(!exist){
			System.out.println("Item Doesnt Exist");
		}
		return expectedCategory;
	}

	@Override
	public FeedCategory findByName(User user, String name, FeedCategory parent) { 
		FeedCategory expectedCategory = super.findByName(user, name, parent);
		
		//shadow read
		boolean exist = false;
		for(FeedCategory item : testingList){
			if((item.getName() == name) && (item.getUser() == user) &&(item.getParent() == parent)){
				exist = true;
				if(expectedCategory != item)	//fix any inconsistency	
					item = expectedCategory;
			}
		}
		if(!exist){
			System.out.println("Item Doesnt Exist");
		}
		return expectedCategory;
	}

	@Override
	public List<FeedCategory> findByParent(User user, FeedCategory parent) {

		List<FeedCategory> expectedCategory = super.findByParent(user, parent);
		
		//shadow read
		boolean exist = false;
		for(FeedCategory item : testingList){
			if((item.getUser() == user) &&(item.getParent() == parent)){
				exist = true;
				if(expectedCategory != item){	//fix any inconsistency	
					item.setChildren(expectedCategory.get(0).getChildren());
				}
			}
		}
		if(!exist){
			System.out.println("Item Doesnt Exist");
		}
		return expectedCategory;
	}

	@Override
	public List<FeedCategory> findAllChildrenCategories(User user, FeedCategory parent) {

		List<FeedCategory> expectedCategory = super.findAllChildrenCategories(user, parent);
		
		//shadow read
		boolean exist = false;
		for(FeedCategory item : testingList){
			if((item.getUser() == user) &&(item.getParent() == parent)){
				exist = true;
				if(expectedCategory != item){	//fix any inconsistency	
					
				}
			}
		}
		if(!exist){
			System.out.println("Item Doesnt Exist");
		}
		return expectedCategory;
	}

	//new should be the same as the old
	public int checkConsistency() {
		int inconsistencies = 0;
		List<FeedCategory> expectedList = super.findAll(user);
		Iterator<FeedCategory> expectedCategories = expectedList.iterator();
		Iterator<FeedCategory> actualCategories = testingList.iterator();
		while (expectedCategories.hasNext() && actualCategories.hasNext())
		{
		    FeedCategory expected = expectedCategories.next();
		    FeedCategory actual = actualCategories.next();
		    if (!expected.equals(actual)) {
				//fix the inconsistency
				actual = expected;
				inconsistencies++;	
			}
		}
		return inconsistencies;
	}
	public void print(String title){
		System.out.println(title);
    	System.out.println("------------------------------------------");
		for(FeedCategory category: testingList){
			System.out.println(category.getName());
		}
    	System.out.println("------------------------------------------");
	}
}
