package io.github.Manzanita;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Manzana game;

    private final Texture fondoTexture;
    public MainMenuScreen(final Manzana game) {
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

        game.font.setColor(Color.BLUE);
        //draw text. Remember that x and y are in meters
        game.font.draw(game.batch, "¡Bienvenido a manzanita! ", 3, 2);
        game.font.draw(game.batch, "Pulsa para comenzar", 3, 1.5f);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
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
    }
}
