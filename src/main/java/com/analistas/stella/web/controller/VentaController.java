package com.analistas.stella.web.controller;

import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.domain.ProductoDTO;
import com.analistas.stella.model.domain.Venta;
import com.analistas.stella.model.service.IProductoService;
import com.analistas.stella.model.service.IUsuarioService;
import com.analistas.stella.model.service.IVentaService;
import com.analistas.stella.model.domain.DetalleVenta;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    IVentaService ventaService;

    @Autowired
    IProductoService productoService;

    @Autowired
    IUsuarioService usuarioService;

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "ventas/lista"; // Vista Thymeleaf o JSP
    }

    @GetMapping("/nuevo")
    public String nuevaVenta(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("productos", productoService.buscarTodo());
        return "ventas/ventas-form";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardar(@RequestBody com.analistas.stella.model.domain.VentaDTO ventaDTO) {
        Venta venta = new Venta();
        venta.setTipoCliente(ventaDTO.getTipoCliente());
        venta.setDocCliente(ventaDTO.getDocCliente());
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setDescuentoGlobalPorc(ventaDTO.getDescuentoGlobalPorc());
        venta.setDescuentoGlobalMonto(ventaDTO.getDescuentoGlobalMonto());
        venta.setSubtotalSinIVA(ventaDTO.getSubtotalSinIVA());
        venta.setIvaTotal(ventaDTO.getIvaTotal());
        venta.setTotal(ventaDTO.getTotal());
        venta.setUsuario(usuarioService.buscarPorId(1L)); //Cambiar esto para cuando los USUARIOS puedan loguearse
        venta.setFechaVenta(LocalDateTime.parse(
            ventaDTO.getFecha().length() == 16 ? ventaDTO.getFecha() + ":00" : ventaDTO.getFecha(),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ));
        // Si tienes usuario logueado, setea aquí
        // venta.setUsuario(...);

        // Mapear los items a DetalleVenta
        for (com.analistas.stella.model.domain.DetalleVentaDTO d : ventaDTO.getItems()) {
            DetalleVenta det = new DetalleVenta();
            List<Producto> productos = productoService.buscarPorCodigoODescripcion(d.getCodigo()); // Debes tener este método
            if (productos != null && !productos.isEmpty()) {
                det.setProducto(productos.get(0));
            } else {
                // Manejar el caso en que no se encuentra el producto
                continue;
            }
            det.setCantidad(d.getCantidad().intValue());
            det.setPrecioUnitario(d.getPrecioUnitaFrio());
            det.setIvaPorc(d.getIvaPorc());
            det.setDescuentoPorc(d.getDescuentoPorc());
            // Calcula el subtotal
            double base = d.getCantidad() * d.getPrecioUnitaFrio();
            double desc = base * (d.getDescuentoPorc()/100);
            double neto = base - desc;
            double ivaMonto = neto * (d.getIvaPorc()/100);
            det.setSubtotal(neto + ivaMonto);
            det.setVenta(venta);
            venta.getDetalles().add(det);
        }

        ventaService.guardar(venta);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", venta.getId());
        resp.put("numero", venta.getId());

        return ResponseEntity.ok(resp);
    }

    @RestController
    @RequestMapping("/api/productos")
    public class ProductoRestController {
        @Autowired
        IProductoService productoService;

        @GetMapping("/buscar")
        public List<ProductoDTO> buscar(@RequestParam String q) {
            return productoService.buscarPorCodigoODescripcion(q)
                .stream()
                .map(p -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setId(p.getId());
                    dto.setCodigo(p.getCodigoBarras());
                    dto.setNombre(p.getNombre());
                    dto.setPrecioMin(p.getPrecioMin() != null ? p.getPrecioMin().doubleValue() : null);
                    dto.setPrecioBulto(p.getPrecioBulto() != null ? p.getPrecioBulto().doubleValue() : null);
                    return dto;
                })
                .toList();
        }
    }

}
