package objects;

import java.util.HashMap;

import screens.GameScreen;
import utils.AssetLord;
import utils.MyInputProcessor.CONTROL;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.softnuke.biosleep.MyGame;

public class GameObject {
	
	public float height, width;
	protected Vector2 position;
	Vector2 startPos;
	
	float gravityScale = 0f;
	public static final Vector2 CENTER_VECTOR = new Vector2(0,0);
	public int SCORE_VALUE = 50;
	
	public boolean DEAD = false;
	protected boolean visible = false;

	GameScreen gameScreen = GameScreen.getInstance();
		
	Body body;
	World world;
	Shape shape;
	BodyDef bodyDef;
	FixtureDef fixtureDef;
	//body is for whole body square
	//sensor for hands, revert movement direction
	//head for player kill by jumping on it
	//leg for platform sensor
	Fixture sensorFixture, bodyFixture, headFixture, legFixture;
	
	float bHEIGHT = MyGame.bHEIGHT;
	float bWIDTH = MyGame.bWIDTH;
	
	float time = 0f;
	Animation idleAnime, moveAnime, jumpAnime, fireAnime;

	public HashMap<CONTROL, Boolean> pKeys = null;
	TextureAtlas atlas = GameScreen.getInstance().getAssetLord().manager.get(AssetLord.game_atlas, TextureAtlas.class);
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Vector2 getStartPosition(){
		return startPos;
	}
	
	public Body getBody(){
		return body;
	}
	
	public Fixture getSensorFixture(){
		return sensorFixture;
	}
	
	public Fixture getLegFixture(){
		return legFixture;
	}
	
	public Fixture getHeadFixture(){
		return headFixture;
	}
	
	public Fixture getBodyFixture(){
		return bodyFixture;
	}
}
