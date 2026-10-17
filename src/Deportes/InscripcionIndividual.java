package Deportes;

import Usuario.Socio;

public class InscripcionIndividual {
	private static final int ELO_INICIAL = 1000;
	private static final int K = 32;
	private static final int ELO_INTERMEDIO = 1200;
	private static final int ELO_AVANZADO = 1400;

	private Socio socio;
	private DeporteIndividual deporte;
	private int rankingELO;

	public InscripcionIndividual(Socio socio, DeporteIndividual deporte, int rankingELO) {
		super();
		this.socio = socio;
		this.deporte = deporte;
		this.rankingELO = rankingELO;
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

	public void setRankingELO(int rankingELO) {
		this.rankingELO = rankingELO;
	}

	// RF 16

	private void actualizarNivel() {
		if (rankingELO >= ELO_AVANZADO) {
			deporte.setNivel(DeporteIndividual.AVANZADO);
		} else if (rankingELO >= ELO_INTERMEDIO) {
			deporte.setNivel(DeporteIndividual.INTERMEDIO);
		} else {
			deporte.setNivel(DeporteIndividual.PRINCIPIANTE);
		}
	}

	// RF 21
	public void actualizarRanking(double resultado, int eloRival) {
		double esperado = 1.0 / (1 + Math.pow(10, (eloRival - rankingELO) / 400.00));
		rankingELO = (int) Math.round(rankingELO + K * (resultado - esperado));
		actualizarNivel();
	}

}
