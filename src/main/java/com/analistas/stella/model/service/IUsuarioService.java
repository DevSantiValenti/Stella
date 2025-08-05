package com.analistas.stella.model.service;

import java.util.List;
import com.analistas.stella.model.domain.Usuario;

public interface IUsuarioService {

    List<Usuario> buscarTodo();
    Usuario buscarPorId(long id);
    Usuario buscarPorCorreo(String correo);
    void guardar(Usuario usuario);
}
