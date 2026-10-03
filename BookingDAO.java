import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class BookingDAO {

    public boolean createBooking(int providerId,
                                 String customerName,
                                 LocalDate bookingDate) {

        String sql = """
                INSERT INTO bookings
                (provider_id, customer_name, booking_date, status)
                VALUES (?, ?, ?, 'Pending')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, providerId);
            statement.setString(2, customerName);
            statement.setDate(
                    3,
                    java.sql.Date.valueOf(bookingDate)
            );

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}