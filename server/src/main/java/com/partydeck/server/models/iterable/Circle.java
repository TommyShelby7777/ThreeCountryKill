package com.partydeck.server.models.iterable;

import com.partydeck.server.models.helpers.Identifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An object representing a circle of entries
 * @author Itay Schechner
 * @version 1.1.2
 * @param <T> the type of the entry
 */
public class Circle<T extends Identifiable<String>> implements Iterable<T> {

    private final ArrayList<T> circle;

    /**
     * Default constructor - initializes the queue.
     */
    public Circle() {
        this.circle = new ArrayList<>();
    }

    public void addEntry(T player){
        circle.add(player);
    }

    public List<String[]> asList(Function<T, String> extraField){
        return circle.stream().map(p -> new String[]{p.getId(), extraField.apply(p)}).collect(Collectors.toList());
    }

    public int count(Function<T,Boolean> condition){
        return (int) circle.stream().filter(condition::apply).count();
    }

    public int size(){
        return circle.size();
    }
    @Override
    public Iterator<T> iterator() {
        return circle.iterator();
    }
}
