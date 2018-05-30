package data;

public enum RequestStates {
    OK("OK"),
    ERROR("ERROR");

    private final String text;

    RequestStates(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
