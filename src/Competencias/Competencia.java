package Competencias;

import java.util.ArrayList;

import Deportes.Categoria;
import Deportes.Deporte;
import Deportes.Equipo;
import Deportes.PartidoCompetencia;
import Usuario.Socio;

public abstract class Competencia {
	protected String nombre;
	protected String entidadOrganizadora;
	protected String temporadaOAño;
	protected Deporte deporte;
	protected Categoria categoria;
	protected ArrayList<NominaCompetencia> nominas;
	protected ArrayList<PartidoCompetencia> partidos;

	public Competencia(String nombre, String entidadOrganizadora, String temporadaOAño, Deporte deporte,
			Categoria categoria) {
		super();
		this.nombre = nombre;
		this.entidadOrganizadora = entidadOrganizadora;
		this.temporadaOAño = temporadaOAño;
		this.deporte = deporte;
		this.categoria = categoria;
		this.nominas = new ArrayList<NominaCompetencia>();
		this.partidos = new ArrayList<PartidoCompetencia>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEntidadOrganizadora() {
		return entidadOrganizadora;
	}

	public void setEntidadOrganizadora(String entidadOrganizadora) {
		this.entidadOrganizadora = entidadOrganizadora;
	}

	public String getTemporadaOAño() {
		return temporadaOAño;
	}

	public void setTemporadaOAño(String temporadaOAño) {
		this.temporadaOAño = temporadaOAño;
	}

	public Deporte getDeporte() {
		return deporte;
	}

	public void setDeporte(Deporte deporte) {
		this.deporte = deporte;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public ArrayList<NominaCompetencia> getNominas() {
		return nominas;
	}

	public ArrayList<PartidoCompetencia> getPartidos() {
		return partidos;
	}
	
	public abstract TablaPosiciones getTabla();
	
	//RF 11
	public void regustrarResultado(Equipo equipo, int puntos) {
		getTabla().sumarPuntos(equipo, puntos);
	}
	
	public NominaCompetencia buscarNomina(Equipo equipo) {
		for (NominaCompetencia n : nominas) {
			if(n.getEquipo() == equipo) {
				return n;
			}
		}
		return null;
	}

	//RF 09
	public void agrgarNomina(NominaCompetencia nomina) {
		if(nomina.getCompetencia() != this) {
			throw new IllegalArgumentException("La nomina es de otra competencia");
		}
		if(buscarNomina(nomina.getEquipo()) != null) {
			throw new IllegalArgumentException("El equipo " + nomina.getEquipo().getNombre() + " ya esta inscrito en " + nombre);
		}
		nominas.add(nomina);
	}
	public void inscribir(NominaCompetencia nomina) {
		agrgarNomina(nomina);
		getTabla().registrarUnEquipo(nomina.getEquipo());
	}
	//RF10
	public boolean puedeJugar(Equipo equipo) {
		return buscarNomina(equipo) != null;
	}
	
	public void agregarPartido(PartidoCompetencia partido) {
		if(!partidos.contains(partido)) {
			partidos.add(partido);
		}
	}
	
	//RF 13
	public int getPuntajeAcumulado(Socio socio) {
		int total = 0;
		for(PartidoCompetencia p : partidos) {
			if (p.isJugado()) {
				total += p.getPuntajeDestacado(socio);
			}
		}
		return total;
	}
	public ArrayList<Socio> getClasificacionJugadores(){
		ArrayList<Socio> jugadores = new ArrayList<Socio>();
		for (NominaCompetencia n : nominas){
			for(Socio s :n.getJugadoresHabilitados()) {
				if(!jugadores.contains(s)) {
					jugadores.add(s);
				}
			}
		}
		for (int i= 0; i< jugadores.size(); i++) {
			int mejor = i;
			for (int j = i +1; j < jugadores.size(); j++) {
				if(getPuntajeAcumulado(jugadores.get(j))>getPuntajeAcumulado(jugadores.get(mejor))) {
					mejor = j;
				}
			}
			Socio temp = jugadores.get(i);
			jugadores.set(i, jugadores.get(mejor));
			jugadores.set(mejor, temp);
		}
		return jugadores;
	}
	
	public Socio getMejorJugador() {
		ArrayList<Socio> clasificacion = getClasificacionJugadores();
		if(clasificacion.isEmpty()) {
			return null;
		}
		return clasificacion.get(0);
	}

}
