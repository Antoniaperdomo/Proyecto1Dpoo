package Persistencia;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Una fila leída de un archivo, con métodos para obtener cada columna con su
 * tipo. Si algo está mal, el error dice el archivo y la línea.
 */
public class Fila {
	private final String archivo;
	private final int numeroLinea;
	private final ArrayList<String> columnas;

	public Fila(String archivo, int numeroLinea, ArrayList<String> columnas) {
		this.archivo = archivo;
		this.numeroLinea = numeroLinea;
		this.columnas = columnas;
	}

	public IOException error(String mensaje) {
		return new IOException(archivo + ", línea " + numeroLinea + ": " + mensaje);
	}

	private String crudo(int i) throws IOException {
		if (i >= columnas.size()) {
			throw error("falta la columna " + (i + 1));
		}
		return columnas.get(i);
	}

	private String crudoOpcional(int i) {
		return (i < columnas.size()) ? columnas.get(i) : "";
	}

	public String texto(int i) throws IOException {
		return Formato.desesc(crudo(i));
	}

	/** Texto, o null si la columna está vacía o no existe. */
	public String textoOpcional(int i) {
		String t = Formato.desesc(crudoOpcional(i));
		return t.isEmpty() ? null : t;
	}

	public int entero(int i) throws IOException {
		try {
			return Integer.parseInt(texto(i).trim());
		} catch (NumberFormatException e) {
			throw error("la columna " + (i + 1) + " debe ser un entero");
		}
	}

	public int enteroOpcional(int i, int porDefecto) throws IOException {
		return crudoOpcional(i).trim().isEmpty() ? porDefecto : entero(i);
	}

	public double decimal(int i) throws IOException {
		try {
			return Double.parseDouble(texto(i).trim());
		} catch (NumberFormatException e) {
			throw error("la columna " + (i + 1) + " debe ser un número");
		}
	}

	public Double decimalOpcional(int i) throws IOException {
		return crudoOpcional(i).trim().isEmpty() ? null : Double.valueOf(decimal(i));
	}

	public boolean booleano(int i) throws IOException {
		String t = texto(i).trim();
		if (t.equals("true")) {
			return true;
		}
		if (t.equals("false")) {
			return false;
		}
		throw error("la columna " + (i + 1) + " debe ser true o false");
	}

	public boolean booleanoOpcional(int i, boolean porDefecto) throws IOException {
		return crudoOpcional(i).trim().isEmpty() ? porDefecto : booleano(i);
	}

	public Date fecha(int i) throws IOException {
		try {
			return Formato.parseFecha(texto(i));
		} catch (ParseException e) {
			throw error("la columna " + (i + 1) + " no es una fecha válida (yyyy-MM-dd HH:mm:ss)");
		}
	}

	public ArrayList<String> lista(int i) {
		return Formato.parseLista(crudoOpcional(i));
	}

	public ArrayList<Integer> listaEnteros(int i) throws IOException {
		ArrayList<Integer> resultado = new ArrayList<Integer>();
		for (String s : lista(i)) {
			try {
				resultado.add(Integer.parseInt(s.trim()));
			} catch (NumberFormatException e) {
				throw error("la lista de la columna " + (i + 1) + " debe contener enteros");
			}
		}
		return resultado;
	}
}
