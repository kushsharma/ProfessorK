package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.softnuke.biosleep.MyGame;

import screens.GameScreen;
import utils.AssetLord;
import utils.LevelGenerate;

public class Boss extends GameObject{
	
	int level = LevelGenerate.CURRENT_LEVEL;
	int HEALTH;
	float Speed = 1f;
	float distance = 0;
	float fire_time = 0;
	
	boolean FIRING = false;
	boolean CAN_FIRE = false;
	public boolean PLAYER_INSIDE = false;

	Animation laserAnime;
	Sprite laserSprite, playerSprite;
	Light light;
	ParticleEffect killParticle;
	TextureRegion hitTexR;
	boolean SHOW_HIT = false;
	
	public Boss(World w, Vector2 pos, Light l){
		world = w;
		startPos = pos.cpy();
		light = l;
		
		position = startPos.cpy();
		
		switch(level){
			default: createBoss1();
		}
	}

	private void createBoss1() {
		SCORE_VALUE = 500;
		HEALTH = 200;
		width = 2.2f;
		height = 2.2f;
		Speed = 0.7f;
		distance = 1.5f;
		visible = true;
		
		//particle
		killParticle = new ParticleEffect(gameScreen.getAssetLord().manager.get(AssetLord.enemy_kill_particle,ParticleEffect.class));
		killParticle.scaleEffect(5f);
		killParticle.setEmittersCleanUpBlendFunction(false);
		
		//Texture texh1 = new Texture("level/boss1-hit-1.png");
		//texh1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);		
		hitTexR = new TextureRegion(atlas.findRegion("boss1-hit-1"));
		
		TextureRegion laserSheet[] = new TextureRegion[2];
		//Texture texl1 = new Texture("level/boss1-laser-1.png");
		//texl1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//Texture texl2 = new Texture("level/boss1-laser-2.png");
		//texl2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		laserSheet[0] = new TextureRegion(atlas.findRegion("boss1-laser-1"));
		laserSheet[1] = new TextureRegion(atlas.findRegion("boss1-laser-2"));
		laserAnime = new Animation(1f, laserSheet);
		laserAnime.setPlayMode(PlayMode.NORMAL);
		
		TextureRegion idleSheet[] = new TextureRegion[2];
		//Texture tex1 = new Texture("level/boss1-idle-1.png");
		//tex1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//Texture tex2 = new Texture("level/boss1-idle-2.png");
		//tex2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		idleSheet[0] = new TextureRegion(atlas.findRegion("boss1-idle-1"));
		idleSheet[1] = new TextureRegion(atlas.findRegion("boss1-idle-2"));
		
		idleAnime = new Animation(0.2f, idleSheet);
		idleAnime.setPlayMode(PlayMode.LOOP);
		
		laserSprite = new Sprite(laserAnime.getKeyFrame(0));
		laserSprite.setSize(bWIDTH * 0.9f, height);
		laserSprite.setPosition(position.x - laserSprite.getWidth() - width/5, position.y - laserSprite.getHeight()/2 + height/12);
		
		playerSprite = new Sprite(idleAnime.getKeyFrame(0));
		playerSprite.setSize(width, height);
		playerSprite.setPosition(position.x, position.y);
		
		
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		
		bodyDef.position.set(position);
		
		shape = new PolygonShape();
		((PolygonShape) shape).setAsBox(width/2, height/2, CENTER_VECTOR, 0);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density =  0.1f;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0;		
		fixtureDef.isSensor = false;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_BADBOY;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_PLAYER | LevelGenerate.CATEGORY_BULLET);
		
		body = world.createBody(bodyDef);		
		bodyFixture = body.createFixture(fixtureDef);
		
		body.setLinearVelocity(body.getLinearVelocity().x, Speed);
		
		body.setUserData("boss1");
		
		
		
		//create laser fixture
		((PolygonShape) shape).setAsBox(laserSprite.getWidth()/2, height/2, new Vector2(-laserSprite.getWidth()/2, 0), 0);
		fixtureDef.shape = shape;		
		fixtureDef.isSensor = true;		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_BADBOY;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_PLAYER);
		
		sensorFixture = body.createFixture(fixtureDef);
		
		
		shape.dispose();
	}
	
	public void render(SpriteBatch batch){
		if(!visible || DEAD) return;
		
		playerSprite.setRegion(idleAnime.getKeyFrame(time));
		
		if(SHOW_HIT){
			playerSprite.setRegion(hitTexR);
			SHOW_HIT = false;
		}
		
		playerSprite.draw(batch);

		laserSprite.setRegion(laserAnime.getKeyFrame(fire_time));		
		if(FIRING)
			laserSprite.draw(batch, 0.8f);
		

	}
	
	public void renderParticles(SpriteBatch batch){
		killParticle.setPosition(position.x, position.y);
		if(GameScreen.PLAYER_PARTICLES)
			killParticle.draw(batch);
	}
	
	float downPause = 0, upPause = 0;
	
	public void update(float delta, float viewportWidth){
		time += delta;
		if(FIRING){
			fire_time += delta;
		
			//stop firing after 2 sec
			if(fire_time > 2)
			{
				FIRING = false;
				fire_time = 0;
			}
		}
				
		position.set(body.getPosition());
		playerSprite.setPosition(body.getPosition().x - playerSprite.getWidth()/2, body.getPosition().y- playerSprite.getHeight()/2);
		laserSprite.setPosition(laserSprite.getX(), body.getPosition().y - laserSprite.getHeight()/2 + height/12);
		light.setPosition(position.x, position.y);
		
		//culling
		if(position.x > viewportWidth-bWIDTH*0.8 && position.x < viewportWidth+bWIDTH*0.8)
			visible = true;
		else
			visible = false;

		//check for player camera position
		if(!DEAD){
			if(position.x - viewportWidth < bWIDTH/2)
			{
				CAN_FIRE = true;
			}
			else
				CAN_FIRE = false;
		}
		
		//make ping pong vertical effect
		if((startPos.y + distance) - body.getPosition().y < 0.00002f)
		{
			if(body.getLinearVelocity().y == 0)
				upPause += delta;
			body.setLinearVelocity( body.getLinearVelocity().x , 0);
			FIRING = true;

			if(upPause > 2.2f)			
			{
				body.setLinearVelocity( body.getLinearVelocity().x , -Speed);
				upPause = 0;
				FIRING = false;
			}
		}
		else if(body.getPosition().y - (startPos.y + height/2 - distance) < 0.00002f)
		{
			if(body.getLinearVelocity().y == 0)
				downPause += delta;
			body.setLinearVelocity( body.getLinearVelocity().x , 0);
			FIRING = true;

			if(downPause > 2.2f)			
			{
				body.setLinearVelocity( body.getLinearVelocity().x , Speed);
				downPause = 0;
				FIRING = false;

			}
		}
		
		
		if(!visible || DEAD || !CAN_FIRE){
			FIRING = false;
			fire_time = 0;
		}	
		
		if(FIRING)
			gameScreen.shakeThatAss();
		
		if(GameScreen.PLAYER_PARTICLES)
			killParticle.update(delta);
			
		//check if player is inside, if yes, kill
		if(PLAYER_INSIDE && FIRING)
			Player.getInstance().setDeath();
	}
	
	public void reset(){
		FIRING = DEAD = CAN_FIRE = PLAYER_INSIDE = false;
		
		body.setTransform(position, 0);
		body.setLinearVelocity(body.getLinearVelocity().x, Speed);
		HEALTH = 200;
	}
	
	public void hitBullet(int val){
		if(!CAN_FIRE) return;
		
		if(HEALTH > val)
			HEALTH -= val;
		else
			setDead();
		
		SHOW_HIT = true;
	}
	
	public void shoot(){
		FIRING = true;
	}
	
	public void setDead(){
		DEAD = true;
		
		//unlock camera
		gameScreen.cameraLock(false);
		
		//unlock portal
		LevelGenerate.getInstance().toggleExitPortalSwitch(true);
		
		if(GameScreen.PLAYER_PARTICLES)
			killParticle.start();
		
		//give score
		GameScreen.getInstance().increaseScore(SCORE_VALUE);
	}
	
	public void setOffScreen(){
		if(!DEAD) return;
		
		if(GameScreen.PLAYER_PARTICLES)
			killParticle.start();

		visible = false;
		body.setTransform(-bWIDTH, -bHEIGHT, 0);
		body.setLinearVelocity(0, 0);
	}
	
	public void dispose(){
		world.destroyBody(body);
		
		if(GameScreen.PLAYER_PARTICLES){
			killParticle.dispose();
		}
	}

}
