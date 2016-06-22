package partykalauz.hu.partykalauz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
public class EventFilters extends AppCompatActivity
        implements FilterDateFrag.getDateFromFrag, FilterDistanceFrag.getDistanceFromFrag, FilterPlaceFrag.getPlaceFromFrag{
    Date selectedDate = new Date();
    int seekDistance = 20;
    int setDistance;
    String selectedName;
    Context context = this;
    int maxEvents = 1000;
    String[] listNameItems = new String[maxEvents];
    ListView listNames;
    EditText filteredPlace;
    ArrayList<String> allPlaces = new ArrayList<String>();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.ic_my_location_white_24dp,
            R.mipmap.ic_date_range_white_24dp,
            R.mipmap.ic_place_white_24dp
    };

    @Override
    public void getDateFromFrag(long fragDate) {
        selectedDate.setTime(fragDate);
    }

    @Override
    public void getDistanceFromFrag(int fragDistance) {
        seekDistance = fragDistance;
    }

    @Override
    public void getPlaceFromFrag(String fragPlace) {
        selectedName = fragPlace;
    }

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
                // Get filter data from fragments


                Intent parentIntent = new Intent(EventFilters.this, PartyKalauz.class);
                parentIntent.putExtra("DATE", selectedDate.getTime());
                parentIntent.putExtra("DISTANCE", seekDistance);
                parentIntent.putExtra("NAME", selectedName);
                startActivity(parentIntent);
            }
        });




    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
      /*  tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
*/
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FilterDistanceFrag(), getString(R.string.layoutDistance), allPlaces, selectedName, selectedDate.getTime(), seekDistance);
        adapter.addFragment(new FilterDateFrag(), getString(R.string.layoutDate), allPlaces, selectedName, selectedDate.getTime(), seekDistance);
        adapter.addFragment(new FilterPlaceFrag(), getString(R.string.layoutPlace), allPlaces, selectedName, selectedDate.getTime(), seekDistance);
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, ArrayList<String> listNameItemsI, String filteredNameI, long filteredDateI, int filteredDistanceI ) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            final Bundle args = new Bundle();
            args.putStringArrayList("PLACES", listNameItemsI);
            args.putString("NAME", filteredNameI);
            args.putInt("DISTANCE", filteredDistanceI);

            fragment.setArguments(args);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        Intent intent = getIntent();
        selectedDate.setTime(intent.getLongExtra("DATE", new Date().getTime()));
        seekDistance = intent.getIntExtra("DISTANCE", 40);
        allPlaces = intent.getStringArrayListExtra("PLACES");
        listNameItems = allPlaces.toArray(listNameItems);
        selectedName = intent.getStringExtra("NAME");

        //==================== Creating tabs here =================================================

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setupTabIcons();

        //=========================================================================================




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
