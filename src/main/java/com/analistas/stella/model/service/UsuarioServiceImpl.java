package com.analistas.stella.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.repository.IUsuarioRepository;

public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> buscarTodo(){
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public void borrarPorId(Long id) {
        usuarioRepository.deleteById(id);;
    }

    
}
