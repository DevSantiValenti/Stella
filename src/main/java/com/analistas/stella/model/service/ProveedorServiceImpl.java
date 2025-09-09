package com.analistas.stella.model.service;

import com.analistas.stella.model.domain.Proveedor;
import com.analistas.stella.model.repository.IProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service

public class ProveedorServiceImpl implements IProveedorService{
    @Autowired
    private IProveedorRepository repo;

    @Override
    public List<Proveedor> listar() {
        return (List<Proveedor>) repo.findAll();
    }

     @Override
    public void guardar(Proveedor proveedor) {
        repo.save(proveedor);
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Proveedor buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

}
