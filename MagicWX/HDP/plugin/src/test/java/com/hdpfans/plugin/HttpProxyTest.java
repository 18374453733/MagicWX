package com.hdpfans.plugin;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import com.hdpfans.plugin.spider.http.Http112;
import com.hdpfans.plugin.spider.http.Http125;
import com.hdpfans.plugin.spider.http.Http154;
import com.hdpfans.plugin.spider.http.Http156;
import com.hdpfans.plugin.spider.http.Http163;
import com.hdpfans.plugin.spider.http.Http167;
import com.hdpfans.plugin.spider.http.Http216;
import com.hdpfans.plugin.spider.http.Http51;
import com.hdpfans.plugin.spider.http.Http801;

@RunWith(RobolectricTestRunner.class)
public class HttpProxyTest {

    @Test
    public void http51Test() {
        String food = "http51://329";
        Http51 http51 = new Http51(RuntimeEnvironment.application);
        Assert.assertNotEquals(http51.silking(food).first, food);
    }

//    @Test
//    public void http105Test() {
//        String food = "http105://800084";
//        Http105 http105 = new Http105(RuntimeEnvironment.application);
//        Assert.assertNotEquals(http105.silking(food).first, food);
//    }

    @Test
    public void http112Test() {
        String food = "http112://c_19rronbp1u";
        Http112 http112 = new Http112(RuntimeEnvironment.application);
        Assert.assertNotEquals(http112.silking(food).first, food);
    }

    @Test
    public void http125Test() {
        String food = "http125://100105100";
        Http125 http125 = new Http125(RuntimeEnvironment.application);
        Assert.assertNotEquals(http125.silking(food).first, food);
    }

    @Test
    public void http154Test() {
        String food = "http154://ZwxzUXr";
        Http154 http154 = new Http154(RuntimeEnvironment.application);
        Assert.assertNotEquals(http154.silking(food).first, food);
    }

    @Test
    public void http164Test() {
        String food = "http156://81276";
        Http156 http156 = new Http156(RuntimeEnvironment.application);
        Assert.assertNotEquals(http156.silking(food).first, food);
    }

    @Test
    public void http163Test() {
        String food = "http163://17961";
        Http163 http163 = new Http163(RuntimeEnvironment.application);
        Assert.assertNotEquals(http163.silking(food).first, food);
    }

    @Test
    public void http167Test() {
        String food = "http167://e9301e073cf94732a380b765c8b9573d";
        Http167 http167 = new Http167(RuntimeEnvironment.application);
        Assert.assertNotEquals(http167.silking(food).first, food);
    }

    @Test
    public void http216Test() {
        String food = "http216://jbtyhd";
        Http216 http216 = new Http216(RuntimeEnvironment.application);
        Assert.assertNotEquals(http216.silking(food).first, food);
    }

    @Test
    public void http801Test() {
        String food = "http801://10454.liveplay.myqcloud.com/live/ahws.m3u8";
        Http801 http801 = new Http801(RuntimeEnvironment.application);
        Assert.assertNotEquals(http801.silking(food).first, food);
    }
}
