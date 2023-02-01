# Mailing service

Service developed with Spring 2.7.1 which has the function to deliver mail and to generate QR Code

# Import

Open Eclipse ID -> File -> Import -> Existing Maven Project -> Select folder -> Ok

# Install

Rigth click on imported project -> Run as -> Maven Install

# Usage

In your main project add the next

pom.xml
```
<dependency>
  <groupId>com.rbg</groupId>
	<artifactId>spring-mailingservice</artifactId>
	<version>0.0.1</version>
</dependency>
```


.java or refer to DEMO folder, it includes service demo sender and QR code generator

```
String host = "0.0.0.1"; // Your mail server IP"
int port = 25; // Your mail server port
boolean isAuthRequired = false; // If it is false, user and password values could be null
String usr = null; // User to login in server
String psd = null; // Psd to login in server
String protocol = "smtp";

// Initializes server configuration
EmailService emailService = new EmailService(new CorreoConfigModel(host, port, usr, psd, protocol, isAuthRequired));

// Data mail
PropiedadesCorreoModel mail = new PropiedadesCorreoModel();
// From
mail.setRemitente(new EmisorModel("mailsender@mail.com", "My Email"));

// To
InternetAddress dest = new InternetAddress("dest@mail.com");
boolean sendBcc = false; // True if you want to send blind carbon copy
boolean sendCc = true; // True if you want to send carbon copy, if either sendBcc or sendCc is true, you should send list of mails
InternetAddress[] mails = new InternetAddress[] { new InternetAddress("copy@mail.com") };
mail.setDestinatario(new ReceptorModel(dest, sendBcc, sendCc, mails));

// Subject
mail.setAsunto("Demo");

// Priority - 1 (highest) and 5 (lowest)
mail.setPrioridad(1);

// Attachments are allowed
Map<String, InputStreamSource> adjuntos = new HashMap<>();
adjuntos.put("logo.png", new ClassPathResource("/templates/images/logo.png"));
adjuntos.put("pregunta.png", new ClassPathResource("/templates/images/question.png"));
```
OR

```
/** Logo which will be attached to the template */
@Value("classpath:/templates/images/logo-baz.png")
private Resource resourceLogo;

/** Image to be attached in template */
@Value("classpath:/templates/images/pregunta.png")
private Resource resourcePregunta;
  
mail.setAdjuntos(adjuntos);

// Body
Map<String, Object> model = new HashMap<>();
String urlEncuesta = "www.google.com";
String qr = "base64Image";

model.put("nombrePersona", "Jimmy");
model.put("urlEncuesta", urlEncuesta);
model.put("asunto", "Demo");
model.put("qr", qr);

// On top of the file
@Autowired
private Configuration configuration;

String body = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("email-template.ftlh"), model);
mail.setCuerpo(body);

emailService.entregaCorreo(correo);
```
