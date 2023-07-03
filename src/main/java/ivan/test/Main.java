package ivan.test;

import ivan.container.Container;

public class Main {
    public static void main(String[] args) {
        Container.IoC container = new Container.IoC();
        container.init("ivan.test", ClassLoader.getSystemClassLoader());
        ClientApp clientApp = (ClientApp) container.getBean(ClientApp.class);
        clientApp.info();
    }
}
