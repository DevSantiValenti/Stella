package com.analistas.stella.model.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.stella.config.SecurityUtils;
import com.analistas.stella.model.domain.Caja;
import com.analistas.stella.model.domain.CajaFisica;
import com.analistas.stella.model.domain.Usuario;
import com.analistas.stella.model.repository.ICajaFisicaRepository;
import com.analistas.stella.model.repository.ICajaRepository;

@Service
public class CajaServiceImpl implements ICajaService {

    @Autowired
    private ICajaRepository cajaRepository;

    // @Autowired
    // private IVentaRepository ventaRepository;

    @Autowired
    IVentaService ventaService;

    @Autowired
    ICajaFisicaRepository cajaFisicaRepository;

    @Autowired
    IUsuarioService usuarioService;

    public Caja abrirCaja(BigDecimal montoInicial, Long cajaFisicaId) {

        // Buscar la caja fisica:
        CajaFisica cajaFisica = cajaFisicaRepository.findById(cajaFisicaId)
                .orElseThrow(() -> new IllegalArgumentException("Caja física no encontrada"));

        if (cajaRepository.findByCajaFisicaAndAbiertaTrue(cajaFisica).isPresent()) {
            throw new IllegalStateException("Ya existe una caja abierta");
        }

        String username = SecurityUtils.getCurrentUsername();
        Usuario usuario2 = usuarioService.findByNombrecompleto(username);

        Caja caja = new Caja();
        caja.setApertura(LocalDateTime.now());
        caja.setMontoInicial(montoInicial);
        caja.setUsuario(usuario2);
        caja.setAbierta(true);
        // 🔗 Relacionar la sesión con la caja física
        caja.setCajaFisica(cajaFisica);

        return cajaRepository.save(caja);
    }

    public Caja cerrarCaja(Long cajaId, BigDecimal montoDeclarado, String comentarioCierre) {
        Caja caja = cajaRepository.findByIdAndAbiertaTrue(cajaId)
                .orElseThrow(() -> new IllegalStateException("La caja no está abierta o no existe"));

        caja.setCierre(LocalDateTime.now());
        
        // Calcular por todos los montos
        // BigDecimal totalVentas = ventaRepository.calcularTotalPorCaja(caja.getId());

        // Calcular solo dinero en EF
        BigDecimal totalEfectivo = cajaRepository.calcularTotalPorMetodo(caja, "EFECTIVO");

        caja.setMontoFinal(caja.getMontoInicial().add(totalEfectivo));
        caja.setAbierta(false);
        caja.setMontoDeclarado(montoDeclarado);
        caja.setComentarioCierre(comentarioCierre);

        // Calcular diferencia
        BigDecimal diferencia = caja.getMontoFinal().subtract(caja.getMontoDeclarado());
        diferencia = diferencia.negate();
        caja.setDiferencia(diferencia);

        return cajaRepository.save(caja);
    }

    public List<Caja> listarCajas() {
        return (List<Caja>) cajaRepository.findAll();
    }

    @Override
    public void guardar(Caja caja) {
        cajaRepository.save(caja);
    }

    @Override
    public Caja buscarPorId(Long id) {
        return cajaRepository.findById(id).orElse(null);
    }

    @Override
    public BigDecimal calcularTotalPorMetodo(Caja caja, String metodoPago) {
        return cajaRepository.calcularTotalPorMetodo(caja, metodoPago);
    }
}
