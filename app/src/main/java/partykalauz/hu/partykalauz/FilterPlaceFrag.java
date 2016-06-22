package partykalauz.hu.partykalauz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zoltán on 2016.05.30..
 */
public class FilterPlaceFrag extends Fragment {

    int maxEvents = 1000;
    String[] listNameItems = new String[maxEvents];
    String selectedName;
    ListView listNames;
    EditText filteredPlace;
    getPlaceFromFrag passData;
    ArrayList<String> allPlaces = new ArrayList<String>();

    public FilterPlaceFrag(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          return inflater.inflate(R.layout.frag_filter_place, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        listNames = (ListView) getView().findViewById(R.id.filteredNameList);
        listNames.setVisibility(listNames.INVISIBLE);
        filteredPlace = (EditText) getView().findViewById(R.id.filteredName);

        listNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedName = (String) listNames.getItemAtPosition(position);
                filteredPlace.setText(selectedName);
                filteredPlace.setSelection(selectedName.length());
                listNames.setVisibility(listNames.INVISIBLE);
                passData.getPlaceFromFrag(selectedName);
            }
        });

        //=============================== Get Data From Activity ===================================

        Bundle args = getArguments();
        if(args != null)
        {
            allPlaces = args.getStringArrayList("PLACES");
            listNameItems = allPlaces.toArray(listNameItems);
            selectedName = args.getString("NAME");
        }

        filteredPlace.setText(selectedName);
        //==========================================================================================

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

                for (int i = 0; i < listNameItems.length - 1; i++) {
                    if (listNameItems[i] != null) {
                        if (listNameItems[i].toLowerCase().contains(s))
                            listNamesArray.add(listNameItems[i]);
                    }
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.placelist, R.id.list_places, listNamesArray);
                listNames.setAdapter(arrayAdapter);
                //==================================================================================

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public interface getPlaceFromFrag {
        public void getPlaceFromFrag(String fragPlace);
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
                passData = (getPlaceFromFrag) eventFilterAct;
            } catch (ClassCastException e) {
                throw new ClassCastException(eventFilterAct.toString()
                        + " must implement getPlaceFromFrag");
            }
        }
    }

}
