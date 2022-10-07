package whatsappclone.proyecto_javier_juan_uceda.whatsappclone;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Chat.ChatObject;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Media.MediaAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Message.MessageAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Message.MessageObject;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.User.UserObject;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Utils.SendNotification;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mChat, mMedia;
    private RecyclerView.Adapter mChatAdapter, mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager, mMediaLayoutManager;
    ArrayList<MessageObject> messageList;

    private Button mSend, mAddMedia;
    private EditText etMessage;
    ChatObject mChatObject;

    DatabaseReference mChatMessagesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setUI();

    }

    private void setUI() {

        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");
        Log.i("userOwn", mChatObject.getChatId());
        mChatMessagesDb = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("messages");


        mSend = findViewById(R.id.send);
        mSend.setOnClickListener(this);

        mAddMedia = findViewById(R.id.addMedia);
        mAddMedia.setOnClickListener(this);

        etMessage = findViewById(R.id.messageInput);
        initializeMedia();
        getChatMessages();
        initializeRecyclerView();


    }

    private void initializeMedia() {
        messageList = new ArrayList<>();
        mMedia = findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void getChatMessages() {
        mChatMessagesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String messages = "";
                    String creatorId = "";
                    ArrayList<String> mediaUriList = new ArrayList<>();
                    if (snapshot.child("text").getValue() != null) {
                        messages = snapshot.child("text").getValue().toString();
                    }


                    if (snapshot.child("creator").getValue() != null) {
                        creatorId = snapshot.child("creator").getValue().toString();
                    }

                    if (snapshot.child("media").getChildrenCount() > 0) {
                        for (DataSnapshot mediaSnapShot :
                                snapshot.child("media").getChildren()) {
                            mediaUriList.add(mediaSnapShot.getValue().toString());
                        }
                    }

                    Log.i("userOwn", String.format("Message key: %s by creator %s with message %s", snapshot.getKey(), creatorId, messages));

                    MessageObject mMesage = new MessageObject(snapshot.getKey(), creatorId, messages, mediaUriList);
                    messageList.add(mMesage);
                    mChatLayoutManager.scrollToPosition(messageList.size() - 1);
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeRecyclerView() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                sendMessage();
                break;
            case R.id.addMedia:
                openGallery();
                break;
        }
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    ArrayList<String> mediaIdList = new ArrayList<>();
    int totalMediaUploaded = 0;

    private void sendMessage() {
        if (!etMessage.getText().toString().isEmpty()) {
            String messageId = mChatMessagesDb.push().getKey();
            final DatabaseReference newMessageDb = mChatMessagesDb.child(messageId);

            final Map newMessageMap = new HashMap<>();
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            if (!etMessage.getText().toString().isEmpty()) {
                newMessageMap.put("text", etMessage.getText().toString());
            }

            newMessageDb.updateChildren(newMessageMap);


            if (!mediaUriList.isEmpty()) {
                for (String mediaUri : mediaUriList) {
                    String mediaId = newMessageDb.child("media").push().getKey();
                    mediaIdList.add(mediaId);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);

                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                                    totalMediaUploaded++;
                                    if (totalMediaUploaded == mediaUriList.size()) {
                                        updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
                                    }
                                }
                            });
                        }
                    });
                }
            } else {
                if (!etMessage.getText().toString().isEmpty()) {
                    updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
                }
            }

        }
    }


    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap) {
        newMessageDb.updateChildren(newMessageMap);
        etMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        totalMediaUploaded = 0;
        mMediaAdapter.notifyDataSetChanged();

        String message;

        if (newMessageMap.get("text") != null)
            message = newMessageMap.get("text").toString();
        else
            message = "Sent Media";

        for (UserObject mUser : mChatObject.getUserObjectArrayList()) {
            if (!mUser.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                try {
                    SendNotification.SendNotification(message, "New Message", mUser.getNotificationKey());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}