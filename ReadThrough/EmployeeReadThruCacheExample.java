import com.alachisoft.ncache.client.Cache;
import com.alachisoft.ncache.client.CacheManager;
import com.alachisoft.ncache.runtime.caching.ReadMode;
import com.alachisoft.ncache.runtime.caching.ReadThruOptions;

public class EmployeeReadThruCacheExample {
    public static void main(String[] args) {
        // Connect to the cache
        String cacheName = "employeeCache";
        Cache cache = CacheManager.getCache(cacheName);

        // Specify the key for the employee (e.g., employee ID)
        String employeeKey = "emp123";
       
         // Fetching employee details without Read-Through. This will return null if cache doesn't have this employee key.
        Employee employee = cache.get(employeeKey, Employee.class);
        print(employee);
        
         // Fetching employee details with ReadThru mode. This will return the employee details even if it doesn't exist in cache by fetching from the underlying data source.
        employee = cache.get(employeeKey, new ReadThruOptions(ReadMode.ReadThru), Employee.class);
        print(employee);
       
         // Fetching employee details with ReadThruForced mode. This will fetch the employee details from the backend source (database) even if it's available in cache.
        employee = cache.get(employeeKey, new ReadThruOptions(ReadMode.ReadThruForced), Employee.class);
        print(employee);
    }
   
     public static void print(Employee employee) {
        if(employee != null){
            System.out.println("EmployeeID, Name, Emaild");
            System.out.println(employee.getId() + ", " + employee.getName() + ", " + employee.getEmailId() + "\n");
        }
        else
            System.out.println("Employee doesn't exist in the cache.");
    }
}