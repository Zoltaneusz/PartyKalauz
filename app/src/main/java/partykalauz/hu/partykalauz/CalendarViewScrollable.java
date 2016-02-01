package partykalauz.hu.partykalauz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.CalendarView;



/**
 * Created by Zsombor on 2015.12.30..
 */
public class CalendarViewScrollable extends CalendarView {

    public CalendarViewScrollable(Context context){
        super(context);
    }

    public CalendarViewScrollable(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public CalendarViewScrollable(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if(ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_UP){
            ViewParent p = getParent();
            if(p != null)
                p.requestDisallowInterceptTouchEvent(true);

        }
        return false;
    }
}
