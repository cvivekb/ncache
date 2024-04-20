import com.alachisoft.ncache.runtime.datasourceprovider.DistributedDataStructureType;
import com.alachisoft.ncache.runtime.datasourceprovider.ProviderCacheItem;
import com.alachisoft.ncache.runtime.datasourceprovider.ProviderDataStructureItem;
import com.alachisoft.ncache.runtime.datasourceprovider.ReadThruProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SampleReadThruProvider implements ReadThruProvider
{
    private Connection connection;
	
    // Perform tasks like allocating resources or acquiring connections
    public void init(Map<String, String> parameters, String cacheName) throws Exception {
        try {
	        String server = parameters.get("server");
	        String userId = parameters.get("username");
	        String password = parameters.get("password");
	        String database = parameters.get("database");
	        String connectionString = getConnectionString(server, database, userId, password);
	        try {
	            connection = DriverManager.getConnection(connectionString);
        } catch (Exception ex){
        	// Handle connection initialization failure
        }
    }

    // Responsible for loading an item from the external data source
    @Override
    public ProviderCacheItem loadFromSource(String key) throws Exception {
        try {
            ProviderCacheItem cacheItem = new ProviderCacheItem(loadEmployee(key));
            return cacheItem;
        }catch (Exception ex)
        {
            return null;
        }
    }

    // Responsible for loading bulk of items from the external data source
    @Override
    public Map<String, ProviderCacheItem> loadFromSource(Collection<String> collection) throws Exception {
        try {
            Map<String, ProviderCacheItem> providerItems = new HashMap<>();
            for (String key : collection) {
                Object data = loadEmployee(key);
                ProviderCacheItem cacheItem = new ProviderCacheItem(data);
                cacheItem.setGroup("employees");
                providerItems.put(key, cacheItem);
            }
            return providerItems;
        }catch (Exception ex)
        {
            //Handle exception
            return null;
        }
    }

    
// Adds ProviderDataStructureItem with distributed data structure type such as List, Set, Counter, etc.,
    @Override
    public ProviderDataStructureItem loadDataStructureFromSource(String key,  DistributedDataStructureType distributedDataStructureType) throws Exception {
    	// Implement the logic if it's necessary
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                // Handle connection closing failure
            }
        }
    }

    // This method runs the SQL query to fetch employee data from the data source
    private Object loadEmployee(String employeeKey) throws Exception {
        String selectQuery = "Select * from Employees WHERE EmployeeID = ?";
        PreparedStatement statement = connection.prepareStatement(selectQuery);
        statement.setString(1, employeeKey);
        ResultSet resultSet = (ResultSet) statement.executeQuery(selectQuery);
        if(resultSet.next()){
            Employee employee = fetchEmployee(resultSet);
            resultSet.close();
            return employee;
        }
        return null;
    }

    private Employee fetchEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt(1));
        employee.setName(rs.getString(2));
        employee.setEmailId(rs.getInt(3));
        return employee;
    }

    private String getConnectionString(String server, String database, String userName, String password) {
        // Construct and return the connection string
        // Example: "jdbc:mysql://server:port/database?user=username&password=password"
        return "jdbc:mysql://" + server + ":3306/" + database + "?user=" + userName + "&password=" + password;
    }
}