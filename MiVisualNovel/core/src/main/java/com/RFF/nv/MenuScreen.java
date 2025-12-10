package com.RFF.nv;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class MenuScreen implements Screen {

    final Main game;
    Stage stage;
    Texture fondoTexture; // Para la imagen de fondo

    public MenuScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Cargar Fondo (Si no tienes imagen, borra estas 3 lineas y el dispose de abajo)
        try {
            fondoTexture = new Texture(Gdx.files.internal("fondo_menu.png"));
            Image fondo = new Image(fondoTexture);
            fondo.setFillParent(true);
            stage.addActor(fondo);
        } catch (Exception e) {
            System.out.println("No se encontró fondo_menu.jpg, se usará color sólido.");
        }

        // 2. Tabla para el Menú
        VisTable tabla = new VisTable();
        tabla.setFillParent(true);
        stage.addActor(tabla);

       
        
        // 4. Botones
        VisTextButton btnJugar = new VisTextButton("COMENZAR AVENTURA");
        VisTextButton btnSalir = new VisTextButton("SALIR");

        // --- FUNCIONALIDAD DE LOS BOTONES ---

        // Al pulsar JUGAR -> Cambiamos a la GameScreen
        btnJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose(); // Borramos el menú de la memoria
            }
        });

        // Al pulsar SALIR -> Cierra la aplicación
        btnSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
                System.exit(0);
            }
        });

        // --- ORGANIZACIÓN VISUAL ---
        tabla.add(btnJugar).width(300).height(50).pad(10).row();
        tabla.add(btnSalir).width(300).height(50).pad(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        if (fondoTexture != null) fondoTexture.dispose();
    }
}