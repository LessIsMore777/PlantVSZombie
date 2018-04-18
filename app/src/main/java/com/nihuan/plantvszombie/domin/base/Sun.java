
package com.nihuan.plantvszombie.domin.base;

import com.nihuan.plantvszombie.layer.FightLayer;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpTo;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 阳光
 */
public class Sun extends Product {

    public static final CopyOnWriteArrayList<Sun> suns = new CopyOnWriteArrayList<Sun>();// 放置生产出来的阳光

    public static final int TYPE_BIG = 1;
    public static final int TYPE_SMALL = 2;

    private CGPoint start;
    private CGPoint end;

    private int speed = 20;

    private static final String resPath = "image/product/sun.png";
    public static int totalMoney = 25;

    private CCNode parent;// 地图对象

    public Sun(CCNode parent, int type, CGPoint start, CGPoint end) {
        super(resPath);
        this.start = start;
        this.end = end;
        this.parent = parent;

        if (type == TYPE_BIG) {
            setScale(0.3);
        }

        setPosition(start);
        parent.getParent().addChild(this);// 应该往图层上添加太阳, 否则会被已选框挡住
        suns.add(this);

        baseAction();
    }

    @Override
    public void destroy() {
        suns.remove(this);
        super.destroy();
    }

    @Override
    public void baseAction() {
        // 弧线动作
        float t = (start.y - end.y) / speed;
        // CCMoveTo moveTo = CCMoveTo.action(t, end);
        CCJumpTo jump = CCJumpTo.action(t, end, 20, 1);
        CCCallFunc callFunc = CCCallFunc.action(this, "destroy");
        CCSequence sequence = CCSequence.actions(jump, CCDelayTime.action(5), callFunc);//5s不收集，销毁
        runAction(sequence);

        // 旋转动作
        runAction(CCRepeatForever.action(CCRotateBy.action(1, 180)));
    }

    public void collectAction() {
        stopAllActions();
        float t = CGPointUtil.distance(CCNode.ccp(0, CCDirector.sharedDirector().winSize().height),
                end) / 200;
        CCMoveTo moveTo = CCMoveTo.action(t,
                CCNode.ccp(10, CCDirector.sharedDirector().winSize().height - 10));
        CCSequence sequence = CCSequence.actions(moveTo,
                CCCallFunc.action(this, "collect"));
        runAction(sequence);
    }

    public void collect() {
        totalMoney += 25;
        CCLabel label = (CCLabel)
                parent.getParent().getChildByTag(FightLayer.TAG_TOTAL_MONEY);
        label.setString(String.valueOf(totalMoney));
        destroy();
    }
}
