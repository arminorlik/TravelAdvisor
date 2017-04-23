package com.example.norbert.routespreparation2;

import android.database.Cursor;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.Color.RED;

public class DistanceAdvisor extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.LvTrasa1)
    ListView LvTrasa1;
    @BindView(R.id.LLglowny)
    LinearLayout LLglowny;
    @BindView(R.id.TvInfoTrasa1)
    TextView TvInfoTrasa1;

    private GoogleMap map1;
    private int ItemClickedPosition;
    private DatabaseHelper databaseHelper;
    private Cursor data;
    private String[] tablicaAdresow;

    private List<LatLng> coord;
    private List<Address> adres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_distance_advisor);

        ButterKnife.bind(this);
        SupportMapFragment supportMapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);

        supportMapFragment1.getMapAsync(this);

        Bundle b = getIntent().getExtras();
        ItemClickedPosition = b.getInt("ItemClicked", 0);

        databaseHelper = new DatabaseHelper(this);
        data = databaseHelper.getData();
        tablicaAdresow = new String[100000];

        coord = new ArrayList<LatLng>();
        makeAdressArray();//tworzenie tablicy adresów
        makeCoordArray(); //tworzenie listy z kooordynatami z nazw adresów

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row, R.id.Row, tablicaAdresow);
        LvTrasa1.setAdapter(adapter);
        TvInfoTrasa1.setText(travelInfo());
    }

    private void makeAdressArray() {
        String stringAdresow;
        data.moveToPosition(ItemClickedPosition);
        stringAdresow = data.getString(4);

        tablicaAdresow = stringAdresow.split(Pattern.quote("|"));
    }

    private void makeCoordArray() {
        String coordString;

        data.moveToPosition(ItemClickedPosition);
        coordString = data.getString(5);

        coordString = coordString.replace("lat/lng: (", "");
        coordString = coordString.replace(")", "");

        String[] strLatLong = coordString.split("\\|");

        for (String item : strLatLong) {
            String[] str = item.split(",");
            double latitude = Double.parseDouble(str[0]);
            double longitude = Double.parseDouble(str[1]);

            coord.add(new LatLng(latitude, longitude));
        }
    }

    private String travelInfo() {
        String dane;
        data.moveToPosition(ItemClickedPosition);
        dane = data.getString(3);

        return dane;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map1 = googleMap;
        cameraMove();//ustawianie widoku mapy na punkcie startowym
        roadPaint();//rysowanie trasy
    }

    private void roadPaint() {

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(coord).width(8).color(RED);
        map1.addPolyline(polylineOptions);
    }

    private void cameraMove() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coord.get(0))
                .zoom(15).build();

        map1.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}



