package com.fenixbcn.calendarioipsc;

import android.content.Intent;
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.fenixbcn.calendarioipsc.CalendarMainActivity.REQUEST_CLUB_LIST;

// es necesario añadir el Implements para poder contralar el cambio de estado del checkbox en la funcion correspondiente
public class ListaClubsActivity extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "Calendario Ipsc";

    List<String> nombresClubs = Arrays.asList("Barcelona", "Granollers", "Igualada", "Jordi Tarragó", "Lleida", "Mataró", "Montsià",
            "Osona", "Platja d'Aro", "R.T.A.A.", "Sabadell", "Terrassa", "Vilassar", "RFEDETO");

    private ListView lvListaClubs;
    private Button btnOkListaClubs;
    private Button btnCancelListaClubs;

    List<Club> lClubs; // listado de clubs para el adapter
    List<String> sClubs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clubs);

        lvListaClubs = (ListView) findViewById(R.id.lvListaClubs);

        lClubs = new ArrayList<>();
        ClubAdapter clubs;

        for (int i = 0; i<nombresClubs.size(); i++) {

            lClubs.add(new Club(nombresClubs.get(i)));

        }

        clubs = new ClubAdapter(this, lClubs);
        lvListaClubs.setAdapter(clubs);

        btnOkListaClubs = (Button) findViewById(R.id.btnOkListaClubs);

        btnOkListaClubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d(TAG, "los calendarios seleccionados son " +  TextUtils.join(",", sClubs));
                Intent intent = getIntent();
                intent.putStringArrayListExtra("lClubs", (ArrayList<String>) sClubs);
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

        if (pos != ListView.INVALID_POSITION) {
            Club club = lClubs.get(pos);
            club.setSelected(isChecked);

            //Toast.makeText(ListaClubsActivity.this,"Clicado en club: " + club.getNombreclub() + ", el estado es " + isChecked,Toast.LENGTH_SHORT).show();

            if (isChecked) {

                Log.d(TAG, "el nombre del club es: " + club.getNombreclub());

                sClubs.add(Funciones.getCalendarId(club.getNombreclub()));



            }
        }
    }


}
