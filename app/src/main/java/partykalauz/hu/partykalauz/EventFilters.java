package partykalauz.hu.partykalauz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Zsombor on 2016.03.27..
 */
public class EventFilters extends AppCompatActivity{
    Date selectedDate = new Date();
    int seekDistance = 20;
    int setDistance;
    String selectedName;
    Context context = this;
    int maxEvents = 1000;
    String[] listNameItems = new String[maxEvents];
    ListView listNames;
    EditText filteredPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_filter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(R.string.title_activity_calendar_view);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent parentIntent = new Intent(EventFilters.this, PartyKalauz.class);
                parentIntent.putExtra("DATE", selectedDate.getTime());
                parentIntent.putExtra("DISTANCE", seekDistance);
                parentIntent.putExtra("NAME", selectedName);
                startActivity(parentIntent);
            }
        });
        listNames = (ListView) findViewById(R.id.filteredNameList);
        listNames.setVisibility(listNames.INVISIBLE);
        filteredPlace = (EditText) findViewById(R.id.filteredName);
        listNames.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedName = (String) listNames.getItemAtPosition(position);
                filteredPlace.setText(selectedName);
                filteredPlace.setSelection(selectedName.length());
                listNames.setVisibility(listNames.INVISIBLE);
            }
        });

    }



    @Override
    protected void onResume() {

        super.onResume();

        Intent intent = getIntent();
        selectedDate.setTime(intent.getLongExtra("DATE", new Date().getTime()));
        seekDistance = intent.getIntExtra("DISTANCE", 40);
        listNameItems = intent.getStringArrayExtra("PLACES");
        selectedName = intent.getStringExtra("NAME");
        filteredPlace.setText(selectedName);


        // When coming back from CalendarView or PartyKalauz the previously filtered distance should stay ==========
        final EditText filteredEditDistance = (EditText) findViewById(R.id.filteredDistance);
        filteredEditDistance.setText(String.valueOf(seekDistance));
        final SeekBar filteredBarDistance = (SeekBar) findViewById(R.id.seekDistance);
        filteredBarDistance.setProgress(seekDistance);
        // =========================================================================================

        TextView filteredDate = (TextView) findViewById(R.id.filteredDate);
        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        filteredDate.setText(dateFormat.format(selectedDate));

        /**
         * When the user moves the seekDistance slide the value of filteredDistance (EditText) changes
         * accordingly.
         */

        filteredBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filteredEditDistance.setText(String.valueOf(progress));
                //seekDistance = seekBarDistance.getProgress();
                seekDistance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * When the user changes the value of filteredDistance (EditText) the seekDistance slide
         * changes accordingly.
         */

        filteredEditDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                int bar = Integer.valueOf(String.valueOf(s));
                if (bar < 1 || s == null)
                    bar = 1;

                filteredBarDistance.setProgress(bar);
                seekDistance = bar;

            }

            @Override
            public void afterTextChanged(Editable s) {
                filteredEditDistance.setSelection(String.valueOf(s).length());
            }
        });
        /**
         * Clicking the filteredDate (EditText) navigates the user to the CalendarActivity with
         * the selected distance as an extra.
         */
        filteredDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventDateIntent = new Intent(context, CalendarActivity.class);
                eventDateIntent.putExtra("DISTANCE", seekDistance);
                eventDateIntent.putExtra("NAME", selectedName);
                startActivity(eventDateIntent);

            }
        });

        /**
         * Writing into the filteredPlace (EditText) shows the user a dropdown list, that contain all
         * places that start with the written letters.
         */



        filteredPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                listNames.setVisibility(listNames.VISIBLE);
                //============= Filter and populate list of places =================================
                final List<String> listNamesArray = new ArrayList<String>();

                //=============== Array of all places coming from previous activity ================

                for(int i=0; i<listNameItems.length-1; i++)
                {
                    if(listNameItems[i] != null){
                        if (listNameItems[i].toLowerCase().contains(s))
                            listNamesArray.add(listNameItems[i]);
                    }
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        context, R.layout.placelist, R.id.list_places, listNamesArray);
                listNames.setAdapter(arrayAdapter);
                //==================================================================================

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });


    }

    /**
     * onNewIntent is used when the activity returns from CalendarActivity. Then we have a selected date in the intent.
     * @param intent
     */

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



    }
}
