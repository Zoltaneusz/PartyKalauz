package partykalauz.hu.partykalauz;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Zsombor on 2015.12.29..
 */
public class MainApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
