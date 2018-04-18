package com.nihuan.plantvszombie.domin.base;

import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.base.CCAction;

/**
 * 坚果
 * Created by NiHuan on 2018/3/31.
 */

public class Nut extends DefancePlant {
    public Nut() {
        super("image/plant/nut/p_3_01.png");
        baseAction();
    }

    @Override
    public void baseAction() {
        CCAction animate = CommonUtils.animate("image/plant/nut/p_3_%02d.png",11,true);
        this.runAction(animate);
    }
}
