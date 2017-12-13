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
		} catch (IOException ex) {
			ex.getStackTrace();
		}
	}
	@OnMessage
	public String handleMessage(String message, Session sesmessage) {
		onMessage = sesmessage;
		String recebida;
		for(int i = 0; i <= mapsize; i++) {
			if(onMessage.getId() == session.get(i).getId()) {
				recebida = message;
				verificar(recebida, sesmessage);
			}
		}
		String replyMessage="";
		return replyMessage;
	}
	@OnClose
	public void handleClose() {
//		for(int i = 0; i <= session.size(); i++) {
//			if(close.getId().equals(session.get(i).getId())) {
//
//				String saiu = clients.get(i).getNome();
//				for(int key : session.keySet()) {
//					try {
//						session.get(key).getBasicRemote().sendText(saiu + " Saiu do chat.");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				session.remove(i, close);
//				clients.remove(i, cliente.getNome().equals(saiu));
				System.out.println("Cliente saiu do chat.");
//			}else break;
//		}
//		

	}
	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}
	String verificar(String verifica, Session sesmessage) {
		//TRATA A DATA
		Date date = new Date();
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormatada = formatador.format(date);
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
							e.printStackTrace();
						}
						break;
					}else {

						if(splited[1].equals("-all")) {
							finalMessage = verifica.replace("/send -all", "");
							System.out.println(verifica);
							System.out.println(finalMessage);
							for(int key : session.keySet()) {
								try {
									session.get(key).getBasicRemote().sendText(userx + " diz: " + finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else
							if(splited[1].equals("-user")) {
								String usuario = splited[2];
								int tamanho = usuario.length() + 12;
								finalMessage = verifica.substring(tamanho);

								for(int u : clients.keySet()) {
									String resultado = clients.get(u).getNome();
									if(resultado.equals(usuario)) {
										Session enviar;
										enviar = session.get(u);
										try {
											enviar.getBasicRemote().sendText(userx + " diz: " + finalMessage + " - <" + horas + ":" + minutos + "><" + dataFormatada + ">");
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

			if(clients.get(i).getNome().equals(newName)) {
				try {
					onMessage.getBasicRemote().sendText(newName + " Já está em uso, favor escolher outro nome.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}else

				if(onMessage.getId() == session.get(i).getId()) {
					String teste = clients.get(i).getNome();
					if(teste.equals("JonhDoe")) {
						for(int key : session.keySet()) {
							try {
								session.get(key).getBasicRemote().sendText(newName + " Entrou no chat");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
					else {
						for(int key : session.keySet()) {
							try {
								session.get(key).getBasicRemote().sendText(teste + " agora é: " + newName);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
					clients.get(i).setNome(newName);
					clients.get(i).setPermissao(true);
					teste = clients.get(i).getNome();

					break;
				}
		}
		break;
		case("/bye"):
		break;
		default:
			try {
				onMessage.getBasicRemote().sendText("Comando inválido, utilizar apenas os comandos seguintes:");
				onMessage.getBasicRemote().sendText("/send -all(envia mensagem para a sala)");
				onMessage.getBasicRemote().sendText("/send -user(enviar mensagem para um usuario especifico)");
				onMessage.getBasicRemote().sendText("/bye (sair do grupo)");
				onMessage.getBasicRemote().sendText("/list (listar usuários na sala)");
				onMessage.getBasicRemote().sendText("/rename (nome) para renomear");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		return aux;
	}
}