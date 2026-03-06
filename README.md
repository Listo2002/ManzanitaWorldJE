# 🍎 ManzanitaWorld

Un juego arcade desarrollado con **libGDX** donde controlas una cesta para recoger manzanas que caen del cielo.

---

## 👨‍💻 Autor

**Javier Escudero**

---

# 📋 Descripción

**ManzanitaWorld** es un juego arcade sencillo en el que el jugador controla una cesta que debe recoger diferentes tipos de manzanas que caen en un campo.

El objetivo es conseguir la **máxima puntuación posible** mientras gestionas la **dificultad creciente del juego** y el **tiempo disponible**, que puede configurarse en segundos desde la pantalla de **Ajustes**.

**Valor por defecto:** `60 segundos`

---

# ✨ Características principales

* 🎮 Controles simples: teclado, ratón o pantalla táctil
* 🍏 Variedad de objetos: manzanas normales, doradas, podridas, power-ups y trampas
* 📈 Dificultad por niveles: la velocidad y aparición de manzanas aumentan cada 10 manzanas recogidas
* ⏱️ Modo contrarreloj configurable
* 🏆 Sistema de récords (**High Score**) guardado entre partidas
* ⏸️ Pantalla de pausa integrada
* 🎵 Música y efectos de sonido activables/desactivables en Ajustes
* 🔄 Reinicio rápido tras Game Over
* 📘 Pantalla de ayuda (**HowToPlayScreen**)

---

# 🎯 Mecánicas del juego

## 🍎 Objetos y tipos de manzanas

| Tipo        | Textura                | Puntos | Velocidad     | Efecto                                   |
| ----------- | ---------------------- | ------ | ------------- | ---------------------------------------- |
| 🍎 Normal   | `manzana.png`          | +1     | Base          | Suma puntos y cuenta para subir de nivel |
| 🌟 Dorada   | `manzana_dorada.png`   | +3     | Rápida (x1.5) | Suma más puntos                          |
| 🍏 Podrida  | `manzana_podrida.png`  | -2     | Lenta (x0.7)  | Resta puntos                             |
| 🔵 Power-Up | `cesta.png` (azul)     | 0      | Media (x1.2)  | Cesta gigante o cámara lenta durante 5s  |
| 💣 Trampa   | `manzana_podrida` gris | 0      | Rápida (x1.3) | **Game Over inmediato**                  |

---

## ⏱️ Dinámica de la partida

* Cada partida dura el **tiempo configurado en Ajustes**.
* Si el jugador no modifica el tiempo:

```text
Tiempo por defecto = 60 segundos
```

### Durante la partida

* 🍎 Manzanas buenas → suman puntos
* 🍏 Manzanas podridas → restan puntos
* 💣 Trampas → terminan la partida

### Sistema de niveles

Cada **10 manzanas recogidas**:

* ↑ aumenta la velocidad de caída
* ↓ disminuye el tiempo entre apariciones de manzanas

---

## 🏆 Sistema de récords

Al finalizar la partida:

* Si la puntuación es mayor que el récord guardado
* Se actualiza automáticamente el **High Score**

Los datos se almacenan usando **Preferences de libGDX**.

---

## ☠️ Game Over

El juego termina cuando ocurre uno de los siguientes eventos:

1. ⏳ Se acaba el tiempo de la partida
2. 💣 La cesta toca una trampa

---

# 🎮 Controles

## ⌨️ Teclado

| Tecla | Acción                     |
| ----- | -------------------------- |
| ⬅️    | mover cesta a la izquierda |
| ➡️    | mover cesta a la derecha   |
| ESC   | pausar / reanudar          |

---

## 🖱️ Ratón o pantalla táctil

* Clic o arrastrar para mover la cesta horizontalmente.

---

## 📱 Pantalla Game Over

* Tocar la pantalla (tras ~1 segundo) para volver al menú.

---

## ⚙️ Ajustes (SettingsScreen)

| Acción                            | Resultado                 |
| --------------------------------- | ------------------------- |
| Tocar lado izquierdo del selector | −10 segundos              |
| Tocar lado derecho                | +10 segundos              |
| Tiempo mínimo                     | 30s                       |
| Tocar texto sonido                | activar/desactivar sonido |

---

# 🏗️ Arquitectura

## 📂 Estructura de clases

```text
ManzanitaWorld/
│
├── Manzana (Game)
│
├── MainMenuScreen (Screen)
│
├── GameScreen (Screen)
│
├── SettingsScreen (Screen)
│
└── HowToPlayScreen (Screen)
```

---

## 🧠 Responsabilidades

### Manzana (Game)

Clase principal que:

* inicializa `SpriteBatch`
* inicializa `BitmapFont`
* controla el ciclo de vida de pantallas

---

### MainMenuScreen

Pantalla principal que incluye:

* título del juego
* récord actual
* tiempo configurado
* acceso a:

    * jugar
    * ajustes
    * cómo jugar

---

### GameScreen

Contiene toda la lógica del juego:

* movimiento de la cesta
* caída de manzanas
* detección de colisiones
* control de puntuación
* sistema de niveles
* temporizador de partida

---

### SettingsScreen

Permite modificar:

* ⏱️ tiempo de partida
* 🔊 sonido activado / desactivado

---

### HowToPlayScreen

Pantalla informativa que explica:

* tipos de manzana
* mecánicas básicas
* controles

---

# 🔧 Desarrollo

## Movimiento independiente del FPS

Todo el movimiento usa:

```java
Gdx.graphics.getDeltaTime()
```

Esto asegura que el juego funcione igual en cualquier dispositivo.

---

## Generación de manzanas

* sistema basado en temporizador
* posiciones aleatorias en el eje X
* velocidad dependiente del nivel
* probabilidades diferentes según el tipo de manzana

---

## Detección de colisiones

Se utilizan objetos `Rectangle` para comprobar:

```text
cesta ↔ manzana
```

Según el tipo de manzana se ejecuta:

* suma de puntos
* resta de puntos
* power-up
* Game Over

---

## Sistema de tiempo configurable

El tiempo de partida se guarda en:

```
Preferences → tiempoJuego
```

Características:

* incremento de **±10 segundos**
* mínimo **30s**
* valor por defecto **60s**

---

## 🎓 Aprendizajes

Este proyecto aplica varias buenas prácticas de desarrollo:

Separación lógica–gráfica
Lógica: gestión de datos (posiciones, velocidades, tipos de manzana, estados, tiempo, nivel, récord).

Gráfica: representación visual de la lógica (sprites, textos, efectos visuales).

Buenas prácticas implementadas
✅ Uso de delta time para movimiento consistente.

✅ Arquitectura basada en Screen para escalabilidad y múltiples pantallas.

✅ Separación de responsabilidades entre clases.

✅ Uso de Preferences para guardar configuración (tiempo, sonido, high score).



---

# 🚀 Instalación y ejecución

Clonar el repositorio:

```bash
git clone https://github.com/tu-usuario/ManzanitaWorld.git
```

Entrar en el proyecto:

```bash
cd ManzanitaWorld
```

Ejecutar versión Desktop:

```bash
./gradlew lwjgl3:run
```

---

## 📱 Android

Puedes ejecutar la versión Android desde **Android Studio** o con:

```bash
./gradlew android:installDebug android:run
```

---

# 🛠️ Tecnologías utilizadas

* **Java**
* **libGDX**
* **Gradle**

---

# 📜 Licencia

Proyecto educativo desarrollado con fines de aprendizaje.
