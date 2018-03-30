package com.commafeed.backend.dao;

import java.util.ArrayList;
import java.util.List;
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
/*
	@Override
	public String barcode(String barcode) {
		if (SaleToggles.isEnabledArray & SaleToggles.isEnabledHash) {
			String expected = hashMap.get(barcode);
			//shadow read
			String actual = array[Integer.parseInt(barcode)];
			if(! expected.equals(actual)) {
				migrationLog.error("Read Inconsistency");
				//fix the inconsistency
				array[Integer.parseInt(barcode)] = expected;
				violation(barcode, expected, actual);
			}
		}
		if (SaleToggles.isEnabledHash) {
			//return the expected value from the hash
			return super.barcode(barcode);
		}
		if (SaleToggles.isEnabledArray) {
			return array[Integer.parseInt(barcode)];
		}
		return null;
	}*/

	@Override
	public List<FeedCategory> findAll(User user) {
		 
		return super.findAll(user);
	}

	@Override
	public FeedCategory findById(User user, Long id) {
		return super.findById(user,id);
	}

	@Override
	public FeedCategory findByName(User user, String name, FeedCategory parent) {
		return super.findByName(user, name, parent);
	}

	@Override
	public List<FeedCategory> findByParent(User user, FeedCategory parent) {
		return super.findByParent(user, parent);
	}

	@Override
	public List<FeedCategory> findAllChildrenCategories(User user, FeedCategory parent) {
		return super.findAllChildrenCategories(user, parent);
	}

	/*
	@Override
	public void persistStorage(String filename) {
		if (SaleToggles.isEnabledHash) {
			super.persistStorage(filename);
		}
	}*/

	/*new should be the same as the old
	public int checkConsistency() {
		if(! (SaleToggles.isEnabledHash & SaleToggles.isEnabledArray)) {
			return 0;
		}
		int inconsistencies = 0;
		for (String barcode : hashMap.keySet()) {
			String expected = hashMap.get(barcode);
			String actual = array[Integer.parseInt(barcode)];
			if (!expected.equals(actual)) {
				migrationLog.error("Inconsistency in full check");
				//fix the inconsistency
				array[Integer.parseInt(barcode)] = expected;
				inconsistencies++;
				violation(barcode, expected, actual);		
			}
		}
		return inconsistencies;
	}*/
	public void print(String title){
		System.out.println(title);
    	System.out.println("------------------------------------------");
		for(FeedCategory category: testingList){
			System.out.println(category.getName());
			if(category.getChildren() != null)
				System.out.println(printList(category.getChildren()));
		}

    	System.out.println("------------------------------------------");
	}
    private String printList(Set<FeedCategory> categories){
    	for(FeedCategory category : categories){
    		if(category.getChildren() != null)
    			return "-"+printList(category.getChildren());
    		return "-"+category.getName();
    	}
    	return "";
    }
}
