package com.analistas.stella.web.controller;

import com.analistas.stella.model.domain.Compra;
import com.analistas.stella.model.domain.CompraDTO;
import com.analistas.stella.model.service.ICompraService;
import com.analistas.stella.model.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private ICompraService compraService;

    @Autowired
    private IProductoService productoService;

    @GetMapping("/nuevo")
    public String nuevaCompra(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("productos", productoService.buscarTodo());
        return "compras/compras-form";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardar(@RequestBody CompraDTO compraDTO) {
        Compra saved = compraService.guardarDesdeDTO(compraDTO);
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", saved.getId());
        resp.put("numero", saved.getId());
        return ResponseEntity.ok(resp);
    }
}