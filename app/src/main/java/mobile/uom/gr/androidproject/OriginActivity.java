package mobile.uom.gr.androidproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Class that allows user to select the city that they start from
 */

public class OriginActivity extends AppCompatActivity implements AirportFinderFragment.OnDataPass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);
        setTitle(R.string.activity_origin);
    }

    @Override // we must implement this cause of the interface
    public void onDataPass(String airport) {
        // this method takes data from fragment
        // the airport variable contains the string of the list item that user clicked
        Intent output = new Intent();
        output.putExtra("AIRPORT", airport);

        // Not pretty way to take the airport code but it works!
        String airport_code = Character.toString(airport.charAt(0)) +
        Character.toString(airport.charAt(1)) +
        Character.toString(airport.charAt(2));
        output.putExtra("CODE", airport_code);

        // TODO: must change the code number
        setResult(3, output);
        finish();
    }
}

//          Intent intent = getActivity().getIntent();          // getting the intent which called this fragment is necessary
//          String code = intent.getStringExtra("ACTIVITY");    // because we must know if origin or destination called us
//
//          Intent output = new Intent();
//          output.putExtra("AIRPORT", adapter_airports.getItem(position)); //getting the item that clicked
//
//          String airport = adapter_airports.getItem(position).toString();
//          // Not pretty way to take the airport code but it works!
//        String airport_code = Character.toString(airport.charAt(0)) +
//        Character.toString(airport.charAt(1)) +
//        Character.toString(airport.charAt(2));
//        output.putExtra("CODE", airport_code);
//
//        // Logging
//        Log.i("Item position ", String.valueOf(position));
//        Log.i("Selected airport ", airport);
//        Log.i("Airport code ", airport_code);
//
//        if(code.equals("ORIGIN")) {
//        getActivity().setResult(3, output);
//        }
//        else {
//        getActivity().setResult(4,output);
//        }
//        getActivity().finish();
