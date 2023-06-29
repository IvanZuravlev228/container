package ivan.test;

import ivan.container.Container;

public class Main {
    public static void main(String[] args) {
        Container injector = Container.getContainer();
        ClientApp clientApp = (ClientApp) Container.getInstance(ClientApp.class);
        clientApp.info();
    }
}
