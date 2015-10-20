package objects;

import screens.GameScreen;
import utils.AssetLord;
import utils.LevelGenerate;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

//TODO: incomplete
public class FallingPlatform {
	//platforms but fall down
	float width, height;
	public float Speed = 1.5f;
	Vector2 position;
	float distance = 0;
	private Vector2 fixed;

	Body body;
	World world;
	PolygonShape shape;
	BodyDef bodyDef;
	FixtureDef fixtureDef;
	Fixture bodyFixture;
	TextureRegion texRegion;
	static final Vector2 Center = new Vector2(0,0);

	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	ParticleEffect effect;
	
	//false means not visible
	public boolean visible = false;
		
	
	public FallingPlatform(World w, Vector2 pos, float size){
		world = w;
		distance = size/2;
		position = pos;
		
		init(w);		
	}
	
	private void init(World w){
		world = w;
		width = 1.5f;
		height = 0.3f;
		fixed = position.cpy();
		
		visible = true;
		
		Texture tex = new Texture("level/mover.png");
		tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		texRegion = new TextureRegion(tex);
		
		
		if(GameScreen.PLAYER_PARTICLES){
			//change this later
			effect = new ParticleEffect(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.enemy_kill_particle, ParticleEffect.class));
			//effect.scaleEffect(MyGame.PTP);
			effect.setPosition(position.x, position.y);
		
			//effect.setEmittersCleanUpBlendFunction(false);

		}
		
		create();
	}
	
	private void create(){
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		
		bodyDef.position.set(position);
		
		shape = new PolygonShape();
		shape.setAsBox(width/2, height/2, Center, 0);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density =  0.1f;
		fixtureDef.friction = 0.95f;
		fixtureDef.restitution = 0;		
		fixtureDef.isSensor = false;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_WALL;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_PLAYER | LevelGenerate.CATEGORY_BADBOY);
		
		body = world.createBody(bodyDef);		
		bodyFixture = body.createFixture(fixtureDef);
		
		//body.setTransform(posx-width/2, posy-height/2, 0);
		body.setLinearVelocity(body.getLinearVelocity().x, Speed);
		
		body.setUserData("fallingPlatform");
		
		shape.dispose();
		
	}
	
	public void render(SpriteBatch batch){		
		//posx = body.getPosition().x;
		//posy = body.getPosition().y;		
		effect.draw(batch);

		if(!visible)
			return;
		
		//check if it is gone off the screen without user consumption
		//if(body.getPosition().y+height < -bWIDTH)
		//	setOffScreen(false);
		
		batch.draw(texRegion, position.x-width/2, position.y-height/2, 0, 0, width, height, 1f, 1f, 0);
		

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
		

		//make it hover
		
		if((fixed.y - height/2 + distance) - body.getPosition().y < 0.00002f)
			body.setLinearVelocity( body.getLinearVelocity().x , -Speed);
		else if(body.getPosition().y - (fixed.y + height/2 - distance) < 0.00002f)
			body.setLinearVelocity( body.getLinearVelocity().x , Speed);		
		
		
		effect.update(delta);
	}
	
	public void reset(){
	
		visible = true;
		
		if(true)
		return;
				
			
		if(GameScreen.PLAYER_PARTICLES){
			effect.allowCompletion();
			effect.start();
		}
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
	
	public float getDistance(){
		return distance;
	}
	
	public void dispose(){
		world.destroyBody(body);
		
		if(GameScreen.PLAYER_PARTICLES){
			effect.dispose();
		}
	}

}
