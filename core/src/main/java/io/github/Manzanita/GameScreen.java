package io.github.Manzanita;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Manzana game;

    Texture fondoTexture;
    Texture cestaTexture;
    Texture manzanaNormalTexture;
    Texture manzanaDoradaTexture;
    Texture manzanaPodridaTexture;

    Sound manzanaSonido;
    Music musica;
    Sprite cestaSprite;
    Vector2 touchPos;

    Array<ManzanaCaida> manzanas;
    float manzanaTimer;
    Rectangle cestaRectangulo;
    Rectangle manzanaRectangulo;
    int manzanasRecolectadas;

    // dificultad
    float velocidadBase = 2f;
    float dificultadTimer = 0f;

    // vidas y estado de juego
    int vidas = 3;
    boolean gameOver = false;

    public GameScreen(final Manzana game) {
        this.game = game;

        fondoTexture = new Texture("fondo.png");
        cestaTexture = new Texture("cesta.png");
        manzanaNormalTexture = new Texture("manzana.png");
        // extra
        manzanaDoradaTexture = new Texture("manzana_dorada.png");
        manzanaPodridaTexture = new Texture("manzana_podrida.png");

        manzanaSonido = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        musica = Gdx.audio.newMusic(Gdx.files.internal("the_field_of_dreams.mp3"));
        musica.setLooping(true);
        musica.setVolume(0.5F);

        cestaSprite = new Sprite(cestaTexture);
        cestaSprite.setSize(1, 1);

        touchPos = new Vector2();

        cestaRectangulo = new Rectangle();
        manzanaRectangulo = new Rectangle();

        manzanas = new Array<>();

    }

    // clase interna para distintos tipos de manzana
    private static class ManzanaCaida {
        Sprite sprite;
        int puntos;
        float multiplicadorVelocidad;

        ManzanaCaida(Sprite sprite, int puntos, float multiplicadorVelocidad) {
            this.sprite = sprite;
            this.puntos = puntos;
            this.multiplicadorVelocidad = multiplicadorVelocidad;
        }
    }

    @Override
    public void show() {
        musica.play();
        reiniciarPartida();  //  comenzar en estado inicial
    }

    @Override
    public void render(float delta) {
        // en game over, pulsar la tecla R para reinicar
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                reiniciarPartida();
            }
            draw(); // dibuja pantalla de game over
            return;
        }

        input();
        logic();
        draw();
    }

    private void input() {
        float velocidadCesta = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cestaSprite.translateX(velocidadCesta * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cestaSprite.translateX(-velocidadCesta * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            cestaSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float cestaWidth = cestaSprite.getWidth();
        float cestaHeight = cestaSprite.getHeight();
        float delta = Gdx.graphics.getDeltaTime();

        cestaSprite.setX(MathUtils.clamp(cestaSprite.getX(), 0, worldWidth - cestaWidth));
        cestaRectangulo.set(cestaSprite.getX(), cestaSprite.getY(), cestaWidth, cestaHeight);

        // dificultad progresiva
        dificultadTimer += delta;
        if (dificultadTimer > 10f) {
            dificultadTimer = 0;
            velocidadBase += 0.5f;
        }

        // actualizar manzanas
        for (int i = manzanas.size - 1; i >= 0; i--) {
            ManzanaCaida manzana = manzanas.get(i);
            Sprite sprite = manzana.sprite;

            float manzanaWidth = sprite.getWidth();
            float manzanaHeight = sprite.getHeight();

            sprite.translateY(-velocidadBase * manzana.multiplicadorVelocidad * delta);
            manzanaRectangulo.set(sprite.getX(), sprite.getY(), manzanaWidth, manzanaHeight);

            // si se cae fuera de pantalla
            if (sprite.getY() < -manzanaHeight) {
                // SOLO restan vidas las manzanas NO podridas (puntos >= 0)
                if (manzana.puntos >= 0) {
                    vidas--;
                    if (vidas <= 0) {
                        manzanas.removeIndex(i);
                        gameOver = true;
                        break;
                    }
                }
                // en todos los casos se elimina la manzana
                manzanas.removeIndex(i);

            } else if (cestaRectangulo.overlaps(manzanaRectangulo)) {
                manzanasRecolectadas += manzana.puntos;
                manzanas.removeIndex(i);
                manzanaSonido.play();
            }
        }

        // si se ha activado gameOver en el bucle, no se generan más manzanas
        if (gameOver) return;

        // crear nuevas manzanas
        manzanaTimer += delta;
        if (manzanaTimer > 1f) {
            manzanaTimer = 0;
            crearManzana();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(fondoTexture, 0, 0, worldWidth, worldHeight);
        cestaSprite.draw(game.batch);

        // HUD: puntuación y vidas

        game.font.draw(game.batch, "Manzanas: " + manzanasRecolectadas, 0.5f, worldHeight - 0.5f);
        game.font.draw(game.batch, "Vidas: " + vidas, 0.5f, worldHeight - 1.0f);

        for (ManzanaCaida manzana : manzanas) {
            manzana.sprite.draw(game.batch);
        }

        // Mensaje de Game Over
        if (gameOver) {
            game.font.draw(game.batch, "GAME OVER", worldWidth / 2f - 2f, worldHeight / 2f + 1f);
            game.font.draw(game.batch, "Pulsa R para reiniciar", worldWidth / 2f - 2.5f, worldHeight / 2f+1.5f);
        }

        game.batch.end();
    }

    private void crearManzana() {
        float manzanaWidth = 1;
        float manzanaHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        int tipo = MathUtils.random(0, 2); // 0 normal, 1 dorada, 2 podrida

        Texture textura;
        int puntos;
        float multVel;

        switch (tipo) {
            case 1: // dorada
                textura = manzanaDoradaTexture != null ? manzanaDoradaTexture : manzanaNormalTexture;
                puntos = 3;
                multVel = 1.5f;
                break;
            case 2: // podrida
                textura = manzanaPodridaTexture != null ? manzanaPodridaTexture : manzanaNormalTexture;
                puntos = -2;
                multVel = 0.7f;
                break;
            default: // normal
                textura = manzanaNormalTexture;
                puntos = 1;
                multVel = 1f;
                break;
        }

        Sprite sprite = new Sprite(textura);
        sprite.setSize(manzanaWidth, manzanaHeight);
        sprite.setX(MathUtils.random(0F, worldWidth - manzanaWidth));
        sprite.setY(worldHeight);

        manzanas.add(new ManzanaCaida(sprite, puntos, multVel));
    }

    // reinicia lógica de partida sin recrear texturas/sonidos
    private void reiniciarPartida() {
        manzanas.clear();
        manzanasRecolectadas = 0;
        vidas = 3;
        gameOver = false;
        velocidadBase = 2f;
        dificultadTimer = 0f;
        manzanaTimer = 0f;

        // colocar cesta en el centro inferior
        float worldWidth = game.viewport.getWorldWidth();
        cestaSprite.setPosition(worldWidth / 2f - cestaSprite.getWidth() / 2f, 0f);
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        fondoTexture.dispose();
        manzanaSonido.dispose();
        musica.dispose();
        manzanaNormalTexture.dispose();
        cestaTexture.dispose();
        if (manzanaDoradaTexture != null) manzanaDoradaTexture.dispose();
        if (manzanaPodridaTexture != null) manzanaPodridaTexture.dispose();
    }
}
