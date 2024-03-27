package com.beaconblast.btdental.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.beaconblast.btdental.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Jagtar Singh on 2021-01-28.
 */
public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private Context cc;
    // private ArrayList<GenericInstruments> instruments;
    private HashMap<String, Integer> ss;
    private int position;
    ArrayList<String> keys = new ArrayList<>();
    private onItemAddListener mOnIncListener;
    ArrayList<String> values = new ArrayList<>();
    int i= 0;

    public ChildAdapter(Context cc,int post, HashMap<String, Integer> ss)  {
        this.cc = cc;
        this.ss = ss;
        this.position = post;
        this.mOnIncListener = (onItemAddListener) cc;
        for (Map.Entry<String, Integer> entry : ss.entrySet()) {
             String key = entry.getKey();
             String value = entry.getValue().toString();
            this.keys.add(entry.getKey());
            this.values.add(entry.getValue().toString());
//            Log.d("newitems", "this is child " + key + " " + value);

        }
    }

    public ChildAdapter(Context cc, HashMap<String, Integer> ss)  {
        this.cc = cc;
        this.ss = ss;
        this.mOnIncListener = (onItemAddListener) cc;
        for (Map.Entry<String, Integer> entry : ss.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            this.keys.add(entry.getKey());
            this.values.add(entry.getValue().toString());
//            Log.d("newitems", "this is child " + key + " " + value);

        }
    }

    @NonNull
    @Override
    public ChildAdapter.ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cc).inflate(R.layout.single_packet_child_row,parent,false);
        return new ChildViewHolder(view,mOnIncListener, this.position, this.keys);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ChildViewHolder holder, int position) {
//        Log.d("child", " This is child running" +  ss.entrySet().size());
        if( this.i < ss.entrySet().size())
        {
//            Log.d("child", " This is child running" +  ss.entrySet().size());
            holder.ins_name.setText(this.keys.get(i));
            holder.count.setText(this.values.get(i));
            this.i++;
        }
    }

    @Override
    public int getItemCount() {
//        Log.d("newitems", "list size is " + ss.size());
        return ss.size();

    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView ins_name, count;
        Button inc, dec;

        public ChildViewHolder(@NonNull View itemView,  final onItemAddListener listener, int position, ArrayList<String> keys) {
            super(itemView);

            ins_name = itemView.findViewById(R.id.ins_name);
            count = itemView.findViewById(R.id.ins_count);
            inc = itemView.findViewById(R.id.incbtn);
            dec = itemView.findViewById(R.id.decbtn);
            inc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d("child", ins_name.getText().toString() + " is it true " + keys.contains(ins_name.getText().toString()));
                    listener.onIncclick(getAdapterPosition(), position, ins_name.getText().toString(), "increase");
                }
            });

            dec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onIncclick(getAdapterPosition(), position, ins_name.getText().toString(), "decrease");
                }
            });
        }
    }

    //interface for click listener
    public interface onItemAddListener {
        void onIncclick(int position, int pos2, String insName, String IncDec);
    }
}