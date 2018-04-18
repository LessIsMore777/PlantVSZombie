package com.nihuan.plantvszombie.domin.base;

import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCAction;

public class SunPlant extends ProductPlant {

    public SunPlant() {
        super("image/plant/sunflower/p_1_01.png");
        life = 100;
        baseAction();
        create();
    }

    /**
     * 产生阳光，每10s
     */
    @Override
    public void create() {
        CCScheduler.sharedScheduler().schedule("create", this, 10, false);
    }

    public void create(float f) {
        new Sun(this.getParent(), Sun.TYPE_BIG, ccp(getPosition().x, getPosition().y + 40), ccp(getPosition().x + 25,
                getPosition().y));
    }

    @Override
    public void baseAction() {
        CCAction animate = CommonUtils.animate("image/plant/sunflower/p_1_%02d.png", 8, true);
        runAction(animate);
    }

}
