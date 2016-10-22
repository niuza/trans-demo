package com.niuza.trans;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by 牛杂辉 on 10.22 022.
 */
public class DevNameDialog extends Dialog {
    //定义接口
    public interface DataBackListener{
        public void getData(String data);
    }
    private EditText editText;
    private Button btnSure;
    private Button btnCancel;
    DataBackListener listener;   //创建监听对象
    public DevNameDialog(Context context, final DataBackListener listener) {
        super(context);
        //用传递过来的监听器来初始化
        this.listener = listener;
        setContentView(R.layout.edit_name_dialog);
        editText = (EditText)findViewById(R.id.name_edit);
        btnSure = (Button)findViewById(R.id.sure);
        btnCancel=(Button)findViewById(R.id.cancel_edit);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                //这里调用接口，将数据传递出去。
                listener.getData(str);
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
