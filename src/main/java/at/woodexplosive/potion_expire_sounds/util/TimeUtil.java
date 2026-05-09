package at.woodexplosive.potion_expire_sounds.util;

public class TimeUtil {

    public static String formatDuration(int ticks) {
        int seconds = ticks / 20;

        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            int min = seconds / 60;
            int sec = seconds % 60;
            return min + "m " + sec + "s";
        } else {
            int hours = seconds / 3600;
            int min = (seconds % 3600) / 60;
            return hours + "h " + min + "m";
        }
    }
}
