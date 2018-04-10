package com.commafeed.backend.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.commafeed.backend.model.FeedEntry;
import com.commafeed.backend.model.FeedEntryContent;

public class SerializeHashMap {
	
	 public static HashMap<Integer, FeedEntryContent> loadMap(String fileToBeRead) {
	        try {
	            FileInputStream fileIn = new FileInputStream(fileToBeRead);
	            ObjectInputStream in = new ObjectInputStream(fileIn);
	            HashMap<Integer, FeedEntry> map = (HashMap<Integer, FeedEntry>)in.readObject();
	            in.close();
	            fileIn.close();
	            return map;
	        }
	        catch (IOException i) {
	            i.printStackTrace();
	            return null;
	        } catch (ClassNotFoundException c) {
	            c.printStackTrace();
	            return null;
	        }
	    }

	    public static void persistMap(HashMap<Integer, FeedEntryContent> map, String FileToBeWrittenTo) {

	        try {
	            FileOutputStream fileOut = new FileOutputStream(FileToBeWrittenTo);
	            ObjectOutputStream out = new ObjectOutputStream(fileOut);
	            out.writeObject(map);
	            out.close();
	            fileOut.close();
	        }
	        catch (IOException i) {
	            i.printStackTrace();
	            return;
	        }
	    }
	}
}
