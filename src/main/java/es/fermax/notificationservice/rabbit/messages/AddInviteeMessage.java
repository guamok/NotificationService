package es.fermax.notificationservice.rabbit.messages;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddInviteeMessage implements Serializable {

	private static final long serialVersionUID = -773382601610311731L;
	String tokenId;
    String roomId;
}
