package com.shinezone.movie;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.testin.analysis.bug.BugOutApi;
import layaair.game.browser.ConchJNI;
import layaair.game.browser.ExportJavaFunction;

public class GameApplication
{
    private static GameApplication instance;
    public static GameApplication getInstance() {
        if (instance == null) {
            instance = new GameApplication();
        }
        return instance;
    }

    public GameApplication()
    {
        if(instance != null)
        {
            enableCallJs = true;
            init(instance.activity);
        }
        instance = this;
    }



    public Activity activity;
    public  void  init(Activity activity)
    {
        this.activity = activity;
    }

    public String enterBackgroundTime = "";
    public boolean enableCallJs = false;


    /**
     （3）回调方法：applicationWillResignActive:
     本地通知：UIApplicationWillResignActiveNotification
     触发时机：从活动状态进入非活动状态。
     适宜操作：这个阶段应该保存UI状态（例如游戏状态）。
     */

    public void applicationWillResignActive()
    {
        if(!enableCallJs) return;
        this.enterBackgroundTime = System.currentTimeMillis() + "";

        ConchJNI.RunJS("if(window['gameApplication'] && window['gameApplication'].applicationWillResignActive) {gameApplication.applicationWillResignActive(); }");
    }


    /**
     （2）回调方法：applicationDidBecomeActive：
     本地通知：UIApplicationDidBecomeActiveNotification
     触发时机：程序进入前台并处于活动状态时调用。
     适宜操作：这个阶段应该恢复UI状态（例如游戏状态）。
     */

    public void applicationDidBecomeActive()
    {
        if(!enableCallJs) return;
        String str =String.format("if(window['gameApplication'] && window['gameApplication'].applicationDidBecomeActive)gameApplication.applicationDidBecomeActive('%s')",this.enterBackgroundTime);
        ConchJNI.RunJS(str);
    }



    /**
     （6）回调方法：applicationWillTerminate:
     本地通知：UIApplicationWillTerminateNotification
     触发时机：程序被杀死时调用。
     适宜操作：这个阶段应该进行释放一些资源和保存用户数据。
     */
    public void applicationWillTerminate()
    {
        if(!enableCallJs) return;
        ConchJNI.RunJS("if(window['gameApplication'] && window['gameApplication'].applicationWillTerminate)gameApplication.applicationWillTerminate()");
    }



    /////////////////////////////////////
    // js 获取
    /////////////////////////////////////

    public String getAndroid()
    {
        String androidID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidID;
    }

    public String getIDFA()
    {
        String androidID = getAndroid();
        ExportJavaFunction.CallBackToJS(this,"getIDFA",androidID);
        return androidID;
    }


    public String getIDFV()
    {
        String androidID = getAndroid();
        ExportJavaFunction.CallBackToJS(this,"getIDFV",androidID);
        return androidID;
    }


    public String getBundleIdentifier()
    {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) //得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }


    /////////////////////////////////////
    // 执行
    /////////////////////////////////////

    // 打开网页
    public  void  openURL(String url)
    {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    public  void  checkNetwork()
    {
        ExportJavaFunction.CallBackToJS(this,"checkNetwork","1");
    }

    public  void  exitApp()
    {

    }



    /////////////////////////////////////
    // Testin
    /////////////////////////////////////

    public  void  reportException(String message)
    {
        BugOutApi.reportException(new Exception(message));
    }


    public  void  setUserInfo(String username, String name)
    {
        try {

            //设置登录事件属性

            JSONObject properties=new JSONObject();

            properties.put("userName", username);

            properties.put("name", name);



            BugOutApi.setUserInfo(properties);

        } catch (JSONException e) {

            e.printStackTrace();

        }
    }




}
