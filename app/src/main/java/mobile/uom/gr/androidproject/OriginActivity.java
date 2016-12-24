package mobile.uom.gr.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Class that allows user to select the city that they start from
 */

public class OriginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_origin);
        setTitle(R.string.activity_origin);
    }
}
