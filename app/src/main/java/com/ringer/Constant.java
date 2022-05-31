package com.ringer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {

    public static void openuserMenuDialog(View view, Context mContext) {

        ExpandableListView expandableListView;
        ExpandableListAdapter expandableListAdapter;
        List<String> expandableListTitle;
        HashMap<String, List<String>> expandableListDetail;
//
//         static ProgressDialog progressDialog;
//         static CustomPrefrence customPrefrence;
//         static TransferObserver observer;
//         static Context mContext;
//         static String folder;
//         final int[] uploadCounter = {0};

        LayoutInflater layoutInflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.menu, null);


        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.LEFT, 0, 0);
        popupWindow.showAsDropDown(view, Gravity.CENTER, 100, 100);


        expandableListView = (ExpandableListView) popupView.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(mContext, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);



        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Log.e("groupPosition", "" + groupPosition);
                if (groupPosition == 2) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                    intent.setData(Uri.parse("mailto:info@flashappllc.com")); // or just "mailto:" for blank
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                    mContext.startActivity(intent);
                    popupWindow.dismiss();
                }

               /* Toast.makeText(mContext,
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

              /*  Toast.makeText(mContext,
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();*/

            }
        });



        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {
                    if (childPosition == 0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.privacy_url)));
                        mContext.startActivity(browserIntent);
                        popupWindow.dismiss();
                    }
                    if (childPosition == 1) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.terms_url)));
                        mContext.startActivity(browserIntent);
                        popupWindow.dismiss();
                    }
                    if (childPosition == 2){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.ack_url)));
                        mContext.startActivity(browserIntent);
                        popupWindow.dismiss();
                    }
                    /*if (childPosition == 0) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        popupWindow.dismiss();
                    }
                    if (childPosition == 1) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        popupWindow.dismiss();

                    }*/
                }
                if (groupPosition == 1) {
                    if (childPosition == 0) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        popupWindow.dismiss();
                    }
                    if (childPosition == 1) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        popupWindow.dismiss();

                    }
                   /* if (childPosition == 0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.privacy_url)));
                        mContext.startActivity(browserIntent);
                        popupWindow.dismiss();
                    }
                    if (childPosition == 1) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.terms_url)));
                        mContext.startActivity(browserIntent);
                        popupWindow.dismiss();
                    }
                    if (childPosition == 2){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getResources().getString(R.string.ack_url)));
                        mContext.startActivity(browserIntent);
                        popupWindow.dismiss();
                    }*/
                }

                return false;
            }
        });

    }


}
