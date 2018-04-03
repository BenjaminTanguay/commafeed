package com.commafeed.backend.dao;

import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedEntryStatus;
import com.commafeed.backend.model.User;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FeedEntryStatusStorage extends FeedEntryStatusDAO{

    private HashMap<Long, FeedEntryStatus> testingList;
    static FeedEntryStatusStorage feedEntryStatusStorage;
    private User user;
    private int readInconsistencies = 0;

    public FeedEntryStatusStorage(SessionFactory sessionFactory, User user) {

        super(sessionFactory);
        this.user = user;
        testingList = new HashMap<Long, FeedEntryStatus>();
    }

    public int getReadInconsistencies() {
        return readInconsistencies;
    }

    public void forklift(){
        List<FeedEntryStatus> originList = super.findAll(user);
        int key = 0;
        for(FeedEntryStatus i : originList){
            testingList.put(i.getId(), i);
            key++;
        }
        print("testing List", testingList);

    }

    public void updateOnlyDatabase(){
        List<FeedCategory> updatedCategory = new ArrayList<FeedCategory>(super.findAll(user));
        updatedCategory.get(0).setName("Testing News");
        super.saveOrUpdate(updatedCategory);
        print("TestingList ", testingList);
        //print("copy list", copyList);
    }

    public void testDeletion(){
        //final Logger logger = Logger.getLogger();
        Iterator<FeedCategory> children = testingList.get(0).getChildren().iterator();
        while(children.hasNext()){
            FeedCategory current = children.next();
            if(current.getName().equals("CNN")){
                //current.setName("testing");
                //testingList.remove(current);
                System.out.println("Deleting " + current.getName());
                super.delete(current);
            }
        }
    }

    @Override
    public void saveOrUpdate(FeedCategory newCategory) {
        //shadow write
        testingList.put(newCategory.getId(), newCategory);
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

        //shadow read & validate
        if(expectedCategory != testingList.get(id))
            fixInconsistency(expectedCategory);
        return expectedCategory;
    }

    public void fixInconsistency(FeedCategory origin){
        testingList.get(origin.getId()).setName(origin.getName());
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
        while (expectedCategories.hasNext())
        {
            FeedCategory expected = expectedCategories.next();
            if (!expected.equals(testingList.get(expected.getId()))) {
                inconsistencies++;
                System.out.println("expected: " + expected.getName());
                System.out.println("actual: " + testingList.get(expected.getId()));
                //fix the inconsistency
                //actual = expected;
            }
        }
        return inconsistencies;
    }
    public void print(String title, HashMap<Long,FeedCategory> list){
        System.out.println(title);
        System.out.println("------------------------------------------");
        for(Long i : list.keySet()){
            System.out.println(list.get(i).getName());
        }
        System.out.println("------------------------------------------");
    }
}

