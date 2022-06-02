package com.food_donation;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.food_donation.utilities.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginDonorActivity extends AppCompatActivity {

    private EditText logMobile, logPassword;
    private Button logButton, signupButton;
    private TextView tvSIGNUP, tvforgotpassword;

    private ProgressDialog progressDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Intent i;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_donor);
        i = getIntent();
        role = i.getStringExtra("role");
        logButton = findViewById(R.id.logButton);
        signupButton = findViewById(R.id.signButton);
        logMobile = findViewById(R.id.LogMobile);
        logPassword = findViewById(R.id.LogPassword);

//        tvSIGNUP = findViewById(R.id.tvsignup);
        tvforgotpassword = findViewById(R.id.tvforgotpassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        if (role.equals("donor"))
            signupButton.setVisibility(View.VISIBLE);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginDonorActivity.this, DonorSignupActivity.class));
            }
        });
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Mobile = logMobile.getText().toString();
                String password = logPassword.getText().toString();
                if (Mobile.equals("")) {
                    logMobile.setError("Mobile please");
                } else if (password.equals("")) {
                    logPassword.setError("Password please");
                } else if (Mobile.length() != 10)
                    logMobile.setError("Please enter proper Mobile No");
                else if (!isValidPassword(password))
                    logPassword.setError("Please enter proper Password");
                else if (role.equals("donor")) {
                    volunteerLog(Mobile, password);
                }
            }
        });
        tvforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(LoginActivity.this,forgetpassword.class);
                startActivity(i);*/
            }
        });
    }

    public void onStart() {
        super.onStart();


    }

    public boolean isValidEmailAddress(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    //User LOG
    private void volunteerLog(final String Mobile, final String Password) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {
                            Log.d("Res", response);
                            progressDialog.dismiss();
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.get("status").equals("true")) {
                                    JSONObject jsonObject = new JSONObject(obj.get("record").toString());

                                    String id = jsonObject.getString("id");
                                    String name = jsonObject.getString("name");
                                    String image = jsonObject.getString("image");
                                    String mobileno = jsonObject.getString("mobile_no");


                                    pref = getApplicationContext()
                                            .getSharedPreferences("UserDetails", MODE_PRIVATE);
                                    editor = pref.edit();
                                    editor.putString("id", id);
                                    editor.putString("name", name);
                                    editor.putString("image", image);
                                    editor.putString("mobileno", mobileno);

                                    editor.commit();
                                    Toast toast = Toast.makeText(LoginDonorActivity.this, "Login Success!!!", Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    view.setBackgroundColor(Color.GREEN);
                                    toast.show();
                                    Intent i = new Intent(LoginDonorActivity.this, DonorDashBoardActivity.class);
                                    Bundle bndlanimation =
                                            ActivityOptions.makeCustomAnimation(LoginDonorActivity.this, R.anim.slide_from_left, R.anim.slide_to_right).toBundle();

                                    startActivity(i, bndlanimation);
                                    finish();

                                } else if (obj.get("status").equals("false"))
                                    showAlertBox(obj.get("message").toString());

                            } catch (JSONException e) {
                                e.printStackTrace();

                            } catch (Exception e) {
                                progressDialog.dismiss();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginDonorActivity.this, "Invalid Username and Password!!", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginDonorActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", Mobile);
                params.put("password", Password);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void showAlertBox(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginDonorActivity.this);
        alert.setMessage(msg);
        alert.setTitle("Login Failed");
        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.create().show();
    }
}