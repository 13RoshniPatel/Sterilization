package com.beaconblast.btdental.models.res;

import android.content.Context;
import android.util.Log;

import com.beaconblast.btdental.util.ConstantNaming;
import com.beaconblast.btdental.util.SharedPrefs;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetArticlesByTagId {

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private int counter = 1;

        @SerializedName("custom_tag_id")
        @Expose
        private String custom_tag_id;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("article_type")
        @Expose
        private String articleType;
        @SerializedName("tag_id")
        @Expose
        private String tagId;
        @SerializedName("manufacturer")
        @Expose
        private String manufacturer;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("lot")
        @Expose
        private String lot;
        @SerializedName("active")
        @Expose
        private Integer active;
        @SerializedName("latest_activity_id")
        @Expose
        private Integer latestActivityId;
        @SerializedName("latest_activity_date")
        @Expose
        private String latestActivityDate;
        @SerializedName("latest_user_id")
        @Expose
        private Integer latestUserId;
        @SerializedName("cycles")
        @Expose
        private Integer cycles;
        @SerializedName("maintenance_count")
        @Expose
        private Integer maintenanceCount;
        @SerializedName("location_id")
        @Expose
        private Integer locationId;
        @SerializedName("owner")
        @Expose
        private String owner;
        @SerializedName("additional_info")
        @Expose
        private String additionalInfo;
        @SerializedName("price")
        @Expose
        private String price;
        @SerializedName("branch_id")
        @Expose
        private Integer branchId;
        @SerializedName("removal_reason")
        @Expose
        private String removal_reason;
        @SerializedName("failed_reason")
        @Expose
        private String failed_reason;

    public GetArticlesByTagId(){}

    //counter for adding one item n number of times in a load
    public GetArticlesByTagId(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter++;
    }

    public void decrementCounter() {
        if (counter > 1) {
            counter--;
        }
    }

    public GetArticlesByTagId(String indicatorName, Context context) {
        this.articleType = "material";
        this.manufacturer = "repro";
        this.code = "fakecode";
        this.latestActivityId = ConstantNaming.c_load_activity_id;
        this.latestActivityDate = "";
        this.latestUserId = Integer.parseInt(SharedPrefs.getSharedPrefs(ConstantNaming.c_current_user_id, context));
        this.cycles = 1;
        this.maintenanceCount = 0;
        this.locationId = 22896;
        this.owner = "";
        this.additionalInfo = "";
        this.price = "0.0";
        this.active = 1;

        //Setting Some Params according to the indicator
        if(indicatorName.equals(ConstantNaming.c_class_5_with_hypehn)) {
            this.tagId = "faketag";
            this.id = 18;
            this.lot ="123";
            this.type= ConstantNaming.c_class_5_with_hypehn.toUpperCase();
        }
        if(indicatorName.equals(ConstantNaming.c_class_2_with_hyphen))
        {
            this.tagId = "faketag2";
            this.id = 20;
            this.lot ="123";
            this.type= ConstantNaming.c_class_2_with_hyphen.toUpperCase();
        }
        if(indicatorName.equals(ConstantNaming.c_biological_indicator)) {
            this.tagId = "faketag3";
            this.id = 21;
            this.type = ConstantNaming.c_bi_24hour.toUpperCase();
            try{
                this.lot = SharedPrefs.getSharedPrefs(ConstantNaming.c_latest_bi_lot,context);
            }
            catch(Exception e)
            {
                Log.d(ConstantNaming.c_exceptionHere, "not able to set bi lot number" + e);
            }}
    }

        public String getRemoval_reason() {
            return removal_reason;
        }

        public void setRemoval_reason(String removal_reason) {
            this.removal_reason = removal_reason;
        }

    public String getFailed_reason() {
        return failed_reason;
    }

    public void setFailed_reason(String failed_reason) {
        this.failed_reason = failed_reason;
    }

    public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getArticleType() {
            return articleType;
        }

        public void setArticleType(String articleType) {
            this.articleType = articleType;
        }

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLot() {
            return lot;
        }

        public void setLot(String lot) {
            this.lot = lot;
        }

        public Integer getActive() {
            return active;
        }

        public void setActive(Integer active) {
            this.active = active;
        }

        public Integer getLatestActivityId() {
            return latestActivityId;
        }

        public void setLatestActivityId(Integer latestActivityId) {
            this.latestActivityId = latestActivityId;
        }

        public String getLatestActivityDate() {
            return latestActivityDate;
        }

        public void setLatestActivityDate(String latestActivityDate) {
            this.latestActivityDate = latestActivityDate;
        }

        public Integer getLatestUserId() {
            return latestUserId;
        }

        public void setLatestUserId(Integer latestUserId) {
            this.latestUserId = latestUserId;
        }

        public Integer getCycles() {
            return cycles;
        }

        public void setCycles(Integer cycles) {
            this.cycles = cycles;
        }

        public Integer getMaintenanceCount() {
            return maintenanceCount;
        }

        public void setMaintenanceCount(Integer maintenanceCount) {
            this.maintenanceCount = maintenanceCount;
        }

        public Integer getLocationId() {
            return locationId;
        }

        public void setLocationId(Integer locationId) {
            this.locationId = locationId;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getAdditionalInfo() {
            return additionalInfo;
        }

        public void setAdditionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public Integer getBranchId() {
            return branchId;
        }

        public void setBranchId(Integer branchId) {
            this.branchId = branchId;
        }

    public String getCustom_tag_id() {
        return custom_tag_id;
    }

    public void setCustom_tag_id(String custom_tag_id) {
        this.custom_tag_id = custom_tag_id;
    }
}
