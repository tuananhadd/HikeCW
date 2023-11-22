package com.example.hikecw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HikeDatabase";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HIKES = "hikes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PARKING = "park";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PAY = "pay";
    public static final String COLUMN_IMAGE = "image";

    public static final String TABLE_OBSERVATIONS = "observations";
    public static final String COLUMN_OBSERVATION_ID = "observation_id";
    public static final String COLUMN_OBSERVATION_HIKE_ID = "hike_id";
    public static final String COLUMN_OBSERVATION_DATE = "date";
    public static final String COLUMN_OBSERVATION_COMMENT = "comment";
    public static final String COLUMN_OBSERVATION_OBSERVE = "observe";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_HIKES = "CREATE TABLE " + TABLE_HIKES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_PARKING + " TEXT," +
                COLUMN_LENGTH + " INTEGER," +
                COLUMN_LEVEL + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_PAY + " INTEGER," +
                COLUMN_IMAGE + " BLOB" +
                ");";

        db.execSQL(CREATE_TABLE_HIKES);

        String CREATE_TABLE_OBSERVATIONS = "CREATE TABLE " + TABLE_OBSERVATIONS + " (" +
                COLUMN_OBSERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OBSERVATION_HIKE_ID + " INTEGER, " +
                COLUMN_OBSERVATION_DATE + " TEXT, " +
                COLUMN_OBSERVATION_COMMENT + " TEXT, " +
                COLUMN_OBSERVATION_OBSERVE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_OBSERVATION_HIKE_ID + ") REFERENCES " + TABLE_HIKES + "(" + COLUMN_ID + ")" +
                ");";

        db.execSQL(CREATE_TABLE_OBSERVATIONS);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }

    public List<Hike> getAllHikes() {
        List<Hike> hikeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_LOCATION,
                COLUMN_DATE,
                COLUMN_PARKING,
                COLUMN_LENGTH,
                COLUMN_LEVEL,
                COLUMN_DESCRIPTION,
                COLUMN_PAY,
                COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_HIKES,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String park = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARKING));
                int length = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LENGTH));
                String level = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LEVEL));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int pay = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PAY));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

                Hike hike = new Hike(id, title, location, date, park, length, level, description, pay,image);
                hikeList.add(hike);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return hikeList;
    }

    public int updateHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, hike.getTitle());
        values.put(COLUMN_LOCATION, hike.getLocation());
        values.put(COLUMN_DATE, hike.getDate());
        values.put(COLUMN_PARKING, hike.getPark());
        values.put(COLUMN_LENGTH, hike.getLength());
        values.put(COLUMN_LEVEL, hike.getLevel());
        values.put(COLUMN_DESCRIPTION, hike.getDescription());
        values.put(COLUMN_PAY, hike.getPay());
        values.put(COLUMN_IMAGE, hike.getImage());

        int rowsUpdated = db.update(
                TABLE_HIKES,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(hike.getId())}
        );

        db.close();
        return rowsUpdated;
    }

    public void deleteHike(long hikeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HIKES, COLUMN_ID + " = ?", new String[]{String.valueOf(hikeId)});
        db.close();
    }

    public List<Hike> searchHikes(String query) {
        List<Hike> searchResults = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_TITLE + " LIKE ? OR " +
                COLUMN_LOCATION + " LIKE ? OR " +
                COLUMN_DATE + " LIKE ? OR " +
                COLUMN_LENGTH + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%", "%" + query + "%", "%" + query + "%"};

        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_LOCATION,
                COLUMN_DATE,
                COLUMN_PARKING,
                COLUMN_LENGTH,
                COLUMN_LEVEL,
                COLUMN_DESCRIPTION,
                COLUMN_PAY,
                COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_HIKES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String park = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARKING));
                int length = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LENGTH));
                String level = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LEVEL));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int pay = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PAY));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

                Hike hike = new Hike(id, title, location, date, park, length, level, description, pay, image);
                searchResults.add(hike);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return searchResults;
    }

    public Hike getHikeById(long hikeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_HIKES, // Table name
                new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_LOCATION, COLUMN_DATE, COLUMN_PARKING, COLUMN_LENGTH, COLUMN_LEVEL, COLUMN_DESCRIPTION, COLUMN_PAY, COLUMN_IMAGE },
                COLUMN_ID + "=?",
                new String[] { String.valueOf(hikeId) },
                null, // Group by
                null, // Having
                null, // Order by
                null  // Limit
        );

        Hike hike = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                hike = new Hike();
                hike.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                hike.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                hike.setPark(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARKING)));
                hike.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LENGTH)));
                hike.setLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                hike.setPay(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PAY)));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                hike.setImage(imageBytes);
            }
            cursor.close();
        }

        db.close();

        return hike;
    }

    public long addObservation(long hikeId, String date, String comment, String observe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long currentTimeMillis = System.currentTimeMillis();

        values.put(COLUMN_OBSERVATION_HIKE_ID, hikeId);
        values.put(COLUMN_OBSERVATION_DATE, date);
        values.put(COLUMN_OBSERVATION_COMMENT, comment);
        values.put(COLUMN_OBSERVATION_OBSERVE, observe);

        long observationId = db.insert(TABLE_OBSERVATIONS, null, values);

        db.close();
        return observationId;
    }

    public List<Observation> getObservationsForHike(long hikeId) {
        List<Observation> observationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_OBSERVATION_ID,
                COLUMN_OBSERVATION_HIKE_ID,
                COLUMN_OBSERVATION_DATE,
                COLUMN_OBSERVATION_COMMENT,
                COLUMN_OBSERVATION_OBSERVE
        };

        String selection = COLUMN_OBSERVATION_HIKE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(hikeId)};

        Cursor cursor = db.query(
                TABLE_OBSERVATIONS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long observationId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_ID));
                String observationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_DATE));
                String observationComment = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_COMMENT));
                String observationObserve = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_OBSERVE));

                Observation observation = new Observation(observationId, hikeId, observationDate, observationComment, observationObserve);
                observationList.add(observation);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return observationList;
    }

}
