package com.pamplona.turnos.datos;

/** Configuracion externa. La URL del authcore se cambia sin tocar codigo (local hoy, AWS despues). */
public final class Configuracion {

    private static final String URL_POR_DEFECTO = "http://100.27.6.5:8081";

    private Configuracion() { }

    /** Orden: -Dauthcore.url=...  >  variable de entorno AUTHCORE_URL  >  http://localhost:8081 */
    public static String urlAuthcore() {
        String url = System.getProperty("authcore.url");
        if (url == null || url.isBlank()) {
            url = System.getenv("AUTHCORE_URL");
        }
        if (url == null || url.isBlank()) {
            url = URL_POR_DEFECTO;
        }
        url = url.trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
