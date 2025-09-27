package com.analistas.stella.model.domain;

import lombok.Data;
import java.util.List;

@Data
public class VentaDTO {
    private String fecha;
    private String tipoCliente;
    private String docCliente;
    private String metodoPago;
    private Double descuentoGlobalPorc;
    private Double descuentoGlobalMonto;
    private Double subtotalSinIVA;
    private Double ivaTotal;
    private Double total;
    private Double recibido;
    private Double vuelto;
    private String observaciones;
    private List<DetalleVentaDTO> items;
    private List<MetodoPagoDTO2> pagos;
}
