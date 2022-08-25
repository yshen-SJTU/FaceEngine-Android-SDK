# FaceEngine Android SDK
A group of libraries for face detection and recognition that provide following features:
- Support Android (arm64-v8a, armeabi-v7a and x86)
- Simple Java APIs
- Easy to use and integrate

## File/Dir Introduction

- JniLibs/{android_arch}/libxcaan_faceengine.so

  The main library file for face detection and recognition implemented using C. It needs libopencv_java4.so. Depending on your android system, libc++_shared.so may be needed. {android_arch} stands for arm64-v8a, armeabi-v7a or x86.

- libs/FaceEngine.jar
  
  Java interface provided by the SDK for your application.

- examples/FaceEngineTest

  An example using the SDK. It is a project created with Android Studio.

<font color="#dd0000"> ** We suggest that you download the above library files from <a href="https://pan.baidu.com/s/1SPFSy6ILbu7IVm4fz1tLSQ ">Baidu cloud (code: sjtu)</a> since they've exceeded the Git LFS storage quota and may be incorrect. **</font>


## Using FaceEngine Android SDK

A code example:

```Java
public class MainActivity extends AppCompatActivity {

    ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		...

        FaceEngine.init(); // First you have to invoke the init function
		...

        List<FaceLocation> fls = FaceEngine.detectFace(imagePath); //then you can use other APIs anywhere in your project

        for(int i=0;i<fls.size();i++) {
            ...

            sb.append("Face_" + (i+1) + ": ("+ fls.get(i).xmin + ", " + fls.get(i).ymin + ") - (" + fls.get(i).xmax + ", " + fls.get(i).ymax + ")\n");
            textView.setText(sb.toString());
        }
        ...

   }
}
```

The example of FaceEngineTest can be the start of your application development.

#### The class FaceLocation

```Java
package com.xcaan.faceengine;

public class FaceLocation {
    public float xmin, ymin, xmax, ymax, confidence;
}
```

#### The class FaceInfo

```Java
package com.xcaan.faceengine;

import java.util.*;

public class FaceInfo {
    public float[] feature;
    public FaceLocation location;
}
```

#### The class FaceEngine

```Java
package com.xcaan.faceengine;

import android.os.Bundle;
import android.util.Log;
import android.graphics.Bitmap;

import java.util.List;

public class FaceEngine {

    static {
        System.loadLibrary("xcaan_faceengine");
    }

    public static native void init();
    
    public static native void setImageResizeArgs(int flag, int width, int height, int min_size, float min_ratio);

    public static native List<FaceLocation> detectFace(String path);
    public static native List<FaceInfo> getFaceFeature(String path);
    public static native List<FaceInfo> getFaceFeatureFromBitmap(Bitmap bitmap);

}
```

#### The Java APIs of FaceEngine

1. Init function
```Java
public static native void init();
```

This function has to be invoked once before any other SDK functions are invoked.

 
2. Face detection
```Java
public static native List<FaceLocation> detectFace(String path);
```

Parameters
 - path - file path of the image to be detected.

Return
 - List of locations of detected faces in the image.

3. Get face feature
```Java
public static native List<FaceInfo> getFaceFeature(String path);
public static native List<FaceInfo> getFaceFeatureFromBitmap(Bitmap bitmap);
```

Parameters
 - path/bitmap - file path or bitmap of the image to be detected.

Return
 - List of face information including face features of detected faces in the image.

4. Resize image
```Java
public static native void setImageResizeArgs(int resize_flag, int resizeto_width, int resizeto_height, int min_size, float min_ratio);
```

Images can be resized to smaller size to reduce computation. The invocation of this API has global effect that means all the following images will be resized according to its parameters until the next invocation of the API.

Parameters
 - resize_flag - 
	- 0   - images will not be resized;
    - < 0 - images will be resized and will not keep image's width/height ratio; 
    - \> 0 - images will be resized and will keep image's width/height ratio. Note that the ratios of image_width/resizeto_width and image_height/resizeto_height may have different values. resize_flag=1 indicates that images are resized using the bigger ratio.

 - resizeto_width - width that images will be resized to; Note that if resizeto_width is less than min_size, min_size will be take as resizeto_width.

 - resizeto_height - height that images will be resized to; Note that if resizeto_height is less than min_size, min_size will be take as resizeto_height.

 - min_size - the minimal width/height value that images will be resized to. This is a threshold size to avoid too small image. The default value is 80, and if min_size has a value less than 80, the SDK will use 80 instead. 

 - min_ratio - the minimal ratio that images will be resized to. For a high resolution image, the faces in the final resized image may be very small even if we resize it to the size of (resizeto_width, resizeto_height). This is a threshold ratio to avoid too small faces in a resized image. The default value is 0.3.



## License

The SDK can be used for any personal or commercial purpose. The network models embedded in the SDK will be expired and upgraded by Sept. 2024. 

The SDK is distributed 'as is' and with no warranties of any kind, whether express or implied. The user must assume the entire risk of using the SDK, and the development team won't be held responsible for any problem in your using the SDK.


## The Development Team

FaceEngine is a cooperation between Shanghai Jiao Tong University and XCAAN with several engineers and students contributing to it.

Please contact <yshen@cs.sjtu.edu.cn> if you have problems about using the SDK. Any technical exchange and cooperation are welcomed.



