package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.User;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.R;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    ArrayList<UserObject> userList;

    public UserListAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mName.setText(userList.get(position).getName());
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createChat(holder.getAdapterPosition());
            }
        });

        holder.mPhone.setText(userList.get(position).getPhone());
        holder.mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat(holder.getAdapterPosition());
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat(holder.getAdapterPosition());
            }
        });
    }

    private void createChat(int position) {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);
        newChatMap.put("users/" + userList.get(position).getUid(), true);

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        chatInfoDb.updateChildren(newChatMap);

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
        userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        userDb.child(userList.get(position).getUid()).child("chat").child(key).setValue(true);

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mPhone;
        public LinearLayout linearLayout;

        public UserListViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            linearLayout = view.findViewById(R.id.layout);
        }
    }
}
