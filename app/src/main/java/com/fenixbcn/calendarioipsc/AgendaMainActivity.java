package com.fenixbcn.calendarioipsc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AgendaMainActivity extends AppCompatActivity {

    List<String> lCadenaEventos = new ArrayList<String>();
    List<String> lCadenaEventosOrdered = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_main);

        Bundle viewDayEventsActivityVars = getIntent().getExtras();
        lCadenaEventos = viewDayEventsActivityVars.getStringArrayList("lCadenaEventos");

        lCadenaEventosOrdered = Funciones.orderEventsByDate(lCadenaEventos);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(TextUtils.join(",", lCadenaEventosOrdered));


    }
}
