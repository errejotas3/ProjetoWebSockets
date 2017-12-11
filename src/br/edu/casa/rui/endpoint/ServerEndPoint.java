package br.edu.casa.rui.endpoint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import br.edu.casa.rui.cliente.Cliente;

@ServerEndpoint("/serverendpoint")
public class ServerEndPoint {

	private HashMap<Integer, Cliente> clients = new HashMap<Integer, Cliente>();
	private HashMap<Integer, Session> session = new HashMap<Integer, Session>();
	int countForEach = 0;
	int mapsize;
	Session onOpen;
	Session onMessage;

	Cliente cliente = new Cliente();

	@OnOpen
	public void handleOpen(Session ses) {
		System.out.println("Novo cliente conectado ao chat");
		onOpen = ses;

		System.out.println(ses.getId());
		cliente.setNome("JonhDoe");
		mapsize = clients.size();
		session.put(mapsize, ses);
		cliente.setId(mapsize);
		cliente.setMessagem("");
		cliente.setPermissao(false);
		clients.put(mapsize, cliente);

		try {
			ses.getBasicRemote().sendText("Bem vindo ao chat, favor utilizar o comando /rename");
			ses.getBasicRemote().sendText(cliente.getNome());
		} catch (IOException ex) {
			ex.getStackTrace();
		}


	}

	@OnMessage
	public String handleMessage(String message, Session sesmessage) {
		System.out.println("Recebido do cliente: " + message);
		System.out.println("cliente que enviou: " + sesmessage.getId());
		onMessage = sesmessage;



		//TRATA A DATA
		Date date = new Date();
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		Calendar data = Calendar.getInstance();
		int horas = data.get(Calendar.HOUR_OF_DAY);
		int minutos = data.get(Calendar.MINUTE);


		//COMEÇA A TRATAR A STRING RECEBIDA
		String sesid = sesmessage.getId();



		String recebida;
		recebida = message;
		String aux;
		aux = verificar(recebida);

		switch(aux) {

		case ("/send"):


		}



		String replyMessage="";
		System.out.println("Enviado ao cliente: " + replyMessage);
		return replyMessage;
	}

	@OnClose
	public void handleClose() {
		System.out.println("Cliente saiu do chat.");
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}

	String verificar(String verifica) {
		String aux;
		String finalMessage;
		String[] splited = verifica.split(" ");

		aux = splited[0];

		switch (aux) {


		case ("send"):
			if(splited[1].equals("-all")) {
				System.out.println("estou no all");
				finalMessage = verifica.replace("send -all", "");
				System.out.println(verifica);
				System.out.println(finalMessage);

			}else
				if(splited[1].equals("-user")) {
					System.out.println("estou no user");
					finalMessage = verifica.replace("send -all", "");
					System.out.println(finalMessage);
				}

		break;

		case("list"):
			break;
		case("rename"):
			break;
		case("bye"):
			Session byeSession;
		byeSession = onMessage;

		handleClose();
		break;

		default:
			System.out.println("Comando inválido, utilizar apenasos comandos seguintes:");
			System.out.println("send -all(envia mensagem para a sala)");
			System.out.println("send -user(enviar mensagem para um usuario especifico)");
			System.out.println("bye (sair do grupo)");
			System.out.println("list (listar usuários na sala)");
			System.out.println("rename (renomear)");
			break;
		}


		return aux;

	}

}
