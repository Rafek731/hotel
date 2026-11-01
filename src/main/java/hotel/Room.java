package hotel;

import java.util.List;
import java.util.Objects;
import java.time.Clock;
import java.time.LocalDate;

public class Room {
    private final String number; // 3-digit code
    private final int pricePerNight; // in minor units (cents) per night in the hotel
    private final int capacity;
    private final String description; 
    private Stay stay;

    private static final String roomNumberRegexp = "^[0-9]{3}$";

    public Room(String number, int pricePerNight, int capacity, String description) {
        if (number == null || !number.matches(roomNumberRegexp)) {
            throw new IllegalArgumentException("The room number must be a 3-digit code.\nGot: " + number);
        }
        this.number = number;

        if (pricePerNight < 0) {
            throw new IllegalArgumentException("Price cannot be negative.\nGot: " + pricePerNight);
        }
        this.pricePerNight = pricePerNight;
        
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity of a room must be greater than 0.\nGot: " + capacity);
        }
        this.capacity = capacity;
        this.description = description == null ? "" : description;
        this.stay = null;
    }

    public String number() {
        return new String(number);
    }

    public int price() {
        return pricePerNight;
    }

    public int capacity() {
        return capacity;
    }

    public String description() {
        return new String(description);
    }

    public Stay stay() {
        return stay;
    }

    public boolean isOccupied() {
        return !isFree();
    }

    public boolean isFree() {
        return stay == null;
    }

    public List<Guest> guests() {
       return isOccupied() ? stay.allGuests() : null;
    }

    public Guest mainGuest() {
        return isOccupied() ? stay.mainGuest() : null;
    }

    public List<Guest> additionalGuests() {
        return isOccupied() ? stay.additionalGuests() : null;
    }

    public int currentCost() {
        return isOccupied() ? stay.cost() : 0; 
    }

    public int numGuests() {
        return isOccupied() ? stay.allGuests().size() : 0;
    }

    public int checkOut() {
        if (isFree()) {
            throw new IllegalStateException("Room " + number + " is not occupied");
        }
        int fee = stay.cost();
        stay = null;
        return fee;
    }

    public void checkIn(List<Guest> guests, int plannedNights, String remarks, LocalDate checkInDate, Clock clock) {
        if (isOccupied()) {
            throw new IllegalStateException("Room " + number + " is already occupied");
        }

        Objects.requireNonNull(guests, "Guests cannot be null");
        if (guests.size() > capacity) {
            throw new IllegalArgumentException("Room " + number + " has capacity " + capacity + " but got " + guests.size() + " guests");
        }

        stay = new Stay(checkInDate, plannedNights, pricePerNight, guests, remarks, clock);
    }
}
