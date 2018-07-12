package mk.edu.ukim.feit.gjorgjim.unitechnet.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import mk.edu.ukim.feit.gjorgjim.unitechnet.R;

/**
 * Created by gjmarkov on 11.7.2018.
 */
public class WaitingDialog{
  private Context context;
  private TextView waitingDialogTv;
  private Dialog dialog;

  public WaitingDialog(Context context) {
    this.context = context;
    dialog = null;
  }

  public void showDialog(String message) {
    if(dialog == null) {
      dialog = new Dialog(context);
      dialog.setContentView(R.layout.waiting_dialog);
      dialog.setCancelable(false);

      waitingDialogTv = dialog.findViewById(R.id.waitingDialogTv);
    }
    waitingDialogTv.setText(message);

    dialog.show();

  }

  public void hideDialog() {
    if (dialog != null) {
      dialog.dismiss();
    }
  }
}
