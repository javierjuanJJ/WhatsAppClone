package whatsappclone.proyecto_javier_juan_uceda.whatsappclone.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

import whatsappclone.proyecto_javier_juan_uceda.whatsappclone.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

   ArrayList<MessageObject> messageList;

   public MessageAdapter(ArrayList<MessageObject> messageList) {
      this.messageList = messageList;
   }

   @NonNull
   @Override
   public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
      RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutView.setLayoutParams(lp);

      MessageViewHolder rcv = new MessageViewHolder(layoutView);
      return rcv;
   }

   @Override
   public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
      holder.mMessage.setText(messageList.get(position).getMessage());
      holder.mSender.setText(messageList.get(position).getSenderId());

      if (messageList.get(holder.getAdapterPosition()).getMediaUriList().isEmpty())
         holder.vViewMedia.setVisibility(View.GONE);

      holder.vViewMedia.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            new ImageViewer.Builder(view.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUriList())
                    .setStartPosition(0)
                    .show();
         }
      });
   }


   @Override
   public int getItemCount() {
      return messageList.size();
   }


   class MessageViewHolder extends RecyclerView.ViewHolder {
      TextView mMessage,
              mSender;
      Button vViewMedia;
      LinearLayout mLayout;

      MessageViewHolder(View view) {
         super(view);
         mLayout = view.findViewById(R.id.layout);
         vViewMedia = view.findViewById(R.id.viewMedia);
         mMessage = view.findViewById(R.id.message);
         mSender = view.findViewById(R.id.sender);
      }
   }
}
