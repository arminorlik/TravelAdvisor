package com.example.norbert.routespreparation2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListDataActivity extends AppCompatActivity {

    public static final String TAG = "ListDataActivity";
    @BindView(R.id.mListView)
    ListView mListView;
    private DatabaseHelper mDatabaseHelper;
    private Cursor data;
    private ArrayList<RowModel> listData;
    private ArrayList<RowModel> listDataClicked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listt_layout);
        ButterKnife.bind(this);
        mDatabaseHelper = new DatabaseHelper(this);

        data = mDatabaseHelper.getData();
        listData = new ArrayList<>();
        listDataClicked = new ArrayList<>();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                data.moveToPosition(i);

                RowModel rowModel = new RowModel(data.getString(1), data.getString(2), data.getString(3));
                Toast.makeText(getApplicationContext(),rowModel.getStartPos() , Toast.LENGTH_SHORT).show();
            }
        });

        populateListView();
    }

    private void populateListView() {

        while (data.moveToNext()) {
            listData.add(new RowModel(data.getString(1), data.getString(2), data.getString(3)));
        }

        ListDataAdapter adapter = new ListDataAdapter(this, R.layout.adapter_view_layout, listData);
        mListView.setAdapter(adapter);
    }
}

