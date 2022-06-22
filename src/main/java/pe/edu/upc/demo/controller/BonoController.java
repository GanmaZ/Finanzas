package pe.edu.upc.demo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pe.edu.upc.demo.entities.Bono;
import pe.edu.upc.demo.serviceinterface.IBonoService;

@Controller
@RequestMapping("/bonos")
public class BonoController {

	@Autowired
	private IBonoService bService;

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
		return "/usuario/inicio";
	}

	@RequestMapping("calcular")
	public String calcuBonos(Model model) {
		try {
			model.addAttribute("listaFlujos", bService.list());
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "/usuario/rescalculos";
	}

}
