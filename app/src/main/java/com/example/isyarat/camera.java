package com.example.isyarat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private ExecutorService cameraExecutor;
    private Camera camera;

    private Analyzer<Void> imageAnalyzer;
    private Callback callback;
    private String model;

    public CameraFragment(Analyzer<Void> imageAnalyzer, Callback callback, String model) {
        this.imageAnalyzer = imageAnalyzer;
        this.callback = callback;
        this.model = model;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor();

        PreviewConfig previewConfig = new PreviewConfig.Builder().build();
        Preview preview = new Preview(previewConfig);

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder().setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer(cameraExecutor, imageAnalyzer);

        Camera camera = Camera.open(cameraSelector);

        int rotation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();
        camera.setTargetRotation(rotation);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        camera.setPreviewSurfaceProvider(previewView.getSurfaceProvider());

        try {
            camera.setTargetRotation(Surface.ROTATION_0);
            camera.setPreviewSurfaceProvider(preview.getPreviewSurfaceProvider());
            camera.setAnalyzer(imageAnalyzer);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up camera: " + e.getMessage());
        }

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        camera = Camera.open(cameraSelector);

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        Camera finalCamera = camera;
        cameraExecutor.execute(() -> {
            try {
                finalCamera.setPreviewSurfaceProvider(preview.getPreviewSurfaceProvider());
                finalCamera.setAnalyzer(imageAnalyzer);
            } catch (Exception e) {
                Log.e(TAG, "Error setting up camera: " + e.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
        camera.close();
    }

    public interface Callback {
        void setRecognitions(List<Object> list, int h, int w);
    }
}

