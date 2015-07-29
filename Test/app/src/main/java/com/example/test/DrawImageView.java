package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DrawImageView extends ImageView {
	PointF point = new PointF(); // 带浮点的point
	int len = 150; //矩形半径
//	int screenWidth;
//	int screenHeight;
	boolean init = true;
//	{
//		DisplayMetrics dm = new DisplayMetrics();
//		WindowManager vm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
//		vm.getDefaultDisplay().getMetrics(dm);
//		screenWidth = dm.widthPixels;
//		screenHeight = dm.heightPixels;
//	}
//	Bundle bundle = new Bundle();
//	public static final int UPDATE_LOCATION = 1;
//	public static final String NEW_POINT_X = "px";
//	public static final String NEW_POINT_Y = "py";
	int new_point_x;
	int new_point_y;
	
	public int get_X() {
		return new_point_x;
	}
	
	public int get_Y() {
		return new_point_y;
	}
	
	public void set_X(int x) {
		new_point_x = x;
	}
	
	public void set_Y(int y) {
		new_point_y = y;
	}
	
	public DrawImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	Paint paint = new Paint();
	{
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2.5f);//设置线宽
		paint.setAlpha(100);
	};
	
	public boolean onTouchEvent(android.view.MotionEvent event) {
		int nCount = event.getPointerCount();
		int n = event.getAction();
		
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN
				&& 2 == nCount) {
		
		// 下面这个处理缩放事件
		} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE
				&& 2 == nCount) { // ctrl +shift +f 自动把长行分解
			
			double xLen = Math.abs(event.getX(0) - event.getX(1));
			double yLen = Math.abs(event.getY(0) - event.getY(1));
			len = (int) Math.sqrt(xLen * xLen + yLen * yLen);
//			Log.e("TotalLen", len+"    "+xLen+"    "+yLen+"  : "+event.getX(0)+"　  "+event.getX(1)+"\n"+event.getY(0)+"  "+event.getY(1));
		} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN && 1 == nCount){
		} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && 1 == nCount) {
			point.set(event.getX(), event.getY());
			new_point_x = (int)event.getX();
			new_point_y = (int)event.getY();
	//		Log.e("1Point", new_point_x+"");
			// screenWidth 和 screenHeight 是整个屏幕的！
	//		if (new_point_x < 150)
	//			new_point_x = 150;
	//		if (new_point_x > (screenWidth-150))
	//			new_point_x = (screenWidth - 150);
	//		if (new_point_y < 150)
	//			new_point_y = 150;
	//		if (new_point_y > (screenHeight-150))
	//			new_point_y = (screenHeight - 150);
	//		Log.e("RECTANGLE", new_point_x+"");
	//		Log.e("screenWidth", screenWidth+"");
	//		Log.e("screenHeight", screenHeight+"");
			
			
	//		bundle.putFloat(NEW_POINT_X, event.getX());
	//		bundle.putFloat(NEW_POINT_Y, event.getY());
	//		
	//		Message message = new Message();
	//		message.what = UPDATE_LOCATION;
	//		message.setData(bundle);
	//		getHandler().sendMessage(message); // 这样可以吗？ 大概不可以
		}
		invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	//	WindowManager vm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
	//	getWindowManager().getDefaultDisplay().getMetrics(dm); // 别忘了这句！
	//	DisplayMetrics dm = new DisplayMetrics();
	//	WindowManager vm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
	//	vm.getDefaultDisplay().getMetrics(dm);
	//	int screenWidth = dm.widthPixels;
	//	int screenHeight = dm.heightPixels;
		// 这东西的原点是基于父 预览图 的！！！！！
		// 因为在layout里我们设置了这个控件和surfaceview一样大
		//canvas.getWidth();
		int w = canvas.getWidth();
		int h = canvas.getHeight();
	//	canvas.drawRect(new Rect(w/2-150, h/2-150, w/2+150, h/2+150), paint);//绘制矩形
		if (init) {
			canvas.drawRect(new Rect(w/2-150, h/2-150, w/2+150, h/2+150), paint);//绘制矩形
			init = false;
		} else {
			//if (new_point_x < )
			canvas.drawRect(new Rect(new_point_x-len, new_point_y-len, new_point_x+len, new_point_y+len), paint);//绘制矩形
		}
			//	canvas.drawRect(new Rect(432-150, 768-150, 432+150, 768+150), paint);//绘制矩形
		// canvas.drawRect(new Rect(screenWidth/2-150, screenHeight/2-150, screenWidth/2+150, screenHeight/2+150), paint);//绘制矩形
		
	}
	
	


	

}
