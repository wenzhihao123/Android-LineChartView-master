package com.wzh.linechart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wzh.linechart.view.LineView;

public class MainActivity extends AppCompatActivity {
    private LineView lineView ;
    private String yTitle  = "销售额（单位：万元）";
    private String xTitle = "(年份)";
    private int[] data = {70, 80, 90, 60, 80, 70, 40};
    private String[] lables = {"2010", "2011", "2012", "2013", "2014", "2015", "2016"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lineView = (LineView) findViewById(R.id.lineView);
        lineView.setData(data);
        lineView.setLables(lables);
        lineView.setxTitle(xTitle);
        lineView.setyTitle(yTitle);
        lineView.setDataFactor(10);
    }
}
