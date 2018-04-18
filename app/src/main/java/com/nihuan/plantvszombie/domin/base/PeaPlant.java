package com.nihuan.plantvszombie.domin.base;

import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.base.CCAction;

/**
 * 豌豆射手
 * Created by NiHuan on 2018/3/31.
 */

public class PeaPlant extends AttackPlant {
    public PeaPlant() {
        super("image/plant/peas/p_2_01.png");
        baseAction();
    }
    @Override
    public Bullet createBullet() {
        if(bullets.size()<1) {  //只能1个子弹
            final Pea pea = new Pea();
            pea.setPosition(ccp(getPosition().x + 30, getPosition().y + 35)); //设置子弹位置
            pea.move();
            pea.setDieListener(new DieListener() {
                @Override
                public void die() {
                    bullets.remove(pea);//从集合移除子弹
                }
            });
            bullets.add(pea);
            this.getParent().addChild(pea);//显示在地图上
            return pea;
        }
        return null;
    }

    @Override
    public void baseAction() {
        CCAction animate = CommonUtils.animate("image/plant/peas/p_2_%02d.png",8,true);
        this.runAction(animate);
    }


}
