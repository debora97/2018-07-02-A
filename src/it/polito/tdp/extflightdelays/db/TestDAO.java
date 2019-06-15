package it.polito.tdp.extflightdelays.db;

import it.polito.tdp.extflightdelays.model.Model;

public class TestDAO {

	public static void main(String[] args) {

	    ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		Model model= new Model();
		model.getAllAp();
		//dao.loadAllAirports();

		System.out.println(dao.loadAllAirlines().size());
		System.out.println(dao.loadAllAirports().size());
	    System.out.println(dao.loadAllFlights().size());
	    //System.out.println(model.mapTuttiAp);
		System.out.println(dao.loadAllAirportsMigliaSelezionate(400, model.mapTuttiAp));
	}

}
