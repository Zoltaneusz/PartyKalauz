package partykalauz.hu.partykalauz;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;

public class Event{

    public String title;
    public String owner;
    public String place;
    public String imageURL;
    public int attendees;
    public Date date;
    public String eventURL;
    public LatLng eventCoordinates;

    public Event(ParseObject eventObject) {
        this.title = (String) eventObject.get("event_name");
        this.owner = (String) eventObject.get("event_owner");
        this.place = (String) eventObject.get("event_location");
        this.imageURL = (String) eventObject.get("event_image");
        this.attendees = (int) eventObject.get("event_attending");
        this.date = (Date) eventObject.get("event_date");
        this.eventURL = "http://www.facebook.com/" + (String) eventObject.get("event_id");
        ParseGeoPoint coord = (ParseGeoPoint) eventObject.get("event_coordinates");
        if(coord != null)
        {
            if (coord.getLatitude()!=0 && coord.getLongitude()!=0) {
                this.eventCoordinates = new LatLng(coord.getLatitude(), coord.getLongitude());
            }
        }
    }

    public Event(LatLng latLongCoordinates){
        this.eventCoordinates = latLongCoordinates;
    }

    public static Comparator compareAttendees = new Comparator<Event>(){

        @Override
        public int compare(Event lhs, Event rhs) {

            return lhs.attendees > rhs.attendees ? -1:1;
        }
    };

    public static Comparator compareDates = new Comparator<Event>(){

        @Override
        public int compare(Event lhs, Event rhs) {

            return lhs.date.getTime() > rhs.date.getTime() ? -1:1;
        }
    };

    public enum EventComparator implements Comparator<Event> {
        DATE_SORT {
            public int compare(Event e1, Event e2) {
                e1.date.setHours(0);
                e1.date.setMinutes(0);
                e1.date.setSeconds(0);
                e2.date.setHours(0);
                e2.date.setMinutes(0);
                e2.date.setSeconds(0);
                e1.date.setTime(10000 * e1.date.getTime() / 10000);
                e2.date.setTime(10000*e2.date.getTime()/10000);
                return -1 * Long.valueOf(e1.date.getTime()).compareTo(e2.date.getTime());
            }},
        ATTENDEES_SORT {
            public int compare(Event e1, Event e2) {
                return Integer.valueOf(e1.attendees).compareTo(e2.attendees);
            }};

        public static Comparator<Event> decending(final Comparator<Event> other) {
            return new Comparator<Event>() {
                public int compare(Event e1, Event e2) {
                    return -1 * other.compare(e1, e2);
                }
            };
        }

        public static Comparator<Event> getComparator(final EventComparator... multipleOptions) {
            return new Comparator<Event>() {
                public int compare(Event e1, Event e2) {
                    for (EventComparator option : multipleOptions) {
                        int result = option.compare(e1, e2);
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                }
            };
        }
    }

}
