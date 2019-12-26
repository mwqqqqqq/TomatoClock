package com.example.tomatoclock.report;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tomatoclock.MainActivity;
import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportByDayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportByDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportByDayFragment extends Fragment implements  DatePickerDialog.OnDateSetListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View root;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //fragment中的组件
    MyLineChartView chartView;
    List<String> xValues;   //x轴数据集合
    List<Integer> yValues;  //y轴数据集合
    List<Focus> focusList;

    TextView reportTitle;
    TextView focusTodayText;
    TextView reportAbstract;
    TextView focusTimesText;
    TextView interruptTimesText;
    TextView continuousFocusDaysText;
    TextView focusRecordTitle;
    TextView dayNow;

    FocusRecordsAdapter fa;// = new FocusRecordsAdapter(focusList);


    RecyclerView focusRecords;
    boolean setFocusByDayFi = false;


    boolean focusListFinish = false;

    ImageButton imageButton;

    int year,month,day;

    String stringOfCurve;

    public ReportByDayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ReportByDayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportByDayFragment newInstance() {
        ReportByDayFragment fragment = new ReportByDayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageButton sweepButton = (ImageButton) getActivity().findViewById(R.id.imageButton);

        sweepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Date date = new Date();
                Calendar mCalendar=Calendar.getInstance();
                int mYear=mCalendar.get(Calendar.YEAR);
                int mMonth=mCalendar.get(Calendar.MONTH);
                int mDay=mCalendar.get(Calendar.DATE);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),ReportByDayFragment.this,
                        mYear,
                        mMonth,
                        mDay
                );
                dialog.show();
            }
        });

        Button showAllRecords = getActivity().findViewById(R.id.button3);
        showAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(root.getContext(), ShowAllFocusRecords.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

    }
    String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String local = "GMT+8";//获取当前时间
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(local));
        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        String dateStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        String timeStr = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);


        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_report_by_day, container, false);
        dayNow = root.findViewById(R.id.textView12);
        Spannable dayNowStr = new SpannableString(dateStr);
        //dayNowStr.setSpan(new AbsoluteSizeSpan(80),0,dateStr.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        dayNow.setText(dayNowStr);
        userName = getArguments().getString("userName");//
        System.out.println("userName is " + userName);
        chartView = (MyLineChartView) root.findViewById(R.id.linechartview);
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        focusList = new ArrayList<>();

        // xy轴集合自己添加数据，测试随便数据
        for(int i = 0;i < 5;i++)
            xValues.add(String.valueOf(i));
        //xValues.add("2");
        for(int i = 0;i < 5;i++)
            yValues.add(2);
        //yValues.add(33);
        chartView.setXValues(xValues);
        chartView.setYValues(yValues);
        //转化为开始时间和分钟数
        //开始时间的小时和分钟数，分钟数除以60得到小数点后位数
        String givenHour = "10";
        String givenMinute = "30";
        int dura = 40;

        int startHour = Integer.parseInt(givenHour);
        int startMinute = Integer.parseInt(givenMinute);

//        Focus focus1 = new Focus();
//        focus1.startHour = startHour;
//        focus1.startMinute = startMinute;
//        focus1.dura = dura;
//        focusList.add(focus1);
//        Focus focus2 = new Focus();
//        focus2.startHour = 13;
//        focus2.startMinute=40;
//        focus2.dura = 45;
//        focusList.add(focus2);
        int totalMin = 0;
//        for (int i = 0;i < MainActivity.flist.size();i++)
//        {
//            //MainActivity.flist.get(i).startHour=8;
//            //MainActivity.flist.get(i).startMinute=0;
//            //MainActivity.flist.get(i).dura = 60;
//            focusList.add(MainActivity.flist.get(i));
//            System.out.print("ffl:"+MainActivity.flist.get(i).startHour + ":"+MainActivity.flist.get(i).startMinute+" dura:" + MainActivity.flist.get(i).dura);
//            totalMin = totalMin + MainActivity.flist.get(i).dura;
//        }
        //更新上方图表
        focusRecords = root.findViewById(R.id.focusRecords);//外面加一层relativeLayout
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        focusRecords.setLayoutManager(layoutManager);
        setFocusByDay(dateStr,userName);

        long startTime =  System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        while(setFocusByDayFi == false && endTime - startTime < 3000)  endTime = System.currentTimeMillis();
        setFocusByDayFi = false;
//        while(setFocusByDayFi == false);
//        setFocusByDayFi = false;
        fa = new FocusRecordsAdapter(focusList);
        focusRecords.setAdapter(fa);

        //fa.notifyDataSetChanged();


        // focusList.add(MainActivity.flist.get(0));
        //(!focusListFinish);
        //chartView.setFocusList(focusList);
        //focusListFinish = false;
        //

        //每日专注时间 文字框部分


        setFocusTime(dateStr,userName);
        //focusTodayText.setBackgroundResource(R.drawable.text_view_border);


        //日历图标部分
        imageButton = root.findViewById(R.id.imageButton);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);

        //文字：摘要
        reportAbstract = root.findViewById(R.id.reportAbstract);
        Spannable reportAbstractStr = new SpannableString("摘要");
        reportAbstractStr.setSpan(new AbsoluteSizeSpan(80),0,2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        reportAbstractStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,2,Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        reportAbstract.setText(reportAbstractStr);


        //卡片一：今日专注次数
//        focusTimesText = root.findViewById(R.id.focusTimesText);
//        String reportFoucsTimesStr1 = " 专注次数：\n\n\n";
//        String reportFocusTimesStr2 = " "+String.valueOf(focusList.size());//" 3";//从服务器端获取数据，填在这里
//        String reportFocusTimesStr3 = "次";
//        Spannable reportFoucsTimesStr = new SpannableString(reportFoucsTimesStr1+reportFocusTimesStr2+reportFocusTimesStr3);
//        reportFoucsTimesStr.setSpan(new AbsoluteSizeSpan(80),reportFoucsTimesStr1.length(),reportFoucsTimesStr1.length()+reportFocusTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        focusTimesText.setText(reportFoucsTimesStr);//此处根据后续取数据情况修改
//        focusTimesText.setBackgroundResource(R.drawable.text_view_border);
        setFocusTimes(dateStr,userName);

        //卡片二：今日打断次数
//        interruptTimesText = root.findViewById(R.id.interruptTimesText);
//        String reportInterruptTimesStr1 = " 打断次数： \n\n\n";
//        String reportInterruptTimesStr2 = " 0";//从服务器端获取数据，填在这里
//        String reportInterruptTimesStr3 = "次";
//        Spannable reportInterruptTimesStr = new SpannableString(reportInterruptTimesStr1+reportInterruptTimesStr2+reportInterruptTimesStr3);
//        reportInterruptTimesStr.setSpan(new AbsoluteSizeSpan(80),reportInterruptTimesStr1.length(),reportInterruptTimesStr1.length()+reportInterruptTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        interruptTimesText.setText(reportInterruptTimesStr);
//        focusTimesText.setBackgroundResource(R.drawable.text_view_border);
        setInterruptTimes(dateStr,userName);

        //卡片三：连续专注天数，TODO:等后台
        //url 示例：http://49.232.5.236:8080/test/continuousFocusDay?user=boot&date=2019-11-16
//        continuousFocusDaysText = root.findViewById(R.id.continuousFocusDaysText);
//        String continuousFocusDaysStr1 = " 连续专注天数： \n\n\n";
//        String continuousFocusDaysStr2 = " 1";//从服务器端获取数据，填在这里
//        String continuousFocusDaysStr3 = "天";
//        Spannable continuousFocusDaysStr = new SpannableString(continuousFocusDaysStr1+continuousFocusDaysStr2+continuousFocusDaysStr3);
//        continuousFocusDaysStr.setSpan(new AbsoluteSizeSpan(80),continuousFocusDaysStr1.length(),continuousFocusDaysStr1.length()+continuousFocusDaysStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        continuousFocusDaysText.setText(continuousFocusDaysStr);
//        continuousFocusDaysText.setBackgroundResource(R.drawable.text_view_border);
        setContinuousDays(dateStr,userName);


        //专注记录标题
        focusRecordTitle = root.findViewById(R.id.focusRecordTitle);
        String focusRecordTitleStr1 = "今日专注记录";
        Spannable focusRecordTitleStr = new SpannableString(focusRecordTitleStr1);
        focusRecordTitleStr.setSpan(new AbsoluteSizeSpan(80),0,focusRecordTitleStr1.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusRecordTitleStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,focusRecordTitleStr1.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusRecordTitle.setText(focusRecordTitleStr);

        //专注记录实现（cardView+RecycleView)https://blog.csdn.net/u014752325/article/details/51384727
       // focusRecords = root.findViewById(R.id.focusRecords);//外面加一层relativeLayout
        //focusRecords.setMinimumHeight();
        //测试
        List<Focus> afocus = new ArrayList<>();
//        Focus f1 = new Focus();
//        f1.startHour = 2;
//        f1.startMinute = 30;
//        Focus f2 = new Focus();
//        f2.startHour = 3;
//        f2.startMinute = 30;
//        Focus f3 = new Focus();
//        f3.startHour = 4;
//        f3.startMinute = 40;
//        Focus f4 = new Focus();
//        f4.startHour = 5;
//        f4.startMinute = 50;
//        afocus.add(f1);
//        afocus.add(f2);
//        afocus.add(f3);
//        afocus.add(f4);
        for (int i = 0;i < MainActivity.flist.size();i++)
        {
            afocus.add(MainActivity.flist.get(i));
            System.out.print("ffl:"+MainActivity.flist.get(i).startHour + ":"+MainActivity.flist.get(i).startMinute+ MainActivity.flist.get(i).dura);

        }
        //更新专注详情列表：等待focusList不为空
        //TODO:bad Design
        //while(focusList.size() == 0 && !focusListFinish)
        //    continue;
        //focusListFinish = false;
//        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
//
//        focusRecords.setLayoutManager(layoutManager);
//        FocusRecordsAdapter fa = new FocusRecordsAdapter(focusList);
//        focusRecords.setAdapter(fa);
        //
        //focusRecords.



        return root;



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }





    //做更新真实TextView等控件的操作
    private void updateFocusTime(int newFocusTime) {
        System.out.println("in SetFocusTime");
        focusTodayText = root.findViewById(R.id.focusTodayText);
        //TextPaint paint = focusTodayText.getPaint();
        //paint.setFakeBoldText(true);
        String focusTodayTextStr1 = "今日专注时长:\n";
        String focusTodayTextStr2 = String.valueOf(newFocusTime);
        String focusTodayTextStr3 = "分钟";

        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2+focusTodayTextStr3);
        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        focusTodayText.setText(focusTodayTextStr);//此处根据后续取数据情况修改
        //focusTodayText.setBackgroundResource(R.drawable.text_view_border);
    }

    private void updateFocusTimes(int newFocusTimes) {
        focusTimesText = root.findViewById(R.id.focusTimesText);
        System.out.println("newFocusTime"+newFocusTimes);
        String reportFoucsTimesStr1 = " 专注次数：\n\n\n";
        String reportFocusTimesStr2 = " "+String.valueOf(newFocusTimes);;//从服务器端获取数据，填在这里
        String reportFocusTimesStr3 = "次";
        Spannable reportFoucsTimesStr = new SpannableString(reportFoucsTimesStr1+reportFocusTimesStr2+reportFocusTimesStr3);
        reportFoucsTimesStr.setSpan(new AbsoluteSizeSpan(80),reportFoucsTimesStr1.length(),reportFoucsTimesStr1.length()+reportFocusTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTimesText.setText(reportFoucsTimesStr);//此处根据后续取数据情况修改
        focusTimesText.setBackgroundResource(R.drawable.text_view_border);
    }

    private void updateInterruptTimes(int newInterruptTimes){
        interruptTimesText = root.findViewById(R.id.interruptTimesText);
        String reportInterruptTimesStr1 = " 打断次数： \n\n\n";
        String reportInterruptTimesStr2 = " "+String.valueOf(newInterruptTimes);//从服务器端获取数据，填在这里
        String reportInterruptTimesStr3 = "次";
        Spannable reportInterruptTimesStr = new SpannableString(reportInterruptTimesStr1+reportInterruptTimesStr2+reportInterruptTimesStr3);
        reportInterruptTimesStr.setSpan(new AbsoluteSizeSpan(80),reportInterruptTimesStr1.length(),reportInterruptTimesStr1.length()+reportInterruptTimesStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        interruptTimesText.setText(reportInterruptTimesStr);
        interruptTimesText.setBackgroundResource(R.drawable.text_view_border);

    }

    private void updateContinuousDays(int newContinuousDays)
    {
        continuousFocusDaysText = root.findViewById(R.id.continuousFocusDaysText);
        String continuousFocusDaysStr1 = " 连续专注天数： \n\n\n";
        String continuousFocusDaysStr2 = " "+String.valueOf(newContinuousDays);//从服务器端获取数据，填在这里
        String continuousFocusDaysStr3 = "天";
        Spannable continuousFocusDaysStr = new SpannableString(continuousFocusDaysStr1+continuousFocusDaysStr2+continuousFocusDaysStr3);
        continuousFocusDaysStr.setSpan(new AbsoluteSizeSpan(80),continuousFocusDaysStr1.length(),continuousFocusDaysStr1.length()+continuousFocusDaysStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        continuousFocusDaysText.setText(continuousFocusDaysStr);
        continuousFocusDaysText.setBackgroundResource(R.drawable.text_view_border);

    }

    public void jumpToDateChoose(View view)
    {
        //Date date = new Date();
        Calendar mCalendar=Calendar.getInstance();
        int mYear=mCalendar.get(Calendar.YEAR);
        int mMonth=mCalendar.get(Calendar.MONTH);
        int mDay=mCalendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),this,
                mYear,
                mMonth,
                mDay
        );
        dialog.show();


    }

    void updateRecords(final int oldSize)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long startTime =  System.currentTimeMillis();
                long endTime = System.currentTimeMillis();
                while(setFocusByDayFi == false && endTime - startTime < 3000)  endTime = System.currentTimeMillis();
                setFocusByDayFi = false;
                fa.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {//暂定，所有的更新都写在这里，肯定不是好方案，但进度优先
        this.year = year;
        this.month = month+1;
        this.day = dayOfMonth;

        String dateStr = this.year+"-"+this.month+"-"+this.day;

        dayNow.setText(dateStr);
        int oldListSize = focusList.size();
        setFocusByDay(dateStr,userName);
        setFocusTime(dateStr,userName);
        setFocusTimes(dateStr,userName);
        setContinuousDays(dateStr,userName);
        updateRecords(oldListSize);

        System.out.println("new FocusListSize = "+focusList.size());
        // focusList.add(MainActivity.flist.get(0));

        //while(!focusListFinish );
        chartView.setFocusList(focusList);

        //focusListFinish = false;
        System.out.println(focusList.size());
        System.out.println(dateStr);
        chartView.invalidate();



//
//        //根据日期取数
//
//        // *假设取到的新数*
//        int newFocusTime = 78;
//        final List<Focus> newFocusList = new ArrayList<>();
////        Focus f1 = new Focus();
////        f1.startHour = 15;
////        f1.startMinute = 30;
////        f1.dura = 10;
////        Focus f2 = new Focus();
////        f2.startHour = 13;
////        f2.startMinute = 30;
////        f2.dura = 20;
////        Focus f3 = new Focus();
////        f3.startHour = 14;
////        f3.startMinute = 40;
////        f3.dura = 30;
////        Focus f4 = new Focus();
////        f4.startHour = 16;
////        f4.startMinute = 50;
////        f4.dura = 40;
////        newFocusList.add(f1);
////        newFocusList.add(f2);
////        newFocusList.add(f3);
////        newFocusList.add(f4);
//
//        int newFocusTimes = 12;
//        int newInterruptTimes = 20;
//        int newContinuousDays = 11;
//
//
//        //*
//
//        //*尝试真实取数*
//        new Thread() {
//            public void run() {
//                try {
//                    String path = "http://49.232.5.236:8080/test/focusByDay?user=wangyihao&date=2019-10-31";
//                    URL url = new URL(path);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
//                    int code = conn.getResponseCode();
//                    if (code == 200) {
//                        InputStream is = conn.getInputStream();
//                        String result = StreamTools.readInputStream(is);
//                        JSONArray demo = new JSONArray(result);
//                        for(int i = 0;i < demo.length();i++)
//                        {
//                            JSONObject item = (JSONObject) demo.get(i);
//                            String beginTime = item.getString("begin");
//                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//                            Date date = format.parse(beginTime);
//                            Focus f = new Focus();
//                            f.startHour = date.getHours();
//                            f.startMinute = date.getMinutes();
//                            f.dura =(int) Double.parseDouble(item.getString("time"));
//                            newFocusList.add(f);
//                            System.out.println("Eren sss " + f.dura);
//                        }
//                        //JSONObject demoJson = new JSONObject(result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    System.out.println(e.getMessage());
//
//                    //System.out.println();
//
//                }
//            }
//        }.start();
//        //
//
//
//
//        //进行更新
//        //更新最上方统计图
//        chartView.setFocusList(newFocusList);
//        chartView.invalidate();
//        FocusRecordsAdapter fa = new FocusRecordsAdapter(newFocusList);
//        focusRecords.setAdapter(fa);
//
//        //更新专注时间
//        updateFocusTime(newFocusTime);
//        //更新专注次数,打断次数，连续专注天数，注意上面初始化部分可能也可以用这个重构
//        updateFocusTimes(newFocusTimes);
//        updateInterruptTimes(newInterruptTimes);
//        updateContinuousDays(newContinuousDays);
//
//
//


    }

    int setFocusTime(String date,String userName) {
        System.out.println("in SetFocusTime");
        final String finalDate = date;
        final String finalUserName = userName;
        int ret = -1;
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/totalFocusTime?user=" + finalUserName + "&date=" + finalDate + "&type=0";
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {

                        InputStream is = conn.getInputStream();

                        String result = StreamTools.readInputStream(is);
                        System.out.println("result is "+ result);
                        //JSONObject item = new JSONObject(result);
                        double focusTime = Double.parseDouble(result);
                        System.out.println("in SetFocusTime");
                        focusTodayText = root.findViewById(R.id.focusTodayText);
                        updateFocusTime((int)focusTime);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //System.out.println();

                }
            }

        }.start();
        return ret;
    }

    int setFocusTimes(String date,String userName) {
        System.out.println("in SetFocusTimes");
        final String finalDate = date;
        final String finalUserName = userName;
        int ret = -1;
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/totalFocusTimes?user=" + finalUserName + "&date=" + finalDate + "&type=0";
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {

                        InputStream is = conn.getInputStream();

                        String result = StreamTools.readInputStream(is);
                        System.out.println("result is "+ result);
                        //JSONObject item = new JSONObject(result);
                        double focusTimes = Double.parseDouble(result);
                        System.out.println("in SetFocusTimes");
                        focusTodayText = root.findViewById(R.id.focusTodayText);
                        updateFocusTimes((int)focusTimes);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //System.out.println();

                }
            }

        }.start();
        return ret;
    }

    int setInterruptTimes(String date,String userName) {
        System.out.println("in SetFocusTimes");
        final String finalDate = date;
        final String finalUserName = userName;
        int ret = -1;
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/totalInterruptTimes?user=" + finalUserName + "&date=" + finalDate + "&type=0";
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {

                        InputStream is = conn.getInputStream();

                        String result = StreamTools.readInputStream(is);
                        System.out.println("result is "+ result);
                        //JSONObject item = new JSONObject(result);
                        //double focusTimes = Double.parseDouble(result);
                        int interruptTImes = Integer.parseInt(result);
                        System.out.println("in SetFocusTimes");
                        focusTodayText = root.findViewById(R.id.focusTodayText);
                        updateInterruptTimes((int)interruptTImes);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //System.out.println();

                }
            }

        }.start();
        return ret;
    }

    int setContinuousDays(String date,String userName) {
        System.out.println("in SetFocusTimes");
        final String finalDate = date;
        final String finalUserName = userName;
        int ret = -1;
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/continuousFocusDay?user=" + finalUserName + "&date=" + finalDate;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {

                        InputStream is = conn.getInputStream();

                        String result = StreamTools.readInputStream(is);
                        System.out.println("result is "+ result);
                        //JSONObject item = new JSONObject(result);
                        //double focusTimes = Double.parseDouble(result);
                        int interruptTImes = Integer.parseInt(result);
                        System.out.println("in SetFocusTimes");
                        focusTodayText = root.findViewById(R.id.focusTodayText);
                        updateContinuousDays((int)interruptTImes);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //System.out.println();

                }
            }

        }.start();
        return ret;
    }

    void updateFocusByDay(List<Focus> newFocusList)
    {
        chartView.invalidate();
        chartView.setFocusList(newFocusList);





    }
    void setFocusByDay(String date,String userName)
    {
        System.out.println("in setFocusByDay");
        final String finalDate = date;
        final String finalUserName = userName;
       // f//inal List<Focus> newFocusList = new ArrayList<>();
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/focusByDay?user=" + finalUserName + "&date=" + finalDate;
                    System.out.println(path);
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                   // List<Focus> newFocusList = new ArrayList<>();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; KB974487)");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamTools.readInputStream(is);
                        JSONArray demo = new JSONArray(result);
                        focusList.clear();
                        for(int i = 0;i < demo.length();i++)
                        {
                            JSONObject item = (JSONObject) demo.get(i);
                            String beginTime = item.getString("begin");
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            Date date = format.parse(beginTime);
                            Focus f = new Focus();
                            f.startHour = date.getHours();
                            f.startMinute = date.getMinutes();
                            f.dura =(int) Double.parseDouble(item.getString("time"));
                            focusList.add(f);

                            System.out.println("Eren sss " + f.dura);
                        }
                        updateFocusByDay(focusList);

                        setFocusByDayFi = true;
                        //JSONObject demoJson = new JSONObject(result);
                    }
                    //focusListFinish = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    //focusListFinish = true;
                    System.out.println(e.getMessage());

                    //System.out.println();

                }
            }

        }.start();
    }

    public void onClickAllrecords(View view)
    {
        //userName = getArguments().getString("userName");
        System.out.println("onClick"+userName);
        Intent intent = new Intent(root.getContext(), ShowAllFocusRecords.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }



}
