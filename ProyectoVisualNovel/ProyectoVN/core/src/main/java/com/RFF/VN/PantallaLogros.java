package com.RFF.VN;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaLogros implements Screen{
	private final Main game;
	private Stage stage;
	private Skin skin;
	private Repository repository;
	
	private Image fondo;
	
	public PantallaLogros(Main game) {
		this.game = game;
		this.repository = new Repository();
		this.stage = new Stage(new ScreenViewport());
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.skin.add("default-font", game.getFuente(), BitmapFont.class);
		
		this.skin.get(Label.LabelStyle.class).font = game.getFuente();
		this.skin.get(TextField.TextFieldStyle.class).font = game.getFuente();
		this.skin.get(TextButton.TextButtonStyle.class).font = game.getFuente();
	}
	
	@Override
	public void show() {
		//Gdx.input.setCursorCatched(false);
		//Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
		
		Gdx.input.setInputProcessor(stage);
		
		game.controlarMusicaMenu("musica_menus.mp3", true);

	    fondo = new Image(new Texture(Gdx.files.internal("fondos/fondo_menu_logros.png")));
	    fondo.setFillParent(true);
	    stage.addActor(fondo);
	    
		Table mainTable = new Table();
		mainTable.setFillParent(true);
		stage.addActor(mainTable);
		
		Label titulo = new Label("TUS LOGROS", skin);
		titulo.setFontScale(1.5f);
		mainTable.add(titulo).padBottom(30).row();
		
		Table listaLogros = new Table();
		listaLogros.top();
		
		List<LogroDetalleDTO> logros = repository.obtenerListaLogros(game.idUsuarioLogueado);
		
		for(LogroDetalleDTO l: logros) {
			Table fila = new Table(skin);
			fila.background("textfield");
			
			Label nameLabel = new Label(l.nombre, skin);
			Label descLabel = new Label(l.descripcion, skin);
			descLabel.setFontScale(0.8f);
			
			if(l.conseguido) {
				nameLabel.setColor(Color.GOLD);
				Label fechaLabel = new Label("Ganado el: " + l.fechaConseguido, skin);
				fechaLabel.setFontScale(0.6f);
				fila.add(nameLabel).left().row();
				fila.add(descLabel).left().row();
				fila.add(fechaLabel).left();
			}else {
				nameLabel.setColor(Color.GRAY);
				descLabel.setColor(Color.GRAY);
				fila.add(nameLabel).left().row();
				fila.add(descLabel).left();
			}
			
			listaLogros.add(fila).width(600).pad(10).row();
	
		}
		
		ScrollPane scroll = new ScrollPane(listaLogros, skin);
		mainTable.add(scroll).width(650).height(400).row();
		
		TextButton btnVolver = new TextButton("Volver al Menú", skin);
		btnVolver.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new PantallaMenu(game));
			}
		});
		mainTable.add(btnVolver).padTop(20).width(200);
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0,0,0,1);
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		game.getViewport().update(width, height, true);
	}
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {}
	@Override public void dispose() {
    	stage.dispose();
    	skin.dispose();
    }
	

}
