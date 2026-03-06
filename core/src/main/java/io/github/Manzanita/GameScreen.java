package io.github.Manzanita;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Preferences;

/**
 * Pantalla principal del juego donde se desarrolla la mecánica de recolectar manzanas.
 * Incluye física, colisiones, sistema de niveles, power-ups y estados del juego.
 */
public class GameScreen implements Screen {
    /** Referencia al juego principal para navegación entre pantallas */
    final Manzana game;

    // Texturas del juego (cargadas en constructor)
    Texture fondoTexture;
    Texture cestaTexture;
    Texture manzanaNormalTexture;
    Texture manzanaDoradaTexture;
    Texture manzanaPodridaTexture;
    Texture manzanaAzulTexture;
    Texture trampaTexture;

    // Audio
    Sound manzanaSonido;
    Music musica;

    // Sprites y posiciones
    Sprite cestaSprite;
    Vector2 touchPos;

    /** Estados posibles del juego */
    enum State {
        RUNNING, PAUSED, GAME_OVER
    }

    /** Estado actual del juego */
    State state = State.RUNNING;
    /** Temporizador para mostrar pantalla GAME_OVER */
    float gameOverTimer = 0f;

    // Sistema de dificultad progresiva
    int nivel = 1;
    float velocidadBase = 2f;
    float dificultadTimer = 0f;
    float tiempoEntreManzanas = 1f;

    // Mecánica contrarreloj
    float tiempoRestante = 60f;

    // Sistema de power-ups
    boolean powerUpActivo = false;
    float powerUpTimer = 0f;

    // Efectos visuales temporales
    float scoreColorTimer = 0f;
    float popTimer = 0f;

    // Colección de manzanas en juego
    Array<ManzanaCaida> manzanas;
    float manzanaTimer;
    Rectangle cestaRectangulo;
    Rectangle manzanaRectangulo;
    int manzanasRecolectadas;

    // Configuración global
    boolean soundOn;

    /**
     * Constructor: inicializa todas las texturas, audio y objetos del juego
     */
    public GameScreen(final Manzana game) {
        this.game = game;

        // Carga texturas (con fallbacks para opcionales)
        fondoTexture = new Texture("fondo.png");
        cestaTexture = new Texture("cesta.png");
        manzanaNormalTexture = new Texture("manzana.png");
        manzanaDoradaTexture = new Texture("manzana_dorada.png");
        manzanaPodridaTexture = new Texture("manzana_podrida.png");
        manzanaAzulTexture = new Texture("cesta.png"); // Power-up reutiliza textura cesta
        trampaTexture = new Texture("manzana_podrida.png");

        // Carga audio
        manzanaSonido = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        musica = Gdx.audio.newMusic(Gdx.files.internal("the_field_of_dreams.mp3"));
        musica.setLooping(true);
        musica.setVolume(0.5f);

        // Inicializa sprite de cesta
        cestaSprite = new Sprite(cestaTexture);
        cestaSprite.setSize(1, 1);
        cestaSprite.setOriginCenter();

        // Objetos reutilizables
        touchPos = new Vector2();
        cestaRectangulo = new Rectangle();
        manzanaRectangulo = new Rectangle();
        manzanas = new Array<>();

        // Carga preferencias de configuración
        Preferences prefs = Gdx.app.getPreferences("ManzanaGame");
        soundOn = prefs.getBoolean("soundOn", true);
        tiempoRestante = prefs.getInteger("tiempoJuego", 60); // Carga tiempo guardado o 60s por defecto
    }

    /**
     * Clase interna para representar manzanas en caída con diferentes propiedades
     */
    private static class ManzanaCaida {
        Sprite sprite;
        int puntos;
        float multiplicadorVelocidad;
        int tipo; // 0=normal, 1=dorada, 2=podrida, 3=powerup, 4=trampa

        ManzanaCaida(Sprite sprite, int puntos, float multiplicadorVelocidad, int tipo) {
            this.sprite = sprite;
            this.puntos = puntos;
            this.multiplicadorVelocidad = multiplicadorVelocidad;
            this.tipo = tipo;
        }
    }

    /**
     * Inicia la música cuando la pantalla se muestra
     */
    @Override
    public void show() {
        if (soundOn) {
            musica.play();
        }
    }

    /**
     * Bucle principal de renderizado cada frame
     */
    @Override
    public void render(float delta) {
        // ESC pausa/reanuda el juego
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (state == State.RUNNING) {
                state = State.PAUSED;
            } else if (state == State.PAUSED) {
                state = State.RUNNING;
            }
        }

        // Lógica según estado actual
        if (state == State.RUNNING) {
            input();
            logic();
        } else if (state == State.GAME_OVER) {
            gameOverTimer -= delta;
            if (gameOverTimer <= 0 && Gdx.input.justTouched()) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }

        draw();
    }

    /**
     * Procesa entrada del usuario (teclado táctil)
     */
    private void input() {
        float velocidadCesta = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        // Movimiento por teclado
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cestaSprite.translateX(velocidadCesta * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cestaSprite.translateX(-velocidadCesta * delta);
        }

        // Movimiento por toque táctil
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            cestaSprite.setCenterX(touchPos.x);
        }
    }

    /**
     * Lógica del juego: física, colisiones, timers, generación de objetos
     */
    private void logic() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float cestaWidth = cestaSprite.getWidth();
        float cestaHeight = cestaSprite.getHeight();
        float delta = Gdx.graphics.getDeltaTime();

        // Actualiza contrarreloj
        tiempoRestante -= delta;
        if (tiempoRestante <= 0) {
            terminarJuego();
            return;
        }

        // Gestión de power-up (aumenta tamaño de cesta)
        if (powerUpActivo) {
            powerUpTimer -= delta;
            if (powerUpTimer <= 0) {
                powerUpActivo = false;
                cestaSprite.setSize(1, 1);
            } else {
                cestaSprite.setSize(2, 1);
            }
        }

        // Timers de efectos visuales
        if (scoreColorTimer > 0)
            scoreColorTimer -= delta;
        if (popTimer > 0) {
            popTimer -= delta;
            float scale = 1f + (popTimer / 0.2f) * 0.3f;
            cestaSprite.setScale(scale);
        } else {
            cestaSprite.setScale(1f);
        }

        // Limita posición de cesta a límites de pantalla
        cestaSprite.setX(MathUtils.clamp(cestaSprite.getX(), 0, worldWidth - cestaWidth));

        // Rectángulo de colisión de la cesta
        float factorAncho = 0.5f;
        float factorAlto  = 0.1f;
        float rectWidth   = cestaWidth * factorAncho;
        float rectHeight  = cestaHeight * factorAlto;

        // Centra el rectángulo respecto al sprite de la cesta
        float rectX = cestaSprite.getX() + (cestaWidth - rectWidth) / 2f;
        float rectY = cestaSprite.getY() + (cestaHeight - rectHeight) / 2f;

        cestaRectangulo.set(rectX, rectY, rectWidth, rectHeight);

        // Actualiza todas las manzanas en juego
        for (int i = manzanas.size - 1; i >= 0; i--) {
            ManzanaCaida manzana = manzanas.get(i);
            Sprite sprite = manzana.sprite;

            float manzanaWidth = sprite.getWidth();
            float manzanaHeight = sprite.getHeight();

            // Velocidad modificada por power-up y tipo de manzana
            float currentSpeed = velocidadBase;
            if (powerUpActivo)
                currentSpeed *= 0.5f;
            sprite.translateY(-currentSpeed * manzana.multiplicadorVelocidad * delta);

            // Rectángulo de la manzana (puedes hacerlo un pelín más pequeño si quieres colisiones menos estrictas)
            manzanaRectangulo.set(sprite.getX(), sprite.getY(), manzanaWidth, manzanaHeight);

            // Elimina manzana si sale de pantalla
            if (sprite.getY() < -manzanaHeight) {
                manzanas.removeIndex(i);
            }
            // Detecta colisión con cesta
            else if (cestaRectangulo.overlaps(manzanaRectangulo)) {
                procesarColision(manzana, i);
            }
        }

        // Genera nuevas manzanas según timer
        manzanaTimer += delta;
        if (manzanaTimer > tiempoEntreManzanas) {
            manzanaTimer = 0;
            crearManzana();
        }
    }

    /**
     * Procesa colisión entre manzana y cesta según tipo
     */
    private void procesarColision(ManzanaCaida manzana, int index) {
        if (manzana.tipo == 4) { // Trampa: termina juego
            terminarJuego();
            return;
        } else if (manzana.tipo == 3) { // Power-up: activa efecto
            powerUpActivo = true;
            powerUpTimer = 5f;
            if (soundOn)
                manzanaSonido.play();
        } else { // Manzanas normales/podridas/doradas
            manzanasRecolectadas += manzana.puntos;

            // Avance de nivel cada 10 manzanas
            if (manzanasRecolectadas > 0 && manzanasRecolectadas / 10 >= nivel) {
                nivel++;
                velocidadBase += 0.5f;
                tiempoEntreManzanas = Math.max(0.3f, tiempoEntreManzanas - 0.1f);
            }

            // Activa efectos visuales si suma puntos
            if (manzana.puntos > 0) {
                scoreColorTimer = 0.5f;
                popTimer = 0.2f;
            }
            if (soundOn)
                manzanaSonido.play();
        }

        // Elimina manzana recolectada
        manzanas.removeIndex(index);
    }

    /**
     * Finaliza partida y guarda puntuación máxima si corresponde
     */
    private void terminarJuego() {
        state = State.GAME_OVER;
        gameOverTimer = 1f;

        Preferences prefs = Gdx.app.getPreferences("ManzanaGame");
        int highScore = prefs.getInteger("highScore", 0);
        if (manzanasRecolectadas > highScore) {
            prefs.putInteger("highScore", manzanasRecolectadas);
            prefs.flush();
        }
    }

    /**
     * Renderiza todos los elementos gráficos del juego
     */
    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Fondo y cesta
        game.batch.draw(fondoTexture, 0, 0, worldWidth, worldHeight);
        cestaSprite.draw(game.batch);

        // UI de puntuación (color especial al sumar puntos)
        Color scoreColor = Color.WHITE;
        if (scoreColorTimer > 0) {
            scoreColor = Color.YELLOW;
        }
        game.font.setColor(scoreColor);
        game.font.draw(game.batch, "Manzanas: " + manzanasRecolectadas, 0.5f, worldHeight - 0.5f);

        // UI de nivel y tiempo restante
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Nivel: " + nivel, 0.5f, worldHeight - 1.0f);
        game.font.draw(game.batch, "Tiempo: " + (int) Math.max(0, Math.ceil(tiempoRestante)),
            worldWidth - 2.5f, worldHeight - 0.5f);

        // Todas las manzanas en juego
        for (ManzanaCaida manzana : manzanas) {
            manzana.sprite.draw(game.batch);
        }

        // Pantallas de estado especial
        if (state == State.PAUSED) {
            game.font.setColor(Color.RED);
            game.font.draw(game.batch, "PAUSADO", worldWidth / 2 - 1f, worldHeight / 2);
        } else if (state == State.GAME_OVER) {
            game.font.setColor(Color.RED);
            game.font.draw(game.batch, "GAME OVER", worldWidth / 2 - 1.5f, worldHeight / 2 + 1f);
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "Toca para continuar", worldWidth / 2 - 2f, worldHeight / 2);
        }

        game.batch.end();
    }

    /**
     * Genera nueva manzana con tipo aleatorio según probabilidades
     */
    private void crearManzana() {
        float manzanaWidth = 1;
        float manzanaHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Probabilidades: 50% normal, 20% dorada, 15% podrida, 7% power-up, 8% trampa
        int prob = MathUtils.random(1, 100);
        int tipo;
        if (prob <= 50)
            tipo = 0; // normal
        else if (prob <= 70)
            tipo = 1; // dorada
        else if (prob <= 85)
            tipo = 2; // podrida
        else if (prob <= 92)
            tipo = 3; // power-up
        else
            tipo = 4; // trampa

        Texture textura;
        int puntos;
        float multVel;

        switch (tipo) {
            case 1: // Dorada: +3 puntos, más rápida
                textura = manzanaDoradaTexture != null ? manzanaDoradaTexture : manzanaNormalTexture;
                puntos = 3;
                multVel = 1.5f;
                break;
            case 2: // Podrida: -2 puntos, más lenta
                textura = manzanaPodridaTexture != null ? manzanaPodridaTexture : manzanaNormalTexture;
                puntos = -2;
                multVel = 0.7f;
                break;
            case 3: // Power-up azul
                textura = manzanaAzulTexture;
                puntos = 0;
                multVel = 1.2f;
                break;
            case 4: // Trampa gris
                textura = trampaTexture;
                puntos = 0;
                multVel = 1.3f;
                break;
            default: // Normal
                textura = manzanaNormalTexture;
                puntos = 1;
                multVel = 1f;
                break;
        }

        Sprite sprite = new Sprite(textura);
        sprite.setSize(manzanaWidth, manzanaHeight);
        sprite.setX(MathUtils.random(0F, worldWidth - manzanaWidth));
        sprite.setY(worldHeight);

        // Colores especiales para power-up y trampa
        if (tipo == 3) {
            sprite.setColor(Color.BLUE);
        } else if (tipo == 4) {
            sprite.setColor(Color.DARK_GRAY);
        }

        manzanas.add(new ManzanaCaida(sprite, puntos, multVel, tipo));
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    /**
     * Libera todos los recursos cargados (texturas y audio)
     */
    @Override
    public void dispose() {
        fondoTexture.dispose();
        manzanaSonido.dispose();
        musica.dispose();
        manzanaNormalTexture.dispose();
        cestaTexture.dispose();
        if (manzanaDoradaTexture != null)
            manzanaDoradaTexture.dispose();
        if (manzanaPodridaTexture != null)
            manzanaPodridaTexture.dispose();
        if (manzanaAzulTexture != null)
            manzanaAzulTexture.dispose();
        if (trampaTexture != null)
            trampaTexture.dispose();
    }
}
