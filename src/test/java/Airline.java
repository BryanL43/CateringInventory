import java.util.List;

import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.impl.AirlineDaoImpl;
import org.fakeskymeal.dto.AirlineDto;

/**
 * Airline
 *
 * Test class to access for Airline.
 */
public class Airline {

    public Airline() {

    }

    public static void main(String[] args) {
        System.out.println("Entering test.Airline.main");

        AirlineDao airlineDao = new AirlineDaoImpl();
        AirlineDto airlineDto = null;
        List<AirlineDto> airlines = null;

        try {
            int test = 3;
            switch(test) {
                case 1:
                    airlineDto = airlineDao.get(2); // Acquisition by direct index
                    System.out.println("Returned Department(1):" + airlineDto.toJson());
                    break;
                case 2: // Acquire a row via specified params
                    airlineDto = airlineDao.getRow("name", "Spirit"); // can have id; p_key_id
                    System.out.println("Returned Department(1):" + airlineDto.toJson());
                    break;
                case 3: // Acquire all the rows via specified params
                    airlines = airlineDao.getRows("contact_info", "Here");
                    for (AirlineDto airline : airlines) {
                        airlineDto = airline;
                        System.out.println("\nReturned Department(" + airlineDto.getAirlineId() + "):" + airlineDto.toJson());
                    }
                    break;
                default: // Dump all items from said table
                    airlines = airlineDao.getAll();
                    for (AirlineDto airline : airlines) {
                        airlineDto = airline;
                        System.out.println("\nReturned Department(" + airlineDto.getAirlineId() + "):" + airlineDto.toJson());
                    }
            }
        } catch (Throwable th) {
            System.out.println(th.getMessage());
        }
    }
}
