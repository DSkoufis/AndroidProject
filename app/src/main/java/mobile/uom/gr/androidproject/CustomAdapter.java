package mobile.uom.gr.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 *  Custom Adapter that help us put the results from API call for low-fare flights
 *  and put them in the ListView in activity_flights.xml
 *
 *  code found in:
 *  http://androidexample.com/How_To_Create_A_Custom_Listview_-_Android_Example/index.php?view=article_discription&aid=67
 */

public class CustomAdapter extends BaseAdapter {

    // Declare variables
    private Activity activity;
    private static LayoutInflater inflater = null;
    public Resources res;
    String data;

    // Constructor
    public CustomAdapter(Activity a, Resources resLocal, String coming_data) {
        // init values
        activity = a;
        res = resLocal;
        data = coming_data;

        // Layout inflator to call external xml layout()
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
