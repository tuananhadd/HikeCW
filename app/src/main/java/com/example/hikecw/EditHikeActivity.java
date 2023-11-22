package com.example.hikecw;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditHikeActivity extends AppCompatActivity {
    private EditText editTitle, editLocation, editLength, editLevel, editDescription, editPay;
    private DatePicker editDate;
    private ImageView hikeImageView;
    private long hikeId;
    private RadioGroup parkRadioGroup;
    private RadioButton yesRadioButton, noRadioButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hike);

        editTitle = findViewById(R.id.editTextEditTitle);
        editLocation = findViewById(R.id.editTextEditLocation);
        editDate = findViewById(R.id.editdatePicker);
        parkRadioGroup = findViewById(R.id.editParkRadioGroup);
        yesRadioButton = findViewById(R.id.yesRadioButton);
        noRadioButton = findViewById(R.id.noRadioButton);
        editLength = findViewById(R.id.editTextEditLength);
        editLevel = findViewById(R.id.editTextEditLevel);
        editDescription = findViewById(R.id.editTextEditDescription);
        editPay = findViewById(R.id.editTextEditPay);
        hikeImageView = findViewById(R.id.hikeImageEditView);

        hikeId = getIntent().getLongExtra("HIKE_ID", -1);
        dbHelper = new DatabaseHelper(this);

        if (hikeId != -1) {
            Hike hike = getHikeData(hikeId);
            editTitle.setText(hike.getTitle());
            editLocation.setText(hike.getLocation());
            setDatePickerDate(editDate, hike.getDate());
            String park = hike.getPark();

            if ("Yes".equals(park)) {
                yesRadioButton.setChecked(true);
            } else if ("No".equals(park)) {
                noRadioButton.setChecked(true);
            }

            editLength.setText(String.valueOf(hike.getLength()));
            editLevel.setText(hike.getLevel());
            editDescription.setText(hike.getDescription());
            editPay.setText(String.valueOf(hike.getPay()));

            hikeImageView.setImageBitmap(BitmapFactory.decodeByteArray(hike.getImage(), 0, hike.getImage().length));
        }
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the main activity
                Intent intent = new Intent(EditHikeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveEditedData(View view) {
        if (validateInput()) {
            String title = editTitle.getText().toString();
            String location = editLocation.getText().toString();
            String date = getSelectedDate(editDate);
            String park = getSelectedPark();
            int length = Integer.parseInt(editLength.getText().toString());
            String level = editLevel.getText().toString();
            String description = editDescription.getText().toString();
            int pay = Integer.parseInt(editPay.getText().toString());
            byte[] image = imageViewToByteArray(hikeImageView);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TITLE, title);
            values.put(DatabaseHelper.COLUMN_LOCATION, location);
            values.put(DatabaseHelper.COLUMN_DATE, date);
            values.put(DatabaseHelper.COLUMN_PARKING, park);
            values.put(String.valueOf(DatabaseHelper.COLUMN_LENGTH), length);
            values.put(DatabaseHelper.COLUMN_LEVEL, level);
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
            values.put(String.valueOf(DatabaseHelper.COLUMN_PAY), pay);
            values.put(DatabaseHelper.COLUMN_IMAGE, image);

            int rowsUpdated = db.update(
                    DatabaseHelper.TABLE_HIKES,
                    values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(hikeId)}
            );

            db.close();

            if (rowsUpdated > 0) {
                Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput() {
        if (editTitle.getText().toString().isEmpty() ||
                editLocation.getText().toString().isEmpty() ||
                editLength.getText().toString().isEmpty() ||
                editLevel.getText().toString().isEmpty() ||
                editDescription.getText().toString().isEmpty() ||
                editPay.getText().toString().isEmpty() ||
                getSelectedPark().isEmpty() ||
                hikeImageView.getDrawable() == null) {

            Toast.makeText(this, "Please enter all required fields and select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getSelectedDate(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private Hike getHikeData(long hikeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
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
        String[] selectionArgs = {String.valueOf(hikeId)};

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
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            String park = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PARKING));
            int length = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LENGTH));
            String level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            int pay = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAY));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

            cursor.close();

            Hike hike = new Hike();
            hike.setId(hikeId);
            hike.setTitle(title);
            hike.setLocation(location);
            hike.setDate(date);
            hike.setPark(park);
            hike.setLength(length);
            hike.setLevel(level);
            hike.setDescription(description);
            hike.setPay(pay);
            hike.setImage(image);

            return hike;
        }

        return null;
    }

    private void setDatePickerDate(DatePicker datePicker, String date) {
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
        int day = Integer.parseInt(dateParts[2]);
        datePicker.init(year, month, day, null);
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                hikeImageView.setImageBitmap(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] imageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private String getSelectedPark() {
        int selectedRadioButtonId = parkRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        } else {
            return "";
        }
    }
}