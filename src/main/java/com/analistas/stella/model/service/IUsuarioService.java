package com.analistas.stella.model.service;

import java.util.List;
import com.analistas.stella.model.domain.Usuario;

public interface IUsuarioService {

<<<<<<< Updated upstream
    List<Usuario> buscarTodo();
    Usuario buscarPorId(long id);
    Usuario buscarPorCorreo(String correo);
    void guardar(Usuario usuario);
=======
    public List<Usuario> buscarTodo();
    public Usuario buscarPorId(Long id);
    public Usuario buscarPorCorreo(String correo);
    public void guardar(Usuario usuario);
    public void borrarPorId(Long id);
>>>>>>> Stashed changes
}
