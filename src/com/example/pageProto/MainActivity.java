package com.example.pageProto;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private BookView bookView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.bookView = (BookView) this.findViewById(R.id.bookView);
//        this.bookView.setBackgroundColor(0xFF202830);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.bookView.onPause();
        Log.d("glDemo",">>>activity on pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bookView.onResume();
        Log.d("glDemo",">>>activity on resume");
    }
}
