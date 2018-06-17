package com.fenixbcn.calendarioipsc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AgendaMainActivity extends AppCompatActivity {

    List<String> lCadenaEventos = new ArrayList<String>();
    List<String> lCadenaEventosOrdered = new ArrayList<String>();
    ListView lvAllEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_main);

        Bundle viewDayEventsActivityVars = getIntent().getExtras();
        lCadenaEventos = viewDayEventsActivityVars.getStringArrayList("lCadenaEventos");

        lCadenaEventosOrdered = Funciones.orderEventsByDate(lCadenaEventos);
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(TextUtils.join(",", lCadenaEventosOrdered));

        ArrayList<Evento> alEventos = new ArrayList<>();
        EventoAdapter eventos = null;

        String  fechaInicial = null; // fecha en la que empieza el evento
        String fechaFinal = null; // fecha en la que termina el evento
        String titulo = "";

        for (int i = 0; i<lCadenaEventosOrdered.size(); i++) {

            String eventoTirada = lCadenaEventosOrdered.get(i);

            //Log.d(TAG, "el items actual: " + eventoTirada);
            String [] vEventoTirada = eventoTirada.split(" - ");

            if (vEventoTirada.length == 5) {
                fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[3]);
                fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[4]);
                titulo = vEventoTirada[0] + "\n" + vEventoTirada[1] + "\n" +vEventoTirada[2];
            } else if (vEventoTirada.length == 4) {
                fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[2]);
                fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[3]);
                titulo = vEventoTirada[0] + "\n" + vEventoTirada[1];
            } else if (vEventoTirada.length == 3) {
                fechaInicial = Funciones.setDateTimeFormat(vEventoTirada[1]);
                fechaFinal = Funciones.setDateTimeFormat(vEventoTirada[2]);
                titulo = vEventoTirada[0];
            }

            alEventos.add (new Evento(titulo, fechaInicial, fechaFinal));

            eventos = new EventoAdapter(this, alEventos);
        }

        lvAllEvents = (ListView) findViewById(R.id.lvAllEvents);

        TextView textView = new TextView(this);
        textView.setText("Hello. I'm a header view");

        lvAllEvents.addHeaderView(textView);

        lvAllEvents.setAdapter(eventos);
    }
}
