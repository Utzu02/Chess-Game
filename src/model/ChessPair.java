package model;

import java.util.Objects;

public class ChessPair<K extends Comparable<K>,V> implements Comparable<ChessPair<K,V>> {
    private  K key;
    private  V value;

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(ChessPair<K, V> o) {
        return this.key.compareTo(o.key);
    }

    public String showPair() {
        return key.toString() + " " + value.toString();
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChessPair<?, ?> other)) {
            return false;
        }
        return Objects.equals(key, other.key);
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
