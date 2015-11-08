package animation.vrnvikas.onthespot.bubblewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.LinkedList;

/**
 * Created by Vikas Kumar on 07-11-2015.
 */
class BubblesView extends SurfaceView implements SurfaceHolder.Callback {

    private LinkedList<Bubble> bubbles = new LinkedList<Bubble>();
    private SurfaceHolder surfaceHolder;
    private float BUBBLE_FREQUENCY = 0.3f;
    private GameLoop gameLoop;
    private Bitmap bubbleBitmap;
    private float BUBBLE_TOUCH_RADIUS = 30;
    private int BUBBLE_TOUCH_QUANTITY = 7;
    private Paint backgroundPaint = new Paint();

    public BubblesView(Context context) {
        super(context);
        getHolder().addCallback(this);
        backgroundPaint.setColor(Color.rgb(50,120,238));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //bubbleBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.football);
        surfaceHolder = holder;
        surfaceHolder = holder;
        startAnimation();
    }

    public void setFrequency(float bubbleFrequency) {
        BUBBLE_FREQUENCY = bubbleFrequency;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopAnimation();

    }

    private void drawScreen(Canvas c) {
        c.drawRect(0, 0, c.getWidth(), c.getHeight(), backgroundPaint);
        synchronized (bubbles) {
            for (Bubble bubble : bubbles) {
                bubble.draw(c);
            }
        }
    }

    /*
    private void calculateDisplay(Canvas c) {
        randomlyAddBubbles(c.getWidth(), c.getHeight());
        LinkedList<Bubble> bubblesToRemove = new
                LinkedList<Bubble>();
        for (Bubble bubble : bubbles) {
            bubble.move();
            if (bubble.outOfRange())
                bubblesToRemove.add(bubble);
        }
        for (Bubble bubble : bubblesToRemove) {
            bubbles.remove(bubble);
        }
    }
    */

    private void calculateDisplay(Canvas c,float numberOfFrames) {
        randomlyAddBubbles(c.getWidth(), c.getHeight(), numberOfFrames);
        LinkedList<Bubble> bubblesToRemove = new LinkedList<Bubble>();
        synchronized (bubbles) {
            for (Bubble bubble : bubbles) {
                bubble.move(numberOfFrames);
                if (bubble.outOfRange()) bubblesToRemove.add(bubble);
            }
            for (Bubble bubble : bubblesToRemove) {
                bubbles.remove(bubble);
            }
        }
    }


    public void randomlyAddBubbles(int screenWidth, int screenHeight,float numFrames) {
        if (Math.random() > BUBBLE_FREQUENCY*numFrames) return;
        synchronized (bubbles) {
            bubbles.add(
                new Bubble((int) (screenWidth * Math.random()), screenHeight + Bubble.RADIUS,
                        (int) (Bubble.MAX_SPEED * Math.random())));
        }
    }

    public void startAnimation() {
        synchronized (this) {
            if (gameLoop == null) {
                gameLoop = new GameLoop();
                gameLoop.start();
            }
        }
    }

    public void stopAnimation() {
        synchronized (this) {
            boolean retry = true;
            if (gameLoop != null) {
                gameLoop.running = false;
                while (retry) {
                    try {
                        gameLoop.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }
            gameLoop = null;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
            createSomeBubbles(event.getX(),event.getY());
            handled = true;
        }
        return handled;

    }

    private void createSomeBubbles(float x, float y) {

        for (int numBubbles = 0; numBubbles < BUBBLE_TOUCH_QUANTITY; ++numBubbles) {
            synchronized (bubbles) {
                bubbles.add(new Bubble((int) (2 * BUBBLE_TOUCH_RADIUS * Math.random() - BUBBLE_TOUCH_RADIUS + x),
                                (int) (2 * BUBBLE_TOUCH_RADIUS * Math.random() - BUBBLE_TOUCH_RADIUS + y),
                                (int) ((Bubble.MAX_SPEED - 0.1) * Math.random() + 0.1)
                        )
                );
            }
        }
    }


    private class GameLoop extends Thread {

        private long msPerFrame = 1000 / 25;
        public boolean running = true;
        long frameTime = 0;
        //BubblesView view = new BubblesView();

        /*
        public void run() {
            Canvas canvas = null;
            frameTime = System.currentTimeMillis();
            final SurfaceHolder surfaceHolder =
                    BubblesView.this.surfaceHolder;
            while (running) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        calculateDisplay(canvas);
                        drawScreen(canvas);
                    }
                } finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
                waitTillNextFrame();
            }
        }
        */
        public void run() {
            Canvas canvas = null;
            long thisFrameTime;
            long lastFrameTime = System.currentTimeMillis();
            float framesSinceLastFrame = 0;
            final SurfaceHolder surfaceHolder = BubblesView.this.surfaceHolder;
            while (running) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        if (canvas != null) {
                            drawScreen(canvas);
                            calculateDisplay(canvas, framesSinceLastFrame);
                        }
                    }
                } finally {
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
                thisFrameTime = System.currentTimeMillis();
                framesSinceLastFrame = (float) (thisFrameTime - lastFrameTime) / msPerFrame;
                lastFrameTime = thisFrameTime;
            }
        }

        private void waitTillNextFrame() {
            long nextSleep = 0;
            frameTime += msPerFrame;
            nextSleep = frameTime - System.currentTimeMillis();
            if (nextSleep > 0) {
                try {
                    sleep(nextSleep);
                } catch (InterruptedException e) {
                }
            }
        }

    }

}
