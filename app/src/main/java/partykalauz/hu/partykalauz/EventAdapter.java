package partykalauz.hu.partykalauz;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.*;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, Event[] events) {
        super(context, R.layout.event, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater eventInflater = LayoutInflater.from(getContext());
        View customView = eventInflater.inflate(R.layout.event, parent, false);

        Event event = (Event)getItem(position);
        String eventPlace = event.place;
        String eventImageURL;
        if(event.imageURL != null){
            eventImageURL = event.imageURL;
        }
        else { eventImageURL = "http://www-tc.pbs.org/onstage-in-america/lunchbox_plugins/s/photogallery/img/no-image-available.jpg"; }

        int eventAttendee = event.attendees;
        Date eventDate = event.date;
        String eventURL = event.eventURL;

        TextView eventPlaceView = (TextView) customView.findViewById(R.id.eventPlace);
        ImageView eventImageView = (ImageView) customView.findViewById(R.id.back_image);
        TextView eventAttendView = (TextView) customView.findViewById(R.id.eventAttend);
        TextView textAttendingView = (TextView) customView.findViewById(R.id.textAttending);
        TextView eventMonthView = (TextView) customView.findViewById(R.id.eventMonth);
        TextView eventDayView = (TextView) customView.findViewById(R.id.eventDay);

        eventPlaceView.setText(eventPlace);
        Picasso.with(getContext()).load(eventImageURL).fit().centerCrop().into(eventImageView);
        eventAttendView.setText(eventAttendee + "");
        textAttendingView.setText(R.string.event_attending);

        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");
        String monthString = monthFormatter.format(eventDate);
        eventMonthView.setText(monthString);

        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
        String dayString = dayFormatter.format(eventDate);
        eventDayView.setText(dayString);
        return customView;
    }
}
