package io.collabanator;

public class Message {
    private String name;
    private double date;
    private int id;
    private String type;
    private String text;

    public Message() {
    }

    public Message(String name, double date, int id, String type, String text) {
        this.name = name;
        this.date = date;
        this.id = id;
        this.type = type;
        this.text = text;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDate() {
        return this.date;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message name(String name) {
        setName(name);
        return this;
    }

    public Message date(int date) {
        setDate(date);
        return this;
    }

    public Message id(int id) {
        setId(id);
        return this;
    }

    public Message type(String type) {
        setType(type);
        return this;
    }

    public Message text(String text) {
        setText(text);
        return this;
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", date='" + getDate() + "'" +
            ", id='" + getId() + "'" +
            ", type='" + getType() + "'" +
            ", text='" + getText() + "'" +
            "}";
    }

}
