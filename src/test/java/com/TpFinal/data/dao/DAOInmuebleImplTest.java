package com.TpFinal.data.dao;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.TpFinal.data.conexion.ConexionHibernate;
import com.TpFinal.data.conexion.TipoConexion;
import com.TpFinal.data.dto.EstadoRegistro;
import com.TpFinal.data.dto.inmueble.ClaseInmueble;
import com.TpFinal.data.dto.inmueble.Coordenada;
import com.TpFinal.data.dto.inmueble.CriterioBusquedaInmuebleDTO;
import com.TpFinal.data.dto.inmueble.Direccion;
import com.TpFinal.data.dto.inmueble.EstadoInmueble;
import com.TpFinal.data.dto.inmueble.Inmueble;
import com.TpFinal.data.dto.inmueble.TipoInmueble;
import com.TpFinal.data.dto.inmueble.TipoMoneda;
import com.TpFinal.data.dto.persona.Persona;
import com.TpFinal.data.dto.persona.Propietario;
import com.TpFinal.data.dto.publicacion.Publicacion;
import com.TpFinal.data.dto.publicacion.PublicacionAlquiler;
import com.TpFinal.data.dto.publicacion.PublicacionVenta;
import com.TpFinal.data.dto.publicacion.Rol;
import com.TpFinal.data.dto.publicacion.TipoPublicacion;
import com.TpFinal.services.ProvinciaService;
import com.TpFinal.utils.GeneradorDeDatos;

public class DAOInmuebleImplTest {
    DAOInmuebleImpl daoInmueble;
    DAOPublicacionImpl daoPublicacion;
    List<Inmueble> inmuebles = new ArrayList<>();
    private CriterioBusquedaInmuebleDTO criterio;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	ConexionHibernate.setTipoConexion(TipoConexion.H2Test);
    }

    @Before
    public void setUp() throws Exception {
	daoInmueble = new DAOInmuebleImpl();
	daoPublicacion = new DAOPublicacionImpl();
	inmuebles.clear();
	criterio = null;

    }

    @After
    public void tearDown() throws Exception {
	inmuebles = daoInmueble.readAll();
	inmuebles.forEach(daoInmueble::delete);
    }

    @Test
    public void testCreate() {
	List<Inmueble> inmueblesAGuardar = unoNoPublicado_unoEnAlquiler_unoEnVenta();
	inmueblesAGuardar.forEach(daoInmueble::create);
	List<Inmueble> inmueblesEnBD = daoInmueble.readAll();
	inmueblesEnBD.forEach(i -> assertEquals(i, inmueblesAGuardar.get(inmueblesEnBD.indexOf(i))));
    }

    @Test
    public void testReadAll() {
	int cantidadDeInmuebles = 3;
	Stream.iterate(0, x -> x++).limit(cantidadDeInmuebles).forEach(x -> daoInmueble.create(unInmuebleNoPublicado()));
	inmuebles = daoInmueble.readAll();
	assertEquals(cantidadDeInmuebles, inmuebles.size());
    }

    @Test
    public void testUpdate() {
	Inmueble original = unInmuebleNoPublicado();
	daoInmueble.create(original);
	Inmueble modificado = daoInmueble.readAll().get(0);
	modificado.setCantidadAmbientes(10);
	modificado.getDireccion().setCoordenada(new Coordenada(12.0, 10.2));
	daoInmueble.update(modificado);
	assertTrue(modificado.getCantidadAmbientes().equals(daoInmueble.findById(modificado.getId()).getCantidadAmbientes()));
	assertTrue(modificado.getDireccion().getCoordenada()
		.equals(daoInmueble.findById(modificado.getId()).getDireccion().getCoordenada()));
    }

    @Test
    public void findInmueblesbyEstado() {
	int cantidadDeInmuebles = 3;
	Stream.iterate(0, x -> x++).limit(cantidadDeInmuebles).forEach(x -> daoInmueble.create(unInmuebleNoPublicado()));
	inmuebles = daoInmueble.findInmueblesbyEstado(EstadoInmueble.NoPublicado);
	assertEquals(cantidadDeInmuebles, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_AEstrenar() {
	Inmueble inmuebleAEstrenar = unInmuebleNoPublicado();
	inmuebleAEstrenar.setaEstrenar(true);
	Inmueble inmuebleEstrenado = unInmuebleNoPublicado();
	inmuebleEstrenado.setaEstrenar(false);

	daoInmueble.create(inmuebleAEstrenar);
	daoInmueble.create(inmuebleEstrenado);

	inmuebles = daoInmueble.readAll();

	assertTrue(inmuebles.get(0).getaEstrenar() == true);
	assertTrue(inmuebles.get(1).getaEstrenar() == false);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setaEstrenar(true).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getaEstrenar() == true);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setaEstrenar(false).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getaEstrenar() == false);

    }

    @Test
    public void findInmueblesByCriteria_Ciudad() {
	int cantidadDeInmuebles = 3;
	Stream.iterate(0, x -> x++).limit(cantidadDeInmuebles).forEach(x -> daoInmueble.create(unInmuebleNoPublicado()));

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setCiudad("una Localidad").build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(cantidadDeInmuebles, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setCiudad("otra localidad").build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(0, inmuebles.size());

    }

    @Test
    public void findInmueblesByCriteria_AireAcondicionado() {
	Inmueble inmuebleConAA = unInmuebleNoPublicado();
	inmuebleConAA.setConAireAcondicionado(true);
	Inmueble inmuebleSinAA = unInmuebleNoPublicado();
	inmuebleSinAA.setConAireAcondicionado(false);

	daoInmueble.create(inmuebleConAA);
	daoInmueble.create(inmuebleSinAA);

	inmuebles = daoInmueble.readAll();

	assertTrue(inmuebles.get(0).getConAireAcondicionado() == true);
	assertTrue(inmuebles.get(1).getConAireAcondicionado() == false);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConAireAcondicionado(true).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConAireAcondicionado() == true);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConAireAcondicionado(false).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConAireAcondicionado() == false);
    }

    @Test
    public void findInmueblesByCriteria_Estado() {
	int cantidadDeInmuebles = 3;
	Stream.iterate(0, x -> x++).limit(cantidadDeInmuebles).forEach(x -> daoInmueble.create(unInmuebleNoPublicado()));

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setEstadoInmueble(EstadoInmueble.NoPublicado).build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(cantidadDeInmuebles, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setEstadoInmueble(EstadoInmueble.Alquilado).build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(0, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_ClaseInmuebleCochera() {
	crearInmueblesEnVentaEnPesosConValorCuota100xN(3);
	Inmueble i = daoInmueble.readAll().get(0);
	i.setClaseInmueble(ClaseInmueble.Cochera);
	daoInmueble.save(i);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setClasesDeInmueble(Arrays.asList(ClaseInmueble.Cochera))
		.build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setClasesDeInmueble(Arrays.asList(ClaseInmueble.Ph))
		.build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_ClaseInmuebleCocheraOrPh() {
	crearInmueblesEnVentaEnPesosConValorCuota100xN(3);
	Inmueble i = daoInmueble.readAll().get(0);
	i.setClaseInmueble(ClaseInmueble.Cochera);
	daoInmueble.save(i);

	criterio = new CriterioBusquedaInmuebleDTO.Builder()
		.setClasesDeInmueble(Arrays.asList(ClaseInmueble.Cochera, ClaseInmueble.Ph)).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(3, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setClasesDeInmueble(Arrays.asList(ClaseInmueble.Ph))
		.build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_CiudadAndAireAcondicionado() {
	int cantidadDeInmuebles = 3;

	for (int x = 0; x < cantidadDeInmuebles; x++) {
	    Inmueble i = unInmuebleNoPublicado();
	    if (x % 2 == 0) {
		i.setConAireAcondicionado(false);
		i.setDireccion(new Direccion.Builder().setLocalidad("otra localidad").build());
	    }
	    daoInmueble.create(i);

	}

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setCiudad("una Localidad").setConAireAcondicionado(true)
		.build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setCiudad("otra localidad").setConAireAcondicionado(false)
		.build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());

    }

    @Test
    public void findInmueblesByCriteria_Jardin() {
	Inmueble inmuebleConJardin = unInmuebleNoPublicado();
	inmuebleConJardin.setConJardin(true);
	Inmueble inmuebleSinJardin = unInmuebleNoPublicado();
	inmuebleSinJardin.setConJardin(false);

	daoInmueble.create(inmuebleConJardin);
	daoInmueble.create(inmuebleSinJardin);

	inmuebles = daoInmueble.readAll();

	assertTrue(inmuebles.get(0).getConJardin() == true);
	assertTrue(inmuebles.get(1).getConJardin() == false);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConJardin(true).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConJardin() == true);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConJardin(false).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConJardin() == false);
    }

    @Test
    public void findInmueblesByCriteria_Parrilla() {
	Inmueble inmuebleCon = unInmuebleNoPublicado();
	inmuebleCon.setConParilla(true);
	Inmueble inmuebleSin = unInmuebleNoPublicado();
	inmuebleSin.setConParilla(false);

	daoInmueble.create(inmuebleCon);
	daoInmueble.create(inmuebleSin);

	inmuebles = daoInmueble.readAll();

	assertTrue(inmuebles.get(0).getConParilla() == true);
	assertTrue(inmuebles.get(1).getConParilla() == false);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConParrilla(true).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConParilla() == true);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConParrilla(false).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConParilla() == false);
    }

    @Test
    public void findInmueblesByCriteria_Pileta() {
	Inmueble inmuebleCon = unInmuebleNoPublicado();
	inmuebleCon.setConPileta(true);
	Inmueble inmuebleSin = unInmuebleNoPublicado();
	inmuebleSin.setConPileta(false);
	daoInmueble.create(inmuebleCon);
	daoInmueble.create(inmuebleSin);

	inmuebles = daoInmueble.readAll();

	assertTrue(inmuebles.get(0).getConPileta() == true);
	assertTrue(inmuebles.get(1).getConPileta() == false);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConPileta(true).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConPileta() == true);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setConPileta(false).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);

	assertEquals(1, inmuebles.size());
	assertTrue(inmuebles.get(0).getConPileta() == false);
    }

    @Test
    public void findInmueblesByCriteria_EstadoInmueble() {

	Inmueble inmuebleAlquilado = unInmuebleNoPublicado();
	inmuebleAlquilado.setEstadoInmueble(EstadoInmueble.Alquilado);

	Inmueble inmuebleEnAlquiler = unInmuebleNoPublicado();
	inmuebleEnAlquiler.setEstadoInmueble(EstadoInmueble.EnAlquiler);

	Inmueble inmuebleEnVenta = unInmuebleNoPublicado();
	inmuebleEnVenta.setEstadoInmueble(EstadoInmueble.EnVenta);

	Inmueble inmuebleNoPublicado = unInmuebleNoPublicado();
	inmuebleNoPublicado.setEstadoInmueble(EstadoInmueble.NoPublicado);

	Inmueble inmuebleVendido = unInmuebleNoPublicado();
	inmuebleVendido.setEstadoInmueble(EstadoInmueble.Vendido);

	daoInmueble.create(inmuebleAlquilado);
	daoInmueble.create(inmuebleEnAlquiler);
	daoInmueble.create(inmuebleEnVenta);
	daoInmueble.create(inmuebleNoPublicado);
	daoInmueble.create(inmuebleVendido);

	inmuebles = daoInmueble.readAll();
	EstadoInmueble[] estados = EstadoInmueble.values();

	assertEquals(inmuebles.size(), estados.length);

	for (int i = 0; i < inmuebles.size(); i++) {
	    assertEquals(inmuebles.get(i).getEstadoInmueble(), estados[i]);
	}

	for (int i = 0; i < estados.length; i++) {
	    criterio = new CriterioBusquedaInmuebleDTO.Builder().setEstadoInmueble(estados[i]).build();
	    inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	    assertEquals(1, inmuebles.size());
	    assertEquals(inmuebles.get(0).getEstadoInmueble(), estados[i]);
	}

    }

    @Test
    public void findInmueblesByCriteria_SupTotal() {
	int cantidadDeInmuebles = 3;

	for (int x = 1; x <= cantidadDeInmuebles; x++) {
	    Inmueble i = unInmuebleNoPublicado();
	    i.setSuperficieTotal(x * 100);
	    daoInmueble.create(i);

	}

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinSupTotal(200).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxSupTotal(250).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxSupTotal(300).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(3, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinSupTotal(100).setMaxSupTotal(300).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(3, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinSupTotal(400).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(0, inmuebles.size());

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxSupTotal(99).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(0, inmuebles.size());

    }

    @Test
    public void findInmueblesByCriteria_InmueblesAlquilados() {
	unoNoPublicado_unoEnAlquiler_unoEnVenta().forEach(daoInmueble::create);
	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Alquiler).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());

    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnVenta() {
	unoNoPublicado_unoEnAlquiler_unoEnVenta().forEach(daoInmueble::create);
	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Venta).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());

    }

    @Test
    public void findInmueblesByCriteria_TodosLosInmuebles() {
	unoNoPublicado_unoEnAlquiler_unoEnVenta().forEach(daoInmueble::create);
	criterio = new CriterioBusquedaInmuebleDTO.Builder().build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(3, inmuebles.size());

    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnAlquilerAndValorCuotaMayorIgualA200() {
	crearInmueblesEnAlquilerEnDolaresConValorCuota100xN(3);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Alquiler)
		.setMinPrecio(BigDecimal.valueOf(200)).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnAlquilerAndValorCuotaMenorIgualA100() {

	crearInmueblesEnAlquilerEnDolaresConValorCuota100xN(3);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Alquiler)
		.setMaxPrecio(BigDecimal.valueOf(100)).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnVentaAndValorCuotaMayorIgualA200() {
	crearInmueblesEnVentaEnPesosConValorCuota100xN(3);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Venta)
		.setMinPrecio(BigDecimal.valueOf(200)).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnVentaAndValorCuotaMenorIgualA100() {

	crearInmueblesEnVentaEnPesosConValorCuota100xN(3);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Venta)
		.setMaxPrecio(BigDecimal.valueOf(100)).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnVentaAndValorCuotaMayorIgualA200AndClaseCochera() {
	crearInmueblesEnVentaEnPesosConValorCuota100xN(3);
	Inmueble i = daoInmueble.readAll().get(0);
	i.setClaseInmueble(ClaseInmueble.Cochera);
	daoInmueble.save(i);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Venta)
		.setClasesDeInmueble(Arrays.asList(ClaseInmueble.Cochera)).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnVentaAndValorCuotaMayorIgualA200AndEsCocheraAndSinPileta() {
	crearInmueblesEnVentaEnPesosConValorCuota100xN(3);
	Inmueble i = daoInmueble.readAll().get(0);
	i.setClaseInmueble(ClaseInmueble.Cochera);
	i.setConPileta(false);
	daoInmueble.save(i);

	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoPublicacion(TipoPublicacion.Venta)
		.setClasesDeInmueble(Arrays.asList(ClaseInmueble.Cochera)).setConPileta(false).build();
	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(1, inmuebles.size());
    }

    @Test
    public void findInmueblesByCriteria_InmueblesEnDolares() {
	crearInmueblesEnAlquilerEnDolaresConValorCuota100xN(2);
	criterio = new CriterioBusquedaInmuebleDTO.Builder().setTipoMoneda(TipoMoneda.Dolares).build();

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(2, inmuebles.size());

	crearInmueblesEnVentaEnPesosConValorCuota100xN(1);
	crearInmueblesEnAlquilerEnDolaresConValorCuota100xN(4);

	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
	assertEquals(6, inmuebles.size());

    }
    
    @Test
    public void findInmueblesByCriteria_minCantAmbientes() {
    	Inmueble inmueble = unInmuebleCon5Ambientes();
    	Inmueble otroInmueble = unInmuebleCon8Ambientes();
    	Inmueble otroInmueble2 = unInmuebleEnVentaEnPesos();
    	
    	daoInmueble.create(inmueble);
    	daoInmueble.create(otroInmueble);
    	daoInmueble.create(otroInmueble2);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinCantAmbientes(5).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	assertEquals(2,inmuebles.size());
    }
    
    @Test
    public void findInmueblesByCriteria_maxCantAmbientes() {
    	Inmueble inmueble = unInmuebleCon5Ambientes();
    	Inmueble otroInmueble = unInmuebleCon8Ambientes();
    	Inmueble otroInmueble2 = unInmuebleEnVentaEnPesos();
    	
    	daoInmueble.create(inmueble);
    	daoInmueble.create(otroInmueble);
    	daoInmueble.create(otroInmueble2);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxCantAmbientes(4).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	assertEquals(1,inmuebles.size());
    }
    
    @Test
    public void findInmueblesByCriteria_minCantCocheras() {
    	Inmueble inmueble1 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble4 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble5 = unInmuebleEnVentaEnPesos();
    	
    	inmueble1.setCantidadCocheras(4);
    	inmueble2.setCantidadCocheras(4);
    	inmueble3.setCantidadCocheras(5);
    	
    	daoInmueble.create(inmueble1);
    	daoInmueble.create(inmueble2);
    	daoInmueble.create(inmueble3);
    	daoInmueble.create(inmueble4);
    	daoInmueble.create(inmueble5);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinCantCocheras(4).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(3, inmuebles.size());
    	
    	
    	inmueble1.setCantidadCocheras(1);
    	inmueble2.setCantidadCocheras(3);
    	inmueble3.setCantidadCocheras(4);
    	inmueble4.setCantidadCocheras(5);
    	inmueble5.setCantidadCocheras(10);
    	
    	daoInmueble.saveOrUpdate(inmueble1);
    	daoInmueble.saveOrUpdate(inmueble2);
    	daoInmueble.saveOrUpdate(inmueble3);
    	daoInmueble.saveOrUpdate(inmueble4);
    	daoInmueble.saveOrUpdate(inmueble5);
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(3, inmuebles.size());
    	
    }
    
    @Test
    public void findInmueblesByCriteria_maxCantCocheras() {
    	Inmueble inmueble1 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble4 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble5 = unInmuebleEnVentaEnPesos();
    	
    	inmueble1.setCantidadCocheras(3);
    	inmueble2.setCantidadCocheras(4);
    	inmueble3.setCantidadCocheras(5);
    	
    	daoInmueble.create(inmueble1);
    	daoInmueble.create(inmueble2);
    	daoInmueble.create(inmueble3);
    	daoInmueble.create(inmueble4);
    	daoInmueble.create(inmueble5);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxCantCocheras(4).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(4, inmuebles.size());
    	
    	
    	inmueble1.setCantidadCocheras(1);
    	inmueble2.setCantidadCocheras(3);
    	inmueble3.setCantidadCocheras(4);
    	inmueble4.setCantidadCocheras(5);
    	inmueble5.setCantidadCocheras(10);
    	
    	daoInmueble.saveOrUpdate(inmueble1);
    	daoInmueble.saveOrUpdate(inmueble2);
    	daoInmueble.saveOrUpdate(inmueble3);
    	daoInmueble.saveOrUpdate(inmueble4);
    	daoInmueble.saveOrUpdate(inmueble5);
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(3, inmuebles.size());
    	
    }

    @Test
    public void findInmueblesByCriteria_minCantDormitorios() {
    	Inmueble inmueble1 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble4 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble5 = unInmuebleEnVentaEnPesos();
    	
    	inmueble1.setCantidadDormitorios(2);
    	inmueble2.setCantidadDormitorios(3);
    	inmueble3.setCantidadDormitorios(4);
    	
    	daoInmueble.create(inmueble1);
    	daoInmueble.create(inmueble2);
    	daoInmueble.create(inmueble3);
    	daoInmueble.create(inmueble4);
    	daoInmueble.create(inmueble5);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinCantDormitorios(3).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(2, inmuebles.size());
    	
    	
    	inmueble1.setCantidadDormitorios(1);
    	inmueble2.setCantidadDormitorios(3);
    	inmueble3.setCantidadDormitorios(4);
    	inmueble4.setCantidadDormitorios(5);
    	inmueble5.setCantidadDormitorios(10);
    	
    	daoInmueble.saveOrUpdate(inmueble1);
    	daoInmueble.saveOrUpdate(inmueble2);
    	daoInmueble.saveOrUpdate(inmueble3);
    	daoInmueble.saveOrUpdate(inmueble4);
    	daoInmueble.saveOrUpdate(inmueble5);
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(4, inmuebles.size());
    	
    }
    
    @Test
    public void findInmueblesByCriteria_maxCantDormitorios() {
    	Inmueble inmueble1 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble4 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble5 = unInmuebleEnVentaEnPesos();
    	
    	inmueble1.setCantidadDormitorios(2);
    	inmueble2.setCantidadDormitorios(3);
    	inmueble3.setCantidadDormitorios(4);
    	
    	daoInmueble.create(inmueble1);
    	daoInmueble.create(inmueble2);
    	daoInmueble.create(inmueble3);
    	daoInmueble.create(inmueble4);
    	daoInmueble.create(inmueble5);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxCantDormitorios(3).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(4, inmuebles.size());
    	
    	
    	inmueble1.setCantidadDormitorios(1);
    	inmueble2.setCantidadDormitorios(3);
    	inmueble3.setCantidadDormitorios(4);
    	inmueble4.setCantidadDormitorios(5);
    	inmueble5.setCantidadDormitorios(10);
    	
    	daoInmueble.saveOrUpdate(inmueble1);
    	daoInmueble.saveOrUpdate(inmueble2);
    	daoInmueble.saveOrUpdate(inmueble3);
    	daoInmueble.saveOrUpdate(inmueble4);
    	daoInmueble.saveOrUpdate(inmueble5);
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(2, inmuebles.size());
    	
    }
    
    @Test
    public void sonIguales() {
    	Inmueble inmueble =  unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 =  unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 =  unInmuebleEnVentaEnPesos();
    	
    	assertTrue(inmueble.equals(inmueble2));
    	assertTrue(inmueble2.equals(inmueble3));
    	
    	inmueble2.setCantidadAmbientes(10);
    	inmueble3.setCantidadCocheras(11);
    	
    	assertFalse(inmueble.equals(inmueble2));
    	assertFalse(inmueble.equals(inmueble3));
    }
    
    @Test
    public void findInmueblesByCriteria_minCSupCubierta() {
    	Inmueble inmueble1 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble4 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble5 = unInmuebleEnVentaEnPesos();
    	
    	inmueble1.setSuperficieCubierta(299);
    	inmueble2.setSuperficieCubierta(300);
    	inmueble3.setSuperficieCubierta(301);
    	
    	daoInmueble.create(inmueble1);
    	daoInmueble.create(inmueble2);
    	daoInmueble.create(inmueble3);
    	daoInmueble.create(inmueble4);
    	daoInmueble.create(inmueble5);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMinSupCubierta(300).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(2, inmuebles.size());
    	
    	
    	inmueble1.setSuperficieCubierta(400);
    	inmueble2.setSuperficieCubierta(355);
    	inmueble3.setSuperficieCubierta(300);
    	inmueble4.setSuperficieCubierta(299);
    	inmueble5.setSuperficieCubierta(100);
    	
    	daoInmueble.saveOrUpdate(inmueble1);
    	daoInmueble.saveOrUpdate(inmueble2);
    	daoInmueble.saveOrUpdate(inmueble3);
    	daoInmueble.saveOrUpdate(inmueble4);
    	daoInmueble.saveOrUpdate(inmueble5);
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(3, inmuebles.size());
    	
    }
    
    @Test
    public void findInmueblesByCriteria_maxCSupCubierta() {
    	Inmueble inmueble1 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble2 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble3 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble4 = unInmuebleEnVentaEnPesos();
    	Inmueble inmueble5 = unInmuebleEnVentaEnPesos();
    	
    	inmueble1.setSuperficieCubierta(301);
    	inmueble2.setSuperficieCubierta(300);
    	inmueble3.setSuperficieCubierta(299);
    	
    	daoInmueble.create(inmueble1);
    	daoInmueble.create(inmueble2);
    	daoInmueble.create(inmueble3);
    	daoInmueble.create(inmueble4);
    	daoInmueble.create(inmueble5);
    	
    	criterio = new CriterioBusquedaInmuebleDTO.Builder().setMaxSupCubierta(300).build();
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(4, inmuebles.size());
    	
    	
    	inmueble1.setSuperficieCubierta(400);
    	inmueble2.setSuperficieCubierta(355);
    	inmueble3.setSuperficieCubierta(300);
    	inmueble4.setSuperficieCubierta(299);
    	inmueble5.setSuperficieCubierta(400);
    	
    	daoInmueble.saveOrUpdate(inmueble1);
    	daoInmueble.saveOrUpdate(inmueble2);
    	daoInmueble.saveOrUpdate(inmueble3);
    	daoInmueble.saveOrUpdate(inmueble4);
    	daoInmueble.saveOrUpdate(inmueble5);
    	
    	inmuebles = daoInmueble.findInmueblesbyCaracteristicas(criterio);
    	
    	assertEquals(2, inmuebles.size());
    	
    }
    
    @Test
    public void testPublicaciones() {
	Inmueble i = unInmuebleNoPublicado();
	i.setEstadoInmueble(EstadoInmueble.EnVenta);
	Publicacion p = new PublicacionVenta.Builder()
		.setFechaPublicacion(LocalDate.now())
		.setPrecio(BigDecimal.valueOf(200))
		.build();
	
	//daoInmueble.create(i);
	//daoPublicacion.create(p);
	p.setInmueble(i);
	i.addPublicacion(p);
	//daoPublicacion.saveOrUpdate(p);
	//daoInmueble.saveOrUpdate(i);
	daoInmueble.merge(i);
	
	inmuebles = daoInmueble.readAll();
	assertEquals(1, inmuebles.get(0).getPublicaciones().size());
    }
    
    @Test
    public void testRelacionPropietarios() {
	Inmueble i = unInmuebleNoPublicado();
	daoInmueble.saveOrUpdate(i);
	Persona p = new Persona.Builder().setApellido("ape")
		.setDNI("123")
		.setinfoAdicional("Info adicional")
		.setMail("a@b.com")
		.setNombre("nom")
		.setTelefono("123456")
		.setTelefono2("321")
		.buid();
	Propietario propietario = new Propietario.Builder()
		.setEstadoRegistro(EstadoRegistro.ACTIVO)
		.setPersona(p)
		.build();
	propietario.setPersona(p);
	p.addRol(propietario);
	
	DAOPersonaImpl daop = new DAOPersonaImpl();
//	Debe existir primero una persona en la bd a la que asignarle el rol de propietario.
	daop.saveOrUpdate(p);
	i.setPropietario(propietario);
	propietario.addInmueble(i);
	
	daoInmueble.saveOrUpdate(i);
	inmuebles = daoInmueble.readAll();
	assertEquals(propietario,i.getPropietario());
	
	
	((Propietario)p.getRol(Rol.Propietario)).getInmuebles().remove(i);
	p.removeRol(propietario);
	daop.save(p);
	i.setPropietario(null);
	daoInmueble.save(i);
    }
    
    //@Test
    public void testGeneradorDeDatos() {
	GeneradorDeDatos.generarDatos(10,ProvinciaService.modoLecturaJson.local);
	inmuebles=daoInmueble.readAll();
	assertEquals(10, inmuebles.size());
	DAOPersonaImpl daop = new DAOPersonaImpl();
	for(Inmueble i : inmuebles) {
	    Persona p = i.getPropietario().getPersona();
	    i.getPropietario().setPersona(null);
	    daop.saveOrUpdate(p);
	}
	
    }

    private void crearInmueblesEnAlquilerEnDolaresConValorCuota100xN(int cantidadDeInmuebles) {
	for (int x = 1; x <= cantidadDeInmuebles; x++) {
	    Inmueble i = unInmuebleEnAlquilerEnDolares();
	    for (Publicacion o : i.getPublicaciones()) {
		if (o instanceof PublicacionAlquiler) {
		    PublicacionAlquiler oa = ((PublicacionAlquiler) o);
		    oa.setPrecio(BigDecimal.valueOf(100 * x));
		}
	    }
	    daoInmueble.create(i);
	}
    }

    private void crearInmueblesEnVentaEnPesosConValorCuota100xN(int cantidadDeInmuebles) {
	for (int x = 1; x <= cantidadDeInmuebles; x++) {
	    Inmueble i = unInmuebleEnVentaEnPesos();
	    for (Publicacion o : i.getPublicaciones()) {
		if (o instanceof PublicacionVenta) {
		    PublicacionVenta ov = ((PublicacionVenta) o);
		    ov.setPrecio(BigDecimal.valueOf(100 * x));
		}
	    }
	    daoInmueble.create(i);
	}
    }

    private List<Inmueble> unoNoPublicado_unoEnAlquiler_unoEnVenta() {
	List<Inmueble> inmuebles = new ArrayList<>();
	inmuebles.add(unInmuebleNoPublicado());
	inmuebles.add(unInmuebleEnVentaEnPesos());
	inmuebles.add(unInmuebleEnAlquilerEnDolares());
	return inmuebles;
    }

    private Inmueble unInmuebleNoPublicado() {
	return new Inmueble.Builder()
		.setaEstrenar(true)
		.setCantidadAmbientes(2)
		.setCantidadCocheras(3)
		.setCantidadDormitorios(1)
		.setClaseInmueble(ClaseInmueble.Casa)
		.setConAireAcondicionado(true)
		.setConJardin(true).setConParilla(true).setConPileta(true)
		.setDireccion(
			new Direccion.Builder()
				.setCalle("Una calle")
				.setCodPostal("asd123")
				.setCoordenada(new Coordenada())
				.setLocalidad("una Localidad")
				.setNro(123)
				.setPais("Argentina")
				.setProvincia("Buenos Aires")
				.build())
		.setEstadoInmueble(EstadoInmueble.NoPublicado)
		.setSuperficieCubierta(200)
		.setSuperficieTotal(400)
		.setTipoInmueble(TipoInmueble.Vivienda)
		.build();
    }

    private Inmueble unInmuebleEnVentaEnPesos() {
	Inmueble inmueble = new Inmueble.Builder()
		.setaEstrenar(true)
		.setCantidadAmbientes(2)
		.setCantidadCocheras(3)
		.setCantidadDormitorios(1)
		.setClaseInmueble(ClaseInmueble.Ph)
		.setConAireAcondicionado(true)
		.setConJardin(true)
		.setConParilla(true)
		.setConPileta(true)
		.setDireccion(new Direccion.Builder()
			.setCalle("Una calle")
			.setCodPostal("asd123")
			.setCoordenada(new Coordenada())
			.setLocalidad("una Localidad")
			.setNro(123)
			.setPais("Argentina")
			.setProvincia("Buenos Aires")
			.build())
		.setEstadoInmueble(EstadoInmueble.EnVenta)
		.setSuperficieCubierta(200)
		.setSuperficieTotal(400)
		.setTipoInmueble(TipoInmueble.Vivienda)
		.build();
	inmueble.addPublicacion(new PublicacionVenta.Builder()
		.setFechaPublicacion(LocalDate.of(2017, 10, 1))
		.setMoneda(TipoMoneda.Pesos)
		.setPrecio(BigDecimal.valueOf(12e3))
		.setInmueble(inmueble)
		.build());
	return inmueble;
    }

    private Inmueble unInmuebleEnAlquilerEnDolares() {
	Inmueble inmueble = new Inmueble.Builder()
		.setaEstrenar(true)
		.setCantidadAmbientes(2)
		.setCantidadCocheras(3)
		.setCantidadDormitorios(1)
		.setClaseInmueble(ClaseInmueble.Consultorio)
		.setConAireAcondicionado(true)
		.setConJardin(true)
		.setConParilla(true)
		.setConPileta(true)
		.setDireccion(new Direccion.Builder()
			.setCalle("Una calle")
			.setCodPostal("asd123")
			.setCoordenada(new Coordenada())
			.setLocalidad("una Localidad")
			.setNro(123)
			.setPais("Argentina")
			.setProvincia("Buenos Aires")
			.build())
		.setEstadoInmueble(EstadoInmueble.EnAlquiler)
		.setSuperficieCubierta(200)
		.setSuperficieTotal(400)
		.setTipoInmueble(TipoInmueble.Comercial)
		.build();
	inmueble.addPublicacion(new PublicacionAlquiler.Builder()
		.setFechaPublicacion(LocalDate.of(2017, 9, 1))
		.setMoneda(TipoMoneda.Dolares)
		.setValorCuota(BigDecimal.valueOf(1e3))
		.setInmueble(inmueble)
		.build());
	return inmueble;
    }
    
    private Inmueble unInmuebleCon5Ambientes() {
    	Inmueble inmueble = new Inmueble.Builder()
    		.setaEstrenar(true)
    		.setCantidadAmbientes(5)
    		.setCantidadCocheras(3)
    		.setCantidadDormitorios(1)
    		.setClaseInmueble(ClaseInmueble.Consultorio)
    		.setConAireAcondicionado(true)
    		.setConJardin(true)
    		.setConParilla(true)
    		.setConPileta(true)
    		.setDireccion(new Direccion.Builder()
    			.setCalle("Una calle")
    			.setCodPostal("asd123")
    			.setCoordenada(new Coordenada())
    			.setLocalidad("una Localidad")
    			.setNro(123)
    			.setPais("Argentina")
    			.setProvincia("Buenos Aires")
    			.build())
    		.setEstadoInmueble(EstadoInmueble.EnAlquiler)
    		.setSuperficieCubierta(200)
    		.setSuperficieTotal(400)
    		.setTipoInmueble(TipoInmueble.Comercial)
    		.build();
    	inmueble.addPublicacion(new PublicacionAlquiler.Builder()
    		.setFechaPublicacion(LocalDate.of(2017, 9, 1))
    		.setMoneda(TipoMoneda.Dolares)
    		.setValorCuota(BigDecimal.valueOf(1e3))
    		.setInmueble(inmueble)
    		.build());
    	return inmueble;
        }
    
    private Inmueble unInmuebleCon8Ambientes() {
    	Inmueble inmueble = new Inmueble.Builder()
    		.setaEstrenar(true)
    		.setCantidadAmbientes(5)
    		.setCantidadCocheras(3)
    		.setCantidadDormitorios(1)
    		.setClaseInmueble(ClaseInmueble.Consultorio)
    		.setConAireAcondicionado(true)
    		.setConJardin(true)
    		.setConParilla(true)
    		.setConPileta(true)
    		.setDireccion(new Direccion.Builder()
    			.setCalle("Una calle")
    			.setCodPostal("asd123")
    			.setCoordenada(new Coordenada())
    			.setLocalidad("una Localidad")
    			.setNro(123)
    			.setPais("Argentina")
    			.setProvincia("Buenos Aires")
    			.build())
    		.setEstadoInmueble(EstadoInmueble.EnAlquiler)
    		.setSuperficieCubierta(200)
    		.setSuperficieTotal(400)
    		.setTipoInmueble(TipoInmueble.Comercial)
    		.build();
    	inmueble.addPublicacion(new PublicacionAlquiler.Builder()
    		.setFechaPublicacion(LocalDate.of(2017, 9, 1))
    		.setMoneda(TipoMoneda.Dolares)
    		.setValorCuota(BigDecimal.valueOf(1e3))
    		.setInmueble(inmueble)
    		.build());
    	return inmueble;
        }



}
