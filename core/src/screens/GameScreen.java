package screens;

import objects.Background;
import objects.PaxBackground;
import objects.Player;
import objects.PowerUp;
import utils.AssetLord;
import utils.CameraShake;
import utils.Cinema;
import utils.GameState;
import utils.LevelGenerate;
import utils.MyContactListener;
import utils.MyInputProcessor;
import utils.ScoreManager;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.softnuke.biosleep.MyGame;

public class GameScreen implements Screen{
	
	/*
	 * TODO:
	 * Camera Shake _done
	 * Player and Enemy particles _ done
	 * Bullet Particles
	 * Make particles alpha off and blending too
	 * Awesome Bullet powerup _ done
	 * Sound Effects and Background music
	 * Portal _ done
	 * More Levels
	 * Main Menu _ done
	 * GameStates
	 * Pause Screen _done
	 * Teleport Animation
	 * Fix Light colors
	 * Clear level screen
	 * Player Jumps and friction _ done partially
	 * resize maps to fit _ no need
	 * Interpolation in world step
	 * make movers _ done
	 * change 2nd powerup picture _ done
	 * fix player direction when shooting left _ done
	 * add coins and hidden doors _ done
	 * falling rocks
	 * laser bot
	 * damage animation _ done
	 * add score and maybe life
	 * BUG: enemy and player get stuck sometimes - on mover - FIXXXXXXXXXXXXXXXXXXXXX this
	 * BUG: mover doesn't take player along with him sometimes
	 * Reverse Gravity _ done
	 * Falling rocks
	 * buttons to make level portal active
	 * Create BeamSpot that throws a big laser from ground that can kill instantly with particles 
	 * Parallax background _ done
	 * Game time to complete within
	 * Make different layers that needs to be rendered over player and below _ done
	 * level clear points for killing enemy, coins, time
	 * Complete cinema class 
	 * make main menu same as intro screen
	 * BUg: First level has odd background _fixed
	 * increase jump button radius sensor _ done
	 * Optimize BACKGROUND!!
	 * Add a ray sprite over portal _ done
	 * add rotation on wind mill
	 * Add scan lines
	 * Add slomo when die & fix player dying twice
	 * 
	 */
	
	public static boolean DEBUG = false;
	public static boolean SOFT_DEBUG = false;
	public static boolean DISABLE_ADS = true;
		
	public static boolean RENDER_LIGHTS = false;
	public static boolean BACKGROUND_SHADER = true;
	public static boolean BACKGROUND_PARALLAX = true;
	public static boolean BACKGROUND_MUSIC = true;
	
	public static boolean BACKGROUND_PARTICLES = true;
	public static boolean PLAYER_PARTICLES = true; 
	
	PerformanceCounter pCounter;
	
	MyGame game;
	AssetLord Assets;
	public static GameScreen _gameScreen = null;
	int WIDTH = MyGame.WIDTH, HEIGHT = MyGame.HEIGHT;
	int bWIDTH = MyGame.bWIDTH, bHEIGHT = MyGame.bHEIGHT;
	float PTP = MyGame.PTP;//pixel to point
	
	public OrthographicCamera camera, cameraui;
	Viewport viewport;
	SpriteBatch batch;
	ShapeRenderer canvas;
	World world;
	Box2DDebugRenderer debugRenderer;
	
	//private Skin skin;
	private Stage stage;
	private Table table;
	Group pauseScreen;
	public Group levelClearScreen;
	TextButton readyButt, scoreBoard, scoreTextButt, scoreHighTextButt, coinsCollectedCount, enemyKilledCount;
	Label scoreLabel;
	Sprite scoreSprite;
	Group controls;
	Image fireImage, pauseBack, coinStarImage, enemyStarImage;
	float tutorialTime = 0;
	
	private TextureAtlas gameAtlas;
	
	BitmapFont fontSmall, fontMedium, fontLarge;
	//FreeTypeFontGenerator generator;
	
	Player player;
	int lastScore = 0, levelScore = 0;
	
	public static GameState CURRENT_STATE = GameState.RUNNING;
	public static float CAMERA_ANGLE = 0;

	public static Vector2 WorldGravity = new Vector2(0,-19f); //-80,0
	public static Vector2 WorldGravityFlipped = new Vector2(0, 19f); //-80,0

	PointLight pLight = null;
	ConeLight cLight = null;
	RayHandler rayHandle;
	ShaderProgram greyShader ;
	CameraShake cameraShake;
	FrameBuffer lightBuffer;
	TextureRegion lightBufferRegion;
	
	Preferences prefs;
	FPSLogger fpsLogger;
	
	public boolean StageVisible = true;
	public boolean UpdateOnceGameOver = true; //helps in doing things only once
	public static boolean SLOW_MOTION = false;
	
	private double stepAccumulator = 0;
    private double stepCurrentTime = 0;
    private float TIME_STEP = 1.0f / 60.0f;	
    
	boolean ScreenFinished = false;
	float Fade = 1f;
	Sprite blackFade;
	
	LevelGenerate level;
	MyInputProcessor inputProcessor;
	ScoreManager scoreManager;
	Background background;
	public Cinema cinema;
	
	public GameScreen(MyGame g, AssetLord ass, int lno){
		game = g;
		Assets = ass;
		_gameScreen = this;
		MyGame.sop("level loading:"+lno);
		LevelGenerate.CURRENT_LEVEL = lno;
		
		if(LevelGenerate.CURRENT_LEVEL == 1)
			LevelGenerate.CURRENT_LEVEL = 0;
		
		//do intro
		if(LevelGenerate.CURRENT_LEVEL == 0)
			CURRENT_STATE = GameState.EVOLVING;
		
		prefs = Gdx.app.getPreferences(MainMenuScreen.PreferenceName);
		GameScreen.BACKGROUND_MUSIC = prefs.getBoolean("music", true);
		GameScreen.RENDER_LIGHTS = (prefs.getInteger("visuals", 1) > 1) ? true : false;
		GameScreen.BACKGROUND_PARALLAX = (prefs.getInteger("visuals", 1) > 0) ? true : false;
		
		/////////
		camera = new OrthographicCamera();
		camera.setToOrtho(false, bWIDTH, bHEIGHT);
		//camera.position.set(bWIDTH/2, bHEIGHT/2 + bHEIGHT/6, 0);
		//camera.update();
		
		cameraui = new OrthographicCamera();
		cameraui.setToOrtho(false, WIDTH, HEIGHT);
		cameraui.position.set(cameraui.viewportWidth/2, cameraui.viewportHeight/2, 0);
		cameraui.update();
		
		//viewport = new FitViewport(bWIDTH, bHEIGHT, bWIDTH*1.5f, bHEIGHT, camera);
		//viewport = new FitViewport(bWIDTH, bHEIGHT, camera);
		//viewport = new StretchViewport(bWIDTH, bHEIGHT,camera);
		//viewport.apply(true);
		camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
		camera.update();
		
		
		batch = new SpriteBatch();		
		
		if(DEBUG)
		canvas = new ShapeRenderer();

		//i have no idea how i initialized this stage
		stage = new Stage(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cameraui), batch);
		
		if(DEBUG) stage.setDebugAll(true);
			
		//handle user inputs
		InputMultiplexer multiplexer = new InputMultiplexer();
		inputProcessor = new MyInputProcessor();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(inputProcessor);
		
		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(true);		
		//Gdx.input.setInputProcessor(inputProcessor);
				
		//-9.8f
		world = new World(WorldGravity, false);	
		if(DEBUG) debugRenderer = new Box2DDebugRenderer();
		
		//handle collision events
		MyContactListener contactListener = new MyContactListener();
		world.setContactListener(contactListener);
		
		gameAtlas = Assets.manager.get(AssetLord.game_atlas, TextureAtlas.class);
		
		fontMedium = Assets.manager.get(AssetLord.medium_font, BitmapFont.class);
		fontLarge = Assets.manager.get(AssetLord.large_font, BitmapFont.class);
		fontSmall = Assets.manager.get(AssetLord.small_font, BitmapFont.class);
		
		createObjects();
		createUI();
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}
	
	private void createObjects() {
		//create cinema before level
		cinema = new Cinema(stage, camera);

		//create player before than level
		player = new Player(world);

		
		level = new LevelGenerate(camera, world, batch, viewport);
		cameraShake = new CameraShake(camera);
		scoreManager = new ScoreManager();
		
		if(SOFT_DEBUG) fpsLogger = new FPSLogger();
		
		lightBuffer = new FrameBuffer(Format.RGBA8888, WIDTH, HEIGHT, false);
		lightBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture(), 0, lightBuffer.getHeight()-HEIGHT, WIDTH, HEIGHT);
		lightBufferRegion.flip(false, true);
		
		
		background = new Background(camera);
		
		
	}

	private void createUI() {
		//create user buttons
		
		controls = new Group();
		
		final Image moveLeftImage = new Image(gameAtlas.findRegion("button-left"));
		moveLeftImage.setSize(HEIGHT*0.25f, HEIGHT*0.25f * moveLeftImage.getHeight()/moveLeftImage.getWidth());
		moveLeftImage.setPosition(WIDTH/35 , 0);
		moveLeftImage.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				inputProcessor.makeActionLeft();
				moveLeftImage.setColor(1f, 1f, 1f, 1f);
				return true;
			}
			
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {				
				moveLeftImage.setColor(1f, 1f, 1f, 0.6f);
				inputProcessor.leaveActionLeft();
			}
		});
		moveLeftImage.setColor(1f, 1f, 1f, 0.6f);
		
		final Image moveRightImage = new Image(gameAtlas.findRegion("button-right"));
		moveRightImage.setSize(HEIGHT*0.25f, HEIGHT*0.25f * moveRightImage.getHeight()/moveRightImage.getWidth());
		moveRightImage.setPosition(moveLeftImage.getWidth() + WIDTH/30, 0);
		moveRightImage.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				inputProcessor.makeActionRight();
				moveRightImage.setColor(1f, 1f, 1f, 1f);
				return true;
			}
			
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {				
				inputProcessor.leaveActionRight();
				moveRightImage.setColor(1f, 1f, 1f, 0.6f);

			}
		});
		moveRightImage.setColor(1f, 1f, 1f, 0.6f);
		
		final Image jumpImage = new Image(gameAtlas.findRegion("button-up"));		
		jumpImage.setSize(HEIGHT*0.25f, HEIGHT*0.25f  * jumpImage.getHeight()/jumpImage.getWidth());
		jumpImage.setPosition(WIDTH - WIDTH/20 - jumpImage.getWidth(), 0);
		jumpImage.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				inputProcessor.makeActionUp();		
				jumpImage.setColor(1f, 1f, 1f, 1f);
				return true;
			}
			
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {				
				inputProcessor.leaveActionUp();
				jumpImage.setColor(1f, 1f, 1f, 0.6f);
			}
		});
		jumpImage.setColor(1f, 1f, 1f, 0.6f);

		
		fireImage = new Image(gameAtlas.findRegion("button-fire"));
		fireImage.setSize(HEIGHT*0.25f, HEIGHT*0.25f  * fireImage.getHeight()/fireImage.getWidth());
		fireImage.setPosition(WIDTH - WIDTH/15 - fireImage.getWidth() - jumpImage.getWidth(), 0);
		fireImage.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//level.fireBullet();
				
				//if(Player.getInstance().PLAYER_EVOLUTION == PowerUp.LEVEL_ONE)
				//	level.fireBullet();
				//else if(Player.getInstance().PLAYER_EVOLUTION == PowerUp.LEVEL_TWO)
					inputProcessor.makeActionFire();	
					
					fireImage.setColor(1f, 1f, 1f, 1f);

				return true;
			}
			
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {				
				//if(Player.getInstance().PLAYER_EVOLUTION == PowerUp.LEVEL_TWO)
					inputProcessor.leaveActionFire();
					
					fireImage.setColor(1f, 1f, 1f, 0.6f);
			}
		});
		fireImage.setColor(1f, 1f, 1f, 0.6f);
		
		
		controls.addActor(moveLeftImage);
		controls.addActor(moveRightImage);
		controls.addActor(fireImage);
		controls.addActor(jumpImage);
		
		stage.addActor(controls);
		
		Label.LabelStyle scoreStyle = new Label.LabelStyle();
		scoreStyle.fontColor = Color.WHITE;
		scoreStyle.font = fontSmall;
		
		scoreLabel = new Label("0000000", scoreStyle);
		scoreLabel.setWidth(WIDTH/9);
		scoreLabel.setPosition(scoreLabel.getWidth() * 1.2f, HEIGHT - scoreLabel.getHeight()*0.8f, Align.right);
		stage.addActor(scoreLabel);
		
		System.out.println(scoreLabel.getX());
		
		createPauseScreen();
		
		createLevelClearScreen();
	}
	

	private void createLevelClearScreen() {
		
		levelClearScreen = new Group();
		
		TextButtonStyle smallStyle = new TextButtonStyle();
		smallStyle.font = fontMedium;
		
		TextButton levelClearText = new TextButton("Level Cleared", smallStyle);
		levelClearText.setDisabled(true);
		levelClearText.setPosition(WIDTH/2 - levelClearText.getWidth()/2, HEIGHT - levelClearText.getHeight() * 1.2f);
		
		levelClearScreen.addActor(levelClearText);
		
		TextButtonStyle largeStyle = new TextButtonStyle();
		largeStyle.font = fontLarge;
		
		TextButton nextLevelText = new TextButton("Next Level", largeStyle);
		nextLevelText.setPosition(WIDTH/2 - nextLevelText.getWidth()/2, HEIGHT/2 - nextLevelText.getHeight()/2);
		nextLevelText.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				startloadingNextLevel();
				
				return true;
			}
		});		
		
		levelClearScreen.addActor(nextLevelText);
		
		TextButton menuText = new TextButton("MAIN MENU", smallStyle);
		//menuText.setPosition(WIDTH/2 - menuText.getWidth()/2, HEIGHT/2 - HEIGHT/4 - menuText.getHeight()/2);
		menuText.setPosition(WIDTH - menuText.getWidth() * 1.2f, menuText.getHeight()/2);
		//menuText.setRotation(90);
		
		menuText.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				returnToMainMenu();
				
				return true;
			}
		});
		
		levelClearScreen.addActor(menuText);		

		
		TextButton coinsCollected = new TextButton("MILK COLLECTED", smallStyle);
		coinsCollected.align(Align.left);
		coinsCollected.setPosition(WIDTH / 4, HEIGHT * 0.7f);
		
		levelClearScreen.addActor(coinsCollected);		

		TextButton enemyKilled = new TextButton("ENEMIE KILLS", smallStyle);
		enemyKilled.align(Align.left);
		enemyKilled.setPosition(WIDTH / 4, coinsCollected.getY() + coinsCollected.getHeight()*1.2f);
		
		levelClearScreen.addActor(enemyKilled);
		
		
		//Texture coinTexture = new Texture("level/star.png");
		//coinTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		coinStarImage = new Image(gameAtlas.findRegion("star"));
		coinStarImage.setSize(coinsCollected.getHeight(), coinsCollected.getHeight() * coinStarImage.getWidth()/coinStarImage.getHeight());
		coinStarImage.setPosition(coinsCollected.getX() - coinStarImage.getWidth()*1.2f, coinsCollected.getY());
		
		enemyStarImage = new Image(gameAtlas.findRegion("star"));
		enemyStarImage.setSize(enemyKilled.getHeight(), enemyKilled.getHeight() * enemyStarImage.getWidth()/enemyStarImage.getHeight());
		enemyStarImage.setPosition(enemyKilled.getX() - enemyStarImage.getWidth()*1.2f, enemyKilled.getY());
		
		levelClearScreen.addActor(coinStarImage);
		levelClearScreen.addActor(enemyStarImage);
		
		coinsCollectedCount = new TextButton("0/0", smallStyle);
		coinsCollectedCount.align(Align.right);
		coinsCollectedCount.setPosition(WIDTH - WIDTH / 4, HEIGHT * 0.7f);
		
		levelClearScreen.addActor(coinsCollectedCount);		

		enemyKilledCount = new TextButton("0/0", smallStyle);
		enemyKilledCount.align(Align.left);
		enemyKilledCount.setPosition(WIDTH - WIDTH / 4, coinsCollectedCount.getY() + coinsCollectedCount.getHeight()*1.2f);
		
		levelClearScreen.addActor(enemyKilledCount);
		
		
		levelClearScreen.setVisible(false);
		stage.addActor(levelClearScreen);
	}
	
	public void showLevelClear(){
		
		//update max level score
		scoreManager.unlockLevel(LevelGenerate.CURRENT_LEVEL+1);
		if(scoreManager.updateHighScore())
		{
			//highScoreHighLight.setVisible(true);
			
			//submit to leaderboard
			//JumperGame.platform.submitLeaderboard(scoreManager.USER_SCORE);
		}
		else
			//highScoreHighLight.setVisible(false);
		
		scoreManager.increaseDeath();
		scoreManager.updateTotalScore();
		
		//update screen stats
		coinsCollectedCount.setText(level.getCoinCollected() +"/"+ level.coinsPool.size);
		enemyKilledCount.setText(level.getEnemyKilled() +"/"+ level.enemyPool.size);
		if(level.getCoinCollected() == level.coinsPool.size)
			{//all coins collected
				coinStarImage.setVisible(true);
				scoreManager.unlockStars(LevelGenerate.CURRENT_LEVEL, ScoreManager.STAR_MILK);
			}
		else
			coinStarImage.setVisible(false);
		if(level.getEnemyKilled() == level.enemyPool.size)
			{//all enemies killed
				enemyStarImage.setVisible(true);
				scoreManager.unlockStars(LevelGenerate.CURRENT_LEVEL, ScoreManager.STAR_ENEMY);
			}
		else
			enemyStarImage.setVisible(false);
		
		pauseBack.setVisible(true);
		levelClearScreen.setVisible(true);
		
		
		//save !!
		scoreManager.save(0);
	}
	
	public void startloadingNextLevel(){
		//TODO
		levelClearScreen.setVisible(false);
		pauseBack.setVisible(false);
		
		//change game state
		CURRENT_STATE = GameState.EVOLVING;
		
		level.loadNextLevel();
		
	}
	
	public void createPauseScreen(){
		//pause screen back background transparent
		Texture pauseBackTex = Assets.manager.get(AssetLord.pause_back_tex, Texture.class);
		pauseBackTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		pauseBack = new Image(pauseBackTex);
		pauseBack.setSize(WIDTH, HEIGHT);
		pauseBack.setColor(1f, 1f, 1f ,0.95f);
		pauseBack.setPosition(0, 0);
		pauseBack.setVisible(false);
		stage.addActor(pauseBack);
		
		pauseScreen = new Group();
		
		TextButtonStyle smallStyle = new TextButtonStyle();
		smallStyle.font = fontMedium;
		
		TextButton pausedText = new TextButton("PAUSED", smallStyle);
		pausedText.setDisabled(true);
		pausedText.setPosition(WIDTH/2 - pausedText.getWidth()/2, HEIGHT/2 + HEIGHT/4 - pausedText.getHeight()/2);
		
		pauseScreen.addActor(pausedText);
		
		TextButtonStyle largeStyle = new TextButtonStyle();
		largeStyle.font = fontLarge;
		
		TextButton continueText = new TextButton("CONTINUE", largeStyle);
		continueText.setPosition(WIDTH/2 - continueText.getWidth()/2, HEIGHT/2 - continueText.getHeight()/2);
		continueText.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				resumeGame();
				
				return true;
			}
		});
		
		pauseScreen.addActor(continueText);
		
		TextButton menuText = new TextButton("MAIN MENU", smallStyle);
		//menuText.setPosition(WIDTH/2 - menuText.getWidth()/2, HEIGHT/2 - HEIGHT/4 - menuText.getHeight()/2);
		menuText.setPosition(WIDTH - menuText.getWidth() * 1.2f, menuText.getHeight()/2);
		//menuText.setRotation(90);
		
		menuText.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				returnToMainMenu();
				
				return true;
			}
		});
		
		pauseScreen.addActor(menuText);		
		
		hidePauseScreen();
		
		stage.addActor(pauseScreen);
	}
	
	public void showPauseScreen(){
		pauseScreen.setVisible(true);
		pauseBack.setVisible(true);
		level.pauseMusic();

	}
	
	public void hidePauseScreen(){
		pauseScreen.setVisible(false);
		pauseBack.setVisible(false);
		
	}
	
	public void resumeGame(){
		hidePauseScreen();
		GameScreen.CURRENT_STATE = GameState.RUNNING;
		level.resumeEverything();
	}


	@Override
	public void show() {
		//start movie if level is 1
		if(LevelGenerate.CURRENT_LEVEL == 0)
			cinema.start(Cinema.MOV_INTRO);
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		if(CURRENT_STATE != GameState.STOPPED)
			update(delta);
		
		if(CURRENT_STATE != GameState.EVOLVING){
		//do if level is not processing
			
			if(CURRENT_STATE == GameState.RUNNING){
				//all the physics should step now
				doPhysics(delta);
				
			}
					
			camera.update();		
			
			//resume developing this
			background.draw(batch);
			
			level.render(batch, canvas);
			
			if(DEBUG)
			{
				canvas.setProjectionMatrix(camera.combined);
				canvas.begin(ShapeType.Filled);
				//player.render(canvas);
				canvas.end();
	
				debugRenderer.render(world, camera.combined);
			}
			
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			cinema.render(batch);
			
			//blend function will become bad
			level.renderParticles(batch);
			player.renderParticles(batch);
			//reset blend function
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			player.render(batch);
			
			
			//things that needs to be rendered over player
			level.renderOverlayed(batch);

			batch.end();


			if(RENDER_LIGHTS)
				renderLights();
		}
		
		stage.draw();
		

		//cinema.render(batch);
		cinema.update(delta);
		
		batch.setProjectionMatrix(cameraui.combined);
		batch.begin();
		cinema.renderUI(batch);
		batch.end();
		
		if(SOFT_DEBUG)
		fpsLogger.log();
	}

	private void renderLights() {
		lightBuffer.begin();

		// setup the right blending
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glEnable(GL20.GL_BLEND);
				        
		// set the ambient color values, this is the "global" light of your scene
		// imagine it being the sun.  Usually the alpha value is just 1, and you change the darkness/brightness with the Red, Green and Blue values for best effect

		//Gdx.gl.glClearColor(0.3f,0.38f,0.4f, 0.7f);
		Gdx.gl.glClearColor(0f, 0f, 0f, 0.3f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
					        
		// start rendering the lights to our spriteBatch
		batch.setProjectionMatrix(camera.combined);
		batch.begin();


		// set the color of your light (red,green,blue,alpha values)
		//batch.setColor(0.3f, 0.2f, 0.2f, 1f);
		batch.setColor(1f, 1f, 1f, 0.4f);
		// and render the sprite
		//batch.draw(lightSprite, tx,ty,tw,tw,0,0,128,128,false,true);
		level.renderLights(batch);
		
		//batch.setColor(1f, 1f, 1f, 1f);
		batch.end();
		lightBuffer.end();


		// now we render the lightBuffer to the default "frame buffer"
		// with the right blending !

		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ONE);
		//Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
		//Gdx.gl.glBlendFunc(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_COLOR);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(lightBufferRegion, camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);               
		batch.end();
	}

	private void doPhysics(float delta) {
		
		if(!SLOW_MOTION)
			TIME_STEP = 1/60f;
		else
			TIME_STEP = delta = 1/180f;
		
		float frameTime = Math.min(delta, 0.25f);	    
	    stepAccumulator += frameTime;   
	    
	    while (stepAccumulator >= TIME_STEP) {
	    		    	
	    	player.update(TIME_STEP);
			level.update(TIME_STEP);
			
			//if(CURRENT_STATE == GameState.RUNNING)
			world.step(TIME_STEP, 6, 2); 
			
            stepAccumulator -= TIME_STEP;            

            //world needs cleanup 
            level.cleanUpLevel();
	    }
	    
		
	}

	private void update(float delta) {
		//player.update(delta);
		//level.update(delta);
		
		if(player.isDead() && !levelClearScreen.isVisible()){
			reset(false);
		}

		if(CURRENT_STATE == GameState.READY)
		{
			if(UpdateOnceGameOver){			
				//Things that needs to be executed only once the game goes to Ready state
				//if this is a high score
				
				
				
				
				UpdateOnceGameOver = false;
			}
			
			
			level.dimMusic();
			//levelG.pauseMusic();
			
			hidePauseScreen();			

		}
		
		if(CURRENT_STATE == GameState.PAUSED && !pauseScreen.isVisible()){
			showPauseScreen();
			
			level.pauseEverything();
		}
		
		if(CURRENT_STATE == GameState.RUNNING)
		{
			cameraShake.update(delta);
			if(Player.getInstance().PLAYER_EVOLUTION == PowerUp.LEVEL_ZERO){
				fireImage.setColor(1f, 1f, 1f, 0.2f);
			}
			else
			{
				if(fireImage.getColor().a != 1)
					fireImage.setColor(1f, 1f, 1f, 0.6f);
			}
			
			//update camera pos
			updateCameraMovement(delta);
			
			//update background effect
			background.update(delta);
		}
		
		//update score text
		if(lastScore < scoreManager.USER_SCORE)
		{
			if(scoreManager.USER_SCORE < 10)
				scoreLabel.setText("000000".concat(String.valueOf(scoreManager.USER_SCORE)));
			else if(scoreManager.USER_SCORE < 100)
				scoreLabel.setText("00000".concat(String.valueOf(scoreManager.USER_SCORE)));
			else if(scoreManager.USER_SCORE < 1000)
				scoreLabel.setText("0000".concat(String.valueOf(scoreManager.USER_SCORE)));
			else if(scoreManager.USER_SCORE < 10000)
				scoreLabel.setText("000".concat(String.valueOf(scoreManager.USER_SCORE)));
			else if(scoreManager.USER_SCORE < 100000)
				scoreLabel.setText("00".concat(String.valueOf(scoreManager.USER_SCORE)));
			else if(scoreManager.USER_SCORE < 1000000)
				scoreLabel.setText("0".concat(String.valueOf(scoreManager.USER_SCORE)));
			else if(scoreManager.USER_SCORE < 10000000)
				scoreLabel.setText(String.valueOf(scoreManager.USER_SCORE));
			
			lastScore = scoreManager.USER_SCORE;
		}
		
		stage.act(delta);
		
	}
	
	/** @param newLevel is reseting to set new level?**/
	public void reset(boolean newLevel){
		SLOW_MOTION = false;
		
		//reset player attributes
		player.reset();
		
		//reseting camera
		//camera.position.set(bWIDTH/2, camera.position.y, 0);
		camera.update();
		
		level.reset();
		inputProcessor.resetKeys();
		
		if(!newLevel){
			//reset user score
			scoreManager.reset();
			lastScore = 0;	
			scoreLabel.setText("0000000");
		}
		
		UpdateOnceGameOver = true;
		CURRENT_STATE = GameState.RUNNING;
		LevelGenerate.CURRENT_LEVEL_CLEARED = false;

		hidePauseScreen();
	}
	
	private void updateCameraMovement(float delta){
		float offsetX = 4; //player and camera difference
		float lerp = 5f;
		

			Vector3 position = this.getCamera().position;
			//if(!cameraShake.getShake())
				//position.x = Interpolation.linear.apply(position.x, player.getPosition().x+offset, delta*5);
			
			//max lower bound
			float y = Interpolation.linear.apply(position.y, Math.max(player.getPosition().y, 6), delta*lerp);
			position.y = y;

			//max right bound			
			float plX = Math.min(level.MAP_RIGHT_BOUND - 10, player.getPosition().x + offsetX);
			//max left bound
			float x = Interpolation.linear.apply(position.x, Math.max(plX, 10), delta*lerp);
			position.x = x;
			
			//else
			//	camera.position.set(player.getPosition().x + offset, camera.position.y, 0);
			//position.x += (player.getPosition().x + offset - position.x) * lerp * delta;
			//position.y += (player.getPosition().y - position.y) * lerp * delta;
			
			//camera.position.set(player.getPosition().x + offset, camera.position.y, 0);
			
		
		camera.update();
	}
	
	public void hideControls(){
		controls.setVisible(false);
	}
	
	public void showControls(){
		controls.setVisible(true);
	}
	
	public void shakeThatAss(){
		cameraShake.shakeThatAss(true);
	}

	public void increaseScore(int val) {
		if(player.checkDeath()) return;
				
		//if(readyButt.isVisible() == false){
		focusScore(false);
		scoreManager.increaseScore(val);	
	}
	
	public void focusScore(boolean bonus){
		/*
		//slightly animate score
		scoreBoard.addAction(Actions.sequence(Actions.alpha(0.6f, 0.2f), Actions.alpha(1f, 0.2f)));
		
		if(bonus){
			scoreBonusImage.setVisible(true);
			scoreBonusImage.addAction(Actions.sequence(Actions.fadeIn(0)));
			scoreBonusImage.addAction(Actions.sequence(Actions.fadeOut(0.5f)));			
		}
		*/
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {

		CURRENT_STATE = GameState.PAUSED;
		showPauseScreen();
	}

	@Override
	public void hide() {
		//save everything first
		scoreManager.save(0);
		dispose();

	}

	@Override
	public void dispose() {
		reset(false);
		
		batch.dispose();
		if(DEBUG) canvas.dispose();
		level.dispose();
		world.dispose();
		player.dispose();
		if(DEBUG) debugRenderer.dispose();
		lightBuffer.dispose();
		
	}
	
	public static GameScreen getInstance(){
		return _gameScreen;
	}

	public void returnToMainMenu() {
		GameScreen.CURRENT_STATE = GameState.STOPPED;

		game.setScreen(new MainMenuScreen(game, Assets));
	}

	public ScoreManager getScoreManager(){
		return scoreManager;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public AssetLord getAssetLord() {
		return Assets;
	}
}
