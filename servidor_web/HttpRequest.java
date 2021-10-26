/*

Redes de Comunicações I - Requisição HTTP
Felipe Daniel Dias dos Santos - 11711ECP004
Graduação em Engenharia de Computação - Faculdade de Engenharia Elétrica - Universidade Federal de Uberlândia

*/

import java.io.*;
import java.net.*;
import java.util.*;

class HttpRequest implements Runnable{
    
    final static String CRLF = "\r\n";

    //Referência do socket da conexão
    Socket socket;

    public HttpRequest(Socket socket) throws Exception{

   		this.socket = socket;
    }
    
    public void run(){
         
        try{
             
            processRequest();
        }
    
        catch(Exception e){
        
             System.out.println(e);
        }
    }
    
    private void processRequest() throws Exception{
        
        //Objeto is: referência para os trechos de entrada
        InputStreamReader is = new InputStreamReader(socket.getInputStream());
        
        //Objeto os: referência para os trechos de saída
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        
        //Ajustar os filtros do trecho de entrada
        BufferedReader br = new BufferedReader(is);
        
        //Obter a linha de requisição da mensagem de requisição HTTP
        String requestLine = br.readLine();
        
        //Exibir a linha de requisição
        System.out.println();
        System.out.println(requestLine);
        
        //Obter e exibir as linhas de cabeçalho.
        String headerLine = null;
        
        while((headerLine = br.readLine()).length() != 0)
            
             System.out.println(headerLine);

        //Extrair o nome do arquivo sa linha de requisição
        StringTokenizer tokens = new StringTokenizer(requestLine);

        //Pular o método, que deve ser "GET"
        tokens.nextToken();
        String fileName = tokens.nextToken();

        //Acrescente um “.” de modo que a requisição do arquivo esteja dentro do diretório atual
        fileName = "." + fileName;

        //Abrir o arquivo requisitado
        FileInputStream fis = null;
        Boolean fileExists = true;

        try{

           fis = new FileInputStream(fileName);
        } 

        catch(FileNotFoundException e){

             fileExists = false;
        }

        //Construir a mensagem de resposta
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if(fileExists){

           statusLine = "HTTP/1.0 200 OK" + CRLF;
           contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        }

       else{

           statusLine = "HTTP/1.0 404 Not found" + CRLF;
           contentTypeLine = "Content-type: " + contentType(fileName)+ CRLF;
           entityBody = "<HTML>" + "<HEAD><TITTLE>Not Found</TITTLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
       }

       //Enviar a linha de status
       os.writeBytes(statusLine);

       //Enviar a linha de tipo de conteúdo
       os.writeBytes(contentTypeLine);

       //Enviar uma linha em branco para indicar o fim das linhas de cabeçalho
       os.writeBytes(CRLF);

       char[] buffer = new char[2048];
       String bodyPost = "";

       //Enviar o corpo da entidade
       if(fileExists){

          sendBytes(fis, os);
          fis.close();
        } 

       else{

          bodyPost = bodyPost + (new String(buffer, 0, br.read(buffer)) + "<BR>");
          entityBody = "<HTML><HEAD><TITLE> POST MESSAGE </TITLE></HEAD>" + "<BODY> POST SENT MESSAGE: </BR>" + bodyPost + "</BODY></HTML>";
          os.writeBytes(entityBody);
        }

        //Feche as cadeias e socket
        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{

    	//Construir um buffer de 1K para comportar os bytes no caminho para o socket
     	byte[] buffer = new byte[1024];
     	int bytes = 0;

     	//Copiar o arquivo requisitado dentro da cadeia de saída do socket
     	while((bytes = fis.read(buffer)) != -1 )

     		os.write(buffer, 0, bytes);    
    }

    private static String contentType(String fileName){

        if(fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.endsWith(".txt"))

           return "text/html";

        if(fileName.endsWith(".gif")) 

           return "image/gif";

        if(fileName.endsWith(".jpeg")) 

           return "image/jpeg";

        return "application/octet-stream";
    }
}
