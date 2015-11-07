package animation.vrnvikas.onthespot.bubblewallpaper;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
/**
 * Created by Vikas Kumar on 07-11-2015.
 */
public class BubbleWallpaperService extends WallpaperService{

    public Engine onCreateEngine() {
        return new BubbleWallpaperEngine();
    }

    private class BubbleWallpaperEngine extends Engine {
        private BubblesView bubbles;
        private SurfaceHolder surfaceHolder;


        public void onCreate(SurfaceHolder surfaceHolder) {
            bubbles = new BubblesView(getApplicationContext());
            this.surfaceHolder = surfaceHolder;
            bubbles.surfaceCreated(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        public void onTouchEvent(MotionEvent event) {
            bubbles.onTouchEvent(event);
        }


        public void onDestroy() {
            bubbles.surfaceDestroyed(surfaceHolder);
        }

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            this.surfaceHolder = surfaceHolder;
            bubbles.surfaceChanged(surfaceHolder, format,width, height);
        }

        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                bubbles.startAnimation();
            } else {
                bubbles.stopAnimation();
            }
        }
    }
}

