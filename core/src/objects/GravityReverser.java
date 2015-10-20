package objects;

import screens.GameScreen;
import utils.AssetLord;
import utils.LevelGenerate;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.softnuke.biosleep.MyGame;

public class GravityReverser {

	float width, height;
	private Vector2 position;
	
	Body body;
	World world;
	PolygonShape shape;
	Fixture bodyFixture;
	
	TextureRegion texRegion;
	Sprite fixSprite, flipSprite;
	static final Vector2 Center = new Vector2(0,0);

	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	ParticleEffect effect;
	Light lightE, lightD;
	Animation reverserAnime;
	float time = 0;
	
	//false means not visible
	public boolean visible = false;
	
	public static final int FLIPED = 1;
	public static final int FIXED = 2;
			
	//current type
	public int TYPE = FIXED;
	
	
	public GravityReverser(World wor, Vector2 pos, int type, Light le, Light ld){
		position = pos;
		height = 1f;
		width = 0.5f;
		lightE = le;
		lightD = ld;
		TYPE = type;
				
		init(wor);		
	}
	
	private void init(World w){
		world = w;
		
		visible = true;
//			if(POWER_TYPE == SCORE_BONUS)
//				texRegion = new TextureRegion(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.color_blocks_texture, Texture.class), 32*3, 32*1, 32, 32);
//			else if(POWER_TYPE == SHIELD)
//				texRegion = new TextureRegion(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.color_blocks_texture, Texture.class), 32*2, 32*1, 32, 32);
		
		//TextureAtlas atlas = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class);
		
		
				
		getTextures();
		
		create();
	}
	
	private void getTextures() {

		TextureRegion[] reverserSheet = new TextureRegion[2];
		
//		Texture texE = new Texture("level/gravity-reverser.png");
//		texE.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture texE2 = new Texture("level/gravity-reverser-2.png");
//		texE2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		reverserSheet[0] = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("gravity-reverser");//new TextureRegion(texE);
		reverserSheet[1] = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("gravity-reverser-2");//new TextureRegion(texE2);
		
		reverserAnime = new Animation(1f, reverserSheet);
		reverserAnime.setPlayMode(PlayMode.LOOP);
		
		//texD = new Texture("level/gravity-reverser.png");
		//texD.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		
		fixSprite = new Sprite(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("gravity-reverser"));
		fixSprite.setSize(width, width * fixSprite.getHeight()/fixSprite.getWidth());
		fixSprite.setPosition(position.x - width/2, position.y - fixSprite.getHeight()/2 );
		
		flipSprite = new Sprite(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("gravity-reverser"));
		flipSprite.setSize(width, width * flipSprite.getHeight()/flipSprite.getWidth());	
		flipSprite.setOrigin(flipSprite.getWidth()/2, 0);
		flipSprite.setPosition(position.x - width/2, position.y - flipSprite.getHeight()/2);
		
		//TODO: remove this
		flipSprite.setFlip(false, true);
		
		
		
		if(GameScreen.PLAYER_PARTICLES){
			//TODO:change this later
			effect = new ParticleEffect(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.gravity_rev_particle, ParticleEffect.class));
			effect.scaleEffect(0.5f);
			if(TYPE != FIXED)
				effect.setPosition(position.x , position.y + fixSprite.getHeight()/2);
			else
				effect.setPosition(position.x , position.y - fixSprite.getHeight()/2);
			
			effect.start();
			effect.setEmittersCleanUpBlendFunction(false);

		}
	}

	private void create(){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;		
		
		if(TYPE != FIXED)
			bodyDef.position.set(position.x, position.y + height/2);
		else
			bodyDef.position.set(position.x, position.y - height/2);
		
		
		
		shape = new PolygonShape();
		shape.setAsBox(width/2, height/2, Enemy.CENTER_VECTOR, 0);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density =  0.0f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0;		
		fixtureDef.isSensor = true;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_WALL;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_PLAYER);
		
		body = world.createBody(bodyDef);		
		bodyFixture = body.createFixture(fixtureDef);
			
		body.setUserData("gravityReverser");		
		shape.dispose();
		
	}
	
	public void render(SpriteBatch batch){		

		if(!visible)
			return;
		
		//check if it is gone off the screen without user consumption
		//if(body.getPosition().y+height < -bWIDTH)
		//	setOffScreen(false);
		
		//batch.draw(texRegion, position.x-width/2, position.y-height/2, 0, 0, width, height, 1f, 1f, 0);
		
		fixSprite.setRegion(reverserAnime.getKeyFrame(time));
		flipSprite.setRegion(reverserAnime.getKeyFrame(time));
		
		fixSprite.setSize(width, width * fixSprite.getHeight()/fixSprite.getWidth());
		fixSprite.setPosition(position.x - width/2, position.y - fixSprite.getHeight()/2 );
		
		flipSprite.setSize(width, width * flipSprite.getHeight()/flipSprite.getWidth());	
		flipSprite.setOrigin(flipSprite.getWidth()/2, 0);
		flipSprite.setPosition(position.x - width/2, position.y - flipSprite.getHeight()/2);
		
		//TODO: remove this
		flipSprite.setFlip(false, true);
		if(TYPE != FIXED)
			fixSprite.draw(batch);
		else
			flipSprite.draw(batch);
		
		

	}
	
	public void renderParticles(SpriteBatch batch){
		if(!visible)
			return;
		
		effect.draw(batch);

	}
	
	public void update(float delta, float viewportWidth){
		time += delta;
		
		position = body.getPosition();
		
		if(position.x > viewportWidth-bWIDTH*0.8 && position.x < viewportWidth+bWIDTH*0.8)
			visible = true;
		else
			visible = false;
		

				
		effect.update(delta);
	}
	
	public void reset(){
	
		visible = true;
		effect.start();
		
		lightE.disable();
		lightD.enable();
		
		if(true)
		return;
		
		
	}
	
	public Fixture getFixture(){
		return bodyFixture;
	}
			
	public void setOffScreen(boolean collision){
		//hide bodies
		
		visible = false;
		
				
		if(GameScreen.PLAYER_PARTICLES){
			effect.allowCompletion();
		}
		

	}
	
	
//	public void updatePreviousPos() {
//		previous = body.getPosition();
//	}
//	
//	public void interpolate(float alpha, float invAlpha){
//		Vector2 pos = body.getPosition();
//		posx = pos.x * alpha + previous.x * invAlpha;
//		posy = pos.y * alpha + previous.y * invAlpha;	
//	}
	
	public float getY(){
		return position.y;
	}
	
	public float getX(){
		return position.x;
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public void dispose(){
		world.destroyBody(body);
		
		if(GameScreen.PLAYER_PARTICLES){
			effect.dispose();
		}
	}
}
