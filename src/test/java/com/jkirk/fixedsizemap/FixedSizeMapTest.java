package com.jkirk.fixedsizemap;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedSizeMapTest {

    @Test
    void maxSizeMustBeGreaterThanZero() {
        assertThatThrownBy(() -> new FixedSizeMap<String, String>(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Max size must be greater than 0");
    }

    @Test
    void associationsCanBeAddedAndRetrieved() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(1);
        String expectedValue = "aValue";

        map.add("aKey", expectedValue);

        assertThat(map.find("aKey")).isEqualTo(expectedValue);
    }

    @Test
    void addReturnsNullIfKeyDoesNotHaveAnAssociation() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(1);

        String addResult = map.add("aKey", "aValue");

        assertThat(addResult).isNull();
    }

    @Test
    void addReturnsPreviousAssociationForKey() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(1);
        String expectedAddResult = "aValue";

        map.add("aKey", expectedAddResult);
        String addResult = map.add("aKey", "anotherValue");

        assertThat(addResult).isEqualTo(expectedAddResult);
    }

    @Test
    void findReturnsNullIfNoAssociationExistsForKey() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(1);

        assertThat(map.find("nonExistentKey")).isNull();
    }

    @Test
    void secondAssociationRemovesFirstAssociationFromMapWithMaxSizeOfOne() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(1);

        map.add("aKey", "aValue");
        map.add("anotherKey", "anotherValue");

        assertThat(map.find("aKey")).isNull();
        assertThat(map.find("anotherKey")).isEqualTo("anotherValue");
    }

    @Test
    void leastAccessedAssociationIsRemovedWhenMapWouldGoOverMaxSize() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(3);
        map.add("aKey", "aValue");
        map.add("bKey", "bValue");
        map.add("cKey", "cValue");
        map.find("aKey");
        map.find("bKey");

        map.add("dKey", "dValue");

        assertThat(map.find("aKey")).isEqualTo("aValue");
        assertThat(map.find("bKey")).isEqualTo("bValue");
        assertThat(map.find("cKey")).isNull();
        assertThat(map.find("dKey")).isEqualTo("dValue");
    }

    @Test
    void leastRecentlyAddedAssociationIsRemovedWhenMultipleAssociationsHaveTheSameAccessCount() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(3);
        map.add("aKey", "aValue");
        map.add("bKey", "bValue");
        map.add("cKey", "cValue");
        map.find("bKey");

        map.add("dKey", "dValue");

        assertThat(map.find("aKey")).isNull();
        assertThat(map.find("bKey")).isEqualTo("bValue");
        assertThat(map.find("cKey")).isEqualTo("cValue");
        assertThat(map.find("dKey")).isEqualTo("dValue");
    }

    @Test
    void reusingAnAssociationKeyResetsTheAccessCountOfTheAssociation() {
        FixedSizeMap<String, String> map = new FixedSizeMap<>(2);
        map.add("aKey", "aValue");
        map.add("bKey", "bValue");
        map.find("aKey");
        map.find("bKey");

        map.add("bKey", "anotherBValue");
        map.add("cKey", "cValue");

        assertThat(map.find("aKey")).isEqualTo("aValue");
        assertThat(map.find("bKey")).isNull();
        assertThat(map.find("cKey")).isEqualTo("cValue");
    }
}