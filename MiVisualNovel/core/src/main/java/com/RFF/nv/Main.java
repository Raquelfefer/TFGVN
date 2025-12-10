package com.RFF.nv;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;

public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        
        // Carga el estilo visual profesional automáticamente.
        // ¡Sin esto, los botones darían error!
        VisUI.load(); 
        
        // Arrancamos la pantalla del juego
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Delega el dibujo a la pantalla activa
    }

    @Override
    public void dispose() {
        batch.dispose();
        VisUI.dispose(); // Limpia la memoria al cerrar
    }
}
