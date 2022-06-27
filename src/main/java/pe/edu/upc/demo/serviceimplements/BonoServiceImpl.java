package pe.edu.upc.demo.serviceimplements;

import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.formula.functions.FinanceLib;
import org.apache.poi.ss.formula.functions.Irr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import pe.edu.upc.demo.entities.Bono;
import pe.edu.upc.demo.entities.Flujo;
import pe.edu.upc.demo.entities.Usuario;
import pe.edu.upc.demo.entities.Valoracion;
import pe.edu.upc.demo.repositories.IBonoRepository;
import pe.edu.upc.demo.repositories.IFlujoRepository;
import pe.edu.upc.demo.repositories.IUsuarioRepository;
import pe.edu.upc.demo.repositories.IValoracionRepository;
import pe.edu.upc.demo.serviceinterface.IBonoService;

@Service
public class BonoServiceImpl implements IBonoService {

	@Autowired
	private IBonoRepository bRepository;

	@Autowired
	private IUsuarioRepository userRepository;

	@Autowired
	private IFlujoRepository fRepository;

	@Autowired
	private IValoracionRepository vRepository;

	@Override
	public void insert(Bono bono) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		UserDetails userDetail = (UserDetails) auth.getPrincipal();

		Usuario usuario = userRepository.findByUserName(userDetail.getUsername());

		bono.setUsuario(usuario);
		bRepository.save(bono);

		if ("semestral".equals(bono.getPeriodoPago())) {

			float tasacuponsem = (bono.getTasaCupon() / 100) / 2;
			int periodsem = (int) bono.getVencimiento() * 2;
			int a = periodsem + 1;
			float flujo[][] = new float[5][a];
			double flucaja[] = new double[a];
			double fluvan[] = new double[periodsem];
			int aux = 0;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < a; j++) {
					if (i == 0) {
						flujo[i][j] = aux;
						aux++;
					} else if (i == 1) {
						flujo[i][j] = bono.getValorNominal();

						if (j == (a - 1)) {
							flujo[i][j] = 0;
						}

					} else if (i == 2 && j > 0 && j < (a - 1)) {
						flujo[i][j] = 0;

					} else if (i == 2 && j == (a - 1)) {
						flujo[i][j] = bono.getValorNominal();

					} else if (i == 3 && j > 0) {
						flujo[i][j] = bono.getValorNominal() * tasacuponsem;

					} else if (i == 4 && j == 0) {
						flujo[i][j] = -bono.getValorNominal();
						flucaja[j] = -bono.getValorNominal();
						fluvan[j] = bono.getValorNominal() * tasacuponsem;

					} else if (i == 4 && j > 0 && j < (a - 1)) {
						flujo[i][j] = bono.getValorNominal() * tasacuponsem;
						flucaja[j] = bono.getValorNominal() * tasacuponsem;
						fluvan[j - 1] = bono.getValorNominal() * tasacuponsem;

					} else if (i == 4 && j == (a - 1)) {
						flujo[i][j] = (bono.getValorNominal() * tasacuponsem) + bono.getValorNominal();
						flucaja[j] = (bono.getValorNominal() * tasacuponsem) + bono.getValorNominal();
						fluvan[j - 1] = (bono.getValorNominal() * tasacuponsem) + bono.getValorNominal();
					}

				}
			}

			for (int j = 0; j < a; j++) {

				Flujo flu = new Flujo();
				flu.setPeriodo((int) flujo[0][j]);
				flu.setCapital(flujo[1][j]);
				flu.setAmortizacion(flujo[2][j]);
				flu.setInteres(flujo[3][j]);
				flu.setCuota(flujo[4][j]);
				flu.setBono(bono);

				fRepository.save(flu);

			}

			Valoracion va = new Valoracion();
			double r = bono.getCostoOportunidad() / 100;
			double d = (FinanceLib.npv(r, fluvan)) - (bono.getValorNominal());
			double ir = Precision.round((Irr.irr(flucaja) * 100), 2);
			d = Precision.round(d, 2);

			va.setTir(ir);
			va.setBono(bono);
			va.setVan(d);
			vRepository.save(va);

		} else if ("mensual".equals(bono.getPeriodoPago())) {
			float tasacuponmen = (bono.getTasaCupon() / 100) / 12;
			int periodmen = (int) bono.getVencimiento() * 12;
			int a = periodmen + 1;
			float flujo[][] = new float[5][a];
			double flucaja[] = new double[a];
			double fluvan[] = new double[periodmen];
			int aux = 0;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < a; j++) {
					if (i == 0) {
						flujo[i][j] = aux;
						aux++;
					} else if (i == 1) {
						flujo[i][j] = bono.getValorNominal();

						if (j == (a - 1)) {
							flujo[i][j] = 0;
						}

					} else if (i == 2 && j > 0 && j < (a - 1)) {
						flujo[i][j] = 0;

					} else if (i == 2 && j == (a - 1)) {
						flujo[i][j] = bono.getValorNominal();

					} else if (i == 3 && j > 0) {
						flujo[i][j] = bono.getValorNominal() * tasacuponmen;

					} else if (i == 4 && j == 0) {
						flujo[i][j] = -bono.getValorNominal();
						flucaja[j] = -bono.getValorNominal();
						fluvan[j] = bono.getValorNominal() * tasacuponmen;

					} else if (i == 4 && j > 0 && j < (a - 1)) {
						flujo[i][j] = bono.getValorNominal() * tasacuponmen;
						flucaja[j] = bono.getValorNominal() * tasacuponmen;
						fluvan[j - 1] = bono.getValorNominal() * tasacuponmen;

					} else if (i == 4 && j == (a - 1)) {
						flujo[i][j] = (bono.getValorNominal() * tasacuponmen) + bono.getValorNominal();
						flucaja[j] = (bono.getValorNominal() * tasacuponmen) + bono.getValorNominal();
						fluvan[j - 1] = (bono.getValorNominal() * tasacuponmen) + bono.getValorNominal();
					}

				}
			}
			for (int j = 0; j < a; j++) {

				Flujo flu = new Flujo();
				flu.setPeriodo((int) flujo[0][j]);
				flu.setCapital(flujo[1][j]);
				flu.setAmortizacion(flujo[2][j]);
				flu.setInteres(flujo[3][j]);
				flu.setCuota(flujo[4][j]);
				flu.setBono(bono);

				fRepository.save(flu);

			}

			Valoracion va = new Valoracion();
			double r = bono.getCostoOportunidad() / 100;
			double d = (FinanceLib.npv(r, fluvan)) - (bono.getValorNominal());
			double ir = Precision.round((Irr.irr(flucaja) * 100), 2);
			d = Precision.round(d, 2);

			va.setTir(ir);
			va.setBono(bono);
			va.setVan(d);
			vRepository.save(va);

		} else {
			int a = (int) bono.getVencimiento() + 1;
			float flujo[][] = new float[5][a];
			double flucaja[] = new double[a];
			double fluvan[] = new double[(int) bono.getVencimiento()];
			int aux = 0;

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < a; j++) {
					if (i == 0) {
						flujo[i][j] = aux;
						aux++;
					} else if (i == 1) {
						flujo[i][j] = bono.getValorNominal();

						if (j == (bono.getVencimiento() - 1)) {
							flujo[i][j] = 0;
						}

					} else if (i == 2 && j > 0 && j < a - 1) {
						flujo[i][j] = 0;

					} else if (i == 2 && j == a - 1) {
						flujo[i][j] = bono.getValorNominal();

					} else if (i == 3 && j > 0) {
						flujo[i][j] = bono.getValorNominal() * (bono.getTasaCupon() / 100);

					} else if (i == 4 && j == 0) {
						flujo[i][j] = -bono.getValorNominal();
						flucaja[j] = -bono.getValorNominal();
						fluvan[j] = bono.getValorNominal() * (bono.getTasaCupon() / 100);

					} else if (i == 4 && j > 0 && j < a - 1) {
						flujo[i][j] = bono.getValorNominal() * (bono.getTasaCupon() / 100);
						flucaja[j] = bono.getValorNominal() * (bono.getTasaCupon() / 100);
						fluvan[j - 1] = bono.getValorNominal() * (bono.getTasaCupon() / 100);

					} else if (i == 4 && j == a - 1) {
						flujo[i][j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100)) + bono.getValorNominal();
						flucaja[j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100)) + bono.getValorNominal();
						fluvan[j - 1] = (bono.getValorNominal() * (bono.getTasaCupon() / 100)) + bono.getValorNominal();
					}

				}
			}
			for (int j = 0; j < a; j++) {

				Flujo flu = new Flujo();
				flu.setPeriodo((int) flujo[0][j]);
				flu.setCapital(flujo[1][j]);
				flu.setAmortizacion(flujo[2][j]);
				flu.setInteres(flujo[3][j]);
				flu.setCuota(flujo[4][j]);
				flu.setBono(bono);

				fRepository.save(flu);

			}
			Valoracion va = new Valoracion();
			double r = bono.getCostoOportunidad() / 100;
			double d = (FinanceLib.npv(r, fluvan)) - (bono.getValorNominal());
			double ir = Precision.round((Irr.irr(flucaja) * 100), 2);
			d = Precision.round(d, 2);

			va.setTir(ir);
			va.setBono(bono);
			va.setVan(d);
			vRepository.save(va);
		}

	}

	@Override
	public List<Bono> list() {
		return bRepository.findAll();
	}
}
