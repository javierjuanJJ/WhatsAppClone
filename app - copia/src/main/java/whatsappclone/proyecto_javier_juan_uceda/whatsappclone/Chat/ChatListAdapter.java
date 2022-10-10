package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.ChatActivity;
import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.UserListViewHolder> {
    ArrayList<ChatObject> chatList;

    public ChatListAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.mTitle.setText(chatList.get(position).getChatId());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("chatObject", chatList.get(holder.getAdapterPosition()));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public LinearLayout linearLayout;

        public UserListViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.title);
            linearLayout = view.findViewById(R.id.layout);
        }
    }
}
