package dkarelin.ru.rxjavaexample_1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.jakewharton.rxbinding.widget.RxTextView;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * https://habrahabr.ru/post/265997/
 * https://github.com/ReactiveX/RxAndroid
 *
 * http://www.slideshare.net/MailRuGroup/buzzwords-everywhere-mailru-group
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private TextView tvMessage;

    private EditText searchEditText;

    private EditText etLogin;
    private EditText etPassword;
    private Button btnLogin;

    private StorIOSQLite storIOSQLite;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Run RX
        myObservable.subscribe(mySubscriber);

        // ------------------------------------------------------------------

        tvMessage = (TextView) findViewById(R.id.tvMessage);
        searchEditText = (EditText) findViewById(R.id.search_edit);

        etLogin = (EditText) findViewById(R.id.et_login);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setEnabled(false);

        // Observable.just("yay").subscribe(s -> Log.d(TAG, "STRING: " + s));

        trimAndFilterText();

        loginAndPassword();
    }


    /**
     * Отслеживает, чтоб 2 поля были заполнены
     */
    private void loginAndPassword() {
        Observable
                .combineLatest(
                     RxTextView.textChanges(etLogin),
                     RxTextView.textChanges(etPassword),
                     (login, password) -> login.length() > 0 && password.length() > 0)
                     .subscribe(btnLogin::setEnabled);
    }


    private void trimAndFilterText() {
        RxTextView
                .textChanges(searchEditText)
                .map(text -> text.toString().trim())
                .filter(text -> text.length() > 3)
                .subscribe(s -> tvMessage.setText("Request on server: " + s));
    }


    // ****************************************

    /**
     * EXAMPLE handle error
     */
    public void networkrequest() {
        Subscription subscription = getUsers()
                .subscribeOn(Schedulers.io())                   // выполняем в другом потоке всё вверху по цепочке
                // .observeOn(Schedulers.computation())         // для примеры вычисляет в другом потоке используя по максимуму процессор
                .observeOn(AndroidSchedulers.mainThread())      // выполняем всё в главном потоке вниз по цепочке
                .subscribe(users -> {
                    // get users
                }, error -> {
                    // handle error
                });

        // somewhere
        subscription.unsubscribe();
    }


    /**
     * Моделируем работу с сетью
     * @return
     */
    private Observable<List<String>> getUsers() {
        return Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                //
            }
        });
    }



    // *********************************************

    /**
     * Комбинируем результаты с 2-х источников
     * @param searchText
     */
    private void combineNetworkWithDb(String searchText) {
        Observable.combineLatest(searchInLocalDB(searchText), searchInRestApi(searchText),
                (tweets, tweets2) -> {
                    // Что делаем с твитами с обеими источниками
                    tweets.get(0).getAuthor();
                    return null;
        });
    }


    // Rx get in SqliteDB
    // https://github.com/pushtorefresh/storio

    /**
     * Имитируем запрос на локальную БД
     * @return
     */
    private Observable<List<Tweet>> searchInLocalDB(String searchText) {
        // Example !!!
        /*List<Tweet> tweets = storIOSQLite
                .get()
                .listOfObjects(Tweet.class) // Type safety
                .withQuery(Query.builder() // Query builder
                        .table("tweets")
                        .where("author = ?")
                        .whereArgs("artem_zin") // Varargs Object..., no more new String[] {"I", "am", "tired", "of", "this", "shit"}
                        .build()) // Query is immutable — you can save it and share without worries
                .prepare() // Operation builder
                .executeAsBlocking(); // Control flow is readable from top to bottom, just like with RxJava*/

        // RxObservable
        return storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .table("tweets")
                        .build())
                .prepare()
                .asRxObservable();   // Get Result as rx.Observable and subscribe to further updates of tables from Query!

                /*
                //.observeOn() // All Rx operations work on Schedulers.io()
                .subscribe(tweet -> {
                            // Please don't forget to unsubscribe  !!!!
                            // Will be called with first result and then after each change of tables from Query
                            // Several changes in transaction -> one notification

                            //adapter.setData(tweet); !!!!!!!!
                        }
                );*/
    }


    /**
     * Имитируем запрос retrofit на REST сервер
     * @param searchText
     * @return
     */
    @NonNull
    private Observable<List<Tweet>> searchInRestApi(String searchText) {
        return Observable.create((Observable.OnSubscribe<List<Tweet>>) subscriber -> {
            //
        });
    }






    // ******************* EX 1 **********************************************

    /**
     * OBSERVABLE
     */
    Observable<String> myObservable = Observable.create(
            new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> sub) {
                    sub.onNext("Hello, world!");
                    sub.onCompleted();
                }
            }
    );


    /**
     * SUBSCRIBER
     */
   Subscriber<String> mySubscriber = new Subscriber<String>(){

       @Override
       public void onCompleted() {

       }

       @Override
       public void onError(Throwable e) {

       }

       @Override
       public void onNext(String s) {
           Log.d(TAG, "RX Out: " + s);
       }
   };






















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
