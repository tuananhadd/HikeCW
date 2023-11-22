package com.example.hikecw;

import android.content.ContentValues;
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

public class AddHikeActivity extends AppCompatActivity {

    private EditText editTitle, editLocation, editLength, editLevel, editDescription, editPay;
    private DatePicker datePicker;
    private ImageView hikeImageView;
    private RadioGroup parkRadioGroup;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        editTitle = findViewById(R.id.editTextTitle);
        editLocation = findViewById(R.id.editTextLocation);
        datePicker = findViewById(R.id.datePicker);
        parkRadioGroup = findViewById(R.id.parkRadioGroup);
        editLength = findViewById(R.id.editTextLength);
        editLevel = findViewById(R.id.editTextLevel);
        editDescription = findViewById(R.id.editTextDescription);
        editPay = findViewById(R.id.editTextPay);
        hikeImageView = findViewById(R.id.hikeImageView);

        dbHelper = new DatabaseHelper(this);

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddHikeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveHikeData(View view) {
        if (validateInput()) {
            String title = editTitle.getText().toString();
            String location = editLocation.getText().toString();
            String date = getSelectedDate();
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

            long newRowId = db.insert(DatabaseHelper.TABLE_HIKES, null, values);
            db.close();

            if (newRowId != -1) {
                Toast.makeText(this, "Hike added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error adding hike", Toast.LENGTH_SHORT).show();
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

    private String getSelectedPark() {
        int selectedRadioButtonId = parkRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        } else {
            return "";
        }
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

    private String getSelectedDate() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private byte[] imageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}