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
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zsombor on 2016.03.27..
 */
public class EventFilters extends AppCompatActivity{
    Date selectedDate = new Date();
    int seekDistance = 20;
    int setDistance;
    Context context = this;

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

    }



    @Override
    protected void onResume() {

        super.onResume();

        Intent intent = getIntent();
        selectedDate.setTime(intent.getLongExtra("DATE", new Date().getTime()));
        seekDistance = intent.getIntExtra("DISTANCE", 40);

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
        filteredDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent eventDateIntent = new Intent(context, CalendarActivity.class);
                eventDateIntent.putExtra("DISTANCE",seekDistance);
                startActivity(eventDateIntent);

            }
        });

        /**
         * Writing into the filteredPlace (EditText) shows the user a dropdown list, that contain all
         * places that start with the written letters.
         */
        final ListView listNames = (ListView) findViewById(R.id.filteredNameList);
        listNames.setVisibility(listNames.INVISIBLE);
        final EditText filteredPlace = (EditText) findViewById(R.id.filteredName);
        filteredPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listNames.setVisibility(listNames.VISIBLE);

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

        Intent parentIntent = new Intent(EventFilters.this, PartyKalauz.class);
        parentIntent.putExtra("DATE", selectedDate.getTime());
        parentIntent.putExtra("DISTANCE", seekDistance);
        parentIntent.putExtra("NAME", "KRAFT");
        startActivity(parentIntent);

    }
}
