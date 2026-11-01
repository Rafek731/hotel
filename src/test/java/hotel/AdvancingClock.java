package hotel;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

// Clock that tests can move forward to simulate days passing during a stay
class AdvancingClock extends Clock {
    private Instant instant;

    AdvancingClock(LocalDate date) {
        this.instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    void advanceDays(long days) {
        instant = instant.plus(Duration.ofDays(days));
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
