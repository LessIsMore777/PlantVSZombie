package com.nihuan.plantvszombie.layer;

import android.util.Log;

import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;

/**
 * Created by NiHuan on 2018/3/30.
 */

public class MenuLayer extends BaseLayer{

    public MenuLayer() {
        CCSprite sprite = CCSprite.sprite("image/menu/main_menu_bg.jpg");
        sprite.setAnchorPoint(0,0);
        this.addChild(sprite);

        CCMenu menu = CCMenu.menu();//初始化CCMenu
        CCSprite normalSprite = CCSprite.sprite("image/menu/start_adventure_default.png");//默认图片
        CCSprite selectSprite = CCSprite.sprite("image/menu/start_adventure_press.png");//选中图片
        CCMenuItemSprite item = CCMenuItemSprite.item(normalSprite,selectSprite,this,"Click");
        menu.addChild(item);
        menu.setScale(0.5f);
        menu.setPosition(winSize.width/2-25,winSize.height/2-110);
        menu.setRotation(4.5f);
        this.addChild(menu);
    }

    /**
     * 菜单按钮点击
     * 必须带object参数，这样才能反射到该方法里
     * @param obj
     */
    public void Click(Object obj){
        Log.d("momo","点击");
        CommonUtils.changeLayer(new FightLayer());
        SoundEngine.sharedEngine().realesAllSounds();//停止
    }
}
