package com.nihuan.plantvszombie.domin.base;

import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

/**
 * 普通僵尸对象
 * Created by NiHuan on 2018/3/31.
 */

public class PrimaryZombie extends Zombie {

    private Plant mPlant;//正在被攻击的植物

    public PrimaryZombie(CGPoint startPoint, CGPoint endPoint) {
        super("image/zombies/zombies_1/walk/z_1_01.png");
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        this.setPosition(startPoint);  //设置起点坐标
        move();
    }

    @Override
    public void move() {
        CCMoveTo move = CCMoveTo.action(CGPointUtil.distance(getPosition(),endPoint)/speed,endPoint);

        CCSequence s = CCSequence.actions(move, CCCallFunc.action(this,"destroy")); //走到头销毁
        this.runAction(s);
        baseAction();
    }

    @Override
    public void attack(BaseElement element) {
        if(element instanceof Plant && !isDieding){//判断是否在植物
            mPlant = (Plant)element;
            this.stopAllActions();//停止一切
            //僵尸咬植物
            CCAction animate = CommonUtils.animate("image/zombies/zombies_1/attack/z_1_attack_%02d.png",10,true);
            this.runAction(animate);

            CCScheduler scheduler = CCScheduler.sharedScheduler();
            scheduler.schedule("attackPlant",this,1,false);
        }
    }

    /**
     * 咬
     * @param f
     */
    public void attackPlant(float f){
        if(mPlant!=null && !isDieding){
            mPlant.attacked(attack);//植物掉血
            if(mPlant.getLife()<=0){//植物挂了
                CCScheduler.sharedScheduler().unschedule("attackPlant",this);//停止定时器
                this.stopAllActions();
                move();  //继续前行
                isAttacking = false;  //攻击结束
                //mPlant.removeSelf();
            }
        }else{
            CCScheduler.sharedScheduler().unschedule("attackPlant",this);//停止攻击
        }
    }

    boolean isDieding = false;  //标记僵尸是否死亡
    @Override
    public void attacked(int attack) {
        life -= attack; //僵尸掉血
        if(life <= 0 && !isDieding){
            isDieding = true;
            this.stopAllActions();
            if(!isAttacking()){  //没有攻击的动画
                CCAnimate animate1 = (CCAnimate)CommonUtils.animate("image/zombies/zombies_1/head/z_1_head_%02d.png",6,false);
                CCAnimate animate2 = (CCAnimate)CommonUtils.animate("image/zombies/zombies_1/die/z_1_die_%02d.png",6,false);
                CCSequence sequence = CCSequence.actions(animate1,animate2,CCCallFunc.action(this,"died"));
                this.runAction(sequence);
            }else { //攻击动画
                CCAnimate animate = (CCAnimate)CommonUtils.animate("image/zombies/zombies_1/attack_losthead/z_1_attack_losthead_%02d.png",8,false);
                CCAnimate die = (CCAnimate)CommonUtils.animate("image/zombies/zombies_1/die/z_1_die_%02d.png",6,false);
                CCSequence sequence = CCSequence.actions(animate,die,CCCallFunc.action(this,"died"));
                this.runAction(sequence);
            }
        }
    }

    public void died(){
        destroy();
        isDieding = false;
    }
    @Override
    public boolean isAttacking() {
        return super.isAttacking();
    }

    @Override
    public void setAttacking(boolean isAttacking) {
        super.setAttacking(isAttacking);
    }

    @Override
    public void baseAction() {
        CCAction animate = CommonUtils.animate("image/zombies/zombies_1/walk/z_1_%02d.png",7,.2f,true);
        this.runAction(animate);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
