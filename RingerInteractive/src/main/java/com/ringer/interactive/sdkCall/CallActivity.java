package com.ringer.interactive.sdkCall;


import static android.telecom.CallAudioState.ROUTE_BLUETOOTH;
import static android.telecom.CallAudioState.ROUTE_EARPIECE;
import static android.telecom.CallAudioState.ROUTE_SPEAKER;
import static com.ringer.interactive.sdkCall.Constants.asString;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.ringer.interactive.R;
import com.ringer.interactive.api.Api;
import com.ringer.interactive.call.AuthAPICall;
import com.ringer.interactive.model.StoreContact;
import com.ringer.interactive.pref.Preferences;
import com.ringer.interactive.service.MyForegroundService;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.collections.CollectionsKt;

public class CallActivity extends AppCompatActivity implements View.OnClickListener {

    private static CallService callService1;
    private static Call call1;
    Button answer, hangup;
    ImageButton btn_mute, btn_speaker, btn_hold, btn_bluetooth, btn_conference;
    ImageView img_profile;
    TextView callInfo, txt_answer, txt_hangup, callNumber, callstate;

    LinearLayout lin_call_accept, lin_call_Data, lin_call_on;
    EditText edt_keypade_number;
    int countryCode = 0;
    String nationalNumber = "";

    Boolean isMerge = false;
    Boolean isHold = false;
    Boolean isMuted = false;
    Boolean isSpeaker = false;
    Boolean isBluetooth = false;

    private CompositeDisposable disposables;
    private String number, contactId, name = "";
    private OngoingCall ongoingCall;
    InputStream photo_stream;

    RelativeLayout relative_data;

    @SuppressLint({"Range", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_call);

        answer = findViewById(R.id.answer);
        hangup = findViewById(R.id.hangup);
        btn_mute = findViewById(R.id.btn_mute);
        btn_speaker = findViewById(R.id.btn_speaker);
        btn_hold = findViewById(R.id.btn_hold);
        btn_bluetooth = findViewById(R.id.btn_bluetooth);
        callInfo = findViewById(R.id.callInfo);
        img_profile = findViewById(R.id.img_profile);
        txt_answer = findViewById(R.id.txt_answer);
        txt_hangup = findViewById(R.id.txt_hangup);
        callNumber = findViewById(R.id.callNumber);
        callstate = findViewById(R.id.callstate);
        lin_call_on = findViewById(R.id.lin_call_on);
        lin_call_accept = findViewById(R.id.lin_call_accept);
        edt_keypade_number = findViewById(R.id.edt_keypade_number);
        lin_call_Data = findViewById(R.id.lin_call_Data);
        relative_data = findViewById(R.id.relative_data);
        btn_conference = findViewById(R.id.btn_conference);
        btn_conference.setOnClickListener(this);
        hangup.setOnClickListener(this);

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

        edt_keypade_number.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edt_keypade_number.setVisibility(View.GONE);
                    edt_keypade_number.clearFocus();
                    lin_call_Data.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_keypade_number.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        if (new Preferences().getIsCallMerge(getApplicationContext()).equals("1")) {

        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }


        try {

            if (new Preferences().getIsCallMerge(getApplicationContext()).equals("1")) {

                callNumber.setText("Merged");
                img_profile.setImageResource(R.drawable.download);
                img_profile.setAlpha(1f);

                callInfo.setVisibility(View.VISIBLE);
                callNumber.setTextSize(18f);
                callInfo.setText("Conference Call");
                callNumber.setTypeface(Typeface.DEFAULT);
                lin_call_accept.setVisibility(View.GONE);


            } else {

                String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
                number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart();
                Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

                assert cursor != null;
                if (cursor.moveToFirst()) {
                    // Get values from contacts database:
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

                    new Preferences().setPhoneName(getApplicationContext(), name);
                } else {
                    return; // contact not found
                }


                int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentApiVersion >= 14) {
                    Uri my_contact_Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri, true);
                } else {
                    Uri my_contact_Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
                    photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri);
                }

                Log.e("photo_stream", "" + photo_stream);
                if (photo_stream != null) {
                    BufferedInputStream buf = new BufferedInputStream(photo_stream);
                    Bitmap my_btmp = BitmapFactory.decodeStream(buf);

                    img_profile.setImageBitmap(my_btmp);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    my_btmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                    new Preferences().setImageUser(getApplicationContext(), encodedImage);

                    Log.e("setGetImage", "123:-" + new Preferences().getImageUser(getApplicationContext()));


                } else {

                    RelativeLayout.LayoutParams layoutParams =
                            (RelativeLayout.LayoutParams) img_profile.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    img_profile.setLayoutParams(layoutParams);

                    img_profile.getLayoutParams().height = 400;
                    img_profile.getLayoutParams().width = 400;
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.lin_call_Data);
                    img_profile.setImageResource(R.drawable.download);
                }

                cursor.close();
            }
        } catch (Exception e) {

            Log.e("ImageException", "" + e.getMessage());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        disposables.add(
                OngoingCall.state
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {

                                Log.e("storeIsMerge", "" + new Preferences().getIsCallMerge(getApplicationContext()));


                                if (new Preferences().getIsCallMerge(getApplicationContext()).equals("1")) {
                                    callNumber.setText("Merged");
                                    img_profile.setImageResource(R.drawable.download);
                                    img_profile.setAlpha(1f);

                                    callInfo.setVisibility(View.VISIBLE);
                                    callNumber.setTextSize(18f);
                                    callInfo.setText("Conference Call");
                                    callNumber.setTypeface(Typeface.DEFAULT);
                                    lin_call_accept.setVisibility(View.GONE);
                                } else {
                                    updateUi(integer);
                                }


                            }
                        }));


        disposables.add(
                OngoingCall.state
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return integer == Call.STATE_DISCONNECTED;
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .firstElement()
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                if (new Preferences().getIsCallMerge(getApplicationContext()).equals("1")) {

                                } else {
                                    new AuthAPICall().apiCallAuth(getApplicationContext());
                                    Intent serviceIntent = new Intent(getApplicationContext(), MyForegroundService.class);
                                    stopService(serviceIntent);
                                   /* if ((call1.getState() == Call.STATE_ACTIVE) ||call1.getState() == Call.STATE_HOLDING) {

                                    }else {
                                        finish();
                                    }*/


                                }


                            }
                        }));
    }

    @SuppressLint("SetTextI18n")
    private Consumer<? super Integer> updateUi(Integer state) {


        new Preferences().setIsCallMerged(getApplicationContext(), "0");

        callInfo.setText(name);


        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;

        if (name.equals("")) {
            callInfo.setVisibility(View.GONE);
            callNumber.setTextSize(25f);
            callNumber.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            callInfo.setVisibility(View.VISIBLE);
            callNumber.setTextSize(18f);
            callNumber.setTypeface(Typeface.DEFAULT);
        }
        try {
            numberProto = phoneUtil.parse(number, "");
            countryCode = numberProto.getCountryCode();
            nationalNumber = String.valueOf(numberProto.getNationalNumber());
            Log.e("countryCode", "" + countryCode);
            Log.e("nationalNumber", "" + nationalNumber);
            String maskFirst = "(" + nationalNumber.substring(0, 3) + ")" + nationalNumber.substring(3, 6) + "-" + nationalNumber.substring(6, nationalNumber.length());
            Log.e("maskFirst", "" + maskFirst);
            callNumber.setText("" + maskFirst);


        } catch (NumberParseException e) {
            e.printStackTrace();
            try {


                String maskFirst = "(" + number.substring(0, 3) + ")" + number.substring(3, 6) + "-" + number.substring(6, number.length());
                callNumber.setText("" + maskFirst);

            } catch (Exception e1) {
                Log.e("ErrorHere", "" + e1.getMessage());
                callNumber.setText(new Preferences().getPhoneNumber(getApplicationContext()));
                String imageUser = new Preferences().getImageUser(getApplicationContext());
                Log.e("imageUser", "132:->" + imageUser);
                if (!imageUser.equals("")) {
                    byte[] b = Base64.decode(imageUser, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    img_profile.setImageBitmap(bitmap);
                    img_profile.setAlpha(0.3f);
                } else {
                    img_profile.setImageResource(R.drawable.download);
                    img_profile.setAlpha(1f);
                }


                String userName = new Preferences().getPhoneName(getApplicationContext());
                Log.e("UserName", "1324:->" + userName);
                if (!userName.equals("")) {
                    callInfo.setVisibility(View.VISIBLE);
                    callNumber.setTextSize(18f);
                    callInfo.setText(userName);
                    callNumber.setTypeface(Typeface.DEFAULT);
                } else {
                    callInfo.setVisibility(View.GONE);
                    callNumber.setTextSize(25f);
                    callNumber.setTypeface(Typeface.DEFAULT_BOLD);
                }


            }

        }


        new Preferences().setPhoneNumber(getApplicationContext(), callNumber.getText().toString());


        callstate.setText(asString(state));
        /*if (name.equals("")) {
            callInfo.setVisibility(View.GONE);
            callNumber.setTextSize(25f);
            callNumber.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            callInfo.setVisibility(View.VISIBLE);
            callNumber.setTextSize(18f);
            callNumber.setTypeface(Typeface.DEFAULT);
        }*/
//        callInfo.setText(name + "\n" +number +"\n"+asString(state));
//        callInfo.setText(name + "\n" + number);

        if (state != Call.STATE_RINGING && state != Call.STATE_DISCONNECTED) {
            answer.setVisibility(View.GONE);
            txt_answer.setVisibility(View.GONE);
            lin_call_accept.setVisibility(View.GONE);
        } else if (state == Call.STATE_DISCONNECTED) {
            finish();
        } else {
            answer.setVisibility(View.VISIBLE);
            txt_answer.setVisibility(View.VISIBLE);

            lin_call_accept.setVisibility(View.VISIBLE);
        }
        if (CollectionsKt.listOf(new Integer[]{
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE,
                Call.STATE_SELECT_PHONE_ACCOUNT,
                Call.STATE_HOLDING}).contains(state)) {

            hangup.setVisibility(View.VISIBLE);
            txt_hangup.setVisibility(View.VISIBLE);


        } else {
            hangup.setVisibility(View.GONE);
            txt_hangup.setVisibility(View.GONE);
        }

        if ((state == Call.STATE_ACTIVE) || (state == Call.STATE_HOLDING)) {
            Log.e("state", "" + state);
            if (photo_stream != null) {
                img_profile.setAlpha(0.3f);
            } else {

                img_profile.setAlpha(1f);
            }
            lin_call_on.setVisibility(View.VISIBLE);

            if (new Preferences().getImageUser(getApplicationContext()).equals("")) {
                img_profile.setAlpha(1f);
            } else {
                img_profile.setAlpha(0.3f);
            }

        } else {
            Log.e("state1", "" + state);
            img_profile.setAlpha(1f);
            lin_call_on.setVisibility(View.GONE);
        }

        Log.e("state", "" + state);

        /*if (state == Call.STATE_HOLDING){

            hangup.setVisibility(View.VISIBLE);
            txt_hangup.setVisibility(View.VISIBLE);
            lin_call_on.setVisibility(View.VISIBLE);
            img_profile.setAlpha(0.3f);

        }else {

            hangup.setVisibility(View.GONE);
            txt_hangup.setVisibility(View.GONE);
            lin_call_on.setVisibility(View.GONE);
            img_profile.setAlpha(0.3f);

        }*/

        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    public static void start(Context context, Call call, CallService callService, String data) {
        if (data.equals("1")) {

            Log.e("alwaysAsk", "alwaysAsk");
            /*        Intent intent = new Intent(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE);*/
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);


//            dataPopup(context);
//            callService1 = callService;
//            call1 = call;
            /*TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (telecomManager != null) {
                telecomManager.getSimCallManager();
            }*/


        } else {
            if (new Preferences().getIsCallMerge(context).equals("1")) {

            } else {
                Intent intent = new Intent(context, CallActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setData(call.getDetails().getHandle());
                context.startActivity(intent);
                callService1 = callService;
                call1 = call;
            }


        }

    }

    private static void dataPopup(Context context) {
        Intent intent = new Intent(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE);
        intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, context.getPackageName());
        ((Activity) context).startActivity(intent);

    }


    public void onAnswerClicked(View view) {
        ongoingCall.answer();
    }

    public void onHangupClicked(View view) {
        ongoingCall.hangup();
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        stopService(serviceIntent);
        CallList.getInstance().onCallRemoved(call1);

    }

    public void onMuted(View view) {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (!isMuted) {

            Log.e("AudioMode", "" + audioManager.getMode());
            if ((audioManager.getMode() == AudioManager.MODE_IN_CALL) || (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION)) {
                audioManager.setMicrophoneMute(true);
                callService1.setMuted(true);
                btn_mute.setImageResource(R.drawable.ic_icn_unmute);
                Log.e("getMicrophone", "" + audioManager.isMicrophoneMute());
            }
            isMuted = true;

        } else {

            Log.e("AudioMode1", "" + audioManager.getMode());
            if ((audioManager.getMode() == AudioManager.MODE_IN_CALL) || (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION)) {
                audioManager.setMicrophoneMute(false);
                callService1.setMuted(false);
                btn_mute.setImageResource(R.drawable.ic_unmute);
                Log.e("getMicrophone1", "" + audioManager.isMicrophoneMute());
            }
            isMuted = false;
        }

    }

    public void onSpeaker(View view) {
       /* audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
        Log.e("getSpeaker", "" + audioManager.isSpeakerphoneOn());*/


        if (!isSpeaker) {

            callService1.setAudioRoute(ROUTE_SPEAKER);
            btn_speaker.setImageResource(R.drawable.ic_icn_speaker_off);
            isSpeaker = true;

        } else {

            callService1.setAudioRoute(ROUTE_EARPIECE);
            btn_speaker.setImageResource(R.drawable.ic_speaker_on);
            isSpeaker = false;
        }
    }

    public void onKeypade(View view) {

        if (!edt_keypade_number.hasFocus()) {
            edt_keypade_number.setVisibility(View.VISIBLE);
            edt_keypade_number.requestFocus();
            lin_call_Data.setVisibility(View.VISIBLE);
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                    view.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);

        } else {

            edt_keypade_number.setVisibility(View.GONE);
            edt_keypade_number.clearFocus();
            lin_call_Data.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


    }

    public void onHolded(View view) {
        if (!isHold) {
            call1.hold();
            btn_hold.setImageResource(R.drawable.ic_icn_unhold);
            isHold = true;
        } else {
            call1.unhold();
            btn_hold.setImageResource(R.drawable.ic_icn_hold);
            isHold = false;
        }
    }

    public void onAddCall(View view) {

        if (!isMerge) {
            Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
            startActivity(intent);
            isMerge = true;
        } else {
//            call1.conference(call1);

//            call1.mergeConference();
            isMerge = false;
        }
    }

    public void onBluetoothConnect(View view) {
        if (!isBluetooth) {
            callService1.setAudioRoute(ROUTE_BLUETOOTH);
            btn_bluetooth.setImageResource(R.drawable.ic_bluetooth_on);
            isBluetooth = true;
        } else {

            callService1.setAudioRoute(ROUTE_EARPIECE);
            btn_bluetooth.setImageResource(R.drawable.ic_bluetooth_off);
            isBluetooth = false;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_conference) {
            final CallList calls = CallList.getInstance();
            CallHelper activeCall = calls.getActiveCall();

            if (activeCall != null) {

                final boolean canMerge = activeCall.can(
                        android.telecom.Call.Details.CAPABILITY_MERGE_CONFERENCE);
                final boolean canSwap = activeCall.can(
                        android.telecom.Call.Details.CAPABILITY_SWAP_CONFERENCE);
                // (2) Attempt actions on conference calls

                if (!canMerge) {
                    Toast.makeText(getApplicationContext(), "First You need to add call to use this feature", Toast.LENGTH_SHORT).show();
                }
                if (canMerge) {


                    Log.e("callMerge", "callMerged");

                    Toast.makeText(getApplicationContext(), "Your Call is merged", Toast.LENGTH_SHORT).show();

                    TelecomAdapter.getInstance().merge(activeCall.getId());
                    new Preferences().setIsCallMerged(getApplicationContext(), "1");


                } else if (canSwap) {
                    Log.e("callSwap", "callSwap");
                    TelecomAdapter.getInstance().swap(activeCall.getId());
                }
            } else {
                Toast.makeText(getApplicationContext(), "First You need to add call to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.hangup) {

            /*ArrayList<StoreContact> contactArrayList = new Preferences().getLocalData(getApplicationContext());
            if (contactArrayList != null && contactArrayList.size() > 0) {

                for (int i = 0; i < contactArrayList.size(); i++) {

                    new AuthAPICall().getCallDetails(getApplicationContext(), contactArrayList.get(i));
                }
            }*/


            ongoingCall.hangup();
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            stopService(serviceIntent);

            if (new Preferences().getIsCallMerge(getApplicationContext()).equals("1")) {
                new CallService().onCallRemoved(call1);

                CallList.getInstance().onCallRemoved(call1);
                new Preferences().setIsCallMerged(getApplicationContext(), "0");
                finish();

            }


        }
    }

}