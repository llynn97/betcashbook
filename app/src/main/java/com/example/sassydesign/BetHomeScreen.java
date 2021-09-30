package com.example.sassydesign;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BetHomeScreen extends Fragment {

    Button betGroupAddButton;
    Button betGroupInviteButton;
    RecyclerView groupList;

    GroupAdapter groupAdapter;

    int replay = 0; //OnResume을 위한 변수
    int replay2 = 0;

    String url = "http://192.168.0.20:3000/process/invitebetcashbook";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bet_group_list, container, false);

        groupAdapter = new GroupAdapter();
        //ids = new ArrayList<ArrayList<String>>();
        //idexample = new ArrayList<String>();

        //+버튼 누르면 그룹 추가 창 띄우기
        betGroupAddButton = rootView.findViewById(R.id.betGroupAddButton);
        betGroupAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupAddActivity.class);
                startActivity(intent);
            }
        });

        //리사이클러뷰 리니어매니저로 설정하기
        groupList = rootView.findViewById(R.id.betGroupListRecyclerView);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        groupList.setLayoutManager(layoutManager);

        betGroupInviteButton = rootView.findViewById(R.id.betGroupInviteButton);
        betGroupInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        if(replay2 == 0){
            setItemObject();
            replay2 = 1;
        }

        return rootView;
    }

    private void showAlertDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_invite, null);
        final EditText inviteCode = (EditText)dialogView.findViewById(R.id.inviteCode);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);


        builder.setPositiveButton("입장하기", new DialogInterface.OnClickListener() {
            String value = "";
            @Override
            public void onClick(DialogInterface dialog, int which) {

                value = inviteCode.getText().toString();
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject object=new JSONObject(result);
                            System.out.println("result: "+result);

                            if(object.isNull("groupname")){
                                System.out.println("내기가계부가 중복되었습니다");
                                //*****************************************************************************
                                //사용자가 이미 초대되어있는 내기가계부의 랜덤코드로 또 입장하기를 눌렀을때
                                //이미 초대된 내기가계부라는 얼럿창을 띄워주라

                                Dialog dialog = new Dialog(getContext());
                                dialog.setContentView(R.layout.dialog_common);
                                TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_title);
                                TextView dialogMessage = (TextView)dialog.findViewById(R.id.dialog_message);
                                Button dialogButton = (Button)dialog.findViewById(R.id.dialog_button);

                                dialogTitle.setText("가계부가 중복되었습니다.");
                                dialogMessage.setText("이미 초대된 내기가계부입니다.");
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                            else if(object.getString("groupname").equals("noinvitecode")) {
                                Dialog dialog = new Dialog(getContext());
                                dialog.setContentView(R.layout.dialog_common);
                                TextView dialogTitle = (TextView)dialog.findViewById(R.id.dialog_title);
                                TextView dialogMessage = (TextView)dialog.findViewById(R.id.dialog_message);
                                Button dialogButton = (Button)dialog.findViewById(R.id.dialog_button);

                                dialogTitle.setText("가계부가 존재하지 않습니다.");
                                dialogMessage.setText("입력된 초대코드에 해당하는 내기가계부가 없습니다.");
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                            else {

                                ArrayList<String> idList=new ArrayList<String>();
                                ArrayList<String> idid=new ArrayList<String>();
                                ArrayList<String> intentgroup=new ArrayList<String>();

                                //JSONObject object = jsonArray.getJSONObject(0);
                                String groupname=object.getString("groupname");
                                String goal = object.getString("goal");
                                JSONArray id=object.getJSONArray("id");

                                for(int i=0; i<id.length(); i++){
                                    String str=id.get(i).toString();
                                    System.out.println(i+"번째 아이디는: "+id.get(i));

                                    idList.add(str);

                                }
                                idList.add(MainActivity.ID);

                                int goalMoney=object.getInt("goalprice");
                                String reward=object.getString("reward");
                                String penalty=object.getString("penalty");
                                String startDay=object.getString("startDay");
                                String endDay=object.getString("endDay");
                                String category= object.getString("category");
                                String randomcode= object.getString("inviteCode");

                                int peopleNum = idList.size();
                                System.out.println("invite 초대하자ㅏㅏㅏㅏ"+category+peopleNum+idList);
                                //String[] newGroup2 = new Group(groupname, goal, peopleNum, goalMoney, reward, penalty,
                                //startDay, endDay, category, randomcode,idList);
                                String[] IDlist = idList.toArray(new String[idList.size()]);


                                //groupAdapter.addItem(newGroup2);
                                //groupList.setAdapter(groupAdapter);
                                intentgroup.add(groupname);
                                intentgroup.add(goal);
                                intentgroup.add(String.valueOf(peopleNum));
                                intentgroup.add(String.valueOf(goalMoney));
                                intentgroup.add(reward);
                                intentgroup.add(penalty);
                                intentgroup.add(startDay);
                                intentgroup.add(endDay);
                                intentgroup.add(category);
                                intentgroup.add(randomcode);
                                intentgroup.add(Arrays.toString(IDlist));


                                Intent intent = new Intent(getActivity(), GroupInviteActivity.class);
                                System.out.println(intentgroup.getClass().getName());
                                intent.putExtra("newGroup", intentgroup);
                                intent.putExtra("idList",idList);
                                //초대코드에 해당하는 고유 코드 보내주기, if문으로 고유코드 있는지 확인해야 됨
                                //intent.putExtra("고유코드", "1111");
                                startActivity(intent);
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
                        parameters.put("id",MainActivity.ID);
                        parameters.put("inviteCode", value);
                        System.out.println("======"+value);
                        return parameters;
                    }

                };
                request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                        20000 ,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                Volley.newRequestQueue(getContext()).add(request);
                Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();




            }

        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#719aff"));
            }
        });
        alertDialog.show();

//        alertDialog.dismiss();

//        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//        dialog.setTitle("과목 추가").setMessage("추가할 과목명을 입력하세요")
//                .setView(inviteCode)
//                .setPositiveButton("추가",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String value = inviteCode.getText().toString();
//                    }
//                });
    }

    public void setItemObject(){

        //데베에서 가져온거 초기화해주기
        String randomCode = "";
        String groupName = "";
        String goal = "";
        int peopleNum = 1;
        int goalMoney = 0;
        String reward = "";
        String penalty = "";
        String startDay = "";
        String endDay = "";
        String category = "";
        String url = "http://192.168.0.20:3000/process/showbetcashbook/"+MainActivity.ID;
        int replay = 0;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {

                    JSONArray jsonArray=new JSONArray(result);
                    ArrayList<String> groupNameList = new ArrayList<String>();
                    ArrayList<String> goalList=new ArrayList<String>();
                    ArrayList<Integer> goalMoneyList=new ArrayList<Integer>();
                    ArrayList<String> rewardList=new ArrayList<String>();
                    ArrayList<String> penaltyList=new ArrayList<String>();
                    ArrayList<String> startDayList=new ArrayList<String>();
                    ArrayList<String> endDayList=new ArrayList<String>();
                    ArrayList<String> categoryList=new ArrayList<String>();
                    ArrayList<String> randomCodeList=new ArrayList<String>();
                    //ArrayList<String> idList=new ArrayList<String>();
                    ArrayList<ArrayList<String>> ids = new ArrayList<ArrayList<String>>();


                    int count = 0;
                    while(count < jsonArray.length())  //title리스트에 title 담기
                    {    ArrayList<String> idList=new ArrayList<String>();
                        JSONObject object = jsonArray.getJSONObject(count);
                        String groupname=object.getString("groupname");
                        String goal = object.getString("goal");
                        JSONArray id=object.getJSONArray("id");

                        System.out.println("count : " + count);

                        //id.length맞음
                        for(int i=0; i<id.length(); i++){
                            String str=id.get(i).toString();
                            System.out.println(i+"번째 아이디는: "+id.get(i));
                            //idList맞음
                            idList.add(str);
                        }

                        System.out.println("id.length : " + id.length());
                        System.out.println("idList.size() : " + idList.size());



                        int goalMoney=object.getInt("goalprice");
                        String reward=object.getString("reward");
                        String penalty=object.getString("penalty");
                        String startDay=object.getString("startDay");
                        String endDay=object.getString("endDay");
                        String category= object.getString("category");
                        String randomcode= object.getString("inviteCode");
                        groupNameList.add(groupname);
                        goalList.add(goal);
                        goalMoneyList.add(goalMoney);
                        rewardList.add(reward);
                        penaltyList.add(penalty);
                        startDayList.add(startDay);
                        endDayList.add(endDay);
                        categoryList.add(category);
                        randomCodeList.add(randomcode);
                        ids.add(idList);

                        count++;
                    }
                    int cursor=groupNameList.size(); //title리스트 개수
                    System.out.println("title리스트 개수 : " + groupNameList.size());
                    for(int i = 0 ; i<cursor; i++){
                        String groupName1 = groupNameList.get(i);
                        String goal1 = goalList.get(i);
                        int goalMoney1 = goalMoneyList.get(i);
                        String reward1 = rewardList.get(i);
                        String penalty1 = penaltyList.get(i);
                        String startDay1 = startDayList.get(i);
                        String endDay1 = endDayList.get(i);
                        String category1 =categoryList.get(i);
                        String randomCode1 = randomCodeList.get(i);
                        ArrayList<String> idexample = ids.get(i);
                        int peopleNum = idexample.size();

                        System.out.println("peopleNum : " + peopleNum);
                        System.out.println("title1 : " +goal1);
                        System.out.println("title1 : " +groupName1);

                        Group newGroup = new Group(groupName1, goal1, peopleNum, goalMoney1, reward1, penalty1,
                                startDay1, endDay1, category1, randomCode1, idexample);

                        groupAdapter.addItem(newGroup);
                        groupList.setAdapter(groupAdapter);

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
        );
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000 ,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        Volley.newRequestQueue(getContext()).add(request);

       /* Group newGroup = new Group(groupName, goal, peopleNum, goalMoney, reward, penalty,
                startDay, endDay, category, randomCode);
        groupAdapter.addItem(newGroup);
        groupAdapter.addItem(new Group("그룹이름", "10만원 이하",4, 100000, "5만원",
                "벌금 5만원", "2012-12-12", "2013-2-15", "식비", "5434"));
        groupList.setAdapter(groupAdapter);*/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (replay != 0){
            groupAdapter.removeItem();
            setItemObject();
        }
        replay = 1;
    }
}