package illumio;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FirewallTest {

    static Firewall fw = null;

    
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		fw = new Firewall("networkrules.csv");
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		boolean acceptPacket = fw.acceptPacket("outbound", "tcp", 20000, "192.168.10.11");
		assertEquals("Success", true, acceptPacket);
	}

}
