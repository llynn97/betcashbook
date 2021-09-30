package com.example.sassydesign;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/*
년월일 받아오는거 데베랑 안드 코드 형변환 주의

//날짜 클릭했을 때 데이터 받아오기
//일일 조회 화면
//daily_screen.xml

 */

public class DailyScreen extends Fragment implements DatePickerDialog.OnDateSetListener {

    RecyclerView dailyReceiptList;

    int replay = 0; //OnResume을 위한 변수
    int replay2 = 0;

    TextView dailyOutcome;
    TextView dailyIncome;
    TextView dailyTotal;

    int totalIncome = 0;
    int totalOutcome = 0;
    int total = 0;

    ItemAdapter itemAdapter = new ItemAdapter();

    Button dailyDateButton;
    String initDate="";
    Date todayDate;
    long now = System.currentTimeMillis();

    DailyScreen dailyScreen;

    ImageView galleryImage;
    Bitmap img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.daily_screen, container, false);
        dailyScreen = this;

        //초기화문
        dailyOutcome = rootView.findViewById(R.id.dailyOutcome);
        dailyIncome = rootView.findViewById(R.id.dailyIncome);
        dailyTotal = rootView.findViewById(R.id.dailyTotal);
        dailyDateButton = rootView.findViewById(R.id.dailyDateButton);

        //오늘 날짜 가져와서 설정
        todayDate = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd");
        initDate = sdf.format(todayDate);
        dailyDateButton.setText(initDate);

        //리사이클러 뷰 선언, 설정, 어댑터 초기화
        dailyReceiptList = rootView.findViewById(R.id.dailyReceiptList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        dailyReceiptList.setLayoutManager(layoutManager);

        itemAdapter = new ItemAdapter(DailyScreen.this);


        //추가 버튼(+) 눌렀을 때
        Button handAddButton = rootView.findViewById(R.id.handAdd);
        handAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HandAddActivity.class);
                startActivity(intent);
            }
        });


        //영수증 인식 추가버튼 눌렀을 때
        Button receiptAddButton = rootView.findViewById(R.id.receiptAdd);
        receiptAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent galleryCompleteIntent = new Intent(getActivity(), GalleryCompleteActivity.class);
                //galleryCompleteIntent.putExtra("image", img);
                startActivity(galleryCompleteIntent);

//                startActivityForResult(intent, 1);
//                System.out.println("startActivityForResult 실행됨");
            }
        });

        if (itemAdapter == null){
            System.out.println("null임");
        }


        //날짜 버튼 눌러서 날짜 선택하기
        dailyDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        if(replay2 == 0){
            setItemObject();
            setTotal();
            replay2 = 1;
        }

        System.out.println("onCreateView 호출");

        return rootView;
    }

    ArrayList<Item> paramItemList = new ArrayList<Item>();

    public void setParmItemList(Item paramItem) {
        paramItemList.add(paramItem);
    }

    public ArrayList getParmItemList(){
        return paramItemList;
    }

    public void setItemObject(){
        String [] seperateDate = initDate.split("/");
        //여기서 년, 월, 일 나눠줌.
        String year = seperateDate[0];
        String month = seperateDate[1];
        String day = seperateDate[2];
        String url = "http://192.168.0.20:3000/process/showitemlist/"+MainActivity.ID+"/"+year+"/"+month+"/"+day;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    String title1 = "";
                    String inOrOut1 = "";

                    //현금/카드
                    String cacheOrCard1 = "";

                    //대신 상세 품목들 개수 받아오기
                    int subCursor=0;

                    //사용자가 선택한 날짜를 Date객체로 변환
                    Date selectedDate = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd");
                    try {
                        selectedDate = simpleDateFormat.parse(initDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray=new JSONArray(result);
                    ArrayList<String> titleList = new ArrayList<String>();
                    ArrayList<String> inOrOutList=new ArrayList<String>();
                    ArrayList<String> cacheOrcardList=new ArrayList<String>();
                    int count = 0;
                    while(count < jsonArray.length())  //title리스트에 title 담기
                    {
                        JSONObject object = jsonArray.getJSONObject(count);
                        String title2=object.getString("title");
                        String inOrOut2 = object.getString("profit");
                        String cacheOrcard2=object.getString("paymethod");
                        if(!titleList.contains(title2)||!inOrOutList.contains(inOrOut2)){
                            titleList.add(title2);
                            inOrOutList.add(inOrOut2);
                            cacheOrcardList.add(cacheOrcard2);
                        }
                        count++;
                    }
                    int cursor=titleList.size(); //title리스트 개수
                    System.out.println("title리스트 개수 : " + titleList.size());
                    System.out.println(titleList);
                    System.out.println(inOrOutList);

                    for(int i = 0 ; i<cursor; i++){
                        System.out.println("i: " + i);
                        //item객체 생성할 null 객체
                        Item detailItem = null;
                        ArrayList<String> itemList1 = new ArrayList<String>();
                        //상세 품목 카테고리들을 담은 리스트
                        ArrayList<String> categoryList1 = new ArrayList<String>();
                        //상세 품목 수량들을 담은 리스트
                        ArrayList<String> quantityList1 = new ArrayList<String>();
                        //상세 품목 가격들을 담은 리스트
                        ArrayList<String> priceList1 = new ArrayList<String>();
                        ArrayList<String> objectList1 = new ArrayList<String>();

                        int cnt=0;
                        //상세품목의 개수
                        subCursor=0;
                        title1=titleList.get(i);
                        System.out.println("title1 : " +title1);
                        inOrOut1=inOrOutList.get(i);
                        System.out.println("inOrOut1 : " +inOrOut1);
                        cacheOrCard1=cacheOrcardList.get(i);
                        System.out.println("현금/카드 : " + cacheOrCard1);
                        while(cnt<jsonArray.length()) {
                            JSONObject object = jsonArray.getJSONObject(cnt);
                            String title2 = object.getString("title");
                            String profit = object.getString("profit");
                            System.out.println("title2 : " + title2);
                            System.out.println("cnt : " + cnt);
                            if (title2.equals(title1)&&profit.equals(inOrOut1)) {
                                String itemname = object.getString("itemname");
                                System.out.println("itemname : "+itemname);
                                String category = object.getString("category");
                                System.out.println("category : " + category);
                                String amount = String.valueOf(object.getInt("amount"));
                                System.out.println("amount : " + amount);
                                String price = String.valueOf(object.getInt("price"));
                                System.out.println("price : " +price);
                                String _id = object.getString("_id");
                                System.out.println("_id : "+_id);
                                //"냠냠굿 과자"대신 상세 품목 이름 가져오기
                                itemList1.add(subCursor, itemname);
                                //"식비"대신 상세 품목 카테고리 가져오기
                                categoryList1.add(subCursor, category);
                                //"1"대신 상세 품목 수량 가져오기
                                quantityList1.add(subCursor, amount);
                                //"-3500"대신 상세 품목 가격 가져오기
                                objectList1.add(subCursor, _id);
                                priceList1.add(subCursor, price);

                                subCursor++;

                            }
                            cnt++;
                        }
                        detailItem = new Item(initDate, title1, inOrOut1, cacheOrCard1, itemList1,
                                categoryList1, quantityList1, priceList1, objectList1);
                        //setParmItemList(detailItem);
                        itemAdapter.addItem(detailItem);
                        System.out.println("@@@@@@@@@@itemAdapter 개수 :" + itemAdapter.getItemCount());
                        dailyReceiptList.setAdapter(itemAdapter);
                    }
                    System.out.println("!!!!!!!!!!!!!itemAdapter 개수 :" + itemAdapter.getItemCount());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                initTotal();
                setTotal();
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
    }

    public void showDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        initDate = year + "/" + (month+1) + "/" + day;
        dailyDateButton.setText(initDate);
        System.out.println("itemAdapter 개수%%%%%" + itemAdapter.getItemCount());
        itemAdapter.removeItem();
        setItemObject();
        dailyReceiptList.setAdapter(itemAdapter);
    }

    public void initTotal(){
        total = 0;
        totalIncome = 0;
        totalOutcome = 0;
    }

    public void setTotal(){
        System.out.println("itemAdapter 개수: " + itemAdapter.getItemCount());
        //품목 하나
        for(int i = 0 ; i<itemAdapter.getItemCount() ; i++){

            //품목 하나 안의 세부 품목
            Log.d("i",i+"");
            for(int j = 0; j<itemAdapter.getItem(i).getPriceList().size(); j++){
                //그 품목의 가격이 음수일 때(지출일 때)
                Log.d("j",j+"");
                if((itemAdapter.getItem(i).getInOrOut()).equals("지출")){
                    totalOutcome += Integer.parseInt(itemAdapter.getItem(i).getPriceList().get(j));
                    Log.d("totalOutcome",totalOutcome+"");
                }
                //그 품목의 가격이 양수일 때(수입일 때)
                else if((itemAdapter.getItem(i).getInOrOut()).equals("수입")){
                    totalIncome += Integer.parseInt(itemAdapter.getItem(i).getPriceList().get(j));
                    Log.d("totalIncome",totalIncome+"");
                }
            }
        }


        dailyOutcome.setText("-"+totalOutcome);
        dailyIncome.setText("+"+totalIncome);

        total = totalIncome - totalOutcome;
        if (total>0)
        {
            dailyTotal.setText("+" + total);
        }
        else
        {
            //알아서 -로 나와서 -안 붙여도 됨
            dailyTotal.setText(""+total);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (replay != 0){
            itemAdapter.removeItem();
            setItemObject();
            System.out.println("onResume 호출");
        }
        replay = 1;
    }

    public void setBackgroundTask(){
        setItemObject();
    }

    public String getInitDate(){
        return initDate;
    }

    public ItemAdapter getItemAdapter(){
        return itemAdapter;
    }

    public RecyclerView getDailyReceiptList(){
        return dailyReceiptList;
    }

    public TextView getDailyOutCome(){
        return dailyOutcome;
    }

    public TextView getDailyIncome(){
        return dailyIncome;
    }

    public TextView getDailyTotal(){
        return dailyTotal;
    }

    public void setItemAdapter(){
        this.itemAdapter = itemAdapter;
    }

    public DailyScreen getDailyScreen() {
        return dailyScreen;
    }

}