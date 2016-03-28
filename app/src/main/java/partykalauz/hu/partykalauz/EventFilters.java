package partykalauz.hu.partykalauz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

/**
 * Created by Zsombor on 2016.03.27..
 */
public class EventFilters extends AppCompatActivity{
    Date selectedDate = new Date();
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


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        selectedDate.setTime(intent.getLongExtra("DATE", -1));
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
        parentIntent.putExtra("DATE", selectedDate);
        parentIntent.putExtra("DISTANCE", 400);
        parentIntent.putExtra("NAME", "KRAFT");
        startActivity(parentIntent);

    }
}
