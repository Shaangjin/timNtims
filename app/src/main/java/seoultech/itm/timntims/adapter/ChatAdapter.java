package seoultech.itm.timntims.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import seoultech.itm.timntims.ImageHandler;
import seoultech.itm.timntims.R;
import seoultech.itm.timntims.model.ChatItem;
import seoultech.itm.timntims.model.ImageItem;
import seoultech.itm.timntims.model.Message;
import seoultech.itm.timntims.model.MessageItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.torchvision.TensorImageUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatItem> chatItems;
    private ImageHandler imageHandler ;
    private Context context;
    public ChatAdapter(Context context, List<ChatItem> chatItems) {
        this.context = context;
        this.chatItems = chatItems;
    }

    @Override
    public int getItemViewType(int position) {
        return chatItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ChatItem.TYPE_MESSAGE_SENT:
            case ChatItem.TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
                return new MessageViewHolder(view);
            case ChatItem.TYPE_IMAGE_SENT:
            case ChatItem.TYPE_IMAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
                return new ImageViewHolder(view);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatItems.get(position);
        switch (holder.getItemViewType()) {
            case ChatItem.TYPE_MESSAGE_SENT:

                MessageViewHolder messageHolder = (MessageViewHolder) holder;
                messageHolder.left_chat_view.setVisibility(View.GONE);
                messageHolder.right_chat_view.setVisibility(View.VISIBLE);

                MessageItem message = (MessageItem) chatItem;
                messageHolder.right_chat_tv.setText((CharSequence) message.getMessage());

                break;

            case ChatItem.TYPE_MESSAGE_RECEIVED:
                MessageViewHolder messageHolder2 = (MessageViewHolder) holder;
                messageHolder2.right_chat_view.setVisibility(View.GONE);
                messageHolder2.left_chat_view.setVisibility(View.VISIBLE);

                MessageItem message2 = (MessageItem) chatItem;
                messageHolder2.left_chat_tv.setText((CharSequence) message2.getMessage());

                break;

            case ChatItem.TYPE_IMAGE_SENT:

                ImageViewHolder imageHolder = (ImageViewHolder) holder;
                imageHolder.left_image_view.setVisibility(View.GONE);
                imageHolder.right_image_view.setVisibility(View.VISIBLE);

                ImageItem image = (ImageItem) chatItem;

                imageHolder.right_image_item.setImageURI(image.getUri());


                break;


            case ChatItem.TYPE_IMAGE_RECEIVED:
                ImageViewHolder imageHolder2 = (ImageViewHolder) holder;
                imageHolder2.right_image_view.setVisibility(View.GONE);
                imageHolder2.left_image_view.setVisibility(View.VISIBLE);

//                ImageItem image2 = (ImageItem) chatItem;
                imageHandler.downloadImage(imageHolder2.left_image_item);
//                imageHolder2.left_image_item.setImageURI(image2.getUri());


                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        // Define your TextViews or other UI elements here
        LinearLayout left_chat_view, right_chat_view;
        TextView left_chat_tv, right_chat_tv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            left_chat_view = itemView.findViewById(R.id.left_chat_view);
            right_chat_view = itemView.findViewById(R.id.right_chat_view);
            left_chat_tv = itemView.findViewById(R.id.left_chat_tv);
            right_chat_tv = itemView.findViewById(R.id.right_chat_tv);
        }

        public void bind(MessageItem messageItem) {
            // Bind message data to your views
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        // Define your ImageView here
        LinearLayout left_image_view, right_image_view;
        ImageView left_image_item, right_image_item;
        public ImageViewHolder(View itemView) {
            super(itemView);
            // Initialize your ImageView here
            left_image_view = itemView.findViewById(R.id.left_image_view);
            right_image_view = itemView.findViewById(R.id.right_image_view);
            left_image_item = itemView.findViewById(R.id.left_image_item);
            right_image_item = itemView.findViewById(R.id.right_image_item);
        }

        public void bind(ImageItem imageItem) {
            // Bind image data to your views
            // Use Glide or another image loading library to load the image from imageItem.getImageModel().getUrl()
        }
    }
}
