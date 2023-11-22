package com.example.hikecw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView hikeListView;
    private List<Hike> hikeList;
    private HikeListAdapter adapter;
    private DatabaseHelper dbHelper;
    private EditText searchEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hikeListView = findViewById(R.id.hikeListView);
        hikeList = new ArrayList<>();
        adapter = new HikeListAdapter(this, hikeList);
        dbHelper = new DatabaseHelper(this);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        loadHikesFromDatabase();

        hikeListView.setAdapter(adapter);

        hikeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hike selectedHike = hikeList.get(position);
                editHike(selectedHike);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString().trim();

                if (!query.isEmpty()) {
                    List<Hike> searchResults = dbHelper.searchHikes(query);

                    if (searchResults.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No results found.", Toast.LENGTH_SHORT).show();
                    } else {
                        hikeList.clear();
                        hikeList.addAll(searchResults);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void loadHikesFromDatabase() {
        hikeList.clear(); // Clear the current list
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_HIKES,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String park = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PARKING));
                int length = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LENGTH));
                String level = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL));
                int pay = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PAY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

                Hike hike = new Hike();
                hike.setId(id);
                hike.setTitle(title);
                hike.setLocation(location);
                hike.setDate(date);
                hike.setPark(park);
                hike.setLength(length);
                hike.setLevel(level);
                hike.setPay(pay);
                hike.setDescription(description);
                hike.setImage(image);

                hikeList.add(hike);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        adapter.notifyDataSetChanged();
    }


    public void addHike(View view) {
        Intent intent = new Intent(this, AddHikeActivity.class);
        startActivity(intent);
    }

    public void editHike(Hike hike) {
        Intent intent = new Intent(this, EditHikeActivity.class);
        intent.putExtra("HIKE_ID", hike.getId());
        startActivity(intent);
    }

    public void deleteHike(View view) {
        View parent = (View) view.getParent();
        int position = hikeListView.getPositionForView(parent);
        Hike hike = hikeList.get(position);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                DatabaseHelper.TABLE_HIKES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(hike.getId())}
        );

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Hike deleted successfully", Toast.LENGTH_SHORT).show();
            hikeList.remove(position);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Error deleting hike", Toast.LENGTH_SHORT).show();
        }
    }

    public void Refresh(View view) {
        loadHikesFromDatabase();
    }

    public void searchHikes(View view) {
        EditText searchEditText = findViewById(R.id.searchEditText);
        String query = searchEditText.getText().toString().trim();

        if (!query.isEmpty()) {
            List<Hike> searchResults = dbHelper.searchHikes(query);

            if (searchResults.isEmpty()) {
                Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
            } else {
                hikeList.clear();
                hikeList.addAll(searchResults);
                adapter.notifyDataSetChanged();
            }
        }
    }
}