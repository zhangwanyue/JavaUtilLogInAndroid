package com.example.vera.logtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity{

    //关于log hierarchy，参考文档：http://tutorials.jenkov.com/java-logging/logger-hierarchy.html
    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                androidLog();
//                filelog();
//                consoleLog();
//                removeRootHandlers();
//                testIsLoggable();
                resetLogLevel();
            }
        });
    }

    /**
     * using android.util.Log
     */
    public void androidLog(){
        Log.i("BUPTNSRC", "INFO: this is a log using android.util.Log");
        Log.d("BUPTNSRC", "DEBUG: this is a log using android.util.Log");
    }

    /**
     * SEVERE (highest value)
     * WARNING
     * INFO (android的logcat默认只显示大于等于该等级的）
     * CONFIG
     * FINE
     * FINER
     * FINEST (lowest value)
     *
     * using java.util.logging in android:
     * https://stackoverflow.com/questions/4561345/how-to-configure-java-util-logging-on-android
     * the default logging handler shipped with Android, it ignores any log messages with level lower than INFO. You don't see DEBUG etc. messages.
     *
     */
    public void resetLogLevel(){
        System.out.println("default level inherit from parent, parent log(root log) level: " + logger.getParent().getLevel());
        //need to set level to finest to show all level log.
        logger.setLevel(Level.FINEST);
        logger.finest("using android handler, it can not show log messages with level lower than INFO..."); //该条显示不出
        AndroidLoggingHandler.reset(new AndroidLoggingHandler());
        logger.finest("reset a new android handler writing by ourselves, now it can show log messages with level lower than INFO!!!"); //reset之后就可以显示了
    }

    /**
     * remove root handlers and add a new file handler
     * find log in file which is in android device:
     * "/data/data/com.example.vera.logtest/logTest_1.log"
     */
    public void removeRootHandlers(){
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            System.out.println("which handler: " + handler.getClass()); //com.android.internal.logging.AndroidHandler
            //The default console handler(com.android.internal.logging.AndroidHandler using Log.println to print log) is attached to the root logger, which is a parent of all other loggers including yours.
            rootLogger.removeHandler(handler);
        }
        try {
            FileHandler fileHandler = new FileHandler("/data/data/com.example.vera.logtest/logTest_1.log");
            rootLogger.addHandler(fileHandler);
            rootLogger.info("remove rootHandler and add fileHandler");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/util/logging/Handler.html
     * - Handler
     *   - MemoryHandler
     *   - StreamHandler
     *     - ConsoleHandler
     *     - FileHandler
     *     - SocketHandler
     */
    public void filelog(){
        try {
            FileHandler fileHandler = new FileHandler("/data/data/com.example.vera.logtest/logTest_2.log");
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("this is a FileHandler test");
    }

    public void consoleLog(){
        ConsoleHandler consoleHandler =new ConsoleHandler();
        logger.addHandler(consoleHandler);
        logger.info("this is a consoleHandler test");
    }

    /**
     * https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/logging/AndroidHandler.java
     * 该Handler处理java.util.logging.Logger，将其转化为android中的log
     * java.util.logging.Logger中对应的log等级也做相应的转化，对应于android中log的相应等级
     *
     * 在AndroidHandler的publish方法中，使用android.util.Log.isLoggable检查tag是否可以在level等级下被打印（tag的等级是否大于level）
     * 但是android.util.Log.isLoggable中设定的tag的默认log level是INFO，所以level值如果小于INFO都会被isLoggable判断为false
     * 这就导致了使用java.util.logging时，只能打印INFO及以上level的日志
     *
     * android.util.Log.isLoggable中的说明：
     * Checks to see whether or not a log for the specified tag is loggable at the specified level
     * The default level of any tag is set to INFO. This means that any level above and including INFO will be logged.
     */
    public void testIsLoggable(){
        System.out.println("is level severe loggable: " + Log.isLoggable("hello", AndroidLoggingHandler.getAndroidLevel(Level.SEVERE)));
        System.out.println("is level info loggable: " + Log.isLoggable("hello",  AndroidLoggingHandler.getAndroidLevel(Level.INFO)));
        System.out.println("is level fine loggable: " + Log.isLoggable("hello",  AndroidLoggingHandler.getAndroidLevel(Level.FINE))); //该项的输出是false
        logger.severe("this is a severe test");
        logger.info("this is a info test");
        logger.fine("this is a fine test"); //该条就不能正常输出
    }

}
