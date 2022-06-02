package com.food_donation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.food_donation.utilities.Base64;
import com.food_donation.utilities.Config;
import com.food_donation.utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolunteerSignupActivity extends AppCompatActivity {
    Button bregister, blogin;
    ImageButton btakephoto;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask, img = "", imagename = "";
    ImageView ivuserimage;
    Bitmap thumbnail;
    Spinner spstate;
    String name, mbno, address1, address2, state, city, password, repassword;
    EditText etname, etmbno, etaddress1, etaddress2, etcity, etpassword, etreconfirmpassword;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_signup);
        ivuserimage = (ImageView) findViewById(R.id.ivuser);
        mProgress = new ProgressDialog(VolunteerSignupActivity.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        etname= findViewById(R.id.etname);
        etmbno = findViewById(R.id.etmbno);
        etaddress1 = findViewById(R.id.etaddress1);
        etaddress2 = findViewById(R.id.etaddress2);
        etpassword = findViewById(R.id.etpassword);
        etreconfirmpassword = findViewById(R.id.etreconfirmpassword);

        spstate = findViewById(R.id.spstate);
        etcity = findViewById(R.id.etcity);
        btakephoto = findViewById(R.id.baddphoto);
        bregister = findViewById(R.id.bregister);

        ArrayAdapter<CharSequence> adaptercollege = ArrayAdapter.createFromResource(VolunteerSignupActivity.this,
                R.array.india_states, android.R.layout.simple_spinner_item);
        adaptercollege.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spstate.setAdapter(adaptercollege);

        spstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                state = spstate.getItemAtPosition(pos).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etname.getText().toString();
                address1 = etaddress1.getText().toString();
                address2 = etaddress2.getText().toString();
                mbno = etmbno.getText().toString();
                city = etcity.getText().toString();
                password = etpassword.getText().toString();
                repassword = etreconfirmpassword.getText().toString();
                if (name.equals(""))
                    etname.setError("Enter Name ");
                else if (mbno.equals(""))
                    etmbno.setError("Enter Mobile No");
                else if (mbno.length()!=10)
                    etmbno.setError("Enter valid Number");
                else if (address1.equals(""))
                    etaddress1.setError("Enter Address");
                else if (address2.equals(""))
                    etaddress2.setError("Enter Address 2");
                else if (state.equals(""))
                    Toast.makeText(VolunteerSignupActivity.this, "Select State", Toast.LENGTH_SHORT).show();
                else if (city.equals(""))
                    etcity.setError("Enter City");
                else if (password.equals(""))
                    etcity.setError("Enter Password");
                else if (repassword.equals(""))
                    etcity.setError("Enter Reconfirm Password");
                else if (!(password.equals(repassword)))
                    showAlertBox("Error", "Password does not match");
                else if (imagename.equals(""))
                    showAlertBox("Error", "Please Upload image");

                else {
                    registerData();
                }
            }
        });

        btakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }


    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(VolunteerSignupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(VolunteerSignupActivity.this);

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

    public void registerData() {
        mProgress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_VOLUNTEER_REGISTER,
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
                        Toast.makeText(VolunteerSignupActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("mbno", mbno);
                params.put("profilepic", img);
                params.put("imagename", imagename);
                params.put("address1", address1);
                params.put("address2", address2);
                params.put("state", state);
                params.put("city", city);
                params.put("password", password);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 20000;//20 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }
    private void showAlertBox(final String s, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(VolunteerSignupActivity.this);
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
                    startActivity(new Intent(VolunteerSignupActivity.this, HomePage.class));
                } else if (s.equals("false")) {
                    dialogInterface.dismiss();
                }
            }
        });
        alert.create().show();

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
        ivuserimage.setImageBitmap(thumbnail);

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
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                imagename = data.getData().getLastPathSegment() + ".jpg";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivuserimage.setImageBitmap(thumbnail);
        img = convertBitmapToString(thumbnail);

    }
}