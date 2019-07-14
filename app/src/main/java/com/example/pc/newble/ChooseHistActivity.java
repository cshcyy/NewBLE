package com.example.pc.newble;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.content.Intent;

import java.util.Vector;

public class ChooseHistActivity extends AppCompatActivity {

    // existingDataUI: 2008年1月1日9点48分21秒
    // existingData：20080101094821
    private Vector<String> existingDataUI;
    private Vector<String> existingData;

    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hist);

        existingData = new Vector<String>();
        existingDataUI = new Vector<String>();

        // 添加listview项
        getAvailableHistData();
        existingDataUI.add("🌏清空所有数据🌍");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                ChooseHistActivity.this,   // Context上下文
                android.R.layout.simple_list_item_1,  // 子项布局id
                existingDataUI);                                // 数据
        ListView listView = (ListView) findViewById(R.id.hist_data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            //parent 代表listView View 代表 被点击的列表项 position 代表第几个 id 代表列表编号
            public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
          /*      if (position == existingData.size() - 1) {
                    android.widget.Toast.makeText(ChooseHistActivity.this, existingData.size() + "remain to be done", android.widget.Toast.LENGTH_LONG).show();
                    return;
                }
                */
                android.widget.Toast.makeText(ChooseHistActivity.this, "打开文件" + existingData.get(position), android.widget.Toast.LENGTH_LONG).show();
                String string = existingData.get(position);
                    Intent intent = new Intent(ChooseHistActivity.this, RetrieveData.class);
                    intent.putExtra("file_to_read", string);
                    startActivity(intent);


            }
        });
    }


    /*
    * 获取已储存的信息条目，并将它们添加到existingData中，以便用户点选
    * 返回值：Vector<String>类型的原始数据。
    * */
    protected void getAvailableHistData(){
        // 获取已存档信息的检索
        String path = FileUtils.getSDCardPath() + "/bletest/DataList.txt";
        Vector<String> retval = FileUtils.readTextFromFile(path);
        // 将从 "DataList.txt" 中读取的每一条信息添加到existingData里
        for (String item : retval){
            String string = item.substring(0,4) + "年" + item.substring(4,6) + "月" + item.substring(6,8) + "日"
                    + item.substring(8,10) + "时" + item.substring(10,12) + "分" + item.substring(12,14) + "秒";
            existingDataUI.add(string);
            existingData.add(item);
        }
     //   return retval;

    }

}

//Adapter.notifyDataSetChanged()