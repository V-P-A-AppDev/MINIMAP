package com.v_p_a_appdev.minimap.chat;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v_p_a_appdev.minimap.R;

public class ChatViewHolders extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMessage;
    public LinearLayout mContainer;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
    }

    @Override
    public void onClick(View view) {
    }
}
