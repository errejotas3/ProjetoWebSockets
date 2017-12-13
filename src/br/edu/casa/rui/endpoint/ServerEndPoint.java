package br.edu.casa.rui.endpoint;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import br.edu.casa.rui.cliente.Cliente;

@ServerEndpoint("/serverendpoint")
public class ServerEndPoint {
	static private HashMap<Integer, Cliente> clients = new HashMap<Integer, Cliente>();
	static private HashMap<Integer, Session> session = new HashMap<Integer, Session>();
	static int mapsize = 0;
	static int idMessage;
	int countFor = 0;
	Session onOpen;
	Session onMessage;
	Cliente cliente = new Cliente();
	@OnOpen
	public void handleOpen(Session ses) {
		System.out.println("Novo cliente conectado ao chat");
		onOpen = ses;
		System.out.println("OPEN" + onOpen.getId());
		mapsize = mapsize + 1;
		System.out.println(ses.getId());
		cliente.setNome("JonhDoe");
		mapsize = clients.size();
		session.put(mapsize, ses);
		cliente.setPermissao(false);
		clients.put(mapsize, cliente);

		System.out.println("Clientes: " + clients.size());
		System.out.println("SessionSize: " + session.size());
		System.out.println(mapsize);
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
		String recebida;
		System.out.println("MESSAGE" + onMessage.getId());
		for(int i = 0; i <= mapsize; i++) {
			if(onMessage.getId() == session.get(i).getId()) {
				recebida = message;
				verificar(recebida, sesmessage);
			}
		}
		//COMEÇA A TRATAR A STRING RECEBIDA
		String sesid = sesmessage.getId();
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
	String verificar(String verifica, Session sesmessage) {
		//TRATA A DATA
		Date date = new Date();
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		Calendar data = Calendar.getInstance();
		int horas = data.get(Calendar.HOUR_OF_DAY);
		int minutos = data.get(Calendar.MINUTE);
		//FIM TRATA DATAs
		String aux;
		String finalMessage;
		String[] splited = verifica.split(" ");
		aux = splited[0];

		switch (aux) {

		case ("/send"):
			for(int i = 0; i <= mapsize; i++) {
				if(onMessage.getId() == session.get(i).getId()) {
					String userx = clients.get(i).getNome();
					if(userx.equals("JonhDoe")) {
						try {
							onMessage.getBasicRemote().sendText("Para entrar no chat utilize o comando /rename");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}else {

						if(splited[1].equals("-all")) {
							System.out.println("estou no all");
							finalMessage = verifica.replace("/send -all", "");
							System.out.println(verifica);
							System.out.println(finalMessage);
							for(int key : session.keySet()) {
								try {
									session.get(key).getBasicRemote().sendText(finalMessage);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else
							if(splited[1].equals("-user")) {
								System.out.println("estou no user");
								String usuario = splited[2];
								int tamanho = usuario.length() + 12;
								finalMessage = verifica.substring(tamanho);
								
								for(int u : clients.keySet()) {
									String resultado = clients.get(u).getNome();
									if(resultado.equals(usuario)) {
										Session enviar;
										enviar = session.get(u);
										try {
											enviar.getBasicRemote().sendText(finalMessage);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}								
					}
				}
			}
		break;

		case("/list"):

			String result;
		try {
			onMessage.getBasicRemote().sendText("<--Usuários Conectados-->");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int key : clients.keySet()) {
			result = clients.get(key).getNome();

			if(result.equals("JonhDoe")) {
				System.out.println(result);
			}else {

				try {
					onMessage.getBasicRemote().sendText(result);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		break;

		case("/rename"):
			String newName = splited[1];
		for(int i = 0; i <= mapsize; i++) {
			if(onMessage.getId() == session.get(i).getId()) {
				String teste = clients.get(i).getNome();
				System.out.println(teste);
				clients.get(i).setNome(newName);
				clients.get(i).setPermissao(true);
				teste = clients.get(i).getNome();
				break;
			}
		}
		break;
		case("bye"):
			Session byeSession;
		byeSession = onMessage;

		handleClose();
		break;
		default:
			try {
				onMessage.getBasicRemote().sendText("Comando inválido, utilizar apenasos comandos seguintes:");
				onMessage.getBasicRemote().sendText("send -all(envia mensagem para a sala)");
				onMessage.getBasicRemote().sendText("send -user(enviar mensagem para um usuario especifico)");
				onMessage.getBasicRemote().sendText("bye (sair do grupo)");
				onMessage.getBasicRemote().sendText("list (listar usuários na sala)");
				onMessage.getBasicRemote().sendText("rename (renomear)");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		return aux;
	}
}