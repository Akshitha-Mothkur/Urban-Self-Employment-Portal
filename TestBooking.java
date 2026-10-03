import java.time.LocalDate;

public class TestBooking {

    public static void main(String[] args) {

        BookingDAO dao = new BookingDAO();

        boolean success = dao.createBooking(
                1,
                "Akshitha",
                LocalDate.now()
        );

        if (success) {
            System.out.println("Booking created successfully!");
        } else {
            System.out.println("Booking failed!");
        }
    }
}