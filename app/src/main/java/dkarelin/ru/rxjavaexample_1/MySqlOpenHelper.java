package dkarelin.ru.rxjavaexample_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite open helper
 */

public class MySqlOpenHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "tracks.sqlite";
    private static final int VERSION = 1;


    private static final String TABLE_USERS = "users";
    private static final String COLUM_USERS_NAME = "name";
    private static final String COLUMN_USERS_AGE = "age";
    private static final String COLUMN_USERS_INFO = "info";

    /**
     * Cstor
     * @param context
     */
    public MySqlOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUM_USERS_NAME + " TEXT, "
                + COLUMN_USERS_AGE + " TEXT, "
                + COLUMN_USERS_INFO + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }


}
