package com.commafeed.backend.dao.newstorage;

public class GenericStorage<Key, Value> {

    private HashStorage hashMap;
    private SerializeHashMap serialize;

    public GenericStorage(String filename) {
        this.hashMap = new HashStorage<Key, Value>();
        this.serialize = new SerializeHashMap(hashMap, filename);
    }

    public void loadStorage() {
        this.hashMap = this.serialize.loadMap();
    }

    public void saveStorage() {
        this.serialize.persistMap();
    }

    public boolean exists(Key key) {
        return this.hashMap.exists(key);
    }

    public void create(Key key, Value value) {
        this.hashMap.create(key, value);
    }

    public Value read(Key key) {
        return (Value) this.hashMap.read(key);
    }

    public Value update(Key key, Value value) {
        return (Value) this.hashMap.update(key, value);
    }

    public Value delete(Key key, Value value) {
        return (Value) this.hashMap.delete(key);
    }
}
