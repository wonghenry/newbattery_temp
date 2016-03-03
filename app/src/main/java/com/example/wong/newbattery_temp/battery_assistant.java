package com.example.wong.newbattery_temp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class  battery_assistant extends AppCompatActivity {

    public  int BatteryN; //目前電量
    public  int BatteryV; //電池電壓
    public static int  BatteryT; //電池溫度
    public static String BatteryStatus; //電池狀態
    public static String BatteryTemp; //電池使用情況
    public static TextView meng, mstatus, mBatteryV, mBatteryT, mBatteryTemp, mtime,mBatteryT2,mtxtname1,mtxtname2,mtxtname3;
    public static FancyButton mtest,mtest2,mtest3;
    public static Context context;
    public static RadioButton on,off;
    public final String MokoService="com.example.wong.battery_assistant.MokoService";
    public static int IDcode;//判斷service只開啟通知欄
    Cursor cursor;

    public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            NumberFormat nf = NumberFormat.getInstance();

            String action = intent.getAction();
         /*
         如果捕捉到的action是ACTION_BATTERY_CHANGED，就運行onBatteryInfoReceiver()
         */
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                BatteryN = intent.getIntExtra("level", 0); //目前電量
                BatteryV = intent.getIntExtra("voltage", 0); //電池電壓
                BatteryT =intent.getIntExtra("temperature", 0); //電池溫度

                switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN))
                {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        BatteryStatus = "充電中";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        BatteryStatus = "放電中";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        BatteryStatus = "未充電";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        BatteryStatus = "已充滿";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        BatteryStatus = "未知道狀態";
                        break;
                }

                switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN))
                {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        BatteryTemp = "未知錯誤";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        BatteryTemp = "良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        BatteryTemp = "電池沒有電";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        BatteryTemp = "電池電壓過高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        BatteryTemp = "電池過熱";
                        break;
                }
                nf.setMaximumFractionDigits(1);
                meng.setText("" + BatteryN + "%");
                mBatteryV.setText(""+nf.format(BatteryV*0.001)+"V");
                mBatteryT.setText("" + nf.format(BatteryT*0.1)+ "℃");
                mBatteryT2.setText("" + nf.format((BatteryT * 0.1 * 9 / 5) + 32) + "℉");
                mstatus.setText("" + BatteryStatus);
                mBatteryTemp.setText("" + BatteryTemp);
                if(BatteryT>400)
                {
                    mBatteryT.setTextColor(Color.RED);
                    mBatteryT2.setTextColor(Color.RED);
                }
               // CallNotification();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_battery_assistant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("leonchen", "onCreate");
        context = this;
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        meng = (TextView) findViewById(R.id.energy);
        mtime = (TextView) findViewById(R.id.time);
        mstatus = (TextView) findViewById(R.id.status);
        mBatteryV = (TextView) findViewById(R.id.BatteryV);
        mBatteryT = (TextView) findViewById(R.id.BatteryT);
        mBatteryT2 = (TextView) findViewById(R.id.BatteryT2);
        mBatteryTemp = (TextView) findViewById(R.id.BatteryTemp);
        mtest = (FancyButton) findViewById(R.id.btn_facebook_share);
        mtest2 = (FancyButton) findViewById(R.id.btn_facebook_share2);
        mtest2.setOnClickListener(listDeviceInfo2);
        mtest.setOnClickListener(listDeviceInfo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mtest3 = (FancyButton) findViewById(R.id.btn_facebook_share3);
        mtest3.setOnClickListener(listDeviceInfo3);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        final Intent intentService = new Intent(battery_assistant.this, MokoService.class);
        intentService.setAction(MokoService);

        IDcode=1;//一開啟APP先開啟通知欄而已
        startService(intentService);




    }
    @Override
    protected void onStart() {
        super.onStart();

        Log.i("leonchen","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("leonchen", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("leonchen", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("leonchen", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("leonchen", "onRestart");
    }

    public void onDestroy()
    {

        unregisterReceiver(mBatInfoReceiver);
        Log.i("leonchen", "Activity_onDestroy");
        super.onDestroy();
    }

   /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(battery_assistant.this);
        ad.setTitle("過熱提醒");
        ad.setMessage("確定要關閉過熱提醒?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub

            }
        });
        ad.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//顯示對話框
    }*/


    private FancyButton.OnClickListener listDeviceInfo2 = new FancyButton.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("電 池 通 知 欄");
            builder.setIcon(android.R.drawable.ic_dialog_info);
            // 單選選項（注意：不可以與builder.setMessage()同時調用）
            builder.setSingleChoiceItems(R.array.drink2, 0, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    List<String> list2 = Arrays.asList((getResources().getStringArray(R.array.drink)));
                    dialog.dismiss();

                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(1);

                    NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                    final Intent intentService =new Intent(battery_assistant.this,MokoService.class);
                    intentService.setAction(MokoService);

                    switch (which)
                    {
                        case 0:
                            Toast.makeText(battery_assistant.this, "開 啟 ", Toast.LENGTH_SHORT).show();
                            if(IDcode==1)
                            {
                                //已開啟
                                startService(intentService);
                            }
                            else
                            {
                                //傳值=1，只開啟通知欄
                                IDcode=1;
                                startService(intentService);
                            }
                            break;
                        case 1:
                            Toast.makeText(battery_assistant.this, "關 閉 ", Toast.LENGTH_SHORT).show();
                            manager.cancel(1);
                            stopService(intentService);
                            break;
                    }

                }

            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_battery_assistant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();


        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*------------------------提醒方式按鈕---------------------------------------------------------------*/
    public static int numbercode;

    private FancyButton.OnClickListener listDeviceInfo = new FancyButton.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intentBatteryUsage = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
            startActivity(intentBatteryUsage);
        }
    };

    private FancyButton.OnClickListener listDeviceInfo3 = new FancyButton.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            final Intent intentService =new Intent(battery_assistant.this,MokoService.class);
            intentService.setAction(MokoService);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("電 池 過 熱 提 醒");
            builder.setIcon(android.R.drawable.ic_dialog_info);
            // 單選選項（注意：不可以與builder.setMessage()同時調用）
            builder.setSingleChoiceItems(R.array.drink, 0, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    List<String> list = Arrays.asList((getResources().getStringArray(R.array.drink)));
                    dialog.dismiss();

                    switch (which) {
                        case 0:
                            Toast.makeText(battery_assistant.this, "開 啟", Toast.LENGTH_SHORT).show();
                            if (IDcode == 1)
                            {
                                IDcode=2;
                                startService(intentService);
                            }
                            break;
                        case 1:
                            Toast.makeText(battery_assistant.this, "關 閉", Toast.LENGTH_SHORT).show();
                            stopService(intentService);
                            com.example.wong.newbattery_temp.MokoService.countThread.Stop();
                            break;

                    }

                }

            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    };


     /*--------------------------------------過熱提醒設定-------------------------------------------------------------*/
    //給countThread知道要用甚麼模式提醒
    /*public void btnswitch(View V)
    {
        if (mbtnswicth.isChecked())
        {

        }
        else
        {

        }
    }*/

   /* public void close()
    {
        countThread.Stop();
        opennew1();
    }

    public  void opennew1()
    {
        countThread = new CountThread();
        countThread.Start();
    }*/

    /*public class CountThread extends Thread
    {
        int number2 = 60 * Integer.parseInt(medit2.getText().toString());
        int count = number2;
        boolean flag = false;

        public CountThread()
        {
            super();
        }

        public void Start()
        {
            flag = true;
            this.start();
        }

        public void Stop()
        {
            flag = false;
        }

        @Override
        public void run()
        {
            while (flag)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {

                    }
                });
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }*/

}
