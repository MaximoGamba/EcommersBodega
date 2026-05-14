package com.uade.tpo.demo.controllers.catalogo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Azucar;
import com.uade.tpo.demo.entity.Cepa;
import com.uade.tpo.demo.entity.Color;
import com.uade.tpo.demo.entity.Crianza;
import com.uade.tpo.demo.entity.Elaboracion;
import com.uade.tpo.demo.entity.Medida;
import com.uade.tpo.demo.repository.AzucarRepository;
import com.uade.tpo.demo.repository.CepaRepository;
import com.uade.tpo.demo.repository.ColorRepository;
import com.uade.tpo.demo.repository.CrianzaRepository;
import com.uade.tpo.demo.repository.ElaboracionRepository;
import com.uade.tpo.demo.repository.MedidaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CatalogoController {

    private final ColorRepository colorRepository;
    private final CepaRepository cepaRepository;
    private final AzucarRepository azucarRepository;
    private final CrianzaRepository crianzaRepository;
    private final ElaboracionRepository elaboracionRepository;
    private final MedidaRepository medidaRepository;

    @GetMapping("/colores")
    public List<Color> listarColores() {
        return colorRepository.findAll();
    }

    @GetMapping("/cepas")
    public List<Cepa> listarCepas() {
        return cepaRepository.findAll();
    }

    @GetMapping("/azucares")
    public List<Azucar> listarAzucares() {
        return azucarRepository.findAll();
    }

    @GetMapping("/crianzas")
    public List<Crianza> listarCrianzas() {
        return crianzaRepository.findAll();
    }

    @GetMapping("/elaboraciones")
    public List<Elaboracion> listarElaboraciones() {
        return elaboracionRepository.findAll();
    }

    @GetMapping("/medidas")
    public List<Medida> listarMedidas() {
        return medidaRepository.findAll();
    }
}
