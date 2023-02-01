package com.demo;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.rbg.springmailingservice.model.CorreoConfigModel;
import com.rbg.springmailingservice.model.EmisorModel;
import com.rbg.springmailingservice.model.ReceptorModel;
import com.rbg.springmailingservice.model.request.PropiedadesCorreoModel;
import freemarker.template.Configuration;
/**
 * EncuestasService.
 */
@Service
public class SendEmail {

	/** Configuration. */
	@Autowired
	private Configuration configuration;

	/** Logo which will be attached to the template */
	@Value("classpath:/templates/images/logo-baz.png")
	private Resource resourceLogo;

	/** Image to be attached in template */
	@Value("classpath:/templates/images/pregunta.png")
	private Resource resourcePregunta;

	/**
	 * Send demo surveys via email.
	 *
	 * @return Object to know if survey was delivered
	 */
	public Object enviaCorreosEncuestaDemo() {

		String host = "0.0.0.1"; // Your mail server IP"
		int port = 25; // Your mail server port
		boolean isAuthRequired = false; // If it is false, user and password values could be null
		String usr = null; // User to login in server
		String psd = null; // Psd to login in server
		String protocol = "smtp";

		// Initializes server configuration
		EmailService emailService = new EmailService(
				new CorreoConfigModel(host, port, usr, psd, protocol, isAuthRequired));

		try {
			PropiedadesCorreoModel mail = new PropiedadesCorreoModel();
			// From
			mail.setRemitente(new EmisorModel("mailsender@mail.com", "My Email"));

			// To
			InternetAddress dest = new InternetAddress("dest@mail.com");
			boolean sendBcc = false; // True if you want to send blind carbon copy
			boolean sendCc = true; // True if you want to send carbon copy, if either sendBcc or sendCc is true,
									// you should send list of mails
			InternetAddress[] mails = new InternetAddress[] { new InternetAddress("copy@mail.com") };
			mail.setDestinatario(new ReceptorModel(dest, sendBcc, sendCc, mails));

			// Subject
			mail.setAsunto("Demo");

			// Priority - 1 (highest) and 5 (lowest)
			mail.setPrioridad(1);

			// Attachments are added
			Map<String, InputStreamSource> adjuntos = new HashMap<>();
			adjuntos.put("logo.png", resourceLogo);
			adjuntos.put("pregunta.png", resourcePregunta);

			mail.setAdjuntos(adjuntos);

			// Body
			Map<String, Object> model = new HashMap<>();
			String urlEncuesta = "https://google.com";
			String qr = QRCodeService.getQRCodeImage("https://google.com", 250, 250);

			model.put("nombrePersona", "Jimmy");
			model.put("urlEncuesta", urlEncuesta);
			model.put("asunto", "Demo encuesta");
			model.put("qr", qr);

			String contenido = FreeMarkerTemplateUtils
					.processTemplateIntoString(configuration.getTemplate("email-template.ftlh"), model);
			mail.setCuerpo(contenido);

			// Mail is sent
			return emailService.entregaCorreo(mail);
		}
		catch (Exception e) {
		}

		return null;
	}
}
