package illumio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Firewall {
	
    static HashMap<Long, Boolean> map = new HashMap<Long, Boolean>();

    public static void main(String[] args) {
        try 
        {
            Firewall f = new Firewall("/path/to/fw.csv");
            
            boolean test1 = f.acceptPacket("outbound", "tcp", 20012, "192.168.10.11");
            boolean test2 = f.acceptPacket("inbound", "tcp",80,"192.168.102.201");
        
            System.out.println(test1);
            System.out.println(test2);


        }
        catch (FileNotFoundException e) {
            System.out.println("Exception occurred");
        }
    }

    public Firewall(String file) throws FileNotFoundException{
  
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            for(String line; (line = br.readLine()) != null;) {

                String [] rule = line.split(",");

                if (rule[2].contains("-")) { 
                	// port w/ range
                    String [] portRanges = rule[2].split("-");
                    int portMin = Integer.parseInt(portRanges[0]);
                    int portMax = Integer.parseInt(portRanges[1]);
                    int portRange = portMax - portMin;

                    if (rule[3].contains("-")) {
                    	// port w/ range, ipAddress w/ a range
                        String [] ipAddressRanges = rule[3].split("-");
                        long ipAddressMin = Long.parseLong(ipAddressRanges[0].replaceAll("\\.", ""));
                        long ipAddressMax = Long.parseLong(ipAddressRanges[1].replaceAll("\\.", ""));
                        long ipRange = ipAddressMax - ipAddressMin;

                        //add all possible ports and ips to map
                        for (int i = 0; i <= portRange; i++) {
                            for (int j = 0; j <= ipRange; j++) {
                                NetworkRule currRule = new NetworkRule(rule[0], rule[1], portMin + i, ipAddressMin + j);
                                map.put(currRule.hashCode, Boolean.TRUE);
                            }
                        }

                    }
                    else { 
                    	// port w/ range, ipAddress w/o range

                    	//add all possible ports and ips to map
                        for (int i = 0; i <= portRange; i++) {
                            NetworkRule currRule = new NetworkRule(rule[0],rule[1], portMin + i, rule[3]);
                            map.put(currRule.hashCode, Boolean.TRUE);
                        }
                    }

                }
                else { 
                	// port w/o range
                    if (rule[3].contains("-")) {
                    	// port w/o range, ipAddress w/ a range
                        String [] ipAddressRanges = rule[3].split("-");
                        long ipAddressMin = Long.parseLong(ipAddressRanges[0].replaceAll("\\.", ""));
                        long ipAddressMax = Long.parseLong(ipAddressRanges[1].replaceAll("\\.", ""));
                        long ipRange = ipAddressMax - ipAddressMin;

                      //add all possible ports and ips to map
                        for (int i = 0; i <= ipRange; i++) {
                            NetworkRule currRule = new NetworkRule(rule[0],rule[1],rule[2], ipAddressMin + i);
                            map.put(currRule.hashCode, Boolean.TRUE);
                        }
                    }
                    else { 
                    	// port w/o range, ipAddress w/o range
                        NetworkRule currRule = new NetworkRule(rule[0],rule[1],rule[2],rule[3]);
                        map.put(currRule.hashCode, Boolean.TRUE);
                    }

                }

            }
        }
        catch (IOException e) {
            System.out.println("Exception occurred");
        }

    }

    public boolean acceptPacket(String direction, String protocol, int port, String ipAddress) {
        NetworkRule rule = new NetworkRule(direction, protocol, port, ipAddress);
        if (this.map.containsKey(rule.hashCode)) {
            return true;
        }
        else {
            return false;
        }

    }

    public class NetworkRule {
        protected String direction;
        protected String protocol;
        protected int port;
        protected long ipAddress;
        protected long hashCode;

        public NetworkRule(String direction, String protocol, String port, String ipAddress) {
            this.direction = direction;
            this.protocol = protocol;
            this.port = Integer.parseInt(port);
            this.ipAddress = Long.parseLong(ipAddress.replaceAll("\\.", "")); //String ipAddress --> a number
            this.hashCode =  31 * (this.ipAddress + this.port + direction.hashCode() + protocol.hashCode()); 
        }

        public NetworkRule(String direction, String protocol, String port, long ipAddress) {
            this.direction = direction;
            this.protocol = protocol;
            this.port = Integer.parseInt(port);
            this.ipAddress = ipAddress;
            this.hashCode =  31 * (this.ipAddress + this.port + direction.hashCode() + protocol.hashCode()); 
        }

        public NetworkRule(String direction, String protocol, int port, long ipAddress) {
            this.direction = direction;
            this.protocol = protocol;
            this.port = port;
            this.ipAddress = ipAddress;
            this.hashCode =  31 * (this.ipAddress + this.port + direction.hashCode() + protocol.hashCode());
        }

        public NetworkRule(String direction, String protocol, int port, String ipAddress) {
            this.direction = direction;
            this.protocol = protocol;
            this.port = port;
            this.ipAddress = Long.parseLong(ipAddress.replaceAll("\\.", "")); 
            this.hashCode =  31 * (this.ipAddress + this.port + direction.hashCode() + protocol.hashCode()); 
        }

        public long gethashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NetworkRule)) return false;
            NetworkRule networkRule = (NetworkRule) o;
            return  direction == networkRule.direction && protocol == networkRule.protocol && port == networkRule.port && ipAddress == networkRule.ipAddress;
        }

        @Override
        public String toString() {
            return this.direction +  ", " + this.protocol + ", " + Integer.toString(this.port) + ", " + Long.toString(this.ipAddress);
        }


        public int hashCode() {
            long hash =  31 * (this.ipAddress + this.port + this.direction.hashCode() + this.protocol.hashCode());
            return Long.valueOf(hash).hashCode();
        }

    }
}

