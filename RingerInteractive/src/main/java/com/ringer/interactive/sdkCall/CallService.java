package com.ringer.interactive.sdkCall;

import static android.telecom.CallAudioState.ROUTE_SPEAKER;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.ConnectionService;
import android.telecom.InCallService;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ringer.interactive.service.MyForegroundService;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        if (call.getState() == Call.STATE_SELECT_PHONE_ACCOUNT){

            /*Intent intent = new Intent(TelecomManager.ACTION_CONFIGURE_PHONE_ACCOUNT);
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, getPackageName());
            getApplicationContext().startActivity(intent);*/

            Log.e("CallService","CallService1");
            CallActivity.start(this, call,CallService.this,"1");

//            Toast.makeText(this, "Please Select One Sim Card to call", Toast.LENGTH_SHORT).show();

           /* Intent intent1 = new Intent(this,CallActivity.class);
            intent1.putExtra("callState",""+call.getState());
            startActivity(intent1);*/



        }else {
            Log.e("CallService","CallService2");
            new OngoingCall().setCall(call);
            CallActivity.start(this, call,CallService.this,"0");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, MyForegroundService.class));
            }


        }

    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }

}
