package com.beaconblast.btdental.customTagsActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconblast.btdental.R;
import com.beaconblast.btdental.ReadRfid;
import com.beaconblast.btdental.ShowLoadActivity;
import com.beaconblast.btdental.StaffLoginActivity;
import com.beaconblast.btdental.WelcomeActivity;
import com.beaconblast.btdental.helpers.LogOutDialog;
import com.beaconblast.btdental.models.res.GetArticlesByTagId;
import com.beaconblast.btdental.models.res.GetDynamicLists;
import com.beaconblast.btdental.util.ConstantNaming;
import com.beaconblast.btdental.util.LogoutTimer;
import com.beaconblast.btdental.util.ReproCalls;
import com.beaconblast.btdental.util.RestApiPoints;
import com.beaconblast.btdental.util.SharedPrefs;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.beaconblast.btdental.util.ConstantNaming.c_ACCESS_TOKEN;
import static com.beaconblast.btdental.util.ConstantNaming.c_STAFF_TOKEN;
import static com.beaconblast.btdental.util.ConstantNaming.c_bi_24hour;
import static com.beaconblast.btdental.util.ConstantNaming.c_bi_shared_prefs;
import static com.beaconblast.btdental.util.ConstantNaming.c_biological_indicator;
import static com.beaconblast.btdental.util.ConstantNaming.c_class2_shared_prefs;
import static com.beaconblast.btdental.util.ConstantNaming.c_class_2_with_hyphen;
import static com.beaconblast.btdental.util.ConstantNaming.c_class_5_with_hypehn;
import static com.beaconblast.btdental.util.ConstantNaming.c_implant_shared_prefs;
import static com.beaconblast.btdental.util.ConstantNaming.c_type5_shared_prefs;

public class CustomTagListActivity extends CustomTagListActivityHelper implements ReproCalls.onListFetch, LogoutTimer.onTimerFinish {

    public String sterilizer_status_loaded;
    public Integer readerLocation = 1;
    public Resources res;
    public int bi_test_needed;
    public int user_id;
    LogoutTimer lgtimer = new LogoutTimer(this);
    String branchLocation;
    int totalCount = 0;
    Activity activity;
    Context context;
    String firstOfTheDay = null;
    private String savedAccessToken;
    private String savedStaffToken;
    private TextView showStaffName;
    private TextView mainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_tag_list);
        res = getResources();
        Intent intent = getIntent();
        savedAccessToken = SharedPrefs.getSharedPrefs(c_ACCESS_TOKEN, getApplicationContext());
        savedStaffToken = SharedPrefs.getSharedPrefs(c_STAFF_TOKEN, getApplicationContext());
        sterilizer_id = intent.getStringExtra("sterilizerId");
        sterilizer_status = intent.getStringExtra("sterilizer_status");
        bagcoming = intent.getBooleanExtra("bag_coming", false);

        //Accessing token from shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String staffName = preferences.getString(ConstantNaming.c_staffName, "fail");
        //showing staff Name on UI
        showStaffName = findViewById(R.id.staff_name);
        showStaffName.setText(staffName);

        searchView = findViewById(R.id.searchView);
        mRecyclerView = findViewById(R.id.show_articles_by_tag);
        addItemsToAv = findViewById(R.id.addItemsToAv);
        showTotalCount = findViewById(R.id.showTotalCount);
        mainTitle = findViewById(R.id.RRfidBoard);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //begin Log out timer
        lgtimer.beginTimer();
        if (ConstantNaming.c_ENVIRONMENT.equalsIgnoreCase("development")) {
            //TestingMethod();
        }

        //helper class
        getActivityContext(CustomTagListActivity.this, CustomTagListActivity.this, sterilizer_id, sterilizer_status);

        Log.d(TAG, "" + SharedPrefs.getSharedPrefs(ConstantNaming.c_the_branch_location, getApplicationContext()));
        branchLocation = SharedPrefs.getSharedPrefs(ConstantNaming.c_the_branch_location, getApplicationContext());

        pouchOrItem(bagcoming);
        getReaderLocation();
        logOutHomeAndBackButton();
        C5OrBIorC2Loaded();

        //THIS ACTIVITY WILL ADD THE LOAD, SO NEED TO CHANGE THE STATUS TO LOADED
        sterilizer_status_loaded = ConstantNaming.c_AUTOCLAVE_LOADED;

        //bi_test needed or not
        bi_test_needed = Integer.parseInt(SharedPrefs.getSharedPrefs(ConstantNaming.c_first_cycle + sterilizer_id, getApplicationContext()));
        firstOfTheDay = bi_test_needed == 1 ? "yes" : "no";

        user_id = Integer.parseInt(SharedPrefs.getSharedPrefs(ConstantNaming.c_current_user_id, getApplicationContext()));

        try {
            getArticles(branchLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set up search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                customTagsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "adapter size: " + customTagsAdapter.getItemCount());
                customTagsAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public void getArticles(String branchId) {
        //dynamically get list of items that go inside the pouch
        try {
            ReproCalls reproCalls = new ReproCalls(getApplicationContext(), this::updateList);
            //ONLY MAKE ANOTHER CALL IF POUCH IS NOT ADDED TO THE LIST YET
            if (!pouchFound)
                reproCalls.getPouchItems();
        } catch (Exception e) {
            Log.d(ConstantNaming.c_exceptionHere, e + "initializing");
        }

        Call<ArrayList<GetArticlesByTagId>> call = RestApiPoints.BTDental.getARegisteredArticleByType("Bearer " + savedAccessToken,
                "Bearer " + savedStaffToken, branchId);
        call.enqueue(new Callback<ArrayList<GetArticlesByTagId>>() {
            @Override
            public void onResponse(Call<ArrayList<GetArticlesByTagId>> call, Response<ArrayList<GetArticlesByTagId>> response) {
                String splitted;
                //get response from the call
                if (response.code() == 200) {
                    assert response.body() != null;

                    getArticlesByTag_ad.addAll(response.body());
                    //call method to perform action according to the item type
                    // Categorize items based on their type
                    for (GetArticlesByTagId item : getArticlesByTag_ad) {

                        splitted = item.getType().toLowerCase().split(" ")[0];

                        if (splitted.equals("pouches")) {
                            pouchFound = true;
                            is_pouch = 1;
                            pouchesList.add(item);
                            Log.d(TAG, "list 1 : " + pouchesList.size());
                            if (bagcoming) {
                                CustomTagsAdapter customTagsAdapter = new CustomTagsAdapter(CustomTagListActivity.this, singleHash,
                                        pouchesList, CustomTagListActivity.this);
                                //adding data to recycler view
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                mRecyclerView.setAdapter(customTagsAdapter);
                                customTagsAdapter.notifyDataSetChanged();
                            }
                        } else if (splitted.equals(ConstantNaming.c_class_2_with_hyphen)) {
                            is_pouch = 0;
                            pouchFound = false;
                            Log.d(TAG, "is a class 2");
                        } else if (splitted.equals(ConstantNaming.c_class_5_with_hypehn)) {
                            is_pouch = 0;
                            pouchFound = false;
                            Log.d(TAG, "is a class 5");
                        } else if (splitted.equals(c_biological_indicator)) {
                            is_pouch = 0;
                            pouchFound = false;
                            Log.d(TAG, "is a BI/Implant");
                        } else {
                            is_pouch = 0;
                            pouchFound = false;
                            otherItemsList.add(item);
                            Log.d(TAG, "list 2 : " + otherItemsList.size());
                            if (!bagcoming) {
                                CustomTagsAdapter customTagsAdapter = new CustomTagsAdapter(CustomTagListActivity.this, singleHash,otherItemsList
                                        ,CustomTagListActivity.this);
                                //adding data to recycler view
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                mRecyclerView.setAdapter(customTagsAdapter);
                                customTagsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                  //  Log.d(TAG, "response  " + getArticlesByTag_ad);
                } else {
                    Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetArticlesByTagId>> call, Throwable t) {

            }
        });
    }

    /**
     * CHECKS IF THE POUCH OR ITEM SCAN IS SELECTED BY USER
     *
     * @param bag_coming boolean;
     */
    void pouchOrItem(Boolean bag_coming) {
        if (bag_coming) {
            mainTitle.setText(R.string.RRfidPouchTitle);
            //INTENT IS FOR ADDING ITEMS
        } else {
            mainTitle.setText(R.string.RRfidTitle);
        }
    }

    /**
     * GET READER LOCATION FROM SHARED PREFS
     */
    void getReaderLocation() {
        try {
            readerLocation = Integer.parseInt(SharedPrefs.getSharedPrefs("theReaderLocation", getApplicationContext()));
        } catch (Exception e) {
            Log.d("exception_here", "onCreate: " + e);
        }
    }

    void logOutHomeAndBackButton() {
        TextView home = findViewById(R.id.mainHome);
        TextView logout = findViewById(R.id.mainLogout);
        Button backButton = findViewById(R.id.mainBack);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        AppBarLayout mainAppbar = findViewById(R.id.mainAppbar);
        setSupportActionBar(mainToolbar);
        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(R.layout.activity_welcome, "elevation", 0));
        mainAppbar.setStateListAnimator(stateListAnimator);
        //clicking log out button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutDialog.confirmLogOut(CustomTagListActivity.this);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeButtonPressed = true;
                customDialogConfirm(res.getString(R.string.exitDialogTitle), res.getString(R.string.exitDialogDesc) +
                        SharedPrefs.getSharedPrefs(ConstantNaming.c_avname + sterilizer_id, getApplicationContext()), "red");
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonPressed = true;
                customDialogConfirm(res.getString(R.string.exitDialogTitle), res.getString(R.string.exitDialogDesc) +
                        SharedPrefs.getSharedPrefs(ConstantNaming.c_avname + sterilizer_id, getApplicationContext()), "red");
            }
        });
    }

    @Override
    public void logOutOnTimeOut() {
        CustomTagListActivity.this.finish();
        Intent i = new Intent(CustomTagListActivity.this, StaffLoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onUserInteraction() {
        lgtimer.restartTimer();
    }

    @Override
    protected void onStop() {
        lgtimer.cancelTimer();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lgtimer.cancelTimer();
        super.onDestroy();
    }

    /**
     * CALLBACK METHOD TO RECEIVE POUCH INSTRUMENTS AND CREATE A BOOLEAN LIST
     * OF THE SAME SIZE
     * TODO method needs some fixing, may not need pouchItems
     *
     * @param pouchItems
     * @param pouchList
     */
    @Override
    public void updateList(String[] pouchItems, ArrayList<GetDynamicLists> pouchList) {
        all_items.clear();
        if (pouchItems != null) {
            try {
                checkItems = new boolean[pouchList.size()];
                if (!pouchList.isEmpty())
                    all_items.addAll(pouchList);
                for (int i = 0; i < all_items.size(); i++) {
                    checkItems[i] = false;
                }
            } catch (Exception e) {
                Log.d("exception_here", e + "");
            }
        }
    }

    /**
     * HANDLES C5, BI AND C2 INDICATOR LOADING
     */
    void C5OrBIorC2Loaded() {
        TextView insertC5 = findViewById(R.id.insertC5);
        TextView insertBI = findViewById(R.id.insertBI);
        TextView insertC2 = findViewById(R.id.insertC2);
        LinearLayout insertIndicatorLayout = findViewById(R.id.insertIndicatorLayout);
        CheckBox checkbox_insertC5 = findViewById(R.id.checkbox_insertC5);
        CheckBox checkbox_insertBI = findViewById(R.id.checkbox_insertBI);
        CheckBox checkBox_insertC2 = findViewById(R.id.checkbox_insertC2);

        //CHECK IF CURRENT AUTOCLAVE NEEDS BOWIE DICK TEST AND TEST IS NOT DONE YET
        if (SharedPrefs.getSharedPrefs(ConstantNaming.c_bowie_av + sterilizer_id, getApplicationContext()).equals("yes") &&
                SharedPrefs.getSharedPrefs(ConstantNaming.c_bowiedone + sterilizer_id, getApplicationContext()).equals("0")) {
            //HIDE BI AND C5 BUT SHOW C2
            insertC5.setVisibility(View.GONE);
            checkbox_insertC5.setVisibility(View.GONE);
            insertBI.setVisibility(View.GONE);
            checkbox_insertBI.setVisibility(View.GONE);
            insertC2.setVisibility(View.VISIBLE);
            checkBox_insertC2.setVisibility(View.VISIBLE);

            //ALSO HIDE C2 CHECKBOX IF C2 IS CURRENTLY LOADED
            if (SharedPrefs.getSharedPrefs(c_class2_shared_prefs + sterilizer_id, getApplicationContext()).equals("1")) {
                insertC2.setVisibility(View.GONE);
                checkBox_insertC2.setVisibility(View.GONE);
            }
        }
        //OTHERWISE HIDE C2 AND SHOW C5 AND BI ACCORDINGLY
        else {
            insertC2.setVisibility(View.GONE);
            checkBox_insertC2.setVisibility(View.GONE);
            if (SharedPrefs.getSharedPrefs(c_type5_shared_prefs + sterilizer_id, getApplicationContext()).equals("1") &&
                    SharedPrefs.getSharedPrefs(c_bi_shared_prefs + sterilizer_id, getApplicationContext()).equals("1")) {
                insertIndicatorLayout.setVisibility(View.GONE);
            }
            if (SharedPrefs.getSharedPrefs(c_type5_shared_prefs + sterilizer_id, getApplicationContext()).equals("1")) {
                insertC5.setVisibility(View.GONE);
                checkbox_insertC5.setVisibility(View.GONE);
            } else {
                insertC5.setVisibility(View.VISIBLE);
                checkbox_insertC5.setVisibility(View.VISIBLE);
            }
            if (SharedPrefs.getSharedPrefs(c_bi_shared_prefs + sterilizer_id, getApplicationContext()).equals("1")) {
                insertBI.setVisibility(View.GONE);
                checkbox_insertBI.setVisibility(View.GONE);
            } else {
                insertBI.setVisibility(View.VISIBLE);
                checkbox_insertBI.setVisibility(View.VISIBLE);
            }
        }
    }

    //Layout button to run Retrofit APi call method
    public void AddTagIdstoLoad(View view) {
        AddToAutoclavePressed = true;
            //getting generic_articles list for pouch
            if (tempTagStore.size() > 0) {
                if (is_pouch == 1) {
                    int i = 0;
                    //converting hashmap into a single string
                    for (Map.Entry<String, Integer> entry : singleHash.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        generic_articles += value + " " + key;
                        if (i < singleHash.size()) {
                            generic_articles += ",";
                        }
                        i++;
                    }
                }
                for (int y = 0; y < tempTagStore.size(); y++) {
                    Log.d("Selected =", "" + bagcoming);
                    Log.d("Selected =", "selected Items : " + tempTagStore.get(y).getType());
                    Log.d("Selected = ", "counts for items : " + tempTagStore.get(y).getCounter());
                    Log.d("Selected = ", "pouch items : " + generic_articles);
                }
            } else {
            //show error dialog if list is empty
            AddToAutoclavePressed = false;
            customDialogOneButton(res.getString(R.string.listEmptyDialog), res.getString(R.string.listEmptyDialogDesc), "red");

        }
    }
}