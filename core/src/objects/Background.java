package objects;

import screens.GameScreen;
import utils.AssetLord;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.softnuke.biosleep.MyGame;

public class Background {
//fix this shit
	//may contain bugs
	
	public static int TOTAL_SPRITES = 10;
	
	private float x;
	private float y;
	private float vx = -0.2f;
	private float vy;
	private float bWIDTH, bHEIGHT;
	private float WIDTH, HEIGHT;
	
	TextureRegion[] layers;
	ParallaxCamera paxCamera;
	OrthographicCamera mainCam;
	OrthographicCamera camLayer1, camLayer2;
	Sprite backSprite, buildLightSprite, cbuildLightSprite, buildDarkSprite, cbuildDarkSprite;
	
	Array<Sprite> paxLayer1 = new Array<Sprite>();
	Array<Sprite> paxLayer2 = new Array<Sprite>();

	Texture layer1,layer2,layer3;
		
	public Background(OrthographicCamera cam ){
		//tmountain= new Texture("mountains-black-blur-extra.png");
//		tmountain= new Texture("back/background-1.png");
//		mountain = new Sprite(tmountain);
//		cmountain = new Sprite(tmountain);

		bHEIGHT = MyGame.bHEIGHT;
		bWIDTH = MyGame.bWIDTH;
		WIDTH = MyGame.WIDTH;
		HEIGHT = MyGame.HEIGHT;
		
		mainCam = cam;
		
		//no use
//		camLayer1 = new OrthographicCamera();
//		camLayer1.setToOrtho(false, bWIDTH, bHEIGHT);
//		camLayer1.position.set(camLayer1.viewportWidth/2, camLayer1.viewportHeight/2, 0);
//		camLayer1.update();		
//		camLayer2 = new OrthographicCamera();
//		camLayer2.setToOrtho(false, bWIDTH, bHEIGHT);
//		camLayer2.position.set(camLayer2.viewportWidth/2, camLayer2.viewportHeight/2, 0);
//		camLayer2.update();
		
		//paxCamera = new ParallaxCamera(false, bWIDTH*4, 4*bHEIGHT);
		paxCamera = new ParallaxCamera(false, bWIDTH, bHEIGHT);
		
		//paxCamera = new ParallaxCamera(bWIDTH, bHEIGHT);
		//paxCamera.setToOrtho(false, bWIDTH, bHEIGHT);
		paxCamera.position.set(paxCamera.viewportWidth/2, paxCamera.viewportHeight/2, 0);
		paxCamera.update();
		
		
		this.init();
	}
	
	public void init(){
		this.x = 0;
		this.y = 0;
		
		
		
		//layers = new TextureRegion[3];
		
		//layer1 = new Texture("back/background-1.png");
		//layer1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//layers[0] = new TextureRegion(layer1);
		
		//layer2 = new Texture("back/background-2.png");
		//layer2 = new Texture(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("pixel-back").getTexture().getTextureData());
		//layer2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//layer2.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		//layers[1] = new TextureRegion(layer2);
		
		//layer3 = new Texture("back/background-3.png");
		//layer3.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//layer3.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		//layers[2] = new TextureRegion(layer3);
		
		
		
		backSprite = new Sprite(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("pixel-back"));
		//backSprite = new Sprite(layer1);
		backSprite.setPosition(0, 0);
		//backSprite.setPosition(0, 0);
		backSprite.setSize(WIDTH, HEIGHT);
		
		buildDarkSprite  = new Sprite(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("back-small-front"));
		buildDarkSprite.setPosition(-bWIDTH/2, -bHEIGHT*0.6f);
		//buildDarkSprite.setPosition(-bWIDTH/2, 0);
		buildDarkSprite.setSize(bHEIGHT * buildDarkSprite.getWidth()/buildDarkSprite.getHeight(), bHEIGHT);

		buildLightSprite = new Sprite(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("back-small-mid"));
		buildLightSprite.setPosition(-bWIDTH/2, -bHEIGHT*0.6f);
		//buildDarkSprite.setPosition(-bWIDTH/2, 0);
		buildLightSprite.setSize(bHEIGHT * buildLightSprite.getWidth()/buildLightSprite.getHeight(), bHEIGHT);
		
		
		//cbuildLightSprite = new Sprite(buildLightSprite);
		//cbuildDarkSprite = new Sprite(buildDarkSprite);
		
		//cbuildLightSprite.setPosition(buildLightSprite.getX() + buildLightSprite.getWidth(), cbuildLightSprite.getY());
		//cbuildDarkSprite.setPosition(buildDarkSprite.getX() + buildDarkSprite.getWidth(), cbuildDarkSprite.getY());

		for(int i = 0;i<TOTAL_SPRITES;i++){
			buildDarkSprite.setPosition(-bWIDTH + (i * buildDarkSprite.getWidth()), buildDarkSprite.getY());
			paxLayer1.add(new Sprite(buildDarkSprite));
		}
		
		for(int i = 0;i<TOTAL_SPRITES;i++){
			buildLightSprite.setPosition(-bWIDTH + (i * buildLightSprite.getWidth()), buildLightSprite.getY());
			paxLayer2.add(new Sprite(buildLightSprite));
		}
		
		
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
	
	Vector3 temp = new Vector3(0,0,0);
	float time = 0;
	public void draw(SpriteBatch batch){
		//time += Gdx.graphics.getDeltaTime();
		//paxCamera.update();
		
		batch.disableBlending();		
		batch.setProjectionMatrix(GameScreen.getInstance().cameraui.combined);
		batch.begin();
		//batch.draw(layers[0], 0, 0);
		backSprite.draw(batch);
		//batch.end();
		
		//batch.setProjectionMatrix(GameScreen.getInstance().cameraui.combined);
		//batch.begin();
		//float layer3x = mainCam.position.x/55;
		//float layer2x = mainCam.position.x/45;


		//batch.draw(layer3, 0, 0, WIDTH, WIDTH * layer3.getHeight()/layer3.getWidth(), 0 + layer3x, 1, 1 + layer3x, 0);
		//batch.draw(layer2, 0, 0, WIDTH, WIDTH * layer2.getHeight()/layer2.getWidth(), 0 + layer2x, 1, 1 + layer2x, 0);

		//TODO
		batch.end();
		batch.enableBlending();

		//batch.setColor(1f,1f,1f,1f);
		if(GameScreen.BACKGROUND_PARALLAX){
			batch.setProjectionMatrix(paxCamera.calculateParallaxMatrix(0.2f, 0f));
			//batch.setProjectionMatrix(camLayer1.combined);
			batch.begin();
			//batch.draw(layers[1], 0, 0);
			//buildLightSprite.draw(batch);
			//cbuildLightSprite.draw(batch);
			for(Sprite s:paxLayer2)
			{
				//MyGame.sop(s.getX()+" - ori");
				//temp.set(s.getX(), s.getY(),0);
				//mainCam.unproject(temp);
				//MyGame.sop(temp.x+" - unprojected");
				
				//if((s.getX() > paxCamera.position.x  - bWIDTH*4) && (s.getX() + s.getWidth() - bWIDTH*4 < paxCamera.position.x))
				//if((temp.x > paxCamera.position.x  - bWIDTH*1) && (temp.x + s.getWidth() - bWIDTH*1 < paxCamera.position.x))
				if(s.getX() - bWIDTH/2 < paxCamera.position.x*0.2 && s.getX() + s.getWidth() + bWIDTH/2 > paxCamera.position.x*0.2)
					s.draw(batch);		
			}
			//batch.end();
			
			batch.setProjectionMatrix(paxCamera.calculateParallaxMatrix(0.4f, 0f));
			//batch.setProjectionMatrix(camLayer2.combined);
			//batch.begin();
			//batch.draw(layers[2], 0, 0);
			
			for(Sprite s:paxLayer1)
			{	
				//MyGame.sop(s.getX()+" - ori");
				//temp.set(s.getX(), s.getY(),0);
				//paxCamera.unproject(temp);
				//MyGame.sop(temp.x+" - unprojected");
				
				//temp.set(paxCamera.position.x, paxCamera.position.y, 0);
				//paxCamera.unproject(temp);
				//MyGame.sop(temp.x+" - screen");
				//MyGame.sop(paxCamera.position.x *0.4f +" - screen");

				if(s.getX() - bWIDTH/2 < paxCamera.position.x*0.4 && s.getX() + s.getWidth() + bWIDTH/2 > paxCamera.position.x*0.4)
					s.draw(batch);		
			}
			
			//cbuildDarkSprite.draw(batch);
			batch.end();
		}
		

	}
	
	public void update(float delta){
		// If the image scrolled off the screen, reset
		//MyGame.sop(paxCamera.position.x +":cam");
		
		/*
		if(paxCamera.position.x <= mainCam.position.x){
			//moving forward
			MyGame.sop("Moving forward");

			if (buildDarkSprite.getX() + buildDarkSprite.getWidth()  + bWIDTH/2 < paxCamera.position.x)
				buildDarkSprite.setPosition(cbuildDarkSprite.getX()+cbuildDarkSprite.getWidth(), buildDarkSprite.getY());
			
			if(cbuildDarkSprite.getX() + cbuildDarkSprite.getWidth()  + bWIDTH/2 < paxCamera.position.x)
				cbuildDarkSprite.setPosition(buildDarkSprite.getX()+buildDarkSprite.getWidth(), cbuildDarkSprite.getY());
			
			MyGame.sop("b:"+buildDarkSprite.getX()+" cb:"+cbuildDarkSprite.getX());

		}
		else
		{//moving backward
			MyGame.sop("Moving back");
			
			if(buildDarkSprite.getX() > cbuildDarkSprite.getX()){
				
				if(buildDarkSprite.getX() > paxCamera.position.x - bWIDTH*0.4f )
					cbuildDarkSprite.setPosition(buildDarkSprite.getX() - cbuildDarkSprite.getWidth(), cbuildDarkSprite.getY());
				
				MyGame.sop("old b:"+buildDarkSprite.getX());
				MyGame.sop("new Cb:"+cbuildDarkSprite.getX());
			}
			else
			{
				if(cbuildDarkSprite.getX() > paxCamera.position.x - bWIDTH*0.4f )
					buildDarkSprite.setPosition(cbuildDarkSprite.getX() - buildDarkSprite.getWidth(), buildDarkSprite.getY());
			
				MyGame.sop("new b:"+buildDarkSprite.getX());
				MyGame.sop("old Cb:"+cbuildDarkSprite.getX());
			}
			
			MyGame.sop("b:"+buildDarkSprite.getX()+" cb:"+cbuildDarkSprite.getX());
		}
		*/
		
		
		
		paxCamera.position.set(mainCam.position.x, mainCam.position.y, 0);
		/*
		camLayer1.position.set(mainCam.position.x * 0.2f, camLayer1.position.y, 0);
		camLayer1.update();

		camLayer2.position.set(mainCam.position.x * 0.5f, camLayer2.position.y, 0);
		camLayer2.update();
		*/
		
		
	}
	
	public void dispose(){
		paxLayer1.clear();
		paxLayer2.clear();
		
		//layer1.dispose();
		//layer2.dispose();
		//layer3.dispose();
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
