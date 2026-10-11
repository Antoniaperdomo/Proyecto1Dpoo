package Tienda;

public class ExistenciaArticulo {
	private Articulo articulo;
	private Ubicacion ubicacion;
	private int cantidadDisponible;

	public ExistenciaArticulo(Articulo articulo, Ubicacion ubicacion, int cantidadDisponible) {
		super();
		this.articulo = articulo;
		this.ubicacion = ubicacion;
		this.cantidadDisponible = cantidadDisponible;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public Ubicacion getUbicacion() {
		return ubicacion;
	}

	public int getCantidadDisponible() {
		return cantidadDisponible;
	}

	public void reducir(int cantidad) {
		if (cantidad > cantidadDisponible) {
			throw new IllegalStateException("No hay suficientes unidades en el inventario de " + articulo.getNombre());
		}
	}

	public void aumentar(int cantidad) {
		cantidadDisponible += cantidad;
	}

}
