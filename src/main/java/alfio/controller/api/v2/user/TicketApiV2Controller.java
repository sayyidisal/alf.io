/**
 * This file is part of alf.io.
 *
 * alf.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alf.io is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alf.io.  If not, see <http://www.gnu.org/licenses/>.
 */
package alfio.controller.api.v2.user;

import alfio.controller.TicketController;
import alfio.controller.api.v2.user.model.TicketInfo;
import alfio.manager.TicketReservationManager;
import alfio.repository.TicketCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/public/")
public class TicketApiV2Controller {


    private final TicketController ticketController;
    private final TicketReservationManager ticketReservationManager;
    private final TicketCategoryRepository ticketCategoryRepository;


    @GetMapping("/event/{eventName}/ticket/{ticketIdentifier}/code.png")
    public void showQrCode(@PathVariable("eventName") String eventName,
                           @PathVariable("ticketIdentifier") String ticketIdentifier, HttpServletResponse response) throws IOException {
        ticketController.generateTicketCode(eventName, ticketIdentifier, response);
    }


    @GetMapping("/tmp/event/{eventName}/ticket/{ticketIdentifier}")
    public Map<String, Object> getTicketInfo(@PathVariable("eventName") String eventName,
                                             @PathVariable("ticketIdentifier") String ticketIdentifier) {

        //TODO: cleanup, we load useless data here!

        var oData = ticketReservationManager.fetchCompleteAndAssigned(eventName, ticketIdentifier);
        if(oData.isEmpty()) {
            return Collections.emptyMap();
        }
        var data = oData.get();


        var ticketCategory = ticketCategoryRepository.getByIdAndActive(data.getRight().getCategoryId(), data.getLeft().getId());

        var res = new HashMap<String, Object>();

        var ticket = data.getRight();
        res.put("ticket", new TicketInfo(ticket.getFullName(), ticket.getEmail(), ticket.getUuid()));
        res.put("ticketCategoryName", ticketCategory.getName());
        res.put("reservationFullName", data.getMiddle().getFullName());
        res.put("reservationId", ticketReservationManager.getShortReservationID(data.getLeft(), data.getMiddle()));

        return res;
    }

}
