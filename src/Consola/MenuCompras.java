package Consola;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import Tienda.Articulo;
import Tienda.CodigoDescuento;
import Tienda.ItemVenta;
import Tienda.Producto;
import Tienda.ProductoCafeteria;
import Tienda.Ubicacion;
import Tienda.Venta;
import Tienda.VentaCafeteria;
import Tienda.VentaTienda;
import Persistencia.DatosClub;
import Usuario.Comprador;
import Usuario.Empleado;
import Usuario.Usuario;

public class MenuCompras {

	private final Entrada entrada;
	private final DatosClub datos;
	private final Usuario comprador;

	public MenuCompras(Entrada entrada, DatosClub datos, Usuario comprador) {
		super();
		this.entrada = entrada;
		this.datos = datos;
		this.comprador = comprador;
	}

	private ArrayList<ItemVenta> armarItems(boolean esTienda) {
		ArrayList<Producto> disponibles = new ArrayList<Producto>();
		ArrayList<String> descripciones = new ArrayList<String>();
		for (Producto p : datos.getProductos()) {
			boolean sirve;
			if (esTienda) {
				sirve = p instanceof Articulo;
			} else {
				sirve = p instanceof ProductoCafeteria;
			}
			if (sirve) {
				disponibles.add(p);
				descripciones.add(p.getNombre() + " - $" + p.getPrecio());
			}
		}
		ArrayList<ItemVenta> items = new ArrayList<ItemVenta>();
		boolean seguir = true;
		while (seguir) {
			int pos = entrada.elegirIndice("Productos", descripciones);
			if (pos < 0) {
				seguir = false;
			} else {
				int cantidad = entrada.leerEntero("Cantidad", 1, 100);
				items.add(new ItemVenta(disponibles.get(pos), cantidad));
				seguir = entrada.leerSiNo("¿Agregar otro producto?");
			}
		}
		return items;
	}

	private int pedirDescuento() {
		boolean compartido = false;
		if (!(comprador instanceof Empleado)) {
			compartido = entrada.leerSiNo("¿Tiene un código de descuento compartido por un empleado?");
		}
		int porcentaje = CodigoDescuento.calcularPorcentaje((Comprador) comprador, compartido);
		if (porcentaje > 0) {
			System.out.println("Descuento aplicado: " + porcentaje + "%");
		}
		return porcentaje;
	}

	private void pedirPuntos(Venta venta) {
		int disponibles = ((Comprador) comprador).getPuntosFidelidad();
		if (disponibles > 0 && entrada.leerSiNo("Tiene " + disponibles + " puntos. ¿Desea redimirlos?")) {
			int puntos = entrada.leerEntero("Puntos a redimir", 0, disponibles);
			venta.redimirPuntos(puntos);
		}
	}

	private void mostrarResumen(Venta venta) {
		System.out.println("\n--- Resumen de la compra ---");
		for (ItemVenta item : venta.getItems()) {
			System.out
					.println(item.getCantidad() + " x " + item.getProducto().getNombre() + " = $" + item.getSubtotal());
		}
		for (Map.Entry<String, Double> rubro : venta.getRubros().entrySet()) {
			System.out.println(rubro.getKey() + ": $" + String.format("%.2f", rubro.getValue()));
		}
	}

	private void confirmar(Venta venta, boolean esTienda) {
		if (esTienda) {
			ArrayList<Ubicacion> ubicaciones = datos.getUbicaciones();
			ArrayList<String> nombres = new ArrayList<String>();
			for (Ubicacion u : ubicaciones) {
				nombres.add(u.getNombre());
			}
			int pos = entrada.elegirIndice("¿De qué ubicación sale el inventario?", nombres);
			if (pos < 0) {
				System.out.println("Compra cancelada.");
				return;
			}
			((VentaTienda) venta).confirmar(datos.getInventario().getExistencias(), ubicaciones.get(pos));
		} else {
			((VentaCafeteria) venta).confirmar();
		}
		datos.getRegistroVentas().registrar(venta);
		System.out.println("Compra confirmada. Puntos ganados: " + venta.calcularPuntos());
		System.out.println("Puntos actuales: " + ((Comprador) comprador).getPuntosFidelidad());
	}

	public void comprar() {
		String[] lugares = { "Tienda", "Cafetería" };
		int lugar = entrada.elegirOpccion("¿Dónde desea comprar?", lugares);
		if (lugar == 0) {
			return;
		}
		boolean esTienda = (lugar == 1);

		ArrayList<ItemVenta> items = armarItems(esTienda);
		if (items.isEmpty()) {
			System.out.println("No se agregó ningún producto. Compra cancelada.");
			return;
		}
		int descuento = pedirDescuento();
		Venta venta;
		if (esTienda) {
			venta = new VentaTienda(new Date(), comprador, items, descuento);
		} else {
			double propina = entrada.leerDecimal("Propina en % (la sugerida es 10)", 0);
			venta = new VentaCafeteria(new Date(), comprador, items, descuento, propina);
		}

		pedirPuntos(venta);
		mostrarResumen(venta);

		if (!esTienda) {
			String alergenos = ((VentaCafeteria) venta).getAdvertenciaAlergenos();
			if (!alergenos.isEmpty()) {
				System.out.println("\n" + alergenos);
			}
		}

		if (!entrada.leerSiNo("¿Confirmar la compra?")) {
			System.out.println("Compra cancelada.");
			return;
		}
		confirmar(venta, esTienda);
	}

}
