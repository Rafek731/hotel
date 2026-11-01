package hotel;

public class Guest {
    public String name;

    public Guest(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name of guest cannot be empty");
        }
        this.name = name;
    }
}

