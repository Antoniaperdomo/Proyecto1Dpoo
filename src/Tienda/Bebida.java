package Tienda;

public class Bebida extends ProductoCafeteria {
	public static final String FRIA = "FRIA";
	public static final String CALIENTE = "CALIENTE";

	private String tipo;

	public Bebida(String nombre, double precio, String tipo) {
		super(nombre, precio);
		if (!FRIA.equals(tipo) && !CALIENTE.equals(tipo)) {
			throw new IllegalArgumentException("Tipo invalido: " + tipo);
		}
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean esCaliente() {
		return CALIENTE.equals(tipo);
	}

}
