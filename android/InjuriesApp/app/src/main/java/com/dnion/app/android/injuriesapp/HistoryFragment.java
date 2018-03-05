package com.dnion.app.android.injuriesapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfoDao;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class HistoryFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "history_fragment";

    private LineChart mLineChart;

    private BarChart mBarChart;

    private ListView tableList;

    private TableAdapter adapter;

    private MainActivity mActivity;

    private RecordDao recordDao;

    private List<RecordInfo> list;

    public static HistoryFragment createInstance() {
        HistoryFragment fragment = new HistoryFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) HistoryFragment.this.getActivity();
        recordDao = mActivity.getRecordDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        mBarChart = (BarChart)rootView.findViewById(R.id.bar_chart);
        mLineChart = (LineChart)rootView.findViewById(R.id.line_chart);
        tableList = (ListView)rootView.findViewById(R.id.table_list);
        String inpatientNo = mActivity.getRecordInfo().getInpatientNo();
        list = recordDao.queryRecordHistoryList(inpatientNo);
        initBarChart();
        initLineChart();
        initListView();
    }

    private void initListView() {
        //List<RecordInfo> list = new ArrayList<>();
        //RecordInfo recordInfo = new RecordInfo();
        //recordInfo.setWoundHeight(1.0f);
        //recordInfo.setWoundWidth(2.0f);
        //list.add(recordInfo);
        adapter = new TableAdapter(mActivity, list);
        tableList.setAdapter(adapter);
    }

    private void initBarChart() {
        float groupSpace = 0.25f;
        float barSpace = 0.01f; // x3 DataSet
        float barWidth = 0.24f; // x3 DataSet
        // (0.24 + 0.01) * 3 + 0.25 = 1.00 -> interval per "group"
        //设置描述信息
        mBarChart.getDescription().setText("");
        //设置没有数据时显示的文本
        mBarChart.setNoDataText(mActivity.getText(R.string.no_data).toString());
        //mBarChart.setOnChartValueSelectedListener(this);
        mBarChart.getDescription().setEnabled(false);
//        mChart.setDrawBorders(true);
        // scaling can now only be done on x- and y-axis separately
        mBarChart.setTouchEnabled(false); // 设置是否可以触摸
        mBarChart.setDragEnabled(false);// 是否可以拖拽
        mBarChart.setScaleEnabled(false);// 是否可以缩放
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);
        //设置图例在上方
        Legend legend = mBarChart.getLegend();
        legend.setTextSize(16);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        mBarChart.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setTextSize(14);
        xAxis.setLabelRotationAngle(30);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置横坐标在底部
        xAxis.setGridColor(Color.TRANSPARENT);//去掉网格中竖线的显
        //xAxis.setDrawAxisLine(false);
        if (list != null && list.size() > 0) {
            xAxis.setLabelCount(list.size() - 1, false);
        }
        //xAxis.setXOffset(30);
        //mBarChart.setExtraLeftOffset(-30);
        mBarChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d("chenweihua", "mBarChart xAxis value="+value);
                if (value <= 0 || value > list.size()) {
                    return "";
                }
                RecordInfo recordInfo = list.get((int)value - 1);
                return recordInfo.getRecordTime().substring(5, 10);
            }
        });

        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setTextSize(14);
        yAxis.setSpaceBottom(0);
        yAxis.setAxisMinimum(0);
        //yAxis.setXOffset(30);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.1f", value) + "%";
            }
        });

        float start = 0.5f;
        int groupCount = list.size();
        List<BarEntry> yRed = new ArrayList<>();
        List<BarEntry> yYellow = new ArrayList<>();
        List<BarEntry> yBlack = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            RecordInfo recordInfo = list.get(i);
            //String date = recordInfo.getRecordTime();
            yRed.add(new BarEntry(i, formatFloatValue(recordInfo.getWoundColorRed())));
            yYellow.add(new BarEntry(i, formatFloatValue(recordInfo.getWoundColorYellow())));
            yBlack.add(new BarEntry(i, formatFloatValue(recordInfo.getWoundColorBlack())));
        }
        // create 4 DataSets
        BarDataSet setRed = new BarDataSet(yRed, mActivity.getText(R.string.measure_red).toString());
        setRed.setDrawValues(false);
        setRed.setColor(Color.RED);
        BarDataSet setYellow = new BarDataSet(yYellow, mActivity.getText(R.string.measure_yellow).toString());
        setYellow.setDrawValues(false);
        setYellow.setColor(Color.YELLOW);
        BarDataSet setBlack = new BarDataSet(yBlack, mActivity.getText(R.string.measure_black).toString());
        setBlack.setDrawValues(false);
        setBlack.setColor(Color.BLACK);

        BarData barData = new BarData(setRed, setYellow, setBlack);
        //barData.setValueFormatter(new LargeValueFormatter());
        //barData.setValueTypeface(mTfLight);
        mBarChart.setData(barData);

        // specify the width each bar should have
        mBarChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        xAxis.setAxisMinimum(start);
        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        xAxis.setAxisMaximum(start + mBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        mBarChart.groupBars(start, groupSpace, barSpace);

        //设置动画
        //mLineChart.animateXY(2000,3000);
    }

    private float formatFloatValue(Float value) {
        if (value == null) {
            return 0;
        }
        return value.floatValue();
    }

    private void initLineChart() {
        //设置描述信息
        mLineChart.getDescription().setText("");
        //mLineChart.getDescription().setPosition(100, 20);
        //设置没有数据时显示的文本
        mLineChart.setNoDataText(mActivity.getText(R.string.no_data).toString());

        mLineChart.setTouchEnabled(false); // 设置是否可以触摸
        mLineChart.setDragEnabled(false);// 是否可以拖拽
        mLineChart.setScaleEnabled(false);// 是否可以缩放

        //设置图例在上方
        Legend legend = mLineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTextSize(16);
        mLineChart.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setTextSize(14);
        xAxis.setLabelRotationAngle(30);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置横坐标在底部
        xAxis.setGridColor(Color.TRANSPARENT);//去掉网格中竖线的显示
        //xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            //private SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.d("chenweihua", "mLineChart xAxis value="+value);
                if (value >= list.size()) {
                    return "";
                }
                RecordInfo recordInfo = list.get((int)value);
                return recordInfo.getRecordTime().substring(5, 10);
            }
        });

        if (list != null && list.size() > 0) {
            xAxis.setLabelCount(list.size() - 1, false);////第一个参数是坐标轴label的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        }


        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setTextSize(14);
        yAxis.setSpaceBottom(0);
        yAxis.setAxisMinimum(0);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.1f", value);
            }
        });

        //每个点的坐标,自己随便搞点（x,y）坐标就可以了
        ArrayList<Entry> widthValues = new ArrayList<Entry>();

        //Calendar calendar = Calendar.getInstance();
        long time = 0;
        for (int i = 0; i < list.size(); i++) {
            RecordInfo recordInfo = list.get(i);
            //widthValues.add(new Entry(i + 1, formatFloatValue(recordInfo.getWoundWidth())));
            widthValues.add(new Entry(i, formatFloatValue(recordInfo.getWoundDeep())));
        }

        //calendar = Calendar.getInstance();
        ArrayList<Entry> heightValues = new ArrayList<Entry>();
        for (int i = 0; i < list.size(); i++) {
            RecordInfo recordInfo = list.get(i);
            //heightValues.add(new Entry(i + 1, formatFloatValue(recordInfo.getWoundHeight())));
            heightValues.add(new Entry(i, formatFloatValue(recordInfo.getWoundArea())));
        }

        LineDataSet widthDataSet = new LineDataSet(widthValues, mActivity.getText(R.string.measure_deep).toString());
        widthDataSet.setDrawCircles(false);//不显示坐标点的小圆点
        widthDataSet.setDrawValues(false);//不显示坐标点的数据
        widthDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置平滑曲线
        widthDataSet.setColor(Color.BLUE);
        LineDataSet heightDataSet = new LineDataSet(heightValues, mActivity.getText(R.string.measure_area).toString());
        heightDataSet.setDrawCircles(false);//不显示坐标点的小圆点
        heightDataSet.setDrawValues(false);//不显示坐标点的数据
        heightDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置平滑曲线
        heightDataSet.setColor(Color.GREEN);
        //线的集合（可单条或多条线）
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(widthDataSet);
        dataSets.add(heightDataSet);
        //把要画的所有线(线的集合)添加到LineData里
        LineData lineData = new LineData(dataSets);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(list.size() - 1);
        //把最终的数据setData
        mLineChart.setData(lineData);
        //设置动画
        //mLineChart.animateXY(2000,3000);
    }


    private void initChart() {

        //描述信息
        Description description = new Description();
        description.setText("我是描述信息");
        //设置描述信息
        mLineChart.setDescription(description);
        //设置没有数据时显示的文本
        mLineChart.setNoDataText(mActivity.getText(R.string.measure_width).toString());
        //设置是否绘制chart边框的线
        mLineChart.setDrawBorders(true);
        //设置chart边框线颜色
        mLineChart.setBorderColor(Color.GRAY);
        //设置chart边框线宽度
        mLineChart.setBorderWidth(1f);
        //设置chart是否可以触摸
        mLineChart.setTouchEnabled(false);
        //设置是否可以拖拽
        mLineChart.setDragEnabled(false);
        //设置是否可以缩放 x和y，默认true
        mLineChart.setScaleEnabled(false);
        //设置是否可以通过双击屏幕放大图表。默认是true
        mLineChart.setDoubleTapToZoomEnabled(false);
        //设置chart动画
        mLineChart.animateXY(1000, 1000);

        //=========================设置图例=========================
        // 像"□ xxx"就是图例
        Legend legend = mLineChart.getLegend();
        //设置图例显示在chart那个位置 setPosition建议放弃使用了
        //设置垂直方向上还是下或中
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //设置水平方向是左边还是右边或中
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        //设置所有图例位置排序方向
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //设置图例的形状 有圆形、正方形、线
        legend.setForm(Legend.LegendForm.CIRCLE);
        //是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
        legend.setWordWrapEnabled(true);


        //=======================设置X轴显示效果==================
        XAxis xAxis = mLineChart.getXAxis();
        //是否启用X轴
        xAxis.setEnabled(true);
        //是否绘制X轴线
        xAxis.setDrawAxisLine(true);
        //设置X轴上每个竖线是否显示
        xAxis.setDrawGridLines(true);
        //设置是否绘制X轴上的对应值(标签)
        xAxis.setDrawLabels(true);
        //设置X轴显示位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置竖线为虚线样式
        // xAxis.enableGridDashedLine(10f, 10f, 0f);
        //设置x轴标签数
        xAxis.setLabelCount(5, true);
        //图表第一个和最后一个label数据不超出左边和右边的Y轴
        // xAxis.setAvoidFirstLastClipping(true);

        //=================设置左边Y轴===============
        YAxis axisLeft = mLineChart.getAxisLeft();
        //是否启用左边Y轴
        axisLeft.setEnabled(true);
        //设置最小值（这里就按demo里固死的写）
        axisLeft.setAxisMinimum(1);
        //设置最大值（这里就按demo里固死的写了）
        axisLeft.setAxisMaximum(20);
        //设置横向的线为虚线
        axisLeft.enableGridDashedLine(10f, 10f, 0f);
        //axisLeft.setDrawLimitLinesBehindData(true);

        //设置限制线 12代表某个该轴某个值，也就是要画到该轴某个值上
        LimitLine limitLine = new LimitLine(12);
        //设置限制线的宽
        limitLine.setLineWidth(1f);
        //设置限制线的颜色
        limitLine.setLineColor(Color.RED);
        //设置基线的位置
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLine.setLabel("马丹我是基线，也可以叫我水位线");
        //设置限制线为虚线
        limitLine.enableDashedLine(10f, 10f, 0f);
        //左边Y轴添加限制线
        axisLeft.addLimitLine(limitLine);

        //====================设置右边的Y轴===============
        YAxis axisRight = mLineChart.getAxisRight();
        //是否启用右边Y轴
        axisRight.setEnabled(true);
        //设置最小值（这里按demo里的数据固死写了）
        axisRight.setAxisMinimum(1);
        //设置最大值（这里按demo里的数据固死写了）
        axisRight.setAxisMaximum(20);
        //设置横向的线为虚线
        axisRight.enableGridDashedLine(10f, 10f, 0f);
    }

    //设置数据
    private void initData() {
        //每个点的坐标,自己随便搞点（x,y）坐标就可以了
        ArrayList<Entry> pointValues = new ArrayList<Entry>();
        for (int i = 1; i < 19; i++) {
            int y = (int)( Math.random() * 20);
            pointValues.add(new Entry(i, y));

        }

        //点构成的某条线
        LineDataSet lineDataSet = new LineDataSet(pointValues, "该线标签1");
        //设置该线的颜色
        lineDataSet.setColor(Color.RED);
        //设置每个点的颜色
        lineDataSet.setCircleColor(Color.YELLOW);
        //设置该线的宽度
        lineDataSet.setLineWidth(1f);
        //设置每个坐标点的圆大小
        //lineDataSet.setCircleRadius(1f);
        //设置是否画圆
        lineDataSet.setDrawCircles(false);
        // 设置平滑曲线模式
        //  lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //设置线一面部分是否填充颜色
        lineDataSet.setDrawFilled(true);
        //设置填充的颜色
        lineDataSet.setFillColor(Color.BLUE);
        //设置是否显示点的坐标值
        lineDataSet.setDrawValues(false);

        //线的集合（可单条或多条线）
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        //把要画的所有线(线的集合)添加到LineData里
        LineData lineData = new LineData(dataSets);
        //把最终的数据setData
        mLineChart.setData(lineData);

    }

    private class TableAdapter extends BaseAdapter {
        private List<RecordInfo> list;
        private LayoutInflater inflater;

        public TableAdapter(Context context, List<RecordInfo> list){
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int ret = 0;
            if(list!=null){
                ret = list.size();
            }
            return ret;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RecordInfo goods = (RecordInfo) this.getItem(position);

            ViewHolder viewHolder;

            if(convertView == null){

                viewHolder = new ViewHolder();

                convertView = inflater.inflate(R.layout.table_item, null);
                viewHolder.text_time = (TextView) convertView.findViewById(R.id.text_time);
                viewHolder.text_height = (TextView) convertView.findViewById(R.id.text_height);
                viewHolder.text_width = (TextView) convertView.findViewById(R.id.text_width);
                viewHolder.text_area = (TextView) convertView.findViewById(R.id.text_area);
                viewHolder.text_volume = (TextView) convertView.findViewById(R.id.text_volume);
                viewHolder.text_red = (TextView) convertView.findViewById(R.id.text_red);
                viewHolder.text_yellow = (TextView) convertView.findViewById(R.id.text_yellow);
                viewHolder.text_black = (TextView) convertView.findViewById(R.id.text_black);


                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            int data_text_size = 16;
            viewHolder.text_time.setText(goods.getRecordTime());
            viewHolder.text_time.setTextSize(data_text_size);
            viewHolder.text_height.setText("" + formatFloatValue(goods.getWoundHeight()));
            viewHolder.text_height.setTextSize(data_text_size);
            viewHolder.text_width.setText("" + formatFloatValue(goods.getWoundWidth()));
            viewHolder.text_width.setTextSize(data_text_size);
            viewHolder.text_area.setText("" + formatFloatValue(goods.getWoundArea()));
            viewHolder.text_area.setTextSize(data_text_size);
            //viewHolder.text_volume.setText("" + formatFloatValue(goods.getWoundVolume()));
            viewHolder.text_volume.setText("" + formatFloatValue(goods.getWoundDeep()));
            viewHolder.text_volume.setTextSize(data_text_size);
            viewHolder.text_red.setText("" + formatFloatValue(goods.getWoundColorRed()));
            viewHolder.text_red.setTextSize(data_text_size);
            viewHolder.text_yellow.setText("" + formatFloatValue(goods.getWoundColorYellow()));
            viewHolder.text_yellow.setTextSize(data_text_size);
            viewHolder.text_black.setText("" + formatFloatValue(goods.getWoundColorBlack()));
            viewHolder.text_black.setTextSize(data_text_size);
            return convertView;
        }
    }

    private static class ViewHolder{
        public TextView text_time;
        public TextView text_height;
        public TextView text_width;
        public TextView text_area;
        public TextView text_volume;
        public TextView text_red;
        public TextView text_yellow;
        public TextView text_black;
    }
}