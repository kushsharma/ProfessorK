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

public class Lasers {
//TODO: under construction, incomplete
	
	float width, height;
	private Vector2 position;
	
	Body body;
	World world;
	PolygonShape shape;
	Fixture bodyFixture;
	
	TextureRegion texRegion;
	Sprite onSprite, offSprite;
	static final Vector2 Center = new Vector2(0,0);

	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	ParticleEffect effect;
	Light lightE, lightD;
	//false means not visible
	public boolean visible = false;
	
	//current type
	public boolean STATE_ENABLED = false;
	
	Sprite glow, ray;

	public Color color = new Color(Color.RED);
	public Color rayColor = new Color(Color.WHITE);
	
	public Lasers(World wor, Vector2 pos, Light le, Light ld){
		position = pos;
		height = 0.6f;
		width = 0.6f;
		lightE = le;
		lightD = ld;
				
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
		
		Texture texE = new Texture("level/switch-enabled.png");
		texE.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		Texture texD = new Texture("level/switch-disabled.png");
		texD.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		
		onSprite = new Sprite(texE);
		onSprite.setSize(width, width * onSprite.getHeight()/onSprite.getWidth());
		
		offSprite = new Sprite(texD);
		offSprite.setSize(width, width * offSprite.getHeight()/offSprite.getWidth());	
		
		onSprite.setPosition(position.x - width/2, position.y - height/2);
		offSprite.setPosition(position.x - width/2, position.y - height/2);
		
		if(GameScreen.PLAYER_PARTICLES){
			//TODO:change this later
			effect = new ParticleEffect(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.portal_particle, ParticleEffect.class));
			effect.scaleEffect(0.5f);
			effect.setPosition(position.x - width/6, position.y + height/2);
			//effect.start();
			//effect.setEmittersCleanUpBlendFunction(false);

		}
		
		//reset lights
		lightE.enable();
		lightD.disable();
		
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
		fixtureDef.isSensor = true;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_WALL;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_PLAYER);
		
		body = world.createBody(bodyDef);		
		bodyFixture = body.createFixture(fixtureDef);
			
		body.setUserData("switch");		
		shape.dispose();
		
	}
	
	public void render(SpriteBatch batch){		
		//effect.draw(batch);

		if(!visible)
			return;
		
		//check if it is gone off the screen without user consumption
		//if(body.getPosition().y+height < -bWIDTH)
		//	setOffScreen(false);
		
		//batch.draw(texRegion, position.x-width/2, position.y-height/2, 0, 0, width, height, 1f, 1f, 0);
		if(STATE_ENABLED)
			onSprite.draw(batch);
		else
			offSprite.draw(batch);
	}
	
	public void renderParticles(SpriteBatch batch){
		if(!visible)
			return;
		
		
	}
	
	public void update(float delta, float viewportWidth){
		position = body.getPosition();
		
		if(position.x > viewportWidth-bWIDTH*0.8 && position.x < viewportWidth+bWIDTH*0.8)
			visible = true;
		else
			visible = false;
		

				
		//effect.update(delta);
	}
	
	public void reset(){
	
		visible = true;
		//effect.start();
		STATE_ENABLED = false;
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
	
	public void toggle(){
		STATE_ENABLED = !STATE_ENABLED;
		
		if(STATE_ENABLED){
			lightE.enable();
			lightD.disable();			
		}
		else{
			lightE.disable();
			lightD.enable();
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
