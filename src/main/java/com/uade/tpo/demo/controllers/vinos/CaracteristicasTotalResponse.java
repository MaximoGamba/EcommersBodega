package com.uade.tpo.demo.controllers.vinos;

import java.util.List;

import com.uade.tpo.demo.entity.Azucar;
import com.uade.tpo.demo.entity.Cepa;
import com.uade.tpo.demo.entity.Color;
import com.uade.tpo.demo.entity.Crianza;
import com.uade.tpo.demo.entity.Elaboracion;
import com.uade.tpo.demo.entity.Medida;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaracteristicasTotalResponse {
    private List<Azucar> azucares;
    private List<Color> colores;
    private List<Cepa> cepas;
    private List<Crianza> crianzas;
    private List<Elaboracion> elaboraciones;
    private List<Medida> medidas;
}

