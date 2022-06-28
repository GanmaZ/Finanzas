package pe.edu.upc.demo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.upc.demo.entities.Bono;
import pe.edu.upc.demo.repositories.IBonoRepository;
import pe.edu.upc.demo.repositories.IFlujoRepository;
import pe.edu.upc.demo.repositories.IIndicadorRepository;
import pe.edu.upc.demo.repositories.IValoracionRepository;
import pe.edu.upc.demo.serviceinterface.IBonoService;

@Controller
@RequestMapping("/bonos")
public class BonoController {

	@Autowired
	private IBonoService bService;

	@Autowired
	private IFlujoRepository fRepository;

	@Autowired
	private IBonoRepository bRepository;

	@Autowired
	private IValoracionRepository vRepository;

	@Autowired
	private IIndicadorRepository iRepository;

	private Bono bono;

	@GetMapping("/nuevo")
	public String NewBono(Model model) {

		model.addAttribute("bono", new Bono());
		return "/usuario/valoracion";
	}

	@PostMapping("/guardar")
	public String saveBono(@Valid Bono objbono, BindingResult binRes) {

		if (binRes.hasErrors()) {
			return "/usuario/valoracion";
		} else {
			bono = objbono;
			bService.insert(objbono);
			return "redirect:/bonos/calcular";
		}
	}

	@GetMapping("/listar")
	public String listBonos(Model model) {
		try {
			model.addAttribute("listaBonos", bService.list());
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "/usuario/historial";
	}

	@GetMapping("/detalles/{id}")
	public String listdetalles(@PathVariable int id, Model model) {
		try {
			model.addAttribute("listabono", bRepository.findByBono(id));
			model.addAttribute("listaFlujos", fRepository.findByBono(id));
			model.addAttribute("listaValoracion", vRepository.findByValoracion(id));
			model.addAttribute("listaIndicador", iRepository.findByBono(id));
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "/usuario/listdetalles";
	}

	@RequestMapping("calcular")
	public String calcuBonos(Model model) {
		try {
			model.addAttribute("listabono", bRepository.findByBono(bono.getIdBono()));
			model.addAttribute("listaFlujos", fRepository.findByBono(bono.getIdBono()));
			model.addAttribute("listaValoracion", vRepository.findByValoracion(bono.getIdBono()));
			model.addAttribute("listaIndicador", iRepository.findByBono(bono.getIdBono()));
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "/usuario/rescalculos";
	}

	@RequestMapping("/eliminar/{id}")
	public String delete(@PathVariable int id, Model model) {
		try {
			fRepository.deleteByBono(id);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		try {
			iRepository.deleteByBono(id);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		try {
			vRepository.deleteByBono(id);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		try {
			bRepository.deleteById(id);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}

		return "redirect:/bonos/listar";
	}

}
