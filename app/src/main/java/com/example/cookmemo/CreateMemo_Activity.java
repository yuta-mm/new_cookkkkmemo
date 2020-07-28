package com.example.cookmemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.UUID;

public class CreateMemo_Activity extends AppCompatActivity {

    // MemoOpenHelperクラスを定義
    MemoOpenHelper helper = null;
    // 新規フラグ
    boolean newFlag = false;
    // id
    String id = "";

    public static final String T
            = "com.example.cookmemo.MESSAGE";


    static final int REQUEST_CODE = 1000;

    String[] dropdownItems = {
            "調味料",
            "砂糖",
            "塩",
            "酢",
            "醤油",
            "味噌"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memo);

        // データベースから値を取得する
        if (helper == null) {
            helper = new MemoOpenHelper(CreateMemo_Activity.this);
        }

        // ListActivityからインテントを取得
        Intent intent = this.getIntent();
        // 値を取得
        id = intent.getStringExtra("id");
        // 画面に表示
        if (id.equals("")) {
            // 新規作成の場合
            newFlag = true;
        } else {
            // 編集の場合 データベースから値を取得して表示
            // データベースを取得する
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                // rawQueryというSELECT専用メソッドを使用してデータを取得する
                Cursor c = db.rawQuery("select body from MEMO_TABLE where uuid = '" + id + "'", null);
                // Cursorの先頭行があるかどうか確認
                boolean next = c.moveToFirst();
                // 取得した全ての行を取得
                while (next) {
                    // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
                    String dispBody = c.getString(0);
                    EditText body = (EditText) findViewById(R.id.body);
                    body.setText(dispBody, TextView.BufferType.NORMAL);
                    next = c.moveToNext();
                }
            } finally {
                // finallyは、tryの中で例外が発生した時でも必ず実行される
                // dbを開いたら確実にclose
                db.close();
            }
        }

        /**
         * タイマーボタン処理
         */
        // idがtimerのボタンを取得
        Button timerButton = (Button) findViewById(R.id.timer);
        // clickイベント追加
        timerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), TimerActivity.class);
                intent.putExtra(T, 0);
                startActivityForResult(intent, REQUEST_CODE);
            }


        });

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getAdapter().getItem(position);
                EditText editText;
                editText = (EditText) findViewById(R.id.body);
                String text1 = editText.getText().toString();
                if (text != "調味料") {
                    TextView textView = (TextView) findViewById(R.id.body);
                    textView.setText(text1 + text + " ");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        /**
         * 小さじボタン処理
         */
        // idがsmallのボタンを取得

        Button smallButton = (Button) findViewById(R.id.small);
        // clickイベント追加
        smallButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText;
                editText = (EditText) findViewById(R.id.body);
                String text1 = editText.getText().toString();
                TextView textView = (TextView) findViewById(R.id.body);
                textView.setText(text1 + "小さじ" + " ");
            }


        });

        /**
         * 大さじボタン処理
         */
        // idがbigのボタンを取得

        Button bigButton = (Button) findViewById(R.id.big);
        // clickイベント追加
        bigButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText;
                editText = (EditText) findViewById(R.id.body);
                String text1 = editText.getText().toString();
                TextView textView = (TextView) findViewById(R.id.body);
                textView.setText(text1 + "大さじ" + " ");
            }


        });


        // 分量SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar1);
        // 初期値
        seekBar.setProgress(0);
        // 最大値
        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    String Q;

                    //ツマミがドラッグされると呼ばれる
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // 68 % のようにフォーマト、
                        // この場合、Locale.USが汎用的に推奨される
                        String str = String.format(Locale.US, "%d 杯", progress);
                        Q = str;
                    }

                    //ツマミがタッチされた時に呼ばれる
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    //ツマミがリリースされた時に呼ばれる
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (Q != null) {
                            EditText editText;
                            editText = (EditText) findViewById(R.id.body);
                            String text1 = editText.getText().toString();
                            TextView textView = (TextView) findViewById(R.id.body);
                            textView.setText(text1 + Q + "\n");
                        }
                    }

                });

        // 火加減SeekBar
        SeekBar seekBar2 = findViewById(R.id.seekBar2);
        // 初期値
        seekBar2.setProgress(0);
        // 最大値
        seekBar2.setMax(100);
        seekBar2.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    String Q;

                    //ツマミがドラッグされると呼ばれる
                    @Override
                    public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                        // 68 % のようにフォーマト、
                        // この場合、Locale.USが汎用的に推奨される
                        String str = String.format(Locale.US, "%d %%", progress);
                        Q = str;
                    }

                    //ツマミがタッチされた時に呼ばれる
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar2) {
                    }

                    //ツマミがリリースされた時に呼ばれる
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar2) {
                        if (Q != null) {
                            EditText editText;
                            editText = (EditText) findViewById(R.id.body);
                            String text1 = editText.getText().toString();
                            TextView textView = (TextView) findViewById(R.id.body);
                            textView.setText(text1 + "火加減" + Q + "\n");
                        }
                    }

                });


        /**
         * 登録ボタン処理
         */
        // idがregisterのボタンを取得
        Button registerButton = (Button) findViewById(R.id.register);
        // clickイベント追加
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 入力内容を取得する
                EditText body = (EditText)findViewById(R.id.body);
                String bodyStr = body.getText().toString();

                // データベースに保存する
                SQLiteDatabase db = helper.getWritableDatabase();
                try {
                    if(newFlag){
                        // 新規作成の場合
                        // 新しくuuidを発行する
                        id = UUID.randomUUID().toString();
                        // INSERT
                        db.execSQL("insert into MEMO_TABLE(uuid, body) VALUES('"+ id +"', '"+ bodyStr +"')");
                    }else{
                        // UPDATE
                        db.execSQL("update MEMO_TABLE set body = '"+ bodyStr +"' where uuid = '"+id+"'");
                    }
                } finally {
                    // finallyは、tryの中で例外が発生した時でも必ず実行される
                    // dbを開いたら確実にclose
                    db.close();
                }
                // 保存後に一覧へ戻る
                Intent intent = new Intent(CreateMemo_Activity.this, ListActivity.class);
                startActivity(intent);
            }
        });


        /**
         * 戻るボタン処理
         */
        // idがbackのボタンを取得
        Button backButton = (Button) findViewById(R.id.back);
        // clickイベント追加
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存せずに一覧へ戻る
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CreateMemo_Activity.super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String res = data.getStringExtra(T);

                    EditText editText;
                    editText = (EditText)findViewById(R.id.body);
                    String text1 = editText.getText().toString();
                    TextView textView = (TextView) findViewById(R.id.body);
                    textView.setText(text1 + res + "\n");
                }
                break;
        }
    }

}
