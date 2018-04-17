package com.applozic.mobicomkit.uiwidgets.conversation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.MobicomMessageTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by reytum on 1/8/17.
 */

public class MobicomMessageTemplateAdapter extends RecyclerView.Adapter<MobicomMessageTemplateAdapter.ViewHolder> {

    private MobicomMessageTemplate messageTemplate;
    private MessageTemplateDataListener listener;
    private List<String> messageList;
    private Map<String, String> messageMap;
    private Context context;

    public MobicomMessageTemplateAdapter(Context context, MobicomMessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
        messageMap = messageTemplate.getMessages();
        Map<String, String> tempMap = ApplozicClient.getInstance(context).getMessageTemplates();

        if (tempMap != null && !tempMap.isEmpty()) {
            messageMap.putAll(tempMap);
        }

        messageList = new ArrayList<>(messageMap.keySet());

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mobicom_message_template_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.messageText.setText(messageList.get(position));
        holder.messageText.setTextColor(Color.parseColor(messageTemplate.getTextColor()));
        holder.messageText.setBackgroundDrawable(getShape(holder.messageText.getContext()));

        holder.messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemSelected(messageMap.get(messageList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setMessageList(Map<String, String> messageList) {
        Map<String, String> tempMap = ApplozicClient.getInstance(context).getMessageTemplates();
        messageMap = messageList;

        if (tempMap != null && !tempMap.isEmpty()) {
            messageMap.putAll(tempMap);
        }

        this.messageList = new ArrayList<>(messageMap.keySet());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;

        public ViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.messageTemplateTv);
        }
    }

    public GradientDrawable getShape(Context context) {
        GradientDrawable bgShape = new GradientDrawable();
        bgShape.setShape(GradientDrawable.RECTANGLE);
        bgShape.setColor(Color.parseColor(messageTemplate.getBackGroundColor()));
        bgShape.setCornerRadius(dpToPixels(context, messageTemplate.getCornerRadius()));
        bgShape.setStroke(dpToPixels(context, 2), Color.parseColor(messageTemplate.getBorderColor()));

        return bgShape;
    }

    public int dpToPixels(Context context, float px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }

    public void setOnItemSelected(MessageTemplateDataListener listener) {
        this.listener = listener;
    }

    public interface MessageTemplateDataListener {
        void onItemSelected(String messsage);
    }

    public void removeTemplates() {
        messageList.clear();
        notifyDataSetChanged();
    }
}