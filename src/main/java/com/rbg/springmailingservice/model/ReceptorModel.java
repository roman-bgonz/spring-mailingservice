package com.rbg.springmailingservice.model;

import javax.mail.internet.InternetAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceptorModel {
	private InternetAddress correo;
	private boolean enviarCorreoCopiaOculta;
	private boolean enviarCorreoCopia;
	private InternetAddress[] correosCC;
}
