package mk.edu.ukim.feit.gjorgjim.unitechnet.ui.navigation_activity;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;

/**
 * Created by gjmarkov on 09.9.2018.
 */

public class NavigationToolbarDelegate {
  private static final NavigationToolbarDelegate ourInstance = new NavigationToolbarDelegate();

  public static NavigationToolbarDelegate getInstance() {
    return ourInstance;
  }

  private Toolbar toolbar;

  private NavigationToolbarDelegate() {
  }

  public void setToolbar(Toolbar toolbar, int color) {
    this.toolbar = toolbar;
    toolbar.setTitleTextColor(color);
  }

  public void setLogo(Drawable drawable) {
    toolbar.setLogo(drawable);
  }
}
