package com.analistas.stella.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.analistas.stella.model.service.IOfertaService;


@Controller
public class HomeController {

    @Autowired
    IOfertaService ofertaService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("ofertas", ofertaService.buscarTodos());
        // model.addAttribute("titulo", "Hola cabeza de poronga");

        return "index";
    }
    

}
