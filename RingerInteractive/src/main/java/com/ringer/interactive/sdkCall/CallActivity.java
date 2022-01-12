package com.ringer.interactive.sdkCall;


import static android.telecom.CallAudioState.ROUTE_EARPIECE;
import static android.telecom.CallAudioState.ROUTE_SPEAKER;
import static com.ringer.interactive.sdkCall.Constants.asString;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.Call;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ringer.interactive.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.collections.CollectionsKt;

public class CallActivity extends AppCompatActivity {

    private static CallService callService1;
    private static Call call1;
    Button answer,hangup;
    ImageButton btn_mute,btn_speaker,btn_hold;
    ImageView img_profile;
    TextView callInfo,txt_answer,txt_hangup,callNumber,callstate;

    LinearLayout lin_call_accept,lin_call_Data,lin_call_on;
    EditText edt_keypade_number;

    Boolean isHold = false;
    Boolean isMuted = false;
    Boolean isSpeaker = false;

    private CompositeDisposable disposables;
    private String number, contactId, name = "";
    private OngoingCall ongoingCall;
    InputStream photo_stream;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        answer = findViewById(R.id.answer);
        hangup = findViewById(R.id.hangup);
        btn_mute = findViewById(R.id.btn_mute);
        btn_speaker = findViewById(R.id.btn_speaker);
        btn_hold = findViewById(R.id.btn_hold);
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

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

        edt_keypade_number.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_DONE){
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        try {


            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
            number = Objects.requireNonNull(getIntent().getData()).getSchemeSpecificPart();
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

            assert cursor != null;
            if (cursor.moveToFirst()) {
                // Get values from contacts database:
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
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
            } else {
                img_profile.setImageResource(R.drawable.download);
            }

            cursor.close();
        } catch (Exception e) {

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
                                updateUi(integer);
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
                                finish();
                            }
                        }));
    }

    @SuppressLint("SetTextI18n")
    private Consumer<? super Integer> updateUi(Integer state) {

        callInfo.setText(name);

        callNumber.setText(number);
        callstate.setText(asString(state));
        if (name.equals("")) {
            callInfo.setVisibility(View.GONE);
            callNumber.setTextSize(25f);
            callNumber.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            callInfo.setVisibility(View.VISIBLE);
            callNumber.setTextSize(18f);
            callNumber.setTypeface(Typeface.DEFAULT);
        }
//        callInfo.setText(name + "\n" +number +"\n"+asString(state));
//        callInfo.setText(name + "\n" + number);

        if (state != Call.STATE_RINGING) {
            answer.setVisibility(View.GONE);
            txt_answer.setVisibility(View.GONE);
            lin_call_accept.setVisibility(View.GONE);
        } else {
            answer.setVisibility(View.VISIBLE);
            txt_answer.setVisibility(View.VISIBLE);

            lin_call_accept.setVisibility(View.VISIBLE);
        }
        if (CollectionsKt.listOf(new Integer[]{
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE,
        Call.STATE_HOLDING}).contains(state)) {

            hangup.setVisibility(View.VISIBLE);
            txt_hangup.setVisibility(View.VISIBLE);


        } else {
            hangup.setVisibility(View.GONE);
            txt_hangup.setVisibility(View.GONE);
        }
        if ((state == Call.STATE_ACTIVE)||(state == Call.STATE_HOLDING)) {
            img_profile.setAlpha(0.3f);
            lin_call_on.setVisibility(View.VISIBLE);
        } else {
            img_profile.setAlpha(1f);
            lin_call_on.setVisibility(View.GONE);
        }

        Log.e("state",""+state);

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

    public static void start(Context context, Call call,CallService callService) {
        Intent intent = new Intent(context, CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
        callService1 = callService;
        call1 = call;
    }

    public void onAnswerClicked(View view) {
        ongoingCall.answer();
    }

    public void onHangupClicked(View view) {
        ongoingCall.hangup();
    }

    public void onMuted(View view) {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (!isMuted) {

            Log.e("AudioMode", "" + audioManager.getMode());
            if ((audioManager.getMode() == AudioManager.MODE_IN_CALL) || (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION)) {
                audioManager.setMicrophoneMute(true);
                btn_mute.setImageResource(R.drawable.ic_icn_unmute);
                Log.e("getMicrophone", "" + audioManager.isMicrophoneMute());
            }
            isMuted = true;

        } else {

            Log.e("AudioMode1", "" + audioManager.getMode());
            if ((audioManager.getMode() == AudioManager.MODE_IN_CALL) || (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION)) {
                audioManager.setMicrophoneMute(false);
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
            lin_call_Data.setVisibility(View.GONE);
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
        if (!isHold){
            call1.hold();
            btn_hold.setImageResource(R.drawable.ic_icn_unhold);
            isHold = true;
        }else {
            call1.unhold();
            btn_hold.setImageResource(R.drawable.ic_icn_hold);
            isHold = false;
        }
    }
}