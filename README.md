# 🚀 NextChat - Plugin de Gestión de Chat para PaperMC

![Java Version](https://img.shields.io/badge/Java-17-blue.svg)
![API Version](https://img.shields.io/badge/API-Paper%201.20.4-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg) NextChat es un plugin para servidores PaperMC diseñado para ofrecer un control y personalización avanzados sobre el chat de los jugadores y la ejecución de comandos.

**Desarrollado por: @gaeldev**

## ✨ Características Principales

* **Formato de Chat Personalizable:**
    * Define un formato de chat global a través de `config.yml`.
    * Soporte completo para **PlaceholderAPI**.
    * Permite el uso de códigos de color legacy (`&c`), hexadecimales (`&xRRGGBB`) y extendidos (`&#RRGGBB`) tanto en el formato como en los mensajes de los jugadores.
* **Permiso para Colores en Mensajes:**
    * Los jugadores pueden usar códigos de color en sus mensajes si tienen el permiso `nextchat.color`.
    * Si no tienen el permiso, los códigos `&` se muestran como texto literal.
* **Cooldown de Chat:**
    * Configura un tiempo de espera entre mensajes para evitar spam.
    * Permiso `nextchat.bypass.cooldown` para omitir esta restricción.
    * Mensaje de cooldown personalizable.
* **Mensajes de Entrada/Salida Personalizables:**
    * Habilita y formatea mensajes únicos cuando los jugadores entran o salen del servidor.
    * Soporte para PlaceholderAPI y códigos de color.
* **Sistema Avanzado de Bloqueo de Comandos:**
    * Gestionado a través de `commands.yml`.
    * Define "listas blancas" de comandos por grupos de permisos.
    * Soporte para herencia de comandos entre grupos y prioridades.
    * Mensajes de denegación personalizables por grupo.
    * Filtrado tanto de la ejecución de comandos como del autocompletado (tab).
    * Permisos de bypass (`nextchat.tabcomplete.bypass`) y para comandos individuales (`nextchat.tabcomplete.<comando>`).
* **Comando de Administración:**
    * `/nextchat reload` para recargar las configuraciones sin reiniciar el servidor.
* **Prefijo de Plugin Configurable.**
* **Log de Inicio Detallado en Consola.**

## 兼容性 (Compatibilidad)

* **Servidor:** PaperMC
* **Versión de Minecraft:**
    * **Principalmente desarrollado y probado para:** `1.20.4` (compilado con la API de Paper 1.20.4 y Java 17).
    * **Probablemente compatible con:** PaperMC `1.20` a `1.20.3` (que también usan Java 17).
    * **Potencialmente compatible (con advertencias):** PaperMC `1.20.5+` (que requieren Java 21 para el servidor). El plugin, compilado con Java 17, podría funcionar si no hay cambios incompatibles en la API.
* **Java:** Compilado con Java 17.

## 🔗 Dependencias

* **PlaceholderAPI:** (Opcional, dependencia suave). El plugin funcionará sin él, pero los placeholders de PlaceholderAPI no estarán disponibles.

## ⚙️ Instalación

1.  Descarga la última versión de `NextChat.jar` desde la [sección de Releases](https://github.com/TU_USUARIO/TU_REPOSITORIO/releases) (reemplaza con tu enlace).
2.  Coloca el archivo `NextChat.jar` en la carpeta `plugins` de tu servidor PaperMC.
3.  Reinicia o carga el plugin en tu servidor.
4.  Configura los archivos `config.yml` y `commands.yml` según tus necesidades. Estos se generarán en la carpeta `plugins/NextChat/` la primera vez que se ejecute el plugin.

