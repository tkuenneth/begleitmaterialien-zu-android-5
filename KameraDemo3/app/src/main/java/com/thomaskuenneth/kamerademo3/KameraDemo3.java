package com.thomaskuenneth.kamerademo3;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class KameraDemo3 extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = KameraDemo3.class.getSimpleName();

    private CameraManager manager;
    private SurfaceHolder holder;
    private CameraDevice camera;
    private CameraCaptureSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Benutzeroberfläche anzeigen
        setContentView(R.layout.main);
        SurfaceView view = (SurfaceView) findViewById(R.id.view);
        holder = view.getHolder();
        // CameraManager-Instanz ermitteln
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            if (session != null) {
                session.close();
            }
            camera.close();
        }
        holder.removeCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        holder.addCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        prepareCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(TAG, "surfaceChanged()");
    }

    private void prepareCamera() {
        camera = null;
        session = null;
        Size[] sizes = null;
        String cameraId = null;
        try {
            // vorhandene Kameras ermitteln und auswählen
            String[] ids = manager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics cc = manager.getCameraCharacteristics(id);
                Log.d(TAG, id + ": " + cc.toString());
                if (cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id;
                    StreamConfigurationMap configs = cc.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    sizes = configs.getOutputSizes(SurfaceHolder.class);
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "getCameraIdList() oder getCameraCharacteristics()", e);
        }
        if (sizes == null) {
            Log.d(TAG, "keine passende Kamera gefunden");
            finish();
        } else {
            holder.setFixedSize(sizes[0].getWidth(), sizes[0].getHeight());
            openCamera(cameraId);
        }
    }

    private void openCamera(String id) {
        try {
            manager.openCamera(id, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    try {
                        CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        builder.addTarget(holder.getSurface());
                        createCaptureSession(camera, builder.build());
                        KameraDemo3.this.camera = camera;
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "createCaptureRequest()", e);
                    }
                    Log.d(TAG, "onOpened()");
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.d(TAG, "onDisconnected()");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.d(TAG, "onError()");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "openCamera()", e);
        }
    }

    private void createCaptureSession(CameraDevice camera, final CaptureRequest request) {
        List<Surface> outputs = new ArrayList<>();
        outputs.add(holder.getSurface());
        try {
            camera.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(request, null, null);
                        KameraDemo3.this.session = session;
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "capture()", e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed()");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createCaptureSession()", e);
        }
    }
}