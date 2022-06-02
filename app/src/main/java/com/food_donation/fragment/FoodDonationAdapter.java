

package com.food_donation.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.food_donation.R;
import com.food_donation.model.FoodModel;
import com.food_donation.utilities.Config;

import java.util.List;

public class FoodDonationAdapter extends RecyclerView.Adapter<FoodDonationAdapter.ViewHolder> {
    private List<FoodModel> foodDonationModelList;
    Context context;
    String tag;

    public FoodDonationAdapter(List<FoodModel> foodDonationModelList, Context context, String tag) {
        this.foodDonationModelList = foodDonationModelList;
        this.context = context;
        this.tag = tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_food_donation, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String donate_id = foodDonationModelList.get(i).getDonate_id();
        String donor_id = foodDonationModelList.get(i).getDonor_id();
        String food = foodDonationModelList.get(i).getFood();
        String type = foodDonationModelList.get(i).getType();
        String qty = foodDonationModelList.get(i).getQty();
        String address = foodDonationModelList.get(i).getAddress();
        String image = foodDonationModelList.get(i).getImage();
        String assign_to = foodDonationModelList.get(i).getAssign_to();
        String posted_date = foodDonationModelList.get(i).getPosted_date();
        String assign_volunteer = foodDonationModelList.get(i).getAssign_volunteer();

        viewHolder.setData(i, donate_id, donor_id, food, type, qty, address, image, assign_to, posted_date, assign_volunteer);
    }

    @Override
    public int getItemCount() {
        return foodDonationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView food_image;
        private TextView food_title, food_posted_date, food_status;
        private Button food_btn_next;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            food_image = itemView.findViewById(R.id.food_image);
            food_title = itemView.findViewById(R.id.food_title);
            food_posted_date = itemView.findViewById(R.id.food_posted_date);
            food_btn_next = itemView.findViewById(R.id.food_btn_next);
            food_status = itemView.findViewById(R.id.food_status);
        }

        private void setData(final int pos, final String donate_id, final String donor_id, final String food, final String type, final String qty, final String address, final String image, final String assign_to, final String posted_date, final String assign_volunteer) {
            Glide.with(context).load(Config.ROOT_IMAGE_URL + image).placeholder(R.drawable.loading).error(R.drawable.default_image).into(food_image);
            food_title.setText(food);
            food_posted_date.setText(posted_date);
            if (assign_volunteer.equals("1")) {
                food_status.setText("Completed");
                food_status.setTextColor(Color.GREEN);
            } else if (assign_to.equals("0")) {
                food_status.setText("Pending");
                food_status.setTextColor(Color.RED);
            } else {
                food_status.setText("Accepted");
                food_status.setTextColor(Color.YELLOW);
            }
            if (tag.equals("VolunteerHome")) {
                food_btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new FoodDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("donate_id", donate_id);
                        bundle.putString("donor_id", donor_id);
                        bundle.putString("food", food);
                        bundle.putString("type", type);
                        bundle.putString("qty", qty);
                        bundle.putString("address", address);
                        bundle.putString("image", image);
                        bundle.putString("assign_to", assign_to);
                        bundle.putString("posted_date", posted_date);
                        bundle.putString("assign_volunteer", assign_volunteer);
                        bundle.putString("tag", tag);
                        myFragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.volunteer_fragment_container, myFragment).commit();

                    }
                });
            } else if (tag.equals("DonorHome")) {
                food_btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new FoodDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("donate_id", donate_id);
                        bundle.putString("donor_id", donor_id);
                        bundle.putString("food", food);
                        bundle.putString("type", type);
                        bundle.putString("qty", qty);
                        bundle.putString("address", address);
                        bundle.putString("image", image);
                        bundle.putString("assign_to", assign_to);
                        bundle.putString("posted_date", posted_date);
                        bundle.putString("assign_volunteer", assign_volunteer);
                        myFragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment_container, myFragment).commit();
                    }
                });
            } else if (tag.equals("DonorHome")) {
                food_btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new FoodDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("donate_id", donate_id);
                        bundle.putString("donor_id", donor_id);
                        bundle.putString("food", food);
                        bundle.putString("type", type);
                        bundle.putString("qty", qty);
                        bundle.putString("address", address);
                        bundle.putString("image", image);
                        bundle.putString("assign_to", assign_to);
                        bundle.putString("posted_date", posted_date);
                        bundle.putString("assign_volunteer", assign_volunteer);
                        myFragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.volunteer_fragment_container, myFragment).commit();
                    }
                });
            } else if (tag.equals("VolunteerPreviousDonation")) {
                food_btn_next.setVisibility(View.GONE);
            }
        }


    }


}
