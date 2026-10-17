package com.pamplona.turnos.config;

/**
 * Configuracion externa. Cada valor se puede cambiar sin tocar codigo:
 * propiedad de Java (-Dnombre=valor) > variable de entorno > valor por defecto.
 */
public final class Configuracion {

    private static final String URL_AUTHCORE_POR_DEFECTO = "http://100.27.6.5:8080";
    /** Mismo valor por defecto que usa el authcore del profesor para firmar. */
    private static final String SECRETO_POR_DEFECTO = "cambia-este-secreto-por-uno-largo-y-aleatorio-de-al-menos-32-bytes";

    private Configuracion() { }

    /** -Dauthcore.url=...  o  AUTHCORE_URL */
    public static String urlAuthcore() {
        String url = leer("authcore.url", "AUTHCORE_URL", URL_AUTHCORE_POR_DEFECTO);
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** -Djwt.secret=...  o  JWT_SECRET. Debe ser EXACTAMENTE el mismo que usa el authcore para firmar. */
    public static String secretoJwt() {
        return leer("jwt.secret", "JWT_SECRET", SECRETO_POR_DEFECTO);
    }

    /** -Dapi.puerto=...  o  API_PUERTO */
    public static int puertoApi() {
        return Integer.parseInt(leer("api.puerto", "API_PUERTO", "8082"));
    }

    private static String leer(String propiedad, String variable, String porDefecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(variable);
        }
        return (valor == null || valor.isBlank()) ? porDefecto : valor.trim();
    }
}
