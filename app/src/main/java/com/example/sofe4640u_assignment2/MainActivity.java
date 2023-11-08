package com.example.sofe4640u_assignment2;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.example.sofe4640u_assignment2.LocationInput;
import com.example.sofe4640u_assignment2.R;
import com.example.sofe4640u_assignment2.SQLDatabaseLocation;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView locationListView; // Listview for locations
    private SearchView locationSearch; // Search for location
    private Button load; // Buttons to load all locations
    private List<Location> Locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        setLocationAdapter();
        DatabaseLoad();
        setOnClickListener();
        setupSearchView();

        // Onclick listener to load all locations point into database
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readLocationData();
            }
        });

    }

    // Call all Id's
    private void initWidgets(){

        locationListView = findViewById(R.id.locationListView);
        locationSearch = findViewById(R.id.searchView);
        load = findViewById(R.id.buttonCSV);
    }


    // Search view when clicked
    private void setupSearchView() {
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle the search as the user types in the search box.
                filterLocation(newText);
                return false;
            }
        });
    }

    // Search view restrictions and constraints
    private void filterLocation(String query) {
        LocationAdapter locationAdapter = (LocationAdapter) locationListView.getAdapter();
        List<Location> allLocation = Location.nonDeletedLocation();
        List<Location> filteredNotes = new ArrayList<>();

        for (Location location : allLocation) {
            if (location.getAddress().toLowerCase().contains(query.toLowerCase())){
                filteredNotes.add(location);
            }
        }

        locationAdapter = new LocationAdapter(getApplicationContext(), filteredNotes);
        locationListView.setAdapter(locationAdapter);
    }

    // Calls location adapter with all the notes that aren't deleted
    private void setLocationAdapter(){
        LocationAdapter locationAdapter = new LocationAdapter(getApplicationContext(), Location.nonDeletedLocation());
        locationListView.setAdapter(locationAdapter);
    }

    // Loads the database
    private void DatabaseLoad(){
        SQLDatabaseLocation sqlDatabaseLocation = SQLDatabaseLocation.instanceofDatabase(this);
        sqlDatabaseLocation.keepLocationArray();
    }


    // Onclick listener for editing a location
    private void setOnClickListener()
    {
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Location selectedNote = (Location) locationListView.getItemAtPosition(position);
                Intent editLocationIntent = new Intent(getApplicationContext(), LocationInput.class);
                editLocationIntent.putExtra(Location.LOCATION_EDIT_EXTRA, selectedNote.getId());
                startActivity(editLocationIntent);
            }
        });
    }

    public void newLocation(View view){
        Intent newLocationIntent = new Intent(this, LocationInput.class);
        startActivity(newLocationIntent);
    }


    private void readLocationData() {
        InputStream is = getResources().openRawResource(R.raw.locations); // File name for locations in csv
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String delimiter = "";
        Double latitude, longitude; // Declare x and y here

        try {
            reader.readLine();
            int id = 1;

            while ((delimiter = reader.readLine()) != null) {
                String[] tokens = delimiter.split(",");

                latitude = Double.parseDouble(tokens[0]); // Assign values to x and y
                longitude = Double.parseDouble(tokens[1]);

                String address = getAddress(latitude, longitude);

                Location sample = new Location(id, address, latitude, longitude);

                sample.setId(id);
                sample.setLatitude(latitude);
                sample.setLongitude(longitude);
                sample.setAddress(address);
                Locations.add(sample);

                saveLocation(latitude, longitude, address);

                id++;
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading file on line " + delimiter, e);
            e.printStackTrace();
        }

        recreate();
    }


    // Saves the location info into database
    private void saveLocation(Double latitude, Double longitude, String address){
        SQLDatabaseLocation sqlDatabaseLocation = SQLDatabaseLocation.instanceofDatabase(this);
        int id = Location.locationArrayList.size();
        Location newLocation = new Location(id, address, longitude, latitude);
        Locations.add(newLocation);
        sqlDatabaseLocation.LocationAddition(newLocation);
    }

    // Function for getting address using Geocoder
    private String getAddress(Double latitude, Double longitude){
        String address = "";

        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(!addresses.isEmpty()){
                    address = addresses.get(0).getAddressLine(0);
                } else{
                    address = "Address Isn't Found";
                }
            } catch(IOException e){
                Log.e("Geocoding Error", "Error fetching address: " + e.getMessage());
                address = "Address Fetching Error";
            }
        }

        return address;
    }

    @Override
    protected void onResume(){
        super.onResume();
        setLocationAdapter();
    }
}
