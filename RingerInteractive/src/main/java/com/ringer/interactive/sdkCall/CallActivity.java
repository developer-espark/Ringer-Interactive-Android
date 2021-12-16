package com.ringer.interactive.sdkCall;


import static com.ringer.interactive.sdkCall.Constants.asString;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.ringer.interactive.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.collections.CollectionsKt;

public class CallActivity extends AppCompatActivity {

    Button answer;
    Button hangup;
    Button btn_mute;
    TextView callInfo;
    ImageView img_profile;
    TextView txt_answer;
    TextView txt_hangup;

    private CompositeDisposable disposables;
    private String number, contactId, name;
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
        callInfo = findViewById(R.id.callInfo);
        img_profile = findViewById(R.id.img_profile);
        txt_answer = findViewById(R.id.txt_answer);
        txt_hangup = findViewById(R.id.txt_hangup);

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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
            Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show();
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

        if (photo_stream != null) {
            BufferedInputStream buf = new BufferedInputStream(photo_stream);
            Bitmap my_btmp = BitmapFactory.decodeStream(buf);
            img_profile.setImageBitmap(my_btmp);
        } else {
            img_profile.setImageResource(R.drawable.dummy);
        }

        cursor.close();

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

//        callInfo.setText(asString(state) + "\n" + number + "\n" + name);
        callInfo.setText(name);

        if (state != Call.STATE_RINGING) {
            answer.setVisibility(View.GONE);
            txt_answer.setVisibility(View.GONE);
        } else {
            answer.setVisibility(View.VISIBLE);
            txt_answer.setVisibility(View.VISIBLE);
        }
        if (CollectionsKt.listOf(new Integer[]{
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE}).contains(state)) {

            hangup.setVisibility(View.VISIBLE);
            txt_hangup.setVisibility(View.VISIBLE);
        } else{
            hangup.setVisibility(View.GONE);
            txt_hangup.setVisibility(View.GONE);
        }


        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }

    public void onAnswerClicked(View view) {
        ongoingCall.answer();
    }

    public void onHangupClicked(View view) {
        ongoingCall.hangup();
    }
    public void onMuted(View view){

        ongoingCall.callMuted();
    }

}