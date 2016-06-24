package com.example.pegroman.tets;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgregarPersona extends AppCompatActivity {
    EditText idItem, agNombre, agApellido;
    Button btnCancel, btnSave;
    ImageView image;
    TextView fecha, ubicacion;
    DateFormat sdf;
    Double latitud, longitud;
    private LocationManager locationManager = null;
    private MyLocationListener locationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_persona);
        idItem = (EditText) findViewById(R.id.idItem);
        agNombre = (EditText) findViewById(R.id.addNombre);
        btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnSave = (Button) findViewById(R.id.btnGuardar);
        agApellido = (EditText) findViewById(R.id.addApellido);
        image = (ImageView) findViewById(R.id.addImagen);
        fecha=(TextView)findViewById(R.id.addHora);
        ubicacion = (TextView) findViewById(R.id.addUbicacion);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 0);

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgregarPersona.this,ItemListActivity.class));
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper helper =new DBHelper(AgregarPersona.this);
                String id=idItem.getText().toString();
                String nombre=agNombre.getText().toString();
                String apellido=agApellido.getText().toString();
                String fech=fecha.getText().toString();
                BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                if (esNumero(id)){
                    ContentValues registro =new ContentValues();
                    registro.put("id",Integer.parseInt(id));
                    registro.put("nombre",nombre);
                    registro.put("apellido",apellido);
                    registro.put("foto",stream.toByteArray());
                    registro.put("fecha",fech);
                    SQLiteDatabase bd =helper.open();
                    bd.insert("Item",null,registro);
                    bd.close();

                    ContentValues registro2 = new ContentValues();
                    registro2.put("latitud", latitud);
                    registro2.put("longitud", longitud);
                    registro2.put("idPersona",id);
                    bd= helper.open();
                    bd.insert("Ubicacion",null,registro2);
                    helper.close();
                    NavUtils.navigateUpTo(AgregarPersona.this, new Intent(AgregarPersona.this, ItemListActivity.class));
                }else{
                    Toast.makeText(AgregarPersona.this,getBaseContext().getString(R.string.idint),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpTo(AgregarPersona.this, new Intent(AgregarPersona.this, ItemListActivity.class));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        image.setImageBitmap(bp);
        Date date = new Date();
        sdf= new SimpleDateFormat("dd/MM/yyyy");
        fecha.setText(sdf.format(date));
        this.sacarLocalidad();
    }

    public void sacarLocalidad() {
        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    public TextView getLocalizacion() {
        return this.ubicacion;
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (ubicacion.getText().toString().isEmpty()) {
                longitud = loc.getLongitude();
                latitud = loc.getLatitude();
                // Muestro las coordenadas
                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latitud, longitud, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AgregarPersona.this.getLocalizacion().setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
            }
            else{
                return;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public static boolean esNumero(String texto) {
        try {
            Long.parseLong(texto);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
