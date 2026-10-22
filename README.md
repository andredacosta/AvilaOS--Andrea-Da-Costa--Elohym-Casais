# ÁvilaOS — Simulador de Sistema Operativo Distribuido

**ÁvilaOS** es un simulador de un sistema operativo distribuido orientado a objetos, desarrollado en **Java (v21+)** para coordinar un clúster de múltiples computadores mediante un **reloj global unificado**. 

El proyecto fue diseñado como parte de la asignatura *Sistemas Operativos (Trimestre 2627-1)* en la **Universidad Metropolitana**.

---

##  Descripción General

El objetivo del simulador es modelar los conceptos fundamentales de **procesos, planificación, gestión de memoria y sincronización de recursos** mediante el algoritmo del **Productor-Consumidor con semáforos acotados**.

### Características Principales
* **Arquitectura Distribuida Multi-Nodo:** Cada computador del clúster posee sus propios componentes (CPU, memoria RAM, núcleo del SO, colas y planificador independiente).
* **Sincronización mediante Semáforos:** Implementación de semáforos orientados al sistema operativo (`mutex`, `lleno`, `vacio`) para proteger los buffers acotados en memoria.
* **Comunicación Transparente y Latencia de Red:** Soporte para accesos locales y remotos a buffers con penalización por latencia de red configurable en ciclos.
* **Políticas de Planificación Dinámicas:** Intercambio en tiempo de ejecución de algoritmos como FCFS, Round Robin, EDF (Earliest Deadline First) y Prioridades Apropiativas.
* **Persistencia de Datos:** Carga y guardado de configuraciones iniciales del clúster a través de archivos **JSON / CSV**.
* **Estructuras Primitivas:** Desarrollo de estructuras de datos customizadas desde cero (sin el uso de `java.util` Collection Framework).

---

## Documentación del Proyecto

El informe detallado, diagramas UML de arquitectura, estado de clases y análisis de métricas se encuentran disponibles en el siguiente documento:

https://docs.google.com/document/d/1rhcq-VXYpPzAyjxVOTDsRsyq_9jvBxi9nlqJjp4SON4/edit?usp=sharing

---

## Equipo de Desarrollo

* **Persona 1:** [Andrea Da Costa]
* **Persona 2:** [Elohym Casais]
