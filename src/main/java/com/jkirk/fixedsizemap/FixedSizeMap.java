package com.jkirk.fixedsizemap;

import java.util.*;

public class FixedSizeMap<K, V> {

    private final int maxSize;
    /* The storage of the associations between key & value */
    private final Map<K, V> associations;
    /* The count of how many times an association key has been accessed */
    private final Map<K, Integer> associationAccess;
    /**
     * Groupings of associations keys, based on how many times the keys have been accessed.
     * Keeping track of the association keys in groups makes it faster to find the eldest key when dealing with
     * associations that have been accessed the same number of times
     */
    private final Map<Integer, LinkedHashSet<K>> accessCountGroups;
    private int minimumAccessCounterGroupKey;

    /**
     * @param maxSize the max capacity of the map
     * @throws IllegalArgumentException if the given maxSize is zero or less
     */
    public FixedSizeMap(int maxSize) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("Max size must be greater than 0");
        }

        this.maxSize = maxSize;
        this.associations = new HashMap<>();
        this.associationAccess = new HashMap<>();
        this.accessCountGroups = new HashMap<>();
        this.accessCountGroups.put(0, new LinkedHashSet<>());
    }

    /**
     * Adds an association for the given key & value. If the map size would be greater than the allowed max size then
     * the least accessed association is removed to make space for the new association.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     * {@code null} if there was no mapping for the <tt>key</tt>.
     */
    public synchronized V add(K key, V value) {
        if (!associations.containsKey(key)) {
            if (associations.size() >= maxSize) {
                evictLeastUsedAssociation();
            }
        } else {
            removeKeyFromCurrentAccessCountGroup(key, associationAccess.get(key));
        }

        accessCountGroups.get(0).add(key);
        associationAccess.put(key, 0);
        minimumAccessCounterGroupKey = 0;
        return associations.put(key, value);
    }

    /**
     * Returns the value that is associated with the specified key,
     * or {@code null} if the map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the key, or
     * {@code null} if this map contains no mapping for the key
     **/
    public synchronized V find(K key) {
        associationAccess.computeIfPresent(key, this::updateAccessCount);
        return associations.get(key);
    }

    private void evictLeastUsedAssociation() {
        K leastAccessedKey = accessCountGroups.get(minimumAccessCounterGroupKey).iterator().next();
        accessCountGroups.get(minimumAccessCounterGroupKey).remove(leastAccessedKey);
        associationAccess.remove(leastAccessedKey);
        associations.remove(leastAccessedKey);
    }

    private int updateAccessCount(K key, Integer accessCount) {
        removeKeyFromCurrentAccessCountGroup(key, accessCount);

        int newAccessCount = accessCount + 1;
        accessCountGroups.computeIfAbsent(newAccessCount, LinkedHashSet::new).add(key);
        return newAccessCount;
    }

    private void removeKeyFromCurrentAccessCountGroup(K key, Integer accessCount) {
        LinkedHashSet<K> accessCountGroup = accessCountGroups.get(accessCount);
        accessCountGroup.remove(key);
    }
}
