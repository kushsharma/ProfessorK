package utils;

import java.util.HashMap;

import objects.BeamSpot;
import objects.Bullet;
import objects.Coin;
import objects.Enemy;
import objects.GravityReverser;
import objects.Light;
import objects.Movers;
import objects.Player;
import objects.Portal;
import objects.PowerUp;
import objects.Switch;
import screens.GameScreen;
import utils.MyInputProcessor.CONTROL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.softnuke.biosleep.MyGame;

public class LevelGenerate {

	public static LevelGenerate _level = null;
	int WIDTH = MyGame.WIDTH, HEIGHT = MyGame.HEIGHT;
	int bWIDTH = MyGame.bWIDTH, bHEIGHT = MyGame.bHEIGHT;
	public static float PTP = 0.15f;
	
	public static boolean MACHINE_GUN = false; //continues fire
	public static boolean WORLD_FLIPPED = false; //if gravity is flipped

	TiledMap tileMap;
	TmxMapLoader tileLoader;
	OrthogonalTiledMapRenderer tmRenderer;
	OrthographicCamera camera;
	World world;
	SpriteBatch batch;
	Viewport viewport;
	GameScreen gameScreen = GameScreen.getInstance();
	
	public static short CATEGORY_NONE = 0x0001;

	public static short CATEGORY_PLAYER = 0x0002;
	public static short CATEGORY_BADBOY = 0x0004;
	public static short CATEGORY_WALL = 0x0008;
	public static short CATEGORY_POWERUP = 0x0010;
	public static short CATEGORY_BULLET = 0x0020;
	
	public Array<Enemy> enemyPool = new Array<Enemy>();	
	Array<Bullet> bulletPool = new Array<Bullet>();
	Array<Bullet> activeBulletPool = new Array<Bullet>();
	Array<PowerUp> powersPool = new Array<PowerUp>();
	Array<Light> lightPool = new Array<Light>();
	Array<Portal> portalPool = new Array<Portal>();
	Array<Body> platformPool = new Array<Body>();
	Array<Body> spikesPool = new Array<Body>();
	Array<Movers> moverPool = new Array<Movers>();
	public Array<Coin> coinsPool = new Array<Coin>();
	Array<GravityReverser> gravityPool = new Array<GravityReverser>();
	Array<BeamSpot> beamPool = new Array<BeamSpot>();

	Switch levelSwitch = null;
	
	private float gametime = 0;
	private float last_bullet = 0; //time since last bullet was fired
	public HashMap<CONTROL, Boolean> pKeys = null;
	TaskQueue taskQueue = new TaskQueue();
	
	public static boolean CURRENT_LEVEL_CLEARED = false;
	public static int CURRENT_LEVEL = 1;
	public static final int MAX_LEVEL = 12;
	public static boolean LEVEL_LOADED = false;
	
	public int MAP_RIGHT_BOUND = 50;
	
	AssetLord Assets = GameScreen.getInstance().getAssetLord();
	//sounds
	Sound coinSound, fireSound, playerHurtSound, enemyHurtSound, levelUpSound, portalSound;
	Music gameMusic = null, menuMusic = null;
	
	public LevelGenerate(OrthographicCamera cam, World w, SpriteBatch b, Viewport view){
		this(cam,w,b,view,CURRENT_LEVEL);
	}
	
	public LevelGenerate(OrthographicCamera cam, World w, SpriteBatch b, Viewport view, int levelno){
		_level = this;
		camera = cam;
		world = w;
		batch = b;
		viewport = view;
		CURRENT_LEVEL = levelno;
		
		PTP = MyGame.PTP;
		
		PTP = 1/64f;
		
		tileLoader = new TmxMapLoader();
		tmRenderer = new OrthogonalTiledMapRenderer(tileMap, PTP, batch);

		levelTiled();
		
		prepareBullets();
		
		coinSound = fireSound = playerHurtSound = enemyHurtSound = levelUpSound = portalSound = null;
		
		
		//create Sounds
		loadSounds();
	}
	
	private void loadSounds() {
		
//			coinSound = Gdx.audio.newSound(Gdx.files.internal("sound/Pickup_Coin.wav"));
//			fireSound = Gdx.audio.newSound(Gdx.files.internal("sound/Laser_Shoot.wav"));
//			playerHurtSound = Gdx.audio.newSound(Gdx.files.internal("sound/Player_Hit_Hurt.wav"));
//			enemyHurtSound = Gdx.audio.newSound(Gdx.files.internal("sound/Enemy_Hit_Hurt.wav"));
//			levelUpSound = Gdx.audio.newSound(Gdx.files.internal("sound/Level_Up.wav"));
//			portalSound = Gdx.audio.newSound(Gdx.files.internal("sound/Portal.wav"));
		
			coinSound = Assets.manager.get(AssetLord.coin_sound, Sound.class);
			fireSound = Assets.manager.get(AssetLord.fire_sound, Sound.class);
			playerHurtSound = Assets.manager.get(AssetLord.player_hurt_sound, Sound.class);
			enemyHurtSound = Assets.manager.get(AssetLord.enemy_hurt_sound, Sound.class);
			levelUpSound = Assets.manager.get(AssetLord.levelup_sound, Sound.class);
			//portalSound = Assets.manager.get(AssetLord.portal_sound, Sound.class);
			
			
			menuMusic = Assets.manager.get(AssetLord.menu_music, Music.class);
			gameMusic = Assets.manager.get(AssetLord.game_music, Music.class);
		
		if(GameScreen.BACKGROUND_MUSIC){
			if(CURRENT_LEVEL >= 1){
				gameMusic.stop();
				gameMusic.play();
				
				menuMusic.stop();
			}
			else
			{
				menuMusic.play();
			}
		}
	}

	public void prepareBullets(){
		
		//10 bullets
		for(int i=0;i<10;i++)
		{
			Bullet b = new Bullet(world, false);
			bulletPool.add(b);
		}
		
	}
	
	public void levelTiled(){
		System.out.println("Generating Tiled Map of Level..."+CURRENT_LEVEL);
		
		try {
			switch(CURRENT_LEVEL){
			case 0:
				tileMap = tileLoader.load("levels/level-intro.tmx");
				break;
			case 1:
				tileMap = tileLoader.load("levels/level1.tmx");
				break;
			case 2:
				tileMap = tileLoader.load("levels/level2.tmx");
				break;
			case 3:
				tileMap = tileLoader.load("levels/level3.tmx");
				break;	
			case 4:
				tileMap = tileLoader.load("levels/level4.tmx");
				break;
			case 5:
				tileMap = tileLoader.load("levels/level5.tmx");
				break;
			case 6:
				tileMap = tileLoader.load("levels/level6.tmx");
				break;
			case 7:
				tileMap = tileLoader.load("levels/level7.tmx");
				break;	
			case 8:
				tileMap = tileLoader.load("levels/level8.tmx");
				break;
			case 9:
				tileMap = tileLoader.load("levels/level9.tmx");
				break;
			case 10:
				tileMap = tileLoader.load("levels/level10.tmx");
				break;
			case 11:
				tileMap = tileLoader.load("levels/level11.tmx");
				break;
			case 12:
				tileMap = tileLoader.load("levels/level12.tmx");
				break;
			case 13:
				tileMap = tileLoader.load("levels/level13.tmx");
				break;
			default:
				tileMap = tileLoader.load("map1.tmx");
				break;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Cannot find file: map files .tmx");
			Gdx.app.exit();
		}		
		
		MapProperties prop = tileMap.getProperties();		
		MAP_RIGHT_BOUND = prop.get("width", Integer.class)/2;
		
		GameScreen.getInstance().cinema.levelUpdate(CURRENT_LEVEL);

		buildShapes(tileMap, world);		
		tmRenderer.setMap(tileMap);
		
		LEVEL_LOADED = true;
		
	}
	
	public void loadNextLevel() {
		if(CURRENT_LEVEL < MAX_LEVEL)
		{
			LEVEL_LOADED = false;
			CURRENT_LEVEL++;
			
			clearOldLevel();
			levelTiled();
			GameScreen.getInstance().reset(true);
			
			CURRENT_LEVEL_CLEARED = false;
			
			//start music
			if(GameScreen.BACKGROUND_MUSIC){
				if(CURRENT_LEVEL > 0)
				{
					menuMusic.stop();
				
					if(CURRENT_LEVEL == 1)
						gameMusic.stop();
					
					gameMusic.play();
				}
			}
		}
	}
	
	private void clearOldLevel() {
		//soft dispose only those who belong to this level
		
		if(CURRENT_LEVEL == 1)
			GameScreen.getInstance().cinema.clearCinema();
		
		dispose(true);
	}

	public void buildShapes(TiledMap map, World world) {
        
        MapObjects platforms = map.getLayers().get("PlatformOb").getObjects();
        for(MapObject object : platforms) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject)object);
                
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }

            
            BodyDef bd = new BodyDef();
            bd.type = BodyType.StaticBody;
            Body body = world.createBody(bd);
            
            FixtureDef fixD = new FixtureDef();
            fixD.density = 0;
            fixD.shape = shape;
            fixD.filter.categoryBits = (CATEGORY_WALL);
            fixD.filter.maskBits = (short) (CATEGORY_PLAYER | CATEGORY_BADBOY | CATEGORY_BULLET);
            
            body.createFixture(fixD);

            body.setUserData("platform");
            platformPool.add(body);

            shape.dispose();
        }
        
        
        
       	if(map.getLayers().get("SpikesOb") != null){
	        MapObjects spikes = map.getLayers().get("SpikesOb").getObjects();
	        for(MapObject object : spikes) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	            Shape shape;
	
	            if (object instanceof RectangleMapObject) {
	                shape = getRectangle((RectangleMapObject)object);
	                
	            }
	            else {
	                continue;
	            }
	
	            
	            BodyDef bd = new BodyDef();
	            bd.type = BodyType.StaticBody;
	            Body body = world.createBody(bd);
	            
	            FixtureDef fixD = new FixtureDef();
	            fixD.density = 0;
	            fixD.shape = shape;
	            fixD.filter.categoryBits = (CATEGORY_BADBOY);
	            fixD.filter.maskBits = (short) (CATEGORY_PLAYER);
	            
	            body.createFixture(fixD);
	            //body.createFixture(shape, 0);
	
	            body.setUserData("spikes");
	            spikesPool.add(body);
	
	            shape.dispose();
	        }
       	}
        
       	if(map.getLayers().get("EnemyOb") != null){
	        MapObjects enemy = map.getLayers().get("EnemyOb").getObjects();
	        for(MapObject object : enemy) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	            Shape shape;
	
	            if (object instanceof RectangleMapObject) {
	                shape = getRectangle((RectangleMapObject)object);
	                
	            }
	            else {
	                continue;
	            }
	
	            //generate enemy ai
	            Rectangle r = ((RectangleMapObject)object).getRectangle();
	            Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
	                    (r.y + r.height * 0.5f ) * PTP);
	            Enemy en = new Enemy(world, shape, pos);
	            
	            enemyPool.add(en);
	        }
       	}
        
        //get level switche if any
        levelSwitch = null;
        if(map.getLayers().get("SwitchOb") != null){
        	MapObjects switchs = map.getLayers().get("SwitchOb").getObjects();
        	for(MapObject object : switchs) {
        		
        		Rectangle r = ((RectangleMapObject)object).getRectangle();
        		Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
        				(r.y + r.height * 0.5f ) * PTP);
        		
        		//calculating light size
        		float len1x = (r.x - r.width/2) * PTP;
        		float len1y = (r.y - r.height/2) * PTP;            
        		float len2x = (r.x + r.width/2) * PTP;
        		float len2y = (r.y + r.height/2) * PTP;            
        		double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
        		size = Math.sqrt(size);
        		
        		Light lightE = new Light(pos, (float)size, Light.GREEN_COLOR);
        		pushLight(lightE);
        		
        		Light lightD = new Light(pos, (float)size, Light.RED_COLOR);
        		pushLight(lightD);
        		
        		levelSwitch = new Switch(world, pos, lightE, lightD);     
        		
        	}        	
        }//end if
        
        
        //create portals
        MapObjects portals = map.getLayers().get("PortalOb").getObjects();
        Array<RectangleMapObject> portalSensors = portals.getByType(RectangleMapObject.class);
        Array<PolygonMapObject> portalBodies = portals.getByType(PolygonMapObject.class);
        
       for(RectangleMapObject rmo:portalSensors){
        	//entry portal
        	if(rmo.getProperties().get("place").toString().equals("entry")){
        		for(PolygonMapObject mpo: portalBodies){
        			
                	if(mpo.getProperties().get("place").toString().equals("entry")){
                		//create new portal
                		
                		Rectangle r = ((RectangleMapObject)rmo).getRectangle();
                        Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
                                 (r.y + r.height * 0.5f ) * PTP);
                         
                   
                		Portal p = new Portal(world, Portal.ENTRY, pos, r.width * PTP, r.height * PTP, (PolygonMapObject) mpo);
                		if(CURRENT_LEVEL == 0)
                			p.ENABLED = false;
                		
                		portalPool.add(p);
                		
                		//UPDATE PLAYER LOCATION
                		Player.getInstance().setStartingPoint(pos.x , pos.y + 1);
                	}
                }
        	}
        	
        	//exit portal
        	if(rmo.getProperties().get("place").toString().equals("exit")){
        		for(PolygonMapObject mpo: portalBodies){
        			
                	if(mpo.getProperties().get("place").toString().equals("exit")){
                		//create new portal
                		
                		Rectangle r = ((RectangleMapObject)rmo).getRectangle();
                        Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
                                 (r.y + r.height * 0.5f ) * PTP);
                         
                        
                		Portal p = new Portal(world, Portal.EXIT, pos, r.width * PTP, r.height * PTP, (PolygonMapObject) mpo);
                		portalPool.add(p);

                	}
                }
        	}
        }

       	if(map.getLayers().get("PowerUpOb") != null){
	        MapObjects powerups = map.getLayers().get("PowerUpOb").getObjects();
	        for(MapObject object : powerups) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	            Shape shape;
	
	            if (object instanceof RectangleMapObject) {
	                Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
	                
	                shape = new PolygonShape();
	                ((PolygonShape) shape).setAsBox(rectangle.width * 0.5f * PTP,
	                                 rectangle.height * 0.5f * PTP,
	                                 Enemy.CENTER_VECTOR,
	                                 0.0f);        
	                
	            }
	            else {
	                continue;
	            }
	            
	            Rectangle r = ((RectangleMapObject)object).getRectangle();
	            Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
	                    (r.y + r.height * 0.5f ) * PTP);
	            
	            //calculating light size
	            float len1x = (r.x - r.width/2) * PTP;
	            float len1y = (r.y - r.height/2) * PTP;            
	            float len2x = (r.x + r.width/2) * PTP;
	            float len2y = (r.y + r.height/2) * PTP;            
	            double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
	            size = Math.sqrt(size);
	            
	            Light light = new Light(pos, (float)size, Light.WHITE_COLOR);
	            pushLight(light);
	            
	            int level = Integer.parseInt(object.getProperties().get("level").toString());
	            PowerUp p = new PowerUp(world, shape, level,  pos.x, pos.y, light);
	            powersPool.add(p);
	        }
       	}
       	
       	if(map.getLayers().get("CoinOb") != null){
	        MapObjects coins = map.getLayers().get("CoinOb").getObjects();
	        for(MapObject object : coins) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	
	            if (object instanceof RectangleMapObject) {
	                Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
	                
	            }
	            else {
	                continue;
	            }
	            
	            Rectangle r = ((RectangleMapObject)object).getRectangle();
	            Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
	                    (r.y + r.height * 0.5f ) * PTP);
	            
	            //calculating light size
	            float len1x = (r.x - r.width/2) * PTP;
	            float len1y = (r.y - r.height/2) * PTP;            
	            float len2x = (r.x + r.width/2) * PTP;
	            float len2y = (r.y + r.height/2) * PTP;            
	            double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
	            size = Math.sqrt(size);
	            
	            
	            int level = Coin.NORMAL;
	            try{
	            	level = Integer.parseInt(object.getProperties().get("type").toString());
	            }catch(Exception e){level = Coin.NORMAL;}
	            
	            //only create light if its not hidden
	            Light light = null;
	            if(level != Coin.HIDDEN){
	            	light = new Light(pos, (float)size, Light.WHITE_COLOR);
	            	pushLight(light);	            	
	            }

	            Coin c = new Coin(world, pos, level, light);
	            coinsPool.add(c);
	        }
       	}
        
       	if(map.getLayers().get("LightOb") != null){
	        MapObjects lights = map.getLayers().get("LightOb").getObjects();
	        for(MapObject object : lights) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	            Shape shape;
	
	            if (object instanceof RectangleMapObject) {
	                shape = getRectangle((RectangleMapObject)object);
	                
	            }
	            else {
	                continue;
	            }
	            
	            Rectangle r = ((RectangleMapObject)object).getRectangle();
	            Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
	                    (r.y + r.height * 0.5f ) * PTP);
	                       
	            int color = 1;
	            try{
	            	color = Integer.parseInt(object.getProperties().get("color").toString());
	            }catch(Exception e){
	            	e.printStackTrace();
	            	System.out.println("No color baby");
	            	color = 1;
	            }
	            
	            //calculating light size
	            float len1x = (r.x - r.width/2) * PTP;
	            float len1y = (r.y - r.height/2) * PTP;            
	            float len2x = (r.x + r.width/2) * PTP;
	            float len2y = (r.y + r.height/2) * PTP;            
	            double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
	            size = (float) Math.sqrt(size);
	                        
	            Light l = new Light(pos, (float)size, color);
	            l.enableOscillate();
	            
	            lightPool.add(l);
	        }
       	}
        
        if(map.getLayers().get("MoverOb") != null){
	        MapObjects movers = map.getLayers().get("MoverOb").getObjects();
	        for(MapObject object : movers) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	            Shape shape;
	
	            if (object instanceof RectangleMapObject) {
	                shape = getRectangle((RectangleMapObject)object);
	                
	            }
	            else {
	                continue;
	            }
	            
	            Rectangle r = ((RectangleMapObject)object).getRectangle();
	            Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
	                    (r.y + r.height * 0.5f ) * PTP);
	                       
	            int dir = Movers.HOR;
	            try{
	            	if(object.getProperties().get("direction").toString().equals("v"))
	            		dir = Movers.VER;
	            	else
	            		dir = Movers.HOR;
	            		
	            }catch(Exception e){
	            	e.printStackTrace();
	            	System.out.println("No direction baby");
	            	dir = Movers.HOR;
	            }
	            
	            //calculating light size
	            float len1x = (r.x - r.width/2) * PTP;
	            float len1y = (r.y - r.height/2) * PTP;            
	            float len2x = (r.x + r.width/2) * PTP;
	            float len2y = (r.y + r.height/2) * PTP;            
	            double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
	            size = (float) Math.sqrt(size);
	                        
	            Movers m = new Movers(world, pos, (float)size, dir);
	            
	            moverPool.add(m);
	        }
        }
        
        //get gravity reversres if any
        if(map.getLayers().get("GravityOb") != null){
        	MapObjects gravityRevs = map.getLayers().get("GravityOb").getObjects();
        	for(MapObject object : gravityRevs) {
        		
        		Rectangle r = ((RectangleMapObject)object).getRectangle();
        		Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
        				(r.y + r.height * 0.5f ) * PTP);
        		
        		//calculating light size
        		float len1x = (r.x - r.width/2) * PTP;
        		float len1y = (r.y - r.height/2) * PTP;            
        		float len2x = (r.x + r.width/2) * PTP;
        		float len2y = (r.y + r.height/2) * PTP;            
        		double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
        		size = Math.sqrt(size);
        		
        		Light lightE = new Light(pos, (float)size, Light.WHITE_COLOR);
        		pushLight(lightE);
        		
        		Light lightD = new Light(pos, (float)size, Light.WHITE_COLOR);
        		pushLight(lightD);
        		
        		int type = GravityReverser.FLIPED;
                try{
                	type = (object.getProperties().get("type").toString().equals("flipper")) ? GravityReverser.FLIPED : GravityReverser.FIXED;
                }catch(Exception e){
                	e.printStackTrace();
                	System.out.println("No type baby, gravity reverser");
                	type = GravityReverser.FLIPED;
                }
        		
                GravityReverser reverser = new GravityReverser(world, pos, type, lightE, lightD);     
        		gravityPool.add(reverser);
                
        	}        	
        }//end if
        
        if(map.getLayers().get("BeamSpotOb") != null){
	        MapObjects beams = map.getLayers().get("BeamSpotOb").getObjects();
	        for(MapObject object : beams) {
	
	            if (object instanceof TextureMapObject) {
	                continue;
	            }
	
	            Shape shape;
	
	            if (object instanceof RectangleMapObject) {
	                Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
	                
	                shape = new PolygonShape();
	                ((PolygonShape) shape).setAsBox(rectangle.width * 0.5f * PTP,
	                                 rectangle.height * 0.5f * PTP,
	                                 Enemy.CENTER_VECTOR,
	                                 0.0f);        
	                
	            }
	            else {
	                continue;
	            }
	            
	            Rectangle r = ((RectangleMapObject)object).getRectangle();
	            Vector2 pos = new Vector2((r.x + r.width * 0.5f) * PTP,
	                    (r.y + r.height * 0.5f ) * PTP);
	            
	            //calculating light size
	            float len1x = (r.x - r.width/2) * PTP;
	            float len1y = (r.y - r.height/2) * PTP;            
	            float len2x = (r.x + r.width/2) * PTP;
	            float len2y = (r.y + r.height/2) * PTP;            
	            double size = Math.pow(len1x - len2x, 2) +  Math.pow(len1y - len2y, 2);
	            size = Math.sqrt(size);
	            
	            Light light = new Light(pos, (float)size, Light.BLUE_COLOR);
	            pushLight(light);
	            
	            //int level = Integer.parseInt(object.getProperties().get("level").toString());
	            BeamSpot p = new BeamSpot(world, pos, light);
	            beamPool.add(p);
	        }
        }

    }
	
	public void loadCinematic(){
		
		
		
	}
	
	Array<TiledMapImageLayer> tileImageLayers = new Array<TiledMapImageLayer>();
	public void render(SpriteBatch batch, ShapeRenderer canvas){
		if(!LEVEL_LOADED) return;
		
		//resetting color changed by idiots
		batch.setColor(1f, 1f, 1f, 1f);		
		tmRenderer.setView(camera.combined, camera.position.x - bWIDTH/2 -bWIDTH/6 , camera.position.y - bHEIGHT/2 - bHEIGHT/6, bWIDTH*1.5f, bHEIGHT*1.5f);
		//tmRenderer.setView(camera);
		//batch.setProjectionMatrix(GameScreen.getInstance().cameraui.combined);
		batch.begin();
		tileMap.getLayers().getByType(TiledMapImageLayer.class, tileImageLayers);
		for(TiledMapImageLayer l:tileImageLayers)
			tmRenderer.renderImageLayer(l);
		
		if(tileMap.getLayers().get("Platform") != null)
			tmRenderer.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("Platform"));
		if(tileMap.getLayers().get("Spikes") != null)
			tmRenderer.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("Spikes"));
		if(tileMap.getLayers().get("Portal") != null)
			tmRenderer.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("Portal"));
		
		
		//tmRenderer.render();
		batch.end();
		
		//reset blend function
		//batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//		canvas.setProjectionMatrix(camera.combined);
//		canvas.begin(ShapeType.Filled);
//		for(Enemy e:enemyPool)
//			e.render(canvas);
//		canvas.end();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for(Enemy e:enemyPool)
			e.render(batch);
		for(Bullet b:activeBulletPool)
			b.render(batch);
		for(PowerUp p:powersPool)
			p.render(batch);
		for(Portal p:portalPool)
			p.render(batch);
		for(Movers m:moverPool)
			m.render(batch);
		for(Coin c:coinsPool)
			c.render(batch);
		for(GravityReverser gr:gravityPool)
			gr.render(batch);			
		for(BeamSpot bs: beamPool)
			bs.render(batch);
		
		
		if(levelSwitch != null)
			levelSwitch.render(batch);
		
				
		//reset blend function
		//batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		batch.end();
		
		
	}
	
	public void renderParticles(SpriteBatch batch){
		if(!GameScreen.BACKGROUND_PARTICLES)
			return;
		
		//render particles
		for(Enemy e:enemyPool)
			e.renderParticles(batch);
		for(PowerUp p:powersPool)
			p.renderParticles(batch);
		for(Portal p:portalPool)
			p.renderParticles(batch);
		for(Coin c:coinsPool)
			c.renderParticles(batch);
		for(GravityReverser gr:gravityPool)
			gr.renderParticles(batch);			
		for(BeamSpot bs: beamPool)
			bs.renderParticles(batch);
		
	}
	
	public void renderOverlayed(SpriteBatch batch){
		//render overlayed tiles

		//batch.disableBlending();
		//batch.setProjectionMatrix(camera.combined);
		//batch.begin();
		if(tileMap.getLayers().get("Overlay") != null)
			tmRenderer.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("Overlay"));		
		//batch.end();
		//batch.enableBlending();		
	}
	

	public void renderLights(SpriteBatch batch) {
		if(!LEVEL_LOADED) return;

		
		if(GameScreen.RENDER_LIGHTS)
		for(Light l: lightPool){
			l.render(batch);
		}
	}
	
	public void update(float delta){
		if(!LEVEL_LOADED) return;

		gametime  += delta;
		last_bullet += delta;
		
		for(Enemy e:enemyPool)
			e.update(delta, camera.position.x);
		
		for(Bullet b:activeBulletPool){
			b.update(delta, camera.position.x);
		}
		
		for(PowerUp p:powersPool)
			p.update(delta, camera.position.x);
		
		for(Portal p: portalPool){
			p.update(delta, camera.position.x);
		}
		
		for(Movers m:moverPool){
			m.update(delta, camera.position.x);
		}
		
		for(Coin c:coinsPool){
			c.update(delta, camera.position.x);
		}
		
		for(GravityReverser gr:gravityPool){
			gr.update(delta, camera.position.x);
		}
		
		for(BeamSpot bs: beamPool){
			bs.update(delta, camera.position.x);
		}
		
		if(levelSwitch != null)
			levelSwitch.update(delta, camera.position.x);
		
		//check for dead bullets and recycle
		int size = activeBulletPool.size;
		while(--size >= 0){
			Bullet b = activeBulletPool.get(size);
			if(b.visible == false)
			{
				activeBulletPool.removeIndex(size);
				bulletPool.add(b);
			}
		}
		
		//update game according to moves
		if(pKeys != null)
			updateMove(pKeys);
		
		if(GameScreen.RENDER_LIGHTS)
		for(Light l: lightPool){
			l.update(delta, camera.position.x);
		}
		
		//execute any pending task if any
		taskQueue.execute();
	}
	
	/** Add manually created lights **/
	public void pushLight(Light l){
		lightPool.add(l);
	}
	
	/**used for cleaning up dead bodies**/
	public void cleanUpLevel(){
		for(Enemy e:enemyPool){
			if(e.DEAD)
				e.setOffScreen();
		}
		
		int size = activeBulletPool.size;
		while(--size >= 0){
			Bullet b = activeBulletPool.get(size);
			if(b.visible == false)
			{	
				activeBulletPool.removeIndex(size);
				b.setOffScreen();
				bulletPool.add(b);
			}
		}
		
		//System.out.println("Bullets:"+activeBulletPool.size);
	}

	public void reset() {
		for(Enemy e:enemyPool)
			e.reset();
		
		//reset bullets
		int size = activeBulletPool.size;
		while(--size >= 0)
		{	Bullet b = activeBulletPool.get(size);
			b.setOffScreen();
			activeBulletPool.removeIndex(size);
			bulletPool.add(b);
		}
		activeBulletPool.clear();
	
		for(PowerUp p:powersPool){
			p.reset();
		}
		
		for(Coin c:coinsPool){
			c.reset();
		}

		for(Light l:lightPool){
			l.enable();
		}
		
		if(levelSwitch != null)
		levelSwitch.reset();
			
		Bullet.BULLET_POWER = false;
		MACHINE_GUN = false;
		
		//fix world gravity
		fixGravity();
	}
	
	public void pauseMusic() {
		if(gameMusic != null)
			gameMusic.pause();
		if(menuMusic != null)
			menuMusic.pause();
	}
	
	public void pauseEverything() {
		pauseMusic();
		
	}

	public void resumeEverything() {
		if(GameScreen.BACKGROUND_MUSIC){
			if(gameMusic != null)
				gameMusic.play();
			if(menuMusic != null)
			{
				if(CURRENT_LEVEL < 1)
					menuMusic.play();
			}			
		}
	}

	public void dimMusic() {
		
	}
	
	public void playMenuMusic(){
		if(!GameScreen.BACKGROUND_MUSIC) return;
		
		if(menuMusic != null)
			menuMusic.play();
	}
	
	/** invert world gravity and player body**/
	public void flipGravity(){
		WORLD_FLIPPED = true;		
		
		world.setGravity(GameScreen.WorldGravityFlipped);
		Player.getInstance().flipBodyAngle();
		
		//flip all enemies
		for(Enemy e:enemyPool){
			e.flipBodyAngle();
		}
		
	}
	
	/** fix world gravity and player body**/
	public void fixGravity(){
		WORLD_FLIPPED = false;

		world.setGravity(GameScreen.WorldGravity);
		Player.getInstance().fixBodyAngle();			

		//fix all enemies
		for(Enemy e:enemyPool){
			e.fixBodyAngle();
		}
		
	}

	public void enemyWallBounce(Fixture fixture) {
		//find enemy and change its direction
		for(Enemy e:enemyPool){
			if(e.getLegFixture().equals(fixture)){
				//set enemy running
				e.RUNNING = true;				
				break;
			}
			else if(e.getSensorFixture().equals(fixture)){
				//revert enemy direction
				e.LEFT_DIRECTION = !e.LEFT_DIRECTION;

				break;
			}
			
		}
		
	}
	

	public void fireBullet() {
		//get a bullet from pool and add to active bullets
		if(!Player.getInstance().CAN_FIRE)
			return;
		
		Player.getInstance().applyFireImpulse();
		
		if(Player.getInstance().getEvolution() == PowerUp.LEVEL_ZERO)
			return;
		
		if(Bullet.BULLET_POWER == true)
			gameScreen.shakeThatAss();
		
		if(bulletPool.size > 0)
		{//get from dead pool
			Bullet b = bulletPool.get(0);
			bulletPool.removeIndex(0);
			
			b.reset();
			b.LEFT_DIRECTION = Player.getInstance().LEFT_DIRECTION;
			
			activeBulletPool.add(b);			
		}
		else
		{//get from active pool
			Bullet b =activeBulletPool.get(0);
			activeBulletPool.removeIndex(0);
			b.reset();
			b.LEFT_DIRECTION = Player.getInstance().LEFT_DIRECTION;
			
			activeBulletPool.add(b);	
		}
		
		
		playFireSound();
	}

	public void test() {
		if(CURRENT_LEVEL < MAX_LEVEL)
			loadNextLevel();
		
		if(CURRENT_LEVEL == MAX_LEVEL)
		if(WORLD_FLIPPED)
			fixGravity();
		else
			flipGravity();
		
	}
	
	/**number of coins collected**/
	public int getCoinCollected(){
		int t = 0;
		for(Coin c:coinsPool){
			if(c.consumed == true)
				t++;
		}
		
		return t;
	}
	
	/**number of enemies killed**/
	public int getEnemyKilled(){
		int t = 0;
		for(Enemy e:enemyPool){
			if(e.DEAD == true)
				t++;
		}		
		return t;
	}
	
	/** increase score and collect coin**/
	public void coinPlayerCollide(Fixture coin) {
		
		for(Coin c:coinsPool)
		{
			if(c.getFixture().equals(coin)){
				int val = c.consume();
				
				GameScreen.getInstance().increaseScore(val);
				break;
			}
		}
	}

	/**find enemy and change set its not flying **/
	public void enemyFlying(Fixture fixture) {
		for(Enemy e:enemyPool){
			if(e.getLegFixture().equals(fixture)){

				//enemy not running
				e.RUNNING = false;

				break;
			}
		}
	}

	public void enemyPlayerCollide(Fixture fixtureE, Fixture fixtureP) {
		//System.out.println("CHECK DEAD");

		for(Enemy e:enemyPool){
			if(e.getHeadFixture().equals(fixtureE)){
				
				//enemy died
				e.setDeath();
				
				LevelGenerate.getInstance().playEnemyHitSound();
				
				//e.hitBullet(Player.getInstance().JUMP_DAMAGE);
				Player.getInstance().makeMiniJump();
				//System.out.println("ENEMY DEAD");
				
				break;
			} else if(e.getSensorFixture().equals(fixtureE) && Player.getInstance().getSensorFixture().equals(fixtureP)){
				
				//player died
				Player.getInstance().setDeath();
				
				if(GameScreen.BACKGROUND_MUSIC)
					Gdx.input.vibrate(50);
				
				break;
			}
		}
		
		
	}
	
	/** kill enemy and bullet **/
	public void enemyBulletCollide(Fixture enemy, Fixture bullet) {
		int damage = 0;
		
		for(Bullet b: activeBulletPool){
			if(b.getBodyFixture().equals(bullet)){
				//bullet got disappeared
				b.makeDead();
				
				damage = b.getDamage();
				break;
			}
		}
		
		for(Enemy e: enemyPool){
			if(e.DEAD == false && e.getBodyFixture().equals(enemy) == true)
			{//enemy got hit by bullet
				e.hitBullet(damage);				
				break;
			}
		}
	}
	
	/** burn player when collide with beam**/
	public void beamPlayerCollide(Fixture beamSensor, boolean enter) {
		for(BeamSpot bs:beamPool){
			if(bs.getFireSensor().equals(beamSensor)){
				
				//player is inside fire sensor
				bs.PLAYER_INSIDE = enter;			
				
				break;
			}
		}
		
	}
	
	/** remove bullet **/
	public void bulletPlatformCollide(Fixture bullet){
		for(Bullet b: activeBulletPool){
			if(b.getBodyFixture().equals(bullet)){
				//bullet got disappeared
				b.makeDead();
				
				break;
			}
		}
	}
	
	/** keep track of user inputs **/
	public void updateMove(HashMap<MyInputProcessor.CONTROL, Boolean> keys) {
		pKeys = keys;
		
		//automatic bullet fire
		if(pKeys.get(MyInputProcessor.CONTROL.FIRE) == true)
		{
			if(last_bullet > 0.1f)
			{
				fireBullet();
				last_bullet = 0;
			}
		}

	}

	/**Power gained, evolve player**/
	public void powerUpPlayer(Fixture powerUp) {
		for(PowerUp p:powersPool){
			if(p.getFixture() == powerUp && p.consumed == false){
				
				if(p.consume() > 0)
					Player.getInstance().evolvePlayer();
				
				break;
			}			
		}
		
	}

	/** is level cleared ??? **/
	public void levelClearPortal(Fixture portal) {
		for(Portal p:portalPool){
			if(p.PORTAL_TYPE == Portal.EXIT && portal.equals(p.getSensorFixture())){
				//exit portal reached
				if(p.ENABLED)
				{
					//start animation of going out
					Player.getInstance().TELEPORTING_OUT = true;

					levelClear();
				}
				
				
				break;
			}
		}
		
	}
	
	/** show level screen and load new **/
	private void levelClear() {
		//uncomment this after making teleport animation
		//playPortalSound();
		
		if(GameScreen.DEBUG)
			MyGame.sop("Level Cleared:"+CURRENT_LEVEL);
		
		if(CURRENT_LEVEL == 0)
		{
			taskQueue.push(TaskQueue.CLEAR_LEVEL);
		}
		else
			gameScreen.showLevelClear();
		
		CURRENT_LEVEL_CLEARED = true;
		
	}
	
	/** level portal switch **/
	public void switchPlayerCollide(Fixture lSwitch) {
		if(levelSwitch!= null && lSwitch.equals(levelSwitch.getFixture())){
			levelSwitch.toggle();
			
			for(Portal p :portalPool){
				if(p.PORTAL_TYPE == Portal.EXIT)
				{
					if(levelSwitch.STATE_ENABLED)
					{//enable protal
						p.ENABLED = true;
					}
					else{
						//disable portal
						p.ENABLED = false;
					}
					
				}
			}
		}
	}
	
	/** gravity reversers**/
	public void gravityPlayerCollide(Fixture reverser) {
		for(GravityReverser gr:gravityPool){
			if(gr.getFixture().equals(reverser) == true){
				if(gr.TYPE == GravityReverser.FIXED){
					taskQueue.push(TaskQueue.GRAVITY_FIX);
				}
				else
					taskQueue.push(TaskQueue.GRAVITY_FLI);
			}
		}
		
	}
	
	public void playCoinSound(){
		if(!GameScreen.BACKGROUND_MUSIC) return;
		
		coinSound.play();
		
		
	}
	
	public void playFireSound(){
		if(!GameScreen.BACKGROUND_MUSIC) return;

		if(fireSound != null)
			fireSound.play();
	}
	
	public void playPlayerHitSound(){
		if(!GameScreen.BACKGROUND_MUSIC) return;

		if(playerHurtSound != null)
			playerHurtSound.play();
	}
	
	public void playEnemyHitSound(){
		if(!GameScreen.BACKGROUND_MUSIC) return;

		if(enemyHurtSound != null)
			enemyHurtSound.play();
	}
	
	public void playLevelUpSound(){
		if(!GameScreen.BACKGROUND_MUSIC) return;

		if(levelUpSound != null)
			levelUpSound.play();
	}
	
//	public void playPortalSound(){
//		if(!GameScreen.BACKGROUND_MUSIC) return;
//
//		if(portalSound != null)
//			portalSound.play();
//	}
	
	public Switch getLevelSwitch(){
		return levelSwitch;
	}
	
	public void dispose(){
		pauseMusic();
		dispose(false);
	}

	public void dispose(boolean levelClear) {
		if(!levelClear){
			//clear only when game ends
			
			tmRenderer.dispose();
			tileMap.dispose();			

			//clear bullets
			for(Bullet b:bulletPool)
				b.dispose();
			bulletPool.clear();
			for(Bullet b:activeBulletPool)
				b.dispose();
			activeBulletPool.clear();
			
		}
		
		if(levelSwitch != null)
		{
			levelSwitch.dispose();
		}
		levelSwitch = null;
		
		for(Body b:platformPool){
			world.destroyBody(b);
		}
		platformPool.clear();
		
		for(Body b:spikesPool){
			world.destroyBody(b);
		}
		spikesPool.clear();
		
		for(Coin c:coinsPool){
			c.dispose();
		}
		coinsPool.clear();
		
		for(Movers m: moverPool)
			m.dispose();
		moverPool.clear();
		
		//clear enemies
		for(Enemy e:enemyPool)
			e.dispose();
		enemyPool.clear();		
		
		for(PowerUp p:powersPool)
			p.dispose();
		powersPool.clear();
		
		for(Light l:lightPool)
			l.dispose();
		lightPool.clear();
		
		for(Portal p: portalPool)
			p.dispose();
		portalPool.clear();
		
		for(GravityReverser gr: gravityPool){
			gr.dispose();
		}
		gravityPool.clear();
		
		for(BeamSpot bs: beamPool){
			bs.dispose();
		}
		beamPool.clear();
		
		//clear image layers used in rendering
		tileImageLayers.clear();
		
		//clear tasks
		taskQueue.clear();
		
		LEVEL_LOADED = false;
	}



    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) * PTP,
                                   (rectangle.y + rectangle.height * 0.5f ) * PTP);
        polygon.setAsBox(rectangle.width * 0.5f * PTP,
                         rectangle.height * 0.5f * PTP,
                         size,
                         0.0f);        
        
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius * PTP);
        circleShape.setPosition(new Vector2(circle.x * PTP, circle.y * PTP));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            //System.out.println(vertices[i]* PTP);
            worldVertices[i] = vertices[i] * PTP;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] * PTP;
            worldVertices[i].y = vertices[i * 2 + 1] * PTP;
        }

        ChainShape chain = new ChainShape(); 
        chain.createChain(worldVertices);
        return chain;
    }
	
	public static LevelGenerate getInstance(){
		return _level;
	}
	
	//this class will enqueue task which needs to be executed outside world step
	class TaskQueue {

		public static final int GRAVITY_FLI = 0;
		public static final int GRAVITY_FIX = 1;
		public static final int CLEAR_LEVEL = 2;

		Array<Integer> tasks;
		
		public TaskQueue(){
			tasks= new Array<Integer>();
			
		}
		
		public void execute(){
			for(Integer i : tasks){
				switch(i){
				case GRAVITY_FLI: flipGravity();
					break;
				case GRAVITY_FIX: fixGravity();
					break;
				case CLEAR_LEVEL:{
					//change game state
					GameScreen.CURRENT_STATE = GameState.EVOLVING;
					
					//directly jump to next level if this is intro
					loadNextLevel();
					
					break;
				}
				}
			}
			
			tasks.clear();
		}
		
		public void push(int t){
			tasks.add(new Integer(t));
		}
		
		public void clear(){
			tasks.clear();
		}
	}


}
