/*

Redes de Comunicações I - Servidor Web
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer{
    
    public static void main(String arvg[]) throws Exception{
        
        //Ajustar o número da porta
        int porta = 6789;

        //Estabelece o socket de escuta do servidor
        ServerSocket socketServ = new ServerSocket(porta);

        //Estabelece o socket do cliente
        Socket socketCli;
        
        //Processar a requisição do serviço HTTP em um laço infinito
        while(true){
            
            //Apenas uma mensagem para indicar que o servidor encontra-se ativo
            System.out.println("Servidor ativo.");

            //Escutar requisição de conexão TCP
            socketCli = socketServ.accept();

            //Constrói um objeto para processar a mensagem de requisição HTTP
            HttpRequest requisicao = new HttpRequest(socketCli);

            //Criado um novo thread para processar as novas requisições
            Thread thread = new Thread(requisicao);

            //Inicia o thread.
            thread.start();
        }
    }
}
