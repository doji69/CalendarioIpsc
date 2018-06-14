package com.fenixbcn.calendarioipsc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewDayEventsActivity extends AppCompatActivity {

    private static final String TAG = "Calendario Ipsc";

    List<String> lCadenaEventos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_day_events);

        Bundle viewDayEventsActivityVars = getIntent().getExtras();
        lCadenaEventos = viewDayEventsActivityVars.getStringArrayList("lCadenaEventos");

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(TextUtils.join(",", lCadenaEventos));

        Log.d(TAG, "numero de items: " + lCadenaEventos.size());

    }
}
