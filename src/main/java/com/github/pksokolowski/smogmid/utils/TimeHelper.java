package com.github.pksokolowski.smogmid.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {
    public static String getDateTimeStampString(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  dd.MM.yy");
        return formatter.format(new Date(millis));
    }
}
