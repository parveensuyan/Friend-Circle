package people.chat.mywhatsapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class All_contact_activity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ArrayList<String> contact_status;
    ListView all_conatact_view;
    ArrayList<String> contact_number;
    ArrayList<String> contact_name;
    ArrayList<String> contact_number_old;
    ArrayList<String> contact_name_old;
    String contact_num;
    String contact_nam;
    String status="Avaiable";
Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        all_conatact_view = (ListView) findViewById(R.id.all_number_list);
        contact_name = new ArrayList<>();
        contact_number=new ArrayList<>();
        contact_name=new ArrayList<>();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getcontact();

       //all_conatact_view.setAdapter(new myview(All_contact_activity.this,android.R.layout.simple_list_item_1,contact_number));

        all_conatact_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gotochat=new Intent(All_contact_activity.this,ChatActivity.class);
                gotochat.putExtra("phone",contact_number.get(position));
                gotochat.putExtra("name",contact_name.get(position));
                startActivity(gotochat);
            }
        });

    }


void getcontact()

{
    final Handler handler=new Handler();
    runnable=new Runnable() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {



                getcontactlist();


                    if (ContextCompat.checkSelfPermission(All_contact_activity.this,
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(All_contact_activity.this,
                                Manifest.permission.READ_CONTACTS)) {
getcontactlist();

                        } else {


                            ActivityCompat.requestPermissions(All_contact_activity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        }

                    }

                }
            });
        }
    };
    new Thread(runnable).start();
}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("chat","request "+requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    getcontactlist();
                    } else {


                 this.finish();   // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    void getcontactlist(){
        String id,name;


        ContentResolver cr=getContentResolver();
        String sortOrder=ContactsContract.Contacts.DISPLAY_NAME +" COLLATE LOCALIZED ASC";
        Cursor cur=cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,sortOrder);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()&&!cur.isClosed())
            {
                id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);

                    // this second loop will retrieve all the contact numbers for a paricular contact id
                    while (pCur.moveToNext()) {
                        // Do something with phone

                        int phNumber = pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String phn = pCur.getString(phNumber);
                        phn=phn.replace("+91","");
                        phn=phn.replace(" ","");
                        phn=phn.replace("-","");
                        phn=phn.replace("(","");
                        phn=phn.replace(")","");
                        if(phn.length()==10 && !contact_number.contains(phn))
                        {
                            contact_number.add(phn);
                            contact_name.add(name);
                        }
                    }
                    pCur.close();

                }

            }

        }
        all_conatact_view.setAdapter(new myview(All_contact_activity.this,android.R.layout.simple_list_item_1,contact_number));

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
            TextView status= (TextView) myschat.findViewById(R.id.last_msg_list);
            ImageView online= (ImageView) myschat.findViewById(R.id.online);
            ImageView mute= (ImageView) myschat.findViewById(R.id.mymute);
            TextView last_msg_time= (TextView) myschat.findViewById(R.id.last_msg_time);
            TextView unread= (TextView) myschat.findViewById(R.id.unread_msg);

            last_msg_time.setVisibility(View.INVISIBLE);
            mute.setVisibility(View.INVISIBLE);
            unread.setVisibility(View.INVISIBLE);





            name_user.setText(contact_name.get(position));
            dp_at_list.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    }
});
           return myschat;
        }
    }
    void getallstatus(){

        contact_status.clear();
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();
        Cursor myresult;
        myresult = obj1.mydb.rawQuery("select mystatus from mydata_whatsapp",null);
        if (myresult.moveToNext())
        {
            contact_status.add(myresult.getString(myresult.getColumnIndex("mystatus")));
        }
        myresult.close();
        obj1.close1();
    }

   /* void update_contactdata()
    {
        mydbhelper obj1=new mydbhelper(this);
        obj1.open();
        obj1.insertdata_contact();
        obj1.close1();

    }*/

}



