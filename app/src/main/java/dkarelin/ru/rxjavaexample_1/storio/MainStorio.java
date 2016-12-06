package dkarelin.ru.rxjavaexample_1.storio;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import dkarelin.ru.rxjavaexample_1.MySqlOpenHelper;

/**
 * Created
 */

public class MainStorio {


    private Context context;

    public MainStorio(Context context) {
        this.context = context;
    }


    // Проверяем запрос к БД
    public void testQueryDb() {
        Log.d("MYTAG", "Test query  DB");

        // SQLiteTypeMapping<User> typeMapping = SQLiteTypeMapping. <User>builder().putResolver().getResolver().deleteResolver().build();

        // Init Storio
        /*StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new MySqlOpenHelper(context))
                .addTypeMapping(User.class, null) // required for object mapping
                .build();*/


        User u1 = User.newUser(1L, "Vasya", "36", "Yapapapa");
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "Hryak");
        contentValues.put("age", "37");

       /* storIOSQLite
                .put()
                //.contentValues(contentValues).
                .object(u1)
                .prepare()
                .executeAsBlocking();*/

        Log.d("MYTAG", "Put content in DB");

    }


}
