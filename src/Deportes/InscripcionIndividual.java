package Deportes;

import java.util.ArrayList;

import Usuario.Socio;

public class InscripcionIndividual {
	private static final int ELO_INICIAL = 1000;
	private static final int K = 32;
	private static final int ELO_INTERMEDIO = 1200;
	private static final int ELO_AVANZADO = 1400;

	private Socio socio;
	private DeporteIndividual deporte;
	private int rankingELO;
	private ArrayList<Integer> historialElo;

	public InscripcionIndividual(Socio socio, DeporteIndividual deporte) {
		this.socio = socio;
		this.deporte = deporte;
		this.rankingELO = ELO_INICIAL;
		this.historialElo = new ArrayList<Integer>();
		this.historialElo.add(ELO_INICIAL);
		actualizarNivel();
	}

	public Socio getSocio() {
		return socio;
	}

	public void setSocio(Socio socio) {
		this.socio = socio;
	}

	public DeporteIndividual getDeporte() {
		return deporte;
	}

	public void setDeporte(DeporteIndividual deporte) {
		this.deporte = deporte;
	}
	
	public int getRankingELO() {
		return rankingELO;
	}
	
	// RF 16

	public ArrayList<Integer> getHistorialElo() {
		return historialElo;
	}

	public void setHistorialElo(ArrayList<Integer> historialElo) {
		this.historialElo = historialElo;
	}

	private void actualizarNivel() {
		if (rankingELO >= ELO_AVANZADO) {
			deporte.setNivel(DeporteIndividual.AVANZADO);
		} else if (rankingELO >= ELO_INTERMEDIO) {
			deporte.setNivel(DeporteIndividual.INTERMEDIO);
		} else {
			deporte.setNivel(DeporteIndividual.PRINCIPIANTE);
		}
	}
	
	// Persistencia: restaura el ELO y su historial tal como se guardaron
		public void restaurar(int rankingELO, ArrayList<Integer> historial) {
			this.rankingELO = rankingELO;
			this.historialElo = new ArrayList<Integer>(historial);
			if (this.historialElo.isEmpty()) {
				this.historialElo.add(rankingELO);
			}
			actualizarNivel();
		}
	// RF 21
	public void actualizarRanking(double resultado, int eloRival) {
		double esperado = 1.0 / (1 + Math.pow(10, (eloRival - rankingELO) / 400.00));
		rankingELO = (int) Math.round(rankingELO + K * (resultado - esperado));
		actualizarNivel();
	}
	

}
