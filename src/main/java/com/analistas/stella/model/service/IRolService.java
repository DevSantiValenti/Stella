package com.analistas.stella.model.service;

import java.util.List;
import com.analistas.stella.model.domain.Rol;

public interface IRolService  {

    public List<Rol> buscarTodos();
    public Rol buscarPorId(Long id);
    public Rol buscarPorNombre(String nombre);
}
