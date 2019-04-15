package alfio.controller.api.v2.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketInfo {

    private final String fullName;
    private final String email;
    private final String uuid;
}
