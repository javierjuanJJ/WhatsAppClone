package whatsappclone.proyecto_javier_juan_uceda.whatsappclone;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSDeviceState;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONException;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Chat.ChatListAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Chat.ChatObject;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Utils.SendNotification;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener, OSSubscriptionObserver {

    private Button btnLogOut, btnFindUser;
    private static final String ONESIGNAL_APP_ID = "d01f2fac-0fe4-40ee-96b1-9ca107d94da2";
    //private static final String ONESIGNAL_APP_ID = "146ff33d-e6e5-474b-b688-416ad622c1b2";

    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    ArrayList<ChatObject> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);

        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OSDeviceState deviceState = OneSignal.getDeviceState();
        String userId = deviceState != null ? deviceState.getUserId() : null;

        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
        OneSignal.pauseInAppMessages(true);
        OneSignal.setLocationShared(false);

        //OneSignal.addSubscriptionObserver(this);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

        try {
            //SendNotification.SendNotification("message 1", "heading 1", ONESIGNAL_APP_ID);
            SendNotification.SendNotification("message 1", "heading 1", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setUI();

    }

    private void setUI() {

        Fresco.initialize(this);

        btnLogOut = findViewById(R.id.logout);
        btnLogOut.setOnClickListener(this);
        btnFindUser = findViewById(R.id.btnFindUser);
        btnFindUser.setOnClickListener(this);


        getPermissions();
        initializeRecyclerView();
        getUserChatList();

    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                OneSignal.disablePush(true);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.btnFindUser:
                startActivity(new Intent(this, FindUserActivity.class));
                break;
        }
    }


    private void getUserChatList() {
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean exists = false;
                        for (ChatObject mChatIterator : chatList) {
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatList.add(mChat);
                        mChatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecyclerView() {
        chatList = new ArrayList<>();
        mChatList = findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges osSubscriptionStateChanges) {
        if (!osSubscriptionStateChanges.getFrom().isSubscribed() &&
                osSubscriptionStateChanges.getTo().isSubscribed()) {
            new AlertDialog.Builder(this)
                    .setMessage("You've successfully subscribed to push notifications!")
                    .show();
            // get player ID
            osSubscriptionStateChanges.getTo().getUserId();
        }

        Log.i("Debug", "onOSSubscriptionChanged: " + osSubscriptionStateChanges);
    }
}