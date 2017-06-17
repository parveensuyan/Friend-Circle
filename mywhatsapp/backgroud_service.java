package people.chat.mywhatsapp;

import android.app.IntentService;
import android.content.Intent;

import android.database.Cursor;
import android.media.MediaPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class backgroud_service extends IntentService
{

    MediaPlayer nootification=null;
    String data=null;
    Confirmation_page getmydata=new Confirmation_page();
    public backgroud_service()
    {
        super("backgroud_service");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
    final YourTask yourTask = new YourTask();
        final Thread thread = new Thread(yourTask);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public class YourTask implements Runnable {
        @Override
        public void run() {
            mydbhelper requq = new mydbhelper(backgroud_service.this);
            requq.open();
            String delivery_key="NONE";
            try {
                Cursor resultt;
                resultt = requq.mydb.rawQuery("select delivery_key from delivery_key_table", null);

                    if (resultt.moveToNext()) {
                    delivery_key = resultt.getString(resultt.getColumnIndex("delivery_key"));
                    }
                requq.close1();
                data = URLEncoder.encode("mynumber", "UTF-8") + "="
                        + URLEncoder.encode(getmydata.mynumber, "UTF-8");

                data += "&" + URLEncoder.encode("secret_key", "UTF-8") + "="
                        + URLEncoder.encode(getmydata.secret_code, "UTF-8");
                data += "&" + URLEncoder.encode("delivery_key", "UTF-8") + "="
                        + URLEncoder.encode(delivery_key, "UTF-8");


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String text = "";
            BufferedReader reader = null;
            try {
                URL url = new URL("http://friendscircle.pe.hu/updates.php");
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
            String result=text;

            try {
                JSONObject myjson=new JSONObject(result);
                if(myjson.get("status").toString().equals("OK")) {
                    mydbhelper obj23 = new mydbhelper(backgroud_service.this);
                    obj23.open();
                    obj23.insertdelivery_key(myjson.get("delivery_key").toString());

                    JSONArray messages_arrary=myjson.getJSONArray("messages");
                    int length_message=messages_arrary.length();
                    for(int i=0;i<length_message;i++){
                        JSONObject item = messages_arrary.getJSONObject(i);
                        String time_sent= item.getString("time_sent");
                        obj23.insertdata_chat(getmydata.mynumber,item.getString("message"),item.getString("from"), String.valueOf(System.currentTimeMillis()),"you",time_sent,item.getString("message_id"));

                    }

                    if(length_message>0)
                    {
                        if(nootification!=null&&nootification.isPlaying()){
                            nootification.stop();
                            nootification.reset();
                        }
                        nootification= MediaPlayer.create(backgroud_service.this,R.raw.notification);
                        nootification.start();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(ChatActivity.Broadcasr_recievre.PROCESS_RESPONSE);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        sendBroadcast(broadcastIntent);
                    }

                    obj23.close1();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final Intent mServiceIntent = new Intent(backgroud_service.this,backgroud_service.class);

             startService(mServiceIntent);
        }
    }



}
