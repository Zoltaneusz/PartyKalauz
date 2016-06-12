package partykalauz.hu.partykalauz;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * Created by Zoltán on 2016.05.30..
 */
public class FilterDistanceFrag extends Fragment {

    int seekDistance = 20;
    getDistanceFromFrag passData;

    public FilterDistanceFrag(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_filter_distance, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        //=============================== Get Data From Activity ===================================
        Bundle args = getArguments();
        if (args != null) {
            seekDistance = args.getInt("DISTANCE");
        }
        //==========================================================================================

        // When coming back from Activity or PartyKalauz the previously filtered distance should stay ==========
        final EditText filteredEditDistance = (EditText) getView().findViewById(R.id.filteredDistance);
        filteredEditDistance.setText(String.valueOf(seekDistance));
        final SeekBar filteredBarDistance = (SeekBar) getView().findViewById(R.id.seekDistance);
        filteredBarDistance.setProgress(seekDistance);
        // =========================================================================================
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
                passData.getDistanceFromFrag(seekDistance);
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
                passData.getDistanceFromFrag(seekDistance);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filteredEditDistance.setSelection(String.valueOf(s).length());
            }
        });
    }

    public interface getDistanceFromFrag {
        public void getDistanceFromFrag(int fragDistance);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            passData = (getDistanceFromFrag) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement getDistanceFromFrag");
        }
    }


}
