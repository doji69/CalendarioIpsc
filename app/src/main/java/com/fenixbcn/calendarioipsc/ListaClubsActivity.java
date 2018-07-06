package com.fenixbcn.calendarioipsc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.fenixbcn.calendarioipsc.CalendarMainActivity.REQUEST_CLUB_LIST;

// es necesario añadir el Implements para poder contralar el cambio de estado del checkbox en la funcion correspondiente
public class ListaClubsActivity extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "Calendario Ipsc";

    private ListView lvListaClubs;
    private Button btnOkListaClubs;
    private Button btnCancelListaClubs;

    List<Club> lClubs; // listado de clubs para el adapter
    List<String> lIdCalendarsClubsChecked = new ArrayList<String>(); // lista de idCalendars de los clubs chequeados
    List<String> lIdCalendarsClubsUnChecked = new ArrayList<String>(); // lista de idCalendars de los clubs no chequeados


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clubs);

        Bundle listaClubsActivityVars = getIntent().getExtras();
        lIdCalendarsClubsChecked = listaClubsActivityVars.getStringArrayList("lIdCalendarsClubsChecked");
        lIdCalendarsClubsUnChecked = listaClubsActivityVars.getStringArrayList("lIdCalendarsClubsUnChecked");

        Log.d(TAG, "la lista de idCalendar es " + lIdCalendarsClubsChecked);

        lvListaClubs = (ListView) findViewById(R.id.lvListaClubs);

        lClubs = new ArrayList<>();
        ClubAdapter clubs;

        for (int i = 0; i<lIdCalendarsClubsChecked.size(); i++) {

            lClubs.add(new Club(Funciones.getNombreClubByCalendarId(lIdCalendarsClubsChecked.get(i)), true));

        }

        for (int i = 0; i<lIdCalendarsClubsUnChecked.size(); i++) {

            lClubs.add(new Club(Funciones.getNombreClubByCalendarId(lIdCalendarsClubsUnChecked.get(i)),false));

        }

        clubs = new ClubAdapter(this, lClubs);
        lvListaClubs.setAdapter(clubs);

        btnOkListaClubs = (Button) findViewById(R.id.btnOkListaClubs);

        btnOkListaClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d(TAG, "los calendarios seleccionados son " +  TextUtils.join(",", sClubs));
                Intent intent = getIntent();
                intent.putStringArrayListExtra("lIdCalendarsClubsChecked", (ArrayList<String>) lIdCalendarsClubsChecked);
                intent.putStringArrayListExtra("lIdCalendarsClubsUnChecked", (ArrayList<String>) lIdCalendarsClubsUnChecked);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btnCancelListaClubs = (Button) findViewById(R.id.btnCancelListaClubs);

        btnCancelListaClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        int pos = lvListaClubs.getPositionForView(compoundButton);
        String idCalendario = "";

        if (pos != ListView.INVALID_POSITION) {
            Club club = lClubs.get(pos);
            club.setSelected(isChecked);

            if (isChecked) {

                // añadimos el club clicado al listado de clubs clicados
                Log.d(TAG, "el club clicado es " + club.getNombreclub() + ". El estado es " + isChecked);
                lIdCalendarsClubsChecked.add(Funciones.getCalendarId(club.getNombreclub()));

                // buscamos el club clicado y lo borramos de la lista de clubs no clicados
                idCalendario = Funciones.getCalendarId(club.getNombreclub());

                Log.d(TAG, "el calendario a eliminar de no clicados es " + club.getNombreclub() + ". El id es " + idCalendario);
                int posIdCalendario = lIdCalendarsClubsUnChecked.indexOf(idCalendario);
                Log.d(TAG, "esta en la posicion " + posIdCalendario);

                lIdCalendarsClubsUnChecked.remove(posIdCalendario);

            } else {

                // añadimos el club desclicado al listado de clubs no clicados
                Log.d(TAG, "el club desclicado es " + club.getNombreclub() + ". El estado es " + isChecked);
                lIdCalendarsClubsUnChecked.add(Funciones.getCalendarId(club.getNombreclub()));
                Log.d(TAG, "el idCalendario del club desclicado es " + Funciones.getCalendarId(club.getNombreclub()));

                // buscamos el club desclicado y lo borramos de la lista de clubs clicados
                idCalendario = Funciones.getCalendarId(club.getNombreclub());

                Log.d(TAG, "el calendario a eliminar de clicados es " + club.getNombreclub() + ". El id es " + idCalendario);
                int posIdCalendario = lIdCalendarsClubsChecked.indexOf(idCalendario);
                Log.d(TAG, "esta en la posicion  " + posIdCalendario);

                lIdCalendarsClubsChecked.remove(posIdCalendario);

            }
        }
    }
}
