package com.pamplona.turnos.adaptadores.entrada.rest;

import com.pamplona.turnos.excepciones.DatosInvalidosException;

import java.util.LinkedHashMap;
import java.util.Map;

/** JSON minimo: lee objetos planos ({"clave":"valor","n":3}) y escapa textos al escribir. */
final class Json {

    private Json() { }

    static Map<String, String> leerObjeto(String texto) {
        Map<String, String> mapa = new LinkedHashMap<>();
        if (texto == null || texto.isBlank()) {
            throw new DatosInvalidosException("El cuerpo de la peticion esta vacio; se esperaba un objeto JSON.");
        }
        int[] pos = {0};
        saltar(texto, pos);
        esperar(texto, pos, '{');
        saltar(texto, pos);
        if (peek(texto, pos) == '}') {
            return mapa;
        }
        while (true) {
            saltar(texto, pos);
            String clave = leerTexto(texto, pos);
            saltar(texto, pos);
            esperar(texto, pos, ':');
            saltar(texto, pos);
            mapa.put(clave, leerValor(texto, pos));
            saltar(texto, pos);
            char c = peek(texto, pos);
            pos[0]++;
            if (c == '}') {
                return mapa;
            }
            if (c != ',') {
                throw malFormado();
            }
        }
    }

    static String texto(String valor) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }

    private static String leerValor(String t, int[] pos) {
        if (peek(t, pos) == '"') {
            return leerTexto(t, pos);
        }
        int inicio = pos[0];
        while (pos[0] < t.length() && t.charAt(pos[0]) != ',' && t.charAt(pos[0]) != '}') {
            pos[0]++;
        }
        String crudo = t.substring(inicio, pos[0]).trim();
        if (crudo.isEmpty()) {
            throw malFormado();
        }
        return crudo.equals("null") ? null : crudo;
    }

    private static String leerTexto(String t, int[] pos) {
        esperar(t, pos, '"');
        StringBuilder sb = new StringBuilder();
        while (pos[0] < t.length()) {
            char c = t.charAt(pos[0]++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (pos[0] >= t.length()) {
                    break;
                }
                char e = t.charAt(pos[0]++);
                switch (e) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'u' -> {
                        if (pos[0] + 4 > t.length()) {
                            throw malFormado();
                        }
                        try {
                            sb.append((char) Integer.parseInt(t.substring(pos[0], pos[0] + 4), 16));
                        } catch (NumberFormatException ex) {
                            throw malFormado();
                        }
                        pos[0] += 4;
                    }
                    default -> sb.append(e);
                }
            } else {
                sb.append(c);
            }
        }
        throw malFormado();
    }

    private static void saltar(String t, int[] pos) {
        while (pos[0] < t.length() && Character.isWhitespace(t.charAt(pos[0]))) {
            pos[0]++;
        }
    }

    private static char peek(String t, int[] pos) {
        if (pos[0] >= t.length()) {
            throw malFormado();
        }
        return t.charAt(pos[0]);
    }

    private static void esperar(String t, int[] pos, char esperado) {
        if (peek(t, pos) != esperado) {
            throw malFormado();
        }
        pos[0]++;
    }

    private static DatosInvalidosException malFormado() {
        return new DatosInvalidosException("El cuerpo de la peticion no es un JSON valido.");
    }
}
