package com.example.pc.newble;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import java.util.ArrayList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.io.*;
import java.util.Iterator;
import java.util.Vector;

public class RetrieveData extends AppCompatActivity {

    public static final String TAG = "RetrieveData.this";

    private LineChart mChart;
    private boolean isRunning;
    private Thread thread;

    // 在 onCreate 里取消了 handler 转而直接调用 onClick，由于这个过程耗时不长，没有必要用handler
    private Handler handler;

    //
    private String file;

    /**
    * 对./bletest/目录下的假设
    * - 应该有一个命名为 "DataList.txt" 的文件用以储存各 entry 的信息
    * - 每一个 entry，都应该有一个独立的存储文档，例："/bletest/20190710025510.txt"
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_data);

        mChart = (LineChart) findViewById(R.id.chart);
        showChart(getLineData(12, 2000));

        Intent intent = getIntent();
        file = FileUtils.getSDCardPath() + "/bletest/" + intent.getStringExtra("file_to_read");
        Toast.makeText(this, file, Toast.LENGTH_SHORT).show();

        Button button = findViewById(R.id.button_retrieve);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "onClick: 点击了按钮" );
                Log.e(TAG, file );

                File file_ = new File(file);
                if (file_.exists() == true){
                    Toast.makeText(RetrieveData.this, "正在恢复数据..." + file, Toast.LENGTH_LONG).show();
                }


                Vector<Double> aaa = FileUtils.readVoltageFromFile(file);
                Log.e(TAG, "onClick: 数据从vector读取结束");
                LineData lineData = RetrieveDataFromVector(FileUtils.readVoltageFromFile(file));

                Log.e(TAG, "onClick: 数据读取结束");
           //     mChart.invalidate();
                mChart.clear();

                Log.e(TAG, "onClick: 清理完毕");
                showChart(lineData);
            }
        });

    }

    private void showChart(LineData lineData) {
        // 设置描述
        mChart.setDescription("折线图演示");
        // 设置触摸模式
        mChart.setTouchEnabled(true);
        // 设置图表数据
        mChart.setData(lineData);
    }

    /**
     * @param count 横向点个数
     * @param range 纵向变化幅度
     * @return
     */
    private LineData getLineData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i + 1) + "月");
        }

        /////////////////////////////////////////

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 1000;
            yVals.add(new Entry(val, i));
        }

        // 创建数据集
        LineDataSet set = new LineDataSet(yVals, "数据集");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.YELLOW);
        set.setLineWidth(2f);
        set.setCircleSize(3f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawCircleHole(false);

        ////////////////////////////////////////////////

        // 创建数据集列表
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set);

        // 创建折线数据对象（第二个参数可以是set）
        LineData lineData = new LineData(xVals, dataSets);
        lineData.setValueTextColor(Color.BLACK);
        lineData.setValueTextSize(9f);

        return lineData;
    }

    public void doStart(View view) {
        isRunning = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        handler.sendEmptyMessage(0x001);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void doStop(View view) {
        isRunning = false;
        thread = null;
    }





    public LineData RetrieveDataFromVector(Vector<Double> vector){

        Log.e(TAG, "RetrieveDataFromVector: 进入了retrieve from vector函数" );
        int count = vector.size();

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i + 1) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = (float) vector.get(i).floatValue();
            yVals.add(new Entry(val, i));
        }

        // 创建数据集
        LineDataSet set = new LineDataSet(yVals, "数据集");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.YELLOW);
        set.setLineWidth(2f);
        set.setCircleSize(3f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawCircleHole(false);


        ////////////////////////////////////////////////

        // 创建数据集列表
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set);

        // 创建折线数据对象（第二个参数可以是set）
        LineData lineData = new LineData(xVals, dataSets);
        lineData.setValueTextColor(Color.BLACK);
        lineData.setValueTextSize(9f);

        return lineData;
    }





}


