package com.ringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("Contacts");
        cricket.add("Notifications");


        List<String> football = new ArrayList<String>();
        football.add("Privacy Policy");
        football.add("Terms of Service");
        football.add("License Agreement");

        List<String> basketball = new ArrayList<String>();

        expandableListDetail.put("Settings", cricket);
        expandableListDetail.put("Terms of Service & Privacy", football);
        expandableListDetail.put("ContactUs", basketball);
        return expandableListDetail;
    }
}