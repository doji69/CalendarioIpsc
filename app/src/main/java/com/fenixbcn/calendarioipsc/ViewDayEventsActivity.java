package com.fenixbcn.calendarioipsc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewDayEventsActivity extends AppCompatActivity {

    private static final String TAG = "Calendario Ipsc";

    ListView lvDayEvents;
    TextView tvNoEvents;
    List<String> lCadenaEventos = new ArrayList<String>();
    List<String> lCadenaEventosSel = new ArrayList<String>();
    Date selectedDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_day_events);

        Bundle viewDayEventsActivityVars = getIntent().getExtras();
        lCadenaEventos = viewDayEventsActivityVars.getStringArrayList("lCadenaEventos");
        selectedDate.setTime(viewDayEventsActivityVars.getLong("selectedDate",-1));

        //Log.d(TAG, "numero de items: " + lCadenaEventos.size());
        //Log.d(TAG, "la fecha pasada: " + selectedDate);

        /* extraer de la lista de eventos los eventos en los que fecha seleccionada y es inicio, fin o
        se encuentra entre el inicio y el fin */

        lCadenaEventosSel = Funciones.getDateEvents(lCadenaEventos,selectedDate);

        //Log.d(TAG, "la lista de items seleccionados: " + lCadenaEventos);

        /* fin extraer de la lista de eventos los eventos en los que fecha seleccionada y es inicio, fin o
        se encuentra entre el inicio y el fin */

        if (lCadenaEventosSel.size()==0) {
            tvNoEvents = (TextView) findViewById(R.id.tvNoEvents);
            tvNoEvents.setText("No hay eventos para este dia");
        } else {
            tvNoEvents = (TextView) findViewById(R.id.tvNoEvents);
            tvNoEvents.setText("");
        }

        ArrayList<Object> alEventos = new ArrayList<>();
        EventoAdapter eventos;

        String  fechaInicial = null; // fecha en la que empieza el evento
        String fechaFinal = null; // fecha en la que termina el evento
        String titulo = "";

        for (int i = 0; i<lCadenaEventosSel.size(); i++) {

            String eventoTirada = lCadenaEventosSel.get(i);

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

        }

        eventos = new EventoAdapter(this, alEventos);

        lvDayEvents = (ListView) findViewById(R.id.lvDayEvents);

        TextView textView = new TextView(this);
        textView.setText(Funciones.setDateListViewHeaderFormat(selectedDate.toString()));
        textView.setTextSize(20);

        lvDayEvents.addHeaderView(textView);

        lvDayEvents.setAdapter(eventos);
    }
}
