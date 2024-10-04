package xyz.hewkawar.qrender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;

public class CopyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String textToCopy = intent.getStringExtra("textToCopy");

        if (textToCopy != null && !textToCopy.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("QR Scan Result", textToCopy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}
