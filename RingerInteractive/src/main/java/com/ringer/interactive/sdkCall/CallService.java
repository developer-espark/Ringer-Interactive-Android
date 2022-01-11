package com.ringer.interactive.sdkCall;

import static android.telecom.CallAudioState.ROUTE_SPEAKER;

import android.telecom.Call;
import android.telecom.InCallService;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        new OngoingCall().setCall(call);
        CallActivity.start(this, call,CallService.this);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }



}
