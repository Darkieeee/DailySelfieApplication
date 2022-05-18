package com.example.dailyselfie;

public class AppConstants {

    public static class Alarm {
        public final static long INTERVAL_TWO_MINUTES_CLOCK = 2 * 60 * 1000L;
    }

    public static class Notification {
        public static final int    NOTIFICATION_ID     = 1;
        public static final String CHANNEL_ID          = "SFA";
        public static final String CHANNEL_NAME        = "Selfie App";
        public static final String CHANNEL_DESCRIPTION = "Selfie App Channel to communicate";
    }

    public static class ImageFile {
        public final static String JPG_SUFFIX = ".jpg";
        public final static String PNG_SUFFIX = ".png";
    }

    public static class Directory {
        public final static String DEFAULT_IMAGE_DELETED_DIRECTORY = "/image_deleted/";
    }
}
