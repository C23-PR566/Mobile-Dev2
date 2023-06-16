import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugins.camera.CameraPlugin;

public class MainActivity extends FlutterActivity {
    private static final String MODEL = "model";
    private boolean isLetterLDetected = false;
    private boolean isLetterEDetected = false;
    private boolean isLetterL2Detected = false;
    private boolean isLetterE2Detected = false;
    private int isLetterLDetectedClear = 0;
    private int isLetterEDetectedClear = 0;
    private int isLetterL2DetectedClear = 0;
    private int isLetterE2DetectedClear = 0;
    private boolean isAlertShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GeneratedPluginRegistrant.registerWith(this);

        FlutterEngine flutterEngine = new FlutterEngine(this);
        flutterEngine.getPlugins().add(new CameraPlugin());
        flutterEngine.getNavigationChannel().setInitialRoute("/");
        FlutterActivity.configureFlutterEngine(this, flutterEngine);

        MethodChannel imageSizeChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "camera/image_size");
        imageSizeChannel.setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("get")) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("width", getScreenWidth());
                        params.put("height", getScreenHeight());
                        result.success(params);
                    }
                }
        );

        MethodChannel detectChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "object_detection/detect");
        detectChannel.setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("setRecognitions")) {
                        setRecognitions(call.arguments);
                        result.success(null);
                    }
                }
        );

        MethodChannel finishChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "object_detection/finish");
        finishChannel.setMethodCallHandler(
                (call, result) -> {
                    if (call.method.equals("show")) {
                        showFinishedAlert();
                        result.success(null);
                    }
                }
        );
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    private void setRecognitions(Object arguments) {
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) arguments;
        List<Map<String, Object>> recognitions = (List<Map<String, Object>>) params.get("recognitions");
        int imageHeight = (int) params.get("imageHeight");
        int imageWidth = (int) params.get("imageWidth");

        isLetterLDetected = false;
        isLetterEDetected = false;
        isLetterL2Detected = false;
        isLetterE2Detected = false;

        if (recognitions != null) {
            for (Map<String, Object> recognition : recognitions) {
                String label = (String) recognition.get("label");
                double confidence = (double) recognition.get("confidence");

                if (label.equals("L") && confidence >= 0.8) {
                    isLetterLDetected = true;
                    isLetterLDetectedClear++;
                    if (isLetterLDetectedClear > 0) {
                        isLetterLDetected = true;
                    }
                } else if (label.equals("E") && confidence >= 0.8) {
                    isLetterEDetected = true;
                    isLetterEDetectedClear++;
                    if (isLetterEDetectedClear > 0 && isLetterLDetected) {
                        isLetterEDetected = true;
                    }
                } else if (label.equals("L") && confidence >= 0.8) {
                    isLetterL2Detected = true;
                    isLetterL2DetectedClear++;
                    if (isLetterL2DetectedClear > 0 && isLetterEDetected) {
                        isLetterL2Detected = true;
                    }
                } else if (label.equals("E") && confidence >= 0.8) {
                    isLetterE2Detected = true;
                    isLetterE2DetectedClear++;
                    if (isLetterE2DetectedClear > 0 && isLetterL2Detected) {
                        isLetterE2Detected = true;
                    }
                }
            }
        }

        if (isLetterLDetected && isLetterEDetected && isLetterL2Detected && isLetterE2Detected && !isAlertShown) {
            isAlertShown = true;
            showFinishedAlert();
        }
    }

    private void showFinishedAlert() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Selesai")
                    .setMessage("Finished!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        });
    }
}
