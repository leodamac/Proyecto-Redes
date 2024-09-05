
# 📡 Simulación de Errores en la Transmisión de Datos en un Entorno Cliente-Servidor

Este proyecto es una **simulación completa del modelo TCP/IP**, implementada en **Java**, que permite analizar los efectos de diversos errores en la transmisión de datos, como la **pérdida de paquetes**, la **corrupción de datos** y el **envío fuera de orden**. A través de esta herramienta interactiva, los usuarios pueden observar en tiempo real cómo los errores impactan la integridad de los datos transmitidos y explorar los desafíos que enfrentan las redes de comunicación. 

## 🚀 Características Principales

- **Simulación del Modelo TCP/IP** junto con la **Capa Física**.
- **Interfaz Gráfica de Usuario (GUI)** interactiva, desarrollada con **JavaFX**, que permite cargar archivos, realizar simulaciones de transmisión y observar el impacto de los errores.
- **Inyección Controlada de Errores**: Simula pérdida de paquetes, corrupción de datos y reordenamiento de paquetes, con parámetros ajustables por el usuario.
- **Modificación de Librería Externa**: Se realizó una modificación a la librería [fxgraph](https://github.com/sirolf2009/fxgraph) para adaptarla a las necesidades específicas del proyecto.
  
## 🖼️ Diagrama del Modelo TCP/IP con Capa Física

En esta simulación, los datos pasan por diversas capas del modelo TCP/IP, cada una con una probabilidad de pérdida o corrupción. Al final del proceso, existe un cálculo probabilístico del porcentaje de acierto en la transmisión, teniendo en cuenta los errores acumulados en cada capa.

![TCP/IP Model with Physical Layer](https://github.com/leodamac/Proyecto-Redes/blob/main/Grupo7_RedesDeDatos/src/main/resources/tcpip.jpg)

## 🏗️ Arquitectura del Proyecto

El proyecto se organiza en las siguientes componentes clave:

1. **Interfaz Gráfica de Usuario (GUI)**: Utiliza **JavaFX** para proporcionar una visualización clara del entorno de red simulado. Permite al usuario cargar un archivo, simular la transmisión y visualizar los errores inyectados.
   
2. **Dispositivos Simulados**: Las computadoras y el router se simulan para emular el flujo de datos a través de la red. Cada dispositivo implementa las capas de enlace de datos, red y la capa física para la transmisión real.

3. **Capas del Modelo TCP/IP**:📤
    - **Capa de Aplicación**: Segmenta los datos y los prepara para la transmisión.
    - **Capa de Transporte**: Encapsula los segmentos con información de control como los checksums.
    - **Capa de Red**: Añade direcciones IP y checksums adicionales.
    - **Capa de Enlace de Datos**: Añade las direcciones MAC para asegurar la entrega de los paquetes.
    - **Capa Física**: Simula la transmisión real de bits entre los dispositivos.

4. **Gestión de Errores**: Se introducen errores aleatorios durante la transmisión. El usuario puede ajustar la probabilidad de errores como la pérdida y la corrupción de datos.

## 📊 Experimentos Realizados

Los experimentos realizados muestran cómo diferentes factores afectan la integridad de los datos:

- **Sin Errores**: Simulación de la transmisión en condiciones ideales.
- **Variación de la Probabilidad de Error**: Ajustes en las probabilidades de pérdida y corrupción para observar el impacto en la integridad.
- **Evaluación de Tamaños de Archivos**: Pruebas con archivos de distintos tamaños para evaluar la susceptibilidad a los errores.

## 🛠️ Modificación de Librería

Para cumplir con los requisitos del proyecto, se modificó la librería de terceros [fxgraph](https://github.com/sirolf2009/fxgraph). Estas modificaciones fueron necesarias para adaptar la topología de red utilizada en la simulación, asegurando una representación gráfica adecuada para la transmisión de datos. Las adaptaciones realizadas permiten que la librería funcione específicamente para los fines de esta simulación.

## 📦 Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/leodamac/Proyecto-Redes.git
   ```

2. Importa el proyecto en tu entorno de desarrollo preferido (**Eclipse**, **IntelliJ**, etc.).

3. Asegúrate de que **Java 8 o superior** y **JavaFX** están configurados correctamente en tu entorno.

4. Ejecuta la clase principal para iniciar la simulación.

## 💻 Requisitos del Sistema

- **Java 8 o superior**
- **JavaFX**

## 🔬 Resultados

Los experimentos mostraron una degradación progresiva de la integridad de los datos a medida que aumenta la probabilidad de errores, con una marcada afectación en mensajes más grandes. Estos resultados subrayan la importancia de los mecanismos de corrección de errores, que podrían incluirse en versiones futuras de la simulación.

Para más detalles sobre los resultados de los experimentos, consulta el [documento técnico](Informe-FINAL.pdf).📄

## ✨ Conclusiones

Esta simulación es una herramienta valiosa para la educación y la investigación, proporcionando una representación clara de los desafíos que enfrentan las redes de comunicación. Los usuarios pueden interactuar con la interfaz para ajustar los parámetros de error y observar cómo estos afectan la transmisión de datos en un entorno controlado. 

El proyecto a futuro se puede mejorar para que incluya la implementación de **protocolos de corrección de errores**, **mayor escalabilidad** del sistema para redes más grandes, añadiendo más hosts y routers o incluso ideas que teníamos pensados de simular un streaming de audio🔊🎶 aplicando el enfoque del protocolo **UDP** y compararlo con el **TCP**, pero por falta de tiempo, no se alcanzó a implementar.

## ✍️ Autores

- **Leonardo Macías** - [leodamac@espol.edu.ec](mailto:leodamac@espol.edu.ec)
- **Kevin Salazar** - [kejosala@espol.edu.ec](mailto:kejosala@espol.edu.ec)
- **Génesis López** - [gennalop@espol.edu.ec](mailto:gennalop@espol.edu.ec)
- **Jorge Herrera** - [joheniet@espol.edu.ec](mailto:joheniet@espol.edu.ec)


## 📚 Referencias

Para una explicación detallada de la simulación y su marco teórico, consulta el [informe completo](Informe-FINAL.pdf) disponible en este repositorio.

