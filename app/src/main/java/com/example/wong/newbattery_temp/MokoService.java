package com.example.wong.newbattery_temp;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.NumberFormat;

/**
 * Created by wong on 2015/11/13.
 */

public class MokoService extends Service
{
    public static CountThread countThread;
    public static double catch_usertemp=25;
    public static int  catch_usertime=600;
    public static int count;
    public static int catch_tempcf=1;
    public static int catch_numbercode=0;
  //  public static int catch_text=battery_assistant.test;
    public  Vibrator mVibrator;
    public   int IDcode;
    public static int number;
    public  int BatteryN2; //目前電量
   // public  int BatteryV; //電池電壓
    public  int  BatteryT2; //電池溫度
    public String BatteryStatus2; //電池狀態
  //  public static String BatteryTemp; //電池使用情況
    public int catch_newcode;
    public int packet;
    public int packet2;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(battery_assistant.IDcode==1)//先開啟通知欄
        {
           // Toast.makeText(MokoService.this, "還沒開啟過熱提醒", Toast.LENGTH_SHORT).show();
            BroadcastReceiver mBatInfoReceiver2 = new BroadcastReceiver()
            {
                public void onReceive(Context context, Intent intent)
                {

                    String action = intent.getAction();

                    if (Intent.ACTION_BATTERY_CHANGED.equals(action))
                    {
                        BatteryN2 = intent.getIntExtra("level", 0); //目前電量

                        BatteryT2 =intent.getIntExtra("temperature", 0); //電池溫度

                        switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN))
                        {
                            case BatteryManager.BATTERY_STATUS_CHARGING:
                                BatteryStatus2 = "充電中";
                                break;
                            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                                BatteryStatus2 = "放電中";
                                break;
                            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                                BatteryStatus2 = "未充電";
                                break;
                            case BatteryManager.BATTERY_STATUS_FULL:
                                BatteryStatus2 = "已充滿";
                                break;
                            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                                BatteryStatus2 = "未知道狀態";
                                break;
                        }
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMaximumFractionDigits(1);

                        RemoteViews contentViews = new RemoteViews(getPackageName(), R.layout.test);
                        contentViews.setImageViewResource(R.id.imageNo, R.drawable.battery6464);
                        contentViews.setTextViewText(R.id.titleNo, "電 池 小 助 手");
                        //contentViews.setTextViewText(R.id.textNo1, "電量:" + BatteryStatus2 + "%");
                        contentViews.setTextViewText(R.id.textNo1, "電量:" + BatteryN2 + "%");
                        contentViews.setTextViewText(R.id.textNo2, "溫度:" + nf.format(BatteryT2 * 0.1) + "℃/" + nf.format((BatteryT2 * 0.1 * 9 / 5) + 32) + "℉");

                        if (BatteryT2 > 400)
                        {
                            contentViews.setTextColor(R.id.textNo2, Color.RED);
                        }

                        Intent it = new Intent(MokoService.this, battery_assistant.class);

                        PendingIntent pendingIntent = PendingIntent.getActivity(MokoService.this, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MokoService.this);

                        mBuilder.setSmallIcon(R.drawable.battery2424)
                                .setContentTitle("標題")
                                .setContentText("內容")
                                .setAutoCancel(true);
                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setContent(contentViews);
                        mBuilder.setAutoCancel(true);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // 建立通知物件
                        Notification notification = mBuilder.build();

                        //通知欄常駐
                        notification.flags = Notification.FLAG_ONGOING_EVENT;

                        mNotificationManager.notify(1, notification);
                        catch_newcode=2;
                    }

                }
            };
            registerReceiver(mBatInfoReceiver2, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
        else
        {
            Log.i("leonchen", "Service_onStart");
            mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            MediaPlayer music =MediaPlayer.create(getApplicationContext(),R.raw.test1);
            Toast.makeText(MokoService.this, "Service開啟", Toast.LENGTH_SHORT).show();

            switch (catch_tempcf)
            {
                case 1://1=攝氏
                {
                    Toast.makeText(MokoService.this, "已啟動，輸入為攝氏溫度", Toast.LENGTH_SHORT).show();
                    switch (catch_numbercode)
                    {
                        case 0:
                        {
                            if (BatteryT2*0.1 >=  catch_usertemp)
                            {
                                // Toast.makeText(MokoService.this, "設定為鈴聲提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~鈴聲提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                music.start();
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                //  Toast.makeText(MokoService.this, "設定為鈴聲提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }
                        case 1:
                        {
                            if (BatteryT2*0.1 >=catch_usertemp)
                            {
                                // Toast.makeText(MokoService.this, "設定為震動提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~震動提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                mVibrator.vibrate(1500);
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                //   Toast.makeText(MokoService.this, "設定為震動提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }
                        case 2:
                        {
                            if (BatteryT2*0.1 >= catch_usertemp)
                            {
                                //  Toast.makeText(MokoService.this, "設定為鈴聲和震動提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~鈴聲和震動提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                mVibrator.vibrate(1500);
                                music.start();
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                //  Toast.makeText(MokoService.this, "設定為鈴聲和震動提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }
                        case 3:
                        {
                            if (BatteryT2*0.1 >= catch_usertemp)
                            {
                                // Toast.makeText(MokoService.this, "設定為即時訊息提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~即時訊息提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                // Toast.makeText(MokoService.this, "設定為即時訊息提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }

                    }
                    break;
                }
                case 2://2=華氏
                {
                    Toast.makeText(MokoService.this, "已啟動,輸入為華氏溫度", Toast.LENGTH_SHORT).show();
                    switch (catch_numbercode)
                    {
                        case 0:
                        {
                            if (((BatteryT2 * 0.1 * 9 / 5) + 32) >= ((catch_usertemp * 0.1 * 9 / 5) + 32))
                            {
                                // Toast.makeText(MokoService.this, "設定為鈴聲提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~鈴聲提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                music.start();
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                // Toast.makeText(MokoService.this, "設定為鈴聲提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }
                        case 1:
                        {
                            if (((BatteryT2 * 0.1 * 9 / 5) + 32) >= ((catch_usertemp * 0.1 * 9 / 5) + 32))
                            {
                                //  Toast.makeText(MokoService.this, "設定為震動提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~震動提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                mVibrator.vibrate(1500);
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                //  Toast.makeText(MokoService.this, "設定為震動提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }
                        case 2:
                        {
                            if (((BatteryT2 * 0.1 * 9 / 5) + 32) >= ((catch_usertemp * 0.1 * 9 / 5) + 32))
                            {
                                //  Toast.makeText(MokoService.this, "設定為鈴聲和震動提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~鈴聲和震動提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                mVibrator.vibrate(1500);
                                music.start();
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                // Toast.makeText(MokoService.this, "設定為鈴聲和震動提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }
                        case 3:
                        {
                            if (((BatteryT2 * 0.1 * 9 / 5) + 32) >= ((catch_usertemp * 0.1 * 9 / 5) + 32))
                            {

                                //  Toast.makeText(MokoService.this, "設定為即時訊息提醒模式", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MokoService.this, "第一次電池過熱~即時訊息提醒", Toast.LENGTH_SHORT).show();
                                CallNotification2();
                                countThread = new CountThread();
                                countThread.Start();


                            }
                            else
                            {
                                //  Toast.makeText(MokoService.this, "設定為即時訊息提醒模式", Toast.LENGTH_SHORT).show();
                                countThread = new CountThread();
                                countThread.Start();

                            }
                            break;
                        }

                    }

                    break;
                }
            }
        }
        return  START_STICKY;
    }

    public void CallNotification2()
    {
        RemoteViews contentViews = new RemoteViews(getPackageName(),R.layout.test2);
        contentViews.setImageViewResource(R.id.imageNo, R.drawable.redbattery6464);
        contentViews.setTextViewText(R.id.titleNo, "電 池 小 助 手");
        contentViews.setTextViewText(R.id.textNo1, "注 意 ! 電 池 過 熱 ! ");

        Intent intent = new Intent(MokoService.this,battery_assistant.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(MokoService.this, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MokoService.this);

        mBuilder.setSmallIcon(R.drawable.redbattery3232)
                .setContentTitle("電 池 小 助 手")
                .setContentText("注 意~電 池 過 熱囉~")
                .setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContent(contentViews);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(10, mBuilder.build());

    }


    @Override
    public void onDestroy()
    {
        Log.i("leonchen", "Service_onDestroy");
    }

    public class CountThread extends Thread
    {

        MediaPlayer music =MediaPlayer.create(getApplicationContext(),R.raw.test1);
        boolean flag = false;

        public CountThread()
        {

            super();
        }

        public void Start()
        {
            number=1;
            count = catch_usertime;
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
                if (count > 0)
                {
                    try
                    {
                        Message msg = new Message();
                        msg.what = 1;
                        hanlder.sendMessage(msg);
                        Thread.sleep(1000);
                    }
                    catch(Exception e)
                    {

                    }
                    //msg.obj =Integer.toString(count - 1);;
                    //battery_assistant.hanlder.sendMessage(msg);
                    // count--;
                }
                if(count == 0)
                {
                    count = catch_usertime;
                    if(BatteryT2*0.1 < catch_usertemp)
                    {
                        close();
                    }
                    else
                    {
                       // catch_numbercode=2;
                        switch (catch_numbercode)
                        {
                            case 0:
                                CallNotification2();
                                music.start();
                                break;
                            case 1:
                                CallNotification2();
                                mVibrator.vibrate(1500);
                                break;
                            case 2:
                                CallNotification2();
                                music.start();
                                mVibrator.vibrate(1500);
                                break;
                            case 3:
                                CallNotification2();
                                break;
                        }

                    }
                }
            }

        }
    }
    static Handler hanlder = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    count--;
                  //  battery_assistant.mtime.setText(Integer.toString(count));
                    Log.i("leonchen", "倒數"+count);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void close()
    {
        countThread.Stop();
        opennew1();
    }

    public  void opennew1()
    {
        countThread = new CountThread();
        countThread.Start();
    }

}