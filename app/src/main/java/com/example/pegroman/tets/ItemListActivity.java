package com.example.pegroman.tets;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    DBHelper helper = new DBHelper(this);
    SQLiteDatabase db;
    public static final List<DummyItem> ITEMS = new ArrayList<>();
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        final Intent i = new Intent(this, AgregarPersona.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        changeLang();
        cargarItems();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(i);
            }
        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_list_activity_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings){
            SharedPreferences pref = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
            if (pref.contains("Id")){
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("Id");
                editor.commit();
                Intent i = new Intent(ItemListActivity.this, Register.class);
                startActivity(i);
            }
        }
        if(id==R.id.action_languaje){
            startActivity(new Intent(ItemListActivity.this, PreferenciasActividad.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        cargarItems();
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        cargarItems2(query);
        if (ItemListActivity.ITEMS.isEmpty()){
            Toast.makeText(this,getBaseContext().getString(R.string.sinResultados), Toast.LENGTH_SHORT).show();
            cargarItems();
        }
        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            Bitmap imag = BitmapFactory.decodeByteArray(mValues.get(position).foto, 0, mValues.get(position).foto.length);
            holder.mImagen.setImageBitmap(imag);
            holder.mFecha.setText(mValues.get(position).fecha);
            //holder.mNombre.setText(mValues.get(position).nombre);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            //public final TextView mNombre;
            public final ImageView mImagen;
            public DummyItem mItem;
            public final TextView mFecha;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mImagen=(ImageView)view.findViewById(R.id.imagen);
                mFecha=(TextView)view.findViewById(R.id.fecha);
                //mNombre=(TextView)view.findViewById(R.id.nombre);
                view.setOnCreateContextMenuListener(menuContextListener);
            }

            private final View.OnCreateContextMenuListener menuContextListener = new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    if (mItem!= null) {
                        MenuInflater inflater = getMenuInflater();
                        inflater.inflate(R.menu.context, contextMenu);
                        contextMenu.findItem(R.id.descargar).setOnMenuItemClickListener(downList);
                    }
                }
            };

            private final MenuItem.OnMenuItemClickListener downList = new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    try {
                        String path= System.getenv("SECONDARY_STORAGE");
                        //File sd = Environment.getExternalStorageDirectory();
                        File directorio = new File("/mnt/sdcard/Producto");
                        directorio.mkdirs();
                        File image = new File(directorio, mItem.nombre+" "+mItem.id+".png");

                        boolean success = false;
                        FileOutputStream outStream;

                        Bitmap bitmap = BitmapFactory.decodeByteArray(mItem.foto, 0, mItem.foto.length);
                        outStream = new FileOutputStream(image);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                        outStream.flush();
                        outStream.close();
                        success = true;

                        if (success) {
                            NotificationCompat.Builder mBuilder =
                                    (NotificationCompat.Builder) new NotificationCompat.Builder(ItemListActivity.this)
                                            .setSmallIcon(R.drawable.ic_download)
                                            .setContentTitle(getBaseContext().getString(R.string.Descargar))
                                            .setContentText(getBaseContext().getString(R.string.DescargarDesc))
                                            .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND);
                            NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1, mBuilder.build());
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getBaseContext().getString(R.string.ErrorDown), Toast.LENGTH_LONG).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            };

                @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }


    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position,String nombre,String apellido,byte[] foto,String fecha) {
        return new DummyItem(String.valueOf(position), nombre+" "+position, makeDetails(nombre,apellido),nombre,apellido,foto,fecha);
    }

    private static String makeDetails(String nombre,String apellido) {
        return nombre+" "+apellido;
    }

    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;
        public final String nombre;
        public final String apellido;
        public final byte[] foto;
        public final String fecha;

        public DummyItem(String id, String content, String details,String nombre,String apellido,byte[] foto,String fecha) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.nombre=nombre;
            this.apellido=apellido;
            this.foto=foto;
            this.fecha=fecha;

        }

        @Override
        public String toString() {
            return content;
        }
    }

    public void cargarItems(){
        ItemListActivity.ITEMS.clear();
        db=helper.open();
        String consulta="SELECT * from Item";
        Cursor fila = db.rawQuery(consulta,null);
        if (fila !=null) {
            fila.moveToFirst();
            for (int i = 0; i < fila.getCount(); i++) {
                addItem(createDummyItem(fila.getInt(0),fila.getString(1),fila.getString(2),fila.getBlob(3),fila.getString(4)));
                fila.moveToNext();
            }
            fila.close();
        }
        helper.close();
    }

    public void cargarItems2(String busqueda){
        ItemListActivity.ITEMS.clear();
        db=helper.open();
        String consulta="SELECT * FROM Item WHERE "+
                "(nombre like '%" + busqueda+ "%')";
        Cursor fila = db.rawQuery(consulta,null);
        if (fila !=null) {
            fila.moveToFirst();
            for (int i = 0; i < fila.getCount(); i++) {
                addItem(createDummyItem(fila.getInt(0),fila.getString(1),fila.getString(2),fila.getBlob(3),fila.getString(4)));
                fila.moveToNext();
            }
            fila.close();
        }
        helper.close();
    }
}
