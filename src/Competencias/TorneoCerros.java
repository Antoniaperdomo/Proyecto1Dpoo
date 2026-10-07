package Competencias;

public class TorneoCerros extends Competencia {
	private TablaPosiciones  tablaPosiciones;

	public TorneoCerros(String nombre, String entidadOrganizadora, String temporadaOAño, Deporte deporte,
			String categoria, TablaPosiciones tablaPosiciones) {
		super(nombre, entidadOrganizadora, temporadaOAño, deporte, categoria);
		this.tablaPosiciones = tablaPosiciones;
	}

	public TablaPosiciones getTablaPosiciones() {
		return tablaPosiciones;
	}

	public void setTablaPosiciones(TablaPosiciones tablaPosiciones) {
		this.tablaPosiciones = tablaPosiciones;
	}
	
	

}
