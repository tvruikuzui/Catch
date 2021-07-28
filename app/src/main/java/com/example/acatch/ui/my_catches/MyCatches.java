package com.example.acatch.ui.my_catches;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acatch.MyApplication;
import com.example.acatch.R;
import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;
import com.squareup.picasso.Picasso;


public class MyCatches extends Fragment {

    View view;
    MyCatchesViewModel myCatchesViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    MyAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_catches, container, false);
        swipeRefreshLayout = view.findViewById(R.id.myCatches_refresh);
        setUpProgressListener();
        swipeRefreshLayout.setOnRefreshListener(()->myCatchesViewModel.refresh());

        myCatchesViewModel  = new ViewModelProvider(this).get(MyCatchesViewModel.class);
        myCatchesViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    myCatchesViewModel.list=myCatchesViewModel.filterByUser(data);
                    adapter.notifyDataSetChanged();
                });


        //RecyclerView:
        RecyclerView ridesList = view.findViewById(R.id.myCatches_recyclerView);
        ridesList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.context);
        ridesList.setLayoutManager(manager);
        adapter = new MyAdapter();
        ridesList.setAdapter(adapter);

        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onCancelClick(int position) {
                Ride customerRide = myCatchesViewModel.getCurrentRide(myCatchesViewModel.list.get(position));
                Ride ownerRide = myCatchesViewModel.getOwnerRide(customerRide);
                if(ownerRide!=null){
                    ownerRide.setNumSits((Integer.parseInt(ownerRide.getNumSits())+1) + "");
                    Model.instance.updateRide(ownerRide,(isSuccess)->{
                        if(isSuccess){
                            customerRide.setDeleted(true);
                            Model.instance.updateRide(customerRide,(isSuccess2)->{
                                adapter.notifyItemRemoved(position);
                            });
                        }
                    });
                }

            }
        });
        return view;
    }


    private void setUpProgressListener() {
        Model.instance.loadingState.observe(getViewLifecycleOwner(),(state)->{
            switch(state){
                case loaded:
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case loading:
                    swipeRefreshLayout.setRefreshing(true);
                    break;
            }
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        OnItemClickListener listener;
        TextView nameTv,dateTv,timeTv,numTv, descriptionTv;
        ImageView imageV;
        Button cancel;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageV = itemView.findViewById(R.id.myCatches_image_imgv);
            nameTv = itemView.findViewById(R.id.myCatches_name_tv);
            dateTv = itemView.findViewById(R.id.myCatches_date_tv);
            timeTv = itemView.findViewById(R.id.myCatches_time_tv);
            numTv = itemView.findViewById(R.id.myCatches_numOfSits_tv);
            descriptionTv = itemView.findViewById(R.id.myCatches_discription_tv);
            cancel = itemView.findViewById(R.id.myCatches_cancel_btn);

            this.listener=listener;
            cancel.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onCancelClick(position);
                    }
                }
            });
        }

        public void bind(Ride ride){
            nameTv.setText(ride.getDriverName());
            dateTv.setText(ride.getDate());
            timeTv.setText(ride.getTime());
            numTv.setText(ride.getNumSits());
            descriptionTv.setText(ride.getDescription());
            imageV.setImageResource(R.drawable.car_avatar);
            if(ride.getImage()!=null && !ride.getImage().equals("")){
                Picasso.get().load(ride.getImage()).placeholder(R.drawable.car_avatar).into(imageV);
            }
        }
    }

    public interface OnItemClickListener{
        void onCancelClick(int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        OnItemClickListener listener;

        public void setOnClickListener(OnItemClickListener listener){
            this.listener=listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= getLayoutInflater().inflate(R.layout.my_catches_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Ride ride = myCatchesViewModel.list.get(position);
            holder.bind(ride);
        }

        @Override
        public int getItemCount() {
            return myCatchesViewModel.list.size();
        }
    }
}