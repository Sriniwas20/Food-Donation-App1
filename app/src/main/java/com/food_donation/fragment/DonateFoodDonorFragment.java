package com.food_donation.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.food_donation.DonorDashBoardActivity;
import com.food_donation.R;
import com.food_donation.utilities.Base64;
import com.food_donation.utilities.Config;
import com.food_donation.utilities.Utility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DonateFoodDonorFragment extends Fragment {
    View v;
    Button bDonateFood;
    ImageButton btakephoto, btakelocation;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask, img = "", imagename = "";
    Bitmap thumbnail;
    Spinner sptype;
    String name, qty, type, address;
    EditText etFood, etQty, etaddress;
    private ProgressDialog mProgress;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    double latitude, longitude;
    SharedPreferences pref;
    String donarId;
    ImageView ivfood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_donate_food_donor, container, false);
        pref = getActivity()
                .getSharedPreferences("UserDetails", MODE_PRIVATE);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle("Processing.");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        donarId = pref.getString("id", "");
        etFood = v.findViewById(R.id.etFood);
        etQty = v.findViewById(R.id.etQty);
        etaddress = v.findViewById(R.id.etaddress);
        ivfood = v.findViewById(R.id.ivfood);
        sptype = v.findViewById(R.id.spType);
        btakephoto = v.findViewById(R.id.baddphoto);
        btakelocation = v.findViewById(R.id.bTakeLocation);
        bDonateFood = v.findViewById(R.id.bDonateFood);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();
        ArrayAdapter<CharSequence> adaptercType = ArrayAdapter.createFromResource(getActivity(),
                R.array.type, android.R.layout.simple_spinner_item);
        adaptercType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sptype.setAdapter(adaptercType);
        sptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                type = sptype.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        // method to get the location
        Log.d("ID", donarId);

        btakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btakelocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();

            }
        });
        bDonateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etFood.getText().toString();
                qty = etQty.getText().toString();
                address = etaddress.getText().toString();
                if (name.equals(""))
                    etFood.setError("Enter Food");
                else if (qty.equals(""))
                    etQty.setError("Enter Qty");
                else if (address.equals(""))
                    etaddress.setError("Enter Address");
                else if (imagename.equals(""))
                    showAlertBox("Error", "Please Upload image");
                else
                    registerData();

            }
        });

        return v;
    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void registerData() {
        Log.d("Profile pic", img + "----" + imagename);
        mProgress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_USER_DONATE_FOOD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mProgress.dismiss();
                            JSONObject json = new JSONObject(response);
                            if (json.getString("success").equals("true")) {
                                showAlertBox(json.getString("success"), json.getString("message"));
                            } else if (json.getString("success").equals("false")) {
                                showAlertBox(json.getString("success"), json.getString("message"));
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
                        mProgress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", donarId);
                params.put("name", name);
                params.put("qty", qty);
                params.put("type", type);
                params.put("address", address);
                params.put("profilepic", img);
                params.put("imagename", imagename);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 20000;//20 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void showAlertBox(final String s, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage(msg);
        if (s.equals("true"))
            alert.setTitle("Congrats");
        else if (s.equals("false"))
            alert.setTitle("Error");
        else if (s.equals("Error"))
            alert.setTitle("Error");

        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (s.equals("true")) {
                    startActivity(new Intent(getActivity(), DonorDashBoardActivity.class));
                } else if (s.equals("false")) {
                    dialogInterface.dismiss();
                }
            }
        });
        alert.create().show();

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        requestNewLocationData();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            getCompleteAddressString(latitude, longitude);
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My address", strReturnedAddress.toString());
                etaddress.setText(strAdd);
            } else {
                Log.w("My address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                onSelectFromGalleryResult(data);

            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        imagename = destination.getName();

        Log.d("Name of image", imagename);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivfood.setImageBitmap(thumbnail);

        img = convertBitmapToString(thumbnail);
        Log.d("Img in String------", img);

    }

    public String convertBitmapToString(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte[] byte_arr = stream.toByteArray();
        String imageStr = Base64.encodeBytes(byte_arr);
        Log.d("Image in String------", imageStr);
        return imageStr;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                imagename = data.getData().getLastPathSegment() + ".jpg";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivfood.setImageBitmap(thumbnail);
        img = convertBitmapToString(thumbnail);

    }
}