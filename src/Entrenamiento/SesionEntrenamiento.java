package Entrenamiento;

import java.util.ArrayList;
import java.util.Date;

import Usuario.Entrenador;
import Usuario.Socio;

public class SesionEntrenamiento {

	private Date fecha;
	private int hora;
	private double duracion;
	private Entrenador entrenadorResponsable;
	private String instalacionUtilizada;
	private ArrayList<Socio> listaDeJugadoresConvocados;
	private ArrayList<RegistroAsistencia> registrosAsistencia;
	private boolean finalizada;

	public SesionEntrenamiento(Date fecha, int hora, double duracion, Entrenador entrenadorResponsable,
			String instalacionUtilizada) {
		this.fecha = fecha;
		this.hora = hora;
		this.duracion = duracion;
		this.entrenadorResponsable = entrenadorResponsable;
		this.instalacionUtilizada = instalacionUtilizada;
		this.listaDeJugadoresConvocados = new ArrayList<Socio>();
		this.registrosAsistencia = new ArrayList<RegistroAsistencia>();
		this.finalizada = false;
	}

	public boolean convocarJugador(Socio socio) {
		if (finalizada || socio == null || !socio.isActivo() || listaDeJugadoresConvocados.contains(socio)) {
			return false;
		}
		listaDeJugadoresConvocados.add(socio);
		return true;
	}

	public boolean quitarJugador(Socio socio) {
		if (finalizada) {
			return false;
		}
		return listaDeJugadoresConvocados.remove(socio);
	}

	public boolean estaConvocado(Socio socio) {
		return listaDeJugadoresConvocados.contains(socio);
	}

	public boolean esIndividual() {
		return listaDeJugadoresConvocados.size() == 1;
	}

	public boolean esGrupal() {
		return listaDeJugadoresConvocados.size() > 1;
	}

	public void finalizarSesion() {
		this.finalizada = true;
	}

	public RegistroAsistencia registrarAsistencia(Socio socio, String estado, Double evaluacion, String comentario)
			throws IllegalArgumentException, IllegalStateException {
		if (!finalizada) {
			throw new IllegalStateException("La sesion aun no ha terminado");
		}
		if (!estaConvocado(socio)) {
			throw new IllegalArgumentException("El socio no fue convocado a esta sesion");
		}
		if (obtenerRegistro(socio) != null) {
			throw new IllegalArgumentException("La asistencia de este socio ya fue registrada");
		}
		RegistroAsistencia registro = new RegistroAsistencia(this, socio, estado);
		registro.setEvaluacion(evaluacion);
		registro.setComentario(comentario);
		registrosAsistencia.add(registro);
		return registro;
	}

	public RegistroAsistencia obtenerRegistro(Socio socio) {
		for (RegistroAsistencia registro : registrosAsistencia) {
			if (registro.getSocio() == socio) {
				return registro;
			}
		}
		return null;
	}

	public boolean asistenciaCompleta() {
		return registrosAsistencia.size() == listaDeJugadoresConvocados.size();
	}

	public double calcularPromedioEvaluaciones() {
		double suma = 0;
		int cantidad = 0;
		for (RegistroAsistencia registro : registrosAsistencia) {
			if (registro.getEvaluacion() != null) {
				suma += registro.getEvaluacion();
				cantidad++;
			}
		}
		return cantidad == 0 ? 0 : suma / cantidad;
	}

	public int contarAsistentes() {
		int contador = 0;
		for (RegistroAsistencia registro : registrosAsistencia) {
			if (registro.asistio()) {
				contador++;
			}
		}
		return contador;
	}

	public Date getFecha() {
		return fecha;
	}

	public int getHora() {
		return hora;
	}

	public double getDuracion() {
		return duracion;
	}

	public Entrenador getEntrenadorResponsable() {
		return entrenadorResponsable;
	}

	public String getInstalacionUtilizada() {
		return instalacionUtilizada;
	}

	public ArrayList<Socio> getListaDeJugadoresConvocados() {
		return listaDeJugadoresConvocados;
	}

	public ArrayList<RegistroAsistencia> getRegistrosAsistencia() {
		return registrosAsistencia;
	}

	public boolean isFinalizada() {
		return finalizada;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public void setHora(int hora) {
		this.hora = hora;
	}

	public void setDuracion(double duracion) {
		this.duracion = duracion;
	}

	public void setEntrenadorResponsable(Entrenador entrenadorResponsable) {
		this.entrenadorResponsable = entrenadorResponsable;
	}

	public void setInstalacionUtilizada(String instalacionUtilizada) {
		this.instalacionUtilizada = instalacionUtilizada;
	}

}