package objects;

import java.util.HashMap;

import screens.GameScreen;
import utils.AssetLord;
import utils.LevelGenerate;
import utils.MyInputProcessor;
import utils.MyInputProcessor.CONTROL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.softnuke.biosleep.MyGame;


public class Player {

	public static Player _player = null;
	private boolean IMMORTAL = false;
	private boolean PLAYER_DRAW = true;
	
	private float height = 1.0f, width = 0.5f;
	private Vector2 position = new Vector2();
	Vector2 startPos = new Vector2();
	private float Speed = 4.6f;
	private float Jump = 1.8f;
	private float gravityScale = 0f;
	
	private boolean GLOWING = false;
	private boolean FLYING = false; // disable player gravity effect
	private float flightTime = 0f;
	private boolean visible = true;
	public boolean Revived = false;
	public int JUMP_DAMAGE = 2;
	
	private boolean DEAD = false; //reset game
	public boolean CONTROLS = true; //player can/can't be controlled
	public boolean GOT_HIT = false;
	public boolean TELEPORTING_OUT = false; //going out of stage
	public boolean TELEPORTING_IN = true; //coming in stage
	public static final float WIN_CAM = 0.8f;	//time before teleport
	public static final float DEATH_CAM = 0.4f;	//time before final death
	
	public boolean CAN_JUMP = true;
	public boolean CAN_FIRE = true;
	
	public boolean LEFT_DIRECTION = false;
	public int PLAYER_EVOLUTION = PowerUp.LEVEL_ZERO;
	
	GameScreen gameScreen = GameScreen.getInstance();
		
	Body body;
	World world;
	PolygonShape shape;
	BodyDef bodyDef;
	Fixture bodyFixture, sensorFixture;
	
	TextureRegion playerTexR;
	Texture playerTex;
	Sprite playerSprite, hitSprite;
	Sprite glow;
	ParticleEffect jumpParticle;
	
	private Vector2 WorldGravityNegative = new Vector2(GameScreen.WorldGravity.x *-1, 0);
	
	boolean ASIDE = true;//player on Wall A or B, true means A 
	
	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	float time = 0f;
	float lastJumpTime = 0;
	float deathClock = 0;
	float winClock = 0;
	Animation idleAnime, moveAnime, jumpAnime, fireAnime, fireIdleAnime, fireMoveAnime, megaIdleAnime, megaMoveAnime, hitAnime, teleportOut, teleportIn;

	AssetLord Assets = GameScreen.getInstance().getAssetLord();
	TextureAtlas gameAtlas;
	public HashMap<CONTROL, Boolean> pKeys = null;
	
	public Player(World w){
		_player = this;
		world = w;		
		
		position.x = startPos.x = bWIDTH/4 ;
		position.y = startPos.y = bHEIGHT/2  - height/2;
		init();
	}
	
	public void init(){
		bodyDef =  new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		
		bodyDef.position.x = position.x;
		bodyDef.position.y = position.y;
		
		shape = new PolygonShape();
		shape.setAsBox((width*0.75f)/2, (height*0.9f)/2); // body
				
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density =  0.6f;
		fixtureDef.friction = 0.5f; // experimental, check if its works fine
		fixtureDef.restitution = 0;		
		fixtureDef.isSensor = false;
		
		fixtureDef.filter.categoryBits = LevelGenerate.CATEGORY_PLAYER;
		fixtureDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_WALL | LevelGenerate.CATEGORY_POWERUP | LevelGenerate.CATEGORY_BADBOY);
		
		CircleShape cs = new CircleShape();
		cs.setRadius((width*0.7f)/2);
		cs.setPosition(new Vector2(0, -height/3)); // legs

		FixtureDef sensorDef = new FixtureDef();
		sensorDef.shape = cs;
		sensorDef.isSensor = true;
		sensorDef.filter.categoryBits = LevelGenerate.CATEGORY_PLAYER;
		sensorDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_WALL | LevelGenerate.CATEGORY_BADBOY);
		
		PolygonShape handShape = new PolygonShape(); //side bars
        float[] vertices = new float[8];
        vertices[0] = -(width*0.75f)/2f;
        vertices[1] = -(height*0.85f)/2f;
        vertices[2] = -(width*0.75f)/2f;
        vertices[3] = (height*0.85f)/2f;
        vertices[4] = (width*0.75f)/2f + 0.01f;
        vertices[5] = (height*0.75f)/2f;
        vertices[6] = (width*0.75f)/2f + 0.01f;
        vertices[7] = -(height*0.75f)/2f;
		handShape.set(vertices);
		//handShape.setAsBox(0.01f, (height*0.85f)/2, new Vector2((width*0.75f)/2, 0), 0);
		
		FixtureDef sideBodyDef = new FixtureDef();
		sideBodyDef.isSensor = false;
		sideBodyDef.shape = handShape;
		sideBodyDef.friction = 0;
		sideBodyDef.density  = 0.02f;
		sideBodyDef.restitution = 0;
		sideBodyDef.filter.categoryBits = LevelGenerate.CATEGORY_PLAYER;
		sideBodyDef.filter.maskBits = (short) (LevelGenerate.CATEGORY_WALL);
		
		body = world.createBody(bodyDef);
		bodyFixture = body.createFixture(fixtureDef);
		sensorFixture = body.createFixture(sensorDef);
		body.createFixture(sideBodyDef);
		
		handShape.setAsBox(0.01f, (height*0.85f)/2, new Vector2(-(width*0.75f)/2, 0), 0);
		sideBodyDef.shape = handShape;
		body.createFixture(sideBodyDef);
		
		
		body.setLinearVelocity(0, 0);
		body.setFixedRotation(true);
		
		body.setUserData("player");
		
		gravityScale = body.getGravityScale();
		
		handShape.dispose();
		cs.dispose();
		shape.dispose();
		
		//textures
		gameAtlas = Assets.manager.get(AssetLord.game_atlas, TextureAtlas.class);

		//Texture tex = new Texture("level/player-idle-1.png");
		playerSprite = new Sprite(gameAtlas.findRegion("character-idle"));
		getSkinTexture();		
		//playerSprite.setSize(height*1.2f * playerSprite.getWidth()/playerSprite.getHeight(), height*1.2f);
		playerSprite.setSize(width*3f, height * 1.2f);
		playerSprite.setOrigin(playerSprite.getWidth()/2, playerSprite.getHeight()/2);

		hitSprite = new Sprite();
		hitSprite.setSize(width*3f, height * 1.2f); //.setSize(width*1.5f, width*1.5f);
		hitSprite.setOrigin(hitSprite.getWidth()/2, hitSprite.getHeight()/2);
		// * hitSprite.getHeight()/hitSprite.getWidth()
		
		//particle
		jumpParticle = gameScreen.getAssetLord().manager.get(AssetLord.player_jump_particle,ParticleEffect.class);
		jumpParticle.setEmittersCleanUpBlendFunction(false);
	}
	
	//set the starting point of player according to entry portal
	public void setStartingPoint(float x, float y){
		startPos.set(x, y);
		position.set(x, y);
		body.setTransform(x, y, 0);
	}
	
	private void getSkinTexture(){
		
		TextureRegion playerSheet[] = new TextureRegion[2];
		
		//Texture cidle1 = new Texture("level/anime/character-idle.png");
		//cidle1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//Texture cidle2 = new Texture("level/anime/character-idle-2.png");
		//cidle2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		playerSheet[0] = gameAtlas.findRegion("character-idle");//new TextureRegion(cidle1);
		playerSheet[1] = gameAtlas.findRegion("character-idle-2");//new TextureRegion(cidle2);
		
		idleAnime = new Animation(0.2f, playerSheet);
		idleAnime.setPlayMode(PlayMode.LOOP);
		
		
		TextureRegion playerFireIdleSheet[] = new TextureRegion[2];
		
		//Texture cFireIdle1 = new Texture("level/anime/character-gun.png");
		//cFireIdle1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		//Texture cFireIdle2 = new Texture("level/anime/character-gun-2.png");
		//cFireIdle2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		playerFireIdleSheet[0] = gameAtlas.findRegion("character-gun");//new TextureRegion(cFireIdle1);
		playerFireIdleSheet[1] = gameAtlas.findRegion("character-gun-2");//new TextureRegion(cFireIdle2);
		
		fireIdleAnime = new Animation(0.2f, playerFireIdleSheet);
		fireIdleAnime.setPlayMode(PlayMode.LOOP);
		
		
		TextureRegion playerMegaIdleSheet[] = new TextureRegion[2];
		
//		Texture cMegaIdle1 = new Texture("level/anime/character-mega-idle.png");
//		cMegaIdle1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture cMegaIdle2 = new Texture("level/anime/character-mega-idle-2.png");
//		cMegaIdle2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		playerMegaIdleSheet[0] = gameAtlas.findRegion("character-mega-idle");//new TextureRegion(cMegaIdle1);
		playerMegaIdleSheet[1] = gameAtlas.findRegion("character-mega-idle-2");//new TextureRegion(cMegaIdle2);
		
		megaIdleAnime = new Animation(0.2f, playerMegaIdleSheet);
		megaIdleAnime.setPlayMode(PlayMode.LOOP);
		
		
		TextureRegion playerMoveSheet[] = new TextureRegion[4];		
		playerMoveSheet[0] = gameAtlas.findRegion("character-idle");//new TextureRegion(new Texture("level/anime/character-idle.png"));
		playerMoveSheet[1] = gameAtlas.findRegion("character-move-2");//new TextureRegion(new Texture("level/anime/character-move-2.png"));
		playerMoveSheet[2] = gameAtlas.findRegion("character-move-3");//new TextureRegion(new Texture("level/anime/character-move-3.png"));
		playerMoveSheet[3] = gameAtlas.findRegion("character-move-4");//new TextureRegion(new Texture("level/anime/character-move-4.png"));
		//playerMoveSheet[4] = new TextureRegion(new Texture("level/anime/character-move-5.png"));

		
		moveAnime = new Animation(0.08f, playerMoveSheet);
		moveAnime.setPlayMode(PlayMode.LOOP);
		
		
		
		TextureRegion playerFireMoveSheet[] = new TextureRegion[4];		
		playerFireMoveSheet[0] = gameAtlas.findRegion("character-gun");//new TextureRegion(new Texture("level/anime/character-gun.png"));
		playerFireMoveSheet[1] = gameAtlas.findRegion("character-gun-move-2");//new TextureRegion(new Texture("level/anime/character-gun-move-2.png"));
		playerFireMoveSheet[2] = gameAtlas.findRegion("character-gun-move-3");//new TextureRegion(new Texture("level/anime/character-gun-move-3.png"));
		playerFireMoveSheet[3] = gameAtlas.findRegion("character-gun-move-4");//new TextureRegion(new Texture("level/anime/character-gun-move-4.png"));
		
		fireMoveAnime = new Animation(0.08f, playerFireMoveSheet);
		fireMoveAnime.setPlayMode(PlayMode.LOOP);
		
		
		TextureRegion playerMegaMoveSheet[] = new TextureRegion[4];		
		playerMegaMoveSheet[0] = gameAtlas.findRegion("character-mega-idle");//new TextureRegion(new Texture("level/anime/character-mega-idle.png"));
		playerMegaMoveSheet[1] = gameAtlas.findRegion("character-mega-move-2");//new TextureRegion(new Texture("level/anime/character-mega-move-2.png"));
		playerMegaMoveSheet[2] = gameAtlas.findRegion("character-mega-move-3");//new TextureRegion(new Texture("level/anime/character-mega-move-3.png"));
		playerMegaMoveSheet[3] = gameAtlas.findRegion("character-mega-move-4");//new TextureRegion(new Texture("level/anime/character-mega-move-4.png"));
		
		megaMoveAnime = new Animation(0.08f, playerMegaMoveSheet);
		megaMoveAnime.setPlayMode(PlayMode.LOOP);
		
		
		
		TextureRegion playerJumpSheet[] = new TextureRegion[1];		
		//playerJumpSheet[0] = new TextureRegion(new Texture("level/anime/character-move-2.png"));
//		Texture cjump1 = new Texture("level/anime/character-move-2.png");
//		cjump1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		playerJumpSheet[0] = gameAtlas.findRegion("character-move-2");//new TextureRegion(cjump1);
				
		jumpAnime = new Animation(0.2f, playerJumpSheet);
		jumpAnime.setPlayMode(PlayMode.LOOP);
		
		
		
		TextureRegion playerFireSheet[] = new TextureRegion[2];		
//		Texture cfire1 = new Texture("level/anime/character-gun.png");
//		cfire1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture cfire2 = new Texture("level/anime/character-gun-2.png");
//		cfire2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		playerFireSheet[0] = gameAtlas.findRegion("character-gun");//new TextureRegion(cfire1);
		playerFireSheet[1] = gameAtlas.findRegion("character-gun-2");//new TextureRegion(cfire2);
		
		fireAnime = new Animation(0.2f, playerFireSheet);
		fireAnime.setPlayMode(PlayMode.NORMAL);
		
		
		TextureRegion playerHitSheet[] = new TextureRegion[4];		
//		Texture tHit1 = new Texture("level/hit/hit-anime-player-1.png");
//		tHit1.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture tHit2 = new Texture("level/hit/hit-anime-2.png");
//		tHit2.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture tHit3 = new Texture("level/hit/hit-anime-3.png");
//		tHit3.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		Texture tHit4 = new Texture("level/hit/hit-anime-4.png");
//		tHit4.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		playerHitSheet[0] = gameAtlas.findRegion("hit-anime-player-1");//new TextureRegion(tHit1);
		playerHitSheet[1] = gameAtlas.findRegion("hit-anime-2");//new TextureRegion(tHit2);
		playerHitSheet[2] = gameAtlas.findRegion("hit-anime-3");//new TextureRegion(tHit3);
		playerHitSheet[3] = gameAtlas.findRegion("hit-anime-4");//new TextureRegion(tHit4);
		
		hitAnime = new Animation(0.1f, playerHitSheet);
		hitAnime.setPlayMode(PlayMode.NORMAL);
		
		
		TextureRegion[] teleportOutSheet = new TextureRegion[8];
		teleportOutSheet[0] = gameAtlas.findRegion("character-idle");
		teleportOutSheet[1] = gameAtlas.findRegion("character-teleport-out-2");
		teleportOutSheet[2] = gameAtlas.findRegion("character-teleport-out-3");
		teleportOutSheet[3] = gameAtlas.findRegion("character-teleport-out-4");
		teleportOutSheet[4] = gameAtlas.findRegion("character-teleport-out-5");
		teleportOutSheet[5] = gameAtlas.findRegion("character-teleport-out-6");
		teleportOutSheet[6] = gameAtlas.findRegion("character-teleport-out-7");
		teleportOutSheet[7] = gameAtlas.findRegion("character-teleport-out-8");
		
		teleportOut = new Animation(0.1f, teleportOutSheet);
		teleportOut.setPlayMode(PlayMode.NORMAL);
		
		TextureRegion[] teleportInSheet = new TextureRegion[8];
		teleportInSheet[0] = gameAtlas.findRegion("character-teleport-out-8");
		teleportInSheet[1] = gameAtlas.findRegion("character-teleport-out-7");
		teleportInSheet[2] = gameAtlas.findRegion("character-teleport-out-6");
		teleportInSheet[3] = gameAtlas.findRegion("character-teleport-out-5");
		teleportInSheet[4] = gameAtlas.findRegion("character-teleport-out-4");
		teleportInSheet[5] = gameAtlas.findRegion("character-teleport-out-3");
		teleportInSheet[6] = gameAtlas.findRegion("character-teleport-out-2");
		teleportInSheet[7] = gameAtlas.findRegion("character-idle");
		
		teleportIn = new Animation(0.1f, teleportInSheet);
		teleportIn.setPlayMode(PlayMode.NORMAL);
	}
	
	public void render(ShapeRenderer canvas){
		//update(delta);
				
        canvas.setColor(0.1f, 0.1f, 0.1f, 1.0f);
		canvas.rect(position.x-width/2, position.y-height/2, width/2 , height/2, width, height, 1, 1, body.getAngle());
		
	}
	
	public void render(SpriteBatch batch){
		if(!visible) return;
		
		playerSprite.setPosition(position.x - playerSprite.getWidth()/2, position.y - playerSprite.getHeight()*0.4f);

		if(PLAYER_EVOLUTION == PowerUp.LEVEL_ZERO)
			playerSprite.setRegion(idleAnime.getKeyFrame(time, true));
		else if(PLAYER_EVOLUTION == PowerUp.LEVEL_ONE)
			playerSprite.setRegion(fireIdleAnime.getKeyFrame(time, true));
		else
			playerSprite.setRegion(megaIdleAnime.getKeyFrame(time, true));// Mega

		if(pKeys!= null){
			if(PLAYER_EVOLUTION == PowerUp.LEVEL_ZERO){
				
				if(pKeys.get(MyInputProcessor.CONTROL.LEFT) == true || pKeys.get(MyInputProcessor.CONTROL.RIGHT) == true)
					playerSprite.setRegion(moveAnime.getKeyFrame(time, true));
				else if(pKeys.get(MyInputProcessor.CONTROL.UP) == true )
					playerSprite.setRegion(jumpAnime.getKeyFrame(time, true));
			}
			else if(PLAYER_EVOLUTION == PowerUp.LEVEL_ONE){
				//moving and fire
				if(pKeys.get(MyInputProcessor.CONTROL.LEFT) == true || pKeys.get(MyInputProcessor.CONTROL.RIGHT) == true)
					playerSprite.setRegion(fireMoveAnime.getKeyFrame(time, true));
				else{
					//standing with gun and fire
					if(pKeys.get(MyInputProcessor.CONTROL.FIRE) == true)
						playerSprite.setRegion(fireAnime.getKeyFrame(time, true));					
				}
			}
			else if(PLAYER_EVOLUTION == PowerUp.LEVEL_TWO){
				//Mega !!
				if(pKeys.get(MyInputProcessor.CONTROL.LEFT) == true || pKeys.get(MyInputProcessor.CONTROL.RIGHT) == true)
					playerSprite.setRegion(megaMoveAnime.getKeyFrame(time, true));
			}
		}
					

		//teleport animation into stage
		if(TELEPORTING_IN)			
			playerSprite.setRegion(teleportIn.getKeyFrame(winClock));
		
		//teleport animation out of stage
		if(TELEPORTING_OUT)			
			playerSprite.setRegion(teleportOut.getKeyFrame(winClock));
		
		
		
		if(!LEFT_DIRECTION)	
			playerSprite.setFlip(true, false);
		
		if(LevelGenerate.WORLD_FLIPPED)
			playerSprite.setFlip(playerSprite.isFlipX(), true);
		
		
		if(PLAYER_DRAW)
			playerSprite.draw(batch);
				
		//for hit animation
		hitSprite.setRegion(hitAnime.getKeyFrame(deathClock));
		if(GOT_HIT)
		{
			if(!LEFT_DIRECTION)	
				hitSprite.setFlip(true, false);
			
			if(LevelGenerate.WORLD_FLIPPED)
				hitSprite.setFlip(playerSprite.isFlipX(), true);
			
			
			
			if(hitAnime.getKeyFrameIndex(deathClock) == 0)
			{
				hitSprite.setRotation(0);
				hitSprite.setSize(width*3.4f, width*2.5f);
				hitSprite.setOrigin(hitSprite.getWidth()/2, hitSprite.getHeight()/2);
				hitSprite.setPosition(position.x - hitSprite.getWidth()*0.55f, position.y - hitSprite.getHeight()*0.5f);
				
			}
			else//if(hitAnime.getKeyFrameIndex(deathClock) == 2 && hitAnime.getKeyFrameIndex(deathClock) == 3)
			{
				if(hitAnime.getKeyFrameIndex(deathClock) == 3)
					hitSprite.setRotation(45);
				else
					hitSprite.setRotation((deathClock * 1000)%360);
				
				hitSprite.setSize(width*2.5f, width * 2.5f);
				hitSprite.setOrigin(hitSprite.getWidth()/2, hitSprite.getHeight()/2);
				hitSprite.setPosition(position.x - hitSprite.getWidth()/2, position.y - hitSprite.getHeight()/2);

			}
			
			if(!hitAnime.isAnimationFinished(deathClock))
				hitSprite.draw(batch);
		}
		
		
		//hide player after teleportation
		if(TELEPORTING_OUT && teleportOut.getKeyFrameIndex(winClock) == 7)
			visible = false;

	}
	
	public void renderParticles(SpriteBatch batch){

		if(GameScreen.PLAYER_PARTICLES)
			jumpParticle.draw(batch);
		
	}
	
	public void update(float delta){
		time+= delta;
		
		if(GOT_HIT && !DEAD)
		{//make him die after half a sec

			deathClock += delta;
			if(deathClock > DEATH_CAM){
				GOT_HIT = false;
				deathClock = 0;
				DEAD = true;
			}
		}
		
		if((TELEPORTING_IN || TELEPORTING_OUT) && !DEAD)
		{//make teleportation animation

			//give entering more time then going out
			winClock += (TELEPORTING_IN) ? delta*0.8f : delta;
			
			if(winClock > WIN_CAM){
				TELEPORTING_OUT = TELEPORTING_IN = false;
				winClock = 0;				
			}
		}
		
		//update player movement based on keys pressed right now
		if(pKeys != null && CONTROLS)
			updateMove(pKeys);
		
		position = body.getPosition();
		
		if(checkDeath())
		{
			DEAD = true;
			CONTROLS  =false;
		}
				
		if(GameScreen.PLAYER_PARTICLES)
			jumpParticle.update(delta);
	}
	
	public void reset(){
		position = startPos;
		
		body.setTransform(position, 0);
		body.setLinearVelocity(0, 0);
		
		GOT_HIT = LEFT_DIRECTION = DEAD = TELEPORTING_OUT= false;
		TELEPORTING_IN = true;
		
		CONTROLS = true;
		deathClock = 0;
		time = 0;
		visible = true;
		winClock = 0;
		
		PLAYER_EVOLUTION = PowerUp.LEVEL_ZERO;
		
		fixBodyAngle();
	}
	
	public void flipBodyAngle(){
		body.setTransform(position, 180*MathUtils.degRad);
	}
	
	public void fixBodyAngle(){
		body.setTransform(position, 0);
	}
	
	public boolean checkDeath(){
		boolean dead = false;
		
		if(IMMORTAL) return false;
		
		if(DEAD || position.y < 0) //|| position.y > bHEIGHT + bHEIGHT/4
			dead = true;
		
		return dead;		
	}
	
	public void evolvePlayer(){
		if(PLAYER_EVOLUTION < PowerUp.LEVEL_TWO)
			PLAYER_EVOLUTION += 1;
		
		if(PLAYER_EVOLUTION == PowerUp.LEVEL_TWO)
		{
			Bullet.BULLET_POWER = true;
			LevelGenerate.MACHINE_GUN = true;
		}
		else
			Bullet.BULLET_POWER = false;
		
		LevelGenerate.getInstance().playLevelUpSound();
	}
	
	public int getEvolution(){
		return PLAYER_EVOLUTION;
	}
	
	public static Player getInstance(){
		return _player;
	}


	public void setDeath() {
		CONTROLS = false;
		GOT_HIT = true;
		
		if(GameScreen.BACKGROUND_MUSIC)
			Gdx.input.vibrate(50);
		LevelGenerate.getInstance().playEnemyHitSound();

				
		int sign = (LEFT_DIRECTION) ? 1: -1;
		int grav = (LevelGenerate.WORLD_FLIPPED)? -1 : 1;
		
		body.setLinearVelocity(0, 0);		
		body.applyLinearImpulse(sign * 2.8f, grav * 1f, body.getWorldCenter().x, body.getWorldCenter().y, false);
		
//		Timer.schedule(new Task(){
//		    @Override
//		    public void run() {
//		    	DEAD = true;
//		    }
//		}, 0.5f);
	
	}
		
	public Body getBody(){
		return body;
	}
	
	public Fixture getBodyFixture(){
		return bodyFixture;
	}
	
	public Fixture getSensorFixture(){
		return sensorFixture;
	}
	
	/** Starts jumping effect**/
	public void startJumpEffect(){
		jumpParticle.setPosition(position.x, position.y - height/2);
		jumpParticle.start();
	}	
	
	public void makeJump() {
		//maybe add a variable jump, like longer you hold, more it will jump
		if(CAN_JUMP && CONTROLS)
		{
			if(!LevelGenerate.WORLD_FLIPPED)
				body.applyLinearImpulse(0, Jump, body.getWorldCenter().x, body.getWorldCenter().y, true);
			else
				body.applyLinearImpulse(0, -Jump, body.getWorldCenter().x, body.getWorldCenter().y, true);
			
			CAN_JUMP = false;
		}
	}
	
	public void makeMiniJump() {
		body.setLinearVelocity(body.getLinearVelocity().x, 0);
		
		if(!LevelGenerate.WORLD_FLIPPED)
			body.applyLinearImpulse(0, Jump*0.7f, body.getWorldCenter().x, body.getWorldCenter().y, true);
		else
			body.applyLinearImpulse(0, -Jump*0.7f, body.getWorldCenter().x, body.getWorldCenter().y, true);
		
	}

	public void moveLeft() {
		if(body.getLinearVelocity().x > -Speed)
		body.applyLinearImpulse(-0.1f, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
		//body.setLinearVelocity(-Speed, body.getLinearVelocity().y);
	}

	public void moveRight() {
		if(body.getLinearVelocity().x < Speed)
		body.applyLinearImpulse(0.1f, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);

		//body.setLinearVelocity(Speed, body.getLinearVelocity().y);
	}

	public void stopMove() {
		//fix this somehow. i don;t know - maybe fixed
		
		if(CONTROLS)
		body.setLinearVelocity(0, body.getLinearVelocity().y);
	}

	public void updateMove(HashMap<MyInputProcessor.CONTROL, Boolean> keys) {
		pKeys = keys;
		
		if(!CONTROLS)
			return;
		
		if(keys.get(MyInputProcessor.CONTROL.LEFT) == true){
			moveLeft();
			LEFT_DIRECTION = true;
		}
		else if(keys.get(MyInputProcessor.CONTROL.RIGHT) == true){
			moveRight();
			LEFT_DIRECTION = false;
		}
		//else if(keys.get(MyInputProcessor.CONTROL.LEFT) == false && keys.get(MyInputProcessor.CONTROL.RIGHT) == false)
		//	stopMove();
		
		
		
	}
	
	public void applyFireImpulse(){
		//only if falling
		int sign = (LEFT_DIRECTION) ? 1 : -1;

		if(body.getLinearVelocity().y <= 0.1f && !LevelGenerate.WORLD_FLIPPED)
			body.applyLinearImpulse(0, Jump/5, body.getWorldCenter().x, body.getWorldCenter().y, true);
		
		if(PLAYER_EVOLUTION == PowerUp.LEVEL_ONE)
			body.applyLinearImpulse(sign * Speed/12, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
		else if(PLAYER_EVOLUTION == PowerUp.LEVEL_TWO)
			body.applyLinearImpulse(sign * Speed/14, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}

	public Vector2 getPosition() {
		return position;
	}

	public Vector2 startPoint() {
		return startPos;
	}

	public boolean isDead() {
		return DEAD;
	}

	public void dispose() {
		//world.destroyBody(body);
	}

}
