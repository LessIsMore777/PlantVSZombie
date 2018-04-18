package com.nihuan.plantvszombie.utils;

import com.nihuan.plantvszombie.layer.MenuLayer;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.types.CGPoint;

import java.util.ArrayList;
import java.util.HashMap;

import static org.cocos2d.nodes.CCNode.ccp;

/**
 * 工具类
 * Created by NiHuan on 2018/3/30.
 */

public class CommonUtils {
    /**
     * 动画工具类
     * @param format 路径的format
     * @param num 帧数
     * @param loop 动画是否循环播放
     * @return
     */
    public static CCAction animate(String format, int num, boolean loop) {
        return animate(format, num, .2f, loop);
    }

    public static CCAction animate(String format, int num, float t, boolean loop) {
        ArrayList<CCSpriteFrame> frames = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            CCSpriteFrame frame = CCSprite.sprite(String.format(format, i)).displayedFrame();
            frames.add(frame);
        }

        CCAnimation anim = CCAnimation.animation("loading", t, frames);
        if (loop) {
            CCAnimate animate = CCAnimate.action(anim);
            CCRepeatForever repeat = CCRepeatForever.action(animate);
            return repeat;
        } else {
            CCAnimate animate = CCAnimate.action(anim, false);
            return animate;
        }
    }
    /**
     * 切换图层
     */
    public static void changeLayer(CCLayer ccLayer){
        CCScene scene = CCScene.node();
        scene.addChild(ccLayer);
        //切换效果
        //CCJumpZoomTransition transition = CCJumpZoomTransition.transition(2,scene);
        CCFadeTransition transition = CCFadeTransition.transition(1,scene);  //淡入淡出
        CCDirector.sharedDirector().replaceScene(transition); //切换场景
    }

    public static ArrayList<CGPoint> loadPoint(CCTMXTiledMap map,String groupName){
        ArrayList<CGPoint> points = new ArrayList<>();
        CCTMXObjectGroup objectGroup = map.objectGroupNamed(groupName);
        ArrayList<HashMap<String, String>> objects = objectGroup.objects;
        for (HashMap<String,String> hashMap : objects) {
            Integer x = Integer.parseInt(hashMap.get("x"));
            Integer y = Integer.parseInt(hashMap.get("y"));
            points.add(CCNode.ccp(x,y));
        }
        return points;
    }
}
