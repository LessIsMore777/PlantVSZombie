package com.nihuan.plantvszombie.layer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import com.nihuan.plantvszombie.utils.CommonUtils;
import com.nihuan.plantvszombie.R;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.instant.CCShow;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

public class WelcomeLayer extends BaseLayer {
    private CCSprite logo;
    private CCSprite start;

    public WelcomeLayer() {
        logo = CCSprite.sprite("image/popcap_logo.png");
        logo.setPosition(winSize.width/2,winSize.height/2);
        this.addChild(logo);

        CCHide hide = CCHide.action();
        CCDelayTime delay = CCDelayTime.action(1);
        CCShow show = CCShow.action();
        CCSequence s = CCSequence.actions(hide,delay,show,delay,hide,
                CCCallFunc.action(this,"showWelcome"));
        logo.runAction(s);
	new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(5600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    start.setVisible(true);
                    setIsTouchEnabled(true);
                }catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        }.execute();
	SoundEngine engine = SoundEngine.sharedEngine();
        engine.playSound(CCDirector.theApp, R.raw.start,true);
    }

    public void showWelcome(){
        logo.removeSelf();
        CCSprite welcome = CCSprite.sprite("image/welcome.jpg");
        welcome.setAnchorPoint(0,0);
        this.addChild(welcome);

	CCSprite loading = CCSprite.sprite("image/loading/loading_01.png");
        loading.setPosition(winSize.width/2,30);
        this.addChild(loading);

        start = CCSprite.sprite("image/loading/loading_start.png");
        start.setPosition(winSize.width/2,30);
        start.setVisible(false);
        this.addChild(start);

        CCAction animate = CommonUtils.animate("image/loading/loading_%02d.png",9,.4f,false);
        loading.runAction(animate);
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGPoint convertTouchToNodeSpace = convertTouchToNodeSpace(event);
        if(CGRect.containsPoint(start.getBoundingBox(),convertTouchToNodeSpace)){
            Log.d("momo", "haha");
            CommonUtils.changeLayer(new MenuLayer());
        }
        return super.ccTouchesBegan(event);
    }


}