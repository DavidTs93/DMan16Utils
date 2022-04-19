package me.DMan16.POPUtils.Classes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class MapEntry<V,T> implements Map.Entry<V,T> {
    private final V key;
    private T value;

    public MapEntry(V key,T value) {
        this.key = key;
        this.value = value;
    }
    
    @NotNull
    @Contract(value = "_,_ -> new",pure = true)
    public static <V,T> MapEntry<V,T> of(V key,T value) {
        return new MapEntry<>(key,value);
    }
    
    public V getKey() {
        return key;
    }
    
    public T getValue() {
        return value;
    }
    
    public T setValue(T value) {
        T old = this.value;
        this.value = value;
        return old;
    }
    
    /**
     * Work with ANY {@link Map.Entry} object!
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Map.Entry<?,?> entry) && Objects.equals(key,entry.getKey()) && Objects.equals(value,entry.getValue());
    }
    
    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }
    
    public String toString() {
        return key + "=" + value;
    }
}
