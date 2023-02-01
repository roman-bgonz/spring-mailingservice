package com.rbg.springmailingservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstatusEnvioModel {

	private boolean enviado;
	private Exception excepcion;
}
