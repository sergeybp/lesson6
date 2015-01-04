package easyrssreader.sergeybudkov.ru.easyrssreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText newRSS;
    Button addRSS;
    ListView listView;
    DataBase cBase;
    String[] finalChans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_chan);
        addRSS = (Button) findViewById(R.id.addChan);
        newRSS = (EditText) findViewById(R.id.newChan);
        newRSS.setText("http://");
        listView = (ListView) findViewById(R.id.channels);
        cBase = new DataBase(this);
        cBase.open();
        baseLoad();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                final String s = (String) ((TextView) itemClicked).getText();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Вы выбрали:")
                        .setMessage(s)
                        .setCancelable(false)
                        .setNegativeButton("Удалить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteChan(s);
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

    public void deleteChan(String name){
        cBase.deleteChannel(name);
        for(int  i = 0;i<finalChans.length;i++){
            if(!finalChans[i].equals(name)){
                cBase.insertChannel(finalChans[i]);
            }
        }
        baseLoad();
    }

    public void baseLoad(){
        Cursor cursor = cBase.sqLiteDatabase.query(DataBase.TABLE_NAME, new String[] {
                       DataBase.CHANNEL},
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<String> chans = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String channel = cursor.getString(cursor.getColumnIndex(DataBase.CHANNEL));
            chans.add(channel);
        }
        finalChans = new String[chans.size()];
        for(int i = 0 ; i  < chans.size();i++){
            finalChans[i] = chans.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,finalChans);
        listView.setAdapter(adapter);
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
        cBase.insertChannel(name);
        baseLoad();
    }


}



