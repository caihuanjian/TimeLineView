package com.rain.timelineview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.rain.timelineview.views.TimeLineView;
import com.rain.timelineview.views.TimeSelectionBar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView left, right, center;
    private String[] datas = new String[]{"10min", "15min", "20min", "25min", "30min", "45min"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimeLineView timeLineView1 = findViewById(R.id.time1);
        timeLineView1.setDotSelectColor(Color.parseColor("#691997"));
        timeLineView1.initData(Arrays.asList(datas));
        timeLineView1.setOnSelectResultListener(new TimeLineView.OnSelectResultListener() {
            @Override
            public void onSelectResult(List<Integer> selections) {
                StringBuilder sb = new StringBuilder();
                for (Integer i : selections) {
                    sb.append(datas[i]);
                    sb.append("  ");
                }
                Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        timeLineView1.selectByIndex(0);

        TimeLineView timeLineView2 = findViewById(R.id.time2);
        timeLineView2.setMode(TimeLineView.MODE_MULTI_CHOOSE);
        timeLineView2.initData(Arrays.asList(datas));
        timeLineView2.setOnSelectResultListener(new TimeLineView.OnSelectResultListener() {
            @Override
            public void onSelectResult(List<Integer> selections) {
                StringBuilder sb = new StringBuilder();
                for (Integer i : selections) {
                    sb.append(datas[i]);
                    sb.append("  ");
                }
                Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        timeLineView2.selectByIndex(0);

        //进度条
        left = findViewById(R.id.tv_left);
        right = findViewById(R.id.tv_right);
        center = findViewById(R.id.tv_center);
        TimeSelectionBar timeSelectionBar = findViewById(R.id.time_select_bar);
        timeSelectionBar.setOnProgressChangeListener(new TimeSelectionBar.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int leftPercent, int rightPercent, int centerPercent) {
                left.setText(String.valueOf((leftPercent)));
                right.setText(String.valueOf(rightPercent));
                center.setText(String.valueOf(centerPercent));
            }
        });
    }
}
