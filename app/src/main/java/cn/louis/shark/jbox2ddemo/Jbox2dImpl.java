package cn.louis.shark.jbox2ddemo;

import android.util.Log;
import android.view.View;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

/**
 * Created by LouisShark on 2017/7/22.
 * this is on cn.louis.shark.jbox2ddemo.
 */

public class Jbox2dImpl {

    private World mWorld; //模拟世界
    private float dt = 1f/60f; //模拟世界的频率
    private int mVelocityIterations = 5; //速率迭代器
    private int mPosiontIterations = 20; //迭代次数

    private int mWidth,mHeight;

    private float mDensity = 0.9f;
    private float mRatio = 100f;//坐标映射比例
    private final Random mRandom = new Random();

    public Jbox2dImpl(float density) {
        this.mDensity = density;
    }
    public void setWorldSize(int width, int height) {
        mHeight = height;
        mWidth = width;
    }
    public void startWorld() {
        if (mWorld != null) {
            mWorld.step(dt, mVelocityIterations, mPosiontIterations);
        }
    }
    public void createWorld() {
        if (mWorld == null) {
            mWorld = new World(new Vec2(0, 10.0f));
            updateVerticalBounds();
            updateHorizontalBounds();
        }
    }

    //画水平的左右边界
    private void updateHorizontalBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;   //定义静止的物体

        PolygonShape polygonShape = new PolygonShape();  //定义形状的描述
        float boxWidth = transPositionToBody(mRatio);
        float boxHeight = transPositionToBody(mHeight);
        polygonShape.setAsBox(boxWidth, boxHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = mDensity;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.9f;

        bodyDef.position.set(-boxWidth, 0);
        Body leftBody = mWorld.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);

        bodyDef.position.set(transPositionToBody(mWidth) + boxWidth, 0);
        Body rightBody = mWorld.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
    }
    //画垂直的上下边界
    private void updateVerticalBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;   //定义静止的物体

        PolygonShape polygonShape = new PolygonShape();  //定义形状的描述
        float boxWidth = transPositionToBody(mWidth);
        float boxHeight = transPositionToBody(mRatio);
        polygonShape.setAsBox(boxWidth, boxHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = mDensity;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.9f;

        bodyDef.position.set(0, -boxHeight);
        Body topBody = mWorld.createBody(bodyDef);
        topBody.createFixture(fixtureDef);

        bodyDef.position.set(0, transPositionToBody(mHeight) + boxHeight);
        Body bottomBody = mWorld.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
    }

    public void createBody(View view) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.DYNAMIC);
        bodyDef.position.set(transPositionToBody(view.getX() + view.getWidth() / 2),
                transPositionToBody(view.getY() + view.getHeight()/ 2));

        Shape shape = null;
        Boolean isCircle = (Boolean) view.getTag(R.id.view_circle_tag);
        if (isCircle != null && isCircle) {
            shape = createCircleShape(transPositionToBody(view.getWidth() / 2));
        } else {
            Log.d("Jbox2dImpl", "createBody view tag is not circle");
            return;
        }
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(shape);
        fixtureDef.friction = 0.8f; //摩擦系数
        fixtureDef.density = mDensity; //密度
        fixtureDef.restitution = 0.9f;  //补偿系数

        Body body = mWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        view.setTag(R.id.view_body_tag, body);

        body.setLinearVelocity(new Vec2(mRandom.nextFloat(), mRandom.nextFloat()));
    }

    private Shape createCircleShape(float radius) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        return circleShape;
    }

    //view坐标映射为物理的坐标
    private float transPositionToBody(float viewPosition) {
        return viewPosition / mRatio;
    }
    //物理坐标映射为body的坐标
    private float transBodyToPosition(float bodyPosition) {
        return bodyPosition * mRatio;
    }

    public boolean isBodyView(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        return body != null;
    }
    public float getViewX(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            return transBodyToPosition(body.getPosition().x) - (view.getWidth() / 2);
        }
        return 0;
    }
    public float getViewY(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            return transBodyToPosition(body.getPosition().y) - (view.getHeight() / 2);
        }
        return 0;
    }
    public float getViewRotation(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            float angle = body.getAngle();
            //弧度转角度
            return (angle / 3.1415f * 180f) % 360;
        }
        return 0;
    }
    public void applyLinearImpulse(float x, float y, View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        //让刚体做线性运动
        Vec2 impulse = new Vec2(x, y);
        body.applyLinearImpulse(impulse, body.getPosition(), true);
    }
}
