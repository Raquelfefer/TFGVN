package com.RFF.VN;


import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaJuego implements Screen{
	private final Main game;
	private Stage stage;
	private Skin skin;
	private Label etiquetaTexto;
	private Repository repository;
	private Container<Table> contenedorPrincipal;
	
	private int idNarracionActual;
	private Integer idSiguienteNarracion; //Usamos Integer para que pueda ser nulo
	private int capituloActual = -1;
	private boolean mostrandoOpciones = false;
	
	private Image imgFondo;
	private Image imgIzq, imgDer;
	private Music musicaActual;
	private String nombreMusicaActual = "";
	
	public PantallaJuego(Main game, int idRecibido, boolean esCapitulo) {
		this.game = game;
		this.repository = new Repository();
		this.stage = new Stage(new ScreenViewport());
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		this.skin.add("default-font", game.getFuente(), BitmapFont.class);
		this.skin.get(Label.LabelStyle.class).font = game.getFuente();
		this.skin.get(TextField.TextFieldStyle.class).font = game.getFuente();
		this.skin.get(TextButton.TextButtonStyle.class).font = game.getFuente();
		
		if(esCapitulo) {
			int idInicial = repository.obtenerIdInicialPorCapitulo(idRecibido);
			
			if(idInicial != -1) {
				this.idNarracionActual = idInicial;
			}else {
				this.idNarracionActual = 1;
			}
		}else {
			this.idNarracionActual = idRecibido;
		}
	}
	
	@Override
	public void show() {
		//Gdx.input.setCursorCatched(false);
		//Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
		
		Gdx.input.setInputProcessor(stage);
		
		//Fondo
		imgFondo = new Image();
		imgFondo.setFillParent(true);
		stage.addActor(imgFondo);
		
		//Personajes
		Table tablaPersonajes = new Table();
		tablaPersonajes.setFillParent(true);
		stage.addActor(tablaPersonajes);
		
		imgIzq = new Image();
		imgDer = new Image();
		
		tablaPersonajes.add(imgIzq).expand().bottom().left().padLeft(50).padBottom(150);
		tablaPersonajes.add(imgDer).expand().bottom().right().padRight(50).padBottom(150);
		
		//Botón superior derecha
		
		Table tablaSuperior = new Table();
	    tablaSuperior.top().right().setFillParent(true);
	    stage.addActor(tablaSuperior);
		
		TextButton btnPausa = new TextButton("MENU", skin);
	    btnPausa.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	            mostrarDialogoSalida();
	        }
	    });
	    
	    tablaSuperior.add(btnPausa).pad(10);
	    
	    //Texto principal
	    Table tablePrincipal = new Table();
	    tablePrincipal.bottom().padBottom(50).setFillParent(true);
		stage.addActor(tablePrincipal);
	
		//Caja de texto para la narración
		etiquetaTexto = new Label("Cargando historia...", skin);
		etiquetaTexto.setWrap(true); //Ajuste de línea automatico
		
		//Tabla interna para el texto u opciones
		Table tablaContenido = new Table();
		tablaContenido.add(etiquetaTexto).width(860).left();
		
		//Fondo
		contenedorPrincipal = new Container<>(tablaContenido);
		contenedorPrincipal.background(skin.getDrawable("textfield"));
		contenedorPrincipal.pad(20);
		contenedorPrincipal.fillX(); 
		contenedorPrincipal.getColor().a = 0.8f;

		tablePrincipal.add(contenedorPrincipal).width(900).bottom();
		
		cargarEscena(idNarracionActual);
	}
	
	private void cargarEscena(int id) {
		//Falta poner la ultima narracion que da pie a los diferentes finales
		if(id == 5) {
			comprobarFinal();
			return;
		}
		
		NarracionDTO datos = repository.obtenerNarracion(id);
		
		if(datos != null) {
			//Fondo
			if(datos.fondo != null) {
				Texture textFondo = new Texture(Gdx.files.internal("fondos/" + datos.fondo));
				imgFondo.setDrawable(new TextureRegionDrawable(new TextureRegion(textFondo)));
			}
			
			//Personajes y animacion
			actualizarPersonaje(datos.personajeIzq, true);
			actualizarPersonaje(datos.personajeDer, false);
			
			//Musica
			gestionarMusica(datos.musica);
			
			//Efecto de sonido
			if (datos.sonidoEfecto != null) {
				Sound efecto = Gdx.audio.newSound(Gdx.files.internal("sonidos/" + datos.sonidoEfecto));
				efecto.play();
			}
			
			mostrandoOpciones = false;
			
			Table tablaContenido = new Table();
			tablaContenido.add(etiquetaTexto).width(860).left();
			contenedorPrincipal.setActor(tablaContenido);
			contenedorPrincipal.getColor().a = 0.8f;
			
			etiquetaTexto.setText(datos.descripcion);
			idSiguienteNarracion = datos.idSiguiente;
			
			if(datos.idCapitulo != capituloActual) {
				capituloActual = datos.idCapitulo;
				repository.actualizarProgreso(game.idUsuarioLogueado, capituloActual);
			}
		}
	}
	
	private void mostrarOpciones() {
		List<OpcionDTO> opciones = repository.obtenerOpciones(idNarracionActual);
		if (opciones.isEmpty()) return;
		
		mostrandoOpciones = true;
		Table tablaOpciones = new Table();
		
		for(int i = 0; i < opciones.size(); i++) {
			final OpcionDTO opcion = opciones.get(i);
			final Label labelOpcion = new Label((i + 1) + ". " + opcion.texto, skin);
			
			
			//Listeners para el raton
			labelOpcion.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					seleccionarOpcion(opcion);
				}
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					labelOpcion.setColor(Color.YELLOW);
				}
				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					labelOpcion.setColor(Color.WHITE);
				}
			});
			
			tablaOpciones.add(labelOpcion).width(860).left().padBottom(10).row();
		}
		
		contenedorPrincipal.setActor(tablaOpciones);
	}
	
	private void seleccionarOpcion(OpcionDTO seleccion) {
		repository.guardarDecision(game.idUsuarioLogueado, seleccion.idOpcion);
		if(seleccion.idLogro != null) {
			repository.registrarLogro(game.idUsuarioLogueado, seleccion.idLogro);
		}
		
		idNarracionActual = seleccion.idDestino;
		cargarEscena(idNarracionActual);
	}
	
	private void mostrarDialogoSalida() {
	    Dialog dialogo = new Dialog("PAUSA", skin) {
	        @Override
	        protected void result(Object object) {
	            int opcion = (Integer) object;
	            switch (opcion) {
	                case 1: 
	                    game.setScreen(new PantallaMenu(game));
	                    break;
	                case 2: 
	                    game.setScreen(new PantallaLogin(game));
	                    break;
	                case 3: 
	                    Gdx.app.exit();
	                    break;
	            }
	        }
	    };

	    dialogo.text("¿Qué deseas hacer?");
	    dialogo.button("Volver al Menú", 1);
	    dialogo.button("Cerrar Sesión", 2);
	    dialogo.button("Salir del Juego", 3);
	    dialogo.button("Cancelar", 4); 
	    
	    dialogo.show(stage);
	}
	
	private void comprobarFinal() {
		boolean finalBueno = repository.haElegidoOpcion(game.idUsuarioLogueado, 1);
		
		if(finalBueno) {
			cargarEscena(6);
		}else {
			cargarEscena(7);
		}
	}
	
	private void actualizarPersonaje(String ruta, boolean esIzquierda) {
		Image img = esIzquierda ? imgIzq : imgDer;
		img.setDrawable(null);
		img.clearActions();
		
		if(ruta != null) {
			Texture tex = new Texture(Gdx.files.internal("personajes/"+ ruta));
			img.setDrawable(new TextureRegionDrawable(new TextureRegion(tex)));
			
			//Efecto de movimiento
			img.addAction(Actions.forever(Actions.sequence(
				Actions.moveBy(0,  15, 1.2f, Interpolation.sine),
				Actions.moveBy(0,-15,1.2f, Interpolation.sine)
			)));
		}
	}
	
	private void gestionarMusica(String musica) {
	    if (musica == null || musica.isEmpty()) return;
	    
	    if (musica.equals(nombreMusicaActual)) {
	        return; 
	    }

	    if (musicaActual != null) {
	        musicaActual.stop();
	        musicaActual.dispose(); 
	    }

        musicaActual = Gdx.audio.newMusic(Gdx.files.internal("musica/" + musica));
        musicaActual.setLooping(true);
        musicaActual.setVolume(0.5f); 
        musicaActual.play();
      
        nombreMusicaActual = musica;
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.1f, 0.1f, 0.3f, 1);
		
		boolean hayDialogo = false;
	    for (Actor actor : stage.getActors()) {
	        if (actor instanceof Dialog) hayDialogo = true;
	    }
	    
	    if(!hayDialogo) {
	    	if(mostrandoOpciones) {
				if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) manejarTeclado(0);
				if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) manejarTeclado(1);
				if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) manejarTeclado(2);
			}else if(Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
				if(idSiguienteNarracion != null) {
					idNarracionActual = idSiguienteNarracion;
					cargarEscena(idNarracionActual);
				}else {
					mostrarOpciones();
				}
			}
	    }
		
		stage.act(delta);
		stage.draw();
	}
	
	private void manejarTeclado(int indice) {
		List<OpcionDTO> lista = repository.obtenerOpciones(idNarracionActual);
		if(indice < lista.size()) {
			seleccionarOpcion(lista.get(indice));
		}
	}
	
	@Override
	public void resize(int width, int height) {
		game.getViewport().update(width, height, true);
	}
	
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {
		if (musicaActual != null) {
	        musicaActual.stop();
	        musicaActual.dispose();
	    }
	}
	@Override public void dispose() {
    	stage.dispose();
    	if (musicaActual != null) {
    		musicaActual.dispose();
    	}
    	skin.dispose();
    }

}
