import com.alachisoft.ncache.runtime.caching.ProviderCacheItem;
import com.alachisoft.ncache.runtime.datasourceprovider.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class SampleWriteThruProvider implements WriteThruProvider {

    private Connection connection;

    // Perform tasks like allocating resources or acquiring connections
    @Override
    public void init(Map<String, String> parameters, String cacheId) throws Exception {
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

    @Override
    public void dispose() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
            }
        }
    }

    @Override
    public OperationResult writeToDataSource(WriteOperation operation) {
        ProviderCacheItem cacheItem = operation.getProviderItem();
        Employee employee = cacheItem.getValue(Employee.class);

        switch (operation.getOperationType()) {
            case WriteOperationType.Add:
                return new OperationResult(operation, insertEmployee(employee));
            case WriteOperationType.Update:
                return new OperationResult(operation, updateEmployee(employee));
            // Handle other operation types (DELETE, etc.) as needed
            default:
                return new OperationResult(operation, OperationResult.Status.Failure);
        }
    }
   
     @Override
    public OperationResult writeToDataSource(Collection<WriteOperation> operations) {
    	List<OperationResult> operationResults = new ArrayList<OperationResult>();
        for (WriteOperation operation : operations) {
            ProviderCacheItem cacheItem = operation.getProviderItem();
            Employee employee = cacheItem.getValue(Employee.class);

            switch (operation.getOperationType()) {
                case WriteOperationType.ADD:
                    operationResults.add(operation, insertEmployee(employee));
                    break;
                case WriteOperationType.UPDATE:
                    operationResults.add(operation, updateEmployee(employee));
                    break;
                // Handle other operation types (DELETE, etc.) as needed
                default:
                    // Log or handle unsupported operation
                    operationResults.add(operation, OperationResult.Status.Failure);
                    break;
            }
        }
        return operationResults;
    }
   
     @Override
    public Collection<OperationResult> writeDataStructureToDataSource(Collection<DataStructureWriteOperation> collection) throws Exception {
        // Implement this if necessary
    }

    private OperationResult insertEmployee(Employee employee) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Employee (Id, Name, EmailId) VALUES (?, ?, ?)");
            statement.setInt(1, employee.getId());
            statement.setString(2, employee.getName());
            statement.setString(3, employee.getEmailId());
            statement.executeUpdate();
            return OperationResult.Status.Success;
        } catch (SQLException ex) {
            // Handle exception
            return OperationResult.Status.Failure;
        }
    }

    private OperationResult updateEmployee(Employee employee) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE Employee SET Name = ?, EmailId = ? WHERE Id = ?");
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getEmailId());
            statement.setInt(3, employee.getId());
            statement.executeUpdate();
            return OperationResult.Status.Success;
        } catch (SQLException ex) {
            // Handle exception
            return OperationResult.Status.Failure;
        }
    }

    private String getConnectionString(String server, String database, String userName, String password) {
        // Construct and return the connection string
        // Example: "jdbc:mysql://server:port/database?user=username&password=password"
        return "jdbc:mysql://" + server + ":3306/" + database + "?user=" + userName + "&password=" + password;
    }
}