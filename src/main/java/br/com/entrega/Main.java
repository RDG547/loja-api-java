package br.com.entrega;

import br.com.entrega.api.ApiServer;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = resolvePort(args);
        new ApiServer(port).start();
    }

    private static int resolvePort(String[] args) {
        if (args.length == 0) {
            return 8080;
        }

        try {
            int port = Integer.parseInt(args[0]);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Porta fora do intervalo valido.");
            }
            return port;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("A porta informada deve ser numerica.", exception);
        }
    }
}
