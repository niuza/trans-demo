package com.niuza.trans;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.niuza.trans.utils.TransferCounter;

public class TransRecordView extends AppCompatActivity {

    private TextView countText;
    private TextView sizeText;
    private Button clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_record_view);

        countText = (TextView) findViewById(R.id.count_text);
        sizeText = (TextView) findViewById(R.id.size_text);
        clearBtn = (Button) findViewById(R.id.clear_button);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("历史记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String cText = "传输" + String.valueOf(TransferCounter.getCount(TransRecordView.this)) + "次";
        String sText = "传输" + String.valueOf(TransferCounter.getSize(TransRecordView.this)) + "kb";
        countText.setText(cText);
        sizeText.setText(sText);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransferCounter.clearData(TransRecordView.this);
                refresh();
            }
        });

        //  Toast.makeText(getApplicationContext(),"显示："+String.valueOf(TransferCounter.getCount(TransRecordView.this)),Toast.LENGTH_LONG).show();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void refresh() {
        finish();
        Intent intent = new Intent();
        intent.setClass(TransRecordView.this, TransRecordView.class);
        startActivity(intent);
    }

}
