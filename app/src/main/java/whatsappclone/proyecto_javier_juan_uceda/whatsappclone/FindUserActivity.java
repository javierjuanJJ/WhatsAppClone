package whatsappclone.proyecto_javier_juan_uceda.whatsappclone;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.User.UserListAdapter;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.User.UserObject;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Utils.CountryToPhonePrefix;

public class FindUserActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList, contactsList;

    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        setUI();
    }

    private void setUI() {
        userList = new ArrayList<>();
        contactsList = new ArrayList<>();
        btnCreate = findViewById(R.id.create);
        btnCreate.setOnClickListener(this);

        initializeRecyclerView();
        getContactList();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String ISOPrefix = getCountryISO();
        while (phones.moveToNext()) {

            @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range") String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Log.i("phoneNumberBefore", "name: " + name + " with phone: " + phone);

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            //phone = "+34" + phone;
            Log.i("phoneNumberNext", "name: " + name + " with phone: " + phone);
            UserObject mContact = new UserObject("", name, phone);
            contactsList.add(mContact);
            mUserListAdapter.notifyDataSetChanged();
            getUserDetails(mContact);
        }
    }

    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserID = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserID.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phone = "",
                            name = "";
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if (childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phone);

                        if (name.equals(phone)) {
                            for (UserObject mContactInterator :
                                    contactsList) {
                                if (mContactInterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setName(mContactInterator.getName());
                                }
                            }
                        }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initializeRecyclerView() {
        mUserList = findViewById(R.id.rvUserList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }

    private String getCountryISO() {
        String iso = "";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            if (telephonyManager.getNetworkCountryIso() != null) {
                if (telephonyManager.getNetworkCountryIso().toString().equals("")) {
                    iso = telephonyManager.getNetworkCountryIso().toString();
                }
            }
        }


        return CountryToPhonePrefix.getPhone(iso);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create:
                createChat();
                break;
        }
    }

    private void createChat() {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);

        Boolean validChat = false;
        for (UserObject mUser : userList) {
            if (mUser.getSelected()) {
                validChat = true;
                newChatMap.put("users/" + mUser.getUid(), true);
                userDb.child(mUser.getUid()).child("chat").child(key).setValue(true);
            }
        }

        if (validChat) {
            chatInfoDb.updateChildren(newChatMap);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }
    }
}