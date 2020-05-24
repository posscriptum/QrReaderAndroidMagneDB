package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class SubMachineActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_machine);

        barcodeValue = (TextView)findViewById(R.id.barcode_value_sub);
        autoFocus = (CompoundButton) findViewById(R.id.auto_focus_sub);
        useFlash = (CompoundButton) findViewById(R.id.use_flash_sub);

        autoFocus.setChecked(true);

        TextView textViewPerson = (TextView) findViewById(R.id.text_choosed_person);
        TextView textViewLine = (TextView) findViewById(R.id.text_line);

        //fill textView from intent
        Intent intent = getIntent();
        String person = intent.getStringExtra("person");
        textViewPerson.setText(person);
        String line = intent.getStringExtra("line");
        textViewLine.setText(line);

        dataBaseHelper = new DataBaseHelper(this);
        Log.d(TAG, "created DataBaseHelper");

        //get data from DB
        adapter.open();
        //Log.d(TAG, "test1");
        data = new String[adapter.getSubmachine(line).size()];
        checkedSubmachine = new boolean[adapter.getSubmachine(line).size()];
        adapter.getSubmachine(line).toArray(data);

        //insert to listView
        ListView sumachinesList = (ListView) findViewById(R.id.list_submachines);

        //sumachinesList.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, R.id.text_view_submachine_item, data));
        sumachinesList.setAdapter(new SubmachineAdapter(this, R.layout.list_item, data));

        adapter.close();

        //click on list
        sumachinesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(view.getContext(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());
                intent.putExtra("positionClick", position);
                checkedSubmachine[position] = true;
                intent.putExtra("checkedSubmachine", checkedSubmachine);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
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

                    //insert to listView
                    ListView sumachinesList = (ListView) findViewById(R.id.list_submachines);
                    sumachinesList.setAdapter(new SubmachineAdapter(this, R.layout.list_item, this.data));

                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

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
