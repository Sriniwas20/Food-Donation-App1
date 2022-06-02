package com.food_donation.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.food_donation.R;
import com.food_donation.model.FoodModel;
import com.food_donation.utilities.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class VolunteerPreviousDonationFragment extends Fragment {
    View v;
    RecyclerView RecyclerView;
    String volunteer_id,donate_id,donor_id, food, type, qty, address, image, assign_to, posted_date, assign_volunteer;
    List<FoodModel> modelList = new ArrayList<>();
    SharedPreferences pref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_volunteer_previous_donation, container, false);
        RecyclerView = v.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager((getContext()));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.setLayoutManager(layoutManager);

        pref = getActivity()
                .getSharedPreferences("UserDetails", MODE_PRIVATE);
        volunteer_id = pref.getString("id", "");
        getFoodData();
        return v;
    }

    void getFoodData() {
        //creating a string request to send request to the url
        StringRequest request = new StringRequest(Request.Method.POST, Config.URL_VOLUNTEER_PREVIOUS_COMPLETED_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        //getting the whole json object from the response
                        try {

                            JSONObject obj = new JSONObject(response);
                            //we have the array named json inside the object
                            //so here we are getting that json array
                            JSONArray jsonArray = obj.getJSONArray("record");
                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("is_completed").equals("1")) {
                                    donate_id = jsonObject.getString("id");
                                    donor_id = jsonObject.getString("donor_id");
                                    food = jsonObject.getString("food");
                                    type = jsonObject.getString("type");
                                    qty = jsonObject.getString("qty");
                                    address = jsonObject.getString("address");
                                    image = jsonObject.getString("image");
                                    assign_to = jsonObject.getString("assign_to");
                                    posted_date = jsonObject.getString("posted_date");
                                    assign_volunteer = jsonObject.getString("is_completed");
                                    FoodModel model = new FoodModel(donate_id,donor_id, food, type, qty, address, image, assign_to, posted_date, assign_volunteer);
                                    modelList.add(model);
                                }
                            }
                            FoodDonationAdapter adapter = new FoodDonationAdapter(modelList, getActivity(), "VolunteerPreviousDonation");
                            RecyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("volunteer_id", volunteer_id);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);

        request.setRetryPolicy(new

                DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

    }
}