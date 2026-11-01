package hotel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RoomTest {
    // double room 101 for $120/night (12000c/night)
    private static final String number = "101";
    private static final int price = 12000;
    private static final int capacity = 2;
    private static final String description = "Standard - double room";
    private static final String remarks = "Late arrival";
    private static final int plannedStay = 5;
    private static final LocalDate today = LocalDate.of(2026, 10, 27);

    private AdvancingClock clock;
    private Guest main;
    private Guest second;
    private Guest third;
    private Room room;

    @Before
    public void setUp() {
        clock = new AdvancingClock(today);
        main = new Guest("Jan Kowalski");
        second = new Guest("Anna Kowalska");
        third = new Guest("Piotr Kowalski");
        room = new Room(number, price, capacity, description);
    }

    // construction

    @Test
    public void returnsRoomData() {
        assertEquals(number, room.number());
        assertEquals(price, room.price());
        assertEquals(capacity, room.capacity());
        assertEquals(description, room.description());
    }

    @Test
    public void nullDescriptionBecomesEmpty() {
        Room r = new Room(number, price, capacity, null);
        assertEquals("", r.description());
    }

    @Test
    public void allowsZeroPrice() {
        Room r = new Room(number, 0, capacity, description);
        assertEquals(0, r.price());
    }

    @Test
    public void allowsCapacityOfOne() {
        Room r = new Room(number, price, 1, description);
        assertEquals(1, r.capacity());
    }

    @Test
    public void deniesInvalidRoomNumbers() {
        for (String invalid : new String[] {null, "", "12", "1234", "abc", "10a", " 101"}) {
            try {
                new Room(invalid, price, capacity, description);
                fail("Expected IllegalArgumentException for room number: " + invalid);
            } catch (IllegalArgumentException expected) {
                // ok
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesNegativePrice() {
        new Room(number, -1, capacity, description);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesZeroCapacity() {
        new Room(number, price, 0, description);
    }

    // free room

    @Test
    public void newRoomIsFree() {
        assertTrue(room.isFree());
    }

    @Test
    public void freeRoomHasNoGuestsAndNoCost() {
        assertEquals(0, room.numGuests());
        assertEquals(0, room.currentCost());
        assertNull(room.guests());
        assertNull(room.mainGuest());
        assertNull(room.additionalGuests());
    }

    // check in

    @Test
    public void checkInOccupiesRoom() {
        room.checkIn(List.of(main, second), plannedStay, remarks, today, clock);

        assertTrue(room.isOccupied());
        assertEquals(today, room.stay().checkIn());
        assertEquals(plannedStay, room.stay().plannedStayDuration());
        assertEquals(today.plusDays(plannedStay), room.stay().plannedCheckout());
        assertEquals(remarks, room.stay().remarks());
    }

    @Test
    public void checkInStoresGuests() {
        room.checkIn(List.of(main, second), plannedStay, remarks, today, clock);

        assertEquals(2, room.numGuests());
        assertEquals(List.of(main, second), room.guests());
        assertSame(main, room.mainGuest());
        assertEquals(List.of(second), room.additionalGuests());
    }

    @Test
    public void checkInWithSingleGuest() {
        room.checkIn(List.of(main), plannedStay, null, today, clock);

        assertEquals(1, room.numGuests());
        assertSame(main, room.mainGuest());
        assertTrue(room.additionalGuests().isEmpty());
        assertEquals("", room.stay().remarks());
    }

    @Test
    public void stayUsesRoomPrice() {
        room.checkIn(List.of(main), plannedStay, remarks, today, clock);
        assertEquals(price, room.stay().price());
    }

    @Test
    public void allowsCheckInWithPastDate() {
        LocalDate checkin = today.minusDays(2);
        room.checkIn(List.of(main), plannedStay, remarks, checkin, clock);

        assertEquals(checkin, room.stay().checkIn());
        assertEquals(2 * price, room.currentCost());
    }

    @Test
    public void allowsGuestsUpToCapacity() {
        room.checkIn(List.of(main, second), plannedStay, remarks, today, clock);
        assertEquals(capacity, room.numGuests());
    }

    // rejected check in leaves the room unchanged

    @Test
    public void deniesCheckInAboveCapacity() {
        try {
            room.checkIn(List.of(main, second, third), plannedStay, remarks, today, clock);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(room.isFree());
        }
    }

    @Test
    public void deniesCheckInToOccupiedRoom() {
        room.checkIn(List.of(main), plannedStay, remarks, today, clock);
        Stay original = room.stay();

        try {
            room.checkIn(List.of(second), plannedStay, remarks, today, clock);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertSame(original, room.stay());
            assertSame(main, room.mainGuest());
        }
    }

    @Test
    public void deniesNullGuests() {
        try {
            room.checkIn(null, plannedStay, remarks, today, clock);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertTrue(room.isFree());
        }
    }

    @Test
    public void deniesNoGuests() {
        try {
            room.checkIn(List.of(), plannedStay, remarks, today, clock);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(room.isFree());
        }
    }

    @Test
    public void deniesZeroNights() {
        try {
            room.checkIn(List.of(main), 0, remarks, today, clock);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(room.isFree());
        }
    }

    @Test
    public void deniesFutureCheckInDate() {
        try {
            room.checkIn(List.of(main), plannedStay, remarks, today.plusDays(1), clock);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(room.isFree());
        }
    }

    @Test
    public void deniesNullCheckInDate() {
        try {
            room.checkIn(List.of(main), plannedStay, remarks, null, clock);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertTrue(room.isFree());
        }
    }

    @Test
    public void deniesNullClock() {
        try {
            room.checkIn(List.of(main), plannedStay, remarks, today, null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertTrue(room.isFree());
        }
    }

    // cost and check out

    @Test
    public void currentCostGrowsWithDays() {
        room.checkIn(List.of(main), plannedStay, remarks, today, clock);
        assertEquals(0, room.currentCost());

        clock.advanceDays(3);
        assertEquals(3 * price, room.currentCost());
    }

    @Test
    public void checkOutReturnsFeeAndFreesRoom() {
        room.checkIn(List.of(main, second), plannedStay, remarks, today, clock);
        clock.advanceDays(3);

        assertEquals(3 * price, room.checkOut());
        assertTrue(room.isFree());
        assertNull(room.stay());
        assertEquals(0, room.numGuests());
    }

    @Test
    public void checkOutSameDayCostsNothing() {
        room.checkIn(List.of(main), plannedStay, remarks, today, clock);
        assertEquals(0, room.checkOut());
        assertTrue(room.isFree());
    }

    @Test
    public void checkOutAcrossMonthBoundary() {
        // 2026-10-30 -> 2026-11-02 is 3 nights
        AdvancingClock monthEnd = new AdvancingClock(LocalDate.of(2026, 10, 30));
        room.checkIn(List.of(main), plannedStay, remarks, LocalDate.of(2026, 10, 30), monthEnd);
        monthEnd.advanceDays(3);

        assertEquals(3 * price, room.checkOut());
    }

    @Test
    public void checkOutChargesActualNotPlannedStay() {
        room.checkIn(List.of(main), plannedStay, remarks, today, clock);
        clock.advanceDays(2);

        assertEquals(2 * price, room.checkOut());
    }

    @Test(expected = IllegalStateException.class)
    public void deniesCheckOutOfFreeRoom() {
        room.checkOut();
    }

    @Test
    public void deniesSecondCheckOut() {
        room.checkIn(List.of(main), plannedStay, remarks, today, clock);
        room.checkOut();

        try {
            room.checkOut();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertTrue(room.isFree());
        }
    }

    // lifecycle

    @Test
    public void roomCanBeReusedAfterCheckOut() {
        room.checkIn(List.of(main, second), plannedStay, remarks, today, clock);
        Stay first = room.stay();
        clock.advanceDays(2);
        room.checkOut();

        room.checkIn(List.of(third), plannedStay, null, LocalDate.now(clock), clock);

        assertNotSame(first, room.stay());
        assertSame(third, room.mainGuest());
        assertEquals(List.of(third), room.guests());
        assertEquals("", room.stay().remarks());
        assertEquals(0, room.currentCost());
    }
}
