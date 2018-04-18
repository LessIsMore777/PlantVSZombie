package com.nihuan.plantvszombie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nihuan.plantvszombie.engine.GameEngine;
import com.nihuan.plantvszombie.layer.MenuLayer;
import com.nihuan.plantvszombie.layer.WelcomeLayer;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;

public class MainActivity extends AppCompatActivity {

    private CCDirector director;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建一个SurfaceView,类似导演眼前的小荧幕
        CCGLSurfaceView view = new CCGLSurfaceView(this);
        setContentView(view);

        //获取导演单例
        director = CCDirector.sharedDirector();
        director.attachInView(view);                        //开启绘制线程
       // director.setDisplayFPS(true);                       //显示帧率，每秒刷新界面次数 da==大于30帧人眼看起来比较流畅
        //director.setAnimationInterval(1/60f);               //设置最高帧率60
        director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);//设置横屏显示
        director.setScreenSize(480,320);        //用于屏幕适配，会基于不同大小的屏幕等比例缩放
        CCScene scene = CCScene.node();                     //创建一个场景对象
        WelcomeLayer layer = new WelcomeLayer();
        scene.addChild(layer);                              //给场景添加图层

        director.runWithScene(scene);                       //导演运行场景

    }

    @Override
    protected void onResume() {
        super.onResume();
        director.resume();
        SoundEngine.sharedEngine().resumeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        director.pause();
        SoundEngine.sharedEngine().pauseSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        director.end();
        System.exit(0);//杀死程序，清空所有静态变量
        //GameEngine.isStart = false;
    }
}
