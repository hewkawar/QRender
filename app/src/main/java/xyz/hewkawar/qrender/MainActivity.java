package xyz.hewkawar.qrender;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public TextView debugText;
    public Button copyTextBtn;

    private ActivityResultLauncher<Intent> scanQrResultLauncher;


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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scanQrResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultData ->{
                    if (resultData.getResultCode() == RESULT_OK) {
                        ScanIntentResult result = ScanIntentResult.parseActivityResult(resultData.getResultCode(), resultData.getData());

                        if (result.getContents() == null) {
                            Toast.makeText(this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                        } else {
                            String text = result.getContents();

                            debugText.setText(text);

                            if (text.startsWith("WIFI:")) {
                                Toast.makeText(this, getString(R.string.not_support_wifi_format), Toast.LENGTH_SHORT).show();
                            } else if (isValidURL(text)) {
                                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                                openExternalBrowser(text);
                            } else {
                                Toast.makeText(this, getString(R.string.not_support_code), Toast.LENGTH_SHORT).show();
                            }

                            debugText.setVisibility(View.VISIBLE);
                            copyTextBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

        Intent intent = getIntent();
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        Button openScannerBtn = findViewById(R.id.openScanner);

        copyTextBtn = findViewById(R.id.copyText);
        debugText = findViewById(R.id.debugText);

        if (intent != null && intent.hasExtra("WithScanner")) {
            boolean value = intent.getBooleanExtra("WithScanner", false);
            if (value) {
                scanQrResultLauncher.launch(new ScanContract().createIntent(MainActivity.this, new ScanOptions()));
            }
        }

        openScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQrResultLauncher.launch(new ScanContract().createIntent(MainActivity.this, new ScanOptions()));

            }
        });

        copyTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToCopy = debugText.getText().toString();

                ClipData clip = ClipData.newPlainText("qrText", textToCopy);

                clipboardManager.setPrimaryClip(clip);
            }
        });
    }
}