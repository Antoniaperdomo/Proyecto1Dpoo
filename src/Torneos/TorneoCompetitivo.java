package Torneos;

import java.util.ArrayList;
import java.util.Date;

import Deportes.Categoria;
import Deportes.Deporte;
import Usuario.Participante;

public class TorneoCompetitivo extends Torneo {

	private final double tarifa;

	public TorneoCompetitivo(int id, String nombre, Deporte deporte, Categoria categoria, String nivel,
			String modalidad, int cupos, Date fechaInicio, Date fechaLimiteReserva, double tarifa) {
		super(id, nombre, deporte, categoria, nivel, modalidad, cupos, fechaInicio, fechaLimiteReserva);
		if (tarifa <= 0) {
			throw new IllegalArgumentException("Un torneo competitivo necesita una tarifa mayor a cero");
		}
		this.tarifa = tarifa;
	}

	@Override
	public String getTipo() {
		return "COMPETITIVO";
	}

	@Override
	public double getTarifa() {
		return tarifa;
	}

	@Override
	protected double tarifaPara(boolean exentoDeTarifa) {
		return exentoDeTarifa ? 0 : tarifa;
	}

	@Override
	public String mensajeRetiro(InscripcionTorneo inscripcion) {
		if (inscripcion.getTarifaPagada() > 0) {
			return "Inscripcion retirada. La devolucion de la tarifa ($" + inscripcion.getTarifaPagada()
					+ ") se gestiona fuera del sistema.";
		}
		return super.mensajeRetiro(inscripcion);
	}

	/**
	 * HU18 y HU27: el premio es el total recaudado repartido en partes iguales
	 * entre los integrantes. Si el ganador incluye un empleado, pasa al
	 * siguiente clasificado sin empleados.
	 */
	@Override
	protected void entregarPremios(ArrayList<InscripcionTorneo> clasificacion) {
		double total = getTotalRecaudado();
		for (InscripcionTorneo candidato : clasificacion) {
			if (candidato.tieneEmpleado()) {
				continue;
			}
			ArrayList<Participante> integrantes = candidato.getIntegrantes();
			double parte = total / integrantes.size();
			for (Participante p : integrantes) {
				registrarPremio("$" + parte + " en efectivo para " + p.getNombre());
			}
			return;
		}
		registrarPremio("Ningun clasificado puede recibir el premio en efectivo");
	}

}
