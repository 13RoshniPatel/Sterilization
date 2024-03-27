package com.beaconblast.btdental.customTagsActivities;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaconblast.btdental.R;
import com.beaconblast.btdental.adapters.ChildAdapter;
import com.beaconblast.btdental.adapters.ShowArticlesByTagAdapter;
import com.beaconblast.btdental.models.res.GetArticlesByTagId;
import com.beaconblast.btdental.util.ConstantNaming;
import com.beaconblast.btdental.util.SharedPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomTagsAdapter extends RecyclerView.Adapter<CustomTagsAdapter.ViewHolder> implements Filterable {

    ArrayList<GetArticlesByTagId> itemList = new ArrayList<>();
    HashMap<String, Integer> pouchItems;
    private ArrayList<GetArticlesByTagId> dataList;
    private ArrayList<GetArticlesByTagId> dataListFull;
    private Context context;
    private Resources res;
    int pos;
    private OnCheckboxClickListener checkboxClickListener;

    //constructor for pouch items
    public CustomTagsAdapter(Context context, HashMap<String, Integer> pouchItems, ArrayList<GetArticlesByTagId> dataList,
                             OnCheckboxClickListener checkboxClickListener) {
        //storing data
        this.context = context;
        this.checkboxClickListener = checkboxClickListener;
        this.dataList = dataList;
        this.pouchItems = pouchItems;
        this.dataListFull = new ArrayList<>(dataList);
        res = context.getResources();
    }

    public CustomTagsAdapter(Context context,HashMap<String, Integer> pouchItems,
                             int pos, ArrayList<GetArticlesByTagId> dataList, OnCheckboxClickListener checkboxClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.dataListFull = new ArrayList<>(dataList);
        res = context.getResources();
        this.pouchItems = pouchItems;
        this.checkboxClickListener = checkboxClickListener;
        this.pos = pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitem_get_article_by_tag, parent, false);
        return new ViewHolder(view, checkboxClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetArticlesByTagId item = dataList.get(position);

        String splited = dataList.get(position).getType().toLowerCase().split(" ")[0];

       // holder.textView.setText(item.getType().toUpperCase());
        if (splited.equals("pouches")) {
            holder.itemCounter.setVisibility(View.GONE);
            holder.bb.setVisibility(View.VISIBLE);
        }else {
            holder.itemCounter.setVisibility(View.VISIBLE);
            holder.bb.setVisibility(View.GONE);
        }

        holder.bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pp = holder.getAdapterPosition();
                if (pp != RecyclerView.NO_POSITION) {
                    checkboxClickListener.onButtonClick(pp);
                    Log.d("Roshani","Adapter click position " + pp);
                }
            }
        });

        setPouchInstruments(holder.childRecycler, holder.getAdapterPosition(), pouchItems);

        //item counter
        holder.inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.incrementCounter();
                holder.count.setText(String.valueOf(item.getCounter()));
            }
        });

        holder.dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.decrementCounter();
                holder.count.setText(String.valueOf(item.getCounter()));
            }
        });

        holder.articleType.setText(dataList.get(position).getArticleType().toUpperCase());
        holder.aType.setText(dataList.get(position).getType());
        holder.articleManu.setText(res.getString(R.string.tagId) + " " + dataList.get(position).getTagId());
        holder.staffName.setText(res.getString(R.string.by) + " " + SharedPrefs.getSharedPrefs(ConstantNaming.c_staffName, context).split(" ")[0]);

        holder.checkBox.setChecked(item.isSelected());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(((CheckBox) v).isChecked());
            }
        });

        //read before space
        String type = dataList.get(position).getType().toLowerCase().split(" ")[0];
//         Log.d("adapter", "onBindViewHolder: " + type);
        if (type.equals(ConstantNaming.c_biological_indicator)) {
//            Log.d("adapter", "bi: " + type);
            holder.itemIcon.setBackgroundResource(R.drawable.ic_icon_item_bi);
        } else if (type.equals(ConstantNaming.c_class_5_with_hypehn)) {
            holder.itemIcon.setBackgroundResource(R.drawable.ic_icon_item_c5);
        } else if (type.equals(ConstantNaming.c_class_2_with_hyphen)) {
            holder.itemIcon.setBackgroundResource(R.drawable.ic_icon_item_c2);
        } else if (splited.equals(ConstantNaming.c_pouches)) {
            holder.itemIcon.setBackgroundResource(R.drawable.ic_icon_item_pouch);
        } else {
//            Log.d("bi", "other: " + type);
            holder.itemIcon.setBackgroundResource(R.drawable.ic_icon_item_instrument);
        }
        //changing the list items background color
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#f0efeb"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#f0efeb"));
        }
        type = dataList.get(position).getType().toLowerCase().split(" ")[0];
        ;
        //cahnge color according to type
        if (type.equals(ConstantNaming.c_class_5_with_hypehn) || type.equals(ConstantNaming.c_class_2_with_hyphen) || type.equals(ConstantNaming.c_class_4_with_hypehn) || type.equals(ConstantNaming.c_biological_indicator)) {
            holder.articleType.setTextColor(Color.parseColor("#00afb9"));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public Filter getFilter() {
        return dataListFilter;
    }

    private Filter dataListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterPattern = constraint.toString().toLowerCase().trim();
            List<GetArticlesByTagId> filteredList = new ArrayList<>();

            if (filterPattern == null || filterPattern.length() == 0) {
                filteredList.addAll(dataList);
            } else {
                for (GetArticlesByTagId item : dataList) {
                    if (item.getType().toLowerCase().contains(filterPattern)) {
                        Log.d("CustomTagActivity","items "+item.getType());
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataListFull.clear();
            dataListFull.addAll((List<GetArticlesByTagId>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, articleType, aType, articleManu, staffName, count;
        CheckBox checkBox;
        ImageView itemIcon;
        Button bb, inc, dec;
        RecyclerView childRecycler;
        LinearLayout itemCounter;

        public ViewHolder(@NonNull View itemView, OnCheckboxClickListener listener) {
            super(itemView);
//            textView = itemView.findViewById(R.id.list_item_name);
            checkBox = itemView.findViewById(R.id.checkBox);

            articleType = itemView.findViewById(R.id.article_type);
            aType = itemView.findViewById(R.id.a_type);
            articleManu = itemView.findViewById(R.id.article_manu);
            staffName = itemView.findViewById(R.id.staff_name);
            bb = itemView.findViewById(R.id.addint);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            childRecycler = itemView.findViewById(R.id.instruemtnrecycle);
            itemCounter = itemView.findViewById(R.id.itemCounts);
            count = itemView.findViewById(R.id.ins_count);
            inc = itemView.findViewById(R.id.incbtn);
            dec = itemView.findViewById(R.id.decbtn);

            itemCounter.setVisibility(View.GONE);
            bb.setVisibility(View.GONE);

//            bb.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pp = getAdapterPosition();
//                    if (pp != RecyclerView.NO_POSITION) {
//                        listener.onButtonClick(pp);
//                        Log.d("Roshani","adapter pos: "+ pp);
//                    }
//                }
//            });

            checkBox.setVisibility(View.VISIBLE);
            articleManu.setVisibility(View.INVISIBLE);

            // Set listener for checkbox clicks
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    int pp = getAdapterPosition();
                    if (pp != RecyclerView.NO_POSITION) {
                        listener.onCheckboxClicked(pp, isChecked);
                    }
                }
            });

        }
    }

    public interface OnCheckboxClickListener {
        void onCheckboxClicked(int position, boolean isChecked);

        void onButtonClick(int position);
    }

    // Method to set the click listener
    public void setOnButtonClickListener(OnCheckboxClickListener listener) {
        this.checkboxClickListener = listener;
    }

    //pouch logic starts here
    private void setPouchInstruments(RecyclerView recyclerView, int post, HashMap<String, Integer> ssa) {
        ChildAdapter childAdapter = new ChildAdapter(context, post, ssa);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(childAdapter);
        childAdapter.notifyItemChanged(post);
    }
}