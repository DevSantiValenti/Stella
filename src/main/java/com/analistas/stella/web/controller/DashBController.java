package com.analistas.stella.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DashBController {

    @GetMapping("/")
    public String dashb(Model model) {

        // model.addAttribute("titulo", "Hola cabeza de poronga");

        return "productos-list";
    }
    

}
