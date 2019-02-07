package com.ripani.perren.amherdt.birrapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ripani.perren.amherdt.birrapp.modelo.Cerveza;
import com.ripani.perren.amherdt.birrapp.modelo.Local;
import com.ripani.perren.amherdt.birrapp.modelo.LocalDao;
import com.ripani.perren.amherdt.birrapp.modelo.MyDataBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class PerfilLocal extends AppCompatActivity  {


    private Button ubicacion;
    private Button reservar;
    private EditText tvNombre;
    private EditText tvTel;
    private EditText tvHoraInicio;
    private EditText tvHoraCierre;
    private ImageView imagen;
    private ListView listaCervezas;
    private Local local;
    private LocalDao localDao;
    private ArrayAdapter<Cerveza> adapterCervezas;
    private List<Cerveza> arrayCervezas = new ArrayList<>();
    private String latitud;
    private String longitud;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_profile);




        ubicacion= (Button) findViewById(R.id.btnUbicacion);
        reservar= (Button) findViewById(R.id.btnReservar);
        tvNombre = (EditText) findViewById(R.id.nombre);
        tvTel = (EditText) findViewById(R.id.numtel);
        tvHoraInicio = (EditText) findViewById(R.id.horainicio);
        tvHoraCierre = (EditText) findViewById(R.id.horacierre);
        listaCervezas = (ListView) findViewById(R.id.listaCervezas);
        imagen = findViewById(R.id.imgLocal);



        tvNombre.setEnabled(false);
        tvNombre.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTel.setEnabled(false);
        tvTel.setGravity(Gravity.CENTER_HORIZONTAL);
        tvHoraInicio.setEnabled(false);
        tvHoraInicio.setGravity(Gravity.CENTER_HORIZONTAL);
        tvHoraCierre.setEnabled(false);
        tvHoraCierre.setGravity(Gravity.CENTER_HORIZONTAL);



        Runnable dbthread = new Runnable() {
            @Override
            public void run() {


        localDao = MyDataBase.getInstance(getBaseContext()).getLocalDao(); //ojo
        long idlocal = getIntent().getExtras().getLong("idlocal");
        local = localDao.getById(idlocal);
        tvNombre.setText(local.getNombre());
        tvTel.setText("Teléfono: +54115689658");
        tvHoraInicio.setText("Apertura: " +local.getHoraApertura()+" hs");
        tvHoraCierre.setText("Cierre: " +local.getHoraCierre()+" hs");

        latitud = local.getLatitud().toString();
        longitud = local.getLongitud().toString();





                ContextWrapper cw = new ContextWrapper(getBaseContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File mypath=new File(directory,idlocal+".jpg");


                Bitmap bm = setImagen(mypath.toString());
                if(bm!=null) {
                    imagen.setImageBitmap(bm);
                }




                adapterCervezas = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, local.getCervezas());
                listaCervezas.setAdapter(adapterCervezas);
                System.out.println(listaCervezas.getAdapter().getCount());

            }
        };


        Thread t1 = new Thread(dbthread);
        t1.start();

        try {
            t1.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        reservar.setEnabled(local.getReservas());



        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               int capacidadRestante = local.getCapacidad();
                if(capacidadRestante>0){
                    local.setCapacidad(capacidadRestante-1);


                    Runnable dbthread = new Runnable() {
                        @Override
                        public void run() {

                    localDao.update(local);

                        }
                    };

                    Thread t1 = new Thread(dbthread);
                    t1.start();

                    Toast.makeText(getBaseContext(), "Reserva Registrada",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "No hay lugar disponible",
                            Toast.LENGTH_LONG).show();
            }


            }
        });





        ubicacion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitud+","+longitud+"100");


// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);


            }
        });



    }

    private Bitmap setImagen(String path){

        Bitmap b = null;
        try {
            File file = new File(path);
          b = BitmapFactory.decodeStream(new FileInputStream(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return b;
    }

}
