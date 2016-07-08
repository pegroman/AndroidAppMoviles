package com.example.pegroman.tets;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;

import java.util.Locale;

public class PreferenciasActividad extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opciones);
        ListPreference listPreference = (ListPreference) findPreference("Idioma");
        if(listPreference.getValue()==null) {
            listPreference.setValueIndex(0);
        }
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                SharedPreferences lang =  getSharedPreferences("Lang",getBaseContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = lang.edit();
                editor.putString("lang", o.toString());
                editor.commit();
                preference.setSummary(o.toString());
                changeLang();
                recreate();
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        changeLang();
        NavUtils.navigateUpTo(PreferenciasActividad.this, new Intent(PreferenciasActividad.this, ItemListActivity.class));
    }

    private void changeLang(){
        SharedPreferences lang = getSharedPreferences("Lang",getBaseContext().MODE_PRIVATE);
        String nuevoLang = lang.getString("lang","ESP");
        Configuration config;
        config = new Configuration(getResources().getConfiguration());
        switch (nuevoLang){
            case "ESP":
                config.locale = new Locale("es");
                break;
            case "ENG":
                config.locale = Locale.ENGLISH;
                break;
        }
        this.getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
