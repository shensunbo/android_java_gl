
package com.example.timer2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
public class Test {
    private String message;
    private int timer = 0;

    public Test(String message, int timer) {
        this.message = message;
        this.timer = timer;
    }

    public int getTimer() {
        return timer;
    }

    public void updateTimer() {
        this.timer -= 1;
    }

    public void resetTimer(int timer) {
        this.timer = timer;
    }

    public String getMessage() {
        return message;
    }

    public String getCurrentTimeWithJavaTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return now.format(formatter);
    }
}
