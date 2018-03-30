package com.commafeed.backend.dao;

import com.commafeed.backend.model.Feed;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.FeedEntryContent;
import com.commafeed.backend.model.FeedEntryStatus;
import com.commafeed.backend.model.FeedEntryTag;
import com.commafeed.backend.model.FeedSubscription;
import com.commafeed.backend.model.User;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FeedCategoryDAOTest extends AbstractDAOTest{


	private static UserDAO userDAO;							
	private static FeedCategoryDAO feedCategoryDAO;	
	private static FeedCategoryStorage feedCategoryStorage;
	private static List<Class> classes = new ArrayList<>();
	private static User testUser;
	
	@BeforeClass
    public static void beforeClass() {
		userDAO = new UserDAO(createSessionFactory(User.class));
		beginTransaction();
		testUser = userDAO.findByEmail("test@gmail.com");			//retrieve testing account
		closeTransaction();
		
		/* cannot create FeedCategoryDAO without following classes*/
		classes.add(User.class);
		classes.add(FeedCategory.class);
		classes.add(FeedSubscription.class);
		classes.add(Feed.class);
		classes.add(FeedEntryStatus.class);
		classes.add(FeedEntry.class);
		classes.add(FeedEntryContent.class);
		classes.add(FeedEntryTag.class);
		beginTransaction();
		feedCategoryStorage = new FeedCategoryStorage(createSessionFactory(classes), testUser);
		feedCategoryStorage.loadStorage();
		closeTransaction();
    }

    @Before
    public void beforeEachTest() {
        // This opens up the session with the database before each individual
        // tests.
        // It can run more than one query at a time.
        
    }

    @After
    public void afterEachTest() {
        // This closes up the session with the database after each
        // individual tests.
       
    }

    //@Test
    public void forkliftTest() {
    	//fork lift
    	//assertEquals(testingList,feedCategoryDAO.findAll(testUser));
    }
    
    @Test
    public void shadowWriteTest(){
    	//forklift();
    	//shadow write
    	/*shadowWrite();
    	String oldName = testingList.get(0).getName();
    	String newName = "Canadian News";
    	testingList.get(0).setName(newName);
    	feedCategoryDAO.saveOrUpdate(testingList);
    	printList(testingList,"List");*/
    	//assertEquals(oldName,newName);
    	
    	//shadow read
    	
    	//check inconsistency
    	feedCategoryStorage.forklift();
    	feedCategoryStorage.print("Copied List");
    	
    }
    
    

    
    private void shadowWrite(){
    	
    	//feedCategoryDAO.saveOrUpdate(newCategory);
    }
    
    private FeedCategory createFakeCategory(){
    	int SIZE_OF_LIST = 5;
    	//create fake list of category names
    	List<String> sports = new ArrayList<String>();
    	sports.add("Basketball");
    	sports.add("Soccer");
    	sports.add("Baseball");
    	sports.add("Football");
    	sports.add("Hockey");
    	FeedCategory newCategory = new FeedCategory();					
    	Set<FeedCategory> childCategories = new HashSet<FeedCategory>();
    	//create parent category
    	newCategory = createCategory(1100L, "Sports", null, 0, null );
    	//create sub categories
    	for(int i = 0; i < SIZE_OF_LIST; i++){
    		childCategories.add(createCategory(new Long(1100+i), sports.get(i), newCategory, 0, null ));
    	}
    	newCategory.setChildren(childCategories);
    	return newCategory;
    }
    
    private void shadowRead(){
    	
    }
    
    private void checkInconsistencies(){
    	
    }
    
    private FeedCategory createCategory(Long id, String name, FeedCategory parent, 
    		int position, Set<FeedSubscription> subscriptions  ){
    
    	FeedCategory newCategory = new FeedCategory();
    	newCategory.setId(id);
    	newCategory.setName(name);
    	newCategory.setParent(parent);
    	newCategory.setPosition(0);
    	newCategory.setSubscriptions(subscriptions);
    	newCategory.setUser(testUser);
    	
    	return newCategory;
    }
    	
}
