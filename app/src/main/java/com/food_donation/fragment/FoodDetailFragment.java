package com.food_donation.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.food_donation.R;
import com.food_donation.VolunteerDashBoardActivity;
import com.food_donation.utilities.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FoodDetailFragment extends Fragment {
    TextView tv_food, tv_type, tv_qty, tv_address, tv_assign_to, tv_posted_date;
    String tag = "", donate_id, donor_id, food, type, qty, status, assign_to, address, image, is_completed, posted_date;
    View v;
    ImageView iv_pic;
    Button bTaskCompleteByVolunteer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_food_detail, container, false);
        tv_food = v.findViewById(R.id.tv_food);
        tv_type = v.findViewById(R.id.tv_type);
        tv_qty = v.findViewById(R.id.tv_qty);
        tv_address = v.findViewById(R.id.tv_address);
        tv_posted_date = v.findViewById(R.id.tv_posted_date);
        tv_assign_to = v.findViewById(R.id.tv_status);
        iv_pic = v.findViewById(R.id.iv_pic);
        bTaskCompleteByVolunteer = v.findViewById(R.id.bTaskCompleteByVolunteer);
        donate_id = getArguments().getString("donate_id");
        donor_id = getArguments().getString("donor_id");
        food = getArguments().getString("food");
        type = getArguments().getString("type");
        qty = getArguments().getString("qty");
        address = getArguments().getString("address");
        image = getArguments().getString("image");
        assign_to = getArguments().getString("assign_to");
        posted_date = getArguments().getString("posted_date");
        is_completed = getArguments().getString("assign_volunteer");
        if (getArguments().getString("tag") != null) {
            tag = getArguments().getString("tag");
            bTaskCompleteByVolunteer.setVisibility(View.VISIBLE);
        }
        tv_food.setText(food);
        tv_type.setText(type);
        tv_qty.setText(qty);
        tv_address.setText(address);
        tv_posted_date.setText(posted_date);
        if (is_completed.equals("1")) {
            tv_assign_to.setText("Completed");
            tv_assign_to.setTextColor(Color.GREEN);
        } else if (assign_to.equals("0")) {
            tv_assign_to.setText("Pending");
            tv_assign_to.setTextColor(Color.RED);

        } else {
            tv_assign_to.setText("Accepted");
            tv_assign_to.setTextColor(Color.YELLOW);
        }
        bTaskCompleteByVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_completed.equals("1"))
                    updateTask("0");
                else
                    updateTask("1");

            }
        });
        Glide.with(this).load(Config.ROOT_IMAGE_URL + image).diskCacheStrategy(DiskCacheStrategy.ALL) //use this to cache
                .centerCrop()
                .placeholder(R.drawable.loading).error(R.drawable.default_image).into(iv_pic);
        return v;
    }

    public void updateTask(final String complete_status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_VOLUNTEER_UPDATE_TASK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("success").equals("true")) {
                                Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), VolunteerDashBoardActivity.class));
                            } else if (json.getString("success").equals("false")) {
                                Toast.makeText(getActivity(), "Rejected", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("donate_id", donate_id);
                params.put("is_completed", complete_status);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 20000;//20 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
}

