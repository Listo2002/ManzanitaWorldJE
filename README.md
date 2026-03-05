# 🍎 ManzanitaWorld

Un juego arcade desarrollado con libGDX donde controlas una cesta para recoger manzanas que caen del cielo.

## 📋 Descripción

ManzanitaWorld es un juego arcade sencillo en el que el jugador controla una cesta que debe recoger diferentes tipos de manzanas que caen en un campo. El objetivo es conseguir la máxima puntuación posible mientras gestionas tus vidas y la dificultad creciente del juego.

### Características principales

- 🎮 Controles simples: teclado o ratón
- 🍏 Tres tipos de manzanas con diferentes texturas y velocidades
- 📈 Dificultad progresiva (cada 10 segundos aumenta la velocidad)
- ❤️ Sistema de vidas
- 🎵 Música y efectos de sonido
- 🔄 Sistema de reinicio rápido (pulsando R)

## 🎯 Mecánicas del juego

### Tipos de manzanas

| Tipo | Puntos | Velocidad | Efecto |
|------|--------|-----------|--------|
| 🍎 Normal | +1 | Base | Resta vida si se escapa |
| 🌟 Dorada | +3 | Rápida | Resta vida si se escapa |
| 🍏 Podrida | -2 | Variable | No resta vida si se escapa |

### Sistema de puntuación

- Comienza con **3 vidas**
- Las manzanas buenas (normal/dorada) restan 1 vida si caen fuera de la pantalla
- Las manzanas podridas penalizan la puntuación pero no afectan las vidas
- La velocidad de caída aumenta progresivamente cada 10 segundos
- **Game Over** al llegar a 0 vidas

## 🎮 Controles

- **Teclado**: Flechas izquierda/derecha para mover la cesta
- **Táctil/Mouse**: Clic o toque en la pantalla
- **R**: Reiniciar partida (durante Game Over)

## 🏗️ Arquitectura

### Estructura de clases

```
ManzanitaWorld/
├── Manzana (Game)
│   └── Clase principal que extiende Game
│       ├── Inicializa SpriteBatch y BitmapFont
│       ├── Configura FitViewport (8x5 unidades)
│       └── Gestiona el ciclo de vida de las pantallas
│
├── MainMenuScreen (Screen)
│   └── Pantalla de inicio
│       ├── Muestra título y mensaje de bienvenida
│       └── Transición a GameScreen al interactuar
│
└── GameScreen (Screen)
    └── Pantalla principal del juego
        ├── Gestión de texturas y sprites
        ├── Sistema de música y sonido
        ├── Lógica de movimiento (cesta y manzanas)
        ├── Sistema de colisiones
        ├── Control de puntuación y vidas
        ├── Dificultad progresiva
        └── Estado de Game Over y reinicio
```

### Pantallas (Screens)

1. **MainMenuScreen**: Menú principal con título y opción de comenzar
2. **GameScreen**: Pantalla de juego con HUD (puntuación y vidas) y mensaje de Game Over

## 🔧 Desarrollo

### Aspectos técnicos destacados

#### Movimiento independiente de FPS
Todo el movimiento utiliza `delta time` mediante `Gdx.graphics.getDeltaTime()`, garantizando velocidad consistente independientemente del hardware.

#### Sistema de generación de manzanas
- Generación periódica con temporizador
- Posiciones aleatorias en el eje X
- Velocidad base que aumenta con el tiempo
- Tipos de manzana con probabilidades diferentes

#### Detección de colisiones
Sistema de colisión entre la cesta y las manzanas que:
- Actualiza la puntuación según el tipo
- Reproduce efectos de sonido
- Elimina la manzana del juego

#### Sistema de vidas
- Pérdida de vida cuando manzanas buenas escapan
- Seguimiento visual en el HUD
- Activación de Game Over al llegar a 0

## 🎓 Aprendizajes

Durante el desarrollo de ManzanitaWorld se identificaron conceptos clave:

### Separación lógica-gráfica
- **Lógica**: Gestión de datos (posiciones, velocidades, tipos, estados)
- **Gráfica**: Representación visual de la lógica (sprites, textos, efectos)

### Buenas prácticas implementadas
- ✅ Uso de `delta time` para movimiento consistente
- ✅ Arquitectura basada en `Screen` para escalabilidad
- ✅ Separación de responsabilidades entre clases
- ✅ Código mantenible y extensible
- ✅ Facilita la adición de nuevas mecánicas

## 🚀 Instalación y ejecución

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/ManzanitaWorld.git

# Navegar al directorio
cd ManzanitaWorld

# Compilar y ejecutar (según tu configuración de Gradle)
./gradlew desktop:run
```

## 🛠️ Tecnologías utilizadas

- **libGDX**: Framework principal de desarrollo
- **Java**: Lenguaje de programación
- **Gradle**: Sistema de construcción



