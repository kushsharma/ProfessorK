package objects;

import screens.GameScreen;
import utils.AssetLord;
import utils.LevelGenerate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.softnuke.biosleep.MyGame;

public class PowerUp {
	//variables in Tiled
	//level : 1/2
	
	float width, height, posx, posy;
	private Vector2 fixed = new Vector2(0,0);

	Body body;
	World world;
	Shape shape;
	BodyDef bodyDef;
	FixtureDef fixtureDef;
	Fixture bodyFixture;
	TextureRegion texRegion;
	Sprite powerSprite;
	static final Vector2 Center = new Vector2(0,0);

	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	ParticleEffect effect;
	Light light;
	//false means not visible
	public boolean visible = false;
	public boolean consumed = false;
	
	//types of power
	//0 - lowest and 2 - highest
	public static int LEVEL_ZERO = 0; // default
	public static int LEVEL_ONE = 1;
	public static int LEVEL_TWO = 2;
	
	//current type
	public int POWER_TYPE = LEVEL_ONE;
	
	public float flyLevel = 0.1f;
	
	public PowerUp(World w, Shape s, int power, float x, float y, Light l){
		POWER_TYPE = power;
		shape = s;
		light = l;
		
		init(w, x, y);		
	}
	
	private void init(World w, float x, float y){
		world = w;
		width = 1f;
		height = 1f;
		posx = x;		
		posy = y;
		fixed.set(posx, posy);
		
		consumed = false;
		visible = true;
//			if(POWER_TYPE == SCORE_BONUS)
//				texRegion = new TextureRegion(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.color_blocks_texture, Texture.class), 32*3, 32*1, 32, 32);
//			else if(POWER_TYPE == SHIELD)
//				texRegion = new TextureRegion(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.color_blocks_texture, Texture.class), 32*2, 32*1, 32, 32);
		
		//TextureAtlas atlas = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class);
		
//		Texture tex = new Texture("level/milk-power.png");
//		tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture texMega = new Texture("level/milk-powerful.png");
//		texMega.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		if(POWER_TYPE == LEVEL_ZERO)
			texRegion = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("milk-power");//new TextureRegion(tex);
		else if(POWER_TYPE == LEVEL_ONE)
			texRegion = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("milk-power");//new TextureRegion(tex);
		else
			texRegion = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class).findRegion("milk-powerful");//new TextureRegion(texMega);
		
		powerSprite = new Sprite(texRegion);
		powerSprite.setSize(height * powerSprite.getWidth()/powerSprite.getHeight(), height);
		
		if(GameScreen.PLAYER_PARTICLES){
			//change this later
			effect = new ParticleEffect(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.enemy_kill_particle, ParticleEffect.class));
			//effect.scaleEffect(MyGame.PTP);
			effect.setPosition(posx, posy);
		
			effect.setEmittersCleanUpBlendFunction(false);

		}
		
		create();
	}
	
	private void create(){
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		
		bodyDef.position.x = posx;		
		bodyDef.position.y = posy ;
		
		//PolygonShape PS = (PolygonShape) shape;
		//shape.setAsBox(width/2, height/2);
		//PS.setAsBox(width/2, height/2, Center, 0);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density =  0.5f;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0;		
		fixtureDef.isSensor = true;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_POWERUP;
		fixtureDef.filter.maskBits = LevelGenerate.CATEGORY_PLAYER;
		
		body = world.createBody(bodyDef);		
		bodyFixture = body.createFixture(fixtureDef);
		
		//body.setTransform(posx-width/2, posy-height/2, 0);
		
		body.setUserData("powerUp");
		
//		if(POWER_TYPE == LEVEL_ONE)
//		else if(POWER_TYPE == LEVEL_TWO)
//			body.setUserData("powerTwo");
//		else
//			body.setUserData("powerUltra");
		
		shape.dispose();
		
		//send it away
		//setOffScreen(false);
	}
	
	public void render(SpriteBatch batch){		
		//posx = body.getPosition().x;
		//posy = body.getPosition().y;		

		if(!visible)
			return;
		
		//check if it is gone off the screen without user consumption
		//if(body.getPosition().y+height < -bWIDTH)
		//	setOffScreen(false);
		
		//batch.draw(texRegion, posx-width/2, posy-height/2, 0, 0, width, height, 1f, 1f, 0);
		powerSprite.setPosition(posx - powerSprite.getWidth()/2, posy - powerSprite.getHeight()/2);
		powerSprite.draw(batch);

	}
	
	public void renderParticles(SpriteBatch batch){
		effect.draw(batch);
		
	}
	
	private boolean up = true;
	public void update(float delta, float viewportWidth){
		posx = body.getPosition().x;
		posy = body.getPosition().y;
		
		if(!consumed && (posx > viewportWidth-bWIDTH/2 && posx < viewportWidth+bWIDTH/2))
			visible = true;
		else
			visible = false;
		

		//make it hover
		if((fixed.y + flyLevel) - body.getPosition().y < 0.00002f)
			up = false;
		else if(body.getPosition().y - (fixed.y - flyLevel) < 0.00002f)
			up = true;
		
		int sign = (up) ? 1 : -1;
		
		float y = body.getPosition().y + (0.2f * sign) * delta;		
		body.setTransform(body.getPosition().x, y, 0);
		
		if(GameScreen.PLAYER_PARTICLES)
		effect.update(delta);
	}
	
	public void reset(){
	
		consumed = false;
		visible = true;
		
		if(true)
		return;
		//wallTexx = AssetLord.manager.get(AssetLord.grey_tile_tex, Texture.class);
		//effect = AssetLord.manager.get(AssetLord.meteor_particle, ParticleEffect.class);
		
		//check if already on screen
		
		//if(body.getPosition().y > 0 && body.getPosition().y<bWIDTH)
		if(visible)	
			return;
		
		body.setActive(true);
		
		//posy = generateY();
		
		//float wallHalf = (bWIDTH + bWIDTH/3)/4f;		
		//posx = MathUtils.random(wallHalf, bWIDTH - wallHalf);
		
		body.setTransform(posx, posy, 0);
		
		
		visible = true;
		
		if(GameScreen.PLAYER_PARTICLES){
			effect.allowCompletion();
			effect.start();
		}
	}
	
	/**consume power and return its type**/
	public int consume(){
		if(Player.getInstance().getEvolution() == POWER_TYPE - 1)
		{
			visible = false;
			consumed = true;
			light.disable();
			
			if(GameScreen.PLAYER_PARTICLES)
			effect.start();
			
			return POWER_TYPE;
		}
		else
			return 0;
	}
	
	public Fixture getFixture(){
		return bodyFixture;
	}
	
	private float generateY(){
		return MathUtils.random(bWIDTH*1.2f, bWIDTH*1.5f);
	}
		
	public void setOffScreen(boolean collision){
		//hide bodies
		
		visible = false;
		
		//body.applyForceToCenter(0, -100 , false);
		//body.applyLinearImpulse(10, -30, 0, 0, false);
		
		if(!collision)
		{
			body.setTransform(posx, -bWIDTH, 0);
			body.setActive(false);
		}
		
		
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
		return posy;
	}
	
	public float getX(){
		return posx;
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
