package com.shinezone.movie;

import android.content.Context;

import com.appsflyer.*;

import java.util.HashMap;
import java.util.Map;

// Appsflyer SDK Version:  4.8.15 (Release Notes)

public class OcNativeClass {
    public  static Context context;
    public  static void OcNativeClass(){}
    // 注册完成
    public static void onAppsFlyerRegistation(){
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.REGION,"Android");
        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.COMPLETE_REGISTRATION,eventValue);
    }
    // 登录完成
    public static void onAppsFlyerLogin(){
        Map<String, Object> eventValue = new HashMap<String, Object>();
        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.LOGIN,eventValue);
    }
    // 完成新手引导
    public static void onAppsFlyerTutorial(){
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.TUTORIAL_ID,"3");
        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.TUTORIAL_COMPLETION,eventValue);
    }
    // 玩家等级提升
    public static void onAppsFlyerLevelAchieved(int level){
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.LEVEL,level);
        eventValue.put(AFInAppEventParameterName.SCORE,100);
        AppsFlyerLib.getInstance().trackEvent(context, AFInAppEventType.LEVEL_ACHIEVED,eventValue);
    }
    // 玩家充值
    public static void onAppsFlyerPurchase(int appContentID, int costVal){
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put(AFInAppEventParameterName.REVENUE,costVal);
        eventValue.put(AFInAppEventParameterName.CONTENT_TYPE,"category_a");
        eventValue.put(AFInAppEventParameterName.CONTENT_ID,appContentID);
        eventValue.put(AFInAppEventParameterName.CURRENCY,"USD");
        AppsFlyerLib.getInstance().trackEvent(context , AFInAppEventType.PURCHASE , eventValue);

    }
}
