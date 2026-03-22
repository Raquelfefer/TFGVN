package com.RFF.VN;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.ExtendViewport;


public class Main extends Game {
    private SpriteBatch batch;
    private BitmapFont fuente;
    private ExtendViewport viewport;
    private OrthographicCamera camera;
    
    public int idUsuarioLogueado;
    public String nombreUsuarioLogueado;
    
    public Music musicaMenu;
    private String nombreMusicaMenu = "";
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);
       
        //Cargar fuente para todo el juego
        generarFuente();
        
        //Conexión global a MaraiDB
        ConexionBD.obtenerConexion();
        
        //Empezar por la pantalla de Login
        this.setScreen(new PantallaLogin(this));
    }
    
    private void generarFuente() {
    	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuente.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 18; 
        parameter.color = Color.WHITE;
        parameter.borderWidth = 3; 
        parameter.borderColor = Color.BLACK;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "áéíóúÁÉÍÓÚñÑ¿?¡!-—";
        
        fuente = generator.generateFont(parameter); 
    
        generator.dispose(); 
    }
    
    public void controlarMusicaMenu(String nombreArchivo, boolean reproducir) {
    	if(!reproducir) {
    		if(musicaMenu != null) {
    			musicaMenu.stop();
    			nombreMusicaMenu = "";
    		}
    		return;
    	}
    	
    	if (nombreMusicaMenu.equals(nombreArchivo)) return;
    	
    	if (musicaMenu != null) musicaMenu.dispose();
    	
    	musicaMenu = Gdx.audio.newMusic(Gdx.files.internal("musica/"+ nombreArchivo));
    	musicaMenu.setLooping(true);
    	musicaMenu.play();
    	nombreMusicaMenu = nombreArchivo;
    }

    @Override
    public void render() {
    	//Llama al render de la pantalla activa
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        fuente.dispose();
    }
    
    public SpriteBatch getBatch() { return batch;}
    public BitmapFont getFuente() { return fuente;}
    public ExtendViewport getViewport() { return viewport;}
    public OrthographicCamera getCamera() { return camera;}
    
   
}
