package com.example.pegroman.tets;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private ItemListActivity.DummyItem mItem;
    private DBHelper helper;
    private SQLiteDatabase bd;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ItemListActivity.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DBHelper(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
            //((TextView) rootView.findViewById(R.id.item_lugar)).setText(mItem.ubicacion);
            WebView web= ((WebView) rootView.findViewById(R.id.item_mapa));
            web.setWebViewClient(new WebViewClient());
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setSupportZoom(true);
            web.getSettings().setBuiltInZoomControls(true);
            web.getSettings().setDisplayZoomControls(false);
            String consulta = "SELECT * FROM Ubicacion WHERE idPersona='"+mItem.id+"'";

            bd = helper.open();
            Cursor fila = bd.rawQuery(consulta, null);
            if (fila.moveToFirst()) {
                double latitud= fila.getDouble(2);
                double longitud= fila.getDouble(1);
                web.loadUrl("https://www.google.com/maps?&q="+longitud+"+"+latitud+"&ll="+longitud+"+"+latitud+"&z=10");
                //web.loadUrl("https://maps.google.com/?ll="+longitud+","+latitud+"&spn="+longitud+","+latitud+"&t=h&z=4");
                //http://www.google.com/maps/place/49.46800006494457,17.11514008755796/@49.46800006494457,17.11514008755796,17z
                fila.close();
            }
            helper.close();
        }

        return rootView;
    }
}
