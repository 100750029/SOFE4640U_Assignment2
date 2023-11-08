package com.example.sofe4640u_assignment2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationInput extends AppCompatActivity {

    private EditText editAddress, editLongitude, editLatitude; // Edit Text variables
    private Button deleteButton; // Delete button for location
    private Location selectedLocation; // Variable for picked location

    Double longitude, latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_input);
        initWidgets(); // Loads all the ID's
        checkForEditNote(); // Check if the location is being edited

    }


    // Loading all the ID's
    private void initWidgets(){
        editAddress = findViewById(R.id.editAddressText);
        editLongitude = findViewById(R.id.editLongitudeText);
        editLatitude = findViewById(R.id.editLatitudeText);
        deleteButton = findViewById(R.id.buttonDeleteLocation);

    }


    // Function for editing a location
    private void checkForEditNote()
    {
        Intent previousIntent = getIntent();

        int passedLocationID = previousIntent.getIntExtra(Location.LOCATION_EDIT_EXTRA, -1);
        selectedLocation = Location.getLocationForID(passedLocationID);

        if (selectedLocation != null)
        {
            // Sets the values from the database
            editAddress.setText(selectedLocation.getAddress());
            editLongitude.setText(String.valueOf(selectedLocation.getLongitude()));
            editLatitude.setText(String.valueOf(selectedLocation.getLatitude()));
        }

        else
        {
            deleteButton.setVisibility(View.INVISIBLE); // Delete button is visible when location is being updated
        }
    }

    // Function for saving a location
    public void saveLocation( View view){
        SQLDatabaseLocation sqlDatabaseLocation = SQLDatabaseLocation.instanceofDatabase(this); // Gets instance of database

        // Values of location
        String address = String.valueOf(editAddress.getText());
        String longitudeText = String.valueOf(editLongitude.getText());
        String latitudeText = String.valueOf(editLatitude.getText());

        longitude = Double.parseDouble(longitudeText);
        latitude = Double.parseDouble(latitudeText);

        String addressFound = getAddress(latitude, longitude);

        // Check is latitude and longitude is entered, if not a warning is given to save a note
        if (latitudeText.isEmpty() || longitudeText.isEmpty()) {
            Toast.makeText(this, "Please enter an latitude and longitude", Toast.LENGTH_SHORT).show();
            return;
        }


        if(selectedLocation == null)
        {
            int id = Location.locationArrayList.size();
            Location newLocation = new Location(id, addressFound, longitude, latitude);
            Location.locationArrayList.add(newLocation);
            sqlDatabaseLocation.LocationAddition(newLocation);
            Log.d("NotesSave", "New note added: " + newLocation.getAddress());
        }
        else
        {
            selectedLocation.setAddress(address);
            selectedLocation.setLongitude(longitude);
            selectedLocation.setLatitude(latitude);
            sqlDatabaseLocation.LocationUpdate(selectedLocation);
            Log.d("NotesSave", "New note added: " + selectedLocation.getAddress());

        }


        finish();
    }

    // Function for deleting a location
    public void deleteLocation(View view)
    {
        selectedLocation.setDelete(new Date());
        SQLDatabaseLocation sqlDatabaseLocation = SQLDatabaseLocation.instanceofDatabase(this);
        sqlDatabaseLocation.LocationUpdate(selectedLocation);
        finish();
    }


    // Function for canceling a location
    public void cancelLocation(View view){
        finish();
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
                //e.printStackTrace();
                address = "Address Isn't Found";
            }
        }

        return address;
    }
}

