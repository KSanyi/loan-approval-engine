package hu.lae.util;

import java.util.Objects;

public class Pair<T, S> {

    public final T v1;
    
    public final S v2;

    public Pair(T v1, S v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    @Override
    public String toString() {
        return "[" + v1 + ", " + v2 + "]";
    }
    
    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(other == null || !(other instanceof Pair)) return false;
        @SuppressWarnings("unchecked")
        Pair<T, S> otherPair = (Pair<T, S>)other;
        return v1.equals(otherPair.v1) && v2.equals(otherPair.v2);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(v1, v2);
    }
    
}
