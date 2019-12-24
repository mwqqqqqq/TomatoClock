package com.example.tomatoclock.report;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tomatoclock.R;

import java.util.List;

public class FocusWIthDateRecordsAdapter extends RecyclerView.Adapter<FocusWIthDateRecordsAdapter.ViewHolder> {
    private List<FocusWIthDate> mFocusRecords;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView focusRecordsImage;
        TextView focusRecordsText;

        public ViewHolder(View view)
        {
            super(view);
            focusRecordsImage = (ImageView) view.findViewById(R.id.focus_image);
            focusRecordsText = (TextView) view.findViewById(R.id.focus_text);
        }
    }

    public FocusWIthDateRecordsAdapter(List<FocusWIthDate> focusRecords)
    {

        mFocusRecords = focusRecords;
        System.out.println("mFocusRecords size = "+mFocusRecords.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.focusrecords_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        FocusWIthDate focus = mFocusRecords.get(position);
        holder.focusRecordsImage.setImageResource(R.drawable.focusimage_clock);
        String strFocusStartHour = String.valueOf(focus.startHour);
        String strFocusStartMinute = String.valueOf(focus.startMinute);
        String strDura = String.valueOf(focus.dura);
        if(focus.startHour < 10)
            strFocusStartHour = "0" + strFocusStartHour;
        if(focus.startMinute < 10)
            strFocusStartMinute = "0" + strFocusStartMinute;

        String focusRecordStr0 = String.valueOf(focus.year) + "-"+String.valueOf(focus.month)+"-"+String.valueOf(focus.dayOfMonth)+'\n';
        String focusRecordStr1 = strFocusStartHour+":"+strFocusStartMinute+"\n";
        String focusRecordStr2 = focus.dura+"分钟";
        Spannable focusRecordStr = new SpannableString(focusRecordStr0+focusRecordStr1+focusRecordStr2);
        focusRecordStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusRecordStr0.length(),focusRecordStr0.length()+focusRecordStr1.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusRecordStr.setSpan(new AbsoluteSizeSpan(80),focusRecordStr0.length(),focusRecordStr0.length()+focusRecordStr1.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.focusRecordsText.setText(focusRecordStr);
    }

    @Override
    public int getItemCount()
    {

        return mFocusRecords.size();
    }
}