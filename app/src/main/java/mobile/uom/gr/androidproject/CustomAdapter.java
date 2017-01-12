package mobile.uom.gr.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
    ArrayList<FlightModel> data;
    FlightModel tempData;

    // Constructor
    public CustomAdapter(Activity a, Resources resLocal, ArrayList coming_data) {
        // init values
        activity = a;
        res = resLocal;
        data = coming_data;

        // Layout inflator to call external xml layout()
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data.size() <= 0) {
            return -1;
        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Create a holder class to contain inflated xml file elements
    public static class ViewHolder {
        public TextView outbound_flight;
        public TextView outbound_flight_time;
        public TextView inbound_flight;
        public TextView inbound_flight_time;
        public TextView flight_price;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView == null) {
            // inflate list_item_flights.xml file for each row
            vi = inflater.inflate(R.layout.list_item_flights, null);

            // get references at list items with ViewHolder class item
            holder = new ViewHolder();
            holder.outbound_flight = (TextView) vi.findViewById(R.id.outbound_flight);
            holder.outbound_flight_time = (TextView) vi.findViewById(R.id.outbound_flight_time);
            holder.inbound_flight = (TextView) vi.findViewById(R.id.inbound_flight);
            holder.inbound_flight_time = (TextView) vi.findViewById(R.id.inbound_flight_time);
            holder.flight_price = (TextView) vi.findViewById(R.id.flight_price);

            // set holder with LayoutInflater
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // get each model from ArrayList
        tempData = null;
        tempData = (FlightModel) data.get(position);

        // set the data into Holder
        holder.outbound_flight.setText(tempData.getOutbound_flight());
        holder.outbound_flight_time.setText(tempData.getOutbound_flight_time());
        holder.inbound_flight.setText(tempData.getInbound_flight());
        holder.inbound_flight_time.setText(tempData.getInbound_flight_time());
        holder.flight_price.setText(tempData.getTotal_price());

        //TODO: add here a clickListener if I have time

        return vi;
    }
}
