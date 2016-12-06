package dkarelin.ru.rxjavaexample_1.rxbus;



public class Events {
    private Events(){}

    public static class Message {
        public final String message;

        public Message(String message) {
            this.message = message;
        }
    }
}