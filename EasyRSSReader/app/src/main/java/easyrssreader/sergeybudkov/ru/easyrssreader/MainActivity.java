package easyrssreader.sergeybudkov.ru.easyrssreader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText newRSS;
    Button addRSS;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_chan);
        addRSS = (Button) findViewById(R.id.addChan);
        newRSS = (EditText) findViewById(R.id.newChan);
        newRSS.setText("http://");
        listView = (ListView) findViewById(R.id.channels);
        listView.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[]{"url"}, new int[]{android.R.id.text1}, 0));
        getLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                final long x = id;
                final String s = (String) ((TextView) itemClicked).getText();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Вы выбрали:")
                        .setMessage(s)
                        .setCancelable(true)
                        .setNegativeButton("Удалить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        getContentResolver().delete(Uri.parse("content://ru.sergeybudkov.easyrssreader.feeds/channels"), "" + x, null);
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Загрузить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MainActivity.this, ChanShow.class);
                                intent.putExtra("CHANNEL", s);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })
                ;
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }


    public void addChan(View v){
        String name =newRSS.getText().toString();
        newRSS.setText("http://");
        if(name.equals("") || name.equals("http://")){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Поле пустое!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", name);
        getContentResolver().insert(Uri.parse("content://ru.sergeybudkov.easyrssreader.feeds/channels"), contentValues);
        //cBase.insertChannel(name);
        //baseLoad();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Uri.parse("content://ru.sergeybudkov.easyrssreader.feeds/channels"), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((CursorAdapter)listView.getAdapter()).swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        ((CursorAdapter)listView.getAdapter()).swapCursor(null);
    }
}



