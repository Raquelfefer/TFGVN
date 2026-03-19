package com.RFF.VN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaMenu implements Screen{
	private final Main game;
	private Stage stage;
	private Skin skin;
	private Repository repository;
	
	public PantallaMenu(Main game) {
		this.game = game;
		this.stage = new Stage(new ScreenViewport());
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.repository = new Repository();
		
		this.skin.add("default-font", game.getFuente(), BitmapFont.class);
		this.skin.get(Label.LabelStyle.class).font = game.getFuente();
		this.skin.get(TextField.TextFieldStyle.class).font = game.getFuente();
	    this.skin.get(TextButton.TextButtonStyle.class).font = game.getFuente();
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		// Consultamos el último punto de guardado 
		final int ultimoId = repository.obtenerUltimoCapitulo(game.idUsuarioLogueado);
		
		// Saludo personalizado
		Label saludo = new Label("Bienvenido, " + game.nombreUsuarioLogueado, skin);
		
		TextButton btnNuevaPartida = new TextButton("Nueva Partida", skin);
		TextButton btnContinuarPartida = new TextButton("Continuar Partida", skin);
		TextButton btnLogros = new TextButton("Ver Logros", skin);
		TextButton btnSalir = new TextButton("Salir", skin);
		
		// Si ultimoId devuelve 0 no hay partidas guardadas y el botón Continuar aparece deshabilitado
		if(ultimoId <= 0) {
			btnContinuarPartida.setDisabled(true);
			btnContinuarPartida.setColor(1,1,1,0.5f);
		}
		
		table.add(saludo).padBottom(30).row();
		table.add(btnNuevaPartida).width(250).pad(5).row();
		table.add(btnContinuarPartida).width(250).pad(5).row();
		table.add(btnLogros).width(250).pad(5).row();
		table.add(btnSalir).width(250).pad(5).row();
		
		btnNuevaPartida.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				repository.actualizarProgreso(game.idUsuarioLogueado,1);
				repository.borrarHistorialUsuario(game.idUsuarioLogueado);
				game.setScreen(new PantallaJuego (game, 1, true));
				}
			});
		
		btnContinuarPartida.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!btnContinuarPartida.isDisabled()) {
					game.setScreen(new PantallaJuego(game, ultimoId, true));
				}
			}
		});
		
		btnLogros.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
					game.setScreen(new PantallaLogros(game));
			}
		});
		
		btnSalir.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL) {
					game.setScreen(new PantallaLogin(game));
				}else {
					Gdx.app.exit();
				}
			}
		});
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.1f, 0.1f, 0.3f, 1);
		
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
