package partykalauz.hu.partykalauz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import com.parse.ParseAnalytics;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;

/**



**/
public class CalendarActivity extends AppCompatActivity {

    Context context = this;
    Date initialDate;
    Date calendarDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CharSequence actionbarTitle;
        setContentView(R.layout.activity_calendar_view);
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
                CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
                Intent parentIntent = new Intent(CalendarActivity.this, EventFilters.class);
                parentIntent.putExtra("DATE", calendarView.getDate());
                startActivity(parentIntent);
            }
        });
        /*
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        initialDate = new Date(calendarView.getDate());
        initialDate.setHours(0);
        initialDate.setMinutes(0);
        initialDate.setSeconds(0);
        initialDate.setTime(initialDate.getTime() / 10000 * 10000);

        calendarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
                        calendarDate = new Date(calendarView.getDate());
                        calendarDate.setHours(0);
                        calendarDate.setMinutes(0);
                        calendarDate.setSeconds(0);
                        calendarDate.setTime(10000 * (calendarDate.getTime() / 10000));
                        initialDate = new Date();
                        initialDate.setHours(0);
                        initialDate.setMinutes(0);
                        initialDate.setSeconds(0);
                        initialDate.setTime(10000 * (initialDate.getTime() / 10000));
                        if(calendarDate.getTime() == initialDate.getTime())
                        {
                            Intent parentIntent = new Intent(CalendarActivity.this, PartyKalauz.class);
                            parentIntent.putExtra("DATE", calendarView.getDate()-84600000);
                            startActivity(parentIntent);
                        }

                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        break;
                }
                return false;
            }
        });




        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Date selectedDate = new Date(year-1900,month,dayOfMonth);
                if(initialDate.getTime() != selectedDate.getTime()) {
                    Intent parentIntent = new Intent(CalendarActivity.this, PartyKalauz.class);
                    parentIntent.putExtra("DATE", selectedDate.getTime());
                    startActivity(parentIntent);
                }
            }
        });*/

    }



 /*   @Override
    public void onResume(){
        super.onResume();
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(new Date().getTime()+86400010);
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        //================================= Analytics ============================================
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("platform", "Android");
        ParseAnalytics.trackEventInBackground("CalendarOpened", properties);
        //========================================================================================
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
