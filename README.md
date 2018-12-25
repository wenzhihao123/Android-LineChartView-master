# Android-LineChartView-master
最近看到有人在做带有折线图的项目，看着挺牛叉的，于是动手写了一把，记录下来绘制的过程，希望不会遗忘，主要就是计算问题，其他就是canvas绘制的过程了。

![](https://upload-images.jianshu.io/upload_images/2018489-e604cab833d2dc07.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

简单封装了一下几个关键属性,使用的姿势：
```
<com.wzh.linechart.view.LineView
        android:id="@+id/lineView"
        android:layout_width="match_parent"
        android:padding="@dimen/activity_horizontal_margin"
        android:layout_height="match_parent"
        app:textColor="@android:color/black"
        app:isShowColumnLine="true"
        app:isShowRowLine="false"
        app:dashColor="@color/colorPrimary"
        app:dataFactor="100"
        app:dotRadius="4dp"
        app:isLinearGradient="true"
        app:lineColor="@color/colorAccent"
        app:lineWidth="2dp"/>

```
- isShowColumnLine ：是否显示竖直方向的垂直线（如图虚线所示）
- isShowRowLine：是否显示水平方向的垂直线（图上是没显示出来）
- dashColor：这个是垂直线的颜色
- dataFactor：这个是y轴数据的因子，比如都是2位数的数据该值就为10，三位数就为100...
- dotRadius：折线图上数据点的半径大小
- isLinearGradient：是否折线图下面显示渐变
- lineColor：折线的颜色
- lineWidth：折现的粗细

在代码中去设置数据集合及横左边的lable，以及x,y轴的名称等
```
private String yTitle  = "销售额（单位：万元）";
private String xTitle = "(年份)";
private int[] data = {70, 80, 90, 60, 80, 70, 40};
private String[] lables = {"2010", "2011", "2012", "2013", "2014", "2015", "2016"};

lineView.setData(data);
lineView.setLables(lables);
lineView.setxTitle(xTitle);
lineView.setyTitle(yTitle);
lineView.setDataFactor(10);

```

这样设置完之后就可以运行出来效果了。

总结下：通过此自定义折线图熟练了绘制点、线、path、文本、以及根据最大和最小的数据值及高度得到的比例，去计算每个数据应在的y坐标，还学会如何使用LinearGradient去绘制渐变。
