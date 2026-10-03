import java.util.List;

public class TestProvider {

    public static void main(String[] args) {

        ProviderDAO dao = new ProviderDAO();

        List<Provider> providers =
                dao.searchProviders("Electrician");

        for (Provider p : providers) {

            System.out.println(
                    p.getName() + " | " +
                    p.getService() + " | " +
                    p.getLocation() + " | ₹" +
                    p.getPrice()
            );
        }
    }
}
