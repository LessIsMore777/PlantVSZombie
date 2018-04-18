package com.nihuan.plantvszombie.engine;

import android.util.Log;
import android.view.MotionEvent;

import com.nihuan.plantvszombie.domin.ShowPlant;
import com.nihuan.plantvszombie.domin.base.Nut;
import com.nihuan.plantvszombie.domin.base.PeaPlant;
import com.nihuan.plantvszombie.domin.base.Plant;
import com.nihuan.plantvszombie.domin.base.PrimaryZombie;
import com.nihuan.plantvszombie.domin.base.Sun;
import com.nihuan.plantvszombie.domin.base.SunPlant;
import com.nihuan.plantvszombie.layer.FightLayer;
import com.nihuan.plantvszombie.utils.CommonUtils;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.CCScheduler;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 处理战斗逻辑的类
 * Created by NiHuan on 2018/3/30.
 * 饿汉式 单例模式
 */

public class GameEngine {
    private static GameEngine mInstance = new GameEngine();
    private CCTMXTiledMap map;
    private CopyOnWriteArrayList<ShowPlant> mSelectPlants;
    public  static boolean isStart;//标记游戏是否正式开始
    private ArrayList<CGPoint> mZombiePoints;
    private ShowPlant mShowPlant; //当前被点击的已选植物
    private Plant mPlant;  //当前要安放的植物
    private static ArrayList<FightLine> mFightLines;
    private int progress;
    //初始化5条战线
    static {
        mFightLines = new ArrayList<>();
        for(int i = 0;i<5;i++){
            FightLine line = new FightLine(i);
            mFightLines.add(line);
        }
    }

    private GameEngine(){

    }
    public static GameEngine getInstance(){
        return mInstance;
    }

    /**
     * 游戏开始
     */
    public void gameStart(CCTMXTiledMap map, CopyOnWriteArrayList<ShowPlant> SelectPlants){
        isStart = true;
        this.map = map;
        this.mSelectPlants = SelectPlants;
        mZombiePoints = CommonUtils.loadPoint(map,"road"); //加载僵尸移动路径
        //loadZombie();
        //定时器 每2s执行一次loadZombie
        CCScheduler scheduler = CCScheduler.sharedScheduler();
        scheduler.schedule("loadZombie",this,2,false);

        loadPlantPoints();
        progress();
    }
    private CGPoint[][] mPlantPoints = new CGPoint[5][9];//初始化二维数组
    /**
     * 加载植物坐标点
     */
    private void loadPlantPoints() {
        String format = "tower%02d";
        for(int i=1;i<=5;i++){
            ArrayList<CGPoint> loadPoint = CommonUtils.loadPoint(map,String.format(format,i));
            for(int j=0;j<loadPoint.size();j++){
                mPlantPoints[i-1][j] = loadPoint.get(j);
            }
        }
    }

    /**
     * 加载僵尸
     * 反射必须是public方法
     * float参数必须有，否则CCScheduler无法通过反射调用
     */
    public void loadZombie(float f) {
        //Log.d("momo",f+"");
        Random random = new Random();
        int line = random.nextInt(5);//0,1,2,3,4
        CGPoint startPoint = mZombiePoints.get(line*2);  //起点坐标
        CGPoint endPoint = mZombiePoints.get(line*2+1);  //终点坐标

        PrimaryZombie zombie = new PrimaryZombie(startPoint,endPoint);
        map.addChild(zombie,1);
        mFightLines.get(line).addZombie(zombie);//把僵尸添加到战线中
        progress += 5; //每加载一个僵尸+5
        progressTimer.setPercentage(progress);
    }

    public void handleTouch(MotionEvent event) {
        CGPoint convertTouchToNodeSpace = map.convertTouchToNodeSpace(event);
        CCSprite selectBox = (CCSprite) map.getParent().getChildByTag(FightLayer.TAG_SELECTED_BOX);
        //已选框被点击
        if(CGRect.containsPoint(selectBox.getBoundingBox(),convertTouchToNodeSpace)){
            for(ShowPlant showPlant:mSelectPlants){
                if(CGRect.containsPoint(showPlant.getShowPlant().getBoundingBox(),convertTouchToNodeSpace)){
                    if(mShowPlant!=null){
                        mShowPlant.getShowPlant().setOpacity(255);//将上一个植物设为不透明
                    }
                    mShowPlant = showPlant;
                    showPlant.getShowPlant().setOpacity(100);//半透明

                    switch (mShowPlant.getId()){
                        case 1:
                            mPlant = new PeaPlant();//安放豌豆射手
                            break;
                        case 2:
                            mPlant = new SunPlant(); //向日葵
                            break;
                        case 4:
                            mPlant = new Nut();  //安放坚果
                            break;
                    }
                    break;
                }
            }
        }else {  //落在草坪上
            if(isInGrass(convertTouchToNodeSpace)){
                if(mPlant!=null && mShowPlant!=null) {
                    map.addChild(mPlant);//安放植物
                    mShowPlant.getShowPlant().setOpacity(255);
                    //给战线添加植物
                    mFightLines.get(mPlant.getLine()).addPlant(mPlant);
                    mPlant = null;
                    mShowPlant = null;
                }
            }
        }

        //判断阳光是否被点击
        CopyOnWriteArrayList<Sun> suns = Sun.suns;
        for (Sun sun:suns) {
            if(CGRect.containsPoint(sun.getBoundingBox(),convertTouchToNodeSpace)){
                //阳光被点击
                sun.collectAction();//收集阳光
                break;
            }
        }
    }

    /**
     * 判断是否在草坪格子里
     * @return
     */
    private boolean isInGrass(CGPoint point){
        int column = (int)(point.x/46);
        int line = (int)((CCDirector.sharedDirector().winSize().height-point.y)/54);

        if(column>=1 && column<=9 && line>=1 && line<=5){
            if(mPlant!=null){
                mPlant.setLine(line-1);   //设置行号
                mPlant.setColumn(column-1);//设置列号
                mPlant.setPosition(mPlantPoints[line-1][column-1]);
                if(mFightLines.get(line-1).containPlant(mPlant)){//判断战线是否包含植物
                    return false;
                }
                return true;
            }

        }
        return false;
    }
    //进度条
    CCProgressTimer progressTimer;
    private void progress() {
        progressTimer = CCProgressTimer.progressWithFile("image/fight/progress.png");

        progressTimer.setPosition(CCDirector.sharedDirector().getWinSize().width - 70, 13);
        map.getParent().addChild(progressTimer);
        progressTimer.setScale(0.6f);
        // 0-100
        progressTimer.setPercentage(0);// 每增加一个僵尸需要调整进度，增加5 0-100
        // 设置样式
        progressTimer.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarLR);

        CCSprite sprite = CCSprite.sprite("image/fight/flagmeter.png");
        sprite.setPosition(CCDirector.sharedDirector().getWinSize().width - 70, 13);
        map.getParent().addChild(sprite);
        sprite.setScale(0.6f);
        CCSprite name = CCSprite.sprite("image/fight/FlagMeterLevelProgress.png");
        name.setPosition(CCDirector.sharedDirector().getWinSize().width - 70, 5);
        map.getParent().addChild(name);
        name.setScale(0.6f);
    }
}
