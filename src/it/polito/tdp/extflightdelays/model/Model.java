package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;
import java.util.Comparator;
public class Model {
	private List<Airport> listaTuttiAp;
	public Map<Integer, Airport> mapTuttiAp;
	private ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
	private List<Airport> listaApMiglia;
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private List<Rotta> rotte;
	private List<Airport> best;
	double numeroMassimo;
	
	public  Model() {
		listaTuttiAp= new LinkedList<Airport>();
		mapTuttiAp= new HashMap<Integer, Airport>();
		grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		rotte= new LinkedList<Rotta>();
	}
	
	
	public List<Airport> getAllAp(){
		listaTuttiAp= dao.loadAllAirports();
		for(Airport a: listaTuttiAp) {
			mapTuttiAp.put(a.getId(), a);
		}
		
		
		return listaTuttiAp;
	}
	
	
	public List<Airport> getApMiglia(int miglia){
		listaApMiglia= new LinkedList<Airport>();
		listaApMiglia= dao.loadAllAirportsMigliaSelezionate(miglia, this.mapTuttiAp);
		this.rotte= dao.liadRotte(miglia, this.mapTuttiAp);
		return listaApMiglia;
	}
	
	
	public void creaGrafo() {
		Graphs.addAllVertices(grafo, this.listaApMiglia);
		
		for(Rotta r : this.rotte) {
			Airport a1= r.getA1();
			Airport a2= r.getA2();
			if( !this.listaApMiglia.contains(a2)  ) {
				grafo.addVertex(a2);
			}
			
			DefaultWeightedEdge e= grafo.getEdge(a1, a2);
			if(e==null) {
				Graphs.addEdge(grafo, a1, a2, r.getMedia());
			}else {
				double p1=r.getMedia();
				double p2=grafo.getEdgeWeight(e);
				double media= (p1+p2)/2;
				grafo.setEdgeWeight(e, media);
			}
			
		
	}
	System.out.println("GRAFO CREATO CON "+grafo.vertexSet().size()+ " vertitci e " +grafo.edgeSet().size()+" archi");
	}
	
	
	public List<Airport> getVertici(){
		List<Airport> lista = new LinkedList<Airport> (this.grafo.vertexSet());
		return lista;
	}


	public List<Airport> getVicini(Airport a) {
		List<Airport> vicini= new LinkedList<Airport>(Graphs.neighborListOf(grafo, a));
		Collections.sort(vicini, new Comparator<Airport>() {

			@Override
			public int compare(Airport o1, Airport o2) {
				DefaultWeightedEdge e1= grafo.getEdge(a, o1);
				DefaultWeightedEdge e2= grafo.getEdge(a, o2);
				Double peso1=grafo.getEdgeWeight(e1);
				Double peso2= grafo.getEdgeWeight(e2);
				
				return peso1.compareTo(peso2);
			}
			
		});
		return vicini;
	}
	
	
	public List<Airport> trovaPercorso(Airport partenza, double maxMiglia)
	{
		best= new ArrayList <Airport>();
		numeroMassimo=0;
		List<Airport> parziale= new LinkedList <Airport>();
		parziale.add(partenza);
		cerca(parziale, maxMiglia);
		return best;		
	}


	private void cerca(List<Airport> parziale, double maxMiglia) {

		List<Airport> vicini= this.getVicini(parziale.get(parziale.size()-1));
		
		if(parziale.size()>numeroMassimo) {
			
			this.best= new ArrayList<>(parziale);
			numeroMassimo=parziale.size();
			
		}
		for (Airport a: vicini) {
			DefaultWeightedEdge edge= grafo.getEdge(a, parziale.get(parziale.size()-1));
			double peso= grafo.getEdgeWeight(edge);
			if(this.getMiglia(parziale)+peso<=maxMiglia) {
				if(!parziale.contains(a)) {
					parziale.add(a);
					this.cerca(parziale, maxMiglia);
					parziale.remove(parziale.size()-1);
				}
			}
				
		}
	}


	private double getMiglia(List<Airport> parziale) {
		double nmiglia=0;
		for(int i=0; i<parziale.size()-1;i++) {
			DefaultWeightedEdge edge= grafo.getEdge(parziale.get(i), parziale.get(i+1));
			double peso= grafo.getEdgeWeight(edge);
			nmiglia+=peso;
			
			
			
		}
			
		return nmiglia;
	}

}