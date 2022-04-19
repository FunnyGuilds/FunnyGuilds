package net.dzikoysk.funnyguilds.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FunnyTime {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

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

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FunnyTime && time.equals(((FunnyTime) obj).time);
    }

    @Override
    public String toString() {
        return "FunnyTime{" +
                "time=" + time +
                ", formattedTime='" + formattedTime + '\'' +
                '}';
    }

}
