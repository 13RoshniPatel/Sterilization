package com.beaconblast.btdental.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beaconblast.btdental.R;
import com.beaconblast.btdental.models.res.GetDynamicLists;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Jagtar Singh on 2021-05-17.
 */
public class customPouchListAdapter extends RecyclerView.Adapter<customPouchListAdapter.ViewHolder> {
        private onServiceListListener onServiceListListener;
        private Context context;
        private String title, id ,status;
        ArrayList<GetDynamicLists> allServiceList;
        private  ArrayList<Integer> itemPosition = new ArrayList<>();

        //constructor
        public customPouchListAdapter(Context context,ArrayList<GetDynamicLists> allServiceList,ArrayList<Integer> itemPosition,String title, String id, String status,onServiceListListener listListener) {
            //storing data
            this.context = context;
            this.onServiceListListener = listListener;
            this.title = title;
            this.id = id;
            this.status = status;
            this.allServiceList = allServiceList;
            if(itemPosition.size() > 0)
            this.itemPosition.addAll(itemPosition);
        }

        @NonNull
        @Override
        //onCreateViewHolder is called when an instance of ViewHolder is created
        public customPouchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_dialog_list,parent,false);
            return new ViewHolder(view, onServiceListListener);
        }

        @Override
        //onBindViewHolder is called when to bind the actual data with the viewHolder
        public void onBindViewHolder(@NonNull customPouchListAdapter.ViewHolder holder, int position) {
            holder.reasonName.setText(this.allServiceList.get(position).getPouchInstruments());
            if(this.itemPosition.size() > 0)
            {
                if(itemPosition.contains(position))
                {
                    holder.serviceCheck.setChecked(true);
                }
                else {
                    holder.serviceCheck.setChecked(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return allServiceList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
            //layout fields from singleitem_sho_autoclaves.xml
            // RecyclerView childRecycler;
            //  ImageView deleteItem;
            TextView reasonName;
            CheckBox serviceCheck;
            onServiceListListener onServiceListListener;

            public ViewHolder(@NonNull View itemView, final onServiceListListener listener) {
                super(itemView);
                reasonName= itemView.findViewById(R.id.reasonName);
                this.onServiceListListener = listener;
                serviceCheck = itemView.findViewById(R.id.serviceCheck);
                reasonName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(serviceCheck.isChecked())
                        onServiceListListener.onListItemClick(getAdapterPosition(),title,id,"checked");
                        else
                            onServiceListListener.onListItemClick(getAdapterPosition(),title,id,"nonChecked");
                    }
                });

                serviceCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(serviceCheck.isChecked())
                            onServiceListListener.onListItemClick(getAdapterPosition(),title,id,"checked");
                        else
                            onServiceListListener.onListItemClick(getAdapterPosition(),title,id,"nonChecked");
                    }
                });
            }

            @Override
            public void onClick(View v) {
                onServiceListListener.onListItemClick(getAdapterPosition(),title,id,status);
            }
        }
        //interface for click listener
        public interface onServiceListListener{
            void onListItemClick(int position, String title, String i, String status);
        }

}

