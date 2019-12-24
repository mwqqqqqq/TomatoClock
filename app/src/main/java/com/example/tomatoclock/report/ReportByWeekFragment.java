package com.example.tomatoclock.report;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tomatoclock.R;
import com.example.tomatoclock.StreamTools;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;


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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportByWeekFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportByWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportByWeekFragment extends Fragment {
    View root;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    BarChart chart;
    TextView focusTimeThisWeek;

    BarChart interruptChart;
    TextView interruptTimesThisWeek;

    String userName;

    TextView textViewDate;

    public ReportByWeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReportByWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportByWeekFragment newInstance() {
        ReportByWeekFragment fragment = new ReportByWeekFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String finalUserName =getArguments().getString("userName");

        String local = "GMT+8";//获取当前时间
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(local));
        cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        final String dateStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        String timeStr = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - cal.get(Calendar.DAY_OF_WEEK));
        String dateThisMondayStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        cal.add(Calendar.DATE, 6);
        String dateThisSundayStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        final String[] xAxisDate = new String[7];
        final List<BarEntry> entries = new ArrayList<>();
        userName = getArguments().getString("userName");
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/FocusTimeByDayOfWeek?user="+finalUserName+"&date="+dateStr;
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
                        //JSONArray demo = new JSONArray(result);
                        JSONObject  day2Time = new JSONObject(result);
                        Iterator iter = day2Time.keys();
                        int i = 0;
                        while(iter.hasNext())
                        {
                            String dateStr = iter.next().toString();
                            xAxisDate[i] = dateStr.substring(5);
                            entries.add(new BarEntry(i, day2Time.getInt(dateStr)));
                            System.out.println(xAxisDate[i]+" " + day2Time.getInt(dateStr));
                            i++;


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                    //System.out.println();

                }
            }

        }.start();
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_report_by_week, container, false);
        while(entries.size() != 7)
            continue;
        //textViewDate = root.findViewById(R.id.textView13);
        //textViewDate.setText(dateThisMondayStr+" To "+dateThisSundayStr);

        chart =  root.findViewById(R.id.chart);
        //此处应该替换为去给定接口取数

        //chart.setDrawLabels(true);

        XAxis xAxis = chart.getXAxis();
        final String[] values = xAxisDate;
        ValueFormatter formatter = new  ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return values[(int) value];
            }

        };

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
//        entries.add(new BarEntry(0f, 30f));
//        entries.add(new BarEntry(1f, 80f));
//        entries.add(new BarEntry(2f, 60f));
//        entries.add(new BarEntry(3f, 50f));
//        entries.add(new BarEntry(4,20));
//        // gap of 2f
//        entries.add(new BarEntry(5f, 70f));
//        entries.add(new BarEntry(6f,40f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(Color.parseColor("#FFA500"));
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

        setFocusTimeByWeek(dateStr,userName);
//        int newFocusTime = 10;
//        focusTimeThisWeek = root.findViewById(R.id.chartTitle);
//        //TextPaint paint = focusTodayText.getPaint();
//        //paint.setFakeBoldText(true);
//        String focusTodayTextStr1 = "本周专注时长:\n";
//        String focusTodayTextStr2 = String.valueOf(newFocusTime);
//        String focusTodayTextStr3 = "分钟";
//
//        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2+focusTodayTextStr3);
//        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//        focusTimeThisWeek.setText(focusTodayTextStr);//此处根据后续取数据情况修改


        //中断次数相关的图表和文字
        interruptChart = root.findViewById(R.id.interruptChart);
        final List<BarEntry> entriesInter = new ArrayList<>();
        XAxis xAxisInter = interruptChart.getXAxis();
        final String[] valuesInter = new String[7];
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/InterruptionTimesByDayOfWeek?user="+finalUserName+"&date="+dateStr;
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
                        //JSONArray demo = new JSONArray(result);
                        JSONObject  day2Time = new JSONObject(result);
                        Iterator iter = day2Time.keys();
                        int i = 0;
                        while(iter.hasNext())
                        {
                            String dateStr = iter.next().toString();
                            valuesInter[i] = dateStr.substring(5);
                            entriesInter.add(new BarEntry(i, day2Time.getInt(dateStr)));
                            System.out.println(xAxisDate[i]+" " + day2Time.getInt(dateStr));
                            i++;


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                    //System.out.println();

                }
            }

        }.start();
        while(entriesInter.size() !=7 )
            continue;
        ValueFormatter formatterInter = new  ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return valuesInter[(int) value];
            }

        };

        xAxisInter.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxisInter.setValueFormatter(formatterInter);
        xAxisInter.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisInter.setDrawLabels(true);
        Legend legendInter = interruptChart.getLegend();
        legendInter.setEnabled(false);
        Description descriptionInter = new Description();
        descriptionInter.setText("");
        interruptChart.setDescription(descriptionInter);

        //entriesInter.add(new BarEntry(0, 2));
        //entriesInter.add(new BarEntry(1, 2));
        //entriesInter.add(new BarEntry(2, 0));
        BarDataSet setInter = new BarDataSet(entriesInter, "BarDataSetInter");
        setInter.setColor(Color.parseColor("#FFA500"));
        BarData dataInter = new BarData(setInter);
        dataInter.setBarWidth(0.9f); // set custom bar width
        interruptChart.setData(dataInter);
        interruptChart.setFitBars(true); // make the x-axis fit exactly all bars
        interruptChart.invalidate(); // refresh

        setInterruptTimesByWeek(dateStr,userName);



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
        focusTimeThisWeek = root.findViewById(R.id.chartTitle);
        //TextPaint paint = focusTodayText.getPaint();
        //paint.setFakeBoldText(true);
        String focusTodayTextStr1 = "过去七天专注时长:\n";
        String focusTodayTextStr2 = String.valueOf(newFocusTime);
        String focusTodayTextStr3 = "分钟";

        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2+focusTodayTextStr3);
        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        focusTimeThisWeek.setText(focusTodayTextStr);//此处根据后续取数据情况修改
        //focusTodayText.setBackgroundResource(R.drawable.text_view_border);
    }


    int setFocusTimeByWeek(String date,String userName) {
        System.out.println("in SetFocusTime");
        final String finalDate = date;
        final String finalUserName = userName;
        int ret = -1;
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/totalFocusTime?user=" + finalUserName + "&date=" + finalDate + "&type=1";
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
                        focusTimeThisWeek = root.findViewById(R.id.focusTodayText);
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

    private void updateInterruptTimes(int newInterruptTimes){
        System.out.println("in SetFocusTime");
        interruptTimesThisWeek = root.findViewById(R.id.interruptChartTitle);
        //TextPaint paint = focusTodayText.getPaint();
        //paint.setFakeBoldText(true);
        String focusTodayTextStr1 = "过去七天打断次数:\n";
        String focusTodayTextStr2 = String.valueOf(newInterruptTimes);
        String focusTodayTextStr3 = "次";

        Spannable focusTodayTextStr =  new SpannableString(focusTodayTextStr1+focusTodayTextStr2+focusTodayTextStr3);
        focusTodayTextStr.setSpan(new AbsoluteSizeSpan(80),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        focusTodayTextStr.setSpan(new ForegroundColorSpan(Color.BLACK),focusTodayTextStr1.length(),focusTodayTextStr1.length()+focusTodayTextStr2.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        //focusTodayText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        interruptTimesThisWeek.setText(focusTodayTextStr);//此处根据后续取数据情况修改
        //focusTodayText.setBackgroundResource(R.drawable.text_view_border);

    }

    int setInterruptTimesByWeek(String date,String userName) {
        System.out.println("in SetFocusTimes");
        final String finalDate = date;
        final String finalUserName = userName;
        int ret = -1;
        new Thread() {
            public void run() {
                try {
                    String path = "http://49.232.5.236:8080/test/totalInterruptTimes?user=" + finalUserName + "&date=" + finalDate + "&type=1";
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
                        //focusTodayText = root.findViewById(R.id.focusTodayText);
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
}
