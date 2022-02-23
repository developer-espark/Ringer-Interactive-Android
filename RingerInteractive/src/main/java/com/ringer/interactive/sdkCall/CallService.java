package com.ringer.interactive.sdkCall;

import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import com.ringer.interactive.pref.Preferences;
import com.ringer.interactive.service.MyForegroundService;

public class CallService extends InCallService {


    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        if (call.getState() == Call.STATE_SELECT_PHONE_ACCOUNT) {

            /*Intent intent = new Intent(TelecomManager.ACTION_CONFIGURE_PHONE_ACCOUNT);
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getPackageName());
            getApplicationContext().startActivity(intent);*/

            CallActivity.start(this, call, CallService.this, "1");
//            CallList.getInstance().onCallAdded(call);

//            Toast.makeText(this, "Please Select One Sim Card to call", Toast.LENGTH_SHORT).show();

           /* Intent intent1 = new Intent(this,CallActivity.class);
            intent1.putExtra("callState",""+call.getState());
            startActivity(intent1);*/


        } else {

            Log.e("CallService", "" + call.getState());


            Log.e("CallServiceIN", "" + call.getState());
            if (new Preferences().getIsCallMerge(getApplicationContext()).equals("1")) {

            }else {
                new OngoingCall().setCall(call);
                CallActivity.start(this, call, CallService.this, "0");
                CallList.getInstance().onCallAdded(call);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, MyForegroundService.class));
                }
            }
           /* new CountDownTimer(5000,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @OverridephoneNumValueC
                public void onFinish() {

                    CallList.getInstance().onCallAdded(call);

                }
            }.start();*/

        }

    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
        CallList.getInstance().onCallRemoved(call);
        /*new CountDownTimer(5000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                CallList.getInstance().onCallRemoved(call);

            }
        }.start();*/

    }

}
