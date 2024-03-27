package com.beaconblast.btdental.customTagsActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaconblast.btdental.R;
import com.beaconblast.btdental.ShowLoadActivity;
import com.beaconblast.btdental.WelcomeActivity;
import com.beaconblast.btdental.adapters.ChildAdapter;
import com.beaconblast.btdental.adapters.ShowArticlesByTagAdapter;
import com.beaconblast.btdental.adapters.customPouchListAdapter;
import com.beaconblast.btdental.models.res.GetArticlesByTagId;
import com.beaconblast.btdental.models.res.GetDynamicLists;
import com.beaconblast.btdental.models.res.doneMessage;
import com.beaconblast.btdental.models.send.SendLoadData;
import com.beaconblast.btdental.util.ConstantNaming;
import com.beaconblast.btdental.util.LogOutUser;
import com.beaconblast.btdental.util.SharedPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.beaconblast.btdental.util.ConstantNaming.c_bi_shared_prefs;
import static com.beaconblast.btdental.util.ConstantNaming.c_biological_indicator;
import static com.beaconblast.btdental.util.ConstantNaming.c_class2_shared_prefs;
import static com.beaconblast.btdental.util.ConstantNaming.c_class_2_with_hyphen;
import static com.beaconblast.btdental.util.ConstantNaming.c_class_5_with_hypehn;
import static com.beaconblast.btdental.util.ConstantNaming.c_type5_shared_prefs;

public class CustomTagListActivityHelper extends AppCompatActivity implements ChildAdapter.onItemAddListener, CustomTagsAdapter.OnCheckboxClickListener,
        customPouchListAdapter.onServiceListListener {

    public doneMessage dd = new doneMessage();
    public SharedPreferences sp;
    public Resources res;
    //xml fields
    public TextView showError, showStaffName;
    public boolean AddToAutoclavePressed = false;
    public HashMap<String, Integer> singleHash = new HashMap<>();
    public ArrayList<GetDynamicLists> all_items = new ArrayList<>();
    public ArrayList<Integer> selectedItems = new ArrayList<>();
    public boolean[] checkItems;
    //variable to send through API
    public int bi_test_needed;
    public int user_id;
    public Integer type5_loaded = 0;
    public String type5_tag_id;
    public Integer class2_loaded = 0;
    public String class2_tagid;
    public Integer class2_loadedby;
    public Integer bi_loaded = 0;
    public String bi_tag_id;
    public Integer bi_loaded_by;
    public Integer type5_loaded_by;
    public Integer is_pouch = 0;
    public String generic_articles = " ";
    public Boolean pouchFound = false;
    boolean bagcoming;
    public RecyclerView serviceListRecycler;
    public customPouchListAdapter adapter;
    //New SendLoadData Model
    public SendLoadData sendloaddata;
    public Boolean isC5inCurrentLoad = false;
    public Boolean isBIinCurrentLoad = false;
    public Boolean isC2inCurrentLoad = false;
    //intent data variables
    public String sterilizer_id;
    public String sterilizer_status_loaded;
    public String sterilizer_status;
    //by default implant is not required
    public String hold_for_bi = "no";
    public String hold_for_bi_tag = "";
    //dialog recycler view
    public Parcelable recyclerViewState;
    //auto log out user
    public LogOutUser ll = new LogOutUser();
    //////////////
    public boolean homeButtonPressed = false;
    public boolean backButtonPressed = false;
    public boolean load_not_allowed = false;
    //Array model for recycler view
    public ArrayList<GetArticlesByTagId> getArticlesByTag_ad = new ArrayList<>();
    public ArrayList<GetArticlesByTagId> tempTagStore = new ArrayList<>();
    public AlertDialog customList;
    SearchView searchView;
    CustomTagsAdapter customTagsAdapter;
    RecyclerView mRecyclerView;
    TextView showTotalCount;
    String TAG = "CustomTagActivity";
    Button addItemsToAv;
    Activity activity;
    Context context;
    ArrayList<GetArticlesByTagId> pouchesList = new ArrayList<>();
    ArrayList<GetArticlesByTagId> otherItemsList = new ArrayList<>();

    public void getActivityContext(Context context, Activity activity, String st_id, String st_status) {
        this.context = context;
        this.activity = activity;
        res = getResources();
    }

    //UI Button for inserting a BI and C5
    public void onIndicatorCheckBoxClick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
//        if(checked){
//            scanItemImage.setVisibility(View.GONE);
//            scanImageTitle.setVisibility(View.GONE);
//            scanImageDesc.setVisibility(View.GONE);
//            addItemsToAv.setVisibility(View.VISIBLE);
//        }
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_insertC5:
                addItemsToAv.setVisibility(View.VISIBLE);
                //GETTING DUMMY DATA FOR C5
                GetArticlesByTagId getC5Data = new GetArticlesByTagId(c_class_5_with_hypehn, getApplicationContext());
                if (checked) {

                    if (!sp.contains(c_type5_shared_prefs + sterilizer_id) || SharedPrefs.getSharedPrefs(c_type5_shared_prefs + sterilizer_id, getApplicationContext()).equals("0")) {
                        type5_loaded = 1; //putting check
                        type5_tag_id = "dummytagid"; //storing tag
                        type5_loaded_by = user_id; //storing who loaded it
                        Log.d(TAG, "c5" + getC5Data.getType());
                        isC5inCurrentLoad = true; // putting check
                        tempTagStore.add(getC5Data);
                        showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
                    } else {
                        customDialogOneButton(res.getString(R.string.c5AlreadyLoaded), res.getString(R.string.onlyOneC5PerLoad), "red");
                    }
                } else {
                    type5_loaded = 0;
                    type5_tag_id = "dummytagid";
                    type5_loaded_by = 0;
                    for (int i = 0; i < tempTagStore.size(); i++) {
                        if (tempTagStore.get(i).getType().equals(c_class_5_with_hypehn.toUpperCase())) {
                            tempTagStore.remove(i);
                            showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
                            break;
                        }
                    }
                    isC5inCurrentLoad = false;
                }
                // Remove the meat
                break;
            case R.id.checkbox_insertBI:
                addItemsToAv.setVisibility(View.VISIBLE);
                if (checked) {
                    //getting dummy data for BI
                    GetArticlesByTagId getBIData = new GetArticlesByTagId(c_biological_indicator, getApplicationContext());
                    if (!sp.contains(c_bi_shared_prefs + sterilizer_id) || SharedPrefs.getSharedPrefs(c_bi_shared_prefs + sterilizer_id, getApplicationContext()).equals("0")) {
                        bi_loaded = 1;
                        bi_tag_id = "dummytagid";
                        bi_loaded_by = user_id;
                        isBIinCurrentLoad = true;
                        tempTagStore.add(getBIData);
                        showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
                        Log.d(TAG, "BI " + getBIData.getType());
                        Log.d("isbi", bi_loaded + " " + " " + bi_tag_id + " ");
                    } else {
                        //showing this on second BI scan
                        customDialogOneButton(res.getString(R.string.biAlreadyLoaded), res.getString(R.string.onlyOneBIPerLoad), "red");
                    }
                } else {
                    bi_loaded = 0; //putting check
                    bi_tag_id = "";
                    bi_loaded_by = 0; //storing who loaded it
                    for (int i = 0; i < tempTagStore.size(); i++) {
                        if (tempTagStore.get(i).getType().equals(ConstantNaming.c_bi_24hour)) {
                            tempTagStore.remove(i);
                            showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
                            break;
                        }
                    }
                    isBIinCurrentLoad = false; //putting check
                }
                break;
            case R.id.checkbox_insertC2:
                addItemsToAv.setVisibility(View.VISIBLE);
                //getting dummy data for C2
                GetArticlesByTagId getC2Data = new GetArticlesByTagId(c_class_2_with_hyphen, getApplicationContext());
                if (checked) {
                    if (!sp.contains("class2" + sterilizer_id) || SharedPrefs.getSharedPrefs(c_class2_shared_prefs + sterilizer_id, getApplicationContext()).equals("0")) {
                        class2_loaded = 1;
                        class2_tagid = "dummydata";
                        class2_loadedby = user_id;
                        Log.d(TAG, "c2 " + getC2Data.getType());
                        isC2inCurrentLoad = true;
                        tempTagStore.add(getC2Data);
                        showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
                    } else {
                        customDialogOneButton(res.getString(R.string.c2AlreadyLoaded), res.getString(R.string.pleaseLoadNewOneDialog), "red");
                    }
                } else {
                    class2_loaded = 0; //putting check
                    class2_tagid = "";
                    class2_loadedby = 0; //storing who loaded it
                    for (int i = 0; i < tempTagStore.size(); i++) {
                        if (tempTagStore.get(i).getType().equals(ConstantNaming.c_class_2_with_hyphen)) {
                            tempTagStore.remove(i);
                            showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
                            break;
                        }
                    }
                    isBIinCurrentLoad = false; //putting check

                }
                break;
        }
    }

    public void customDialogOneButton(String title, String description, String iconColor) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate and set the layout for the dialog
        View view = inflater.inflate(R.layout.customdialog_ok, null);
        builder.setView(view);
        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        TextView customTitle = (TextView) view.findViewById(R.id.customTitle);
        TextView customDesc = (TextView) view.findViewById(R.id.customDescription);
        ImageView dialogLogo = view.findViewById(R.id.dialogLogo2);
        if (iconColor.equals("red"))
            dialogLogo.setBackgroundResource(R.drawable.ic_dialog_alert_icon_red);
        else
            dialogLogo.setBackgroundResource(R.drawable.ic_dialog_alert_icon);
        customTitle.setText(title);
        customDesc.setText(description);
        android.app.AlertDialog add = builder.create();
        add.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        add.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.customConfirmButton(customDialog.this);
                if (load_not_allowed) {
                    load_not_allowed = false;
                    Intent i = new Intent(context, ShowLoadActivity.class);
                    i.putExtra("id", sterilizer_id);
                    i.putExtra("status", sterilizer_status);
                    context.startActivity(i);
                }
                add.dismiss();
            }
        });
    }

    //custom dialog builder
    public void customDialogConfirm(String title, String description, String iconColor) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // Get the layout inflater
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate and set the layout for the dialog
        View view = inflater.inflate(R.layout.customdialog, null);
        builder.setView(view);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        TextView customTitle = (TextView) view.findViewById(R.id.customTitle);
        TextView customDesc = (TextView) view.findViewById(R.id.customDescription);
        ImageView dialogLogo = view.findViewById(R.id.dialogLogo);
        if (iconColor.equals("red"))
            dialogLogo.setBackgroundResource(R.drawable.ic_dialog_alert_icon_red);
        else
            dialogLogo.setBackgroundResource(R.drawable.ic_dialog_alert_icon);
        customTitle.setText(title);
        customDesc.setText(description);

        // builder.create().show();
        android.app.AlertDialog add = builder.create();
        add.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        add.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if click is comign from home button
                if (homeButtonPressed) {
                    homeButtonPressed = false;
                }
                if (backButtonPressed) {
                    backButtonPressed = false;
                }
                add.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if click is coming from home button
                if (homeButtonPressed) {
                    homeButtonPressed = false;
                    Intent i = new Intent(context, WelcomeActivity.class);
                    context.startActivity(i);
                }
                if (backButtonPressed) {
                    backButtonPressed = false;
                    Intent i = new Intent(context, ShowLoadActivity.class);
                    i.putExtra("id", sterilizer_id);
                    i.putExtra("status", sterilizer_status);
                    context.startActivity(i);
                }
                add.dismiss();
            }
        });

    }

    @Override
    public void onIncclick(int position, int pos2, String insName, String IncDec) {
        int index = -1;
        for (int y = 0; y < all_items.size(); y++) {
            if (all_items.get(y).getPouchInstruments().equals(insName))
                index = y;
        }

        if (index != -1) {
            if (IncDec.equals("increase")) {
                if (singleHash.containsKey(all_items.get(index).getPouchInstruments())) {
                    singleHash.put(all_items.get(index).getPouchInstruments(), singleHash.get(all_items.get(index).getPouchInstruments()) + 1);
                }
            } else if (IncDec.equals("decrease")) {
                if (singleHash.containsKey(all_items.get(index).getPouchInstruments())) {
                    singleHash.put(all_items.get(index).getPouchInstruments(), singleHash.get(all_items.get(index).getPouchInstruments()) - 1);
                    if (Objects.equals(singleHash.get(all_items.get(index).getPouchInstruments()), 0)) {
                        singleHash.remove(all_items.get(index).getPouchInstruments());

                        if (selectedItems.contains(Integer.valueOf(index))) {
                            selectedItems.remove(Integer.valueOf(index));
                            checkItems[index] = false;
                        }
                    }
                }
            }
            CustomTagsAdapter adapter = new CustomTagsAdapter(context, singleHash, pouchesList, this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(int position, String title, String i, String status) {
//on selecting an item from a list, add it to hashmap and increment it
        try {
            if (!checkItems[position]) {
                if (singleHash.containsKey(all_items.get(position).getPouchInstruments())) {
                    singleHash.put(all_items.get(position).getPouchInstruments(),
                            singleHash.get(all_items.get(position).getPouchInstruments()) + 1);
                } else {
                    singleHash.put(all_items.get(position).getPouchInstruments(), 1);
                }
                selectedItems.add(position);
                checkItems[position] = true;

                //on deselecting an item from the list, remove it entirely from the list
            } else {
                singleHash.remove(all_items.get(position).getPouchInstruments());
                if (selectedItems.contains(position))
                    selectedItems.remove(Integer.valueOf(position));
                checkItems[position] = false;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //storing the current scroll position
        for (Map.Entry<String, Integer> entry : singleHash.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
        }
        recyclerViewState = serviceListRecycler.getLayoutManager().onSaveInstanceState();
        //refresh the adapter
        if (all_items.size() == 0) {
            Toast.makeText(getApplicationContext(), "List empty", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new customPouchListAdapter(getApplicationContext(), all_items, selectedItems, title, "na", "na", this::onListItemClick);
            adapter.notifyItemChanged(position);
            serviceListRecycler.setAdapter(adapter);
            //restoring the recycler scroll position
            serviceListRecycler.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
    }

    //recyclerview checkbox handle function
    @Override
    public void onCheckboxClicked(int position, boolean isChecked) {
        addItemsToAv.setVisibility(View.VISIBLE);
        if (!bagcoming){
            if (isChecked) {
                if (!tempTagStore.contains(otherItemsList.get(position))) {
                    tempTagStore.add(otherItemsList.get(position));
                }
                otherItemsList.get(position).setSelected(true);
            } else {
                tempTagStore.remove(otherItemsList.get(position));
                otherItemsList.get(position).setSelected(false);
            }
            showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
        }else {
            if (isChecked) {
                if (!tempTagStore.contains(pouchesList.get(position))) {
                    tempTagStore.add(pouchesList.get(position));
                }
                pouchesList.get(position).setSelected(true);
            } else {
                tempTagStore.remove(pouchesList.get(position));
                pouchesList.get(position).setSelected(false);
            }
            showTotalCount.setText(getResources().getString(R.string.totalCount) + " " + tempTagStore.size());
        }

    }

    @Override
    public void onButtonClick(int position) {
        Log.d(TAG,"pos "+ position);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        View view = inflater.inflate(R.layout.custom_dialog_listview, null);
        //recyclerview layout
        serviceListRecycler = view.findViewById(R.id.serviceListRecycler);
        builder.setView(view);
        if (all_items.isEmpty() || all_items.size() == 0) {
            Toast.makeText(getApplicationContext(), "List empty", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new customPouchListAdapter(getApplicationContext(), all_items, selectedItems, getResources().getString(R.string.selectIns),
                    "na", "na", this);
            //adding data to recycler view
            serviceListRecycler.setAdapter(adapter);
            serviceListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        TextView pouchListTitle = view.findViewById(R.id.hamMenuTitle);
        customList = builder.create();
        customList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customList.show();
        pouchListTitle.setText(R.string.selectIns);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateArticleByTagAdapter(position);
                customList.dismiss();
            }
        });
    }

    private void updateArticleByTagAdapter(int position) {
        CustomTagsAdapter adapter = new CustomTagsAdapter(context, singleHash, position, pouchesList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyItemChanged(position);
    }
}
