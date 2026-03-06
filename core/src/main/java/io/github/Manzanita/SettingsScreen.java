package io.github.Manzanita;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Pantalla de ajustes con selector de tiempo (segundos) y toggle de sonido
 */
public class SettingsScreen implements Screen {

    final Manzana game;
    private final Texture fondoTexture;
    private boolean soundOn;
    private int tiempoSeleccionado = 60; // Tiempo en segundos (por defecto 1 min)
    private final GlyphLayout layout = new GlyphLayout();

    public SettingsScreen(final Manzana game) {
        this.game = game;
        fondoTexture = new Texture("fondoinicio.png");

        // Cargar ajustes actuales
        Preferences prefs = Gdx.app.getPreferences("ManzanaGame");
        soundOn = prefs.getBoolean("soundOn", true);
        tiempoSeleccionado = prefs.getInteger("tiempoJuego", 60);
    }

    /**
     * Dibuja texto perfectamente centrado horizontalmente
     */
    private void drawCentered(String text, float y) {
        float worldWidth = game.viewport.getWorldWidth();
        layout.setText(game.font, text);
        float x = (worldWidth - layout.width) / 2f;
        game.font.draw(game.batch, layout, x, y);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        // Configura viewport ANTES de batch
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Fondo
        game.batch.draw(fondoTexture, 0, 0, worldWidth, worldHeight);

        game.font.setColor(Color.BLUE);
        // Título centrado
        drawCentered("AJUSTES", worldHeight - 0.8f);

        // Selector de tiempo (zona grande para tocar)
        game.font.setColor(Color.BLUE);
        drawCentered("Tiempo: " + tiempoSeleccionado + "s", worldHeight - 2f);
        game.font.setColor(Color.GREEN);
        drawCentered("-10s               (toca aqui)               +10s", worldHeight - 2.5f);

        // Toggle sonido
        game.font.setColor(soundOn ? Color.GREEN : Color.RED);
        drawCentered("Sonido: " + (soundOn ? "SÍ" : "NO"), worldHeight - 3.5f);
        game.font.setColor(Color.WHITE);
        drawCentered("(toca para cambiar)", worldHeight - 4f);

        // Botón volver
        game.font.setColor(Color.BLUE);
        drawCentered("> VOLVER AL MENÚ <", 1.8f);

        game.batch.end();


        // Procesar input táctil
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            float touchY = touchPos.y;

            // Zona tiempo (+10s / -10s) - divide horizontalmente
            if (touchY > worldHeight - 2.8f && touchY < worldHeight - 1.8f) {
                float touchX = touchPos.x;
                if (touchX < worldWidth / 3) { // Izquierda: -10s
                    tiempoSeleccionado = Math.max(30, tiempoSeleccionado - 10);
                } else if (touchX > 2 * worldWidth / 3) { // Derecha: +10s
                    tiempoSeleccionado += 10;
                }
            }
            // Zona sonido
            else if (touchY > worldHeight - 4.3f && touchY < worldHeight - 3.3f) {
                soundOn = !soundOn;
                Preferences prefs = Gdx.app.getPreferences("ManzanaGame");
                prefs.putBoolean("soundOn", soundOn);
                prefs.flush();
            }
            // Botón volver
            else if (touchY > 1f && touchY < 2.5f) {
                // Guarda tiempo antes de salir
                Preferences prefs = Gdx.app.getPreferences("ManzanaGame");
                prefs.putInteger("tiempoJuego", tiempoSeleccionado);
                prefs.flush();
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        fondoTexture.dispose();
    }
}
