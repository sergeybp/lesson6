package easyrssreader.sergeybudkov.ru.easyrssreader;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {

    private NDataBase database;
    private static final int channelCount;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        channelCount = 100;
        uriMatcher.addURI("ru.sergeybudkov.easyrssreader.feeds", "channels", channelCount);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == channelCount) {
            if (database.sqLiteDatabase.delete("channel", "_id=" + (Long.parseLong(selection)), null) == 1) {
                getContext().getContentResolver().notifyChange(uri, null);
                return 1;
            } else {
                getContext().getContentResolver().notifyChange(uri, null);
                return 0;
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long number = -1;
        if (uriMatcher.match(uri) == channelCount) {
            number = database.sqLiteDatabase.insert("channel", null, contentValues);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri.toString() + "/" + number);
    }

    @Override
    public boolean onCreate() {
        database = NDataBase.getDataBase(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
       if (uriMatcher.match(uri) == channelCount) {
            cursor = database.sqLiteDatabase.query("channel", new String[]{"_id", "name", "url"}, null, null, null, null, null);
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != channelCount)
            throw new UnsupportedOperationException();
        if (database.sqLiteDatabase.update("channel", contentValues, "_id=" + contentValues.getAsLong("_id"), null) == 1) {
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
            return 0;
        }

    }
}