package com.analistas.stella.model.service;

import com.analistas.stella.model.domain.*;
import com.analistas.stella.model.repository.ICompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.analistas.stella.config.SecurityUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CompraServiceImpl implements ICompraService {

    @Autowired
    private ICompraRepository compraRepository;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    ICategoriaService categoriaService;

    @Override
    public List<Compra> listar() {
        return (List<Compra>) compraRepository.findAll();
    }

    @Override
    public Compra guardar(Compra compra) {
        return compraRepository.save(compra);
    }

    @Override
    public Compra guardarDesdeDTO(CompraDTO dto) {
        Compra compra = new Compra();

        //Asignar una categoria base para cualquier producto agregado:
        Categoria cat = categoriaService.buscarPorId(1L);

        // asignar usuario autenticado
        String username = SecurityUtils.getCurrentUsername();
        Usuario usuario = usuarioService.findByNombrecompleto(username);
        if (usuario == null) {
            throw new IllegalStateException("Usuario autenticado no encontrado: " + username);
        }
        compra.setUsuario(usuario);

        compra.setObservaciones(dto.getObservaciones());
        compra.setFechaCompra(LocalDateTime.parse(
                dto.getFecha().length() == 16 ? dto.getFecha() + ":00" : dto.getFecha(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        double subtotal = 0.0;
        double ivaTotal = 0.0;

        if (dto.getItems() != null) {
            for (DetalleCompraDTO d : dto.getItems()) {
                DetalleCompra det = new DetalleCompra();

                List<Producto> encontrados = productoService.buscarPorCodigoODescripcion(d.getCodigo());
                Producto producto;
                int cantidadInt = d.getCantidad() != null ? d.getCantidad().intValue() : 0;

                if (encontrados != null && !encontrados.isEmpty()) {
                    producto = encontrados.get(0);
                    int nuevoStock = (producto.getStock() != null ? producto.getStock() : 0) + cantidadInt;
                    producto.setStock(nuevoStock);
                    // opcional: actualizar precios si vienen en la compra
                    if (d.getPrecioUnitario() != null) {
                        producto.setPrecioMin(d.getPrecioUnitario().intValue());
                    }
                    productoService.guardar(producto);
                } else {
                    producto = new Producto();
                    producto.setCodigoBarras(d.getCodigo());
                    producto.setNombre(d.getDescripcion() != null ? d.getDescripcion() : "SIN_NOMBRE");
                    producto.setPrecioMin(d.getPrecioUnitario() != null ? d.getPrecioUnitario().intValue() : 0);
                    producto.setPrecioBulto(d.getPrecioUnitario() != null ? d.getPrecioUnitario().intValue() : 0);
                    producto.setStock(cantidadInt);
                    producto.setStockMin(0);
                    producto.setActivo(true);
                    producto.setCategoria(cat);
                    producto.setFechaVen(LocalDate.now());
                    productoService.guardar(producto);
                }

                det.setProducto(producto);
                det.setCantidad(cantidadInt);
                det.setPrecioUnitario(d.getPrecioUnitario() != null ? d.getPrecioUnitario() : 0.0);
                det.setIvaPorc(d.getIvaPorc() != null ? d.getIvaPorc() : 0.0);
                det.setDescuentoPorc(d.getDescuentoPorc() != null ? d.getDescuentoPorc() : 0.0);

                double base = det.getCantidad() * det.getPrecioUnitario();
                double desc = base * (det.getDescuentoPorc() / 100.0);
                double neto = base - desc;
                double ivaMonto = neto * (det.getIvaPorc() / 100.0);
                det.setSubtotal(neto + ivaMonto);
                det.setCompra(compra);
                compra.getDetalles().add(det);

                subtotal += neto;
                ivaTotal += ivaMonto;
            }
        }

        compra.setSubtotalSinIVA(subtotal);
        compra.setIvaTotal(ivaTotal);
        compra.setTotal(subtotal + ivaTotal);
        // usuario: si necesita setear, se puede setear desde seguridad similar a
        // VentaService
        return compraRepository.save(compra);
    }
}