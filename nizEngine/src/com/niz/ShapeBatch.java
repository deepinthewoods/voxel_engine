package com.niz;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;

public class ShapeBatch {
	private static final String TAG = "shape batch";
	private ShapeRenderer shapes;
	//private Vector2[][] circles;
	public FloatArray lineCircleQ = new FloatArray(), lineQ = new FloatArray(), filledCircleQ = new FloatArray()
	, targetQ = new FloatArray();;
	public Array<Color> lineCircleColorQ = new Array<Color>(), lineColorQ = new Array<Color>(), filledCircleColorQ = new Array<Color>()
			, targetColorQ = new Array<Color>();;
			
	public ShapeBatch() {
		shapes = new ShapeRenderer();
		//circles = new Vector2[10][];
		//circles[0] = initLineCircle(16);
	}
	Vector3 b = new Vector3();
	public void draw(Camera shapeCamera, Camera worldCam) {

		shapes.setProjectionMatrix(shapeCamera.combined);
		shapes.begin(ShapeType.Line);
		while (lineCircleQ.size > 0){
			shapes.setColor(lineCircleColorQ.pop());
			shapes.circle(lineCircleQ.pop(), lineCircleQ.pop(), lineCircleQ.pop(), 16);
			
		}
		shapes.end();

		//worldCam.update();
		shapes.begin(ShapeType.Filled);
		while (pathQ.size > 0){
			IntArray q = pathQ.pop();
			for (int i = 0; i < pathQ.size-2; i+=2){
				int x = q.get(i), y = q.get(i+1);
				b.set(x,2,y);
				float widthOverHeight = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
				worldCam.project(b, 0,0,widthOverHeight, 1f);
				
				//cam.update();
				//Gdx.app.log("shaeps", "box"+b+"  "+x+" , "+y);
				shapes.circle(b.x, b.y, .5f, 12);
				
			}
		}
		shapes.end();
		
		shapes.begin(ShapeType.Line);
		while (lineQ.size > 0){
			shapes.setColor(lineColorQ.pop());
			shapes.line(lineQ.pop(),  lineQ.pop(), lineQ.pop(), lineQ.pop());
		}
		shapes.end();
		
		shapes.begin(ShapeType.Filled);
		while (filledCircleQ.size > 0){
			shapes.setColor(filledCircleColorQ.pop());
			shapes.circle(filledCircleQ.pop(), filledCircleQ.pop(), filledCircleQ.pop());
		}
		shapes.end();
		
		
		shapes.begin(ShapeType.Line);
		while (targetQ.size > 0){
			//Gdx.app.log("shaeps", "target");

			shapes.setColor(targetColorQ.pop());
			float px = targetQ.pop(), pz = targetQ.pop();
			worldCam.project(tmp.set(px,2,pz));
			//float invX = Gdx.graphics.getWidth(), invY = 1/Gdx.graphics;
			float widthOverHeight = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();

			float invX = 1f/ Gdx.graphics.getWidth();
			float invY = 1f/ Gdx.graphics.getHeight();
			
			tmp.x *= invX;
			tmp.x *= widthOverHeight;
			tmp.y *= invY;
			
			tmp.sub(widthOverHeight/2f, .5f, 0);
			
			float x = tmp.x, y = tmp.y, size = .025f, skewFactor = 1.3f;
			//Gdx.app.log(TAG, "draw "+x+"   ,   "+y);
			shapes.line(x+size*skewFactor, y, x+size, y-size);
			shapes.line(x+size, y+size, x+size*skewFactor, y);
			
			shapes.line(x-size*skewFactor, y, x-size, y-size);
			shapes.line(x-size, y+size, x-size*skewFactor, y);
			
		}
		shapes.end();
		
		
	}
	
	Vector2 v = new Vector2();
	/*private Vector2[] initLineCircle(int subdivisions) {
		float angle = 360f/subdivisions;
		Vector2[] verts = new Vector2[subdivisions*2+1];
		int index = 0;
		v.set(0,1f);
		v.rotate(angle/2);
		float c = Color.WHITE.toFloatBits();
		for (int i = 0; i < subdivisions+1; i++){
			verts[index++] = new Vector2(v);
			v.rotate(angle);
			verts[index++] = new Vector2(v);

			
		}
		
		return verts;
	}*/

	public void drawLineCircle(float x, float y, float radius,
			Color c) {
		lineCircleColorQ.add(c);
		lineCircleQ.add(radius);
		lineCircleQ.add(y);
		lineCircleQ.add(x);
		//lineCircleQ.add(i);
		//lineCircleQ.add(c);
	}

	public void drawInvItemTarget(float x, float y, float radius, Color c) {
		//lineQ.add(radius);
		lineQ.add(y+radius);
		lineQ.add(x);
		
		
		lineQ.add(y-radius);
		lineQ.add(x);
		lineColorQ.add(c);
		
		lineQ.add(y);
		lineQ.add(x+radius);
		
		
		lineQ.add(y);
		lineQ.add(x-radius);
		lineColorQ.add(c);
		
	}
	Color semiWhite = new Color(1f, 1f, 1f, .15f);
	public void drawWalkTarget(){
		Color c = semiWhite;
		float radius = .02f;
		lineQ.add(radius);
		lineQ.add(0);
		
		
		lineQ.add(-radius);
		lineQ.add(0);
		lineColorQ.add(c);
		
		lineQ.add(0);
		lineQ.add(radius);
		
		
		lineQ.add(0);
		lineQ.add(-radius);
		lineColorQ.add(c);
	}
	
	public void drawCentralLine(){
		Color c = semiWhite;
		float radius = .5f;
		lineQ.add(radius);
		lineQ.add(0);
		
		
		//lineQ.add(-radius);
		//lineQ.add(0);
		//lineColorQ.add(c);
		
		lineQ.add(-radius);
		lineQ.add(0);
		
		
		//lineQ.add(0);
		//lineQ.add(-radius);
		lineColorQ.add(c);
	}
	
	Vector3 tmp = new Vector3(), tmp2 = new Vector3();
	
	public void drawEntityTarget(float px, float py, float pz){
		tmp.set(px, py, pz);
		float x = px, z = pz;
		Color c = semiWhite;
		float radius = .02f;
		targetQ.add(z);
		targetQ.add(x);
		targetColorQ.add(Color.WHITE);
		
	}

	public void drawFilledCircle(float x, float y, float radius,
			Color c) {
		filledCircleColorQ.add(c);
		filledCircleQ.add(radius);
		filledCircleQ.add(y);
		filledCircleQ.add(x);
		
	}
	public Array<IntArray> pathQ = new Array<IntArray>();
	public void drawPath(IntArray path) {
		if (path != null);
			pathQ.add(path);
		
	}

	public void dispose() {
		
		
	}

}
