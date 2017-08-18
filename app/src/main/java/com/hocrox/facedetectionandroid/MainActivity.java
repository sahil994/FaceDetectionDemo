package com.hocrox.facedetectionandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraSourcePreview cameraSourcePreview = (CameraSourcePreview) findViewById(R.id.ff_camera);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.go_overlay);
        FaceDetector detector = new FaceDetector.Builder(this).build();

        detector.setProcessor(
                new MultiProcessor.Builder<Face>(new GraphicFaceTrackerFactory())
                        .build());

        CameraSource mCameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            cameraSourcePreview.start(mCameraSource, mGraphicOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    private class GraphicFaceTracker extends Tracker<Face> {

        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        public GraphicFaceTracker(GraphicOverlay mGraphicOverlay) {
            mOverlay=mGraphicOverlay;
            mFaceGraphic =new FaceGraphic(mOverlay);
        }

        @Override
        public void onNewItem(int faceId, Face face) {
            Log.e("Testing",""+faceId);
            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults,
                             Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
    private class GraphicFaceTrackerFactory
            implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }
}

