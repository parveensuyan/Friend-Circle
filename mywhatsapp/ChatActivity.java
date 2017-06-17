package people.chat.mywhatsapp;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
public class ChatActivity extends AppCompatActivity {
    ListView chatview;
    ArrayAdapter list_chat1;
    EditText msg_to_be_sent;
    Button send_btn;
    ArrayList<String> messages_by;
    ArrayList<String> messages;
    ArrayList<String> messages_time;
    String get_phone=null,get_name=null;
    String phone_now,name_now;
    String message1,time1,from1;
    TextView Name1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
if(getSupportActionBar()!=null){
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
}
        Button back_btn= (Button) toolbar.findViewById(R.id.back_btn);
Name1= (TextView) toolbar.findViewById(R.id.contact_name);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
           }
        });
getWindow().setBackgroundDrawableResource(R.drawable.background);
        messages=new ArrayList<>();
        messages_by=new ArrayList<>();
        messages_time=new ArrayList<>();
        get_phone=getIntent().getExtras().getString("phone");
        get_name=getIntent().getExtras().getString("name");
        Name1.setText(get_name);

        chatview= (ListView) findViewById(R.id.chatview);
        msg_to_be_sent= (EditText) findViewById(R.id.msg_send_textview);
        send_btn= (Button) findViewById(R.id.send_btn);
        fetchdetails_chat_list(get_phone,get_name);
        list_chat1=new myview(ChatActivity.this, R.layout.chat_layout_send, messages);
        chatview.setAdapter(list_chat1);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message_check=msg_to_be_sent.getText().toString();
                message_check=message_check.replace(" ","");
                if(message_check.equals(""))return;
                String time1= String.valueOf(System.currentTimeMillis());
                mydbhelper obj2=new mydbhelper(ChatActivity.this);
                obj2.open();
                String mymsg=  msg_to_be_sent.getText().toString();
                Confirmation_page m1=new Confirmation_page();
                String mynumber1=m1.mynumber;
                Random rand= new Random();
                String messge_id=mynumber1+time1+rand.nextInt(900);
                obj2.insertdata_chat(mynumber1,mymsg,get_phone+"",time1,"me",null,messge_id);
                obj2.close1();
                msg_to_be_sent.setText("");
                fetchdetails_chat_list(get_phone,get_name);
                String[] toserver={m1.secret_code,mynumber1,get_phone,mymsg,messge_id};
                new sendmydata().execute(toserver);

            }
        });
        setNotification_for_msg("stop");
        IntentFilter filter = new IntentFilter(receiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new Broadcasr_recievre();
        registerReceiver(receiver, filter);

    }

    private Broadcasr_recievre receiver;

 public class Broadcasr_recievre extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "people.chat.mywhatsapp.intent.action.PROCESS_RESPONSE";
       @Override
        public void onReceive(Context context, Intent intent) {
            setNotification_for_msg("start");
           fetchdetails_chat_list(get_phone,get_name);
       }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public class sendmydata extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String data = null;
            try {
                data = URLEncoder.encode("secret_key", "UTF-8") + "="
                        + URLEncoder.encode(params[0], "UTF-8");
                data += "&" + URLEncoder.encode("mynumber", "UTF-8") + "="
                        + URLEncoder.encode(params[1], "UTF-8");
                data += "&" + URLEncoder.encode("tonumber", "UTF-8") + "="
                        + URLEncoder.encode(params[2], "UTF-8");
                data += "&" + URLEncoder.encode("message", "UTF-8")
                        + "=" + URLEncoder.encode(params[3], "UTF-8");
                data += "&" + URLEncoder.encode("message_id", "UTF-8")
                        + "=" + URLEncoder.encode(params[4], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String text = "";
            BufferedReader reader = null;
            try {
                URL url = new URL("http://friendscircle.pe.hu/sendmessage.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                conn.setReadTimeout(15000);
                conn.connect();
                wr.flush();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                text = sb.toString();
            } catch (Exception ae) {
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception ex) {
                }
            }
            return text;
        }


        protected void onPostExecute(String result) {
           try {
                JSONObject myjson=new JSONObject(result);
                if(myjson.get("status").toString().equals("OK")) {
              mydbhelper obj23 = new mydbhelper(ChatActivity.this);
              obj23.open();
              obj23.insertdata_chat(null, null, null, null, null,myjson.get("time").toString(),myjson.get("message_id").toString());
    obj23.close1();
              fetchdetails_chat_list(get_phone,get_name);
          }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    class myview extends ArrayAdapter<String>
    {
        public myview(Context context, int resource,ArrayList<String> objects) {
            super(context, resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater myinflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mysend = myinflater.inflate(R.layout.chat_layout_send, parent, false);
            View myrecieve = myinflater.inflate(R.layout.chat_layout_recieve, parent, false);

           if (messages_by.get(position).equals("me"))
                {
                TextView send_msg= (TextView) mysend.findViewById(R.id.send_text);
                send_msg.setText(messages.get(position));
                TextView send_time= (TextView) mysend.findViewById(R.id.time_send);
            try
            {       if(messages_time.get(position)!=null)
                    send_time.setText(date_to_time(convertDate(messages_time.get(position),"yyyy-MM-dd HH:mm:ss")));
            } catch (ParseException e)
            {
                    e.printStackTrace();
            }
                return mysend;
            }
            else if (messages_by.get(position).equals("you"))
            {
                 TextView recieve_msg= (TextView) myrecieve.findViewById(R.id.recieve_text);
                recieve_msg.setText(messages.get(position));
                TextView recieve_time= (TextView)myrecieve.findViewById(R.id.time_recieve);
                try
                {     if(messages_time.get(position)!=null)
                    recieve_time.setText(date_to_time(convertDate(messages_time.get(position),"yyyy-MM-dd HH:mm:ss")));
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }


                return myrecieve;
            }
        return null;
        }
    }
    String time12;
   public void fetchdetails_chat_list(String phone_now1,String name_now1)
    {
        phone_now=phone_now1;
        name_now=name_now1;
        messages_by.clear();
        messages.clear();
        messages_time.clear();
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();
        obj1.insertdata_contact(phone_now,name_now,null,null,null,null,null,null,null,"0");
        Cursor myresult;
        myresult = obj1.mydb.rawQuery("select * from (select * from chat_whatsapp where from_number= ? order by mytime_msg DESC LIMIT 0, 20) order by mytime_msg ASC",new String[]{phone_now});
        while(myresult.moveToNext())
        {
            message1=myresult.getString(myresult.getColumnIndex("message"));
            time1=myresult.getString(myresult.getColumnIndex("sent"));
            time12=myresult.getString(myresult.getColumnIndex("mytime_msg"));
            from1=myresult.getString(myresult.getColumnIndex("message_by"));
            messages_by.add(from1);
            messages.add(message1);
            messages_time.add(time1);
        }
        myresult.close();
        obj1.close1();


        list_chat1=new myview(ChatActivity.this, R.layout.chat_layout_send, messages);
        this.list_chat1.notifyDataSetChanged();
        chatview.setAdapter(list_chat1);

        update_chat_list(message1,time12);

    }
   public void update_chat_list(String message_last, String last_msg_time)

    {
        mydbhelper obj3=new mydbhelper(this);
        obj3.open();
        obj3.insertdata_contact(phone_now,name_now,null,message_last,last_msg_time,null,null,null,null,"0");
        obj3.close1();
    }
    String date_to_time(String startTime) throws ParseException {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = dateFormatter.parse(startTime);
   SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
    String displayValue = timeFormatter.format(date);
return displayValue;
}
    void setNotification_for_msg(String start)
    {
        NotificationCompat.Builder mbuilder= (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.new_message)
                .setContentTitle("You have got new messages")
                .setContentText("tap to view the messages");

        Intent resultIntent=new Intent(this,Chat_list_Activity.class);
        TaskStackBuilder stackBuilder= TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Chat_list_Activity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(
                0,PendingIntent.FLAG_UPDATE_CURRENT

        );
        mbuilder.setContentIntent(resultPendingIntent);
        NotificationManager mnotification= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(start.equals("start"))
            mnotification.notify(0,mbuilder.build());
        else mnotification.cancelAll();


    }

}
