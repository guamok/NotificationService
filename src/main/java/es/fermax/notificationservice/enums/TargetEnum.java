package es.fermax.notificationservice.enums;

public enum TargetEnum {
    TOKEN("T"),
    TOPIC("O"),
    CONDITION("C");

    public final String target;

    TargetEnum(String target) {
        this.target = target;
    }
}
