package com.example.tomatoclock.rankList;

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

public class FocusDegreeRankListAdapter extends RecyclerView.Adapter<FocusDegreeRankListAdapter.ViewHolder> {
    private List<FocusTimeRank> mRankRecords;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView focusRecordsImage;
        TextView focusRecordsText;

        public ViewHolder(View view)
        {
            super(view);
            focusRecordsImage = (ImageView) view.findViewById(R.id.focus_time_rank_image);
            focusRecordsText = (TextView) view.findViewById(R.id.focus_time_rank_text);
        }
    }

    public FocusDegreeRankListAdapter(List<FocusTimeRank> RankRecords)
    {

        mRankRecords = RankRecords;
        System.out.println("mFocusRecords size = "+mRankRecords.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.focus_time_rank_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        FocusTimeRank rankRecord = mRankRecords.get(position);
        holder.focusRecordsImage.setImageResource(R.drawable.focusimage_clock);
        if(position == 0)//第一名
            holder.focusRecordsImage.setImageResource(R.drawable.focusimage_clock);
        String rankStr = String.valueOf(rankRecord.rank);
        String focusTimeStr = String.valueOf(rankRecord.focusTime);
        Spannable rankRecordStr = new SpannableString(rankStr+" "+focusTimeStr);
        rankRecordStr.setSpan(new ForegroundColorSpan(Color.BLACK),0,rankRecordStr.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        rankRecordStr.setSpan(new AbsoluteSizeSpan(30),0,rankRecordStr.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.focusRecordsText.setText(rankRecordStr);
    }

    @Override
    public int getItemCount()
    {

        return mRankRecords.size();
    }
}