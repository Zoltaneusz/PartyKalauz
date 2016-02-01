package partykalauz.hu.partykalauz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EventView extends AppCompatActivity {
    Context context = this;
    double lat;
    double lng;
    String place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        WebView eventWebView = (WebView) findViewById(R.id.eventview);
        eventWebView.getSettings().setJavaScriptEnabled(true);
        eventWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        eventWebView.setWebViewClient(new WebViewClient());
        Intent eventViewIntent = getIntent();
        lat = eventViewIntent.getDoubleExtra("LATITUDE", 47.504292);
        lng = eventViewIntent.getDoubleExtra("LONGITUDE", 19.058779);
        place = eventViewIntent.getStringExtra("PLACE");
        eventWebView.loadUrl(eventViewIntent.getStringExtra("EXTRA_MESSAGE"));
        actionBar.setTitle(place);

        FloatingActionButton fabEventMap = (FloatingActionButton) findViewById(R.id.fabEventMap);
        fabEventMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent eventMapIntent = new Intent(context, EventMap.class);
                eventMapIntent.putExtra("LATITUDE", lat);
                eventMapIntent.putExtra("LONGITUDE", lng);
                eventMapIntent.putExtra("PLACE", place);
                startActivity(eventMapIntent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
