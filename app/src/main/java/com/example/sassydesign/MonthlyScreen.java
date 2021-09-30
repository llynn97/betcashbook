package com.example.sassydesign;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MonthlyScreen extends Fragment{

    MaterialCalendarView materialCalendarView; //캘린더 보여주는거

    MonthlyScreen monthlyScreen; //이 화면

    //화면에 띄워 줄 수입, 지출 TextView
    TextView month_income;
    TextView month_outcome;
    TextView MonthlyBudgetText;

    //"예산의 +nnn원을(를) 사용했습니다."에서 "+nnn"에 해당하는 TextView
    TextView percent_budget;
    TextView montlyBudgetGapText;

    //사용자가 설정해놓은 예산
    int UserBudget = 0;


    //그 달에 해당하는 전체 지출을 구하는 함수
    public void MonthTotalOutCome(int year, int month, ViewGroup rootView){

        String url = "http://192.168.1.6:3000/process/monthmoneyminus";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //materialCalendarView.getSelectedDate(); //이거 지금 월이 -1임.
                    //System.out.println("!!!!!!!!!!!!!!!"+materialCalendarView.getSelectedDate().toString());

                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println("jsonresponse: "+jsonResponse);
                    String monthmoneyminus=jsonResponse.getString("월지출");//MonthTotalOutCome()-월에 해당하는 총지출
                    String budget=jsonResponse.getString("예산");

                    System.out.println("monthmoneyminus: "+monthmoneyminus);
                    System.out.println("budget: "+budget);

                    int money=Integer.valueOf(monthmoneyminus);
                    UserBudget=Integer.valueOf(budget);

                    //화면에 띄워줄 예산 text
                    percent_budget = rootView.findViewById(R.id.percent_budget);
                    montlyBudgetGapText = rootView.findViewById(R.id.montlyBudgetGapText);
                    MonthlyBudgetText = rootView.findViewById(R.id.MonthlyBudgetText);
                    int budget_gap = UserBudget - money; //예산에서 월간 지출빼줌.
                    if(budget_gap == 0){ //
                        String strGap = Integer.toString(budget_gap);
                        percent_budget.setText(strGap);//예산과의 갭을 화면에 띄워줌
                        MonthlyBudgetText.setText("이번 달 예산이 ");
                        montlyBudgetGapText.setText("원이 남았습니다.");
                    }
                    else if(budget_gap > 0){ //
                        String strGap = Integer.toString(budget_gap);
                        MonthlyBudgetText.setText("이번 달 예산이 ");
                        percent_budget.setText(strGap);//예산과의 갭을 화면에 띄워줌
                        montlyBudgetGapText.setText("원이 남았습니다.");
                    }
                    else{ //예산을 더 많이 사용했을 때
                        budget_gap *= -1; //음수->양수로 바꿔주기
                        String strGap = Integer.toString(budget_gap);
                        percent_budget.setText(strGap);//예산과의 갭을 화면에 띄워줌
                        montlyBudgetGapText.setText("원을 더 사용했습니다.");
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
                parameters.put("year",String.valueOf(year));
                parameters.put("month",String.valueOf(month));
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.monthly_screen, container, false);
        monthlyScreen = this;
        materialCalendarView = rootView.findViewById(R.id.MonthShow);

        //년간 가계부가 2018년도 ~ 2023년도까지라서 동일하게 맞춰줬음.
        materialCalendarView.state().edit().setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2018,1,1))
                .setMaximumDate(CalendarDay.from(2023,12,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        //[오늘 날짜 표시] 오늘 날짜에 색 칠해줌
        materialCalendarView.setSelectedDate(CalendarDay.today());

        //[주말 표시] 한달 가계부에서 토, 일에 각각 색상을 넣어줌.
        materialCalendarView.addDecorators(new MonthlySundayDecorator(), new MonthlySaturdayDecorator());

        //그 날의 수입
        month_income = rootView.findViewById(R.id.month_income);
        //그 날의 지출
        month_outcome = rootView.findViewById(R.id.month_outcome);

        //[오늘에 해당하는 "월"의 월별 지출 - 첫화면에 보여줄 예산계산]
        /* 그 월별은 monthlyscreen의 290줄부터 357줄까지가 서버에서 해당하는 월의 일일별 지출수입 받아오는 코드야.
        try catch문의 plusarraylist,minusarraylist에 받아져있어.*/

        //오늘 날짜의 해당하는 월별 지출 받아오기
        MonthTotalOutCome(CalendarDay.today().getYear(),CalendarDay.today().getMonth()+1, rootView);

        //오늘에 해당하는 수입, 지출 받아오기
        String url = "http://192.168.1.6:3000/process/daymoneyminusplus";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String moneyminus=jsonResponse.getString("지출");
                    String moneyplus=jsonResponse.getString("수입");

                    month_income.setText(moneyplus); //그 날에 해당하는 수입
                    month_outcome.setText(moneyminus); //그 날에 해당하는 지출
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
                parameters.put("year",String.valueOf(CalendarDay.today().getYear()));
                parameters.put("month",String.valueOf(CalendarDay.today().getMonth()+1));
                parameters.put("day",String.valueOf(CalendarDay.today().getDay()));
                return parameters;
            }
        };
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        Volley.newRequestQueue(getContext()).add(request);


        //이번 달에 해당하는 점찍기
        String url4 = "http://192.168.1.6:3000/process/redblackmoney";

        StringRequest request2 = new StringRequest(Request.Method.POST, url4, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    System.out.println("jsonresponse: "+jsonResponse);

                    ArrayList<String> plusarraylist=new ArrayList<>(); //그 월에 해당하는 일일별 수입
                    ArrayList<String> minusarraylist=new ArrayList<>(); //그 월에 해당하는 일일별 지출

                    JSONArray jsonminus=jsonResponse.getJSONArray(0);
                    JSONArray jsonplus=jsonResponse.getJSONArray(1);

                    System.out.println("지출 jsonarray: "+jsonminus);
                    System.out.println("수입 jsonarray: "+jsonplus);

                    for(int i=0; i<jsonminus.length(); i++){
                        plusarraylist.add(jsonplus.getString(i));
                        minusarraylist.add(jsonminus.getString(i));
                    }
                    System.out.println("지출 minusarraylist: "+minusarraylist);
                    System.out.println("수입 plusarraylist: "+plusarraylist);

                    //이 번달의 수입, 지출 점을 찍어준다.
                    for(int i=0; i<jsonminus.length(); i++) {
                        if((Integer.parseInt(plusarraylist.get(i)) != 0) && (Integer.parseInt(minusarraylist.get(i)) != 0)){
                            materialCalendarView.addDecorator(new MonthlyEventDecorator(Color.YELLOW,
                                    Collections.singleton(CalendarDay.from(CalendarDay.today().getYear(), CalendarDay.today().getMonth(), i) )));
                            continue;
                        }
                        else if((Integer.parseInt(plusarraylist.get(i)) != 0)){
                            materialCalendarView.addDecorator(new MonthlyEventDecorator(Color.BLUE,
                                    Collections.singleton(CalendarDay.from(CalendarDay.today().getYear(), CalendarDay.today().getMonth(), i) )));
                            continue;
                        }
                        else if((Integer.parseInt(minusarraylist.get(i)) != 0)){
                            materialCalendarView.addDecorator(new MonthlyEventDecorator(Color.RED,
                                    Collections.singleton(CalendarDay.from(CalendarDay.today().getYear(), CalendarDay.today().getMonth(), i) )));
                            continue;
                        }
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
                parameters.put("year", String.valueOf(CalendarDay.today().getYear()));
                parameters.put("month", String.valueOf(CalendarDay.today().getMonth()+1)); //**************
                return parameters;
            }
        };
        request2.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                50000 ,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request2.setShouldCache(false);
        Volley.newRequestQueue(getContext()).add(request2);


        //---------------------------------------------------------------------------------
        //[달력의 월이 바뀌었을 때] 해당 달에 해당하는 예산 부분과 점 찍는거 바꿔줌.
        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {

            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                //사용자가 선택한 날짜 받아오기
                int changedYear = date.getYear();
                int changedMonth = date.getMonth();
                System.out.println("냥?"+materialCalendarView.getCurrentDate());
                System.out.println("월이 바뀔 때 선택한 월:"+changedMonth);

                MonthTotalOutCome(changedYear, changedMonth+1, rootView); //그 달에 해당하는 지출을 프린트해준다.

                //그 달에 해당하는 일일 지출, 수입 찍어주기.
                String url5="http://192.168.1.6:3000/process/redblackmoney";
                StringRequest request3 = new StringRequest(Request.Method.POST, url5, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponse = new JSONArray(response);
                            System.out.println("jsonresponse: "+jsonResponse);

                            ArrayList<String> plusarraylist=new ArrayList<>(); //그 월에 해당하는 일일별 수입
                            ArrayList<String> minusarraylist=new ArrayList<>(); //그 월에 해당하는 일일별 지출

                            JSONArray jsonminus=jsonResponse.getJSONArray(0);
                            JSONArray jsonplus=jsonResponse.getJSONArray(1);

                            System.out.println("지출 jsonarray ********: "+jsonminus);
                            System.out.println("수입 jsonarray ******* : "+jsonplus);

                            for(int i=0; i<jsonminus.length(); i++){
                                plusarraylist.add(jsonplus.getString(i));
                                minusarraylist.add(jsonminus.getString(i));
                            }
                            System.out.println("이달의 지출 minusarraylist: "+minusarraylist);
                            System.out.println("이달의 수입 plusarraylist: "+plusarraylist);

                            //해당 달의 수입, 지출을 바꿔준다.
                            for(int i=0; i<jsonminus.length(); i++) {
                                if((Integer.parseInt(plusarraylist.get(i)) != 0) && (Integer.parseInt(minusarraylist.get(i)) != 0)){
                                    materialCalendarView.addDecorator(new MonthlyEventDecorator(Color.YELLOW,
                                            Collections.singleton(CalendarDay.from(changedYear, changedMonth, i) )));
                                    continue;
                                }
                                else if((Integer.parseInt(plusarraylist.get(i)) != 0)){
                                    materialCalendarView.addDecorator(new MonthlyEventDecorator(Color.BLUE,
                                            Collections.singleton(CalendarDay.from(changedYear, changedMonth, i) )));
                                    continue;
                                }
                                else if((Integer.parseInt(minusarraylist.get(i)) != 0)){
                                    materialCalendarView.addDecorator(new MonthlyEventDecorator(Color.RED,
                                            Collections.singleton(CalendarDay.from(changedYear, changedMonth, i) )));
                                    continue;
                                }
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
                        parameters.put("year",String.valueOf(changedYear));
                        parameters.put("month",String.valueOf(changedMonth+1));
                        return parameters;
                    }
                };
                request3.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                        20000,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request3.setShouldCache(false);
                Volley.newRequestQueue(getContext()).add(request3);
            }
        });
        //---------------------------------------------------------------------------------

        //[달력에서 날짜를 클릭했을 때] - 그 날의 수입, 지출을 바꿔준다.
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                //사용자가 선택한 날짜 받아오기
                int year = date.getYear();
                int month = date.getMonth()+1;
                int day = date.getDay();
                System.out.println("사용자가 선택한 날짜: "+year+"/"+month+"/"+day);

                MonthTotalOutCome(year, month, rootView); //클릭한 날에 해당하는 월별총지출 뽑아줌.

                String inCome = null;
                String outCome = null;

                String url = "http://192.168.1.6:3000/process/daymoneyminusplus";

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println("jsonresponse: "+jsonResponse);

                            String moneyminus=jsonResponse.getString("지출");
                            String moneyplus=jsonResponse.getString("수입");

                            System.out.println("moneyminus: "+moneyminus);
                            System.out.println("moneyplus: "+moneyplus);

                            month_income.setText(moneyplus); //그 날에 해당하는 수입
                            month_outcome.setText(moneyminus); //그 날에 해당하는 지출

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
                        parameters.put("year",String.valueOf(year));
                        parameters.put("month",String.valueOf(month));
                        parameters.put("day",String.valueOf(day));

                        return parameters;
                    }

                };
                request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                        50000,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                Volley.newRequestQueue(getContext()).add(request);
            }
        });

        return rootView;
    }

    class MonthlySundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public MonthlySundayDecorator(){

        }

        @Override
        public boolean shouldDecorate(CalendarDay day){
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view){
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    class MonthlySelectorDecorator implements DayViewDecorator {
        private final Drawable drawable;

        public MonthlySelectorDecorator(Fragment context){
            drawable = context.getResources().getDrawable(R.drawable.my_selector);

        }

        @Override
        public boolean shouldDecorate(CalendarDay day){
            return true;
        }

        @Override
        public void decorate(DayViewFacade view){
            view.setSelectionDrawable(drawable);
        }
    }

    class MonthlySaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public MonthlySaturdayDecorator(){
        }
        @Override
        public boolean shouldDecorate(CalendarDay day){
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view){
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    class MonthlyEventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;
        private final Calendar calendar = Calendar.getInstance();

        public MonthlyEventDecorator(int color, Collection<CalendarDay> dates){
            this.color = color;
            this.dates = new HashSet<>(dates);
        }
        @Override
        public boolean shouldDecorate(CalendarDay day){
            day.copyTo(calendar);
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view){
            view.addSpan(new DotSpan(8, color));
        }
    }

}