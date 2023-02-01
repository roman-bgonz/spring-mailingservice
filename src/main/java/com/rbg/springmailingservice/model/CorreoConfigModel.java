package com.rbg.springmailingservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CorreoConfigModel {

	private String host;
	private int puerto;
	private String usuario;
	private String psd;
	private String protocolo;
	private boolean autenticacionRequerida;
}
