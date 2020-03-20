package xposed.qihao.magicwx;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class weixinEntry implements IXposedHookLoadPackage {
    private static String TAG = "weixin_zp_sailong";
    public static ClassLoader attachloadPackageParam;
    public static Boolean global = false;
    public static String mineWxid = BuildConfig.FLAVOR;
    public Object SQLiteDatabaseObj = null;
    public ClassLoader classLoader;
    public Method rawQuery = null;

    public void insertHook(final ClassLoader classLoader2) {
        XposedBridge.log("insert 开始hook");
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", classLoader2, "insertWithOnConflict", String.class, String.class, ContentValues.class, Integer.TYPE, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                MethodHookParam methodHookParam = param;
//                weixinEntry.super.beforeHookedMethod(param);
                weixinEntry.this.rawQuery = XposedHelpers.findMethodBestMatch(methodHookParam.thisObject.getClass(), "rawQuery", new Class[]{String.class, Object[].class});
                weixinEntry.this.SQLiteDatabaseObj = methodHookParam.thisObject;
                String tab = (String) methodHookParam.args[0];
                XposedBridge.log("[start] \r\n Table Name : " + tab);
                String type = (String) methodHookParam.args[1];
                XposedBridge.log("Type Name : " + type);
                ContentValues contentValues = (ContentValues) methodHookParam.args[2];
                XposedBridge.log("插入的消息: " + contentValues.toString() + "\r\n[end] ");
                if ("fmessage_msginfo".equals(tab) && "rowid".equals(type)) {
                    String xmlInfo = contentValues.get("msgContent").toString();
                    weixinEntry.this.addFriend(classLoader2, xmlInfo.split("fromusername=\"")[1].split("\"")[0], xmlInfo.split("ticket=\"")[1].split("\"")[0], xmlInfo.split("scene=\"")[1].split("\"")[0]);
                }
                if (!"message".equals(tab) || !"msgId".equals(type)) {
                } else {
                    try {
                        XposedBridge.log("message msgId stared!");
                        String contentValuesStr = contentValues.toString();
                        int con_type = Integer.parseInt(contentValuesStr.split("type=")[1].split(" ")[0]);
                        int con_status = Integer.parseInt(contentValuesStr.split("status=")[1].split(" ")[0]);
                        String con_talker = contentValuesStr.split("talker=")[1].split(" ")[0];
                        final String wxid = con_talker;
                        String str = contentValuesStr.split("content=")[1];
                        String[] a = contentValuesStr.split("content=")[1].split("=")[0].split(" ");
                        String con_content = BuildConfig.FLAVOR;
                        int i = 0;
                        while (i < a.length - 1) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(con_content);
                            String tab2 = tab;
                            try {
                                sb.append(a[i]);
                                sb.append(" ");
                                con_content = sb.toString();
                                i++;
                                MethodHookParam methodHookParam2 = param;
                                tab = tab2;
                            } catch (Exception e) {
                                e = e;
                                XposedBridge.log(e);
                                XposedBridge.log("Exception with [message msgId stared!]");
                                return;
                            }
                        }
                        XposedBridge.log("con_content :  new:  " + con_content);
                        boolean z = false;
                        int con_isSend = Integer.parseInt(contentValuesStr.split("isSend=")[1].split(" ")[0]);
                        XposedBridge.log("con_type : " + con_type);
                        XposedBridge.log("con_status : " + con_status);
                        XposedBridge.log("con_content : " + con_content);
                        if (con_type == 10000) {
                            if (con_status != 4) {
                                if (con_status != 6) {
                                    String[] strArr = a;
                                }
                            }
                            if (!(con_content.indexOf("刚刚把你添加到通讯录") == -1 && con_content.indexOf("现在可以开始聊天了") == -1)) {
                                z = true;
                            }
                            if (Boolean.valueOf(z).booleanValue()) {
                                LogUtil.d("到底走这里了没？！");
                                final String finalCon_content = con_content;
                                String[] strArr2 = a;
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            Thread.sleep(500);
                                            XposedBridge.log(">>> 可添加对方到通讯录");
                                            if (finalCon_content.indexOf("刚刚把你添加到通讯录") != -1) {
                                                weixinEntry.this.addFriend(classLoader2, wxid);
                                            }
                                            Thread.sleep(600);
                                            XposedBridge.log(">>> 可发送一个刚刚加好友的消息");
                                            weixinEntry.this.sendText(classLoader2, wxid, "你好，这是第一次加好友之后的消息");
                                            weixinEntry.this.sendImg(wxid, "/sdcard/a.gif");
                                            Thread.sleep(600);
                                            weixinEntry.this.sendVideo(wxid, "/sdcard/a.mp4");
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                            con_content.indexOf("weixin://findfriend/verifycontact");
                        }
                        if (con_type == 1 && con_isSend == 1) {
                            XposedBridge.log(">>> 发送一条普通消息");
                        }
                        if (con_type == 1 && con_isSend == 0) {

                            if (con_talker != null && con_talker.startsWith("gh_")) {

                            } else if ("weixin".equals(con_talker)) {

                            } else {
                                LogUtil.d(">>> " + con_talker + "[发送一条普通消息]:" + con_content);
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("\u60a8\u53d1\u9001\u4e86: ");
                                sb2.append(con_content);
                                XposedBridge.log(sb2.toString());
                                sendText(classLoader2, wxid, "您发送了: " + con_content);
                                Thread.sleep(300);
                            }
                        }
                    } catch (Exception e2) {
//                        e = e2;
                        String str2 = tab;
                        XposedBridge.log(e2);
                        LogUtil.d("Exception with [message msgId stared!]");
                        return;
                    }
                }
                LogUtil.d("weixin_zp_sailong", "insertWithOnConflict-----------------------------------------------------------------------------");
            }
        });
    }

    public static Object getMicroMsgDB(ClassLoader classLoader2) {
        return XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.kernel.g", classLoader2), "MH", new Object[0]), "eqv");
    }

    public String getMainInfo() {
        if (this.rawQuery == null || this.SQLiteDatabaseObj == null) {
            return null;
        }
        if (!mineWxid.equals(BuildConfig.FLAVOR)) {
            return mineWxid;
        }
        try {
            Cursor cr = (Cursor) this.rawQuery.invoke(this.SQLiteDatabaseObj, new Object[]{"select id,value from userinfo where id = 2 or id = 42", null});
            XposedBridge.log(cr.getCount() + " : cr.getCount()");
            if (cr.moveToFirst()) {
                for (int i = 0; i < cr.getCount(); i++) {
                    mineWxid = cr.getString(cr.getColumnIndex("value"));
                    if (!mineWxid.equals(BuildConfig.FLAVOR)) {
                        return cr.getString(cr.getColumnIndex("value"));
                    }
                    cr.moveToNext();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        }
        return BuildConfig.FLAVOR;
    }

    public void getNewFriendList(ClassLoader classLoader2) {
        Cursor cursor = null;
        try {
            cursor = (Cursor) XposedHelpers.callMethod(getMicroMsgDB(classLoader2), "rawQuery", new Object[]{"select * from fmessage_conversation where state = 0 order by lastModifiedTime asc ", null});
            String talker = cursor.getString(cursor.getColumnIndex("talker"));
            String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            long lastModifiedTime = cursor.getLong(cursor.getColumnIndex("lastModifiedTime"));
            String fmsgContent = cursor.getString(cursor.getColumnIndex("fmsgContent"));
            String contentFromuserName = cursor.getString(cursor.getColumnIndex("contentFromUsername"));
            XposedBridge.log("-----------------------");
            XposedBridge.log(talker);
            XposedBridge.log(displayName);
            XposedBridge.log(state + BuildConfig.FLAVOR);
            XposedBridge.log(lastModifiedTime + BuildConfig.FLAVOR);
            XposedBridge.log(fmsgContent + BuildConfig.FLAVOR);
            XposedBridge.log(contentFromuserName + BuildConfig.FLAVOR);
            if (cursor != null) {
                XposedHelpers.callMethod(cursor, "close", new Object[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                XposedHelpers.callMethod(cursor, "close", new Object[0]);
            }
        } catch (Throwable th) {
            if (cursor != null) {
                XposedHelpers.callMethod(cursor, "close", new Object[0]);
            }
            throw th;
        }
    }

    public void addFriend(ClassLoader classLoader2, String wxid, String v2, String scene) {
        Log.e(TAG, "addFriend------>");
        Object m = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mm.pluginsdk.model.m", classLoader2), new Object[]{wxid, v2, Integer.valueOf(Integer.parseInt(scene))});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.model.av", classLoader2), "LZ", new Object[0]), "a", new Object[]{m, 0});
    }

    /* access modifiers changed from: private */
    public void addFriend(ClassLoader classLoader2, String wxid) {
        List<String> list1 = new ArrayList<>();
        list1.add(wxid);
        List<Integer> list2 = new ArrayList<>();
        list2.add(3);
        List<Integer> list3 = new ArrayList<>();
        Object obj_m = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mm.pluginsdk.model.m", classLoader2), new Object[]{1, list1, list2, list3, BuildConfig.FLAVOR, BuildConfig.FLAVOR, null, BuildConfig.FLAVOR, BuildConfig.FLAVOR});
        XposedHelpers.callMethod(obj_m, "oF", new Object[]{BuildConfig.FLAVOR});
        XposedHelpers.callMethod(obj_m, "kW", new Object[]{0});
        XposedHelpers.callMethod(XposedHelpers.getObjectField(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.kernel.g", classLoader2), "MG", new Object[0]), "epW"), "a", new Object[]{obj_m, 0});
    }

    public void sendImg(String targetWxid, String imgPath) {
        if (!new File(imgPath).exists()) {
            XposedBridge.log("sendImg 文件找不到");
            return;
        }
        Class clazz_l = XposedHelpers.findClass("com.tencent.mm.as.l", attachloadPackageParam);
        XposedBridge.log(getMainInfo());
        Object obj_l = XposedHelpers.newInstance(clazz_l, new Object[]{3, getMainInfo(), targetWxid, imgPath, 0, null, 0, BuildConfig.FLAVOR, BuildConfig.FLAVOR, true, 2130838195, 0, Float.valueOf(-1000.0f), Float.valueOf(-1000.0f)});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.model.av", attachloadPackageParam), "LZ", new Object[0]), "a", new Object[]{obj_l, 0});
        String id = String.valueOf(XposedHelpers.getObjectField(obj_l, "fiL"));
        String str = TAG;
        Log.e(str, "msg id = " + id);
    }

    public void sendVideo(String wxid, String path) {
        if (!new File(path).exists()) {
            XposedBridge.log("sendVideo 文件找不到");
            return;
        }
        Class clazz_j = XposedHelpers.findClass("com.tencent.mm.pluginsdk.model.j", attachloadPackageParam);
        Context context = AndroidAppHelper.currentApplication().getApplicationContext();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(path);
        Object obj_j = XposedHelpers.newInstance(clazz_j, new Object[]{context, arrayList, null, wxid, 2, null});
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.sdk.g.d", attachloadPackageParam), "post", new Object[]{obj_j, "ChattingUI_importMultiVideo"});
    }

    public void sendText(ClassLoader classLoader2, String sendId, String sendStr) {
        Object j = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mm.modelmulti.h", classLoader2), new Object[]{sendId, sendStr, 1, 0, null});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.model.av", classLoader2), "LZ", new Object[0]), "a", new Object[]{j, 0});
        String msgId = String.valueOf(XposedHelpers.getObjectField(j, "cfF"));
        String str = TAG;
        Log.e(str, "msg id = " + msgId);
    }

    public static void sendTxt(ClassLoader cl, String sendContent) throws IllegalAccessException, InterruptedException {
        Class axClass = XposedHelpers.findClass("com.tencent.mm.plugin.sns.model.ax", cl);
        Object arjObject = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mm.protocal.protobuf.awv", cl), new Object[0]);
        XposedHelpers.setFloatField(arjObject, "uHw", -1000.0f);
        XposedHelpers.setFloatField(arjObject, "uHx", -1000.0f);
        Object axObject = XposedHelpers.newInstance(axClass, new Object[]{2});
        XposedHelpers.callMethod(axObject, "Bq", new Object[]{0});
        XposedHelpers.callMethod(axObject, "Bp", new Object[]{0});
        XposedHelpers.callMethod(axObject, "Sq", new Object[]{sendContent});
        XposedHelpers.callMethod(axObject, "a", new Object[]{arjObject});
        XposedHelpers.callMethod(axObject, "aq", new Object[]{new LinkedList()});
        XposedHelpers.callMethod(axObject, "Bn", new Object[]{0});
        XposedHelpers.callMethod(axObject, "Bo", new Object[]{0});
        XposedHelpers.callMethod(axObject, "da", new Object[]{new ArrayList()});
        XposedHelpers.callMethod(axObject, "commit", new Object[0]);
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.plugin.report.service.g", cl), "zV", new Object[]{22});
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.plugin.sns.model.af", cl), "cdo", new Object[0]), "cce", new Object[0]);
        String time = String.valueOf(XposedHelpers.getObjectField(XposedHelpers.getObjectField(axObject, "pPz"), "field_createTime"));
        String str = TAG;
        Log.e(str, "field_createTime = " + time);
    }

    public static void sendTxtAndPic(ClassLoader cl, String sendContent, List<String> pics) {
        try {
            Class<?> axClass = XposedHelpers.findClass("com.tencent.mm.plugin.sns.model.ax", cl);
            Class<?> arjClass = XposedHelpers.findClass("com.tencent.mm.protocal.protobuf.awv", cl);
            Class<?> hClass = XposedHelpers.findClass("com.tencent.mm.plugin.sns.data.h", cl);
            Object axObject = XposedHelpers.newInstance(axClass, new Object[]{1});
            XposedHelpers.callMethod(axObject, "Sq", new Object[]{sendContent});
            Object arjObject = XposedHelpers.newInstance(arjClass, new Object[0]);
            XposedHelpers.setFloatField(arjObject, "uHw", -1000.0f);
            XposedHelpers.setFloatField(arjObject, "uHx", -1000.0f);
            XposedHelpers.callMethod(axObject, "a", new Object[]{arjObject});
            XposedHelpers.callMethod(axObject, "aq", new Object[]{new LinkedList()});
            XposedHelpers.callMethod(axObject, "Bq", new Object[]{0});
            XposedHelpers.callMethod(axObject, "Bp", new Object[]{0});
            XposedHelpers.callMethod(axObject, "Bn", new Object[]{0});
            XposedHelpers.callMethod(axObject, "Bo", new Object[]{0});
            XposedHelpers.callMethod(axObject, "da", new Object[]{new ArrayList()});
            XposedHelpers.callMethod(axObject, "setSessionId", new Object[]{BuildConfig.FLAVOR});
            LinkedList<Object> linkedList = new LinkedList<>();
            for (int i = 0; i < pics.size(); i++) {
                linkedList.add(XposedHelpers.newInstance(hClass, new Object[]{pics.get(i), 2}));
            }
            XposedHelpers.callMethod(axObject, "db", new Object[]{linkedList});
            XposedHelpers.callMethod(axObject, "commit", new Object[0]);
            XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.plugin.sns.model.af", cl), "cdo", new Object[0]), "cce", new Object[0]);
        } catch (Exception e) {
        }
    }

    public static void sendTxtAndVideo(ClassLoader cl, String sendContent, String video_path, String video_pic) {
        Class<?> axClass = XposedHelpers.findClass("com.tencent.mm.plugin.sns.model.ax", cl);
        Class<?> arjClass = XposedHelpers.findClass("com.tencent.mm.protocal.protobuf.awv", cl);
        Object axObjcet = XposedHelpers.newInstance(axClass, new Object[]{15});
        XposedHelpers.callMethod(axObjcet, "Sq", new Object[]{sendContent});
        Object arjObject = XposedHelpers.newInstance(arjClass, new Object[0]);
        XposedHelpers.setFloatField(arjObject, "uHw", -1000.0f);
        XposedHelpers.setFloatField(arjObject, "uHx", -1000.0f);
        XposedHelpers.callMethod(axObjcet, "a", new Object[]{arjObject});
        XposedHelpers.callMethod(axObjcet, "aq", new Object[]{new LinkedList()});
        XposedHelpers.callMethod(axObjcet, "Bq", new Object[]{0});
        XposedHelpers.callMethod(axObjcet, "Bp", new Object[]{0});
        XposedHelpers.callMethod(axObjcet, "da", new Object[]{new ArrayList()});
        XposedHelpers.callMethod(axObjcet, "Bn", new Object[]{0});
        XposedHelpers.callMethod(axObjcet, "Bo", new Object[]{0});
        XposedHelpers.callMethod(axObjcet, "s", new Object[]{video_path, video_pic, sendContent, (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.a.g", cl), "v", new Object[]{new File(video_path)})});
        XposedHelpers.callMethod(axObjcet, "commit", new Object[0]);
        XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.plugin.sns.model.af", cl), "cdo", new Object[0]), "cce", new Object[0]);
    }

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("\u52a0\u8f7d\u4e86\u5305\u5305:" + lpparam.packageName);
        if (lpparam.packageName.equals("com.tencent.mm")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", new Object[]{Context.class, new XC_MethodHook() {
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                    XposedBridge.log(param.getClass().toString());
                    weixinEntry.attachloadPackageParam = ((Context) param.args[0]).getClassLoader();
                    weixinEntry.this.insertHook(weixinEntry.attachloadPackageParam);
                }
            }});
        } else if (lpparam.packageName.equals("tv.danmaku.bili")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", new Object[]{Context.class, new XC_MethodHook() {
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                    XposedBridge.log(param.getClass().toString());
                    weixinEntry.attachloadPackageParam = ((Context) param.args[0]).getClassLoader();
                    weixinEntry.this.insertHook(weixinEntry.attachloadPackageParam);
                    BiliHook.init((Context) param.args[0]);
                }
            }});
        }
    }
}
