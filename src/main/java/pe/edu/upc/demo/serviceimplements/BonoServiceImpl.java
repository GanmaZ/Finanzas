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
import pe.edu.upc.demo.entities.Indicador;
import pe.edu.upc.demo.entities.Usuario;
import pe.edu.upc.demo.entities.Valoracion;
import pe.edu.upc.demo.repositories.IBonoRepository;
import pe.edu.upc.demo.repositories.IFlujoRepository;
import pe.edu.upc.demo.repositories.IIndicadorRepository;
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

	@Autowired
	private IIndicadorRepository iRepository;

	@Override
	public void insert(Bono bono) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		UserDetails userDetail = (UserDetails) auth.getPrincipal();

		Usuario usuario = userRepository.findByUserName(userDetail.getUsername());

		bono.setUsuario(usuario);
		bRepository.save(bono);

		if ("nominal".equals(bono.getTipoTasa())) {
			double m = 1 + ((bono.getTasaCupon() / 100) / 360);
			double n = (Math.pow(m, 360) - 1)*100;
			double tefectiva = Precision.round(n, 2);
			bono.setTasaCupon((float) tefectiva);

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

				Indicador indi = new Indicador();
				double flujoindi[][] = new double[5][fluvan.length];
				int t = 1;

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < fluvan.length; j++) {
						if (i == 0) {
							flujoindi[i][j] = t;
							t++;

						} else if (i == 1) {
							flujoindi[i][j] = fluvan[j];

						} else if (i == 2) {
							double x = flujoindi[1][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double vp = Precision.round(x / z, 2);

							flujoindi[i][j] = vp;

						} else if (i == 3) {
							double x = flujoindi[2][j] * flujoindi[0][j];
							double vpt = Precision.round(x, 2);

							flujoindi[i][j] = vpt;

						} else {
							double w = flujoindi[2][j];
							double x = Math.pow(flujoindi[0][j], 2) + flujoindi[0][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double resul = (w * x) / z;
							double convex = Precision.round(resul, 2);

							flujoindi[i][j] = convex;
						}

					}
				}

				double sumavp = 0;
				double sumavpt = 0;
				double sumaconvex = 0;

				for (int j = 0; j < fluvan.length; j++) {
					sumavp += flujoindi[2][j];
					sumavpt += flujoindi[3][j];
					sumaconvex += flujoindi[4][j];
				}

				double preciobono = Precision.round(sumavp, 2);

				double duracianual = (sumavpt / preciobono) / 2;
				double duracion = Precision.round(duracianual, 2);

				double duracionanualmod = duracion / (1 + (bono.getTasaNegociacion() / 100));
				double duracionmodificada = Precision.round(duracionanualmod, 2);

				double p = Math.pow((1 + (bono.getTasaNegociacion() / 100)), 2);
				double convex = (1 / (preciobono * p)) * sumaconvex;
				double convexidad = Precision.round(convex, 2);

				indi.setPrecio(preciobono);
				indi.setDuracion(duracion);
				indi.setDuracionModificada(duracionmodificada);
				indi.setConvexidad(convexidad);
				indi.setBono(bono);

				iRepository.save(indi);

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

				Indicador indi = new Indicador();
				double flujoindi[][] = new double[5][fluvan.length];
				int t = 1;

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < fluvan.length; j++) {
						if (i == 0) {
							flujoindi[i][j] = t;
							t++;

						} else if (i == 1) {
							flujoindi[i][j] = fluvan[j];

						} else if (i == 2) {
							double x = flujoindi[1][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double vp = Precision.round(x / z, 2);

							flujoindi[i][j] = vp;

						} else if (i == 3) {
							double x = flujoindi[2][j] * flujoindi[0][j];
							double vpt = Precision.round(x, 2);

							flujoindi[i][j] = vpt;

						} else {
							double w = flujoindi[2][j];
							double x = Math.pow(flujoindi[0][j], 2) + flujoindi[0][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double resul = (w * x) / z;
							double convex = Precision.round(resul, 2);

							flujoindi[i][j] = convex;
						}

					}
				}

				double sumavp = 0;
				double sumavpt = 0;
				double sumaconvex = 0;

				for (int j = 0; j < fluvan.length; j++) {
					sumavp += flujoindi[2][j];
					sumavpt += flujoindi[3][j];
					sumaconvex += flujoindi[4][j];
				}

				double preciobono = Precision.round(sumavp, 2);

				double duracianual = (sumavpt / preciobono) / 12;
				double duracion = Precision.round(duracianual, 2);

				double duracionanualmod = duracion / (1 + (bono.getTasaNegociacion() / 100));
				double duracionmodificada = Precision.round(duracionanualmod, 2);

				double p = Math.pow((1 + (bono.getTasaNegociacion() / 100)), 2);
				double convex = (1 / (preciobono * p)) * sumaconvex;
				double convexidad = Precision.round(convex, 2);

				indi.setPrecio(preciobono);
				indi.setDuracion(duracion);
				indi.setDuracionModificada(duracionmodificada);
				indi.setConvexidad(convexidad);
				indi.setBono(bono);

				iRepository.save(indi);

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

							if (j == (a - 1)) {
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
							flujo[i][j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100))
									+ bono.getValorNominal();
							flucaja[j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100))
									+ bono.getValorNominal();
							fluvan[j - 1] = (bono.getValorNominal() * (bono.getTasaCupon() / 100))
									+ bono.getValorNominal();
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

				Indicador indi = new Indicador();
				double flujoindi[][] = new double[5][fluvan.length];
				int t = 1;

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < fluvan.length; j++) {
						if (i == 0) {
							flujoindi[i][j] = t;
							t++;

						} else if (i == 1) {
							flujoindi[i][j] = fluvan[j];

						} else if (i == 2) {
							double x = flujoindi[1][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double vp = Precision.round(x / z, 2);

							flujoindi[i][j] = vp;

						} else if (i == 3) {
							double x = flujoindi[2][j] * flujoindi[0][j];
							double vpt = Precision.round(x, 2);

							flujoindi[i][j] = vpt;

						} else {
							double w = flujoindi[2][j];
							double x = Math.pow(flujoindi[0][j], 2) + flujoindi[0][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double resul = (w * x) / z;
							double convex = Precision.round(resul, 2);

							flujoindi[i][j] = convex;
						}

					}
				}

				double sumavp = 0;
				double sumavpt = 0;
				double sumaconvex = 0;

				for (int j = 0; j < fluvan.length; j++) {
					sumavp += flujoindi[2][j];
					sumavpt += flujoindi[3][j];
					sumaconvex += flujoindi[4][j];
				}

				double preciobono = Precision.round(sumavp, 2);

				double duracianual = sumavpt / preciobono;
				double duracion = Precision.round(duracianual, 2);

				double duracionanualmod = duracion / (1 + (bono.getTasaNegociacion() / 100));
				double duracionmodificada = Precision.round(duracionanualmod, 2);

				double p = Math.pow((1 + (bono.getTasaNegociacion() / 100)), 2);
				double convex = (1 / (preciobono * p)) * sumaconvex;
				double convexidad = Precision.round(convex, 2);

				indi.setPrecio(preciobono);
				indi.setDuracion(duracion);
				indi.setDuracionModificada(duracionmodificada);
				indi.setConvexidad(convexidad);
				indi.setBono(bono);

				iRepository.save(indi);
			}

		} else {

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

				Indicador indi = new Indicador();
				double flujoindi[][] = new double[5][fluvan.length];
				int t = 1;

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < fluvan.length; j++) {
						if (i == 0) {
							flujoindi[i][j] = t;
							t++;

						} else if (i == 1) {
							flujoindi[i][j] = fluvan[j];

						} else if (i == 2) {
							double x = flujoindi[1][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double vp = Precision.round(x / z, 2);

							flujoindi[i][j] = vp;

						} else if (i == 3) {
							double x = flujoindi[2][j] * flujoindi[0][j];
							double vpt = Precision.round(x, 2);

							flujoindi[i][j] = vpt;

						} else {
							double w = flujoindi[2][j];
							double x = Math.pow(flujoindi[0][j], 2) + flujoindi[0][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double resul = (w * x) / z;
							double convex = Precision.round(resul, 2);

							flujoindi[i][j] = convex;
						}

					}
				}

				double sumavp = 0;
				double sumavpt = 0;
				double sumaconvex = 0;

				for (int j = 0; j < fluvan.length; j++) {
					sumavp += flujoindi[2][j];
					sumavpt += flujoindi[3][j];
					sumaconvex += flujoindi[4][j];
				}

				double preciobono = Precision.round(sumavp, 2);

				double duracianual = (sumavpt / preciobono) / 2;
				double duracion = Precision.round(duracianual, 2);

				double duracionanualmod = duracion / (1 + (bono.getTasaNegociacion() / 100));
				double duracionmodificada = Precision.round(duracionanualmod, 2);

				double p = Math.pow((1 + (bono.getTasaNegociacion() / 100)), 2);
				double convex = (1 / (preciobono * p)) * sumaconvex;
				double convexidad = Precision.round(convex, 2);

				indi.setPrecio(preciobono);
				indi.setDuracion(duracion);
				indi.setDuracionModificada(duracionmodificada);
				indi.setConvexidad(convexidad);
				indi.setBono(bono);

				iRepository.save(indi);

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

				Indicador indi = new Indicador();
				double flujoindi[][] = new double[5][fluvan.length];
				int t = 1;

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < fluvan.length; j++) {
						if (i == 0) {
							flujoindi[i][j] = t;
							t++;

						} else if (i == 1) {
							flujoindi[i][j] = fluvan[j];

						} else if (i == 2) {
							double x = flujoindi[1][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double vp = Precision.round(x / z, 2);

							flujoindi[i][j] = vp;

						} else if (i == 3) {
							double x = flujoindi[2][j] * flujoindi[0][j];
							double vpt = Precision.round(x, 2);

							flujoindi[i][j] = vpt;

						} else {
							double w = flujoindi[2][j];
							double x = Math.pow(flujoindi[0][j], 2) + flujoindi[0][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double resul = (w * x) / z;
							double convex = Precision.round(resul, 2);

							flujoindi[i][j] = convex;
						}

					}
				}

				double sumavp = 0;
				double sumavpt = 0;
				double sumaconvex = 0;

				for (int j = 0; j < fluvan.length; j++) {
					sumavp += flujoindi[2][j];
					sumavpt += flujoindi[3][j];
					sumaconvex += flujoindi[4][j];
				}

				double preciobono = Precision.round(sumavp, 2);

				double duracianual = (sumavpt / preciobono) / 12;
				double duracion = Precision.round(duracianual, 2);

				double duracionanualmod = duracion / (1 + (bono.getTasaNegociacion() / 100));
				double duracionmodificada = Precision.round(duracionanualmod, 2);

				double p = Math.pow((1 + (bono.getTasaNegociacion() / 100)), 2);
				double convex = (1 / (preciobono * p)) * sumaconvex;
				double convexidad = Precision.round(convex, 2);

				indi.setPrecio(preciobono);
				indi.setDuracion(duracion);
				indi.setDuracionModificada(duracionmodificada);
				indi.setConvexidad(convexidad);
				indi.setBono(bono);

				iRepository.save(indi);

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

							if (j == (a - 1)) {
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
							flujo[i][j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100))
									+ bono.getValorNominal();
							flucaja[j] = (bono.getValorNominal() * (bono.getTasaCupon() / 100))
									+ bono.getValorNominal();
							fluvan[j - 1] = (bono.getValorNominal() * (bono.getTasaCupon() / 100))
									+ bono.getValorNominal();
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

				Indicador indi = new Indicador();
				double flujoindi[][] = new double[5][fluvan.length];
				int t = 1;

				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < fluvan.length; j++) {
						if (i == 0) {
							flujoindi[i][j] = t;
							t++;

						} else if (i == 1) {
							flujoindi[i][j] = fluvan[j];

						} else if (i == 2) {
							double x = flujoindi[1][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double vp = Precision.round(x / z, 2);

							flujoindi[i][j] = vp;

						} else if (i == 3) {
							double x = flujoindi[2][j] * flujoindi[0][j];
							double vpt = Precision.round(x, 2);

							flujoindi[i][j] = vpt;

						} else {
							double w = flujoindi[2][j];
							double x = Math.pow(flujoindi[0][j], 2) + flujoindi[0][j];
							double y = 1 + (bono.getTasaNegociacion() / 100);
							double z = Math.pow(y, flujoindi[0][j]);
							double resul = (w * x) / z;
							double convex = Precision.round(resul, 2);

							flujoindi[i][j] = convex;
						}

					}
				}

				double sumavp = 0;
				double sumavpt = 0;
				double sumaconvex = 0;

				for (int j = 0; j < fluvan.length; j++) {
					sumavp += flujoindi[2][j];
					sumavpt += flujoindi[3][j];
					sumaconvex += flujoindi[4][j];
				}

				double preciobono = Precision.round(sumavp, 2);

				double duracianual = sumavpt / preciobono;
				double duracion = Precision.round(duracianual, 2);

				double duracionanualmod = duracion / (1 + (bono.getTasaNegociacion() / 100));
				double duracionmodificada = Precision.round(duracionanualmod, 2);

				double p = Math.pow((1 + (bono.getTasaNegociacion() / 100)), 2);
				double convex = (1 / (preciobono * p)) * sumaconvex;
				double convexidad = Precision.round(convex, 2);

				indi.setPrecio(preciobono);
				indi.setDuracion(duracion);
				indi.setDuracionModificada(duracionmodificada);
				indi.setConvexidad(convexidad);
				indi.setBono(bono);

				iRepository.save(indi);
			}
		}
	}

	@Override
	public List<Bono> list() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		UserDetails userDetail = (UserDetails) auth.getPrincipal();

		Usuario usuario = userRepository.findByUserName(userDetail.getUsername());
		
		
		return bRepository.findByid_usuario(usuario.getIdUsuario());
	}
}
