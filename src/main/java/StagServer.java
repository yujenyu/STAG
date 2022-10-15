import java.io.*;
import java.net.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class StagServer {
    StagController stagController;

    public static void main(String[] args) {
        if (args.length != 2) {
            // new StagServer("data/basic-entities.dot", "data/basic-actions.json", 8888);
            new StagServer("data/extended-entities.dot", "data/extended-actions.json", 8888);
            // System.out.println("Usage: java StagServer <entity-file> <action-file>");
        }
        else new StagServer(args[0], args[1], 8888);
    }


    public StagServer(String entityFilename, String actionFilename, int portNumber) {
        try {
            // Next line will block until a connection is received
            ServerSocket ss = new ServerSocket(portNumber);

            StagParser stagParser = new StagParser(entityFilename, actionFilename);
            stagController = new StagController(stagParser.parseGame());

            while (true) acceptNextConnection(ss);
        } catch (IOException ioe) {
            System.err.println(ioe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptNextConnection(ServerSocket ss) {
        try {
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            stagController.processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

}
