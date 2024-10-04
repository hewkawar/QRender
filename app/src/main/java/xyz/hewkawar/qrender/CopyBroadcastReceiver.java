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
        // Get the text to copy from the intent
        String textToCopy = intent.getStringExtra("textToCopy");

        if (textToCopy != null && !textToCopy.isEmpty()) {
            // Copy the text to the clipboard
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("QR Scan Result", textToCopy);
            clipboard.setPrimaryClip(clip);

            // Show a toast notification
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}
