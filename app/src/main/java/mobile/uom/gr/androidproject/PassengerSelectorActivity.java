package mobile.uom.gr.androidproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Class that show the number_picker.xml and allows user to select the number of passengers
 */

public class PassengerSelectorActivity extends AppCompatActivity {

    private NumberPicker npAdults;
    private NumberPicker npChildren;
    private NumberPicker npInfants;
    //these 3 strings are holding the previous numbers of passengers (if any)
    private String adults;
    private String children;
    private String infants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_picker);
        this.setTitle("Select the passengers");

        Intent intent = getIntent();                    //Creating a new intent to get the one that SearchFlightsActivity has passed
        adults = intent.getStringExtra("ADULTS");       //so we can read the previous picks (if any). This is a must, if we want
        children = intent.getStringExtra("CHILDREN");   //when "Cancel" is pressed, we return the previous numbers and not initialize
        infants = intent.getStringExtra("INFANTS");     //all

        //getting the NumberPickers in 'hand'
        npAdults = (NumberPicker) findViewById(R.id.number_picker_adults);
        npChildren = (NumberPicker) findViewById(R.id.number_picker_children);
        npInfants = (NumberPicker) findViewById(R.id.number_picker_infants);

        npAdults.setMaxValue(16);
        npAdults.setMinValue(1);
        npChildren.setMaxValue(16);
        npChildren.setMinValue(0);
        npInfants.setMaxValue(16);
        npInfants.setMinValue(0);

    }

    public void cancelBtn(View view) {
        Intent output = new Intent();
        output.putExtra("ADULTS", adults);      //In cancel, we return the previous picked numbers
        output.putExtra("CHILDREN", children);  //which we are having in the 3 Strings
        output.putExtra("INFANTS", infants);
        // TODO: must change the code number
        setResult(2,output);                    //the code when 'Cancel' is pressed
        finish();
    }

    public void setBtn(View view) {
        if (npInfants.getValue() > npAdults.getValue())
            //if infants are more than adults we show a toast message
            onError();
        else
            //else we exit this activity and we return the new numbers to parent activity
            onSuccess();
    }

    public void onSuccess() { //this method is when user picks all passengers right
        Intent output = new Intent();
        output.putExtra("ADULTS", String.valueOf(npAdults.getValue()));     //with these we return the new numbers that user picked
        output.putExtra("CHILDREN", String.valueOf(npChildren.getValue()));
        output.putExtra("INFANTS", String.valueOf(npInfants.getValue()));
        // TODO: must change the code number
        setResult(1,output); //the code when 'OK' is pressed
        finish();
    }

    public void onError() { //if user picks more infants we call this method
        Context content = getApplicationContext();
        CharSequence text = "Infants must not exceed adults";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(content, text, duration);
        toast.show();
    }

}
