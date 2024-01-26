package com.petros.bibernate.collection;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class LazySet<T> implements Set<T>{

    private Supplier<Set<T>> collectionSupplier;
    private Set<T> internalSet;

    public LazySet(Supplier<Set<T>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    public Set<T> getInternalSet() {
        if (internalSet == null) {
            internalSet = collectionSupplier.get();
        }
        return internalSet;
    }

    @Override
    public int size() {
        return getInternalSet().size();
    }

    @Override
    public boolean isEmpty() {
        return getInternalSet().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getInternalSet().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return getInternalSet().iterator();
    }

    @Override
    public Object[] toArray() {
        return getInternalSet().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return getInternalSet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return getInternalSet().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getInternalSet().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getInternalSet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getInternalSet().addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getInternalSet().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getInternalSet().removeAll(c);
    }

    @Override
    public void clear() {
        getInternalSet().clear();
    }

    @Override
    public boolean equals(Object o) {
        return getInternalSet().equals(o);
    }

    @Override
    public int hashCode() {
        return getInternalSet().hashCode();
    }

    @Override
    public Spliterator<T> spliterator() {
        return getInternalSet().spliterator();
    }

    public static <E> Set<E> of() {
        return Set.of();
    }

    public static <E> Set<E> of(E e1) {
        return Set.of(e1);
    }

    public static <E> Set<E> of(E e1, E e2) {
        return Set.of(e1, e2);
    }

    public static <E> Set<E> of(E e1, E e2, E e3) {
        return Set.of(e1, e2, e3);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4) {
        return Set.of(e1, e2, e3, e4);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) {
        return Set.of(e1, e2, e3, e4, e5);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return Set.of(e1, e2, e3, e4, e5, e6);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return Set.of(e1, e2, e3, e4, e5, e6, e7);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return Set.of(e1, e2, e3, e4, e5, e6, e7, e8);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return Set.of(e1, e2, e3, e4, e5, e6, e7, e8, e9);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return Set.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }

    @SafeVarargs
    public static <E> Set<E> of(E... elements) {
        return Set.of(elements);
    }

    public static <E> Set<E> copyOf(Collection<? extends E> coll) {
        return Set.copyOf(coll);
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return getInternalSet().toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return getInternalSet().removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return getInternalSet().stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return getInternalSet().parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getInternalSet().forEach(action);
    }
}
