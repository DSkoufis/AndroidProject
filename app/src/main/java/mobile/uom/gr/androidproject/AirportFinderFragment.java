package mobile.uom.gr.androidproject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that shows the airport search in
 * DestinationActivity.java and OriginActivity.java
 */

public class AirportFinderFragment extends Fragment {

    ArrayAdapter<String> adapter_airports;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<String> airports = new ArrayList<String>(); // The objects to represent in the ListView

        // creating the ArrayAdapter
        adapter_airports = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_airports_fragment, // The name of the layout ID
                R.id.list_item_airport, // The ID of the textview to populate
                airports);

        View view = inflater.inflate(R.layout.airports_fragment, container, false);

        // Getting the reference to the ListView of the fragment and setting ArrayAdapter
        ListView listView = (ListView) view.findViewById(R.id.listView_airports);
        listView.setAdapter(adapter_airports);

        // setting the code for when an item(airport) is pressed, it returned to the previous
        // activity which is SearchFlightsActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getActivity().getIntent();          // getting the intent which called this fragment is necessary
                String code = intent.getStringExtra("ACTIVITY");    // because we must know if origin or destination called us

                Intent output = new Intent();
                output.putExtra("AIRPORT", adapter_airports.getItem(position)); //getting the item that clicked

                //Todo: return the airport code and name from api response
                if(code.equals("ORIGIN")) {

                }else {

                }
            }
        });

        return view;
    }
}

//Todo: make the API call to find airport code and name
