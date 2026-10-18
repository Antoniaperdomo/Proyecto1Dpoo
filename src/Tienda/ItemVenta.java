package Tienda;

public class ItemVenta {
	private Producto producto;
	private int cantidad;

	public ItemVenta(Producto producto, int cantidad) {
		if (producto == null) {
			throw new IllegalArgumentException("El producto es obligatorio");
		}
		if (cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
		}
		this.producto = producto;
		this.cantidad = cantidad;
	}
	public Producto getProducto() {
		return producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public double getSubtotal() {
		return producto.getPrecio() * cantidad;
	}

}
