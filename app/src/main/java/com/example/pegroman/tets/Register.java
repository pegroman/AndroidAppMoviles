package com.example.pegroman.tets;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Register extends AppCompatActivity {
    public EditText rNombre, rPass, rNpass;
    public Button rBtnRegistrar, rBtnLimpiar;
    public TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        if (pref.contains("Id")){
            Intent i = new Intent(Register.this, ItemListActivity.class);
            startActivity(i);
        }else{
            setContentView(R.layout.activity_register);

            rNombre=(EditText)findViewById(R.id.rName);
            rPass=(EditText)findViewById(R.id.rPass);
            rNpass=(EditText)findViewById(R.id.rNpass);
            rBtnLimpiar=(Button)findViewById(R.id.rBtnLimpiar);
            rBtnRegistrar=(Button)findViewById(R.id.rBtnRegistrar);
            login=(TextView)findViewById(R.id.lgin);

            rBtnLimpiar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    rNombre.setText("");
                    rPass.setText("");
                    rNpass.setText("");
                }
            });

            rBtnRegistrar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = rNombre.getText().toString();
                    String pass1 = rPass.getText().toString();
                    String pass2 = rNpass.getText().toString();
                    DBHelper helper = new DBHelper(Register.this);
                    SQLiteDatabase db = helper.open();
                    if (name.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                        Snackbar.make(view, getBaseContext().getString(R.string.nulos), Snackbar.LENGTH_SHORT).show();
                    }else{
                        if (pass1.equals(pass2)){
                            helper = new DBHelper(Register.this);
                            ContentValues registros = new ContentValues();
                            registros.put("nombre", name);
                            registros.put("contrasenia", pass1);
                            SQLiteDatabase bd = helper.open();
                            bd.insert("User", null, registros);
                            bd.close();
                            Intent i = new Intent(Register.this, Login.class);
                            startActivity(i);
                        }else{
                            Snackbar.make(view, getBaseContext().getString(R.string.passDif), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            login.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Register.this, Login.class);
                    startActivity(i);
                }
            });
        }

    }
}
