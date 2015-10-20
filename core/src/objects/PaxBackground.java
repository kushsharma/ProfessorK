package objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.softnuke.biosleep.MyGame;

public class PaxBackground {
//fix this shit
	//Try to center project camera coordinates
	
	private float x;
	private float y;
	private float vx = -0.2f;
	private float vy;
	private float bWIDTH, bHEIGHT; 
	private float WIDTH, HEIGHT; 

	TextureRegion[] layers;
	ParallaxCamera paxCamera;
	OrthographicCamera mainCam;
	Sprite backSprite, buildLightSprite, cbuildLightSprite, buildDarkSprite, cbuildDarkSprite;
	
	public PaxBackground(OrthographicCamera cam ){
		//tmountain= new Texture("mountains-black-blur-extra.png");
//		tmountain= new Texture("back/background-1.png");
//		mountain = new Sprite(tmountain);
//		cmountain = new Sprite(tmountain);

		bHEIGHT = MyGame.bHEIGHT;
		bWIDTH = MyGame.bWIDTH;
		HEIGHT = MyGame.HEIGHT;
		WIDTH = MyGame.WIDTH;
		
		mainCam = cam;
		
			
		
		this.init();
	}
	
	public void init(){
		this.x = 0;
		this.y = 0;
		
		
		
		layers = new TextureRegion[3];
		
		Texture layer1 = new Texture("back/background-1.png");
		layer1.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		layer1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		layers[0] = new TextureRegion(layer1);
		
		Texture layer2 = new Texture("back/background-2.png");
		layer2.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		layer2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		layers[1] = new TextureRegion(layer2);
		
		Texture layer3 = new Texture("back/background-3.png");
		layer3.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		layer3.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		layers[2] = new TextureRegion(layer3);
		
		
		
		backSprite = new Sprite(layer1);
		//backSprite.setPosition(-bWIDTH/2, -bHEIGHT/2);
		backSprite.setPosition(0, 0);
		backSprite.setSize(bWIDTH, bHEIGHT);
		
		buildDarkSprite  = new Sprite(layer2);
		//buildDarkSprite.setPosition(-bWIDTH/2, -bHEIGHT*0.6f);
		buildDarkSprite.setPosition(0,0);
		buildDarkSprite.setSize(bHEIGHT * buildDarkSprite.getWidth()/buildDarkSprite.getHeight(), bHEIGHT);

		buildLightSprite = new Sprite(layer3);
		//buildLightSprite.setPosition(-bWIDTH/2, -bHEIGHT/2);
		buildLightSprite.setPosition(0, 0);
		buildLightSprite.setSize(bHEIGHT * buildLightSprite.getWidth()/buildLightSprite.getHeight(), bHEIGHT);
		
		
		cbuildLightSprite = new Sprite(buildLightSprite);
		cbuildDarkSprite = new Sprite(buildDarkSprite);
		
		cbuildLightSprite.setPosition(buildLightSprite.getX() + buildLightSprite.getWidth(), cbuildLightSprite.getY());
		cbuildDarkSprite.setPosition(buildDarkSprite.getX() + buildDarkSprite.getWidth(), cbuildDarkSprite.getY());

//		this.mountain.setOrigin(0f, 0f);
//		//mountain.setScale(0.5f);
//		this.mountain.setSize(bWIDTH, bWIDTH * actual_aspect_ratio);
//		
//		this.cmountain.setOrigin(0f, 0f);
//		this.cmountain.setSize(bWIDTH, bWIDTH * actual_aspect_ratio);
//		
//		rocks = new Sprite(mountain);
//		rocks.flip(false, true);
//		crocks = new Sprite(cmountain);
//		crocks.flip(false, true);
		
		
	}
	
	public void draw(SpriteBatch batch){	
		
		batch.setProjectionMatrix(mainCam.combined);
		batch.begin();
		//batch.draw(layers[0], 0, 0);
		backSprite.draw(batch);
		
		//batch.draw(layers[1], 0, 0);
		buildLightSprite.draw(batch);
		cbuildLightSprite.draw(batch);
		
		//batch.draw(layers[2], 0, 0);
		buildDarkSprite.draw(batch);
		cbuildDarkSprite.draw(batch);
		//batch.draw(layers[2].getTexture(), 0, 0 , (int)buildDarkSprite.getX(), (int)buildDarkSprite.getY(), (int)buildDarkSprite.getWidth(), (int)buildDarkSprite.getHeight());
				
		//batch.draw(layers[1].getTexture(), mainCam.position.x - bWIDTH/2, mainCam.position.y - bHEIGHT/2, bWIDTH, bHEIGHT, buildDarkSprite.getX(), buildDarkSprite.getY(), buildDarkSprite.getX()+ (bWIDTH)/10,  -bHEIGHT/layers[1].getTexture().getHeight() - buildDarkSprite.getY());
		//batch.draw(layers[2].getTexture(), 0, 0 , buildDarkSprite.getWidth(), buildDarkSprite.getHeight(), buildDarkSprite.getX(), buildDarkSprite.getY(), buildDarkSprite.getWidth(), buildDarkSprite.getHeight(), false, false);
		batch.end();
		
//		this.mountain.setPosition(this.x, this.y);		
//		this.cmountain.setPosition(this.x + this.cmountain.getWidth(), this.y);
//		
//		this.rocks.setPosition(this.x, bHEIGHT - this.rocks.getHeight());		
//		this.crocks.setPosition(this.x + this.crocks.getWidth(), bHEIGHT - this.rocks.getHeight());
//		
//		this.mountain.draw(batch);
//		this.cmountain.draw(batch);
//		
//		this.rocks.draw(batch);
//		this.crocks.draw(batch);
	}
	
	public void update(float delta){
		// If the image scrolled off the screen, reset
//		if (buildDarkSprite.getX() + buildDarkSprite.getWidth()  + bWIDTH/2 < paxCamera.position.x)
//			buildDarkSprite.setPosition(cbuildDarkSprite.getX()+cbuildDarkSprite.getWidth(), buildDarkSprite.getY());
//		
//		if(cbuildDarkSprite.getX() + cbuildDarkSprite.getWidth()  + bWIDTH/2 < paxCamera.position.x)
//			cbuildDarkSprite.setPosition(buildDarkSprite.getX()+buildDarkSprite.getWidth(), cbuildDarkSprite.getY());
		
		float x = mainCam.position.x - bWIDTH/2;
		
		backSprite.setPosition(x, mainCam.position.y - bHEIGHT/2);
		
		if(buildDarkSprite.getX() + buildDarkSprite.getWidth() > x){
			buildDarkSprite.setPosition( - x * 0.2f, buildDarkSprite.getY());
			cbuildDarkSprite.setPosition(buildDarkSprite.getX()+buildDarkSprite.getWidth(), cbuildDarkSprite.getY());			
		}
		else{
			buildDarkSprite.setPosition(cbuildDarkSprite.getX()+cbuildDarkSprite.getWidth(), buildDarkSprite.getY());
			cbuildDarkSprite.setPosition( - x * 0.2f, cbuildDarkSprite.getY());
		}
		
		//if(buildDarkSprite.getX() + buildDarkSprite.getWidth() < x)
		//	buildDarkSprite.setPosition( (- x+ cbuildDarkSprite.getWidth()) * 0.2f, buildDarkSprite.getY());
		
		this.x += delta * this.vx;
				
		
	}
	
	public void dispose(){

		
	}
	
	class ParallaxCamera extends OrthographicCamera {
		Matrix4 parallaxView = new Matrix4();
		Matrix4 parallaxCombined = new Matrix4();
		Vector3 tmp = new Vector3();
		Vector3 tmp2 = new Vector3();

		public ParallaxCamera (float viewportWidth, float viewportHeight) {
			super(viewportWidth, viewportHeight);
		}
		
		public ParallaxCamera (boolean yDown, float viewportWidth, float viewportHeight) {
			super();
			this.setToOrtho(yDown, viewportWidth, viewportHeight);
		}

		public Matrix4 calculateParallaxMatrix (float parallaxX, float parallaxY) {
			update();
			tmp.set(position);
			tmp.x *= parallaxX;
			tmp.y *= parallaxY;

			parallaxView.setToLookAt(tmp, tmp2.set(tmp).add(direction), up);
			parallaxCombined.set(projection);
			
			Matrix4.mul(parallaxCombined.val, parallaxView.val);
			return parallaxCombined;
		}
	}
}
