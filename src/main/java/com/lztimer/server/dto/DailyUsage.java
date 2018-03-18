package com.lztimer.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Data
@AllArgsConstructor
public class DailyUsage {
    private String date;
    private String task;
    private long seconds;

    @Override
    public String toString() {
        return date + " " + task + " " + formatDuration(Duration.ofSeconds(seconds));
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}
