package com.example.whatshistory.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatshistory.Fragments.CallsModel;
import com.example.whatshistory.R;

import java.util.ArrayList;
import java.util.List;

public class CallReyclerAdapter extends RecyclerView.Adapter<CallReyclerAdapter.ContactHolder> {

    private ArrayList<CallsModel> contactsList;
    private Context mContext;

    public CallReyclerAdapter(Context context, ArrayList<CallsModel> contactsList) {
        this.contactsList = contactsList;
        this.mContext = context;
    }

    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.call_list_item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public int getItemCount() {
        return contactsList == null ? 0 : contactsList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, final int position) {
        final CallsModel model = contactsList.get(position);
        holder.setPhoneName(model.getNumber());
        holder.setDate(model.getDate());
        holder.setTime(model.getTime());
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private TextView number_textview, date_textview, time_textview;
        private ImageView enter_whatsapp_btn;

        public ContactHolder(View itemView) {
            super(itemView);
            number_textview = itemView.findViewById(R.id.number_textview);
            date_textview = itemView.findViewById(R.id.date_textview);
            time_textview = itemView.findViewById(R.id.time_textview);
            enter_whatsapp_btn = itemView.findViewById(R.id.enter_whatsapp);
            enter_whatsapp_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
//                        intent.setPackage("com.whatsapp");
                        intent.setPackage("com.whatsapp.w4b");
                        // Give your message here
                        intent.putExtra(Intent.EXTRA_TEXT, "Enter Message");
                        // Checking whether Whatsapp
                        // is installed or not
                        if (intent.resolveActivity(mContext.getPackageManager()) == null) {
                            Toast.makeText(mContext, "Please install whatsapp first.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        if (e instanceof ActivityNotFoundException) {

                        }
                    }
                }
            });
        }

        public void setPhoneName(String number) {
            number_textview.setText(number);
        }

        public void setDate(String date) {
            date_textview.setText(date);
        }

        public void setTime(String time) {
            time_textview.setText(time);
        }
    }
}

//public class CallReyclerAdapter extends RecyclerView.Adapter<CallReyclerAdapter.CartFragViewHolder> {
//    Context context;
//    private static final String TAG = "debinf PurchaseAdap";
//
//    private static final DiffUtil.ItemCallback<CallsModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<CallsModel>() {
//        @Override
//        public boolean areItemsTheSame(@NonNull CallsModel oldProduct, @NonNull CallsModel newProduct) {
//            Log.i(TAG, "areItemsTheSame: old is " + oldProduct.getNumber() + " ; new is " + newProduct.getNumber());
//            return oldProduct.getNumber().equals(newProduct.getNumber());
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull CallsModel oldProduct, @NonNull CallsModel newProduct) {
//            Log.i(TAG, "areContentsTheSame: old is " + oldProduct.getNumber() + " ; new is " + newProduct.getNumber());
//            return oldProduct.getNumber().equals(newProduct.getNumber());
//        }
//    };
//
//    private AsyncListDiffer<CallsModel> differ = new AsyncListDiffer<CallsModel>(this, DIFF_CALLBACK);
//
//    @NonNull
//    @Override
//    public CartFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_list_item, parent, false);
//        return new CartFragViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CartFragViewHolder holder, int position) {
//        final CallsModel model = differ.getCurrentList().get(position);
//        holder.setPhoneName(model.getNumber());
//        holder.setDate(model.getDate());
//        holder.setTime(model.getTime());
//    }
//
//    @Override
//    public int getItemCount() {
//        Log.i(TAG, "getItemCount");
//        return differ.getCurrentList().size();
//    }
//
//    public void submitList(Context context, List<CallsModel> products) {
//        Log.i(TAG, "submitList: products.size is " + products.size());
//        this.context = context;
//        differ.submitList(products);
//    }
//
//    public class CartFragViewHolder extends RecyclerView.ViewHolder {
//        private TextView number_textview, date_textview, time_textview;
//        private ImageView enter_whatsapp_btn;
//
//        public CartFragViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            number_textview = itemView.findViewById(R.id.number_textview);
//            date_textview = itemView.findViewById(R.id.date_textview);
//            time_textview = itemView.findViewById(R.id.time_textview);
//            enter_whatsapp_btn = itemView.findViewById(R.id.enter_whatsapp);
//            enter_whatsapp_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/plain");
////                        intent.setPackage("com.whatsapp");
//                        intent.setPackage("com.whatsapp.w4b");
//                        // Give your message here
//                        intent.putExtra(Intent.EXTRA_TEXT, "Enter Message");
//                        // Checking whether Whatsapp
//                        // is installed or not
//                        if (intent.resolveActivity(context.getPackageManager()) == null) {
//                            Toast.makeText(context, "Please install whatsapp first.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        context.startActivity(intent);
//                    } catch (Exception e) {
//                        if (e instanceof ActivityNotFoundException) {
//
//                        }
//                    }
//                }
//            });
//        }
//
//        public void setPhoneName(String number) {
//            number_textview.setText(number);
//        }
//
//        public void setDate(String date) {
//            date_textview.setText(date);
//        }
//
//        public void setTime(String time) {
//            time_textview.setText(time);
//        }
//    }
//}