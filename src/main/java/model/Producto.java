package model;
import java.math.BigDecimal;

public class Producto {
    private String codigo; // Usaremos cod_cabys como código
    private String nombre;
    private String descripcion;
    private BigDecimal precioUnitario;
    private int stock;
    private boolean activo;

    public Producto() {}

    // Constructor limpio (sin idProducto porque no existe en la BD)
    public Producto(String codigo, String nombre, String descripcion, BigDecimal precioUnitario, int stock, boolean activo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
        this.activo = activo;
    }

    // Getters y Setters...
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}