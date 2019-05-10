package com.app_republic.bottle.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.app_republic.bottle.R;
import com.app_republic.bottle.model.GoldItem;
import com.app_republic.bottle.service.ByGoldInterface;

import java.util.ArrayList;

/**
 * Created by elhadj on 17/09/2018.
 */

public class buyGoldDialog extends DialogFragment {



    ByGoldInterface byGoldInterface;

    @Override
    public void onStart() {

        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        byGoldInterface = (ByGoldInterface) context;
    }

    ArrayList<GoldItem> list = new ArrayList<>();
    Adapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().getWindow().setBackgroundDrawableResource(R.color.cardview_dark_background);

        View rootView = inflater.inflate(R.layout.buy_gold, container, false);


        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);

        list = getArguments().getParcelableArrayList("items");

        adapter = new Adapter(getActivity(),list);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);




        return rootView;
    }


    class Adapter extends RecyclerView.Adapter<viewHolder> {

        private Context context;
        private ArrayList<GoldItem> list;

        public Adapter(Context context, ArrayList<GoldItem> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(context).inflate(R.layout.buy_gold_item, parent, false);
            return new viewHolder(view);


        }

        @Override
        public void onBindViewHolder(final viewHolder holder, int position) {

            GoldItem item = list.get(position);
            holder.price.setText(item.getPrice());
            holder.coins.setText(list.get(position).getCoins()+" Coins");

            if (list.get(position).getDiscount() != 0)
            holder.discount.setText("Discount: -"+list.get(position).getDiscount()+"%");

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byGoldInterface.buy(list.get(holder.getAdapterPosition()).getCoins(),list.get(holder.getAdapterPosition()).getPrice());
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder {

        public TextView price,coins,discount;
        public CardView root;


        public viewHolder(View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            coins = itemView.findViewById(R.id.coins);
            discount = itemView.findViewById(R.id.discount);
            root = itemView.findViewById(R.id.root);

        }
    }

}
