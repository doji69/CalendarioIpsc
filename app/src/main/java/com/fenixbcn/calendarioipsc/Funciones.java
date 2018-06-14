package com.fenixbcn.calendarioipsc;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Funciones {


    static public List<String> getDateEvents (List<String> lCadenaEventos, Date selectedDate) {

        String TAG = "Calendario Ipsc";
        List<String> lCadenaEventosSel = new ArrayList<String>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicial = null; // fecha en la que empieza el evento
        Date fechaFinal = null; // fecha en la que termina el evento

        for (int i=0;i<lCadenaEventos.size();i++) {

            String eventoTirada = lCadenaEventos.get(i);
            String [] vEventoTirada = eventoTirada.split(" - ");

            try {
                if (vEventoTirada.length == 5) {
                    fechaInicial = dateFormat.parse(vEventoTirada[3]);
                    fechaFinal = dateFormat.parse(vEventoTirada[4]);
                } else if (vEventoTirada.length == 4) {
                    fechaInicial = dateFormat.parse(vEventoTirada[2]);
                    fechaFinal = dateFormat.parse(vEventoTirada[3]);
                } else if (vEventoTirada.length == 3) {
                    fechaInicial = dateFormat.parse(vEventoTirada[1]);
                    fechaFinal = dateFormat.parse(vEventoTirada[2]);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (fechaInicial.getTime() == selectedDate.getTime()) {

                Log.d(TAG, "las fechas son iguale: " + fechaInicial + "-" + selectedDate);
                lCadenaEventosSel.add(lCadenaEventos.get(i));

            } else if ((fechaInicial.getTime() < selectedDate.getTime()) && (fechaFinal.getTime() > selectedDate.getTime())) {

                Log.d(TAG, "la fecha seleccionada " + selectedDate + " esta entre: " + fechaInicial + "-" + fechaFinal);
                lCadenaEventosSel.add(lCadenaEventos.get(i));
            } else if (fechaFinal.getTime() == selectedDate.getTime()) {

                Log.d(TAG, "las fechas son iguale: " + fechaFinal + "-" + selectedDate);
                lCadenaEventosSel.add(lCadenaEventos.get(i));
            }

        }


        return lCadenaEventosSel;
    }
}