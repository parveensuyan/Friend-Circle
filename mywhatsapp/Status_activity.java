package people.chat.mywhatsapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Status_activity extends AppCompatActivity {

    private static final int CREATE_REQUEST_CODE = 1;
    ListView mystatus_list;
    ArrayList<String> status;
    String mystatus;
    String mynumber;
    TextView typed_status;
    Button edit_status;
ArrayAdapter a;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mystatus_list= (ListView) findViewById(R.id.status_list);
        typed_status= (TextView) findViewById(R.id.status_now);
        edit_status= (Button) findViewById(R.id.status_edit_btn);
        status=new ArrayList<>();
        getmystatus();
        getallstatus();
        a=new ArrayAdapter<String>(Status_activity.this,android.R.layout.simple_list_item_1,status);

        mystatus_list.setAdapter(a);
        mystatus_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mystatus=status.get(position);
        insert_status();
    }
});
        edit_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myint=new Intent(Status_activity.this,TypeStatus.class);
                startActivityForResult(myint,1);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                mystatus = data.getStringExtra("RESULT_STRING");

     Log.d("status",mystatus);
                insert_status();

            }
        }
    }
    void getallstatus(){
       status.clear();
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();

        Cursor myresult;
        myresult = obj1.mydb.rawQuery("select mystatus from status_list_whatsapp order by id desc",null);
        while (myresult.moveToNext())
        {
            status.add(myresult.getString(myresult.getColumnIndex("mystatus")));
        }
        myresult.close();
        obj1.close1();
        status.add("At Pizza");
        status.add("In Love");
        status.add("Coffe Day");
        status.add("At Movie");
        status.add("Busy Day");
        status.add("Tired");
        status.add("Hate You");
        status.add("Enjoying Weather");

        Object[] st = status.toArray();
        for (Object s : st) {
            if (status.indexOf(s) != status.lastIndexOf(s)) {
                status.remove(status.lastIndexOf(s));
            }
        }

    }
    void getmystatus(){
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();
        Cursor myresult;
        myresult = obj1.mydb.rawQuery("select mystatus from mydata_whatsapp",null);
        if (myresult.moveToNext())
        {
            mystatus=myresult.getString(myresult.getColumnIndex("mystatus"));
        }
        myresult.close();
        obj1.close1();
        typed_status.setText(mystatus);
    }
    void insert_status()
    {   Confirmation_page obj=new Confirmation_page();
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();
        mynumber=obj.mynumber;
        obj.finish();
        obj1.insertdata_mydata(mynumber,null,null,null,null,mystatus,null);
        obj1.insertstatus(mystatus);
        obj1.close1();

        getallstatus();

typed_status.setText(mystatus);

        mystatus_list.setAdapter(a);
    }
}
