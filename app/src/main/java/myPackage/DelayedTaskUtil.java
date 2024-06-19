package myPackage;

import android.os.Handler;
import android.os.Looper;

public class DelayedTaskUtil {

    // Static method to execute a task after a specified delay
    public static void executeWithDelay(long delayMillis, Runnable task) {
        new Handler(Looper.getMainLooper()).postDelayed(task, delayMillis);
    }
}
