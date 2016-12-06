package dkarelin.ru.rxjavaexample_1.rxbus;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 *
 */

public class App extends Application {


    private static App instance;

    private RxBus bus;


    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...


        instance = this;
        bus = new RxBus();
    }


    public static App get() {
        return instance;
    }


    public RxBus bus() {
        return bus;
    }


}
