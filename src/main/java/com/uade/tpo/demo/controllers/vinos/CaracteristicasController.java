package com.uade.tpo.demo.controllers.vinos;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Azucar;
import com.uade.tpo.demo.entity.Cepa;
import com.uade.tpo.demo.entity.Crianza;
import com.uade.tpo.demo.entity.Elaboracion;
import com.uade.tpo.demo.entity.Medida;
import com.uade.tpo.demo.entity.Color;
import com.uade.tpo.demo.exceptions.BadRequestException;
import com.uade.tpo.demo.exceptions.ResourceNotFoundException;
import com.uade.tpo.demo.repository.AzucarRepository;
import com.uade.tpo.demo.repository.CepaRepository;
import com.uade.tpo.demo.repository.CrianzaRepository;
import com.uade.tpo.demo.repository.ElaboracionRepository;
import com.uade.tpo.demo.repository.MedidaRepository;
import com.uade.tpo.demo.repository.ColorRepository;

@RestController
@RequestMapping("/caracteristica")
@RequiredArgsConstructor
public class CaracteristicasController {

	private final AzucarRepository azucarRepository;
	private final ColorRepository colorRepository;
	private final CepaRepository cepaRepository;
	private final CrianzaRepository crianzaRepository;
	private final ElaboracionRepository elaboracionRepository;
	private final MedidaRepository medidaRepository;



	@GetMapping("/azucares")
	public List<Azucar> listarAzucares() {
		return azucarRepository.findAll();
	}

	@GetMapping("/azucares/{id}")
	public Azucar obtenerAzucar(@PathVariable Long id) {
		return azucarRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Azucar no encontrado: " + id));
	}

	@PostMapping("/azucares")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Azucar crearAzucar(@RequestBody CatalogoCreateRequest request) {
		String name = requireName(request);
		Optional<Azucar> existing = azucarRepository.findByName(name);
		if (existing.isPresent()) {
			throw new BadRequestException("Azucar ya existe: " + name);
		}
		try {
			return azucarRepository.save(Azucar.builder().name(name).build());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("Azucar ya existe: " + name);
		}
	}

	// -----------------------------
	// COLOR
	// -----------------------------
	@GetMapping("/colores")
	public List<Color> listarColores() {
		return colorRepository.findAll();
	}

	@GetMapping("/colores/{id}")
	public Color obtenerColor(@PathVariable Long id) {
		return colorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Color no encontrado: " + id));
	}

	@PostMapping("/colores")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Color crearColor(@RequestBody CatalogoCreateRequest request) {
		String name = requireName(request);
		Optional<Color> existing = colorRepository.findByName(name);
		if (existing.isPresent()) {
			throw new BadRequestException("Color ya existe: " + name);
		}
		try {
			return colorRepository.save(Color.builder().name(name).build());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("Color ya existe: " + name);
		}
	}

	// -----------------------------
	// CEPA
	// -----------------------------
	@GetMapping("/cepas")
	public List<Cepa> listarCepas() {
		return cepaRepository.findAll();
	}

	@GetMapping("/cepas/{id}")
	public Cepa obtenerCepa(@PathVariable Long id) {
		return cepaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cepa no encontrada: " + id));
	}

	@PostMapping("/cepas")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Cepa crearCepa(@RequestBody CatalogoCreateRequest request) {
		String name = requireName(request);
		Optional<Cepa> existing = cepaRepository.findByName(name);
		if (existing.isPresent()) {
			throw new BadRequestException("Cepa ya existe: " + name);
		}
		try {
			return cepaRepository.save(Cepa.builder().name(name).build());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("Cepa ya existe: " + name);
		}
	}

	// -----------------------------
	// CRIANZA
	// -----------------------------
	@GetMapping("/crianzas")
	public List<Crianza> listarCrianzas() {
		return crianzaRepository.findAll();
	}

	@GetMapping("/crianzas/{id}")
	public Crianza obtenerCrianza(@PathVariable Long id) {
		return crianzaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Crianza no encontrada: " + id));
	}

	@PostMapping("/crianzas")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Crianza crearCrianza(@RequestBody CatalogoCreateRequest request) {
		String name = requireName(request);
		Optional<Crianza> existing = crianzaRepository.findByName(name);
		if (existing.isPresent()) {
			throw new BadRequestException("Crianza ya existe: " + name);
		}
		try {
			return crianzaRepository.save(Crianza.builder().name(name).build());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("Crianza ya existe: " + name);
		}
	}

	// -----------------------------
	// ELABORACION
	// -----------------------------
	@GetMapping("/elaboraciones")
	public List<Elaboracion> listarElaboraciones() {
		return elaboracionRepository.findAll();
	}

	@GetMapping("/elaboraciones/{id}")
	public Elaboracion obtenerElaboracion(@PathVariable Long id) {
		return elaboracionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Elaboracion no encontrada: " + id));
	}

	@PostMapping("/elaboraciones")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Elaboracion crearElaboracion(@RequestBody CatalogoCreateRequest request) {
		String name = requireName(request);
		Optional<Elaboracion> existing = elaboracionRepository.findByName(name);
		if (existing.isPresent()) {
			throw new BadRequestException("Elaboracion ya existe: " + name);
		}
		try {
			return elaboracionRepository.save(Elaboracion.builder().name(name).build());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("Elaboracion ya existe: " + name);
		}
	}

	// -----------------------------
	// MEDIDA
	// -----------------------------
	@GetMapping("/medidas")
	public List<Medida> listarMedidas() {
		return medidaRepository.findAll();
	}

	@GetMapping("/medidas/{id}")
	public Medida obtenerMedida(@PathVariable Long id) {
		return medidaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Medida no encontrada: " + id));
	}

	@PostMapping("/medidas")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public Medida crearMedida(@RequestBody CatalogoCreateRequest request) {
		String name = requireName(request);
		Optional<Medida> existing = medidaRepository.findByName(name);
		if (existing.isPresent()) {
			throw new BadRequestException("Medida ya existe: " + name);
		}
		try {
			return medidaRepository.save(Medida.builder().name(name).build());
		} catch (DataIntegrityViolationException e) {
			throw new BadRequestException("Medida ya existe: " + name);
		}
	}

	// -----------------------------
	// TOTAL
	// -----------------------------
	@GetMapping("/total")
	public CaracteristicasTotalResponse total() {
		return new CaracteristicasTotalResponse(
				azucarRepository.findAll(),
				colorRepository.findAll(),
				cepaRepository.findAll(),
				crianzaRepository.findAll(),
				elaboracionRepository.findAll(),
				medidaRepository.findAll());
	}

	private static String requireName(CatalogoCreateRequest request) {
		if (request == null || !StringUtils.hasText(request.getName())) {
			throw new BadRequestException("name es obligatorio");
		}
		return request.getName().trim();
	}
}
