package com.example.vera.logtest;


import android.util.Log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by vera on 18-9-17.
 */

public class AndroidLoggingHandler extends Handler {

    /**
     * 将java.util.logging.Logger转化成android.util.Log
     * 使用getAndroidLevel()方法将java.util.logging.Logger的level对应成android.util.Log的level
     * 使用android.util.Log.println()打印日志
     * @param record
     */
    @Override
    public void publish(LogRecord record) {
        if(!super.isLoggable(record)){
            return;
        }

        //set maxLength to tag, and substring tag if it exceeds.
        String name = record.getLoggerName();
        int maxLength = 30;
        String tag = name.length() > maxLength ? name.substring(name.length() - maxLength) : name;

        try {
            int level = getAndroidLevel(record.getLevel());
            Log.println(level, tag, record.getMessage());
            if(record.getThrown() != null){
                Log.println(level, tag, Log.getStackTraceString(record.getThrown()));
            }
        } catch (RuntimeException e) {
            Log.e("AndroidLoggingHandler", "Error logging message.", e);
        }
    }

    @Override
    public void close() throws SecurityException {
        //no need to close, but must implement abstract method.
    }

    @Override
    public void flush() {
        //no need to flush, but must implement abstract method.
    }

    /**
     * copy from: https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/logging/AndroidHandler.java
     *
     * Converts a {@link java.util.logging.Logger} logging level into an Android one.
     *
     * @param level The {@link java.util.logging.Logger} logging level.
     *
     * @return The resulting Android logging level.
     */
    static int getAndroidLevel(Level level){
        int value = level.intValue();
        if(value >= 1000){ //SEVERE
            return Log.ERROR;
        }else if(value >= 900){ //WARNING
            return Log.WARN;
        }else if(value >= 800){ //INFO
            return Log.INFO;
        }else{
            return Log.DEBUG;
        }
    }

    /**
     * remove all default root handlers in root logger, and reset a new rootHandler
     * @param rootHandler
     */
    public static void reset(Handler rootHandler){
        //get root logger.
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for(Handler handler : handlers){
            //remove all handlers in rootLogger.
            rootLogger.removeHandler(handler);
        }
        //add new handler to rootLogger.
        rootLogger.addHandler(rootHandler);
    }
}
