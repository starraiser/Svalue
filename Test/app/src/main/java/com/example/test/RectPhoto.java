/*作者yanzi1225627，欢迎加QQ1927067685进行android开发交流*/

package com.example.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class RectPhoto extends Activity implements SurfaceHolder.Callback, OnTouchListener, OnClickListener {
	private static final String tag = "yan";
	private boolean isPreview = false;
	private SurfaceView mPreviewSV = null; // 预览SurfaceView
	private DrawImageView mDrawIV = null;
	private SurfaceHolder mySurfaceHolder = null;
	private Button mPhotoImgBtn = null;
	private Camera myCamera = null;
	private Bitmap mBitmap = null;
	private AutoFocusCallback myAutoFocusCallback = null;

	Bundle bundle;
	int new_point_x = 200;
	int new_point_y = 200;

	Button focusDown;
	Button focusUp;

	public int zoom = 0;

	Camera.Parameters myParam;

	TextView text;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					bundle = msg.getData();
					new_point_x = (int)bundle.getFloat("px");
					new_point_y = (int)bundle.getFloat("py");
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置全屏无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		Window myWindow = this.getWindow();
		myWindow.setFlags(flag, flag);

		setContentView(R.layout.activity_rect_photo);

		focusDown = (Button)findViewById(R.id.focusDown);
		focusDown.setOnClickListener(this);
		focusUp = (Button)findViewById(R.id.focusUp);
		focusUp.setOnClickListener(this);

		text = (TextView) findViewById(R.id.text);

		// 初始化SurfaceView
		mPreviewSV = (SurfaceView) findViewById(R.id.previewSV);
		//放在顶层
		mPreviewSV.setZOrderOnTop(false);
		mySurfaceHolder = mPreviewSV.getHolder();
		mySurfaceHolder.setFormat(PixelFormat.TRANSPARENT);// translucent半透明
		// transparent透明

		mySurfaceHolder.addCallback(this);
		mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// 自动聚焦变量回调
		myAutoFocusCallback = new AutoFocusCallback() {

			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub
				if (success)// success表示对焦成功
				{
					Log.i(tag, "myAutoFocusCallback: success...");
					// myCamera.setOneShotPreviewCallback(null);

				} else {
					// 未对焦成功
					Log.i(tag, "myAutoFocusCallback: 失败了...");

				}

			}
		};

		// 绘制矩形的ImageView
		mDrawIV = (com.example.test.DrawImageView) findViewById(R.id.drawIV);
		mDrawIV.setOnTouchListener(this);
		mDrawIV.draw(new Canvas());

		mPhotoImgBtn = (Button) findViewById(R.id.photoImgBtn);
		// 手动设置拍照ImageButton的大小为120×120,原图片大小是64×64
		LayoutParams lp = mPhotoImgBtn.getLayoutParams();
		lp.width = 120;
		lp.height = 120;
		mPhotoImgBtn.setLayoutParams(lp);
		mPhotoImgBtn.setOnClickListener(new PhotoOnClickListener());
		mPhotoImgBtn.setOnTouchListener(new MyOnTouchListener());

	}

	/* 下面三个是SurfaceHolder.Callback创建的回调函数 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height)
	// 当SurfaceView/预览界面的格式和大小发生改变时，该方法被调用
	{
		// TODO Auto-generated method stub
		Log.i(tag, "SurfaceHolder.Callback:surfaceChanged!");
		initCamera();

	}

	public void surfaceCreated(SurfaceHolder holder)
	// SurfaceView启动时/初次实例化，预览界面被创建时，该方法被调用。
	{
		// TODO Auto-generated method stub

		myCamera = Camera.open();
		try {
			myCamera.setPreviewDisplay(mySurfaceHolder);
			Log.i(tag, "SurfaceHolder.Callback: surfaceCreated!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (null != myCamera) {
				myCamera.release();
				myCamera = null;
			}
			e.printStackTrace();
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder)
	// 销毁时被调用
	{
		// TODO Auto-generated method stub
		Log.i(tag, "SurfaceHolder.Callback：Surface Destroyed");
		if (null != myCamera) {
			myCamera.setPreviewCallback(null); /*
												 * 在启动PreviewCallback时这个必须在前不然退出出错。
												 * 这里实际上注释掉也没关系
												 */

			myCamera.stopPreview();
			isPreview = false;
			myCamera.release();
			myCamera = null;
		}

	}

	// 初始化相机
	public void initCamera() {

		if (isPreview) {
			myCamera.stopPreview();
		}
		if (null != myCamera) {

			//	Camera.Parameters myParam = myCamera.getParameters();
			myParam = myCamera.getParameters();
			// //查询屏幕的宽和高
			// WindowManager wm =
			// (WindowManager)getSystemService(Context.WINDOW_SERVICE);
			// Display display = wm.getDefaultDisplay();
			// Log.i(tag,
			// "屏幕宽度："+display.getWidth()+" 屏幕高度:"+display.getHeight());

			myParam.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式


			// //查询camera支持的picturesize和previewsize
			// List<Size> pictureSizes = myParam.getSupportedPictureSizes();
			// List<Size> previewSizes = myParam.getSupportedPreviewSizes();
			// for(int i=0; i<pictureSizes.size(); i++){
			// Size size = pictureSizes.get(i);
			// Log.i(tag,
			// "initCamera:摄像头支持的pictureSizes: width = "+size.width+"height = "+size.height);
			// }
			// for(int i=0; i<previewSizes.size(); i++){
			// Size size = previewSizes.get(i);
			// Log.i(tag,
			// "initCamera:摄像头支持的previewSizes: width = "+size.width+"height = "+size.height);
			//
			//
			// 设置大小和方向等参数
			//myParam.setPictureSize(720, 1280);
			/*myParam.setPictureSize(1920, 1080); // 像素
			//myParam.setPreviewSize(540, 960);
			myParam.setPreviewSize(960, 540); // 像素
			// myParam.set("rotation", 90);
//			myParam.setColorEffect(Parameters.EFFECT_NEGATIVE);
			myCamera.setDisplayOrientation(90);
			myParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			myCamera.setParameters(myParam);*/
			//获取支持的PreviewSize
			List<Camera.Size> previewSizeList = myParam
					.getSupportedPreviewSizes();
			//对获得的PreviewSize排序
			Collections.sort(previewSizeList, new CameraSizeComparator());
			//选择第一个
			Camera.Size previewSize = previewSizeList.get(0);
			//myParam.setPreviewSize(previewSize.width, previewSize.height);
			myParam.setPreviewSize(previewSize.width, previewSize.height);
			//myParam.set("rotation", 90);
			//myParam.setColorEffect(Parameters.EFFECT_NEGATIVE);
			//旋转镜头
			myCamera.setDisplayOrientation(90);
			myParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			//myParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			myCamera.setParameters(myParam);
			myCamera.startPreview();
			myCamera.autoFocus(myAutoFocusCallback);
			isPreview = true;

			//	myParam.setZoom(zoom);
		}
	}
	class CameraSizeComparator implements Comparator<Camera.Size> {
		public int compare(Camera.Size lhs, Camera.Size rhs) {
			long lsize = lhs.width * lhs.height;
			long rsize = rhs.width * lhs.height;
			if (lsize == rsize)
				return 0;
			else if (lsize < rsize)
				return 1;
			else
				return -1;
		}
	}
	/* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
	ShutterCallback myShutterCallback = new ShutterCallback()
			// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	{

		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i(tag, "myShutterCallback:onShutter...");

		}
	};
	PictureCallback myRawCallback = new PictureCallback()
			// 拍摄的未压缩原数据的回调,可以为null
	{

		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(tag, "myRawCallback:onPictureTaken...");

		}
	};
	PictureCallback myJpegCallback = new PictureCallback()
			// 对jpeg图像数据的回调,最重要的一个回调
	{
		//	Camera.Parameters myParam = myCamera.getParameters();
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(tag, "myJpegCallback:onPictureTaken...");
			if (null != data) {
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				myCamera.stopPreview();
				isPreview = false;
			}
			// 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
			// 90)失效。图片竟然不能旋转了，故这里要旋转下
			Matrix matrix = new Matrix();
			matrix.postRotate((float) 90.0); // 如果不旋转 则数朝左的

//			DisplayMetrics dm = new DisplayMetrics();
//			getWindowManager().getDefaultDisplay().getMetrics(dm);
//			int screenWidth = dm.widthPixels;
//			int screenHeight = dm.heightPixels;

			// 下面这个是拍照后向右旋转90度的图
			// Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
			// screenWidth, screenHeight, matrix, false);
			Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
					mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
			// Bitmap rectBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
			// mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
			// 旋转后rotaBitmap是960×1280.预览surfaview的大小是540×800
			// 将960×1280缩放到540×800

			int preview_width = mPreviewSV.getWidth();
			int preview_Height = mPreviewSV.getHeight();

			//		Bitmap sizeBitmap = Bitmap.createScaledBitmap(rotaBitmap, 864,
			//				1536, true);

			Bitmap sizeBitmap = Bitmap.createScaledBitmap(rotaBitmap, preview_width,
					preview_Height, true);

//			Log.e("TAG",
//					sizeBitmap.getWidth() + "    ^^^^   "
//							+ sizeBitmap.getHeight());
//
//			Log.e("TAG", sizeBitmap.getHeight() / 2 - 150+"");
//			
//			Log.e("TAG", sizeBitmap.getHeight() / 2 - 300+"");

//			Bitmap rectBitmap = Bitmap.createBitmap(sizeBitmap,
//					sizeBitmap.getWidth() / 2 - 150,
//					sizeBitmap.getHeight() / 2 -150, 300, 300);// 截取

			Bitmap rectBitmap = Bitmap.createBitmap(sizeBitmap,
					new_point_x - 150,
					new_point_y -150, 300, 300);// 截取
			//灰度图
			Bitmap grayBitmap = rectBitmap.copy(Config.ARGB_8888, true);
			if (grayBitmap == null) {
				Log.e("Bitmap","null");
			}

			for (int i = 0; i < grayBitmap.getWidth(); i++) {
				for (int j = 0; j < grayBitmap.getHeight(); j++) {
					int col = grayBitmap.getPixel(i, j);
					int alpha = col & 0xFF000000;
					int red = (col & 0x00FF0000)>>16;
					int green = (col & 0x0000FF00)>>8;
					int blue = col & 0x000000FF;

					int gray = (int)((float)red*0.3+(float)green*0.59+(float)blue*0.11);
					int newColor = alpha | (gray<<16) | (gray << 8) | gray;
					grayBitmap.setPixel(i, j, newColor);
				}
			}

			// 保存图片到sdcard
			if (null != grayBitmap) {
				saveJpeg(grayBitmap);
			}
			// 再次进入预览
			myCamera.startPreview();
			isPreview = true;
		}
	};

	// 拍照按键的监听
	public class PhotoOnClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (isPreview && myCamera != null) {
				myCamera.takePicture(myShutterCallback, null, myJpegCallback);
			}

		}

	}

	/* 给定一个Bitmap，进行保存 */
	public void saveJpeg(Bitmap bm) {
		String savePath = "/mnt/sdcard/rectPhoto/";
		File folder = new File(savePath);
		if (!folder.exists()) // 如果文件夹不存在则创建
		{
			folder.mkdir();
		}
		long dataTake = System.currentTimeMillis();
		String jpegName = savePath + dataTake + ".jpg";
		Log.i(tag, "saveJpeg:jpegName--" + jpegName);
		// File jpegFile = new File(jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);

			// //如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
			// Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);

			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			File file = new File(jpegName);
			regular(file);
			Log.i(tag, "saveJpeg：存储完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(tag, "saveJpeg:存储失败！");
			Log.e("WTF", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
	}

	static void regular(File file){
		if(file.isDirectory()){
			File files[] = file.listFiles();
			for(int i = 0; i < files.length; i++){
				regular(files[i]);
			}
		}
		else{
			new Thread(new User(file)).start();
		}
	}
	/* 为了使图片按钮按下和弹起状态不同，采用过滤颜色的方法.按下的时候让图片颜色变淡 */
	public class MyOnTouchListener implements OnTouchListener {

		public final float[] BT_SELECTED = new float[] { 2, 0, 0, 0, 2, 0, 2,
				0, 0, 2, 0, 0, 2, 0, 2, 0, 0, 0, 1, 0 };

		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
				1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());

			}
			return false;
		}

	}

	@Override
	public void onBackPressed()
	// 无意中按返回键时要释放内存
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
		RectPhoto.this.finish();
	}

	public boolean onTouch(View v, MotionEvent event) {// 记得设置OnTouchListener
		// 其实也可以在这里直接获取位置的，就不用那个类再传过来了
		new_point_x = mDrawIV.get_X();
		new_point_y = mDrawIV.get_Y();
//		Log.e("OnTouchListener", new_point_x+"");
//		Log.e("OnTouchListener", "！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
		text.setText(zoom+"");
//		text.setText(myCamera.getParameters().getMaxZoom()+"");
		return false;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.focusDown:
				if (zoom > 0) {
					zoom-=1;
					myParam.setZoom(zoom);
					myCamera.setParameters(myParam);
					myCamera.autoFocus(myAutoFocusCallback);
					Log.e("ZOOM--", zoom+"");
				}
				break;
			case R.id.focusUp:
				if (zoom < myCamera.getParameters().getMaxZoom()) {
					zoom+=1;
					myParam.setZoom(zoom);
					myCamera.setParameters(myParam);
					myCamera.autoFocus(myAutoFocusCallback);
					Log.e("ZOOM++", zoom+"");
				}
				break;
			default:
				break;
		}
	}
}
