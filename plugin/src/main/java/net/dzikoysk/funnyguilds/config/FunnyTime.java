package net.dzikoysk.funnyguilds.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FunnyTime {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime time;
    private final String formattedTime;

    public FunnyTime(LocalTime time, DateTimeFormatter format) {
        this.time = time;
        this.formattedTime = format.format(time);
    }

    public FunnyTime(LocalTime time) {
        this(time, TIME_FORMATTER);
    }

    public FunnyTime(int hour, int minute) {
        this(LocalTime.of(hour, minute), TIME_FORMATTER);
    }

    public LocalTime getTime() {
        return time;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

}
