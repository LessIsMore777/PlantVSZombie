package com.nihuan.plantvszombie.domin;

import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.nodes.CCSprite;

/**
 * 展现僵尸
 * Created by NiHuan on 2018/3/30.
 */

public class ShowZombies extends CCSprite {
    public ShowZombies(){
        super("image/zombies/zombies_1/shake/z_1_01.png");
        setScale(0.5);
        setAnchorPoint(0.5f,0);//两脚间
        CCAction animate = CommonUtils.animate("image/zombies/zombies_1/shake/z_1_%02d.png",2,.2f,true);
        runAction(animate);
    }
}
