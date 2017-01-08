package mobile.uom.gr.androidproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Class that gathers all data from user selections
 * and calls the API to show the returned flights in FlightsActivity.java
 */

public class SearchFlightsActivity extends AppCompatActivity {

    /*-----------------------------------------------------------------------------------------------------------------------*/
    /* Bellow variables are the TextViews that show the data before user decide to search for flights */

    private TextView location_tv; //textView that shows the city that user selected in OriginActivity.java
    private TextView destination_tv; //textView that shows the city that user selected in DestinationActivity.java
    private TextView adults_tv; //textView that shows the number of adults that the user selected in PassengerSelectionActivity
    private TextView children_tv; //textView that shows the number of children that the user selected in PassengerSelectionActivity
    private TextView infants_tv; //textView that shows the number of infants that the user selected in PassengerSelectionActivity
    private TextView tvdeparture_date; //date of leaving
    private TextView tvreturn_date; //returning date

    /*-----------------------------------------------------------------------------------------------------------------------*/
            /* Bellow variables are for api call when btn is pressed */

    //strings that holds the name of the airports in IATA code format
    private String origin_airport;
    private String destination_airport;

    //string that holds the seat type
    private String seat_type;

    //checkbox that holds if selection is direct flights or not
    private CheckBox checkBox;

    //these hold the date in ISO format for API call
    private String departure_date;
    private String return_date;

    /*-----------------------------------------------------------------------------------------------------------------------*/
            /* Bellow variables are only for checking if user selected correct date */

    //these hold the dates for checking if departure date is prior of return date
    private int dept_year;
    private int dept_month;
    private int dept_day;
    private int ret_year;
    private int ret_month;
    private int ret_day;

    /*-----------------------------------------------------------------------------------------------------------------------*/

    private Toast mToast; // toast that shows errors (we need this var because we need to cancel previous Toast messages)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);
        this.setTitle("Select your search criteria");

        //Give the selected value of spinner on seat_type string
        Spinner seat_types = (Spinner) findViewById(R.id.seats_spinner);
        seat_type = String.valueOf(seat_types.getSelectedItem());

        //Init
        location_tv = (TextView) findViewById(R.id.origin_text);
        destination_tv = (TextView) findViewById(R.id.destination_text);
        adults_tv = (TextView) findViewById(R.id.adults_text);
        children_tv = (TextView) findViewById(R.id.children_text);
        infants_tv = (TextView) findViewById(R.id.infants_text);

        tvdeparture_date = (TextView) findViewById(R.id.departure_date_tv);
        tvreturn_date = (TextView) findViewById(R.id.return_date_tv);

        Button select_dep_btn = (Button) findViewById(R.id.departure_btn);
        Button select_arr_btn = (Button) findViewById(R.id.return_btn);

        checkBox = (CheckBox) findViewById(R.id.checkbox_direct_flights);

        select_dep_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
        select_arr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(998);
            }
        });

    }

    //this must be imported for datepicker dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (id == 999) { //999 code for departure btn
            return new DatePickerDialog(this, departureDateListener, year, month, day);
        }
        else if (id == 998){ //998 code for return btn
            return new DatePickerDialog(this, returnDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener departureDateListener = new DatePickerDialog.OnDateSetListener() {
        //this is called when "Select departure date" btn is pressed and sets the departure TextView into the date
        //plus it sets the departure_date into ISO date format for API call
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String fmonth;
            String fday;

            if (month+1<10) {
                fmonth = "0" + String.valueOf(month+1);
            } else {
                fmonth = String.valueOf(month + 1);
            }
            if (day<10) {
                fday = "0" + day;
            } else {
                fday = String.valueOf(day);
            }

            tvdeparture_date.setText(new StringBuilder().append(fday).append(" - ")
                    .append(fmonth).append(" - ").append(year).append(" "));
            departure_date = fmonth + "-" + fday + "-" + year;
            dept_day = day;
            dept_month=month+1;
            dept_year=year;
        }
    };

    private DatePickerDialog.OnDateSetListener returnDateListener = new DatePickerDialog.OnDateSetListener() {
        //this is called when "Select return date" btn is pressed and sets the return TextView into the date
        //plus it sets the return_date into ISO date format for API call
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String fmonth;
            String fday;

            if (month+1<10) {
                fmonth = "0" + String.valueOf(month+1);
            } else {
                fmonth = String.valueOf(month + 1);
            }
            if (day<10) {
                fday = "0" + day;
            } else {
                fday = String.valueOf(day);
            }
            tvreturn_date.setText(new StringBuilder().append(fday).append(" - ")
                    .append(fmonth).append(" - ").append(year).append(" "));
            return_date = fmonth + "-" + fday + "-" + year;
            ret_day = day;
            ret_month=month+1;
            ret_year=year;
        }
    };

    public void setLocation(View view) {
        //this when 'Select your origin' btn is pressed
        Intent intent = new Intent(this, OriginActivity.class);
        intent.putExtra("ACTIVITY", "ORIGIN"); // this is for OriginActivity to know which btn open the Fragment
        startActivityForResult(intent, 1);
    }

    public void setDestination(View view) {
        //this when 'Select your destination' btn is pressed
        Intent intent = new Intent(this, DestinationActivity.class);
        intent.putExtra("ACTIVITY", "DESTINATION"); // this is for DestinationActivity to know which btn open the Fragment
        startActivityForResult(intent, 1);
    }

    public void showFlights(View view) {
        Context content = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        //toast = Toast.makeText(content, "", duration);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        //canceling previous Toast messages (if any)
        if(mToast != null){
            mToast.cancel();
        }

        //TODO: check if user has filled all data before clicking "Search flights"
        /* must have: origin and destinations airports, passengers, departure and arrival dates */

        //trying to find out if returning day is prior departure which is wrong or if user selected dates
        while (dept_year > ret_year || dept_month > ret_month || dept_day > ret_day || departure_date.equals("") || return_date.equals("")) {
            if (dept_year > ret_year)
                mToast = Toast.makeText(content, "Departure day must be prior return", duration);
            else if (dept_month > ret_month && dept_year == ret_year)
                mToast = Toast.makeText(content, "Departure day must be prior return", duration);
            else if (dept_day > ret_day && dept_month == ret_month && dept_year == ret_year)
                mToast = Toast.makeText(content, "Departure day must be prior return", duration);
            else if (departure_date.equals("") || return_date.equals(""))
                mToast = Toast.makeText(content, "You must select days", duration);
            else
                break;
            mToast.show();
            return;
        }

        //trying to find out if departure date is prior today which is wrong
        while (year > dept_year || month > dept_month || day > dept_day) {
            if (year > dept_year)
                mToast = Toast.makeText(content, "Wrong departure date!", duration);
            else if (month > dept_month && year == dept_year)
                mToast = Toast.makeText(content, "Wrong departure date!", duration);
            else if (day > dept_day && month == dept_month && year == dept_year)
                mToast = Toast.makeText(content, "Wrong departure date!", duration);
            else
                break;
            mToast.show();
            return;
        }

        //this when 'Search for flights' btn is pressed
        Intent intent = new Intent(this, FlightsActivity.class);
        // adding the extra data that the API call wants
        intent.putExtra("ORIGIN_AIRPORT", origin_airport);
        intent.putExtra("DESTINATION_AIRPORT", destination_airport);
        intent.putExtra("SEAT_TYPE", seat_type);
        intent.putExtra("DEPARTURE_DATE", departure_date);
        intent.putExtra("RETURN_DATE", return_date);
        intent.putExtra("ADULTS", adults_tv.getText().toString());
        intent.putExtra("CHILDREN", children_tv.getText().toString());
        intent.putExtra("INFANTS", infants_tv.getText().toString());

        //finally we find if user selected direct flights or no
        String isDirect;
        if(checkBox.isChecked()) {
            isDirect = "true";
        } else {
            isDirect = "false";
        }
        intent.putExtra("IS_DIRECT", isDirect);
        startActivity(intent);
    }

    public void showNumberPicker(View view) {
        //this when 'Set passengers' btn is pressed
        Intent intent = new Intent(this, PassengerSelectorActivity.class);
        intent.putExtra("ADULTS", adults_tv.getText());     //we must give the previous numbers from TextViews
        intent.putExtra("CHILDREN", children_tv.getText());
        intent.putExtra("INFANTS", infants_tv.getText());
        startActivityForResult(intent, 1);
    }

    @Override
    //This method is when we finish() the children method
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String check; // this String holds the code, to know which activity returned the results

        /*
         * check CODES:
         * SPINNER_SUCCESS: if user selected the correct amount of passengers and hit OK on PassengerSelectorActivity
         * SPINNER_CANCEL: if user selected cancel button on PassengerSelectorActivity
         * ORIGIN_AIRPORT: this is the "check code" if the activity which returned the results is OriginActivity
         * DESTINATION_AIRPORT: this is the "check code" if the activity which returned the results is DestinationActivity
         */

        if (requestCode == 1 && resultCode != RESULT_CANCELED) { // we requested code==1 so if it is, it is a success
            check = data.getStringExtra("CHECK");
            if (resultCode == 1) {
                switch (check) {
                    case "SPINNER_SUCCESS":
                        // setting the data at the correct TextViews
                        adults_tv.setText(data.getStringExtra("ADULTS"));
                        children_tv.setText(data.getStringExtra("CHILDREN"));
                        infants_tv.setText(data.getStringExtra("INFANTS"));
                        break;
                    case "SPINNER_CANCEL":
                        // setting the data at the correct TextViews
                        adults_tv.setText(data.getStringExtra("ADULTS"));
                        children_tv.setText(data.getStringExtra("CHILDREN"));
                        infants_tv.setText(data.getStringExtra("INFANTS"));
                        break;
                    case "ORIGIN_AIRPORT":
                        // setting the data at the correct TextView
                        location_tv.setText(data.getStringExtra("AIRPORT"));
                        // keeping the code for further use
                        origin_airport = data.getStringExtra("CODE");
                        break;
                    case "DESTINATION_AIRPORT":
                        // setting the data at the correct TextView
                        destination_tv.setText(data.getStringExtra("AIRPORT"));
                        // keeping the code for further use
                        destination_airport = data.getStringExtra("CODE");
                        break;
                }
            }
        }
    }
}
