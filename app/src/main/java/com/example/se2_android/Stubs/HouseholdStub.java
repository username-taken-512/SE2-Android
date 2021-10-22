package com.example.se2_android.Stubs;

public class HouseholdStub {

    private static HouseholdStub householdStub = null;
    String householdID = "";
    String householdName = "My Household";

    public static HouseholdStub getInstance() {
        if (householdStub == null) {
            householdStub = new HouseholdStub();
        }
        return householdStub;
    }

    public HouseholdStub() {
    }

    public String getHouseholdID() {
        return householdID;
    }

    public void setHouseholdID(String householdID) {
        this.householdID = householdID;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }
}
