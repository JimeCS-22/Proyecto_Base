package model;

import java.math.BigDecimal;

public class DetalleFactura {
    private int idDetalle;
    private int idFactura;
    private String codCabys; // CAMBIADO: Ahora es String
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotalLinea;

    public DetalleFactura() {}

    // CAMBIADO: Recibe un String para el código CABYS
    public DetalleFactura(String codCabys, int cantidad, BigDecimal precioUnitario) {
        this.codCabys = codCabys;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotalLinea = precioUnitario.multiply(new BigDecimal(cantidad));
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public String getCodCabys() { return codCabys; } // CAMBIADO
    public void setCodCabys(String codCabys) { this.codCabys = codCabys; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotalLinea() { return subtotalLinea; }
    public void setSubtotalLinea(BigDecimal subtotalLinea) { this.subtotalLinea = subtotalLinea; }
}