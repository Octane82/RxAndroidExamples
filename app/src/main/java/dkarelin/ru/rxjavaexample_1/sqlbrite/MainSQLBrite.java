package dkarelin.ru.rxjavaexample_1.sqlbrite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import dkarelin.ru.rxjavaexample_1.MySqlOpenHelper;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created
 */

public class MainSQLBrite {

    private final String TAG = this.getClass().getSimpleName();

    private Context context;

    public MainSQLBrite(Context context) {
        this.context = context;
    }


    //
    public void dbExample() {

        SqlBrite sqlBrite = new SqlBrite.Builder().build();

        BriteDatabase db = sqlBrite.wrapDatabaseHelper(new MySqlOpenHelper(context), Schedulers.io());


        // -----------
//        db.insert("users", insertUser("Hryak", "37", "Lorem ipsum"));
//        db.insert("users", insertUser("Vasya Pupkin", "24", "Dolor sit amet"));



        //Observable<SqlBrite.Query> users = db.createQuery("users", "SELECT * FROM users");
        //users.subscribe(query -> { Cursor cursor = query.run(); mapUsers(cursor); });

        //Observable<SqlBrite.Query> users = db.createQuery("users", "SELECT * FROM users");

        db.createQuery("users", "SELECT * FROM users")
        .map(query -> query.run())
                .flatMap(cursor -> mapUsers(cursor))
                .subscribe(user -> Log.d(TAG, "User RX-J: " + user.getName()));

    }


    /**
     * Метод возвращает Observable users по данным курсора
     * @param cursor
     * @return
     */
    private Observable<UsersB> mapUsers(Cursor cursor) {
        List<UsersB> u = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                u.add(new UsersB(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)));
            }
            cursor.close();
        }
        return Observable.from(u);
    }




    /**
     * Insert new user in DB
     * @param name
     * @param age
     * @param info
     * @return
     */
    private ContentValues insertUser(String name, String age, String info) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("age", age);
        contentValues.put("info", info);
        return contentValues;
    }

}
