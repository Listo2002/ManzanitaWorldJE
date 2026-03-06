package io.github.Manzanita;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class HowToPlayScreen implements Screen {

    final Manzana game;
    private final Texture fondoTexture;

    public HowToPlayScreen(final Manzana game) {
        this.game = game;
        fondoTexture = new Texture("fondoinicio.png");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(fondoTexture, 0, 0, worldWidth, worldHeight);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        // Título
        game.font.setColor(Color.BLUE);
        game.font.draw(game.batch, "CÓMO JUGAR", 0.5f, worldHeight - 0.5f);

        game.font.setColor(Color.DARK_GRAY);

        game.font.draw(game.batch, "Objetivo: Recoge manzanas y suma la máxima", 0.5f, worldHeight - 2.3f);
        game.font.draw(game.batch, "puntuación antes de que el tiempo llegue a 0.", 0.5f, worldHeight - 2.5f);

        game.font.draw(game.batch, "Rojas: +1 pt   Doradas: +3 pts", 0.5f, worldHeight - 2.8f);
        game.font.draw(game.batch, "Podridas/Gris: -2 pts", 0.5f, worldHeight - 3.2f);

        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, "Azul (Power-up): Cesta más grande y cámara lenta", 0.5f, worldHeight - 3.7f);

        game.font.setColor(Color.RED);
        game.font.draw(game.batch, "Piedra Negra: GAME OVER inmediato", 0.5f, worldHeight - 4.2f);

        game.font.setColor(Color.BLUE);
        game.font.draw(game.batch, "[ Toca la pantalla para volver ]", worldWidth / 2 - 2f, 0.5f);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (fondoTexture != null)
            fondoTexture.dispose();
    }
}
