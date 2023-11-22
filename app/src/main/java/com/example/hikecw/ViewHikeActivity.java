package com.example.hikecw;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewHikeActivity extends AppCompatActivity {
    private ImageView hikeImageView;
    private TextView titleTextView, locationTextView, parkTextView, dateTextView, lengthTextView, levelTextView, descriptionTextView, payTextView;
    private Button addObservationButton;
    private TextView observationsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hike);

        hikeImageView = findViewById(R.id.hikeImageView);
        titleTextView = findViewById(R.id.titleTextView);
        locationTextView = findViewById(R.id.locationTextView);
        dateTextView = findViewById(R.id.dateTextView);
        parkTextView = findViewById(R.id.parkTextView);
        lengthTextView = findViewById(R.id.lengthTextView);
        levelTextView = findViewById(R.id.levelTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        payTextView = findViewById(R.id.payTextView);
        addObservationButton = findViewById(R.id.addObservationButton);
        observationsTextView = findViewById(R.id.observationsTextView);

        long hikeId = getIntent().getLongExtra("HIKE_ID", -1);

        Hike hike = getHikeData(hikeId);

        if (hike != null) {
            titleTextView.setText("Title: " + hike.getTitle());
            locationTextView.setText("Location: " + hike.getLocation());
            dateTextView.setText("Date: " + hike.getDate());
            parkTextView.setText("Parking lot: " + hike.getPark());
            lengthTextView.setText("Length: " + hike.getLength() + " km");
            levelTextView.setText("Level of trip: " + hike.getLevel());
            descriptionTextView.setText("Description: " + hike.getDescription());
            payTextView.setText("The amount of money spent on the trip: " + hike.getPay() + " $");

            byte[] imageBytes = hike.getImage();
            if (imageBytes != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                hikeImageView.setImageBitmap(imageBitmap);
            }
            Button backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Return to the main activity
                    Intent intent = new Intent(ViewHikeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            addObservationButton = findViewById(R.id.addObservationButton);
            addObservationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the click event to add a new observation
                    showAddObservationDialog(hikeId);
                }
            });
            if (hike != null) {
                // Existing code...

                // Display existing observations
                displayObservations(hikeId);

                // Add observation button click listener
                addObservationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle the click event to add a new observation
                        showAddObservationDialog(hikeId);
                    }
                });
            }
        }
    }

    private void displayObservations(long hikeId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Observation> observations = dbHelper.getObservationsForHike(hikeId);

        if (observations != null && !observations.isEmpty()) {
            StringBuilder observationsText = new StringBuilder("Observations:\n\n");

            for (Observation observation : observations) {
                String formattedDate = formatObservationDate(observation.getDate());
                observationsText.append("Observation: ").append(observation.getObserve()).append("\n")
                        .append("Date and Time: ").append(formattedDate).append("\n")
                        .append("Comment: ").append(observation.getComment()).append("\n\n");
            }

            observationsTextView.setText(observationsText.toString());
        } else {
            observationsTextView.setText("No observations available.");
        }
    }

    private String formatObservationDate(String timestamp) {
        // Assuming timestamp is a string representing a date, adjust the formatting as needed
        // You can use SimpleDateFormat to format the date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp; // Return original timestamp in case of an error
        }
    }


    private void showAddObservationDialog(final long hikeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Observation");

        // Create a layout to hold the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText observeEditText = new EditText(this);
        observeEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        observeEditText.setHint("Observation");
        layout.addView(observeEditText);

        final EditText timeEditText = new EditText(this);
        timeEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        timeEditText.setHint("Date and Time");
        timeEditText.setText(getCurrentDateTime());
        layout.addView(timeEditText);

        final EditText commentEditText = new EditText(this);
        commentEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        commentEditText.setHint("Comment");
        layout.addView(commentEditText);

        builder.setView(layout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String observe = observeEditText.getText().toString().trim();
                String time = timeEditText.getText().toString().trim();
                String comment = commentEditText.getText().toString().trim();

                if (observe.isEmpty()) {
                    Toast.makeText(ViewHikeActivity.this, "Please enter the Observation", Toast.LENGTH_SHORT).show();
                } else if (time.isEmpty()) {
                    Toast.makeText(ViewHikeActivity.this, "Please enter the Date and Time", Toast.LENGTH_SHORT).show();
                } else if (comment.isEmpty()) {
                    Toast.makeText(ViewHikeActivity.this, "Please enter the Comment", Toast.LENGTH_SHORT).show();
                } else {
                    // Date and Time are not empty, and Status is not empty, proceed with adding observation
                    addObservationToDatabase(hikeId, observe, time, comment);
                    displayObservations(hikeId);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addObservationToDatabase(long hikeId, String date, String observe, String comment) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String currentDateTime = getCurrentDateTime();
        dbHelper.addObservation(hikeId, observe, date, comment);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private Hike getHikeData(long userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Hike hike = null;

        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_LOCATION,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_PARKING,
                DatabaseHelper.COLUMN_LENGTH,
                DatabaseHelper.COLUMN_LEVEL,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_PAY,
                DatabaseHelper.COLUMN_IMAGE
        };

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_HIKES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            String park = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PARKING));
            int length = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LENGTH));
            String level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            int pay = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAY));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

            hike = new Hike(id, title, location, date, park, length, level, description, pay, imageBytes);

            cursor.close();
        }

        db.close();

        return hike;
    }
}