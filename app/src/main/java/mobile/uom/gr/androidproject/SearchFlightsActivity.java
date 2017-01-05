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

    private static TextView location_tv; //textView that shows the city that user selected in OriginActivity.java
    private static TextView destination_tv; //textView that shows the city that user selected in DestinationActivity.java
    private static TextView adults_tv; //textView that shows the number of adults that the user selected in PassengerSelectionActivity
    private static TextView children_tv; //textView that shows the number of children that the user selected in PassengerSelectionActivity
    private static TextView infants_tv; //textView that shows the number of infants that the user selected in PassengerSelectionActivity
    private static TextView tvdeparture_date; //date of leaving
    private static TextView tvreturn_date; //returning date

    /*-----------------------------------------------------------------------------------------------------------------------*/
            /* Bellow variables are for api call when btn is pressed */

    //strings that holds the name of the airports in IATA code format
    private String origin_airport;
    private String destination_airport;

    //string that holds the seat type
    private String seat_type;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);
        this.setTitle("Select your search criteria");

        //Give the selected value of spinner on seat_type string
        Spinner seat_types = (Spinner) findViewById(R.id.seats_spinner);
        seat_type = String.valueOf(seat_types.getSelectedItem());

        location_tv = (TextView) findViewById(R.id.origin_text);
        destination_tv = (TextView) findViewById(R.id.destination_text);
        adults_tv = (TextView) findViewById(R.id.adults_text);
        children_tv = (TextView) findViewById(R.id.children_text);
        infants_tv = (TextView) findViewById(R.id.infants_text);

        tvdeparture_date = (TextView) findViewById(R.id.departure_date_tv);
        tvreturn_date = (TextView) findViewById(R.id.return_date_tv);

        Button select_dep_btn = (Button) findViewById(R.id.departure_btn);
        Button select_arr_btn = (Button) findViewById(R.id.return_btn);

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
        Toast toast;

        //toast = Toast.makeText(content, "", duration);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        //trying to find out if departure date is prior today which is wrong
        while (year > dept_year || month > dept_month || day > dept_day) {
            if (year > dept_year)
                toast = Toast.makeText(content, "Wrong departure date!", duration);
            else if (month > dept_month && year == dept_year)
                toast = Toast.makeText(content, "Wrong departure date!", duration);
            else if (day > dept_day && month == dept_month && year == dept_year)
                toast = Toast.makeText(content, "Wrong departure date!", duration);
            else
                break;
            toast.show();
            return;
        }
        //trying to find out if returning day is prior departure which is wrong
        while (dept_year > ret_year || dept_month > ret_month || dept_day > ret_day) {
            if (dept_year > ret_year)
                toast = Toast.makeText(content, "Departure day must be prior return", duration);
            else if (dept_month > ret_month && dept_year == ret_year)
                toast = Toast.makeText(content, "Departure day must be prior return", duration);
            else if (dept_day > ret_day && dept_month == ret_month && dept_year == ret_year)
                toast = Toast.makeText(content, "Departure day must be prior return", duration);
            else
                break;
            toast.show();
            return;
        }
        //this when 'Search for flights' btn is pressed
        //Todo: fill the putExtra for API call
        Intent intent = new Intent(this, FlightsActivity.class);
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
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { //code==1 when we change data in PassengerSelectorActivity
            adults_tv.setText(data.getStringExtra("ADULTS"));
            children_tv.setText(data.getStringExtra("CHILDREN"));
            infants_tv.setText(data.getStringExtra("INFANTS"));
        } else if (requestCode == 2) { //code==2 when we cancel in PassengerSelectorActivity
            adults_tv.setText(data.getStringExtra("ADULTS"));
            children_tv.setText(data.getStringExtra("CHILDREN"));
            infants_tv.setText(data.getStringExtra("INFANTS"));
        } else if (requestCode == 3) { //code==3 when we select airport from OriginActivity
            Log.i("Origin returned data ", data.getStringExtra("AIRPORT") + " - " + data.getStringExtra("CODE"));
            location_tv.setText(data.getStringExtra("AIRPORT"));
            origin_airport = data.getStringExtra("CODE");
        } else if (requestCode == 4) { //code==4 when we select airport from DestinationActivity
            Log.i("Dest returned data ", data.getStringExtra("AIRPORT") + " - " + data.getStringExtra("CODE"));
            destination_tv.setText(data.getStringExtra("AIRPORT"));
            destination_airport = data.getStringExtra("CODE");
        }
        // TODO: Change the codes to return all 1, but adding one more element to distinguish

    }
}
