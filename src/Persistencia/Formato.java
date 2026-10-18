package Persistencia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Utilidades para escribir y leer los archivos de texto de la persistencia.
 * 
 * Formato: una fila por línea, columnas separadas por ';'. Dentro de una
 * columna puede haber una lista de valores separados por ','. Los caracteres
 * especiales (\ ; , salto de línea) se escapan con '\'. Las fechas se guardan
 * como "yyyy-MM-dd HH:mm:ss".
 */
public final class Formato {

	public static final char SEPARADOR_COLUMNA = ';';
	public static final char SEPARADOR_LISTA = ',';

	private static final String FECHA_HORA = "yyyy-MM-dd HH:mm:ss";
	private static final String FECHA_HORA_CORTA = "yyyy-MM-dd HH:mm";
	private static final String SOLO_FECHA = "yyyy-MM-dd";

	private Formato() {
	}

	/** Escapa un texto para poder guardarlo dentro de una columna. */
	public static String esc(Object valor) {
		if (valor == null) {
			return "";
		}
		String s = String.valueOf(valor);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\\':
				sb.append("\\\\");
				break;
			case ';':
				sb.append("\\;");
				break;
			case ',':
				sb.append("\\,");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/** Deshace el escape hecho por {@link #esc(Object)}. */
	public static String desesc(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\' && i + 1 < s.length()) {
				char siguiente = s.charAt(++i);
				if (siguiente == 'n') {
					sb.append('\n');
				} else if (siguiente == 'r') {
					sb.append('\r');
				} else {
					sb.append(siguiente);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/** Parte un texto en los separadores que NO están escapados (conserva los escapes). */
	public static ArrayList<String> dividirCrudo(String texto, char separador) {
		ArrayList<String> partes = new ArrayList<String>();
		StringBuilder actual = new StringBuilder();
		for (int i = 0; i < texto.length(); i++) {
			char c = texto.charAt(i);
			if (c == '\\' && i + 1 < texto.length()) {
				actual.append(c).append(texto.charAt(++i));
			} else if (c == separador) {
				partes.add(actual.toString());
				actual.setLength(0);
			} else {
				actual.append(c);
			}
		}
		partes.add(actual.toString());
		return partes;
	}

	/** Une columnas ya escapadas en una línea. */
	public static String linea(String... columnas) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < columnas.length; i++) {
			if (i > 0) {
				sb.append(SEPARADOR_COLUMNA);
			}
			sb.append(columnas[i]);
		}
		return sb.toString();
	}

	/** Convierte una colección en el texto de una columna de lista. */
	public static String lista(Collection<?> elementos) {
		StringBuilder sb = new StringBuilder();
		boolean primero = true;
		for (Object e : elementos) {
			if (!primero) {
				sb.append(SEPARADOR_LISTA);
			}
			sb.append(esc(e));
			primero = false;
		}
		return sb.toString();
	}

	public static ArrayList<String> parseLista(String crudo) {
		ArrayList<String> resultado = new ArrayList<String>();
		if (crudo.isEmpty()) {
			return resultado;
		}
		for (String parte : dividirCrudo(crudo, SEPARADOR_LISTA)) {
			resultado.add(desesc(parte));
		}
		return resultado;
	}

	public static String fecha(Date fecha) {
		if (fecha == null) {
			return "";
		}
		return new SimpleDateFormat(FECHA_HORA).format(fecha);
	}

	/** Acepta "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm" o "yyyy-MM-dd". */
	public static Date parseFecha(String texto) throws ParseException {
		String t = texto.trim();
		String patron = SOLO_FECHA;
		if (t.length() > SOLO_FECHA.length()) {
			patron = (t.length() > FECHA_HORA_CORTA.length()) ? FECHA_HORA : FECHA_HORA_CORTA;
		}
		SimpleDateFormat formato = new SimpleDateFormat(patron);
		formato.setLenient(false);
		return formato.parse(t);
	}
}
