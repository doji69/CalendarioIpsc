package com.fenixbcn.calendarioipsc;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CalendarMainActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "Calendario Ipsc";
    GoogleAccountCredential mCredential;
    private TextView tvOutputText;
    private CaldroidFragment calendario;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity_main);

        tvOutputText = (TextView) findViewById(R.id.tvOutputText);

        calendario = new CaldroidFragment(); // crea la instancia del calendario tipo Caldroid

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            calendario.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        } else { // If activity is created from fresh
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY); // Monday

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            //args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            calendario.setArguments(args);
        }

        calendario.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                Toast.makeText(CalendarMainActivity.this, date.toString(), Toast.LENGTH_LONG).show();
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi(); // llama a la funcion que gestiona la conexion y recepcion a google calendar
    }

    /**
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (calendario != null) {
            calendario.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (calendario != null) {
            calendario.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    /**
     *
     * @param result
     * Funcion llamada desde onPostExecute y que pinta los dias que hay un evento.
     * Esta funcion se ejecuta antes de asignar el calendario al layout
     */
    private void setCustomResourceForDates(List<String > result) {

        // hay que recuperar las fechas de inicio y fin de todos los eventos y marcar las fechas que estan en la lista.
        // si el evento dura un solo dia se marca el día, pero si dura más de un día se marca principio y final

        //Log.d(TAG, "el valor de result es: " + result.get(0));
        //Log.d(TAG, "el numero de valores es: " + result.size());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = null; // fecha del sistema
        Date fechaInicial = null; // fecha en la que empieza el evento
        Date fechaFinal = null; // fecha en la que termina el evento

        int diasInicio = 0; // guardamos la diferencia de dias entre la fecha actual y la de inicio del evento para saber que dia hay que colorear
        int diasFin = 0; // guardamos la diferencia de dias entre la fecha actual y la de fin del evento para saber que dia hay que colorear

        try {
            fechaActual = dateFormat.parse(dateFormat.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i=0;i<result.size();i++) {

            String eventoTirada = result.get(i);
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

            //Log.d(TAG, "la fecha actual es : " + fechaActual);
            //Log.d(TAG, "la fecha inicio evento es : " + fechaInicial);
            //Log.d(TAG, "la fecha fin evento es : " + fechaFinal);

            diasInicio = (int) ((fechaInicial.getTime()-fechaActual.getTime())/86400000);
            diasFin = (int) ((fechaFinal.getTime()-fechaActual.getTime())/86400000);

            //Log.d(TAG, "la diferencia de dias al inicio es : " + diasInicio);
            //Log.d(TAG, "la diferencia de dias al final es : " + diasFin);

            // pintamos todos los dias dedes la fecha de inicio a la fecha de fin del evento
            int difDias = diasFin-diasInicio;
            //Log.d(TAG, "la diferencia de dias es : " + difDias);
            for (int j=0; j<=difDias;j++) {
                Calendar cal = Calendar.getInstance(); // hay que llamar al getInstance para cada fecha que se quiere colorear
                cal.add(Calendar.DATE, diasInicio+j);
                Date colorFechaPintar = cal.getTime();
                if (calendario != null) {
                    ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
                    calendario.setBackgroundDrawableForDate(blue, colorFechaPintar);
                    calendario.setTextColorForDate(R.color.white, colorFechaPintar);

                }
            }
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            tvOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    tvOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                CalendarMainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Calendario Ipsc")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 100 events from the primary calendar.

            List<String> lCalendars = new ArrayList<String>();


            //lCalendars.add("ro24qiumugq2mdqfsulhci6ctk@group.calendar.google.com");
            //lCalendars.add("f0bfcbcgif270cj7ts3tqq5ic0@group.calendar.google.com");

            //lCalendars.add("ngjagh7og8ij1qicffe17ubtcc@group.calendar.google.com"); // desconocido
            //lCalendars.add("dne67tuddd0jrn2182igm783sc@group.calendar.google.com"); // Granollers
            //lCalendars.add("ilv3lk9c0fqnnodmi1t71ocv3g@group.calendar.google.com"); // Granollers privado
            //lCalendars.add("kak1rooupa6ru9kt6vki5gmrs0@group.calendar.google.com"); // desconocido
            //lCalendars.add("k0pmhg0b0i8l574n34c9im5r1s@group.calendar.google.com"); // Lleida
            //lCalendars.add("e20gtq6h142m0vt4olgpmagvj0@group.calendar.google.com"); // Mataro
            //lCalendars.add("nicucek3ijoo2dk21ucsthb2m0@group.calendar.google.com"); // Montsia
            //lCalendars.add("epmhu8fts0ai17ot9fvmlr2r80@group.calendar.google.com"); // Osona
            //lCalendars.add("3pplpsjb0rte6upt0ecielvrec@group.calendar.google.com"); // Platja d'Aro
            //lCalendars.add("pngv5u4uit1opct9r4c7d74ofs@group.calendar.google.com"); // Sabadell
            //lCalendars.add("2t466nuslhmr90u7odfc5bn1is@group.calendar.google.com"); // Terrassa
            //lCalendars.add("9slr1m12oodn74flqppp9nllng@group.calendar.google.com"); // Error 404 not found.
            //lCalendars.add("uo34u3j4mqd00e3h5g1kcm1928@group.calendar.google.com"); // Vilassar
            //lCalendars.add("j36gq85ai9q4bp6325le90eig0@group.calendar.google.com"); // Agustina de Aragón Zaragoza
            lCalendars.add("ert4hkolipo06154v6p7k0c7co@group.calendar.google.com"); // Federacion Tiro

            // Obtenemos la fecha creada en un string en formato google DateTime de inicio para la recuperacion de eventos
            Calendar calendar = Calendar.getInstance(); // necesitamos crear la instacia de Calendar para luego obtener el año
            int year = calendar.get(Calendar.YEAR);
            year -=1; // quiero capturar de google calendar los eventos desde el inicio del año anterior al actual

            String sFirstDay = year + "-01-01";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTime fechaInicial = null;

            try {
                 fechaInicial = new DateTime(dateFormat.parse(sFirstDay));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // fin Obtenemos la fecha creada en un string en formato google DateTime de inicio para la recuperacion de eventos

            DateTime now = new DateTime(System.currentTimeMillis()); // obtiene la fecha actual
            //Log.d(TAG, "now : " + now);
            //Log.d(TAG, "fechaInicial : " + fechaInicial);

            List<String> eventStrings = new ArrayList<String>();

            /* multiples calendarios */
            for (int j=0; j<lCalendars.size();j++) {

                Events events = mService.events().list(lCalendars.get(j))
                        .setMaxResults(100)
                        .setTimeMin(fechaInicial)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                List<Event> items = events.getItems();

                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    DateTime end = event.getEnd().getDateTime();
                    if (start == null) {
                        // All-day events don't have start times, so just use
                        // the start date.
                        start = event.getStart().getDate();
                        end = event.getEnd().getDate();
                    }
                    eventStrings.add(
                            String.format("%s - %s - %s", event.getSummary(), start, end));
                }
            }

            /* fin multiples calendarios */

            /* solo un calendario */
            /*

            Events events = mService.events().list("ro24qiumugq2mdqfsulhci6ctk@group.calendar.google.com")
                    .setMaxResults(100)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                    end = event.getEnd().getDate();
                }
                eventStrings.add(
                        String.format("%s - %s - %s", event.getSummary(), start, end));
            }
            */
            /* fin solo un calendario */

            return eventStrings;
        }

        @Override
        protected void onPreExecute() {
            tvOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                tvOutputText.setText("No results returned.");
            } else {

                setCustomResourceForDates(output); // colorea el calendario. Esta funcion tiene que estar por encima de la asynTask
                //output.add(0, "Data retrieved using the Google Calendar API:");
                tvOutputText.setText(TextUtils.join(",", output));
                // Attach to the activity. asigna el calendario a layout deseado
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.lyCalendario, calendario);
                t.commit();

            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CalendarMainActivity.REQUEST_AUTHORIZATION);
                } else {
                    tvOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                tvOutputText.setText("Request cancelled.");
            }
        }
    }


}