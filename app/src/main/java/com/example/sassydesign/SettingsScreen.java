package com.example.sassydesign;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingsScreen extends Fragment {
    int replay = 0;
    TextView userName;
    EditText budgetText;
    ImageView profileImage;
    TextView userID;
    String personName = "";
    String photoNAME = "";
    String budget = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.settings, container, false);

        userName = rootView.findViewById(R.id.userName);
        budgetText = rootView.findViewById(R.id.budget);
        profileImage = rootView.findViewById(R.id.profileImage);
        userID = rootView.findViewById(R.id.userID);

        setProfile();

        //로그아웃 버튼
        Button logOutButton = rootView.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 버튼 누르면

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_two_button);
                TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_title);
                TextView dialogMessage = (TextView)dialog.findViewById(R.id.dialog_message);
                dialogTitle.setText("로그아웃");
                dialogMessage.setText("로그아웃 하시겠습니까?");

                //취소 버튼
                Button dialogButton = (Button)dialog.findViewById(R.id.dialog_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //확인 버튼
                Button dialogButton2 = (Button)dialog.findViewById(R.id.dialog_button2);
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });

        Button withdrawalButton = rootView.findViewById(R.id.withdrawalButton);
        withdrawalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_two_button);
                TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_title);
                TextView dialogMessage = (TextView)dialog.findViewById(R.id.dialog_message);
                dialogTitle.setText("탈퇴");
                dialogMessage.setText("정말 탈퇴하시겠습니까?");

                //취소 버튼
                Button dialogButton = (Button)dialog.findViewById(R.id.dialog_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //확인 버튼
                Button dialogButton2 = (Button)dialog.findViewById(R.id.dialog_button2);
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://192.168.0.20:3000/process/deleteuser";

                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    JSONObject jsonResponse = new JSONObject(response);
                                    Boolean success=jsonResponse.getBoolean("success");

                                    if(success){

                                        //로그인 화면으로 돌아가기
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                    else{

                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> parameters = new HashMap<>();
                                parameters = new HashMap<>();
                                parameters.put("id", MainActivity.ID);

                                return parameters;
                            }

                        };
                        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(

                                20000 ,

                                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        request.setShouldCache(false);
                        Volley.newRequestQueue(getContext()).add(request);
                    }
                });
                dialog.show();
            }
        });
        //예산 저장버튼 누르면 데이터베이스에 저장
        Button saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.0.20:3000/process/setbudget";
                String budget = budgetText.getText().toString();

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //여기서 예산 저장됐다고 띄워도 되는건가..?
                            Toast.makeText(getContext(), "예산이 저장되었습니다", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parameters = new HashMap<>();
                        parameters = new HashMap<>();
                        parameters.put("id", MainActivity.ID);
                        System.out.println("예산 지금 얼마야??????"+budget);
                        parameters.put("budget", budget);



                        return parameters;
                    }

                };
                request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(

                        20000 ,

                        com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                        com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                Volley.newRequestQueue(getContext()).add(request);
            }
        });

        Button profileModifyButton = rootView.findViewById(R.id.profileModifyButton);
        profileModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("닉네임뭐야!!!!!!!!!!"+personName);
                Intent profileModifyIntent = new Intent(getActivity(), ProfileModify.class);
                profileModifyIntent.putExtra("name", personName);
                profileModifyIntent.putExtra("photolink", photoNAME);
                startActivity(profileModifyIntent);
            }
        });

        return rootView;
    }

    public void setProfile(){
        String url = "http://192.168.0.20:3000/process/showuser";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONArray jsonArray=new JSONArray(result);
                    ArrayList<String> NameList = new ArrayList<String>();
                    ArrayList<String> IdList = new ArrayList<String>();
                    ArrayList<String> budgetList = new ArrayList<String>();
                    ArrayList<String> photonameList = new ArrayList<String>();
                    String name = "";
                    String Id ="";
                    String photoname = "";
                    String budget3 = "";

                    int count = 0;
                    JSONObject object = jsonArray.getJSONObject(0);

                    name=object.getString("name");
                    Id= object.getString("id");
                    photoname = object.getString("photoname");
                    budget3 = object.getString("budget");
                    System.out.println(name+Id+photoname+budget3);
                    // IdList.add(Id);
                    // NameList.add(name);
                    // photonameList.add(photoname);
                    // budgetList.add(budget3);

                    //ArrayList<String> idList=new ArrayList<String>();

                    personName = name;
                    //데이터베이스에서 사용자 이름 가져오기!!
                    //가져온 이름 = name
                    userName.setText(personName);



                    //데이터베이스에서 사용자 아이디 가져오기!!
                    //가져온 아이디 = id
                    userID.setText(MainActivity.ID);


                    //프로필 이미
                    System.out.println(photoname);
                    if (photoname.isEmpty()||photoname.equals("")) {
                        profileImage.setImageResource(R.drawable.default_profile_image);
                    } else{
                        Picasso.with(getContext())
                                .load(photoname)
                                .into(profileImage);
                    }
                    //데이터베이스에 프로필이미지 URI가 있으면 가져와서 설정해야해요
                    //데이터베이스에 프로필 이미지 URI가 있으면 밑의 코드 활성화!!
                    //profileImage.setImageURI(Uri.parse(photo));


                    //데베에 저장되어있는 예산 가져오기

                    //데이터베이스에 저장된 예산 있으면 가져와서 설정해주세요
                    budget = budget3;


                    photoNAME = photoname;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                setText();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters = new HashMap<>();
                parameters.put("id", MainActivity.ID);

                return parameters;
            }

        };

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000 ,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        Volley.newRequestQueue(getContext()).add(request);
    }

    public void setText(){
        userName.setText(personName);
        budgetText.setText(budget);
        userID.setText(MainActivity.ID);

        System.out.println(photoNAME);
        if (photoNAME.isEmpty()||photoNAME.equals("")) {
            profileImage.setImageResource(R.drawable.default_profile_image);
        } else{
            Picasso.with(getContext())
                    .load(photoNAME)
                    .into(profileImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (replay != 0){
            //프로필사진이랑 이름 불러와주는 함수 붙이기
            setProfile();
            System.out.println("onResume 호출");
        }
        replay = 1;
    }
}