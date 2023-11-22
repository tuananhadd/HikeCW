package com.example.hikecw;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HikeListAdapter extends BaseAdapter {
    private Context context;
    private List<Hike> hikeList;
    private DatabaseHelper dbHelper;

    public HikeListAdapter(Context context, List<Hike> hikeList) {
        this.context = context;
        this.hikeList = hikeList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return hikeList.size();
    }

    @Override
    public Object getItem(int position) {
        return hikeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return hikeList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.hike_list_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button updateButton = convertView.findViewById(R.id.updateButton);
        Button viewButton = convertView.findViewById(R.id.viewButton);

        final Hike hike = hikeList.get(position);

        titleTextView.setText(hike.getTitle());
        locationTextView.setText(hike.getLocation());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHike(hike.getId(), hike);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditHikeActivity.class);
                intent.putExtra("HIKE_ID", hike.getId());
                context.startActivity(intent);
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHike(hike);
            }
        });

        return convertView;
    }

    private void deleteHike(long hikeId, Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                DatabaseHelper.TABLE_HIKES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(hikeId)}
        );

        db.close();

        if (rowsDeleted > 0) {
            hikeList.remove(hike);
            notifyDataSetChanged();
            Toast.makeText(context, "Hike deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error deleting hike", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHike(Hike hike) {
        Intent intent = new Intent(context, EditHikeActivity.class);
        intent.putExtra("HIKE_ID", hike.getId());
        context.startActivity(intent);
    }

    private void viewHike(Hike hike) {
        Intent intent = new Intent(context, ViewHikeActivity.class);
        intent.putExtra("HIKE_ID", hike.getId());
        context.startActivity(intent);
    }

    public void setData(List<Hike> data) {
        hikeList = data;
    }

}