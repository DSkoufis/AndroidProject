package mobile.uom.gr.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Class that allows user to select a destination city
 */

public class DestinationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        setTitle(R.string.activity_destination);
    }
}
