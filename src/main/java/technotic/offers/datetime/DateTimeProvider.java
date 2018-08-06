package technotic.offers.datetime;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateTimeProvider {

    public Date getNow() {
        return new Date();
    }
}
