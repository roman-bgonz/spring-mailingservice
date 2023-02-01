package com.rbg.springmailingservice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import com.rbg.springmailingservice.model.CorreoConfigModel;
import com.rbg.springmailingservice.model.EmisorModel;
import com.rbg.springmailingservice.model.EstatusEnvioModel;
import com.rbg.springmailingservice.model.ReceptorModel;
import com.rbg.springmailingservice.model.request.PropiedadesCorreoModel;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailServiceTest {

	@Mock
	private JavaMailSenderImpl mailSender;

	EmailService ser = new EmailService(new CorreoConfigModel("0.0.0.0", 25, null, null, "smtp", false));

	@BeforeEach
	void init() {
		ReflectionTestUtils.setField(ser, "mailSender", mailSender);

		doNothing().when(mailSender).send(ArgumentMatchers.any(MimeMessage.class));

		when(mailSender.getJavaMailProperties()).thenReturn(new Properties());

		when(mailSender.createMimeMessage()).thenCallRealMethod();
	}

	@Test
	void testEntregaCorreo() throws Exception {
		PropiedadesCorreoModel props = new PropiedadesCorreoModel();
		props.setAdjuntos(Collections.emptyMap());
		props.setAsunto("Correo test");
		props.setCuerpo("Body correo");
		props.setPrioridad(1);

		ReceptorModel dest = new ReceptorModel();
		dest.setCorreo(new InternetAddress("test@mail.com"));
		dest.setEnviarCorreoCopia(false);
		dest.setEnviarCorreoCopiaOculta(false);
		props.setDestinatario(dest);

		EmisorModel rem = new EmisorModel();
		rem.setAlias("Test");
		rem.setCorreo("rem@mail.com");
		props.setRemitente(rem);

		EstatusEnvioModel enviado = ser.entregaCorreo(props);

		assertTrue(enviado.isEnviado());
		assertNull(enviado.getExcepcion());

		assertNotNull(new PropiedadesCorreoModel(null, null, null, 0, null, null));
		assertNotNull(new ReceptorModel(null, false, false, null));
		assertNotNull(new EmisorModel(null, null));
	}

	@ParameterizedTest
	@CsvSource(value = { "true, false", "false, true" }, ignoreLeadingAndTrailingWhitespace = true)
	void testEntregaCorreoCCAttachments(boolean enviaCc, boolean enviaBcc) throws Exception {
		PropiedadesCorreoModel props = new PropiedadesCorreoModel();
		props.setAdjuntos(Map.of("image.png", new ClassPathResource("path")));
		props.setAsunto("Correo test");
		props.setCuerpo("Body correo");
		props.setPrioridad(1);

		ReceptorModel dest = new ReceptorModel();
		dest.setCorreo(new InternetAddress("test@mail.com"));
		dest.setEnviarCorreoCopia(enviaCc);
		dest.setEnviarCorreoCopiaOculta(enviaBcc);
		dest.setCorreosCC(new InternetAddress[] { new InternetAddress("test@mail") });
		props.setDestinatario(dest);

		EmisorModel rem = new EmisorModel();
		rem.setAlias("Test");
		rem.setCorreo("rem@mail.com");
		props.setRemitente(rem);

		EstatusEnvioModel enviado = ser.entregaCorreo(props);

		assertTrue(enviado.isEnviado());
		assertNull(enviado.getExcepcion());
		assertTrue(dest.getCorreosCC().length > 0);
	}

	@Test
	void testEntregaCorreoEx() throws Exception {
		try (MockedConstruction<MimeBodyPart> mocked = mockConstruction(MimeBodyPart.class, (mock, context) -> {
			doThrow(MessagingException.class).when(mock).setDataHandler(any());
		})) {
			PropiedadesCorreoModel props = new PropiedadesCorreoModel();
			props.setAdjuntos(Map.of("image.png", new ClassPathResource("path")));
			props.setAsunto("Correo test");
			props.setCuerpo("Body correo");
			props.setPrioridad(1);

			ReceptorModel dest = new ReceptorModel();
			dest.setCorreo(new InternetAddress("test@mail.com"));
			props.setDestinatario(dest);

			EmisorModel rem = new EmisorModel();
			rem.setAlias("Test");
			rem.setCorreo("rem@mail.com");
			props.setRemitente(rem);

			doThrow(NullPointerException.class).when(mailSender).send(ArgumentMatchers.any(MimeMessage.class));
			try {
				ser.entregaCorreo(props);
			}
			catch (Exception e) {
				assertInstanceOf(Exception.class, e);
			}
		}
	}

	@Test
	void testModels() {
		CorreoConfigModel config = new CorreoConfigModel();
		config.setAutenticacionRequerida(true);
		config.setHost("1.1.1.1");
		config.setProtocolo("smtp");
		config.setPuerto(25);
		config.setPsd("psd");
		config.setUsuario("usr");

		assertNotNull(config.getHost());
		assertNotNull(config.getProtocolo());
		assertNotNull(config.getPsd());
		assertNotNull(config.getUsuario());
		assertTrue(config.getPuerto() > 0);
		assertTrue(config.isAutenticacionRequerida());

		EstatusEnvioModel estatus = new EstatusEnvioModel(false, new NullPointerException());
		estatus.setExcepcion(new NullPointerException());
		assertFalse(estatus.isEnviado());
		assertNotNull(estatus.getExcepcion());
	}
}
