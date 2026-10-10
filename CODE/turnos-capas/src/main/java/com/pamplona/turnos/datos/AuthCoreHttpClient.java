package com.pamplona.turnos.datos;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cliente HTTP del authcore-service del profesor.
 * Contrato: POST {url}/api/auth/login  {"username","password"}  ->  200 {"token":"<JWT>"}  |  401 {"error":"..."}
 */
public class AuthCoreHttpClient implements AuthGateway {

    private static final Pattern TOKEN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public AuthCoreHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String login(String usuario, String clave) {
        String cuerpo = "{\"username\":\"" + escapar(usuario) + "\",\"password\":\"" + escapar(clave) + "\"}";
        HttpRequest peticion = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/login"))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build();
        HttpResponse<String> respuesta;
        try {
            respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new AuthGatewayException(AuthGatewayException.Tipo.SERVICIO_NO_DISPONIBLE, "Sin conexion con " + baseUrl);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthGatewayException(AuthGatewayException.Tipo.SERVICIO_NO_DISPONIBLE, "Peticion interrumpida");
        } catch (IllegalArgumentException e) {
            throw new AuthGatewayException(AuthGatewayException.Tipo.SERVICIO_NO_DISPONIBLE, "URL invalida: " + baseUrl);
        }

        int estado = respuesta.statusCode();
        if (estado == 200) {
            Matcher m = TOKEN.matcher(respuesta.body());
            if (m.find()) {
                return m.group(1);
            }
            throw new AuthGatewayException(AuthGatewayException.Tipo.RESPUESTA_INVALIDA, "La respuesta no trae token");
        }
        if (estado == 400 || estado == 401 || estado == 403) {
            throw new AuthGatewayException(AuthGatewayException.Tipo.CREDENCIALES_INVALIDAS, "Credenciales rechazadas");
        }
        throw new AuthGatewayException(AuthGatewayException.Tipo.SERVICIO_NO_DISPONIBLE, "Estado HTTP " + estado);
    }

    private String escapar(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
