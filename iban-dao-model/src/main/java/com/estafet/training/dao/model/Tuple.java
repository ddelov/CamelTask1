package com.estafet.training.dao.model;

/**
 * Created by Delcho Delov on 09/12/16.
 */
public class Tuple<K, V> {
    K key;
    V value;
    Tuple(K key, V value){
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
