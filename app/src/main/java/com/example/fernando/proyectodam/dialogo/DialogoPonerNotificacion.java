package com.example.fernando.proyectodam.dialogo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.fernando.proyectodam.R;
import com.example.fernando.proyectodam.pojo.Lista;
import com.example.fernando.proyectodam.pojo.Nota;

/**
 * Created by Fernando on 21/10/2016.
 */

public class DialogoPonerNotificacion extends AppCompatDialogFragment {

    private Object elemento;
    private Spinner fecha;
    private Spinner hora;

    //Adaptadores
    private ArrayAdapter<CharSequence> adapterFecha;
    private ArrayAdapter<CharSequence> adapterHora;

    //Fecha
    private Calendar calendar;

    public static DialogoPonerNotificacion newInstance(Object elemento) {

        DialogoPonerNotificacion fragment = new DialogoPonerNotificacion();
        Bundle args = new Bundle();
        args.putParcelable("elemento", (Parcelable) elemento);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getArguments() != null ) {

            this.elemento = getArguments().get("elemento");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        calendar                    = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_spinner, null);

        builder.setView(v);

        builder.setTitle("Añadir recordatorio");
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Si la fecha es superior a la actual
                        if ( calendar.after(Calendar.getInstance()) ) {

                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                            String date = sdf.format(calendar.getTime());

                            if ( elemento instanceof Nota ) {

                                ((Nota)elemento).setFecha_not(date);
                            }
                            else
                            {
                                ((Lista) elemento).setFecha_not(date);
                            }
                        }
                        else
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder( getActivity());
                            alert.setTitle(R.string.titulo_fecha_incorrecta);
                            alert.setMessage(R.string.mensaje_fecha_incorrecta);
                            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            });

                            alert.create().show();
                        }

                    }
                });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        init(v);

        return builder.create();

    }

    private void init( View v ) {

        fecha   = (Spinner) v.findViewById(R.id.spinner_fecha);
        hora    = (Spinner) v.findViewById(R.id.spinner_hora);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterFecha = ArrayAdapter.createFromResource(getContext(), R.array.spinner_fecha_array,
                       android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterFecha.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        fecha.setAdapter(adapterFecha);

        adapterHora = ArrayAdapter.createFromResource( getContext(),R.array.spinner_hora_array,
                      android.R.layout.simple_spinner_item);
        adapterHora.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hora.setAdapter(adapterHora);


        //Eventos

        fecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final TextView tv = (TextView) parent.getChildAt(0);

                //Fecha actual
                if ( tv != null ) {

                    switch (position) {

                        case 0: {

                            Calendar calendarAux = Calendar.getInstance();

                            calendar.set(Calendar.DAY_OF_MONTH, calendarAux.get(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.MONTH, calendarAux.get(Calendar.MONTH));

                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            String mestxt = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

                            tv.setText(day + " de " + mestxt);

                            break;
                        }

                        case 1: {

                            Calendar calendarAux = Calendar.getInstance();
                            calendarAux.add(Calendar.DAY_OF_YEAR, 1);

                            calendar.set(Calendar.DAY_OF_MONTH, calendarAux.get(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.MONTH, calendarAux.get(Calendar.MONTH));

                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            String mestxt = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());


                            tv.setText(day + " de " + mestxt);

                            break;
                        }


                        case 2: {

                            Calendar calendarAux = Calendar.getInstance();

                            int day = calendarAux.get(Calendar.DAY_OF_MONTH);
                            int month = calendarAux.get(Calendar.MONTH);
                            int year = calendarAux.get(Calendar.YEAR);

                            // Retornar en nueva instancia del dialogo selector de fecha
                            DatePickerDialog dialog = new DatePickerDialog(

                                    getActivity(),
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                            DialogoPonerNotificacion.this.calendar.set(year, month, dayOfMonth);

                                            String mes = DialogoPonerNotificacion.this.calendar.getDisplayName(
                                                    Calendar.MONTH,
                                                    Calendar.LONG,
                                                    Locale.getDefault());

                                            tv.setText(dayOfMonth + " de " + mes);
                                        }
                                    },
                                    year,
                                    month,
                                    day);


                            dialog.getDatePicker().setMinDate(calendarAux.getTimeInMillis());
                            if ( !dialog.isShowing() ) dialog.show();

                            break;
                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }

        });

        hora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final SimpleDateFormat format   = new SimpleDateFormat("HH:mm a");
                final TextView tv               = (TextView) parent.getChildAt(0);

                //Fecha actual
                if ( tv != null ) {

                    switch (position){

                        case 0 : {


                            calendar.set( Calendar.HOUR_OF_DAY, 8);
                            calendar.set( Calendar.MINUTE, 0);
                            calendar.set( Calendar.SECOND, 0);

                            tv.setText(format.format(calendar.getTime()));

                            break;
                        }

                        case 1 : {


                            calendar.set( Calendar.HOUR_OF_DAY, 13);
                            calendar.set( Calendar.MINUTE, 0);
                            calendar.set( Calendar.SECOND, 0);

                            tv.setText(format.format(calendar.getTime()));

                            break;
                        }

                        case 2 : {


                            calendar.set( Calendar.HOUR_OF_DAY, 18);
                            calendar.set( Calendar.MINUTE, 0);
                            calendar.set( Calendar.SECOND, 0);

                            tv.setText(format.format(calendar.getTime()));

                            break;
                        }

                        case 3 : {


                            calendar.set( Calendar.HOUR_OF_DAY, 20);
                            calendar.set( Calendar.MINUTE, 0);
                            calendar.set( Calendar.SECOND, 0);

                            tv.setText(format.format(calendar.getTime()));

                            break;
                        }

                        case 4 : {

                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);

                            // Retornar en nueva instancia del dialogo selector de tiempo
                            TimePickerDialog dialog = new TimePickerDialog(

                                    getActivity(),
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                            calendar.set( Calendar.HOUR_OF_DAY, hourOfDay);
                                            calendar.set( Calendar.MINUTE, minute);

                                            tv.setText(format.format(calendar.getTime()));
                                        }
                                    },
                                    hour,
                                    minute,
                                    DateFormat.is24HourFormat(getActivity()));

                            if ( !dialog.isShowing() ) dialog.show();

                            break;
                        }

                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}
