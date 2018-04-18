package com.nihuan.plantvszombie.domin;

import org.cocos2d.nodes.CCSprite;

/**
 * 选择植物框的植物
 * Created by NiHuan on 2018/3/30.
 */

public class ShowPlant {

    String format = "image/fight/chose/choose_default%02d.png";
    private final CCSprite bgPlant;
    private final CCSprite showPlant;
    private int id;  //表示植物ID

    public ShowPlant(int i){
        //初始化背景图片
        this.id = i;
        bgPlant = CCSprite.sprite(String.format(format,i));
        bgPlant.setAnchorPoint(0,0);
        float x = (i-1)%4 * 54 + 16;
        float y = 175 - (i-1)/4 * 59;
        bgPlant.setPosition(x,y);
        bgPlant.setOpacity(100); //设置半透明
        //初始化展现图片
        showPlant = CCSprite.sprite(String.format(format,i));
        showPlant.setAnchorPoint(0,0);
        showPlant.setPosition(bgPlant.getPosition());
    }

    public CCSprite getBgPlant(){
        return bgPlant;
    }

    public CCSprite getShowPlant() {
        return showPlant;
    }

    public int getId(){
        return id;
    }
}
