package Torneos;

import java.util.ArrayList;
import java.util.Date;

import Deportes.Categoria;
import Deportes.Deporte;
import Usuario.Participante;
import Usuario.Socio;

public class TorneoRecreativo extends Torneo {

	private final int porcentajeBono;

	public TorneoRecreativo(int id, String nombre, Deporte deporte, Categoria categoria, String nivel,
			String modalidad, int cupos, Date fechaInicio, Date fechaLimiteReserva, double tarifa,
			int porcentajeBono) {
		super(id, nombre, deporte, categoria, nivel, modalidad, cupos, fechaInicio, fechaLimiteReserva);
		if (tarifa > 0) {
			throw new IllegalArgumentException("Un torneo recreativo no tiene tarifa de inscripcion");
		}
		if (porcentajeBono < 1 || porcentajeBono > 100) {
			throw new IllegalArgumentException("El porcentaje del bono debe estar entre 1 y 100");
		}
		this.porcentajeBono = porcentajeBono;
	}

	public int getPorcentajeBono() {
		return porcentajeBono;
	}

	@Override
	public String getTipo() {
		return "RECREATIVO";
	}

	@Override
	public double getTarifa() {
		return 0;
	}

	@Override
	protected double tarifaPara(boolean exentoDeTarifa) {
		return 0;
	}

	/** HU28: un bono por cada socio del equipo o pareja ganadora; empleados y externos no reciben. */
	@Override
	protected void entregarPremios(ArrayList<InscripcionTorneo> clasificacion) {
		for (Participante p : clasificacion.get(0).getIntegrantes()) {
			if (p instanceof Socio) {
				((Socio) p).agregarBono(new BonoDescuento(getNombre(), porcentajeBono));
				registrarPremio("Bono de " + porcentajeBono + "% para " + p.getNombre());
			}
		}
	}

}
