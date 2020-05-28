package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubMachineActivity extends Activity {

    LinearLayout llMain;
    LinearLayout lineTags1;
    LinearLayout lineTags2;
    LinearLayout lineTags3;
    LinearLayout lineTags4;
    LinearLayout lineTags5;

    boolean btnSaveToDbClk;

    private static final String TAG = "SubMachineActivity: ";
    //database definition
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;
    private DatabaseAdapterEmployee adapter = new DatabaseAdapterEmployee(this);
    private int positionClick = -1;
    private boolean[] checkedSubmachine;

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView barcodeValue;
    private static final int RC_BARCODE_CAPTURE = 9001;
    String[] data;
    Barcode barcode = null;
    String person;
    String line;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_machine);

        btnSaveToDbClk = true;

        //create new event
        Event event = new Event();

        //get links on view
        barcodeValue = (TextView)findViewById(R.id.comments);
        autoFocus = (CompoundButton) findViewById(R.id.auto_focus_sub);
        useFlash = (CompoundButton) findViewById(R.id.use_flash_sub);

        //check autofocus
        autoFocus.setChecked(true);

        TextView textViewPerson = (TextView) findViewById(R.id.text_choosed_person);
        TextView textViewLine = (TextView) findViewById(R.id.text_line);

        //fill textView from intent
        Intent intent = getIntent();
        person = intent.getStringExtra("person");
        textViewPerson.setText(person);
        line = intent.getStringExtra("line");
        textViewLine.setText(line);

        //start working with DB
        dataBaseHelper = new DataBaseHelper(this);
        Log.d(TAG, "created DataBaseHelper");

        //get data from DB
        adapter.open();
        data = new String[adapter.getSubmachine(line).size()];
        checkedSubmachine = new boolean[adapter.getSubmachine(line).size()];
        adapter.getSubmachine(line).toArray(data);

        //insert to listView
        ListView sumachinesList = (ListView) findViewById(R.id.list_submachines);
        sumachinesList.setAdapter(new SubmachineAdapter(this, R.layout.list_item, data));

        //close DB
        adapter.close();

        //click on list
        sumachinesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(btnSaveToDbClk){
                    Intent intent = new Intent(view.getContext(), BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
                    intent.putExtra("positionClick", position);
                    //checkedSubmachine[position] = true;
                    intent.putExtra("checkedSubmachine", checkedSubmachine);

                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                }
            }
        });

        // add buttons with tags
        lineTags1 = (LinearLayout) findViewById(R.id.line1);
        lineTags2 = (LinearLayout) findViewById(R.id.line2);
        lineTags3 = (LinearLayout) findViewById(R.id.line3);
        lineTags4 = (LinearLayout) findViewById(R.id.line4);
        lineTags5 = (LinearLayout) findViewById(R.id.line5);
        LinearLayout[] lines = new LinearLayout[]{lineTags1, lineTags2, lineTags3, lineTags4, lineTags5};
        int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(wrapContent, wrapContent);
        //int btnGravity = Gravity.LEFT;
        //lParams.gravity = btnGravity;

        //get tags from resources
        String[] Tags = this.getResources().getStringArray(R.array.tags);
        Button[] buttons = new Button[Tags.length];

        //listener to buttons with tags
        final EditText editText = (EditText) findViewById(R.id.comments);
        View.OnClickListener oclBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Меняем текст в TextView (tvOut)
                String str = editText.getText().toString();
                if (str != ""){str += " ";}
                editText.setText(str + ((Button)v).getText());
            }
        };

        //program create buttons with tags
        int currentTagindex = 0;
        for(int l = 1; l <= 5; l++){
            LinearLayout currentLine = lines[l-1];
            for(int r = 1; r <= 3; r++){
                if(currentTagindex < Tags.length)    {
                    Button btnNew = new Button(this);
                    btnNew.generateViewId();
                    btnNew.setText(Tags[currentTagindex]);
                    currentLine.addView(btnNew, lParams);
                    buttons[currentTagindex] = btnNew;
                    btnNew.setOnClickListener(oclBtn);
                    currentTagindex++;
                }
            }
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    barcodeValue.setText(barcode.displayValue);
                    positionClick = data.getIntExtra("positionClick", -1);
                    checkedSubmachine = data.getBooleanArrayExtra("checkedSubmachine");
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);

                    Button btnSaveToDb = (Button) findViewById(R.id.button_insert_data_to_db);
                    btnSaveToDb.setVisibility(View.VISIBLE);

                    //insert to listView
                    ListView sumachinesList = (ListView) findViewById(R.id.list_submachines);
                    sumachinesList.setAdapter(new SubmachineAdapter(this, R.layout.list_item, this.data));

                    btnSaveToDbClk = false;
                } else {
                    //Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //check then all items on listview was check
    private boolean checkFinishWork(boolean[] checkedSubmachine) {
        boolean result = true;
        for(boolean bool: checkedSubmachine){
            result = bool && result;
        }
        return  result;
    }

    //click button for going back to the main activity
    public void goToMainActivity(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        //TextView textView = (TextView) findViewById(R.id.text_choosed_person);
        //intent.putExtra("person",textView.getText());
        startActivity(intent);
    }

    //click button for save data to db
    public void saveEventToDB(View view){
        //create new event
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(new Date());
        EditText textView = (EditText) findViewById(R.id.comments);
        adapter.open();
        Event event = new Event(currentTime, 0, adapter.getIdUser(person), 0, true, textView.getText().toString(), "not link");
        adapter.close();

        //button logic
        btnSaveToDbClk = true;
        Button btnSaveToDb = (Button) findViewById(R.id.button_insert_data_to_db);
        if(checkFinishWork(checkedSubmachine)){
            btnSaveToDb.setVisibility(View.INVISIBLE);
            Button buttonFinish = (Button) findViewById(R.id.button_finish);
            buttonFinish.setVisibility(View.VISIBLE);
        }
    }

    //custom adapter for listview (add red and green icons)
    class SubmachineAdapter extends ArrayAdapter<String> {

        public SubmachineAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.list_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.text_view_submachine_item);
            label.setText(data[position]);
            ImageView iconImageView = (ImageView) row.findViewById(R.id.image_view_icon);
            //
            if (checkedSubmachine[position] == true){
                iconImageView.setImageResource(R.drawable.icon_yes);
            } else {
                iconImageView.setImageResource(R.drawable.icon_no);
            }
            return row;
        }
    }
}
