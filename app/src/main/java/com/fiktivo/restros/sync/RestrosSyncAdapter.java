package com.fiktivo.restros.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.fiktivo.restros.PlacesContract;
import com.fiktivo.restros.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

public class RestrosSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String FILENAME_PREF = "com.fiktivo.restros";

    public RestrosSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        fetchPlaces();
    }

    public void fetchPlaces() {
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String placesJsonStr = null;
        try {
            final String BASE_URL = "http://staging.couponapitest.com/task_data.txt";
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            placesJsonStr = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            if (placesJsonStr.length() != 0)
                getPlacesDataFromJson(placesJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }

    public void getPlacesDataFromJson(String placesJsonStr) throws JSONException {
        JSONObject placesJson = new JSONObject(placesJsonStr);
        JSONObject data = placesJson.getJSONObject("data");

        Vector<ContentValues> resultsVector = new Vector<ContentValues>(data.length());
        Iterator<String> keys = data.keys();

        while (keys.hasNext()) {
            JSONObject place = data.getJSONObject(keys.next());
            String placeID = place.getString("OutletID");
            String placeName = place.getString("OutletName");
            String logoURL = place.getString("LogoURL");
            int couponsNumber = place.getInt("NumCoupons");
            double placeLatitude = Double.parseDouble(place.getString("Latitude"));
            double placeLongitude = Double.parseDouble(place.getString("Longitude"));

            ContentValues placeValues = new ContentValues();
            placeValues.put(PlacesContract.PlacesEntry.COLUMN_NAME_Place_ID, placeID);
            placeValues.put(PlacesContract.PlacesEntry.COLUMN_NAME_Place_Name, placeName);
            placeValues.put(PlacesContract.PlacesEntry.COLUMN_NAME_Logo_URL, logoURL);
            placeValues.put(PlacesContract.PlacesEntry.COLUMN_NAME_Coupons_Number, couponsNumber);
            placeValues.put(PlacesContract.PlacesEntry.COLUMN_NAME_Place_Latitude, placeLatitude);
            placeValues.put(PlacesContract.PlacesEntry.COLUMN_NAME_Place_Longitude, placeLongitude);

            resultsVector.add(placeValues);
        }

        if (resultsVector.size() > 0) {
            ContentValues[] resultsArray = new ContentValues[resultsVector.size()];
            resultsVector.toArray(resultsArray);
            getContext().getContentResolver().delete(PlacesContract.PlacesEntry.CONTENT_URI, null, null);
            getContext().getContentResolver().bulkInsert(PlacesContract.PlacesEntry.CONTENT_URI, resultsArray);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount))
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;

        return newAccount;
    }
}
