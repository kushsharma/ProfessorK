package utils;

import objects.Player;
import screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.softnuke.biosleep.MyGame;

public class Cinema {

	//will handle first story intro and in between messages
	Stage stage;
	OrthographicCamera camera;
	
	float bWIDTH = MyGame.bWIDTH;
	float bHEIGHT = MyGame.bHEIGHT;
	float WIDTH = MyGame.WIDTH;
	float HEIGHT = MyGame.HEIGHT;
	
	float time = 0;
	float anime_time = 0;
	float anime_delay = 2f;
	int MOV_STAGE = 0;
	boolean CAN_EXECUTE = true;
	
	public static final int MOV_NONE = 0;
	public static final int MOV_INTRO = 1;
	public static final int MOV_INTRO_LEVEL = 2;
	public static final int MOV_TUT1 = 3;
	public static final int MOV_TUT_LEVEL_2 = 4;
	public static final int MOV_TUT_LEVEL_3 = 5;
	public static final int MOV_TUT_LEVEL_4 = 6;
	public static final int MOV_TUT_LEVEL_5 = 7;
	public static final int MOV_TUT_LEVEL_6 = 8;
	public static final int MOV_TUT_LEVEL_7 = 9;
	public static final int MOV_TUT_LEVEL_8 = 10;
	public static final int MOV_TUT_LEVEL_9 = 11;
	public static final int MOV_TUT_LEVEL_10 = 12;
	public static final int MOV_TUT_LEVEL_11 = 13;
	public static final int MOV_TUT_LEVEL_12 = 14;
	public static final int MOV_TUT_LEVEL_13 = 15;

	int CINEMA_TYPE = 0;
	
	Image intro1, introAlien, introPlayerSleep, introPlayerFly, introStar, introAlienRays, introMillFan;
	Sprite introLevelPods, introScientist;
	ParticleEffect introSmokeEffect;
	Texture intro1T;
	
	BitmapFont fontMedium;
	
	TextButton introText1;
	TextureAtlas atlas = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class);
	
	public Cinema(Stage stg, OrthographicCamera cam){
		stage = stg;
		camera = cam;
	
		init();
	}
	
	private void init(){
		fontMedium = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.small_font, BitmapFont.class);
		
		loadIntroMovie();
		loadIntroLevel();
		
	}
	
	private void loadIntroMovie() {
		//dispose this
		intro1T = new Texture("back/intro.png");
		intro1T.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		intro1 = new Image(intro1T);
		intro1.setSize(WIDTH, HEIGHT);
		intro1.setPosition(0, 0);
		intro1.addListener(new InputListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				MOV_STAGE ++;
				anime_time = 0;
				CAN_EXECUTE = true;
				
				return true;
			}
			
		});
		intro1.setVisible(false);		
		stage.addActor(intro1);
		
		introSmokeEffect = new ParticleEffect(GameScreen.getInstance().getAssetLord().manager.get(AssetLord.intro_somke_particle, ParticleEffect.class));
		introSmokeEffect.scaleEffect(0.4f);
		introSmokeEffect.setPosition(WIDTH - WIDTH*0.18f, HEIGHT*0.5f - HEIGHT*0.22f);
		
		//dispose this
		Texture intro2T = new Texture("back/intro-player-fly.png");
		intro2T.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		Texture intro3T = new Texture("back/intro-alien.png");
		intro3T.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		Texture intro4T = new Texture("back/intro-alien-rays.png");
		intro4T.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		Texture intro5T = new Texture("back/intro-star.png");
		intro5T.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

				
		introPlayerSleep = new Image(atlas.findRegion("intro-player-sleep"));
		introPlayerSleep.setSize(WIDTH/16, WIDTH/16 * introPlayerSleep.getHeight()/introPlayerSleep.getWidth());
		introPlayerSleep.setPosition(WIDTH - WIDTH*0.3f, HEIGHT/9);		
		introPlayerSleep.setVisible(false);		
		stage.addActor(introPlayerSleep);
		
		introPlayerFly = new Image(intro2T);
		introPlayerFly.setSize(WIDTH/10, WIDTH/10 * introPlayerFly.getHeight()/introPlayerFly.getWidth());
		introPlayerFly.setPosition(introPlayerSleep.getX() + introPlayerSleep.getWidth()/5, introPlayerSleep.getY()*1.4f);		
		introPlayerFly.setVisible(false);		
		stage.addActor(introPlayerFly);
		
		introAlienRays = new Image(intro4T);
		introAlienRays.setSize(WIDTH/22, WIDTH/26 * introAlienRays.getHeight()/introAlienRays.getWidth());
		introAlienRays.setPosition(introPlayerSleep.getX(), introPlayerSleep.getY());		
		introAlienRays.setVisible(false);		
		stage.addActor(introAlienRays);
		
		introAlien = new Image(intro3T);
		introAlien.setSize(WIDTH/22, WIDTH/26 * introAlien.getHeight()/introAlien.getWidth());
		introAlien.setPosition(WIDTH - WIDTH*0.3f, HEIGHT + introAlien.getHeight());		
		introAlien.setVisible(false);		
		stage.addActor(introAlien);
		
		introStar = new Image(intro5T);
		introStar.setSize(WIDTH/30, WIDTH/30 * introStar.getHeight()/introStar.getWidth());
		introStar.setPosition(WIDTH- WIDTH/20, HEIGHT - HEIGHT/12);		
		introStar.setVisible(false);		
		stage.addActor(introStar);
		
		
		introMillFan = new Image(atlas.findRegion("intro-wind-mil-fan"));
		introMillFan.setSize(WIDTH/24, WIDTH/24 * introMillFan.getHeight()/introMillFan.getWidth());
		introMillFan.setOrigin(Align.center);
		introMillFan.setPosition( WIDTH*0.162f, HEIGHT*0.16f);	
		introMillFan.setVisible(false);
		stage.addActor(introMillFan);
		
	}
	
	private void loadIntroLevel(){
		
		//Texture introLevel1 = new Texture("level/intro-back-pods.png");
		//introLevel1.setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
		
		introLevelPods = new Sprite(atlas.findRegion("intro-back-pods"));
		introLevelPods.setPosition(camera.position.x - bWIDTH/4, 4);
		introLevelPods.setSize(bWIDTH/3, bWIDTH/3 * introLevelPods.getHeight()/introLevelPods.getWidth());
		introLevelPods.setFlip(true, false);

		//Texture introLevel2 = new Texture("level/anime/scientist.png");
		//introLevel2.setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
		
		introScientist = new Sprite(atlas.findRegion("scientist"));
		introScientist.setPosition(camera.position.x + bWIDTH/5, 4);
		introScientist.setSize(0.7f, 0.7f * introScientist.getHeight()/introScientist.getWidth());
		introScientist.setFlip(true, false);
		
		//Texture introLevel3 = new Texture("level/anime/scientist-2.png");
		//introLevel3.setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
		
		
		TextButtonStyle tStyle = new TextButtonStyle();
		tStyle.font = fontMedium;
		tStyle.fontColor = Color.WHITE;
		
		introText1 = new TextButton("lol",tStyle);
		introText1.align(Align.center);
		introText1.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				MOV_STAGE ++;
				anime_time = 0;
				CAN_EXECUTE = true;
				
		 		return true;
		 	}

		});
		
		introText1.setWidth(WIDTH * 0.8f);		
		introText1.setPosition(WIDTH/2 - introText1.getWidth()/2, HEIGHT*2);
		introText1.setVisible(false);
		stage.addActor(introText1);
		
	}

	public void start(int t){
		MOV_STAGE = 0;
		CINEMA_TYPE = t;
		
		switch(CINEMA_TYPE){
			case MOV_INTRO:{
				anime_delay = 2f;
				LevelGenerate.getInstance().playMenuMusic();
				
				introSmokeEffect.start();
				intro1.setVisible(true);
				introPlayerSleep.setVisible(true);
				introMillFan.setVisible(true);
				
				GameScreen.getInstance().hideControls();
				
				break;
			}
			case MOV_INTRO_LEVEL:{
				introText1.clearActions();
					anime_delay = 6f;
				break;
			}
			case MOV_TUT_LEVEL_3:{
				introText1.clearActions();
				anime_delay = 8f;
			break;
			}
			case MOV_TUT_LEVEL_4:{
				introText1.clearActions();
				anime_delay = 8f;
			break;
			}
			case MOV_TUT_LEVEL_5:{
				introText1.clearActions();
				anime_delay = 6f;				
			break;
			}
			case MOV_TUT_LEVEL_6:{
				introText1.clearActions();
				anime_delay = 6f;				
			break;
			}
			case MOV_TUT_LEVEL_7:{
				introText1.clearActions();
				anime_delay = 8f;				
			break;
			}
			case MOV_TUT_LEVEL_8:{
				introText1.clearActions();
				anime_delay = 8f;				
			break;
			}
			case MOV_TUT_LEVEL_9:{
				introText1.clearActions();
				anime_delay = 8f;				
			break;
			}
			case MOV_TUT_LEVEL_10:{
				introText1.clearActions();
				anime_delay = 6f;				
			break;
			}
			case MOV_TUT_LEVEL_11:{
				introText1.clearActions();
				anime_delay = 10f;				
			break;
			}
			case MOV_TUT_LEVEL_12:{
				introText1.clearActions();
				anime_delay = 6f;				
			break;
			}
			case MOV_TUT_LEVEL_13:{
				introText1.clearActions();
				anime_delay = 5f;				
			break;
			}
		}
	}
	
	public void render(SpriteBatch batch){
		switch(CINEMA_TYPE){
			case MOV_INTRO_LEVEL:{
				introLevelPods.draw(batch);
				introScientist.draw(batch);
				
				break;
			}			
		}
	}
	
	public void renderUI(SpriteBatch batch){
		switch(CINEMA_TYPE){
			case MOV_INTRO:{
				introSmokeEffect.draw(batch);
			break;
			}
			
		}
	}
	
	public void update(float delta){
		time += delta;
		
		if(CINEMA_TYPE != MOV_NONE){
			anime_time += delta;
			
			if(anime_time > anime_delay)
			{
				anime_time = 0;
				MOV_STAGE++;
				CAN_EXECUTE = true;
			}
		}
		
		//needs continuous  execution
		switch(CINEMA_TYPE){
			case MOV_INTRO:{
				introSmokeEffect.update(delta);
				introMillFan.setRotation(360 - (time*50)%360);

				break;
			}
			case MOV_INTRO_LEVEL:{
				if(Player.getInstance().getPosition().x > introScientist.getX())
					introScientist.setFlip(false, false);
				else
					introScientist.setFlip(true, false);
			}
		}
		
		//one time execution
		if(CAN_EXECUTE)
		{
			switch(CINEMA_TYPE){
				case MOV_INTRO:{
					introSmokeEffect.update(delta);
					
					handleMovIntro();
					
					break;
				}
				case MOV_INTRO_LEVEL:{
					
					handleIntroLevel();
					
					break;
				}
				case MOV_TUT_LEVEL_3:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: Psss! Can you hear me?\nI am trying to talk using modern version\nof cell phone...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
					}break;
					case 2:{
						introText1.setText("Prof K: I forgot to introduce myself.\nI am Professor Kush...");
					}break;
					case 3:{
						introText1.setText("Prof K: I am also stuck here since last 10 years.\nYou are my last hope.");
					}break;
					case 4:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
					}		
					}
					
					break;
				}
				case MOV_TUT_LEVEL_4:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: Remember! Drink as much milk as you can...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
					}break;
					case 2:{
						introText1.setText("Prof K: 'Horlicks' will make you equally stronger & sharper.");
					}break;
					case 3:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_5:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: Some levels may have hidden walls.");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_6:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: You might have noticed by now...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
					}break;
					case 2:{
						introText1.setText("Prof K: Everytime you teleport,\nyou lose super human abilities...");
					}break;
					case 3:{
						introText1.setText("Prof K: Your tiny brain is not capable\nof understanding the whole process.\nIn layman terms...");
					}break;
					case 4:{
						introText1.setText("Prof K: Teleporter cannot rearrange 'Horlicks'\n atoms along with milk in the body...");
					}break;
					case 5:{
						introText1.setText("Prof K: Once atoms are disintegrated from entry portal,\nthey are lost as a whole...");
					}break;
					case 6:{
						introText1.setText("Prof K: But you don't have to worry about anything...");
					}break;
					case 7:{
						introText1.setText("Prof K: As long as you keep drinking milk!");
					}break;
					case 8:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_7:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: Watch out for neutron beam exhaust.");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_8:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: I don't know why but i just love\nseeing you try so hard...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.setText("Prof K: By the way, here is a piece of advice...");
					}break;
					case 3:{
						introText1.setText("Prof K: Once you get out of here, buy some clothes.\nClowns look better than you.");
					}break;
					case 4:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_9:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: Some levels may need you to activate\nthe switch to powerup portal.");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_10:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: You may find here enough milk to transform\neven more powerful...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.setText("Prof K: But remember to use that power wisely.");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 3:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_11:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: You are really enjoying the power, don't you?");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_12:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: Gravity is a great force to master...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.setText("Prof K: I realized this by falling from stairs,\ntrust me worth it! Ouch...");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 3:{
						introText1.setText("Prof K: I hope you enjoyed talking to me,\nask your master to rate this world well.");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 4:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
				case MOV_TUT_LEVEL_13:{
					switch(MOV_STAGE){
					case 1:{
						introText1.setText("Prof K: How could you find this private area you stupid kid!");
						introText1.setVisible(true);
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
						break;
					}
					case 2:{
						introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
						
						MOV_STAGE = 0;
						CINEMA_TYPE = MOV_NONE;
						break;
					}		
					}
					break;
				}
			}
			
			
			//executed once already
			CAN_EXECUTE = false;
			
		}
	}
	
	private void handleIntroLevel(){		
		
		switch(MOV_STAGE){
		case 1:{
			introText1.setText("You: What just happened?");
			introText1.setVisible(true);
			introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT - introText1.getHeight()*3, 1f));
		}break;
		case 2:{
			introText1.setText("Prof K: Calm down! You were abducted by aliens...");
		}break;
		case 3:{
			introText1.setText("Prof K: Well, it's normal to freak out actually.\n But before that...");
		}break;
		case 4:{
			introText1.setText("Prof K: I don't have much time,\n so listen closely...");
		}break;
		case 5:{
			introText1.setText("Prof K: The race that rules here is similar to humans,\nexcept that they are evil and love milk too much...");
		}break;
		case 6:{
			introText1.setText("Prof K: You have to fight back to survive.\nLuckily I have been working on a compound...");
		}break;
		case 7:{
			introText1.setText("Prof K: This compound can enhance human ability by \ngenetically modifying ordinary milk...");
		}break;
		case 8:{
			introText1.setText("Prof K: And this planet has more than enough.\nHere take this, I named it 'Horlicks'...");
		}break;
		case 9:{
			introText1.setText("Prof K: Just remember, drink as much milk as you can!\n You should go now.");
			
			if(GameScreen.getInstance().getScoreManager().TUTORIAL_LEVEL == 0)
				GameScreen.getInstance().showControls();
		}break;
		case 10:{
			introText1.addAction(Actions.moveTo(introText1.getX(), HEIGHT*2, 1f));
			
		}break;
		}
	}
	
	private void handleMovIntro() {
		switch(MOV_STAGE){
		case 1:{
			introStar.setVisible(true);
		}break;
		case 2:{
			introStar.setVisible(false);
			introAlien.setVisible(true);
			introAlien.addAction(Actions.moveTo(WIDTH - WIDTH*0.3f, HEIGHT/9 + introAlienRays.getHeight(), 1f, Interpolation.sineOut));
		}break;
		case 3:{
			
		}break;
		case 4:{
			introAlien.setPosition(WIDTH - WIDTH*0.3f, HEIGHT/9 + introAlienRays.getHeight());
			introAlien.clearActions();
			introAlienRays.setVisible(true);
		}break;
		case 5:{
			introPlayerFly.setVisible(true);
			introPlayerSleep.setVisible(false);
		}break;
		case 6:{
			introPlayerFly.setVisible(false);
		}break;
		case 7:{
			introAlienRays.setVisible(false);
			introAlien.addAction(Actions.moveTo(WIDTH - WIDTH*0.3f, 2*HEIGHT, 2f, Interpolation.sineOut));
		}break;
		case 8:{
			introAlien.setVisible(false);
			introStar.setVisible(true);
		}break;
		case 9:{
			intro1.addAction(Actions.sequence(Actions.after(Actions.alpha(0, 1f)), Actions.hide()));
			introAlien.setVisible(false);
			introAlienRays.setVisible(false);
			introStar.setVisible(false);
			introSmokeEffect.allowCompletion();
			introMillFan.setVisible(false);
			
			GameScreen.CURRENT_STATE = GameState.RUNNING;
			
			if(GameScreen.getInstance().getScoreManager().TUTORIAL_LEVEL != 0)
				GameScreen.getInstance().showControls();

			
			MOV_STAGE = 0;
			CINEMA_TYPE = MOV_NONE;
			
			start(MOV_INTRO_LEVEL);
		}break;
		case 10:{
			
		}break;
	}
		
	}

	private void startLevel1() {

	}

	public void dispose(){
		intro1T.dispose();
		
	}

	public void clearCinema() {
		introText1.setVisible(false);
		CINEMA_TYPE = MOV_NONE;
	}

	/** notification if there is anything that needs to be done in this new level**/
	public void levelUpdate(int level) {
		switch(level){
		case 3:{
			start(MOV_TUT_LEVEL_3);
			break;
		}
		case 4:{
			start(MOV_TUT_LEVEL_4);
			break;
		}
		case 5:{
			start(MOV_TUT_LEVEL_5);
			break;
		}
		case 6:{
			start(MOV_TUT_LEVEL_6);
			break;
		}
		case 7:{
			start(MOV_TUT_LEVEL_7);
			break;
		}
		case 8:{
			start(MOV_TUT_LEVEL_8);
			break;
		}
		case 9:{
			start(MOV_TUT_LEVEL_9);
			break;
		}
		case 10:{
			start(MOV_TUT_LEVEL_10);
			break;
		}
		case 11:{
			start(MOV_TUT_LEVEL_11);
			break;
		}
		case 12:{
			start(MOV_TUT_LEVEL_12);
			break;
		}
		case 13:{
			start(MOV_TUT_LEVEL_13);
			break;
		}
		}
	}
	
}
