package com.nihuan.plantvszombie.engine;

import com.nihuan.plantvszombie.domin.base.AttackPlant;
import com.nihuan.plantvszombie.domin.base.BaseElement;
import com.nihuan.plantvszombie.domin.base.Bullet;
import com.nihuan.plantvszombie.domin.base.Plant;
import com.nihuan.plantvszombie.domin.base.PrimaryZombie;
import com.nihuan.plantvszombie.domin.base.Zombie;

import org.cocos2d.actions.CCScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 战线
 * Created by NiHuan on 2018/3/31.
 */

public class FightLine {
    private HashMap<Integer,Plant> mPlants = new HashMap<Integer, Plant>();

    private CopyOnWriteArrayList<AttackPlant> mAttackPlants = new CopyOnWriteArrayList<>();//攻击性植物集合

    private CopyOnWriteArrayList<Zombie> mZombies = new CopyOnWriteArrayList<>();

    public FightLine(int i){
        CCScheduler scheduler = CCScheduler.sharedScheduler();
        scheduler.schedule("attackPlant",this,.05f,false);//每0.2s检测攻击
        scheduler.schedule("createBullet",this,.05f,false);//产生子弹
        scheduler.schedule("attackZombie",this,.05f,false);//检测子弹是否可以攻击僵尸
    }

    /**
     * 产生子弹
     * @param f
     */
    public void createBullet(float f){
        if(!mZombies.isEmpty() && !mAttackPlants.isEmpty()){//有僵尸和攻击性植物
            for(AttackPlant plant:mAttackPlants){
                plant.createBullet(); //产生子弹
            }
        }
    }
    /**
     * 僵尸攻击植物
     * @param f
     */
    public void attackPlant(float f){
        int column;
        if(!mPlants.isEmpty() && !mZombies.isEmpty()){
            for (Zombie zombie : mZombies) {
                column = (int) (zombie.getPosition().x / 46 - 1);
                if(mPlants.keySet().contains(column)){//僵尸所在列是否有植物
                    if(!zombie.isAttacking()) {
                        //开始攻击植物
                        zombie.attack(mPlants.get(column));
                        zombie.setAttacking(true);   //标记正在攻击

                    }
                }
            }
        }
    }

    /**
     * 子弹攻击僵尸
     * @param f
     */
    public void attackZombie(float f) {
        if (!mPlants.isEmpty() && !mZombies.isEmpty()) {
            for (Zombie zombie : mZombies) {
                int x = (int) zombie.getPosition().x;
                int left = x-10;
                int right = x+10;
                for(AttackPlant plant:mAttackPlants){
                    List<Bullet> bullets = plant.getBullets();//获取植物子弹
                    for(Bullet bullet:bullets){
                        int bx = (int)bullet.getPosition().x;
                        if(bx>=left && bx<=right){//子弹可攻击范围
                            zombie.attacked(bullet.getAttack());  //僵尸掉血
                            bullet.setVisible(false);
                            bullet.setAttack(0);//设置攻击力为0
                        }
                    }
                }
            }
        }
    }
    /**
     * 添加植物
     * @param plant
     */
    public void addPlant(final Plant plant){
        plant.setDieListener(new BaseElement.DieListener() {
            @Override
            public void die() {
                mPlants.remove(plant.getColumn());//移除植物
                mAttackPlants.remove(plant);
            }
        });
        mPlants.put(plant.getColumn(),plant);

        if(plant instanceof AttackPlant){//判断是否是攻击性植物
            mAttackPlants.add((AttackPlant) plant);
        }
    }

    /**
     * 添加僵尸
     * @param zombie
     */
    public void addZombie(final Zombie zombie){
        //僵尸死亡回调
        zombie.setDieListener(new BaseElement.DieListener() {
            @Override
            public void die() {
                mZombies.remove(zombie);//僵尸死亡从集合移除
            }
        });
        mZombies.add(zombie);
    }
    /**
     * 判断战线上是否有植物，有的话就不能安放
     * @return
     */
    public boolean containPlant(Plant plant){
        return mPlants.keySet().contains(plant.getColumn());
    }
}
