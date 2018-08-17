package com.ezparking.com.blelearn;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

public class ExpandViewActivity extends AppCompatActivity {


    protected ExpandableListView expandaFirst;
    private String[] titles = {"Activity", "Service", "BroadcastReciver", "ContentProvider"};
    private String[][] subtitles = {
            {"the lifecycle", "LcanuchMode & Flag", "intent and 序列化", "activity crate process"},
            {"startService& bindService", "service keep alive", "aidl & messanger", "intent service"},
            {"LocalBroadcastManger", "register/unregister"},
            {"normal add delete change query", "ContentObser", "implement ContentProvider"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_expand_view);
        initView();

    }

    private void initView() {
        expandaFirst = (ExpandableListView) findViewById(R.id.expanda_first);
        H1ExpandAdapter adapter = new H1ExpandAdapter(this,subtitles,titles);
        expandaFirst.setAdapter(adapter);
       expandaFirst.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
           @Override
           public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
               Log.e("zyh","onChildClickListener--> groupPosition  "+groupPosition+" childPosition--> "+childPosition);
               return false;
           }
       });

       expandaFirst.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
           @Override
           public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
               Log.e("zyh","onGroupClickListener--> groupPosition  "+groupPosition);
               return false;
           }
       });

      expandaFirst.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
          @Override
          public void onGroupCollapse(int groupPosition) {
              Log.e("zyh","onGroupCollapseListener--> groupPosition  "+groupPosition);

          }
      });

      expandaFirst.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
          @Override
          public void onGroupExpand(int groupPosition) {
              Log.e("zyh","onGroupExpandListener--> groupPosition  "+groupPosition);
          }
      });
    }
}
