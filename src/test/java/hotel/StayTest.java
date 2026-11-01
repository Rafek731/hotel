package hotel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StayTest {
    // we checked in two days ago, with intention of staying 5 nights and price of $10/night (1000c/night)
    private static final int checkinDaysAgo = 2;
    private static final int plannedStay = 5;
    private static final int price = 1000;
    private static final String remarks = "Late arrival";
    private static final LocalDate today = LocalDate.of(2026, 10, 27);
    private static final LocalDate checkin = today.minusDays(checkinDaysAgo);
    private static final LocalDate plannedCheckout = checkin.plusDays(plannedStay);

    private AdvancingClock clock;
    private Guest main;
    private Guest second;
    private Guest third;
    private Stay stay;

    @Before
    public void setUp() {
        clock = new AdvancingClock(today);
        main = new Guest("Jan Kowalski");
        second = new Guest("Anna Kowalska");
        third = new Guest("Piotr Kowalski");
        stay = new Stay(checkin, plannedStay, price, List.of(main, second, third), remarks, clock);
    }

    // getters

    @Test
    public void returnsCheckinDate() {
        assertEquals(checkin, stay.checkIn());
    }

    @Test
    public void returnsPrice() {
        assertEquals(price, stay.price());
    }

    @Test
    public void returnsPlannedStayDuration() {
        assertEquals(plannedStay, stay.plannedStayDuration());
    }

    @Test
    public void calculatesPlannedCheckout() {
        assertEquals(plannedCheckout, stay.plannedCheckout());
    }

    @Test
    public void returnsRemarks() {
        assertEquals(remarks, stay.remarks());
    }

    @Test
    public void nullRemarksBecomeEmpty() {
        Stay s = new Stay(checkin, plannedStay, price, List.of(main), null, clock);
        assertEquals("", s.remarks());
    }

    // time calculations

    @Test
    public void returnsDaysSinceCheckin() {
        assertEquals(checkinDaysAgo, stay.daysSinceCheckin());
    }

    @Test
    public void returnsDaysLeft() {
        assertEquals(plannedStay - checkinDaysAgo, stay.daysLeft());
    }

    @Test
    public void calculatesCost() {
        assertEquals(checkinDaysAgo * price, stay.cost());
    }

    @Test
    public void costIsZeroOnCheckinDay() {
        Stay s = new Stay(today, plannedStay, price, List.of(main), remarks, clock);
        assertEquals(0, s.daysSinceCheckin());
        assertEquals(0, s.cost());
    }

    @Test
    public void costGrowsAsDaysPass() {
        clock.advanceDays(1);
        assertEquals((checkinDaysAgo + 1) * price, stay.cost());
    }

    @Test
    public void isNotCheckoutDayBeforePlannedCheckout() {
        assertFalse(stay.isCheckoutToday());
    }

    @Test
    public void isCheckoutDayOnPlannedCheckout() {
        clock.advanceDays(plannedStay - checkinDaysAgo);
        assertTrue(stay.isCheckoutToday());
        assertEquals(0, stay.daysLeft());
    }

    @Test
    public void isNotCheckoutDayAfterPlannedCheckout() {
        clock.advanceDays(plannedStay - checkinDaysAgo + 1);
        assertFalse(stay.isCheckoutToday());
    }

    @Test
    public void isActiveBeforeAndOnCheckoutDay() {
        assertTrue(stay.isActive());
        clock.advanceDays(plannedStay - checkinDaysAgo);
        assertTrue(stay.isActive());
    }

    @Test
    public void isNotActiveAfterCheckoutDay() {
        clock.advanceDays(plannedStay - checkinDaysAgo + 1);
        assertFalse(stay.isActive());
        assertEquals(-1, stay.daysLeft());
    }

    @Test
    public void defaultClockUsesCurrentDate() {
        Stay s = new Stay(LocalDate.now(), plannedStay, price, List.of(main), remarks);
        assertEquals(0, s.daysSinceCheckin());
        assertEquals(plannedStay, s.daysLeft());
    }

    // guests

    @Test
    public void firstGuestIsMainGuest() {
        assertEquals(main, stay.mainGuest());
    }

    @Test
    public void singleGuestHasNoAdditionalGuests() {
        Stay s = new Stay(checkin, plannedStay, price, List.of(main), remarks, clock);
        assertEquals(main, s.mainGuest());
        assertTrue(s.additionalGuests().isEmpty());
    }

    @Test
    public void returnsAdditionalGuestsInOrder() {
        assertEquals(List.of(second, third), stay.additionalGuests());
    }

    @Test
    public void returnsAllGuestsInOrder() {
        assertEquals(List.of(main, second, third), stay.allGuests());
    }

    @Test
    public void changingInputListDoesNotAffectStay() {
        List<Guest> input = new ArrayList<>(List.of(main));
        Stay s = new Stay(checkin, plannedStay, price, input, remarks, clock);
        input.add(second);
        input.set(0, third);
        assertEquals(List.of(main), s.allGuests());
    }

    @Test
    public void changingReturnedListsDoesNotAffectStay() {
        Guest intruder = new Guest("Ewa Nowak");
        stay.allGuests().add(intruder);
        stay.additionalGuests().clear();
        assertEquals(List.of(main, second, third), stay.allGuests());
        assertEquals(List.of(second, third), stay.additionalGuests());
    }

    // validation

    @Test
    public void allowsZeroPrice() {
        Stay s = new Stay(checkin, plannedStay, 0, List.of(main), remarks, clock);
        assertEquals(0, s.cost());
    }

    @Test
    public void allowsOneNightStay() {
        Stay s = new Stay(today, 1, price, List.of(main), remarks, clock);
        assertEquals(today.plusDays(1), s.plannedCheckout());
    }

    @Test
    public void allowsCreatingStayOnCheckoutDay() {
        Stay s = new Stay(today.minusDays(plannedStay), plannedStay, price, List.of(main), remarks, clock);
        assertTrue(s.isCheckoutToday());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesNegativePrice() {
        new Stay(checkin, plannedStay, -100, List.of(main), remarks, clock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesNegativeDuration() {
        new Stay(checkin, -1, price, List.of(main), remarks, clock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesZeroDuration() {
        new Stay(today, 0, price, List.of(main), remarks, clock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesStayPastPlannedCheckout() {
        new Stay(today.minusDays(plannedStay + 1), plannedStay, price, List.of(main), remarks, clock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesFutureCheckin() {
        new Stay(today.plusDays(1), plannedStay, price, List.of(main), remarks, clock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deniesNoGuests() {
        new Stay(checkin, plannedStay, price, List.of(), remarks, clock);
    }

    @Test(expected = NullPointerException.class)
    public void deniesNullCheckin() {
        new Stay(null, plannedStay, price, List.of(main), remarks, clock);
    }

    @Test(expected = NullPointerException.class)
    public void deniesNullGuestList() {
        new Stay(checkin, plannedStay, price, null, remarks, clock);
    }

    @Test(expected = NullPointerException.class)
    public void deniesNullGuestInList() {
        new Stay(checkin, plannedStay, price, Arrays.asList(main, null), remarks, clock);
    }

    @Test(expected = NullPointerException.class)
    public void deniesNullClock() {
        new Stay(checkin, plannedStay, price, List.of(main), remarks, null);
    }
}
