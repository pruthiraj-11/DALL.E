package com.app.dalle.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dalle.Models.QueryModel;
import com.app.dalle.R;

import java.util.ArrayList;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.viewHolder> {
    ArrayList<QueryModel> list=new ArrayList<>();

    public QueryAdapter(ArrayList<QueryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        QueryModel queryModel=list.get(position);
        if (queryModel.getSentBy().equals(queryModel.SENT_BY_USER)){
            holder.user_msg_view.setVisibility(View.VISIBLE);
            holder.user_msg_text.setText(queryModel.getMessage());
        } else {
            holder.chatgpt_msg_view.setVisibility(View.VISIBLE);
            holder.chatgpt_msg_text.setText(queryModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        LinearLayout chatgpt_msg_view,user_msg_view;
        TextView chatgpt_msg_text,user_msg_text;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            chatgpt_msg_view=itemView.findViewById(R.id.left_chat_view);
            chatgpt_msg_view=itemView.findViewById(R.id.left_chat_view);
            user_msg_view=itemView.findViewById(R.id.right_chat_view);
            user_msg_text=itemView.findViewById(R.id.right_chat_text_view);
        }
    }
}
