package parallax;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.softnuke.biosleep.MyGame;

public class Parallax {

	OrthographicCamera camera;
	
	int HEIGHT, WIDTH;
	float bHEIGHT, bWIDTH;
	float ASPECT_RATIO ;
	Body body;
	float time = 0;
	float positionx = 0;
	
	ShaderProgram shader;
	Mesh quadMesh;
	
	Texture back1,back2,back3;
	
	public Parallax(OrthographicCamera cam ){
		
		bHEIGHT = MyGame.bHEIGHT;
		bWIDTH = MyGame.bWIDTH;
		WIDTH = MyGame.WIDTH;
		HEIGHT = MyGame.HEIGHT;
		
		camera = cam;
		
		shader = new ShaderProgram(Gdx.files.internal("parallax/vert.k"), Gdx.files.internal("parallax/frag.k"));
		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		quadMesh = createQuad(0, 0,
				WIDTH, 0,
				WIDTH, HEIGHT,
				0, HEIGHT);
		
		
		back1 = new Texture("parallax/pixel-back.png");
		back1.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		back2 = new Texture("parallax/back-small-front.png");
		back2.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		back3 = new Texture("parallax/back-small-mid.png");
		back3.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
	}
	
	public void render(){
				
		shader.begin();
		
		back3.bind(2);
		shader.setUniformi("u_texture2", 2);
		back2.bind(1);
		shader.setUniformi("u_texture1", 1);
		back1.bind(0);
		shader.setUniformi("u_texture0", 0);

		shader.setUniformMatrix("u_projTrans",  camera.combined);
		shader.setUniformf("travelDistance", -positionx);
		
		quadMesh.render(shader, GL20.GL_TRIANGLE_FAN);
		
		shader.end();	
	}
	
	public void update(float delta, float posx){
		time += delta;
		positionx = posx/10f; 
	}
	
	public static Mesh createQuad(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		float[] verts = new float[20];
		int i = 0;

		verts[i++] = x1; // x1
		verts[i++] = y1; // y1
		verts[i++] = 0;
		verts[i++] = 1f; // u1
		verts[i++] = 1f; // v1

		verts[i++] = x2; // x2
		verts[i++] = y2; // y2
		verts[i++] = 0;
		verts[i++] = 0f; // u2
		verts[i++] = 1f; // v2

		verts[i++] = x3; // x3
		verts[i++] = y3; // y2
		verts[i++] = 0;
		verts[i++] = 0f; // u3
		verts[i++] = 0f; // v3

		verts[i++] = x4; // x4
		verts[i++] = y4; // y4
		verts[i++] = 0;
		verts[i++] = 1f; // u4
		verts[i++] = 0f; // v4

		// static mesh with 4 vertices and no
		// indices		
		Mesh mesh = new Mesh(true, 4, 0, 
				new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), 
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

		mesh.setVertices(verts);
		return mesh;

	}
	
	public void dispose(){		
		shader.dispose();
		quadMesh.dispose();
		
		back1.dispose();
		back2.dispose();
		back3.dispose();
		
	}
}
