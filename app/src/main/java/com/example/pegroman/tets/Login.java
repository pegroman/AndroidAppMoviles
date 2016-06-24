package com.example.pegroman.tets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    public EditText nombre, pass;
    public Button login, clean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nombre=(EditText)findViewById(R.id.lNombre);
        pass=(EditText)findViewById(R.id.lPass);
        login=(Button)findViewById(R.id.lBtnLogin);
        clean=(Button)findViewById(R.id.lBtnLimpiar);

        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre.setText("");
                pass.setText("");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombree=nombre.getText().toString();
                String passwd=pass.getText().toString();
                DBHelper helper = new DBHelper(Login.this);
                SQLiteDatabase bd = helper.open();
                if (nombree.isEmpty()||passwd.isEmpty()){
                    Snackbar.make(view,getBaseContext().getString(R.string.nulos),Snackbar.LENGTH_SHORT).show();
                }else {
                    String consulta = "SELECT nombre,contrasenia,id FROM User WHERE " +
                            "(nombre like '" + nombree + "%')";
                    Cursor fila = bd.rawQuery(consulta, null);
                    if (fila.moveToFirst()) {
                        String passRecu = fila.getString(1);
                        if (passwd.equals(passRecu)){
                            SharedPreferences preferencias = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("Id",fila.getString(2));
                            editor.commit();
                            Intent i = new Intent(Login.this, ItemListActivity.class);
                            startActivity(i);
                        }else{
                            Snackbar.make(view,getBaseContext().getString(R.string.passDif),Snackbar.LENGTH_SHORT).show();
                        }
                        helper.close();
                    }
                }
            }
        });
    }
}
