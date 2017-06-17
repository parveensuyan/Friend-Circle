package people.chat.mywhatsapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TypeStatus extends AppCompatActivity {
EditText edit_stats;
    Button save,cancel;
String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_status);

        save= (Button) findViewById(R.id.save_status);
        cancel= (Button) findViewById(R.id.cancel_status);
        edit_stats= (EditText) findViewById(R.id.edit_status);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                status=edit_stats.getText().toString();
                intent.putExtra("RESULT_STRING", status);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }
}
