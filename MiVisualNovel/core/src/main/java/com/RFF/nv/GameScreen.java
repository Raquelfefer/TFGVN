package com.RFF.nv;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class GameScreen implements Screen {

    final Main game;
    Stage stage; // El escenario donde actúan los elementos
    VisTable tablaPrincipal; // La tabla que organiza todo

    public GameScreen(final Main game) {
        this.game = game;

        // 1. Preparamos el escenario
        stage = new Stage(new ScreenViewport());
        // ¡IMPORTANTE! Esto permite que el ratón funcione en los botones
        Gdx.input.setInputProcessor(stage); 

        // 2. Creamos la tabla principal que ocupará toda la pantalla
        tablaPrincipal = new VisTable();
        tablaPrincipal.setFillParent(true); // Ocupar todo el espacio
        //tablaPrincipal.setDebug(true); // <--- DESCOMENTA ESTO SI QUIERES VER LAS LÍNEAS DE LA TABLA (Maldito CSS, pero visual)
        stage.addActor(tablaPrincipal);

        // --- AÑADIENDO CONTENIDO ---

        // Texto de Narración (Usamos VisLabel que ya viene bonito)
        VisLabel textoHistoria = new VisLabel("Laurie despierta en un lugar extraño...\n¿Qué debería hacer?");
        
        // Botón de Opción 1
        VisTextButton boton1 = new VisTextButton("Explorar el bosque");
        boton1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("¡Has elegido explorar!");
                // Aquí cambiaremos la historia más adelante
            }
        });

        // Botón de Opción 2
        VisTextButton boton2 = new VisTextButton("Gritar pidiendo ayuda");
        
        // --- ORGANIZANDO EN LA TABLA ---
        
        // Fila 1: El texto (centrado)
        tablaPrincipal.add(textoHistoria).pad(20).row(); 
        
        // Fila 2: Botón 1
        tablaPrincipal.add(boton1).pad(10).width(200).row();
        
        // Fila 3: Botón 2
        tablaPrincipal.add(boton2).pad(10).width(200).row();
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla (Fondo azul oscuro para probar)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar escenario
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
    }
}
