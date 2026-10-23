package hotel.util.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MyMapTest {
    private MyMap<String, Integer> map;

    @Before
    public void setUp() {
        map = new MyMap<>();
    }

    @Test
    public void getReturnsValueAfterPut() {
        map.put("a", 1);

        assertEquals(Integer.valueOf(1), map.get("a"));
    }

    @Test
    public void putOverwritesExistingKey() {
        map.put("a", 1);
        map.put("a", 2);

        assertEquals(Integer.valueOf(2), map.get("a"));
        assertEquals(1, map.keys().size()); 
    }

    @Test
    public void getReturnsNullForMissingKey() {
        assertNull(map.get("missing"));
    }

    @Test
    public void containsReflectsPutAndRemove() {
        assertFalse(map.contains("a"));

        map.put("a", 1);
        assertTrue(map.contains("a"));

        map.remove("a");
        assertFalse(map.contains("a"));
    }

    @Test
    public void removeReturnsFalseForMissingKey() {
        assertFalse(map.remove("missing"));
    }

    @Test
    public void keysReturnsACopy() {
        map.put("a", 1);
        map.put("b", 2);

        map.keys().clear();

        assertTrue(map.contains("a"));
        assertTrue(map.contains("b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void putRejectsNullKey() {
        map.put(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void putRejectsNullValue() {
        map.put("a", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRejectsNullKey() {
        map.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeRejectsNullKey() {
        map.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void containsRejectsNullKey() {
        map.contains(null);
    }
}
