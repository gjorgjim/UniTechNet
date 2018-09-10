package mk.edu.ukim.feit.gjorgjim.unitechnet.cache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;

/**
 * Created by gjmarkov on 10.9.2018.
 */

public class ImagesCacher {
  private static final ImagesCacher ourInstance = new ImagesCacher();

  public static ImagesCacher getInstance() {
    return ourInstance;
  }

  private LruCache mMemoryCache;

  private Drawable toolbarLogo;
  private Drawable toolbarLogoCourse;
  private Drawable toolbarLogoProblem;

  private Resources resources;

  private ImagesCacher() {
    initializeMemoryCache();
  }

  public void setResources(Resources resources) {
    this.resources = resources;
  }

  private void initializeMemoryCache(){
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;

    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
      @Override
      protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getByteCount() / 1024;
      }
    };
  }

  public Drawable getToolbarLogo() {
    if(toolbarLogo == null) {
      toolbarLogo = new BitmapDrawable(resources, getToolbarLogoFromCache("toolbar_logo"));
    }
    return toolbarLogo;
  }

  public Drawable getToolbarLogoCourse() {
    if(toolbarLogoCourse == null) {
      toolbarLogoCourse = new BitmapDrawable(resources, getToolbarLogoFromCache("toolbar_logo_course"));
    }
    return toolbarLogoCourse;
  }

  public Drawable getToolbarLogoProblem() {
    if(toolbarLogoProblem == null) {
      toolbarLogoProblem = new BitmapDrawable(resources, getToolbarLogoFromCache("toolbar_logo_problem"));
    }
    return toolbarLogoProblem;
  }

  private Bitmap getToolbarLogoFromCache(String name) {
    Bitmap logo = getBitmapFromMemCache(name);
    if(logo != null) {
      return logo;
    }

    Bitmap bitmap = null;
    switch (name) {
      case "toolbar_logo":
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.toolbar_logo);
        break;
      case "toolbar_logo_course":
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.toolbar_logo_course);
        break;
      case "toolbar_logo_problem":
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.toolbar_logo_problem);
        break;
    }

    return addBitmapToCache(bitmap, name);
  }


  private Bitmap getBitmapFromMemCache(String name) {
    return (Bitmap) mMemoryCache.get(name);
  }

  private Bitmap addBitmapToCache(Bitmap bitmap, String name) {
    mMemoryCache.put(name, bitmap);
    return bitmap;
  }
}
