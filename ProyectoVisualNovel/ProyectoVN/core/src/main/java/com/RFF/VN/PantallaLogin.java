package com.RFF.VN;


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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaLogin implements Screen{
	private final Main game;
	private Stage stage;
	private Skin skin;
	private Repository repository;
	
	private Image fondo;
	
	public PantallaLogin(Main game) {
		this.game = game;
		this.repository = new Repository();
		this.stage = new Stage(new ScreenViewport()); // Contenedor de botones y texto
		this.skin = new Skin(Gdx.files.internal("uiskin.json")); // Cargamos el estilo visual
		
		this.skin.add("default-font", game.getFuente(), BitmapFont.class);
		this.skin.get(Label.LabelStyle.class).font = game.getFuente();
	    this.skin.get(TextField.TextFieldStyle.class).font = game.getFuente();
	    this.skin.get(TextButton.TextButtonStyle.class).font = game.getFuente();
	}
	
	@Override
	public void show() {
		//Gdx.input.setCursorCatched(false);
		//Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
		
		Gdx.input.setInputProcessor(stage); // Stage recibe los clics del ratón
		
		game.controlarMusicaMenu("musica_menus.mp3", true);
	    
	    fondo = new Image(new Texture(Gdx.files.internal("fondos/fondo_login.png")));
	    fondo.setFillParent(true);
	    stage.addActor(fondo);
	    
		Table table = new Table(); // Crear tabla para organizar los elementos en pantalla
		table.setFillParent(true); // Ocupa toda la pantalla
		stage.addActor(table);
		
		// ELEMENTOS DEL FORMULARIO
		Label labelUsuario = new Label("Nombre de Usuario:", skin);
		final TextField campoUsuario = new TextField("", skin);
		Label labelPassword = new Label("Contraseña:", skin);
		final TextField campoPassword = new TextField("", skin);
		campoPassword.setPasswordMode(true);
		campoPassword.setPasswordCharacter('*');
		
		final Label mensajeEstado = new Label("", skin);
		mensajeEstado.setColor(Color.RED);
		
		TextButton botonLogin = new TextButton("Entrar", skin);
		TextButton botonRegistro = new TextButton("Crear usuario", skin);
		TextButton botonSalir = new TextButton("Salir", skin);
		
		// Añadimos la tabla
		table.add(mensajeEstado).colspan(2).padBottom(10).row();
		table.add(labelUsuario).padBottom(10).row();
		table.add(campoUsuario).width(300).padBottom(20).row();
		table.add(labelPassword).padBottom(10).row();
		table.add(campoPassword).width(300).padBottom(20).row();
		table.add(botonLogin).width(200);
		table.add(botonRegistro).width(140).pad(5).padBottom(20).row();
		
		
		//El botón salir se muestra solo si estamos en escritorio
		if(Gdx.app.getType() != com.badlogic.gdx.Application.ApplicationType.WebGL) {
			table.add(botonSalir).width(200).colspan(2).padTop(10);
			
			botonSalir.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					Gdx.app.exit();
				}
			});
		}else {
			botonSalir.setVisible(false); //Si estamos en navegador se oculta el botón salir
		}
		
		
		// LOGICA DEL BOTÓN LOGIN
		botonLogin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String nombre = campoUsuario.getText().trim();
				String password = campoPassword.getText().trim();
				
				if(nombre.isEmpty() || password.isEmpty()) {
					mensajeEstado.setText("Escribe usuario y contraseña.");
					return;
				}
				
				int id = repository.validarLogin(nombre, password);
				if(id > 0) {
					game.idUsuarioLogueado = id;
					game.nombreUsuarioLogueado = campoUsuario.getText();
					game.setScreen(new PantallaMenu(game));
				}else if(id == -1) {
					mensajeEstado.setText("Contraseña incorrecta.");
				}else {
					mensajeEstado.setText("El usuario no existe.");
				}
			}
		});
		
		// LOGICA DEL BOTÓN REGISTRO
		botonRegistro.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String nombre = campoUsuario.getText().trim();
				String password = campoPassword.getText().trim();
				
				if(nombre.isEmpty() || password.isEmpty()) {
					mensajeEstado.setText("Rellena todos los campos.");
					return;
				}
				
				int resultado = repository.registrarUsuario(nombre, password);
				if(resultado > 0) {
					game.idUsuarioLogueado = resultado;
					game.nombreUsuarioLogueado = nombre;
					game.setScreen(new PantallaMenu(game));
				}else if(resultado == -1) {
					mensajeEstado.setText("El usuario ya existe.");
				}else {
					mensajeEstado.setText("Error en la base de datos.");
				}
			}
		});
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0,0,0,1);
        stage.act(delta);
        stage.draw();
	}
	
	@Override public void resize(int width, int height) {
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
