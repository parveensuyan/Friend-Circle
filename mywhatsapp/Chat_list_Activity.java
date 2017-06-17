package people.chat.mywhatsapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Chat_list_Activity extends AppCompatActivity {
    public Chat_list_Activity(){
    }

    ArrayList<String> contact_names;

ChatActivity obj=new ChatActivity();
    ArrayList<String> contact_numbers;
    ArrayList<String> last_msg1;
    ArrayList<String> last_time;
    ArrayList<String> mute_contact1;
    ArrayList<String> dpname;
    ArrayList<String> last_seen;
    ArrayList<String> last_status_time;
    ArrayList<String> unread_msg;

    ListView chat_list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        chat_list = (ListView) findViewById(R.id.chat_list);
        contact_names = new ArrayList<>();
        contact_numbers = new ArrayList<>();
        last_msg1 = new ArrayList<>();
        last_time = new ArrayList<>();
        dpname = new ArrayList<>();
        last_seen = new ArrayList<>();
        last_status_time = new ArrayList<>();
        mute_contact1 = new ArrayList<>();
        unread_msg = new ArrayList<>();
        get_chat_list();

        chat_list.setAdapter(new myview(Chat_list_Activity.this, R.layout.contact_list_view, contact_names));
        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gotochat1 = new Intent(Chat_list_Activity.this, ChatActivity.class);
                gotochat1.putExtra("phone", contact_numbers.get(position));
                gotochat1.putExtra("name", contact_names.get(position));
                startActivity(gotochat1);
            }
        });



        Intent mServiceIntent = new Intent(Chat_list_Activity.this,backgroud_service.class);
       startService(mServiceIntent);
    }


    public boolean check(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("people.chat.mywhatsapp.background_service"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

getMenuInflater().inflate(R.menu.mylongclick,menu);
        super.onCreateContextMenu(menu, v, menuInfo);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String myactivities="";
        switch (item.getItemId()){
            case R.id.all_contacts:myactivities="All_contact_activity";break;
            case R.id.broadcast_menu:myactivities="Broadcast_activity";break;
            case R.id.important_messages_menu:myactivities="Important_msg_activity";break;
            case R.id.status_menu:myactivities="Status_activity";break;
            case R.id.settings_menu:myactivities="Settings_activity";break;
                    }
        Class myclass= null;
        try {
            myclass = Class.forName("people.chat.mywhatsapp."+myactivities);
            Intent myintent=new Intent(Chat_list_Activity.this,myclass);
            startActivity(myintent);

        } catch (ClassNotFoundException e) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo menuinfo1= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position=menuinfo1.position;

        if(item.toString().equals("Mute Chat"))
        {
            mydbhelper obj4=new mydbhelper(this);
            obj4.open();
            obj4.insertdata_contact(contact_numbers.get(position),contact_names.get(position),null,null,null,null,null,null,"true",null);
            obj4.close();
        }
        else if(item.toString().equals("Delete Chat"))
        {

        }
        else if(item.toString().equals("Unmute Chat"))
        {
            mydbhelper obj4=new mydbhelper(this);
            obj4.open();
            obj4.insertdata_contact(contact_numbers.get(position),contact_names.get(position),null,null,null,null,null,null,"false",null);
            obj4.close();
        }
        else if(item.toString().equals("Block"))
        {
        }


        return super.onContextItemSelected(item);
    }

    class myview extends ArrayAdapter
    {

        public myview(Context context, int resource, List objects)
        {
            super(context, resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater myinflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myschat = myinflater.inflate(R.layout.contact_list_view, parent, false);

            ImageView dp_at_list= (ImageView) myschat.findViewById(R.id.dp_list);
            TextView name_user= (TextView) myschat.findViewById(R.id.name_list);
            TextView last_msg= (TextView) myschat.findViewById(R.id.last_msg_list);
            TextView last_msg_time= (TextView) myschat.findViewById(R.id.last_msg_time);
            ImageView mute1= (ImageView) myschat.findViewById(R.id.mymute);
            TextView unread_msg1= (TextView) myschat.findViewById(R.id.unread_msg);
            name_user.setText(contact_names.get(position));
            last_msg.setText(last_msg1.get(position));
            if(unread_msg.get(position).equals("0"))
            {
                unread_msg1.setVisibility(View.INVISIBLE);
            }
            else unread_msg1.setText(unread_msg.get(position));
            try {
                last_msg_time.setText(obj.date_to_time(obj.convertDate(last_time.get(position),"yyyy-MM-dd HH:mm:ss")));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mute_contact1.get(position)!=null||mute_contact1.get(position)=="true")mute1.setVisibility(View.VISIBLE);
            if (mute_contact1.get(position)!=null||mute_contact1.get(position)=="true")mute1.setVisibility(View.VISIBLE);



            return myschat;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        get_chat_list();
        chat_list.setAdapter(new myview(Chat_list_Activity.this,R.layout.contact_list_view,contact_names));


    }

    @Override
    protected void onPause() {
        contact_numbers.clear();
        contact_names.clear();
        last_msg1.clear();
        last_time.clear();
        dpname.clear();
        last_seen.clear();
        last_status_time.clear();
        mute_contact1.clear();
        unread_msg.clear();
        super.onPause();
    }

    void get_chat_list(){

        contact_numbers.clear();
        contact_names.clear();
        last_msg1.clear();
        last_time.clear();
        dpname.clear();
        last_seen.clear();
        last_status_time.clear();
        mute_contact1.clear();
        unread_msg.clear();
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();
        Cursor myresult;
        myresult = obj1.mydb.rawQuery("select * from contact_whatsapp where last_msg IS NOT NULL order by last_time_msg DESC",null);
        while(myresult.moveToNext())
        {
            contact_names.add(myresult.getString(myresult.getColumnIndex("name")));
            contact_numbers.add(myresult.getString(myresult.getColumnIndex("phonenumber")));
            last_msg1.add(myresult.getString(myresult.getColumnIndex("last_msg")));
            last_time.add(myresult.getString(myresult.getColumnIndex("last_time_msg")));
            dpname.add(myresult.getString(myresult.getColumnIndex("dpname")));
            last_seen.add(myresult.getString(myresult.getColumnIndex("last_seen")));
            last_status_time.add(myresult.getString(myresult.getColumnIndex("last_status_time")));
            mute_contact1.add(myresult.getString(myresult.getColumnIndex("mute_status")));
            unread_msg.add(myresult.getString(myresult.getColumnIndex("unread_msg")));
        }
        myresult.close();
        obj1.close1();
    }

    }


