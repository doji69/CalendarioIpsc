package com.fenixbcn.calendarioipsc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

// es necesario añadir el Implements para poder contralar el cambio de estado del checkbox en la funcion correspondiente
public class ListaClubsActivity extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{

    List<String> nombresClubs = Arrays.asList("Barcelona", "Granollers", "Igualada", "Jordi Tarragó", "Lleida", "Mataró", "Montsià",
            "Osona", "Platja d'Aro", "R.T.A.A.", "Sabadell", "Terrassa", "Vilassar", "RFEDETO");

    private ListView lvListaClubs;
    List<Club> lClubs;

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
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int pos = lvListaClubs.getPositionForView(compoundButton);

        if (pos != ListView.INVALID_POSITION) {
            Club club = lClubs.get(pos);
            club.setSelected(isChecked);

            Toast.makeText(ListaClubsActivity.this,
                    "Clicado en club: " + club.getNombreclub() + ", el estado es " + isChecked,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
