package people.chat.mywhatsapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class mydbhelper extends SQLiteOpenHelper
{


 static String DATABASE_NAME="new_bas2e";
    static int DB_VERSION=1;
    Context mycontext;
    SQLiteDatabase mydb;
   public mydbhelper(Context context)
   {
       super(context,DATABASE_NAME,null,DB_VERSION);
       mycontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String q="create table if not exists contact_whatsapp(" +
                "phonenumber integer primary key," +
                "name text," +
                "status text," +
                "last_msg text," +
                "last_time_msg Integer," +
                "dpname text," +
                "last_seen Integer," +
                "last_status_time text," +
                "mute_status text," +
                "unread_msg integer" +
                ")";
        String q111="create table if not exists delivery_key_table(" +
                    "delivery_key text" +
                    ",id_key Integer primary key"+
                ")";
        String qq="create table if not exists chat_whatsapp(" +
                "phonenumber text" +
                ",message text" +
                ",name text," +
                "from_number text" +
                ",mytime_msg text" +
                ",message_by text," +
                "sent text," +
                "message_id text primary key);";
        String qqq="create table if not exists mydata_whatsapp(" +
                "myphonenumber text primary key" +
                ",myname text" +
                ",dob text," +
                "blood_group text" +
                ",mydp_name text" +
                ",mystatus text," +
                "last_active text);";
        String qqqq="create table if not exists status_list_whatsapp(id integer PRIMARY KEY AUTOINCREMENT,mystatus text);";
        db.execSQL(qq);
        db.execSQL(q111);

        db.execSQL(qqq);
        db.execSQL(qqqq);
        db.execSQL(q);
        Toast.makeText(mycontext, "created", Toast.LENGTH_SHORT).show();
    }
    public long insertdata_contact(String phone_number,String name,String status,
                                   String last_msg1,String last_time_msg,String dpname,
                                   String last_seen,String last_status_time,String mute_status,String unread_msg){
        ContentValues myvalues=new ContentValues();
        myvalues.clear();
        if (phone_number!=null)myvalues.put("phonenumber",phone_number);
        if (name!=null)myvalues.put("name",name);
        if(status!=null) myvalues.put("status",status);
        if (last_msg1!=null)myvalues.put("last_msg",last_msg1);
        if (last_time_msg!=null)myvalues.put("last_time_msg",last_time_msg);
        if (dpname!=null)myvalues.put("dpname",dpname);
        if (last_seen!=null)myvalues.put("last_seen",last_seen);
        if (last_status_time!=null)myvalues.put("last_status_time",last_status_time);
        if (mute_status!=null)myvalues.put("mute_status",mute_status);
        if (unread_msg!=null)myvalues.put("unread_msg",unread_msg);

        long a= mydb.insertWithOnConflict("contact_whatsapp",null,myvalues, SQLiteDatabase.CONFLICT_REPLACE);
        return a;
    }
    public long insertdata_mydata(String phone_number,String name,String dob,
                                  String blood_group,String mydp_name,
                                  String mystatus,String last_active)
    {
    ContentValues myvalues1=new ContentValues();
    myvalues1.clear();
    if (phone_number!=null)myvalues1.put("myphonenumber",phone_number);
    if (name!=null)myvalues1.put("myname",name);
    if(dob!=null) myvalues1.put("dob",dob);
    if (blood_group!=null)myvalues1.put("blood_group",blood_group);
    if (mydp_name!=null)myvalues1.put("mydp_name",mydp_name);
    if (mystatus!=null)myvalues1.put("mystatus",mystatus);
    if (last_active!=null)myvalues1.put("last_active",last_active);
    long a= mydb.insertWithOnConflict("mydata_whatsapp",null,myvalues1, SQLiteDatabase.CONFLICT_REPLACE);
    return a;
}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
    public void open() throws SQLException
    {
        mydb=this.getReadableDatabase();
    }
    public void close1(){
        mydb.close();

    }
    public long insertdelivery_key(String delivery_key)
    {
        ContentValues myvalues2=new ContentValues();
        myvalues2.clear();

        myvalues2.put("id_key",1);
         myvalues2.put("delivery_key",delivery_key);
        long a1= mydb.insertWithOnConflict("delivery_key_table",null,myvalues2, SQLiteDatabase.CONFLICT_IGNORE);
        if(a1<0)mydb.update("delivery_key_table",myvalues2, "id_key = 1", null);
       // Log.d("chat","dilvery report "+a1);
        return a1;

    }

    public long insertdata_chat(String phonenumber,String message,String from_number,String time_msg,String msg_by,String sent_time,String message_id)
    {
        ContentValues myvalues2=new ContentValues();
        myvalues2.clear();
        if(phonenumber!=null) myvalues2.put("phonenumber",phonenumber);
        if(message!=null) myvalues2.put("message",message);
        if(from_number!=null)  myvalues2.put("from_number",from_number);
        if(time_msg!=null) myvalues2.put("mytime_msg",time_msg);
        if(msg_by!=null) myvalues2.put("message_by",msg_by);
        if(sent_time!=null)myvalues2.put("sent",sent_time);
        if(message_id!=null) myvalues2.put("message_id",message_id);
        long a= mydb.insertWithOnConflict("chat_whatsapp",null,myvalues2, SQLiteDatabase.CONFLICT_IGNORE);

//Log.d("chat",""+a);
        if(a<0)mydb.update("chat_whatsapp",myvalues2, "message_id =?", new String[]{message_id});
        return a;
    
    }
     public long insertstatus(String status)
{
    ContentValues myvalues=new ContentValues();
    myvalues.clear();
    myvalues.put("mystatus",status);
    return mydb.insert("status_list_whatsapp",null,myvalues);
}
    public long deletedata_chat(String phonenumber)
    {
        return mydb.delete("chat_whatsapp","phonenumber"+phonenumber,null);
    }
}
