package io.github.Manzanita;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * Pantalla principal del menú inicial del juego Manzanita.
 * Muestra fondo, título, botones de navegación y puntuación máxima.
 */
public class MainMenuScreen implements Screen {

    /** Referencia al juego principal para cambiar pantallas */
    final Manzana game;

    /** Textura del fondo del menú principal */
    private final Texture fondoTexture;

    /** Layout reutilizable para medir dimensiones del texto */
    private final GlyphLayout layout = new GlyphLayout();

    /**
     * Constructor: inicializa la pantalla del menú principal
     * @param game instancia principal del juego
     */
    public MainMenuScreen(final Manzana game) {
        this.game = game;
        this.fondoTexture = new Texture("fondoinicio.png");
    }

    /**
     * Dibuja texto centrado horizontalmente en la posición Y especificada
     * @param text texto a dibujar
     * @param y coordenada Y en coordenadas del mundo
     */
    private void drawCentered(String text, float y) {
        float worldWidth = game.viewport.getWorldWidth();
        layout.setText(game.font, text);
        float x = (worldWidth - layout.width) / 2f;
        game.font.draw(game.batch, layout, x, y);
    }

    /**
     * Renderiza el menú principal cada frame
     */
    @Override
    public void render(float delta) {
        // Limpia pantalla con color blanco
        ScreenUtils.clear(Color.WHITE);

        // Configura viewport y matriz de proyección antes del batch
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        // Obtiene dimensiones del mundo viewport
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Inicia el batch de sprites
        game.batch.begin();

        // Dibuja fondo escalado al tamaño del viewport
        game.batch.draw(fondoTexture, 0, 0, worldWidth, worldHeight);

        // Configura color del texto
        game.font.setColor(Color.BLUE);

        // Dibuja elementos de UI centrados
        drawCentered("¡Bienvenido a manzanita! ", worldHeight - 0.5f);
        drawCentered("> JUGAR <", 2.9f);
        drawCentered("> CÓMO JUGAR <", 2.3f);
        drawCentered("> AJUSTES <", 1.7f);

        // Muestra puntuación máxima guardada
        int highScore = Gdx.app.getPreferences("ManzanaGame").getInteger("highScore", 0);
        drawCentered("Puntuación máxima: " + highScore, 1f);


        //importa el tiempo seleccionado
        Preferences prefs = Gdx.app.getPreferences("ManzanaGame");
        int tiempoJuego = prefs.getInteger("tiempoJuego", 60);
        drawCentered("Tiempo: " + (tiempoJuego / 60) + " min", 0.5f);


        // Finaliza el batch de sprites
        game.batch.end();

        // Detecta toque único de pantalla
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);

            // Detecta toque en botón JUGAR (Y: 2.7-3.2)
            if (touchPos.y > 2.7f && touchPos.y < 3.2f) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
            // Detecta toque en botón CÓMO JUGAR (Y: 2.1-2.6)
            else if (touchPos.y > 2.1f && touchPos.y <= 2.6f) {
                game.setScreen(new HowToPlayScreen(game));
                dispose();
            }
            // Detecta toque en botón AJUSTES (Y: 1.0-2.0)
            else if (touchPos.y > 1f && touchPos.y <= 2f) {
                game.setScreen(new SettingsScreen(game));
                dispose();
            }
        }
    }

    /**
     * Actualiza viewport cuando cambia el tamaño de la ventana/dispositivo
     */
    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    // Métodos vacíos requeridos por Screen (sin implementación específica)
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    /**
     * Libera recursos de la textura del fondo
     */
    @Override
    public void dispose() {
        fondoTexture.dispose();
    }
}
