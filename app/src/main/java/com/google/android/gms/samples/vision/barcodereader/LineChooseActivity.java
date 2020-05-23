package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LineChooseActivity extends Activity {

    private static final String TAG = "LineChooseActivity: ";

    //database definition
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;
    private DatabaseAdapterEmployee adapter = new DatabaseAdapterEmployee(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_choose);

        TextView textViewPerson = (TextView) findViewById(R.id.text_choosed_person);

        //fill textView from intent
        Intent intent = getIntent();
        String personString = intent.getStringExtra("person");
        String[] personArray = personString.split(" ");
        final String person = personArray[1] + " " + personArray[2] + " " + personArray[3];
        textViewPerson.setText(person);

        dataBaseHelper = new DataBaseHelper(this);
        Log.d(TAG, "created DataBaseHelper");

        //get data from DB
        adapter.open();
        //Log.d(TAG, "test1");
        final String[] data = new String[adapter.getLines().size()];
        adapter.getLines().toArray(data);

        //insert to listView
        ListView countriesList = (ListView) findViewById(R.id.list_lines);

        ArrayAdapter<String> adapterList = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        countriesList.setAdapter(adapterList);

        adapter.close();

        //click on list
        countriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //open line activity for choose submachine
                Intent intent = new Intent(view.getContext(), SubMachineActivity.class);
                intent.putExtra("person", person);
                intent.putExtra("line", data[position]);
                startActivity(intent);
            }
        });
    }
}
