package com.commafeed.backend.dao;

import com.commafeed.backend.model.FeedEntryTag;

import java.io.*;
import java.util.HashMap;

public class SerializaHashMap {
	

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, FeedEntryTag> loadMap(String fileName) {
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            HashMap<Integer, FeedEntryTag> map = (HashMap<Integer, FeedEntryTag>)in.readObject();
            in.close();
            fileIn.close();
            return map;
        }
        catch(FileNotFoundException | ClassCastException e) {
            return null;
        }
        catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }
    }

    public static void persistMap(HashMap<Integer, FeedEntryTag> map, String fileName) {

        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
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


