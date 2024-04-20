import com.alachisoft.ncache.client.Cache;
import com.alachisoft.ncache.client.CacheItem;
import com.alachisoft.ncache.client.CacheManager;
import com.alachisoft.ncache.runtime.caching.WriteMode;
import com.alachisoft.ncache.runtime.caching.WriteThruOptions;

public class EmployeeWriteThruCacheExample
{
    public static void main(String[] args) throws Exception {
        String cacheName = "employeeCache";
       
         // Connect to the cache and return a cache handle
        Cache cache = CacheManager.getCache(cacheName);

        Employee employee = new Employee("sdfl123", "Ron", "ron@xyz.com");
        String employeeKey = "Emp" + employee.getId();
        CacheItem employeeCacheItem = new CacheItem(employee);

        // Add item to the cache with Write Through
        cache.insert(employeeKey, employeeCacheItem, new WriteThruOptions(WriteMode.WriteThru));

        print(employee);
       
         // Getting item from the cache. If not found, a null object is returned
        Employee retrievedEmployee = cache.get(employeeKey, Employee.class);

        if (retrievedEmployee != null)
        {
            print(retrievedEmployee);
           
             // Update employee details
            retrievedEmployee.setEmailId("ronupd@xyz.com");
        	employeeCacheItem = new CacheItem(retrievedEmployee);
        	// Update employee in the cache using Write Through operation
        	cache.insert(employeeKey, employeeCacheItem, new WriteThruOptions(WriteMode.WriteThru));
        }
        cache.close();
    }

    public static void print(Employee employee) {
        if(employee != null){
            System.out.println("EmployeeID, Name, EmailId");
            System.out.println(employee.getId() + ", " + employee.getName() + ", " + employee.getEmailId() + "\n");
        }
        else
            System.out.println("Employee doesn't exist in the cache.");
    }
}