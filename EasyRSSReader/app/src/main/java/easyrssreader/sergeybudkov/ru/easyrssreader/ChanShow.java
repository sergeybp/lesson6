package easyrssreader.sergeybudkov.ru.easyrssreader;

/**
 * Created by Ser on 04.01.2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChanShow extends Activity {

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        String s = getIntent().getStringExtra("CHANNEL");
        setRssList(s);

    }

    ArrayAdapter<Entry> adapter;
    ArrayList<Entry> links = new ArrayList<Entry>();

    public void setRssList(String link) {
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<Entry>(this, R.layout.item, links);
        listView.setAdapter(adapter);

        new Load() {
            @Override
            protected void onPostExecute(FeedBack feed) {
                try {
                    if (feed.getException() != null) {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, invalid url or no internet", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    links.clear();
                    for (int i = 0; i < feed.getArray().size(); i++)
                        links.add(feed.getArray().get(i));
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(link);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Entry entry = links.get(i);
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SingleArticle.class);
                intent.putExtra("URL", entry.getLink());
                if (entry.getDescription() != null) {
                    intent.putExtra("Content", entry.getTitle() + "<br>" + entry.getDescription());
                }
                startActivity(intent);
            }

        });
    }
}