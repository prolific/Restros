package com.fiktivo.restros;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fiktivo.restros.sync.RestrosSyncAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class RestrosFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RestrosAdapter restrosAdapter;
    public static final int PLACES_LOADER = 0;
    private ListView placesListView;

    private static final String FILENAME_PREF = "com.fiktivo.restros";

    public RestrosFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLACES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        placesListView = (ListView) rootView.findViewById(R.id.places_listview);
        restrosAdapter = new RestrosAdapter(getActivity(), new ArrayList<Restro>());
        placesListView.setAdapter(restrosAdapter);

        if (isNetworkAvailable()) {
            getCurrentLocationAndUpdateRestros();
        } else
            Toast.makeText(getActivity(), "Please check your Internet Connection", Toast.LENGTH_LONG).show();
        return rootView;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getCurrentLocationAndUpdateRestros() {
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    Log.e("location", "found");
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(FILENAME_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("latitude", location.getLatitude() + "");
                    editor.putString("longitude", location.getLongitude() + "");
                    editor.commit();
                    RestrosSyncAdapter.syncImmediately(getActivity());
                    locationManager.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri placesUri = PlacesContract.PlacesEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), placesUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        restrosAdapter.clear();
        ArrayList<Restro> arrayList = new ArrayList<Restro>();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(FILENAME_PREF, Context.MODE_PRIVATE);
        double currentLatitude = Double.parseDouble(sharedPref.getString("latitude", "0"));
        double currentLongitude = Double.parseDouble(sharedPref.getString("longitude", "0"));
        Location currentLocation = new Location("A");
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);

        while (cursor.moveToNext()) {

            int place_id_index = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME_Place_ID);
            int place_name_index = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME_Place_Name);
            int place_logo_index = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME_Logo_URL);
            int offers_index = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME_Coupons_Number);
            int latitude_index = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME_Place_Latitude);
            int longitude_index = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_NAME_Place_Longitude);

            Location placeLocation = new Location("B");
            placeLocation.setLatitude(cursor.getDouble(latitude_index));
            placeLocation.setLongitude(cursor.getDouble(longitude_index));
            int distance = (int) currentLocation.distanceTo(placeLocation);

            String name = cursor.getString(place_name_index);
            String id = cursor.getString(place_id_index);
            int offers = cursor.getInt(offers_index);
            String logoURL = cursor.getString(place_logo_index);

            Restro restro = new Restro(id, name, logoURL, offers, distance);

            arrayList.add(restro);
        }

        Collections.sort(arrayList);

        for (Restro obj : arrayList)
            restrosAdapter.add(obj);
        restrosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}