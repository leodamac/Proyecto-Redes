# Simulación de Errores en la Transmisión de Datos en un Entorno Cliente-Servidor

Este proyecto consiste en una simulación detallada del modelo TCP/IP utilizando Java, con el propósito de analizar el impacto de errores comunes en la transmisión de datos a través de redes. La simulación aborda aspectos críticos de la comunicación en red, permitiendo una evaluación de cómo la integridad de los datos se ve afectada en presencia de errores durante el proceso de transmisión.

## Descripción General

La simulación emula el proceso de envío de un archivo de texto desde un cliente hacia un servidor, pasando por un router intermedio, y siguiendo estrictamente las capas del modelo TCP/IP, incluida la capa física. Durante la transmisión, se introducen de manera aleatoria errores como pérdida de paquetes, entrega desordenada y corrupción de bits. Estos errores permiten observar y evaluar sus efectos sobre la integridad de los datos y la fiabilidad de la red.

### Características Principales

- **Implementación Completa del Modelo TCP/IP**: La simulación abarca las capas de Aplicación, Transporte, Internet, Enlace de Datos y Física.
- **Interfaz Gráfica Intuitiva**: Desarrollada con JavaFX, la GUI proporciona una representación visual clara del entorno de red, permitiendo la carga, transmisión y recepción de archivos de manera interactiva.
- **Simulación de Errores**: El sistema incorpora la posibilidad de simular errores críticos en la transmisión de datos, como la pérdida de paquetes y la corrupción de datos, con parámetros ajustables.
- **Visualización en Tiempo Real**: Los usuarios pueden monitorizar el número de paquetes enviados y recibidos, así como los errores detectados, todo en tiempo real.

## Estructura del Proyecto

El proyecto se organiza en varias secciones que abordan los distintos componentes y funcionalidades:

1. **Interfaz Gráfica de Usuario (GUI)**: Utiliza JavaFX para ofrecer una visualización intuitiva del proceso de transmisión de datos en la red simulada.
2. **Simulación de Dispositivos de Red**: Los dispositivos como PCs y routers son simulados para recrear un entorno de red realista, implementando las funciones clave de las capas de red.
3. **Gestión de Errores y Control de Transmisión**: Se introducen errores en la transmisión de datos mediante parámetros de probabilidad, permitiendo un análisis detallado de su impacto.

## Requisitos
- **Java 8 o superior**: Necesario para la ejecución del proyecto.
- **JavaFX**: Utilizado para el desarrollo de la interfaz gráfica.

## Instrucciones de Instalación

1. Clona este repositorio en tu entorno local:
   ```bash
   git clone https://github.com/leodamac/Proyecto-Redes.git
   ```
2. Importa el proyecto en tu entorno de desarrollo integrado (IDE) preferido, como Eclipse o IntelliJ IDEA.
3. Asegúrate de que Java y JavaFX están configurados correctamente en tu entorno.
4. Ejecuta el proyecto desde la clase principal para iniciar la simulación.

## Uso de la Simulación

1. Al iniciar la aplicación, se desplegará una interfaz gráfica que permite la carga de archivos de texto desde una computadora simulada.
2. Configura los parámetros de simulación, incluyendo las probabilidades de errores en la transmisión.
3. Inicia la simulación para observar cómo los datos se transmiten a través de la red y cómo los errores afectan la integridad de la información.

## Autores

Este proyecto ha sido desarrollado por:
- **Leonardo Macías** - [leodamac@espol.edu.ec](mailto:leodamac@espol.edu.ec)
- **Kevin Salazar** - [kejosala@espol.edu.ec](mailto:kejosala@espol.edu.ec)
- **Génesis López** - [gennalop@espol.edu.ec](mailto:gennalop@espol.edu.ec)
- **Jorge Herrera** - [joheniet@espol.edu.ec](mailto:joheniet@espol.edu.ec)

## Referencias

Para obtener información adicional sobre el modelo TCP/IP y las técnicas de simulación utilizadas, consulta las referencias incluidas en la documentación del proyecto.
