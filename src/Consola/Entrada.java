package Consola;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Entrada {
	private static final String FORMATO_FECHA = "yyyy-MM-dd";
	private static final String FORMATO_FECHA_HORA = "yyyy-MM-dd HH:mm";

	private final Scanner scanner;

	public Entrada(Scanner scanner) {
		super();
		this.scanner = scanner;
	}

	private String leerLinea(String prompt) {
		System.out.print(prompt + ": ");
		return scanner.nextLine();
	}

	private String leerTexto(String prompt) {
		while (true) {
			String s = leerLinea(prompt);
			if (!s.isEmpty()) {
				return s;
			}
			System.out.println(" El valor no puede estar vacio.");
		}
	}

	public int leerEntero(String prompt, int min, int max) {
		while (true) {
			String s = leerLinea(prompt + " [" + min + "-" + max + "]");
			try {
				int n = Integer.parseInt(s);
				if (n >= min && n <= max) {
					return n;
				}
				System.out.println("  Debe estar entre " + min + " y " + max + ".");
			} catch (NumberFormatException e) {
				System.out.println("Ingrese un número entero válido.");
			}

		}
	}

	public double leerDecimal(String prompt, double min) {
		while (true) {
			String s = leerLinea(prompt).replace(',', '.');
			try {
				double d = Double.parseDouble(s);
				if (d >= min) {
					return d;
				}
				System.out.println("  Debe ser mayor o igual a " + min + ".");

			} catch (NumberFormatException e) {
				System.out.println("  Ingrese un número válido.");
			}
		}
	}

	public Boolean leerSiNo(String prompt) {
		while (true) {
			String s = leerLinea(prompt + " (s/n) ").toLowerCase();
			if (s.equals("s") || s.equals("si") || s.equals("sí")) {
				return true;
			}
			if (s.equals("n") || s.equals("no")) {
				return false;
			}
			System.out.println("Responda s o n.");
		}
	}

	private Date leerFechaConFormato(String prompt, String formato) {
		SimpleDateFormat sdf = new SimpleDateFormat(formato);
		sdf.setLenient(false);
		while (true) {
			String s = leerLinea(prompt + "(" + formato + ")");
			try {
				return sdf.parse(s);
			} catch (ParseException e) {
				System.out.println("  Fecha inválida. Use el formato " + formato + ".");
			}
		}
	}

	public Date leerFechaHora(String prompt) {
		return leerFechaConFormato(prompt, FORMATO_FECHA_HORA);
	}

	public Date leerFcha(String prompt) {
		return leerFechaConFormato(prompt, FORMATO_FECHA);
	}
	public String leerCredencial(String prompt) {
		return leerLinea(prompt);
	}

}
