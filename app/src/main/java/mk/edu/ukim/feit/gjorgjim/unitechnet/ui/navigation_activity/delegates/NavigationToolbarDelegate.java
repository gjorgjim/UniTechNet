package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity.delegates;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.Toolbar;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;
import mk.edu.ukim.feit.gjorgjim.unitechnet.cache.ImagesCacher;

/**
 * Created by gjmarkov on 09.9.2018.
 */

public class NavigationToolbarDelegate {

  public enum NavigationToolbarLogo {
    MAIN(),
    COURSE(),
    PROBLEM()
  }

  private static final NavigationToolbarDelegate ourInstance = new NavigationToolbarDelegate();

  public static NavigationToolbarDelegate getInstance() {
    return ourInstance;
  }

  private Toolbar toolbar;

  private Resources resources;

  private ImagesCacher imagesCacher;

  private NavigationToolbarDelegate() {
    imagesCacher = ImagesCacher.getInstance();
  }

  public void setToolbar(Toolbar toolbar, Resources resources) {
    this.toolbar = toolbar;
    this.resources = resources;
    imagesCacher.setResources(resources);
  }

  public void setLogo(NavigationToolbarLogo logo) {
    if(logo.equals(NavigationToolbarLogo.COURSE)) {
      toolbar.setLogo(imagesCacher.getToolbarLogoCourse());
    } else if(logo.equals(NavigationToolbarLogo.PROBLEM)) {
      toolbar.setLogo(imagesCacher.getToolbarLogoProblem());
    } else if(logo.equals(NavigationToolbarLogo.MAIN)) {
      toolbar.setLogo(imagesCacher.getToolbarLogo());
    }
  }
}
