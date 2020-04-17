package com.example.opencvanddemp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener{

    private CameraBridgeViewBase openCvCameraView;
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
    private int absoluteFaceSize;
    private Bitmap rectBitmap;
    private void initializeOpenCVDependencies() {
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }
        openCvCameraView.enableView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        openCvCameraView = new JavaCameraView(this, -1);    // 新建一个布局文件
        setContentView(openCvCameraView);   // 为该活动设置布局
        openCvCameraView.setCvCameraViewListener(this);

    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);
        absoluteFaceSize = (int) (height * 0.2);
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(Mat aInputFrame) {
        Mat copyMat = new Mat();
        aInputFrame.copyTo(copyMat); // 复制
        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
        MatOfRect faces = new MatOfRect();
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }
        Rect[] facesArray = faces.toArray();

        int maxRectArea = 0 * 0;
        Rect maxRect = null;
        int facenum = 0;
        for (int i = 0; i <facesArray.length; i++) {
            Rect rect=facesArray[i];
            Imgproc.rectangle(aInputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
            ++facenum;
            // 找出最大的面积
            int tmp = rect.width * rect.height;
            if (tmp >= maxRectArea) {
                maxRectArea = tmp;
                maxRect = rect;
            }
        }
        rectBitmap = null;
        if (facenum != 0) {
            // 剪切最大的头像
            Log.e("剪切的长宽", String.format("高:%s,宽:%s", maxRect.width, maxRect.height));
            Rect rect = new Rect(maxRect.x, maxRect.y, maxRect.width, maxRect.height);
            Mat rectMat = new Mat(copyMat, rect);  // 从原始图像拿
            rectBitmap = Bitmap.createBitmap(rectMat.cols(), rectMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rectMat, rectBitmap);
        }
        Log.i("onCameraFrame: ", "检测到"+facenum+"个人脸");
//        Toast.makeText(MainActivity.this,"检测到"+facenum+"个人脸",Toast.LENGTH_SHORT).show();
//        textView.setText(String.format("检测到%1$d个人脸", facenum));
//        Utils.matToBitmap(toMat, bitmap);
        return aInputFrame;
    }
    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
        }
        initializeOpenCVDependencies();
    }
}
