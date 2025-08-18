package com.analistas.stella.model.service;
import com.analistas.stella.model.domain.Venta;
import com.analistas.stella.model.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaServiceImpl implements IVentaService {

    @Autowired
    IVentaRepository ventaRepo;

    @Override
    public void guardar(Venta venta) {
        ventaRepo.save(venta);
    }

    @Override
    public List<Venta> listar() {
        return (List<Venta>) ventaRepo.findAll();
    }

    @Override
    public Venta buscarPorId(Long id) {
        return ventaRepo.findById(id).orElse(null);
    }

}
