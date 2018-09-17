### configure java.util.logging.Logger in android
https://stackoverflow.com/questions/4561345/how-to-configure-java-util-logging-on-android

### 关于briar
在briar项目中使用了java.util.logging.Logger。并且实现了一个有趣的事情：在release版本中删除全部的日志，在debug版本中保留全部日志

### 该demo探求了：
* java.util.logging.Logger在android中如何实现：
    * 如何设置多种level和handler
    * 关于log hierarchy，参考文档：http://tutorials.jenkov.com/java-logging/logger-hierarchy.html
* java.util.logging.Logger在android中的实现原理：
    * android使用AndroidHandler将java.util.logging.Logger转化为了android.util.Log
    * 为什么使用java.util.logging.Logger无法输出INFO以下level的日志，如何重写AndroidHandler使其能够输出INFO以下level的日志
    * 关于AndroidHandler的详细代码见：https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/logging/AndroidHandler.java