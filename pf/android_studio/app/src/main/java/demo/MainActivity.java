package demo;
import java.io.InputStream;
import java.util.Map;

import layaair.autoupdateversion.AutoUpdateAPK;
import layaair.game.IMarket.IPlugin;
import layaair.game.IMarket.IPluginRuntimeProxy;
import layaair.game.Market.GameEngine;
import layaair.game.config.config;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;

// appsflyer
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.layabox.movie.OcNativeClass;

// testin
import cn.testin.analysis.data.TestinDataApi;
import cn.testin.analysis.data.TestinDataConfig;
import android.view.MotionEvent;
import cn.testin.analysis.bug.BugOutApi;


public class MainActivity extends Activity{
    public static final int AR_CHECK_UPDATE = 1;
    private IPlugin mPlugin = null;
    private IPluginRuntimeProxy mProxy = null;
    boolean isLoad=false;
    boolean isExit=false;
    public static SplashDialog mSplashDialog;
    // appsFlyer key
    private static final String AF_DEV_KEY = "Tm6NpyjCqyyJUHXKMDvewJ";
    @Override    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化 Testin
        //设置启动参数
        TestinDataConfig testinDataConfig = new TestinDataConfig()
                .openShake(true)//设置是否打开摇一摇反馈bug功能
                .collectCrash(true)//设置是否收集app崩溃信息
                .collectANR(true)//设置是否收集ANR异常信息
                .collectLogCat(false)//设置是否收集logcat系统日志
                .setScreenshot(true)//设置是否开启崩溃截图功能
                .collectUserSteps(true);//设置是否收集用户操作步骤
        //SDK初始化
        TestinDataApi.init(this, "1a20b61e9cb75fe554ccd436b7cd9d38", testinDataConfig);



        // 初始化appsflyer SDK开始

        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {

            }

            @Override
            public void onInstallConversionFailure(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(getApplication());
        OcNativeClass.context = getApplicationContext();
        // 初始化appsflyer SDK结束



        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JSBridge.mMainActivity = this;
        mSplashDialog = new SplashDialog(this);
        mSplashDialog.showSplash();
        /*
         * 如果不想使用更新流程，可以屏蔽checkApkUpdate函数，直接打开initEngine函数
         */
        checkApkUpdate(this);
        //initEngine();
    }
    public void initEngine()
    {
        mProxy = new RuntimeProxy(this);
        mPlugin = new GameEngine(this);
        mPlugin.game_plugin_set_runtime_proxy(mProxy);
        mPlugin.game_plugin_set_option("localize","false");
        mPlugin.game_plugin_set_option("gameUrl", "https://cdn-mv-release.shinezone.com/resources/android/web/index.html");
        mPlugin.game_plugin_init(3);
        View gameView = mPlugin.game_plugin_get_view();
        this.setContentView(gameView);
        isLoad=true;
    }
    public  boolean isOpenNetwork(Context context)
    {
        if (!config.GetInstance().m_bCheckNetwork)
            return true;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && (connManager.getActiveNetworkInfo().isAvailable() && connManager.getActiveNetworkInfo().isConnected());
    }
    public void settingNetwork(final Context context, final int p_nType)
    {
        AlertDialog.Builder pBuilder = new AlertDialog.Builder(context);
        pBuilder.setTitle("连接失败，请检查网络或与开发商联系").setMessage("是否对网络进行设置?");
        // 退出按钮
        pBuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface p_pDialog, int arg1) {
                Intent intent;
                try {
                    String sdkVersion = android.os.Build.VERSION.SDK;
                    if (Integer.valueOf(sdkVersion) > 10) {
                        intent = new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    } else {
                        intent = new Intent();
                        ComponentName comp = new ComponentName(
                                "com.android.settings",
                                "com.android.settings.WirelessSettings");
                        intent.setComponent(comp);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    ((Activity)context).startActivityForResult(intent, p_nType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        pBuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ((Activity)context).finish();
            }
        });
        AlertDialog alertdlg = pBuilder.create();
        alertdlg.setCanceledOnTouchOutside(false);
        alertdlg.show();
    }
    public  void checkApkUpdate( Context context,final ValueCallback<Integer> callback)
    {
        if (isOpenNetwork(context)) {
            // 自动版本更新
            if ( "0".equals(config.GetInstance().getProperty("IsHandleUpdateAPK","0")) == false ) {
                Log.e("0", "==============Java流程 checkApkUpdate");
                new AutoUpdateAPK(context, new ValueCallback<Integer>() {
                    @Override
                    public void onReceiveValue(Integer integer) {
                        Log.e("",">>>>>>>>>>>>>>>>>>");
                        callback.onReceiveValue(integer);
                    }
                });
            } else {
                Log.e("0", "==============Java流程 checkApkUpdate 不许要自己管理update");
                callback.onReceiveValue(1);
            }
        } else {
            settingNetwork(context,AR_CHECK_UPDATE);
        }
    }
    public void checkApkUpdate(Context context) {
        InputStream inputStream = getClass().getResourceAsStream("/assets/config.ini");
        config.GetInstance().init(inputStream);
        checkApkUpdate(context,new ValueCallback<Integer>() {
            @Override
            public void onReceiveValue(Integer integer) {
                if (integer.intValue() == 1) {
                    initEngine();
                } else {
                    finish();
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == AR_CHECK_UPDATE) {
            checkApkUpdate(this);
        }
    }
    protected void onPause()
    {
        super.onPause();
        if(isLoad)mPlugin.game_plugin_onPause();

        //注：回调 2
        BugOutApi.onPause(this);
    }
    //------------------------------------------------------------------------------
    protected void onResume()
    {
        super.onResume();
        if(isLoad)mPlugin.game_plugin_onResume();

        //注：回调 1
        BugOutApi.onResume(this);
        
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //注：回调 3
        BugOutApi.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }
    
    protected void onDestroy()
    {
        super.onDestroy();
        if(isLoad)mPlugin.game_plugin_onDestory();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility =View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getWindow().setAttributes(params);
    }
}
