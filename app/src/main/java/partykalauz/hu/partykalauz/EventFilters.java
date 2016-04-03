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
        TextView filteredDate = (TextView) findViewById(R.id.filteredDate);
        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        filteredDate.setText(dateFormat.format(selectedDate));

        SeekBar filteredBarDistance = (SeekBar) findViewById(R.id.seekDistance);
        filteredBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                EditText setEditDistance = (EditText) findViewById(R.id.filteredDistance);
                setEditDistance.setText(String.valueOf(progress));
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

        final EditText filteredEditDistance = (EditText) findViewById(R.id.filteredDistance);
        filteredEditDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){return;}
                SeekBar seekBarDistance = (SeekBar) findViewById(R.id.seekDistance);
                int bar = Integer.valueOf(String.valueOf(s));
                if(bar < 1 || s == null)
                    bar = 1;

                seekBarDistance.setProgress(bar);
                seekDistance = bar;

            }

            @Override
            public void afterTextChanged(Editable s) {
                filteredEditDistance.setSelection(String.valueOf(s).length());
            }
        });

        filteredDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent eventDateIntent = new Intent(context, CalendarActivity.class);
                startActivity(eventDateIntent);

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
