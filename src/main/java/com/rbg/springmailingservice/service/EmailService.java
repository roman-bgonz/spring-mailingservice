package com.rbg.springmailingservice.service;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;

import com.rbg.springmailingservice.model.CorreoConfigModel;
import com.rbg.springmailingservice.model.EstatusEnvioModel;
import com.rbg.springmailingservice.model.request.PropiedadesCorreoModel;

/**
 * Sends an email
 * 
 * @author 292821
 *
 */
public class EmailService {

	// Mail sender
	private JavaMailSenderImpl mailSender;

	/**
	 * Constructor
	 * 
	 * @param request {@link CorreoConfigModel} Initializes mail sender properties
	 */
	public EmailService(CorreoConfigModel request) {
		mailSender = new JavaMailSenderImpl();
		inicializaMailSender(request);
	}

	/**
	 * Sends an email taking request values to configure sender
	 * 
	 * @param props {@link PropiedadesCorreoModel}
	 * @return {@link EstatusEnvioModel}
	 * @throws Exception
	 */
	public EstatusEnvioModel entregaCorreo(PropiedadesCorreoModel props) throws Exception {
		EstatusEnvioModel response = new EstatusEnvioModel();

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());
		// From
		helper.setFrom(props.getRemitente().getCorreo(), props.getRemitente().getAlias());

		// To
		mimeMessage.setRecipient(TO, props.getDestinatario().getCorreo());
		helper.setTo(props.getDestinatario().getCorreo());

		// CC
		if (props.getDestinatario().isEnviarCorreoCopia()) {
			mimeMessage.addRecipients(CC, props.getDestinatario().getCorreosCC());
			helper.setCc(props.getDestinatario().getCorreosCC());
		}

		// BCC
		if (props.getDestinatario().isEnviarCorreoCopiaOculta()) {
			mimeMessage.addRecipients(BCC, props.getDestinatario().getCorreosCC());
			helper.setBcc(props.getDestinatario().getCorreosCC());
		}

		// Subject
		helper.setSubject(props.getAsunto());

		// Priority
		helper.setPriority(props.getPrioridad());

		// Attachments
		if (!CollectionUtils.isEmpty(props.getAdjuntos())) {
			props.getAdjuntos().entrySet().forEach(a -> {
				try {
					helper.addAttachment(a.getKey(), a.getValue());
				}
				catch (MessagingException e) {
					response.setExcepcion(e);
				}
			});
		}

		// Body
		helper.setText(props.getCuerpo(), true);

		// Mail is sent
		mailSender.send(mimeMessage);
		response.setEnviado(true);

		return response;
	}

	/**
	 * Initializes mail sender
	 * 
	 * @param request {@link CorreoConfigModel}
	 */
	private void inicializaMailSender(CorreoConfigModel request) {
		// Server configuration is set
		mailSender.setHost(request.getHost());
		mailSender.setPort(request.getPuerto());
		mailSender.setUsername(request.getUsuario());
		mailSender.setPassword(request.getPsd());

		// Temporal properties are created to establish mail configuration
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", request.getProtocolo());
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.auth", String.valueOf(request.isAutenticacionRequerida()));
	}
}
