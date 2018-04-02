package com.commafeed.backend.dao.FeedEntryDAOTest;

import com.commafeed.frontend.model.Entries;

import java.io.*;
import java.util.HashMap;

public class SerializeHashMap {

    //Largely inspired by the class notes

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, Entries> loadMap(String fileName) {
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            HashMap<Integer, Entries> map = (HashMap<Integer, Entries>)in.readObject();
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

    public static void persistMap(HashMap<Integer, Entries> map, String fileName) {

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
