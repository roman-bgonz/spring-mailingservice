package com.rbg.springmailingservice.model.request;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.io.InputStreamSource;

import com.rbg.springmailingservice.model.EmisorModel;
import com.rbg.springmailingservice.model.ReceptorModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PropiedadesCorreoModel {

	private EmisorModel remitente;
	private ReceptorModel destinatario;
	private String asunto;
	private int prioridad;
	private Map<String, InputStreamSource> adjuntos;
	private String cuerpo;

	public PropiedadesCorreoModel() {
		this.remitente = new EmisorModel();
		this.destinatario = new ReceptorModel();
		this.asunto = "";
		this.prioridad = 0;
		this.adjuntos = Collections.emptyMap();
		this.cuerpo = "";
	}
}
