package hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Stay {
    private final List<Guest> guests;
    private final LocalDate checkIn;
    private final int plannedNights;
    private final int pricePerNight; // in minor units copy from room
    private final String remarks;
    private final Clock clock;
    
    // constructors
    
    public Stay(LocalDate checkin, int plannedStayDuration, int pricePerNight, List<Guest> guests, String remarks, Clock clock) {
        this.checkIn = Objects.requireNonNull(checkin, "Checkin date cannot be null");
        this.plannedNights = plannedStayDuration;
        this.pricePerNight = pricePerNight;
        this.guests = List.copyOf(Objects.requireNonNull(guests, "Guests cannot be null"));
        this.remarks = remarks == null ? "" : remarks;
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");

        checkStayValidity();
    }

    public Stay(LocalDate checkIn, int plannedStayDuration, int price, List<Guest> guests, String remarks) {
        this(checkIn, plannedStayDuration, price, guests, remarks, Clock.systemDefaultZone());
    }
    
    // public

    public LocalDate checkIn() {
        return checkIn;
    }

    public int price() {
        return pricePerNight;
    }

    public int plannedStayDuration() {
        return plannedNights;
    }

    public LocalDate plannedCheckout() {
        return checkIn().plusDays(plannedNights);
    }

    public int daysSinceCheckin() {
        return (int)ChronoUnit.DAYS.between(checkIn, today());
    }
    
    public int daysLeft() {
        return (int)ChronoUnit.DAYS.between(today(), plannedCheckout());
    }
    
    public boolean isCheckoutToday() {
        return today().isEqual(plannedCheckout());
    }

    public boolean isActive() {
        return !today().isAfter(plannedCheckout());
    }
    
    public int cost() {
        return daysSinceCheckin() * pricePerNight;
    }

    public String remarks() {
        return remarks;
    }

    public Guest mainGuest() {
        return guests.getFirst();
    }

    public ArrayList<Guest> additionalGuests() {
        if (guests.size() == 1) {
            return new ArrayList<>();
        }
        return new ArrayList<>(guests.subList(1, guests.size()));
    }

    public ArrayList<Guest> allGuests() {
        return new ArrayList<>(guests);
    }

    // private 
    
    private void checkStayValidity() {
        if (this.plannedNights < 1) {
            throw new IllegalArgumentException("Stay duration must be at least 1 night");
        }
        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price per night cannot be negative");
        }
        if (plannedCheckout().isBefore(today())) {
            throw new IllegalArgumentException("Guests should be checked out already");
        }
        if (checkIn().isAfter(today())) {
            throw new IllegalArgumentException("We don't handle future stays");
        }
        if (guests.size() < 1) {
            throw new IllegalArgumentException("Stay must have at least 1 guest");
        }
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
