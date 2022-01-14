package es.fermax.notificationservice.enums;

public enum StatusEnum {

    DRAFT("D"),
    SENT("S"),
    ERROR("E"),
    ATTENDED("A");

    public final String status;

    StatusEnum(String status) {
        this.status = status;
    }

}
