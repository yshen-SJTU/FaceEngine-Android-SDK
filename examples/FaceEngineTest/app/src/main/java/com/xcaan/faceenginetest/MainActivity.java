package com.xcaan.faceenginetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import com.xcaan.faceengine.*;

public class MainActivity extends AppCompatActivity {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Button btnSelection;
    private Button btnDetection;
    private Button btnEncoding;
    private TextView textView;
    private ImageView imageView;

    Bitmap bitmap = null;
    String imagePath;

    StringBuilder sb = new StringBuilder();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelection = findViewById(R.id.button1);
        btnDetection = findViewById(R.id.button2);
        btnEncoding = findViewById(R.id.button3);
        textView = findViewById(R.id.text);
        imageView = findViewById(R.id.image);
        //Access permissions: for SDK v23 - v28
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
            }
        }

        FaceEngine.init();

        btnSelection.setOnClickListener(view -> {
            sb.delete(0,sb.length());
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 2);
        });

        btnDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    List<FaceLocation> fls = FaceEngine.detectFace(imagePath);

                    sb.delete(0, sb.length());

                    if(fls.size() == 0)
                        sb.append("未检测到人脸。\n");
                    else
                    {
                        //传入canvas必须是能修改的bitmap对象
                        sb.append("检测到人脸: "+fls.size()+"\n");
                        sb.append("坐标: \n");
                        Bitmap bitmap_result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        for(int i=0;i<fls.size();i++)
                        {
                            Canvas canvas = new Canvas(bitmap_result);
                            Paint paint = new Paint();
                            paint.setStrokeWidth(3);
                            paint.setColor(Color.BLUE);
                            paint.setStyle(Paint.Style.STROKE);
                            canvas.drawRect(fls.get(i).xmin,fls.get(i).ymin,fls.get(i).xmax,fls.get(i).ymax,paint);
                            sb.append("> Face_" + (i+1) + ": ("+ (int)fls.get(i).xmin + ", " + (int)fls.get(i).ymin + ") - (" + (int)fls.get(i).xmax + ", " + (int)fls.get(i).ymax + ")\n");
                            textView.setText(sb.toString());
                        }
                        imageView.setImageBitmap(bitmap_result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnEncoding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    List<FaceInfo> fis = FaceEngine.getFaceFeature(imagePath);

                    sb.delete(0, sb.length());

                    if(fis.size() == 0)
                        sb.append("未检测到人脸。\n");
                    else
                    {
                        //传入canvas必须是能修改的bitmap对象
                        sb.append("检测到人脸: "+fis.size()+"\n");
                        Bitmap bitmap_result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        for(int i=0;i<fis.size();i++)
                        {
                            sb.append("Face_" + (i+1) + " 坐标与编码: \n");
                            FaceLocation fl = fis.get(i).location;
                            float[] ff = fis.get(i).feature;

                            Canvas canvas = new Canvas(bitmap_result);
                            Paint paint = new Paint();
                            paint.setStrokeWidth(3);
                            paint.setColor(Color.BLUE);
                            paint.setStyle(Paint.Style.STROKE);
                            canvas.drawRect(fl.xmin, fl.ymin, fl.xmax, fl.ymax, paint);

                            sb.append("> 坐标：("+ (int)fl.xmin + ", " + (int)fl.ymin + ") - (" + (int)fl.xmax + ", " + (int)fl.ymax + ")\n");

                            sb.append("> 128维编码：(");
                            for(int j=0; j<ff.length; j++) {
                                sb.append( ff[j] + " ");
                            }
                            sb.append(")\n");

                            textView.setText(sb.toString());
                        }
                        imageView.setImageBitmap(bitmap_result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        sb.delete(0, sb.length());
        sb.append("请从相册选择图片。");
        textView.setText(sb.toString());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                imageView.setImageURI(uri);
                imagePath = RealPathFromUriUtils.getRealPathFromUri(this, uri);
                Log.e(this.getClass().getName(), "Uri:" + String.valueOf(imagePath));
                bitmap = BitmapFactory.decodeFile(imagePath);

                sb.delete(0, sb.length());
                sb.append("已经选择图片，可尝试进行人脸检测。");
                textView.setText(sb.toString());
            }
            else {
                sb.delete(0, sb.length());
                sb.append("请从相册选择图片。");
                textView.setText(sb.toString());
            }
        }
    }
}