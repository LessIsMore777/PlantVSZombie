package com.nihuan.plantvszombie.layer;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;

import com.nihuan.plantvszombie.R;
import com.nihuan.plantvszombie.domin.ShowPlant;
import com.nihuan.plantvszombie.domin.ShowZombies;
import com.nihuan.plantvszombie.domin.base.Sun;
import com.nihuan.plantvszombie.engine.GameEngine;
import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by NiHuan on 2018/3/30.
 */

public class FightLayer extends BaseLayer {

    private CCTMXTiledMap map;
    private ArrayList<CGPoint> mZombiePoint;
    private CCSprite mSelectBox;  //已选植物框
    private CCSprite mChooseBox;  //待选植物框
    private CopyOnWriteArrayList<ShowPlant> mShowPlants;  //待选植物集合
    private CopyOnWriteArrayList<ShowPlant> mSelectPlants = new CopyOnWriteArrayList<>(); //已选植物集合
    private boolean isMoving = false; //标记选择的植物是否正在移动
    private CCSprite btnStart;
    private ArrayList<ShowZombies> mShowZombies;
    private CCSprite startlabel;
    public static final int TAG_SELECTED_BOX = 1;
    public static final int TAG_TOTAL_MONEY = 2;
    private CCLabel label;

    public FightLayer() {
        loadMap();
        loadZombie();
    }

    /**
     * 加载地图
     */
    private void loadMap() {
        map = CCTMXTiledMap.tiledMap("image/fight/map_day.tmx");
        this.addChild(map);
        mZombiePoint = CommonUtils.loadPoint(map,"zombies");
        moveMap();
    }

    /**
     * 加载僵尸
     */
    private void loadZombie(){
        mShowZombies = new ArrayList<>();
        for (CGPoint point: mZombiePoint) {
            ShowZombies zombie = new ShowZombies();
            zombie.setPosition(point);
            map.addChild(zombie);
            mShowZombies.add(zombie);
        }
    }
    /**
     * 移动地图
     */
    private void moveMap(){
        float offset = winSize.width-map.getContentSize().width; //地图移动偏移量
        CCDelayTime delay = CCDelayTime.action(1);
        CCMoveBy move = CCMoveBy.action(2,ccp(offset,0));
        CCSequence s = CCSequence.actions(delay,move,delay, CCCallFunc.action(this,"showPlantBox"));
        map.runAction(s);
    }
    //反射来调用
    public void showPlantBox(){
        setIsTouchEnabled(true);
        showSelectBox();
        showChooseBox();
    }

    private void showChooseBox() {
        mChooseBox = CCSprite.sprite("image/fight/chose/fight_choose.png");
        mChooseBox.setAnchorPoint(0,0);
        this.addChild(mChooseBox);

        mShowPlants = new CopyOnWriteArrayList<>();
        for(int i=1;i<=9;i++){
            //添加背景植物和展示植物，位置一样
            ShowPlant showPlant = new ShowPlant(i);
            mChooseBox.addChild(showPlant.getBgPlant());
            mChooseBox.addChild(showPlant.getShowPlant());
            mShowPlants.add(showPlant);
        }
        //开始战斗
        btnStart = CCSprite.sprite("image/fight/chose/fight_start.png");
        btnStart.setPosition(mChooseBox.getContentSize().width/2,30);
        mChooseBox.addChild(btnStart);
    }

    private void showSelectBox() {
        mSelectBox = CCSprite.sprite("image/fight/chose/fight_chose.png");
        mSelectBox.setAnchorPoint(0,1);
        mSelectBox.setPosition(0,winSize.height);
        this.addChild(mSelectBox,0, TAG_SELECTED_BOX);
        //显示阳光数文字
        label = CCLabel.labelWithString(String.valueOf(Sun.totalMoney),"hkbd.ttf",15);
        label.setColor(ccc3(0,0,0));
        label.setPosition(33, CCDirector.sharedDirector().winSize().height-62);
        this.addChild(label,1,TAG_TOTAL_MONEY);
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        if(GameEngine.isStart){//判断游戏是否已经开始
            GameEngine.getInstance().handleTouch(event);
            return true;

        }
        CGPoint convertTouchToNodeSpace= convertTouchToNodeSpace(event);
        if(CGRect.containsPoint(mChooseBox.getBoundingBox(),convertTouchToNodeSpace)){//落在植物选择框
            if(CGRect.containsPoint(btnStart.getBoundingBox(),convertTouchToNodeSpace)){
                //开始战斗被点击
                if(mSelectPlants.size()== 5) {
                    gamePrepare();
                }
                return true;
            }
            for (ShowPlant showPlant:mShowPlants) {
                if(CGRect.containsPoint(showPlant.getShowPlant().getBoundingBox(),
                        convertTouchToNodeSpace)){
                    //Log.d("momo","植物被选择");
                    //植物移动到已选框
                    if(mSelectPlants.size()<5 && !isMoving) {
                        isMoving = true;
                        mSelectPlants.add(showPlant);
                        CCMoveTo move = CCMoveTo.action(0.5f, ccp(75 + (mSelectPlants.size() - 1) * 53, winSize.height - 65));
                        CCSequence s = CCSequence.actions(move,CCCallFunc.action(this,"unlock"));
                        showPlant.getShowPlant().runAction(s);
                    }
                    break;
                }
            }
        }else if(CGRect.containsPoint(mSelectBox.getBoundingBox(),convertTouchToNodeSpace)){
            boolean isSelected = false;
            for (ShowPlant showPlant:mSelectPlants) {
                if(CGRect.containsPoint(showPlant.getShowPlant().getBoundingBox(),
                        convertTouchToNodeSpace)){
                    CCMoveTo move = CCMoveTo.action(0.5f,showPlant.getBgPlant().getPosition());
                    showPlant.getShowPlant().runAction(move);
                    mSelectPlants.remove(showPlant);
                    isSelected = true;
                    continue;
                }
                if (isSelected){//有植物被点击移除
                    CCMoveBy move = CCMoveBy.action(0.5f,ccp(-53,0));//向左偏移
                    showPlant.getShowPlant().runAction(move);
                }
            }
        }
        return super.ccTouchesBegan(event);

    }
    /**
     * 开始战斗
     */
    private void gamePrepare() {
        setIsTouchEnabled(false);
        //隐藏植物框
        mChooseBox.removeSelf();
        //地图移回
        moveMapBack();
        //缩放已选框
        mSelectBox.setScale(0.65);
        //重新添加已选植物
        for(ShowPlant plant:mSelectPlants){
            plant.getShowPlant().setScale(0.65f);
            plant.getShowPlant().setPosition(plant.getShowPlant().getPosition().x*0.65f,
                    plant.getShowPlant().getPosition().y+(winSize.height-plant.getShowPlant().getPosition().y)*0.35f);
            this.addChild(plant.getShowPlant());
        }
        //缩放添加阳光文字
        label.setPosition(22,CCDirector.sharedDirector().winSize().height-42);
        label.setScale(0.65f);
    }

    private void moveMapBack() {
        float offset = map.getContentSize().width-winSize.width; //地图移动偏移量
        CCDelayTime delay = CCDelayTime.action(1);
        CCMoveBy move = CCMoveBy.action(2,ccp(offset,0));
        CCSequence s = CCSequence.actions(delay,move,delay,CCCallFunc.action(this,"showLabel"));
        map.runAction(s);
    }
    //文字展示
    public void showLabel(){
        //回收僵尸,节省内存
        for(ShowZombies zombie : mShowZombies){
            zombie.removeSelf();
        }
        mShowZombies.clear();
        //显示准备开始战斗文字
        //帧动画
        startlabel = CCSprite.sprite("image/fight/startready_01.png");
        startlabel.setPosition(winSize.width/2,winSize.height/2);
        this.addChild(startlabel);

        CCAnimate anim = (CCAnimate) CommonUtils.animate("image/fight/startready_%02d.png",3,0.6f,false);
        CCSequence s = CCSequence.actions(anim,CCCallFunc.action(this,"gameBegin"));
        startlabel.runAction(s);
    }
    //游戏开始
    public void gameBegin(){
        startlabel.removeSelf();
        Log.d("momo","开始战斗");
        setIsTouchEnabled(true);
        GameEngine.getInstance().gameStart(map,mSelectPlants);
        SoundEngine.sharedEngine().playSound(CCDirector.theApp, R.raw.day,true);
    }

    public void unlock(){
        isMoving = false;
    }
}
