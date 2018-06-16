package com.fenixbcn.calendarioipsc;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Funciones {

    /**
     * de la lista de eventos recuperada de google calendar filtra aquellos que coinciden con la fecha selecionada y los devuelve en otra lista
     * @param lCadenaEventos
     * @param selectedDate
     * @return
     */
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

                //Log.d(TAG, "las fechas son iguale: " + fechaInicial + "-" + selectedDate);
                lCadenaEventosSel.add(lCadenaEventos.get(i));

            } else if ((fechaInicial.getTime() < selectedDate.getTime()) && (fechaFinal.getTime() > selectedDate.getTime())) {

                //Log.d(TAG, "la fecha seleccionada " + selectedDate + " esta entre: " + fechaInicial + "-" + fechaFinal);
                lCadenaEventosSel.add(lCadenaEventos.get(i));
            } else if (fechaFinal.getTime() == selectedDate.getTime()) {

                //Log.d(TAG, "las fechas son iguale: " + fechaFinal + "-" + selectedDate);
                lCadenaEventosSel.add(lCadenaEventos.get(i));
            }

        }

        return lCadenaEventosSel;
    }

    static public String setDateTimeFormat (String dateTime) {

        Locale spanish = new Locale("es", "ES");
        SimpleDateFormat inputDateFormat = null;
        SimpleDateFormat outputDateFormat = null;
        String formattedDateTime = "";
        Date date = null;

        if (dateTime.length()==10) {
            inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            outputDateFormat = new SimpleDateFormat("dd MMM yyyy");
        } else {
            inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'.000+02:00'");
            outputDateFormat = new SimpleDateFormat("dd MMM yyyy k:mm ");
        }

        try {
            date = inputDateFormat.parse(dateTime);
            formattedDateTime = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDateTime;
    }

    static public List<String> orderEventsByDate (List<String> lCadenaEventos) {

        String TAG = "Calendario Ipsc";
        List<String> lCadenaEventosOrdered = new ArrayList<String>();
        List<NumDiasPosicion> lNumdias = new ArrayList<NumDiasPosicion>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicioOrden = null; // fecha inicio del orden
        Date fechaInicial = null; // fecha en la que empieza el evento

        int diasInicio = 0; // guardamos la diferencia de dias entre la fecha inicio del orden y la de inicio del evento

        // Obtenemos la fecha inicio del orden creada en un string
        Calendar calendar = Calendar.getInstance(); // necesitamos crear la instacia de Calendar para luego obtener el año
        int year = calendar.get(Calendar.YEAR);
        year -=1; // quiero ordenar desde un año antes a actual

        String sFirstDay = year + "-01-01";

        try {
            fechaInicioOrden = dateFormat.parse(sFirstDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // fin Obtenemos la fecha inicio del orden creada en un string


        /* recorrido por la lista de eventos para saber obtener la fecha de inicio del evento y restar
        con la fecha de inico del orden. Guardamos la diferencia de dias en una lista de interegers.
        El indice de la lista sera el indice para saber que evento es*/
        for (int i=0; i<lCadenaEventos.size();i++) {
            String eventoTirada = lCadenaEventos.get(i);
            String [] vEventoTirada = eventoTirada.split(" - ");

            try {
                if (vEventoTirada.length == 5) {
                    fechaInicial = dateFormat.parse(vEventoTirada[3]);

                } else if (vEventoTirada.length == 4) {
                    fechaInicial = dateFormat.parse(vEventoTirada[2]);

                } else if (vEventoTirada.length == 3) {
                    fechaInicial = dateFormat.parse(vEventoTirada[1]);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            diasInicio = (int) ((fechaInicial.getTime()-fechaInicioOrden.getTime())/86400000);

            lNumdias.add(new NumDiasPosicion(diasInicio,i));
        }

        /* ordena la lista de menor a major por el campo numdias de la clase NumDiasPosicion*/
        Collections.sort(lNumdias, new Comparator<NumDiasPosicion>() {
            @Override
            public int compare(NumDiasPosicion numDiasPosicion, NumDiasPosicion t1) {
                return numDiasPosicion.getNumDias()-t1.getNumDias();
            }
        });
        /* fin ordena la lista de menor a major por el campo numdias de la clase NumDiasPosicion */

        for (int j=0; j<lNumdias.size();j++) {

            lCadenaEventosOrdered.add(lCadenaEventos.get(lNumdias.get(j).getPosicionEvento()));

        }

        return lCadenaEventosOrdered;
    }

}
