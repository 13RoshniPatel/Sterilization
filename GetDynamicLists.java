package com.beaconblast.btdental.models.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//model to store list data
public class GetDynamicLists {

        @SerializedName("outof_service_reasons")
        @Expose
        private String outofServiceReasons;

        @SerializedName("pouch_instruments")
        @Expose
        private String pouchInstruments;

        //getter setter
        public String getOutofServiceReasons() {
            return outofServiceReasons;
        }

        public void setOutofServiceReasons(String outofServiceReasons) {
            this.outofServiceReasons = outofServiceReasons;
        }

        public String getPouchInstruments() {
            return pouchInstruments;
        }

        public void setPouchInstruments(String pouchInstruments) {
            this.pouchInstruments = pouchInstruments;
        }
}
