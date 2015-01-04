package easyrssreader.sergeybudkov.ru.easyrssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Sergey Budkov 2536

public class DataBase {
    public SQLiteDatabase sqLiteDatabase;
    private Context context;
    public DatabaseHelper databaseHelper;

    public static final String DATABASE_NAME = "MyDataBase";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Channels";

    public static final String CHANNEL = "channel";
    

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + CHANNEL + " );";

    public DataBase(Context context) {
        this.context = context;
    }

    public DataBase open() throws SQLiteException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public long insertChannel(String name) {
        ContentValues values = new ContentValues();
        values.put(CHANNEL, name);
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    public void deleteChannel(String name) {
        Cursor cursor = sqLiteDatabase.query(DataBase.TABLE_NAME, new String[] {
                        DataBase.CHANNEL},
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            if (name.equals(cursor.getString(cursor.getColumnIndex(DataBase.CHANNEL))))
                sqLiteDatabase.delete(TABLE_NAME, CHANNEL + "=" + cursor.getColumnName(cursor.getColumnIndex(DataBase.CHANNEL)), null);
        }
    }

    public int deleteWeatherTable() {
        return sqLiteDatabase.delete(TABLE_NAME, null, null);
    }



    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("TABLE CREATE", DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
        }
    }
}