package com.ringer.interactive.sdkCall;

import android.annotation.SuppressLint;
import android.telecom.Call;

import java.util.List;

public class TelecomAdapter {
    private static TelecomAdapter sInstance;
    @SuppressLint("RestrictedApi")
    static TelecomAdapter getInstance() {
//        Preconditions.checkState(Looper.getMainLooper().getThread() == Thread.currentThread());
        if (sInstance == null) {
            sInstance = new TelecomAdapter();
        }
        return sInstance;
    }

    void merge(String callId) {
            android.telecom.Call call = getTelecommCallById(callId);
            List<Call> conferenceable = call.getConferenceableCalls();
            if (!conferenceable.isEmpty()) {
                call.conference(conferenceable.get(0));
            } else {
                int capabilities = call.getDetails().getCallCapabilities();
                    call.mergeConference();
            }
    }

    void swap(String callId) {
            android.telecom.Call call = getTelecommCallById(callId);
            int capabilities = call.getDetails().getCallCapabilities();
                call.swapConference();
    }
/*    private Call getTelecommCallById(String callId) {
        final Call call = CallList.getInstance().getCallById(callId);
        return call == null ? null : call;
    }*/
private android.telecom.Call getTelecommCallById(String callId) {
    final CallHelper call = CallList.getInstance().getCallById(callId);
    return call == null ? null : call.getTelecommCall();
}

}
