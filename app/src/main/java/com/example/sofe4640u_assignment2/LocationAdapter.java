package com.example.sofe4640u_assignment2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

public class LocationAdapter extends ArrayAdapter<Location> {

    private Context context;

    // Note adapter
    public LocationAdapter(Context context, List<Location> location ){
        super(context, 0, location);
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Location location = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_layout, parent, false);
        }

        // Getting ID for locations inputs
        TextView address = convertView.findViewById(R.id.cellAddress);
        TextView longitude = convertView.findViewById(R.id.cellLongitude);
        TextView latitude = convertView.findViewById(R.id.cellLatitude);

        Button editButton = convertView.findViewById(R.id.buttonEditLocation);
        Button deleteButton = convertView.findViewById(R.id.buttonDeleteLocation);

        // Setting locations information to text output
        address.setText(location.getAddress());
        longitude.setText(String.valueOf(location.getLongitude()));
        latitude.setText(String.valueOf(location.getLatitude()));


        // OnClick listener for edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = getItem(position); // Gets the locations position
                if (location != null) {
                    Intent editLocationIntent = new Intent(context, LocationInput.class);
                    editLocationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    editLocationIntent.putExtra(Location.LOCATION_EDIT_EXTRA, location.getId()); // Passes the notes ID
                    context.startActivity(editLocationIntent); // Starts the note_input page
                }
            }
        });

        // OnClick listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = getItem(position); // Gets the locations position
                if (location != null) {
                    location.setDelete(new Date()); // Sets the deleted date
                    SQLDatabaseLocation sqlDatabase = SQLDatabaseLocation.instanceofDatabase(context); // Passes it to the database
                    sqlDatabase.LocationUpdate(location); // Updates the list of location
                    remove(location); // Removes the location
                    notifyDataSetChanged();
                }
            }
        });

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_layout, parent, false);
        }

        return convertView;

    }
}
