package com.example.acatch.ui.my_rides;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
import com.example.acatch.ui.my_catches.MyCatches;
import com.example.acatch.ui.my_catches.MyCatchesViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MyRides extends Fragment {


    View view;
    MyRidesViewModel myRidesViewModel;
    SwipeRefreshLayout swipeRefreshLayout;
    MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        swipeRefreshLayout = view.findViewById(R.id.myRides_refresh);
        setUpProgressListener();
        swipeRefreshLayout.setOnRefreshListener(()->myRidesViewModel.refresh());

        myRidesViewModel  = new ViewModelProvider(this).get(MyRidesViewModel.class);
        myRidesViewModel.getData().observe(getViewLifecycleOwner(),
                (data)->{
                    myRidesViewModel.list=myRidesViewModel.filterByUser(data);
                    adapter.notifyDataSetChanged();
                });


        //RecyclerView:
        RecyclerView ridesList = view.findViewById(R.id.myRides_recyclerView);
        ridesList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.context);
        ridesList.setLayoutManager(manager);
        adapter = new MyAdapter();
        ridesList.setAdapter(adapter);

        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onCancelClick(int position) {
                List<Ride> ownerRides = myRidesViewModel.getRidesToDelete(myRidesViewModel.list.get(position));
                if (ownerRides.size()>0)
                {
                    for (Ride r: ownerRides)
                    {
                        r.setDeleted(true);
                        Model.instance.updateRide(r, (isSuccess) -> {
                            if (isSuccess)
                                adapter.notifyItemRemoved(position);
                        });
                    }
                }
            }

            @Override
            public void onEditClick(int position) {
                Ride ride = myRidesViewModel.list.get(position);
                MyRidesDirections.ActionNavMyRidesToNavEditRide action = MyRidesDirections.actionNavMyRidesToNavEditRide(ride.getId());
                Navigation.findNavController(view).navigate(action);
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
        ImageView imageV, editImgV;
        Button cancel;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            imageV = itemView.findViewById(R.id.ridesListRow_image_imgv);
            editImgV = itemView.findViewById(R.id.ridesListRow_edit_imgV);
            nameTv = itemView.findViewById(R.id.ridesListRow_name_tv);
            dateTv = itemView.findViewById(R.id.ridesListRow_date_tv);
            timeTv = itemView.findViewById(R.id.ridesListRow_time_tv);
            numTv = itemView.findViewById(R.id.ridesListRow_numOfSits_tv);
            descriptionTv = itemView.findViewById(R.id.ridesListRow_discription_tv);
            cancel = itemView.findViewById(R.id.ridesListRow_cancel_btn);

            this.listener=listener;
            cancel.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onCancelClick(position);
                    }
                }
            });
            editImgV.setOnClickListener(v -> {
                if(listener!=null){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onEditClick(position);
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
        void onEditClick(int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        OnItemClickListener listener;

        public void setOnClickListener(OnItemClickListener listener){
            this.listener=listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= getLayoutInflater().inflate(R.layout.rieds_list_row,parent,false);
            MyViewHolder holder= new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Ride ride = myRidesViewModel.list.get(position);
            holder.bind(ride);
        }

        @Override
        public int getItemCount() {
            return myRidesViewModel.list.size();
        }
    }
}