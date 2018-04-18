package com.nihuan.plantvszombie.layer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

/**
 * 图层基类
 * Created by NiHuan on 2018/3/30.
 */

public class BaseLayer extends CCLayer {
    public CGSize winSize = CCDirector.sharedDirector().winSize();  //屏幕宽高

    public BaseLayer(){
    }
}
