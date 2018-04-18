package com.nihuan.plantvszombie.domin.base;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;

/**
 * Created by NiHuan on 2018/3/31.
 */

public class Pea extends Bullet {
    public Pea() {
        super("image/fight/bullet.png");
        setScale(0.65f);
    }

    @Override
    public void move() {
        float t = (CCDirector.sharedDirector().winSize().width-getPosition().x)/speed;
        CCMoveTo move = CCMoveTo.action(t,ccp(CCDirector.sharedDirector().winSize().width,getPosition().y));
        CCSequence s = CCSequence.actions(move, CCCallFunc.action(this,"destroy"));
        runAction(s);
    }
}
