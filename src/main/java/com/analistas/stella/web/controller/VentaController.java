package com.analistas.stella.web.controller;

import com.analistas.stella.model.domain.Producto;
import com.analistas.stella.model.domain.ProductoDTO;
import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.domain.Venta;
import com.analistas.stella.model.repository.ICajaRepository;
import com.analistas.stella.model.service.IProductoService;
import com.analistas.stella.model.service.IUsuarioService;
import com.analistas.stella.model.service.IVentaService;

import jakarta.servlet.http.HttpSession;

import com.analistas.stella.config.SecurityUtils;
import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.domain.DetalleVenta;
import com.analistas.stella.model.domain.PagoVenta;
import com.analistas.stella.model.domain.MetodoPagoDTO2; // tu DTO clase

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    ICajaRepository cajaRepository;

    @Autowired
    IVentaService ventaService;

    @Autowired
    IProductoService productoService;

    @Autowired
    IUsuarioService usuarioService;

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "ventas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaVenta(Model model, @RequestParam(required = false) Long CajaId) {
        model.addAttribute("cajaId", CajaId);
        model.addAttribute("venta", new Venta());
        model.addAttribute("productos", productoService.buscarTodo());

        return "ventas/ventas-form";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardar(@RequestBody com.analistas.stella.model.domain.VentaDTO ventaDTO,
            HttpSession session) {
        Venta venta = new Venta();

        String username = SecurityUtils.getCurrentUsername();
        Usuario usuario = usuarioService.findByNombrecompleto(username);
        System.out.println("Username from authentication: " + username);

        venta.setTipoCliente(ventaDTO.getTipoCliente());
        venta.setDocCliente(ventaDTO.getDocCliente());
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setDescuentoGlobalPorc(ventaDTO.getDescuentoGlobalPorc());
        venta.setDescuentoGlobalMonto(ventaDTO.getDescuentoGlobalMonto());
        venta.setSubtotalSinIVA(ventaDTO.getSubtotalSinIVA());
        venta.setIvaTotal(ventaDTO.getIvaTotal());
        venta.setTotal(ventaDTO.getTotal());
        venta.setVuelto(ventaDTO.getVuelto());
        venta.setRecibido(ventaDTO.getRecibido());
        venta.setUsuario(usuario);
        venta.setFechaVenta(LocalDateTime.parse(
                ventaDTO.getFecha().length() == 16 ? ventaDTO.getFecha() + ":00" : ventaDTO.getFecha(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
        Long cajaId = (Long) session.getAttribute("cajaId");
        if (cajaId != null) {
            Caja caja = cajaRepository.findById(cajaId)
                    .orElseThrow(() -> new IllegalStateException("Caja no encontrada"));
            venta.setCaja(caja);
        }

        // Mapear los items a DetalleVenta
        for (com.analistas.stella.model.domain.DetalleVentaDTO d : ventaDTO.getItems()) {
            DetalleVenta det = new DetalleVenta();
            List<Producto> productos = productoService.buscarPorCodigoODescripcion(d.getCodigo()); // Debes tener este
                                                                                                   // método
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
            double desc = base * (d.getDescuentoPorc() / 100);
            double neto = base - desc;
            double ivaMonto = neto * (d.getIvaPorc() / 100);
            det.setSubtotal(neto + ivaMonto);
            det.setVenta(venta);
            venta.getDetalles().add(det);
        }

        // Mapear pagos (si vienen) a PagoVenta y agregarlos a la venta
        if (ventaDTO.getPagos() != null && !ventaDTO.getPagos().isEmpty()) {
            for (MetodoPagoDTO2 p : ventaDTO.getPagos()) {
                PagoVenta pago = new PagoVenta();
                pago.setMetodoPago(p.getMetodoPago() != null ? p.getMetodoPago() : p.getMetodoPago());
                pago.setMonto(p.getMonto() != null ? p.getMonto() : 0.0);
                pago.setVenta(venta);
                venta.getPagos().add(pago);
            }
            // opcional: para compatibilidad con campos antiguos, setear metodoPago principal
            venta.setMetodoPago(venta.getPagos().get(0).getMetodoPago());
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
