package com.example.sassydesign;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileModify extends AppCompatActivity {

    Boolean CheckBlank = false;
    String personName = "";
    String personLink = "";
    ImageView profileImage;
    String firebaseurl = "";
    String newPersonName = "";
    Uri file;
    private final int GALLERY_CODE = 10;
    private FirebaseStorage storage;

    public static Activity profileModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modify);

        profileModify = ProfileModify.this;

        //프로필 이미지
        profileImage = findViewById(R.id.profileImage);
        storage = FirebaseStorage.getInstance();

        //이름 설정창에서 넘겨받아서 넣어놓기
        EditText personNameEditText = findViewById(R.id.personNameEditText);

        Intent fromSettingsIntent = getIntent();
        personName = fromSettingsIntent.getStringExtra("name");
        personLink = fromSettingsIntent.getStringExtra("photolink");
        firebaseurl=personLink;
        newPersonName=personName;
        personNameEditText.setText(personName);
        if (personLink.isEmpty() || personLink==null) {
            profileImage.setImageResource(R.drawable.default_profile_image);
        } else{
            Picasso.with(this)
                    .load(personLink)
                    .into(profileImage);
        }



        //프로필 변경버튼 누르면 파이어베이스에 올리기!!!
        Button profileImageButton = findViewById(R.id.profileImageButton);
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 10);
            }
        });

        //프로필이미지 삭제버튼
        Button profileImageDeleteButton = findViewById(R.id.profileImageDeleteButton);
        profileImageDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프로필 이미지 삭제 버튼을 누르면, 파이어베이스에 올려놓은 default_profile_image를 기본 이미지로 넘겨준다.
                firebaseurl="https://firebasestorage.googleapis.com/v0/b/storage-2ccab.appspot.com/o/photo%2Fdefault_profile_image.png?alt=media&token=6bbd11ad-356b-44c0-beb7-4a966542033d"; //default_profile_image.png링크
                profileImage.setImageResource(R.drawable.default_profile_image);
            }
        });

        //수정 완료버튼

        Button profileModityCompleteButton = findViewById(R.id.profileModityCompleteButton);
        profileModityCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 새로 설정한 이름
                newPersonName = personNameEditText.getText().toString();
                //이름 없으면 채우기
                if(newPersonName.equals("")) {
                    CheckBlank = false;
                    Dialog dialog = new Dialog(ProfileModify.this);
                    dialog.setContentView(R.layout.dialog_common);
                    TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_title);
                    TextView dialogMessage = (TextView)dialog.findViewById(R.id.dialog_message);
                    dialogTitle.setText("빈칸을 채워주세요");
                    dialogMessage.setText("이름을 작성해주세요");
                    Button dialogButton = (Button)dialog.findViewById(R.id.dialog_button);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else {
                    CheckBlank = true;
                    String url = "http://192.168.0.20:3000/process/setphoto";

                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonResponse = new JSONObject(response);
                                String photoname=jsonResponse.getString("photoname");

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
                            parameters.put("id",MainActivity.ID);
                            parameters.put("name", newPersonName);
                            parameters.put("photoname",firebaseurl);

                            return parameters;
                        }

                    };
                    request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                            20000 ,
                            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    request.setShouldCache(false);
                    Volley.newRequestQueue(ProfileModify.this).add(request);
                    //데이터베이스에 firebaseurl이랑 userPersonName 업로드 해주세여!!!
                    //사용자가 프로필사진을 없애면 데이터베이스에서 저장되어있는 url값을 없애줄수 있나요..?
                   /* if(firebaseurl.equals("")) {
                        //url없애는게 있으면 그렇게 해주세여
                    }*/


                    //데이터베이스에 업로드 하고 나서
                    //액티비티 종료
                    if (CheckBlank == true) {
                        ProfileModify PA = (ProfileModify) ProfileModify.profileModify;
                        PA.finish();
                    }
                }
            }
        });

    }

    //갤러리 연동코드
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE) {
            file = data.getData();

            StorageReference storageRef = storage.getReference();

            StorageReference riversRef = storageRef.child("photo/" + System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = riversRef.putFile(file);
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                profileImage.setImageBitmap(img);
                Toast.makeText(ProfileModify.this, "사진이 업로드 중입니다 기다려주세요", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ProfileModify.this, "사진이 정상적으로 업로드 되지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(ProfileModify.this, "사진 정상 업로드 성공", Toast.LENGTH_SHORT).show();
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();
                    firebaseurl = String.valueOf(downloadUrl);
                    System.out.println("firebaseurl******" + firebaseurl);
                }
            });
        }
    }



}