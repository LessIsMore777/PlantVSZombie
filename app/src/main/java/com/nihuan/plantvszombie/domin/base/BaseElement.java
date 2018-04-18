
package com.nihuan.plantvszombie.domin.base;

import org.cocos2d.nodes.CCSprite;

/**
 * 对战元素共性
 * 
 * @author Administrator
 */
public abstract class BaseElement extends CCSprite {

    public interface DieListener {
        void die();
    }

    private DieListener dieListener; // 死亡的监听

    public void setDieListener(DieListener dieListener) {
        this.dieListener = dieListener;
    }

    public BaseElement(String filepath) {
        super(filepath);
    }

    /**
     * 原地不动的基本动作
     */
    public abstract void baseAction();

    public void destroy() {
        if (dieListener != null) {
            dieListener.die();
        }
        removeSelf();
    }

}
