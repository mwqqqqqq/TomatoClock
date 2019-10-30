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

public class FocusRecordsAdapter extends RecyclerView.Adapter<FocusRecordsAdapter.ViewHolder> {
    private List<Focus> mFocusRecords;

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

    public FocusRecordsAdapter(List<Focus> focusRecords)
    {
        mFocusRecords = focusRecords;
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
        Focus focus = mFocusRecords.get(position);
        holder.focusRecordsImage.setImageResource(R.drawable.focusimage_clock);
        String focusRecordStr1 = focus.startHour+":"+focus.startMinute+"\n";
        String focusRecordStr2 = focus.dura+"分钟";
        Spannable focusRecordStr = new SpannableString(focusRecordStr1+focusRecordStr2);
        focusRecordStr.setSpan(new ForegroundColorSpan(Color.BLACK),0,focusRecordStr1.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusRecordStr.setSpan(new AbsoluteSizeSpan(80),0,focusRecordStr1.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.focusRecordsText.setText(focusRecordStr);
    }

    @Override
    public int getItemCount()
    {
        return mFocusRecords.size();
    }
}