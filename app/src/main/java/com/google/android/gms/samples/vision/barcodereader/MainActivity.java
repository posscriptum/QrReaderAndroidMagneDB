/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity {

    //database definition
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;
    private DatabaseAdapterEmployee adapter = new DatabaseAdapterEmployee(this);

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dataBaseHelper = new DataBaseHelper(this);

        Log.d(TAG, "created DataBaseHelper");

        //get data from DB
        adapter.open();
        //Log.d(TAG, "test1");
        final String[] data = new String[adapter.getUsers().size()];
        adapter.getUsers().toArray(data);
        Log.d(TAG, data[0]);

        ListView countriesList = (ListView) findViewById(R.id.listFragment);

        ArrayAdapter<String> adapterList = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        countriesList.setAdapter(adapterList);

        adapter.close();

        //click on list
        countriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(TAG, "itemClick: position = " + position + ", id = "
                        + id);
                //open line activity for choose line
                Intent intent = new Intent(view.getContext(), LineChooseActivity.class);
                intent.putExtra("person",data[position]);
                startActivity(intent);
            }
        });
    }

}