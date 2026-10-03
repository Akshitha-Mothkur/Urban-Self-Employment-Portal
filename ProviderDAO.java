import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProviderDAO {

    public List<Provider> searchProviders(String service) {

        List<Provider> providers = new ArrayList<>();

        String sql = """
                SELECT p.id, u.name, p.service,
                       p.location, p.price, p.experience
                FROM providers p
                JOIN users u ON p.user_id = u.id
                WHERE LOWER(p.service) = LOWER(?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, service);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Provider provider = new Provider(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getString("service"),
                        result.getString("location"),
                        result.getDouble("price"),
                        result.getInt("experience")
                );

                providers.add(provider);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return providers;
    }
}