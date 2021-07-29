package cinema.controllers;

import cinema.entities.Cinema;
import cinema.entities.Seat;
import cinema.entities.Token;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
public class CinemaController {

    private final Cinema cinema = new Cinema();

    @GetMapping("/seats")
    public Cinema getSeats() {
        return cinema;
    }

    @PostMapping("/purchase")
    public String checkSeat(HttpServletResponse response, @RequestBody Seat seat) {
        response.addHeader("Content-type", "application/json");

        if (seat.getRow() > 9 || seat.getColumn() > 9 || seat.getRow() < 1 || seat.getColumn() < 1)  {
            response.setStatus(400);
            return "{ \"error\": \"The number of a row or a column is out of bounds!\" }";
        }

        Seat[] seats = cinema.getSeats();
        int arrPos = cinema.getArrPos(seat.getRow(), seat.getColumn());
        Seat seatToBook = seats[arrPos];

        if (seatToBook.isTaken()) {
            response.setStatus(400);
            return "{ \"error\": \"The ticket has been already purchased!\" }";
        }

        seatToBook.setTaken(true);
        return seatToBook.bookSeat();
    }

    @PostMapping("/return")
    public String returnToken(HttpServletResponse response, @RequestBody Token token) {
        response.addHeader("Content-type", "application/json");

        Optional<Seat> seat = cinema.findSeat(token);

        if (seat.isEmpty()) {
            response.setStatus(400);
            return "{ \"error\": \"Wrong token!\" }";
        }

        Seat s = seat.get();
        s.setTaken(false);
        return s.returnSeat();
    }

    @PostMapping(path="/stats")
    public String getStats(HttpServletResponse response,
                           @RequestParam(required = false) @RequestBody String password) {
        response.addHeader("Content-type", "application/json");

        if (password == null || !password.equals("super_secret")) {
            response.setStatus(401);
            return "{ \"error\": \"The password is wrong!\" }";
        }

       return cinema.getStats();
    }

}