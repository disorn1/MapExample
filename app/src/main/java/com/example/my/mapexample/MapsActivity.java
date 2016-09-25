package com.example.my.mapexample;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        //Check if location is available
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            String provider = locationManager.getBestProvider(criteria, true);
            try
            {
                Location location = locationManager.getLastKnownLocation(provider);
                updateMarker(location);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                locationManager.requestLocationUpdates(provider, 3000, 10, this);
            } catch (SecurityException ex) {ex.printStackTrace();}
        }
    }

    @Override
    public void onLocationChanged (Location location)
    {
        updateMarker(location);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {}
    @Override
    public void onProviderDisabled(String s) {}

    private Marker marker;
    private Geocoder geocoder;
    private void updateMarker(Location location)
    {
        if (geocoder == null) geocoder = new Geocoder(getApplicationContext());
        LatLng pos = new LatLng(location.getLatitude(),location.getLongitude());
        String addressString = pos.toString();
        try
        {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (!addresses.isEmpty()) addressString = addresses.get(0).getAddressLine(0);
        }
        catch (IOException e) {e.printStackTrace();}
        if (marker == null)
        {
            marker = mMap.addMarker(new MarkerOptions().position(pos).title(addressString));
        }
        else
        {
            marker.hideInfoWindow();
            marker.setPosition(pos);
            marker.setTitle(addressString);
        }
        marker.showInfoWindow();
    }
}