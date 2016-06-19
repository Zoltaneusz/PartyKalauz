package partykalauz.hu.partykalauz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zoltán on 2016.05.30..
 */
public class FilterDateFrag extends Fragment {

    Date selectedDate = new Date();
    getDateFromFrag passData;
    CalendarView calendarView;

    public FilterDateFrag() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_filter_date, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        CalendarView calendarView = (CalendarView) getView().findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Date chosenDate = new Date(year - 1900, month, dayOfMonth);
                passData.getDateFromFrag(chosenDate.getTime());
            }
        });
        hideKeyboardFrom(getContext(), getView());
    }



    public interface getDateFromFrag {
        public void getDateFromFrag(long fragDate);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity eventFilterAct;

        if (context instanceof Activity) {
            eventFilterAct = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                passData = (getDateFromFrag) eventFilterAct;
            } catch (ClassCastException e) {
                throw new ClassCastException(eventFilterAct.toString()
                        + " must implement getDateFromFrag");
            }
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void onPause() {
        super.onPause();

    }
}
