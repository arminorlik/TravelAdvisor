package com.example.norbert.routespreparation2;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



class ListDataAdapter extends ArrayAdapter<RowModel> {

private static final String TAG = "ListDataAdapter";
    private Context mContext;
    int mResource;

    public ListDataAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<RowModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RowModel model = getItem(position);
        String StartP = model.getStartPos();
        String StopP = model.getStopPos();
        String CzasP = model.getCzasPod();



        LayoutInflater inflater =  LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent, false);


        TextView tvStart = (TextView) convertView.findViewById(R.id.textView1);
        TextView tvStop = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvCzas = (TextView) convertView.findViewById(R.id.textView3);

        tvStart.setText(StartP);
        tvCzas.setText(CzasP);
        tvStop.setText(StopP);

        return convertView;
    }
}
