package com.example.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SendingAdapter extends RecyclerView.Adapter<SendingAdapter.ViewHolder> {

    ArrayList<String> list;
    Context context;
    RecyclerViewInterfaceSend recyclerViewInterface;

    SendingAdapter(ArrayList<String> list, Context context, RecyclerViewInterfaceSend recyclerViewInterface){
        this.recyclerViewInterface = recyclerViewInterface;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contentHolder = list.get(position);
        holder.userName.setText(contentHolder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView userName;

        public ViewHolder(View view){
            super(view);
            userName = (TextView) view.findViewById(R.id.user_name_friend);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
