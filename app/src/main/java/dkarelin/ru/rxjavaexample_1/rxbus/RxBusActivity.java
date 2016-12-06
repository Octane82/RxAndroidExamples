package dkarelin.ru.rxjavaexample_1.rxbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;

import dkarelin.ru.rxjavaexample_1.R;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RxBusActivity extends AppCompatActivity {

    private Subscription busSubscription;
    TextView tvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);


        tvContent = (TextView) findViewById(R.id.tvContent);
        findViewById(R.id.btnClickMe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.get().bus().send(new Events.Message("Click at(ms): " + System.currentTimeMillis()));
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        autoUnsubBus();
        busSubscription = App.get().bus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> handlerBus(o));
    }


    private void handlerBus(Object o) {
        if (o instanceof Events.Message) {
            tvContent.setText(((Events.Message) o).message);
        }
    }


    private void autoUnsubBus() {
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoUnsubBus();
    }
}
