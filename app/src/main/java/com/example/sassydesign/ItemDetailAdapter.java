package com.example.sassydesign;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailAdapter.ViewHolder>{

    ArrayList<ItemDetail> items = new ArrayList<ItemDetail>();
    ArrayList<Item> items2 = new ArrayList<Item>();
    ItemAdapter itemAdapter = new ItemAdapter();
    ArrayList<String> objid = new ArrayList<String>();
    String productName;
    String productCost;
    String productQuantity;
    String selectedCategory;
    String objectId ;
    Context context;
    String deleteObjectId;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.hand_add_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ItemDetail item = items.get(position);
        viewHolder.setItem(item);
        //String title=item.productQuantity;
        String title = item.getProductQuantity();
        objectId = items.get(position).getObjectId();
        //deleteObjectId = objectId;
        //objectId = item.objectId;

        //String objectid = items2.get(position).getObjectidList().get(position);
        System.out.println("부분삭제에서 오브젝트 아이디 잘 받았나 확인하자!!");
        System.out.println(position);
        System.out.println(objectId);
        System.out.println(title);
        //System.out.println(objectid);

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //처음 추가할때 - 부분삭제
                if(objectId == "1"){
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());
                }
                //수정 - 부분삭제
                if(objectId != "1") {
                    String url = "http://192.168.0.20:3000/process/deleteitem";
                    deleteObjectId = items.get(position).getObjectId();
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                Boolean success=jsonResponse.getBoolean("success");

                                if(success){
                                    System.out.println("삭제된 오브아이디 : "+ items.get(position).getObjectId());
                                    //deleteObjectId = items.get(position).getObjectId();
                                    System.out.println("포지션 확인하자 : "+position);
                                    System.out.println("수량 확인:"+title);
                                    //deleteItem(position);
                                    items.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, items.size());

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
                            parameters.put("_id", deleteObjectId);

                            return parameters;
                        }

                    };
                    request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                            20000 ,
                            com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    request.setShouldCache(false);
                    Volley.newRequestQueue(context).add(request);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ItemDetail item) {
        items.add(item);
    }

    public void setItems(ArrayList<ItemDetail> items) {
        this.items = items;
    }

    public ItemDetail getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, ItemDetail item) {
        items.set(position, item);
    }

    //아이템 개별 삭제
    public void deleteItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText productName;
        EditText productCost;
        EditText productQuantity;
        String selectedCategory;
        Spinner category;
        ImageButton deleteButton;

        public ViewHolder(View itemView){
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            productCost = itemView.findViewById(R.id.productCost);
            category = itemView.findViewById(R.id.categorySpinner);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            productQuantity = itemView.findViewById(R.id.productQuantity);

        }

        public void setItem(ItemDetail item){
            productName.setText(item.getProductName());
            productName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setProductName(productName.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            productCost.setText(item.getProductCost());
            productCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setProductCost(productCost.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            productQuantity.setText(item.getProductQuantity());
            productQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setProductQuantity(productQuantity.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedCategory = (String)category.getSelectedItem();

                    if (item.getProductCategory() == null){
                        selectedCategory = (String)category.getSelectedItem();
                        if(selectedCategory.equals("카테고리")){
                            //
                        }
                        else{
                            item.setProductCategory(selectedCategory);
                            item.setCategory(category);
                            item.setPosition(position);
                        }
                    }
                    else{
                        selectedCategory = item.getProductCategory();
                        item.setProductCategory(selectedCategory);
                        item.setCategory(category);
                        //카테고리 변경됐을때
                        String changedCategory = (String) category.getSelectedItem();
                        if(selectedCategory != changedCategory) {
                            item.setProductCategory(changedCategory);
                            item.setCategory(category);
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
            int position = item.getPosition();
            category.setSelection(position);
        }

    }

}