package com.analistas.stella.model.service;

import java.util.List;
import com.analistas.stella.model.domain.Usuario;

public interface IUsuarioService {

    public List<Usuario> buscarTodo();
    public Usuario buscarPorId(Long id);
    public Usuario buscarPorCorreo(String correo);
    public void guardar(Usuario usuario);
    public void borrarPorId(Long id);
    public Usuario findByNombrecompleto(String nombreCompleto);
}
