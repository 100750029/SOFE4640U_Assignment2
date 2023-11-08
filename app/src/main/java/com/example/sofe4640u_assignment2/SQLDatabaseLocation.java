package com.example.sofe4640u_assignment2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLDatabaseLocation extends SQLiteOpenHelper {

    private static SQLDatabaseLocation sqlDatabaseLocation;

    private static final String DATABASE_NAME = "LocationDatabase"; // Database name
    private static final int DATABASE_VERSION = 1; // Database version
    private static final String TABLE_NAME = "LocationTable"; // Table name
    private static final String Counter = "Counter"; // Counter
    private static final String Unique_ID = "id"; // Location ID
    private static final String Address = "address"; // Location title
    private static final String Longitude = "longitude"; // Location longitude
    private static final String Latitude = "latitude"; // Location latitude
    private static final String Deleted_Note = "deleted"; // Deleted note

    // Converting date format
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    // Creating Database and setting version
    public SQLDatabaseLocation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Most updated version of database
    public static SQLDatabaseLocation instanceofDatabase(Context context){
        if(sqlDatabaseLocation == null){
            sqlDatabaseLocation = new SQLDatabaseLocation(context);
        }
        return sqlDatabaseLocation;
    }

    // Function for creating table and column in tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(Counter)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(Unique_ID)
                .append(" INT, ")
                .append(Address)
                .append(" TEXT, ")
                .append(Longitude)
                .append(" DOUBLE, ")
                .append(Latitude)
                .append(" DOUBLE, ")
                .append(Deleted_Note)
                .append(" TEXT)");

        sqLiteDatabase.execSQL(sql.toString());
    }

    // Function for upgrading database version
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

//        if(oldVersion < 2){
//                sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + Note_Background + " TEXT ");
//        }

    }

    // Function for adding location
    public void LocationAddition(Location location){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase(); // Writable to database

        // adding locations to database
        ContentValues cv = new ContentValues();
        cv.put(Unique_ID,location.getId());
        cv.put(Address,location.getAddress());
        cv.put(Longitude,location.getLongitude());
        cv.put(Latitude,location.getLatitude());
        cv.put(Deleted_Note, getStringFromDate(location.getDelete()));

        // SQl statement for adding locations
        sqLiteDatabase.insert(TABLE_NAME, null, cv);

        sqLiteDatabase.close();
    }

    // Adding locations from database to array
    public void keepLocationArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try(Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)){

            if(result.getCount()!=0){

                // Assigning column values
                while(result.moveToNext()){
                    int id = result.getInt(1);
                    String address = result.getString(2);
                    Double longitude = result.getDouble(3);
                    Double latitude = result.getDouble(4);
                    String stringDelete = result.getString(5);
                    Date delete = getDateFromString(stringDelete);

                    Location location = new Location(id,address,longitude,latitude, delete);
                    Location.locationArrayList.add(location);
                }

            }

        }

        sqLiteDatabase.close();
    }

    // Function for updating locations currently not active in app
    public void LocationUpdate(Location location){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Unique_ID,location.getId());
        cv.put(Address,location.getAddress());
        cv.put(Longitude,location.getLongitude());
        cv.put(Latitude,location.getLatitude());
        cv.put(Deleted_Note, getStringFromDate(location.getDelete()));

        // SQl statement for updating locations
        sqLiteDatabase.update(TABLE_NAME, cv, Unique_ID + " =? ", new String[]{String.valueOf(location.getId())});
    }

    // Get string from date
    private String getStringFromDate(Date date){
        if(date == null){
            return null;
        }
        return dateFormat.format(date);
    }

    // Get date from String
    private Date getDateFromString(String string){
        try{
            return dateFormat.parse(string);
        }
        catch (ParseException | NullPointerException e){
            return null;
        }
    }
}
