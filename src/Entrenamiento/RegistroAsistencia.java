package Entrenamiento;

import Usuario.Socio;

public class RegistroAsistencia {

	public static final String ASISTIO = "asistio";
	public static final String FALTO = "falto";
	public static final String LLEGO_TARDE = "llego tarde";

	private SesionEntrenamiento sesion;
	private Socio socio;
	private String estado;
	private Double evaluacion;
	private String comentario;

	public RegistroAsistencia(SesionEntrenamiento sesion, Socio socio, String estado) throws IllegalArgumentException {
		this.sesion = sesion;
		this.socio = socio;
		setEstado(estado);
	}

	public boolean asistio() {
		return estado.equals(ASISTIO) || estado.equals(LLEGO_TARDE);
	}

	public boolean tieneEvaluacion() {
		return evaluacion != null;
	}

	public SesionEntrenamiento getSesion() {
		return sesion;
	}

	public Socio getSocio() {
		return socio;
	}

	public String getEstado() {
		return estado;
	}

	public Double getEvaluacion() {
		return evaluacion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setEstado(String estado) throws IllegalArgumentException {
		if (!ASISTIO.equals(estado) && !FALTO.equals(estado) && !LLEGO_TARDE.equals(estado)) {
			throw new IllegalArgumentException("Estado invalido: debe ser asistio, falto o llego tarde");
		}
		this.estado = estado;
	}

	public void setEvaluacion(Double evaluacion) throws IllegalArgumentException {
		if (evaluacion != null && (evaluacion < 1 || evaluacion > 5)) {
			throw new IllegalArgumentException("La evaluacion debe estar entre 1 y 5");
		}
		this.evaluacion = evaluacion;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Override
	public String toString() {
		return socio.getNombre() + " - " + estado + (evaluacion != null ? " - nota: " + evaluacion : "")
				+ (comentario != null ? " - " + comentario : "");
	}

}