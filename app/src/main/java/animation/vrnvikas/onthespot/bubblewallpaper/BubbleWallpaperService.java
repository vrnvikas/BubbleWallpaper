package animation.vrnvikas.onthespot.bubblewallpaper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
/**
 * Created by Vikas Kumar on 07-11-2015.
 */
public class BubbleWallpaperService extends WallpaperService{


    private boolean isInteractive = true;

    public Engine onCreateEngine() {
        return new BubbleWallpaperEngine();
    }

    private class BubbleWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener{
        private BubblesView bubbles;
        private SurfaceHolder surfaceHolder;
        private SharedPreferences preferences;


        public void onCreate(SurfaceHolder surfaceHolder) {
            bubbles = new BubblesView(getApplicationContext());
            this.surfaceHolder = surfaceHolder;
            bubbles.surfaceCreated(surfaceHolder);
            setTouchEventsEnabled(true);
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            readPreferences(preferences);
            preferences.registerOnSharedPreferenceChangeListener(this);
        }


        private void readPreferences(SharedPreferences preferences) {
            if (preferences.contains("isInteractive") && preferences.contains("bubbleFrequency")) {
                isInteractive = preferences.getBoolean("isInteractive", true);
                float bubbleFrequency = Float.parseFloat(preferences.getString("bubbleFrequency", "0.03"));
                bubbles.setFrequency(bubbleFrequency);
            }
        }

        public void onTouchEvent(MotionEvent event) {
            if (isInteractive) {
                bubbles.onTouchEvent(event);
            }
        }


        public void onDestroy() {
            bubbles.surfaceDestroyed(surfaceHolder);
            preferences.unregisterOnSharedPreferenceChangeListener(this);
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

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }
}

