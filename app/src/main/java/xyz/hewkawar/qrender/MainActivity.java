package xyz.hewkawar.qrender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> scanQrResultLauncher;

    private void createNotificationChannel() {
        CharSequence name = "Result";
        String description = "Scan result";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("RESULT", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void openExternalBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public boolean isValidURL(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createNotificationChannel();

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setOrientationLocked(true);

        scanQrResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultData -> {
                    if (resultData.getResultCode() == RESULT_OK) {
                        ScanIntentResult result = ScanIntentResult.parseActivityResult(resultData.getResultCode(), resultData.getData());

                        if (result.getContents() == null) {
                            Toast.makeText(this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                        } else {
                            String text = result.getContents();

                            if (isValidURL(text)) {
                                openExternalBrowser(text);
                            } else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "RESULT")
                                        .setSmallIcon(R.drawable.scan_qrcode_svgrepo_com)
                                        .setContentTitle("Scan Result")
                                        .setContentText(text)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .addAction(new NotificationCompat.Action.Builder(
                                                R.drawable.copy_ic, "Copy",
                                                PendingIntent.getBroadcast(this, 0,
                                                        new Intent(this, CopyBroadcastReceiver.class)
                                                                .putExtra("textToCopy", text),
                                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE)).build());

                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationManager.notify(1, builder.build());
                            }

                            this.finishAffinity();
                        }
                    }
                });

        scanQrResultLauncher.launch(new ScanContract().createIntent(MainActivity.this, scanOptions));
    }
}