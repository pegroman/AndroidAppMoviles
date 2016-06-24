package com.example.pegroman.tets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
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
            }

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

}
