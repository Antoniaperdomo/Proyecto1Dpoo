package Consola;

import Usuario.Socio;
import Usuario.Usuario;

public class ConsolaSocio extends ConsolaBase{
	
	public static void main(String[] args) {
		new ConsolaSocio().correr();
	}
	
	private static final String[] OPCIONES = {
			"Ver mi perfil y puntos de fidelidad",
			"Equipos: unirme a un equipo",
			"Deportes individuales: inscribirme / ver mi ranking",
			"Reservar una instalación",
			"Programar partido de práctica",
			"Torneos: consultar torneos",
			"Torneos: inscribirme / salirme",
			"Mi bono de descuento",
			"Comprar en tienda o cafetería"	
	};

	@Override
	protected String titulo() {
		// TODO Auto-generated method stub
		return "Menú del Socio";
	}

	@Override
	protected boolean usuarioPermitido(Usuario u) {
		// TODO Auto-generated method stub
		return u instanceof Socio;
	}

	@Override
	protected String[] opciones() {
		// TODO Auto-generated method stub
		return OPCIONES;
	}
	
	private Socio socio() {
		return (Socio) usuarioActual;
	}

	@Override
	protected void ejecutar(int opcion) {
		// TODO Auto-generated method stub
		
	}
	

}
