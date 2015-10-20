package objects;

import screens.GameScreen;
import utils.AssetLord;
import utils.LevelGenerate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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

public class BeamSpot {

	float width, height;
	private Vector2 position;
	
	Body body;
	World world;
	PolygonShape shape;
	Fixture bodyFixture, fireFixture;
	
	TextureRegion texE,texD;
	Sprite bodySprite;
	static final Vector2 Center = new Vector2(0,0);

	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	ParticleEffect effect;
	Light light;
	//false means not visible
	public boolean visible = false;
	
	//current type
	public boolean STATE_ENABLED = false;
	
	Sprite glow, ray;

	public Color color = new Color(Color.RED);
	public Color rayColor = new Color(Color.WHITE);
	
	public float delay = 2f;
	public float totalTime = 0;
	public float fireTime = 0;
	
	public boolean CAN_KILL = true;
	public boolean PLAYER_INSIDE = false;
	
	public BeamSpot(World wor, Vector2 pos, Light le){
		position = pos;
		height = 0.3f;
		width = 1.4f;
		light = le;
				
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
		
		texE = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("beamSpot-enabled");
		
		texD = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("beamSpot-disabled");
		
		
		bodySprite = new Sprite(texE);
		bodySprite.setSize(width, width * bodySprite.getHeight()/bodySprite.getWidth());
		bodySprite.setPosition(position.x - width/2, position.y - height*0.8f);
		
		
		if(GameScreen.PLAYER_PARTICLES){
			//TODO:change this later
			effect = new ParticleEffect(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.beamspot_particle, ParticleEffect.class));
			effect.scaleEffect(0.6f);
			effect.setEmittersCleanUpBlendFunction(false);

			effect.setPosition(position.x - width/3, position.y + height/2);
			effect.start();

		}
		
		//reset lights
		light.enable();
		
		create();
	}
	
	private void create(){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;		
		bodyDef.position.set(position);
		
		
		shape = new PolygonShape();
		shape.setAsBox(width/2, height/2, Enemy.CENTER_VECTOR, 0);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density =  0.0f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0;		
		fixtureDef.isSensor = false;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_WALL;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_PLAYER);
		
		body = world.createBody(bodyDef);		
		bodyFixture = body.createFixture(fixtureDef);
		
		//fire sensor
		shape.setAsBox(width*0.3f, bHEIGHT*0.4f, new Vector2(0, bHEIGHT*0.4f), 0);
		fixtureDef.shape = shape;
		fixtureDef.isSensor = true;
		fireFixture = body.createFixture(fixtureDef);
		
		body.setUserData("beamSpot");		
		shape.dispose();
		
	}
	
	public void render(SpriteBatch batch){	
		//if(CAN_KILL)

		if(!visible)
			return;
		
		//check if it is gone off the screen without user consumption
		//if(body.getPosition().y+height < -bWIDTH)
		//	setOffScreen(false);
		
		//batch.draw(texRegion, position.x-width/2, position.y-height/2, 0, 0, width, height, 1f, 1f, 0);
		if(CAN_KILL)
			bodySprite.setRegion(texE);
		else
			bodySprite.setRegion(texD);
		
		bodySprite.draw(batch);
	}
	
	public void renderParticles(SpriteBatch batch){
		effect.draw(batch);	
	}
	
	public void update(float delta, float viewportWidth){
		totalTime += delta;
		fireTime += delta;
		
		if(fireTime > delay){
			CAN_KILL = !CAN_KILL;
			fireTime = 0;
			
			if(CAN_KILL)
				effect.start();
			else
				effect.allowCompletion();
		}
		
		position = body.getPosition();
		
		if(position.x > viewportWidth-bWIDTH*0.8 && position.x < viewportWidth+bWIDTH*0.8)
			visible = true;
		else
			visible = false;
		
		
		//check if player is inside, if yes, kill
		if(PLAYER_INSIDE && CAN_KILL)
			Player.getInstance().setDeath();
				
		effect.update(delta);
	}
	
	public void reset(){
	
		visible = true;
		//effect.start();
		STATE_ENABLED = false;
		light.disable();
		
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
	
	public void toggle(){
		STATE_ENABLED = !STATE_ENABLED;
		
		
	}
	
	public Fixture getFireSensor() {
		return fireFixture;
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