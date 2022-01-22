package com.diozero.aoc.algorithm;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LondonUnderground {
	public static void main(String[] args) {
		BaeldungDataSet.generateDataStructures();
	}

	/*-
	 * Transport for London Tube Map: https://content.tfl.gov.uk/standard-tube-map.pdf
	 *
	 * Java data set originally taken from:
	 * https://github.com/eugenp/tutorials/blob/master/algorithms-miscellaneous-2/src/test/java/com/baeldung/algorithms/astar/underground/RouteFinderIntegrationTest.java
	 *
	 * Corrections:
	 * - Knightsbridge (146) goes to Hyde Park Corner (133) and Gloucester Road (99) and not South Kensington (235)
	 * - South Kensignton (235) goes to Gloucester Road (99) and Sloane Square (228) and not Knightsbridge (146)
	 * - Gloucester Road (99) goes to High Street Kensington (122), Earl's Court (74), South Kensington (235), as well as Knightsbridge (146)
	 */
	public static Map<String, GraphNode<String, Station>> getGraph() {
		final Map<String, Station> stations = new HashMap<>();
		stations.put("Acton Town", new Station("Acton Town", 51.5028, -0.2801));
		stations.put("Aldgate", new Station("Aldgate", 51.5143, -0.0755));
		stations.put("Aldgate East", new Station("Aldgate East", 51.5154, -0.0726));
		stations.put("All Saints", new Station("All Saints", 51.5107, -0.013));
		stations.put("Alperton", new Station("Alperton", 51.5407, -0.2997));
		stations.put("Amersham", new Station("Amersham", 51.6736, -0.607));
		stations.put("Angel", new Station("Angel", 51.5322, -0.1058));
		stations.put("Archway", new Station("Archway", 51.5653, -0.1353));
		stations.put("Arnos Grove", new Station("Arnos Grove", 51.6164, -0.1331));
		stations.put("Arsenal", new Station("Arsenal", 51.5586, -0.1059));
		stations.put("Baker Street", new Station("Baker Street", 51.5226, -0.1571));
		stations.put("Balham", new Station("Balham", 51.4431, -0.1525));
		stations.put("Bank", new Station("Bank", 51.5133, -0.0886));
		stations.put("Barbican", new Station("Barbican", 51.5204, -0.0979));
		stations.put("Barking", new Station("Barking", 51.5396, 0.081));
		stations.put("Barkingside", new Station("Barkingside", 51.5856, 0.0887));
		stations.put("Barons Court", new Station("Barons Court", 51.4905, -0.2139));
		stations.put("Bayswater", new Station("Bayswater", 51.5121, -0.1879));
		stations.put("Beckton", new Station("Beckton", 51.5148, 0.0613));
		stations.put("Beckton Park", new Station("Beckton Park", 51.5087, 0.055));
		stations.put("Becontree", new Station("Becontree", 51.5403, 0.127));
		stations.put("Belsize Park", new Station("Belsize Park", 51.5504, -0.1642));
		stations.put("Bermondsey", new Station("Bermondsey", 51.4979, -0.0637));
		stations.put("Bethnal Green", new Station("Bethnal Green", 51.527, -0.0549));
		stations.put("Blackfriars", new Station("Blackfriars", 51.512, -0.1031));
		stations.put("Blackhorse Road", new Station("Blackhorse Road", 51.5867, -0.0417));
		stations.put("Blackwall", new Station("Blackwall", 51.5079, -0.0066));
		stations.put("Bond Street", new Station("Bond Street", 51.5142, -0.1494));
		stations.put("Borough", new Station("Borough", 51.5011, -0.0943));
		stations.put("Boston Manor", new Station("Boston Manor", 51.4956, -0.325));
		stations.put("Bounds Green", new Station("Bounds Green", 51.6071, -0.1243));
		stations.put("Bow Church", new Station("Bow Church", 51.5273, -0.0208));
		stations.put("Bow Road", new Station("Bow Road", 51.5269, -0.0247));
		stations.put("Brent Cross", new Station("Brent Cross", 51.5766, -0.2136));
		stations.put("Brixton", new Station("Brixton", 51.4627, -0.1145));
		stations.put("Bromley-By-Bow", new Station("Bromley-By-Bow", 51.5248, -0.0119));
		stations.put("Buckhurst Hill", new Station("Buckhurst Hill", 51.6266, 0.0471));
		stations.put("Burnt Oak", new Station("Burnt Oak", 51.6028, -0.2641));
		stations.put("Caledonian Road", new Station("Caledonian Road", 51.5481, -0.1188));
		stations.put("Camden Town", new Station("Camden Town", 51.5392, -0.1426));
		stations.put("Canada Water", new Station("Canada Water", 51.4982, -0.0502));
		stations.put("Canary Wharf", new Station("Canary Wharf", 51.5051, -0.0209));
		stations.put("Canning Town", new Station("Canning Town", 51.5147, 0.0082));
		stations.put("Cannon Street", new Station("Cannon Street", 51.5113, -0.0904));
		stations.put("Canons Park", new Station("Canons Park", 51.6078, -0.2947));
		stations.put("Chalfont & Latimer", new Station("Chalfont & Latimer", 51.6679, -0.561));
		stations.put("Chalk Farm", new Station("Chalk Farm", 51.5441, -0.1538));
		stations.put("Chancery Lane", new Station("Chancery Lane", 51.5185, -0.1111));
		stations.put("Charing Cross", new Station("Charing Cross", 51.508, -0.1247));
		stations.put("Chesham", new Station("Chesham", 51.7052, -0.611));
		stations.put("Chigwell", new Station("Chigwell", 51.6177, 0.0755));
		stations.put("Chiswick Park", new Station("Chiswick Park", 51.4946, -0.2678));
		stations.put("Chorleywood", new Station("Chorleywood", 51.6543, -0.5183));
		stations.put("Clapham Common", new Station("Clapham Common", 51.4618, -0.1384));
		stations.put("Clapham North", new Station("Clapham North", 51.4649, -0.1299));
		stations.put("Clapham South", new Station("Clapham South", 51.4527, -0.148));
		stations.put("Cockfosters", new Station("Cockfosters", 51.6517, -0.1496));
		stations.put("Colindale", new Station("Colindale", 51.5955, -0.2502));
		stations.put("Colliers Wood", new Station("Colliers Wood", 51.418, -0.1778));
		stations.put("Covent Garden", new Station("Covent Garden", 51.5129, -0.1243));
		stations.put("Crossharbour & London Arena", new Station("Crossharbour & London Arena", 51.4957, -0.0144));
		stations.put("Croxley", new Station("Croxley", 51.647, -0.4412));
		stations.put("Custom House", new Station("Custom House", 51.5095, 0.0276));
		stations.put("Cutty Sark", new Station("Cutty Sark", 51.4827, -0.0096));
		stations.put("Cyprus", new Station("Cyprus", 51.5085, 0.064));
		stations.put("Dagenham East", new Station("Dagenham East", 51.5443, 0.1655));
		stations.put("Dagenham Heathway", new Station("Dagenham Heathway", 51.5417, 0.1469));
		stations.put("Debden", new Station("Debden", 51.6455, 0.0838));
		stations.put("Deptford Bridge", new Station("Deptford Bridge", 51.474, -0.0216));
		stations.put("Devons Road", new Station("Devons Road", 51.5223, -0.0173));
		stations.put("Dollis Hill", new Station("Dollis Hill", 51.552, -0.2387));
		stations.put("Ealing Broadway", new Station("Ealing Broadway", 51.5152, -0.3017));
		stations.put("Ealing Common", new Station("Ealing Common", 51.5101, -0.2882));
		stations.put("Earl's Court", new Station("Earl's Court", 51.492, -0.1973));
		stations.put("Eastcote", new Station("Eastcote", 51.5765, -0.397));
		stations.put("East Acton", new Station("East Acton", 51.5168, -0.2474));
		stations.put("East Finchley", new Station("East Finchley", 51.5874, -0.165));
		stations.put("East Ham", new Station("East Ham", 51.5394, 0.0518));
		stations.put("East India", new Station("East India", 51.5093, -0.0021));
		stations.put("East Putney", new Station("East Putney", 51.4586, -0.2112));
		stations.put("Edgware", new Station("Edgware", 51.6137, -0.275));
		stations.put("Edgware Road (B)", new Station("Edgware Road (B)", 51.5199, -0.1679));
		stations.put("Edgware Road (C)", new Station("Edgware Road (C)", 51.5203, -0.17));
		stations.put("Elephant & Castle", new Station("Elephant & Castle", 51.4943, -0.1001));
		stations.put("Elm Park", new Station("Elm Park", 51.5496, 0.1977));
		stations.put("Elverson Road", new Station("Elverson Road", 51.4693, -0.0174));
		stations.put("Embankment", new Station("Embankment", 51.5074, -0.1223));
		stations.put("Epping", new Station("Epping", 51.6937, 0.1139));
		stations.put("Euston", new Station("Euston", 51.5282, -0.1337));
		stations.put("Euston Square", new Station("Euston Square", 51.526, -0.1359));
		stations.put("Fairlop", new Station("Fairlop", 51.596, 0.0912));
		stations.put("Farringdon", new Station("Farringdon", 51.5203, -0.1053));
		stations.put("Finchley Central", new Station("Finchley Central", 51.6012, -0.1932));
		stations.put("Finchley Road", new Station("Finchley Road", 51.5472, -0.1803));
		stations.put("Finsbury Park", new Station("Finsbury Park", 51.5642, -0.1065));
		stations.put("Fulham Broadway", new Station("Fulham Broadway", 51.4804, -0.195));
		stations.put("Gallions Reach", new Station("Gallions Reach", 51.5096, 0.0716));
		stations.put("Gants Hill", new Station("Gants Hill", 51.5765, 0.0663));
		stations.put("Gloucester Road", new Station("Gloucester Road", 51.4945, -0.1829));
		stations.put("Golders Green", new Station("Golders Green", 51.5724, -0.1941));
		stations.put("Goldhawk Road", new Station("Goldhawk Road", 51.5018, -0.2267));
		stations.put("Goodge Street", new Station("Goodge Street", 51.5205, -0.1347));
		stations.put("Grange Hill", new Station("Grange Hill", 51.6132, 0.0923));
		stations.put("Great Portland Street", new Station("Great Portland Street", 51.5238, -0.1439));
		stations.put("Greenford", new Station("Greenford", 51.5423, -0.3456));
		stations.put("Greenwich", new Station("Greenwich", 51.4781, -0.0149));
		stations.put("Green Park", new Station("Green Park", 51.5067, -0.1428));
		stations.put("Gunnersbury", new Station("Gunnersbury", 51.4915, -0.2754));
		stations.put("Hainault", new Station("Hainault", 51.603, 0.0933));
		stations.put("Hammersmith", new Station("Hammersmith", 51.4936, -0.2251));
		stations.put("Hampstead", new Station("Hampstead", 51.5568, -0.178));
		stations.put("Hanger Lane", new Station("Hanger Lane", 51.5302, -0.2933));
		stations.put("Harlesden", new Station("Harlesden", 51.5362, -0.2575));
		stations.put("Harrow & Wealdston", new Station("Harrow & Wealdston", 51.5925, -0.3351));
		stations.put("Harrow-on-the-Hill", new Station("Harrow-on-the-Hill", 51.5793, -0.3366));
		stations.put("Hatton Cross", new Station("Hatton Cross", 51.4669, -0.4227));
		stations.put("Heathrow Terminals 1, 2 & 3", new Station("Heathrow Terminals 1, 2 & 3", 51.4713, -0.4524));
		stations.put("Heathrow Terminal 4", new Station("Heathrow Terminal 4", 51.4598, -0.4476));
		stations.put("Hendon Central", new Station("Hendon Central", 51.5829, -0.2259));
		stations.put("Heron Quays", new Station("Heron Quays", 51.5033, -0.0215));
		stations.put("High Barnet", new Station("High Barnet", 51.6503, -0.1943));
		stations.put("High Street Kensington", new Station("High Street Kensington", 51.5009, -0.1925));
		stations.put("Highbury & Islington", new Station("Highbury & Islington", 51.546, -0.104));
		stations.put("Highgate", new Station("Highgate", 51.5777, -0.1458));
		stations.put("Hillingdon", new Station("Hillingdon", 51.5538, -0.4499));
		stations.put("Holborn", new Station("Holborn", 51.5174, -0.12));
		stations.put("Holland Park", new Station("Holland Park", 51.5075, -0.206));
		stations.put("Holloway Road", new Station("Holloway Road", 51.5526, -0.1132));
		stations.put("Hornchurch", new Station("Hornchurch", 51.5539, 0.2184));
		stations.put("Hounslow Central", new Station("Hounslow Central", 51.4713, -0.3665));
		stations.put("Hounslow East", new Station("Hounslow East", 51.4733, -0.3564));
		stations.put("Hounslow West", new Station("Hounslow West", 51.4734, -0.3855));
		stations.put("Hyde Park Corner", new Station("Hyde Park Corner", 51.5027, -0.1527));
		stations.put("Ickenham", new Station("Ickenham", 51.5619, -0.4421));
		stations.put("Island Gardens", new Station("Island Gardens", 51.4871, -0.0101));
		stations.put("Kennington", new Station("Kennington", 51.4884, -0.1053));
		stations.put("Kensal Green", new Station("Kensal Green", 51.5304, -0.225));
		stations.put("Kensington (Olympia)", new Station("Kensington (Olympia)", 51.4983, -0.2106));
		stations.put("Kentish Town", new Station("Kentish Town", 51.5507, -0.1402));
		stations.put("Kenton", new Station("Kenton", 51.5816, -0.3162));
		stations.put("Kew Gardens", new Station("Kew Gardens", 51.477, -0.285));
		stations.put("Kilburn", new Station("Kilburn", 51.5471, -0.2047));
		stations.put("Kilburn Park", new Station("Kilburn Park", 51.5351, -0.1939));
		stations.put("Kingsbury", new Station("Kingsbury", 51.5846, -0.2786));
		stations.put("King's Cross St. Pancras", new Station("King's Cross St. Pancras", 51.5308, -0.1238));
		stations.put("Knightsbridge", new Station("Knightsbridge", 51.5015, -0.1607));
		stations.put("Ladbroke Grove", new Station("Ladbroke Grove", 51.5172, -0.2107));
		stations.put("Lambeth North", new Station("Lambeth North", 51.4991, -0.1115));
		stations.put("Lancaster Gate", new Station("Lancaster Gate", 51.5119, -0.1756));
		stations.put("Latimer Road", new Station("Latimer Road", 51.5139, -0.2172));
		stations.put("Leicester Square", new Station("Leicester Square", 51.5113, -0.1281));
		stations.put("Lewisham", new Station("Lewisham", 51.4657, -0.0142));
		stations.put("Leyton", new Station("Leyton", 51.5566, -0.0053));
		stations.put("Leytonstone", new Station("Leytonstone", 51.5683, 0.0083));
		stations.put("Limehouse", new Station("Limehouse", 51.5123, -0.0396));
		stations.put("Liverpool Street", new Station("Liverpool Street", 51.5178, -0.0823));
		stations.put("London Bridge", new Station("London Bridge", 51.5052, -0.0864));
		stations.put("Loughton", new Station("Loughton", 51.6412, 0.0558));
		stations.put("Maida Vale", new Station("Maida Vale", 51.53, -0.1854));
		stations.put("Manor House", new Station("Manor House", 51.5712, -0.0958));
		stations.put("Mansion House", new Station("Mansion House", 51.5122, -0.094));
		stations.put("Marble Arch", new Station("Marble Arch", 51.5136, -0.1586));
		stations.put("Marylebone", new Station("Marylebone", 51.5225, -0.1631));
		stations.put("Mile End", new Station("Mile End", 51.5249, -0.0332));
		stations.put("Mill Hill East", new Station("Mill Hill East", 51.6082, -0.2103));
		stations.put("Monument", new Station("Monument", 51.5108, -0.0863));
		stations.put("Moorgate", new Station("Moorgate", 51.5186, -0.0886));
		stations.put("Moor Park", new Station("Moor Park", 51.6294, -0.432));
		stations.put("Morden", new Station("Morden", 51.4022, -0.1948));
		stations.put("Mornington Crescent", new Station("Mornington Crescent", 51.5342, -0.1387));
		stations.put("Mudchute", new Station("Mudchute", 51.4902, -0.0145));
		stations.put("Neasden", new Station("Neasden", 51.5542, -0.2503));
		stations.put("Newbury Park", new Station("Newbury Park", 51.5756, 0.0899));
		stations.put("New Cross", new Station("New Cross", 51.4767, -0.0327));
		stations.put("New Cross Gate", new Station("New Cross Gate", 51.4757, -0.0402));
		stations.put("Northfields", new Station("Northfields", 51.4995, -0.3142));
		stations.put("Northolt", new Station("Northolt", 51.5483, -0.3687));
		stations.put("Northwick Park", new Station("Northwick Park", 51.5784, -0.3184));
		stations.put("Northwood", new Station("Northwood", 51.6111, -0.424));
		stations.put("Northwood Hills", new Station("Northwood Hills", 51.6004, -0.4092));
		stations.put("North Acton", new Station("North Acton", 51.5237, -0.2597));
		stations.put("North Ealing", new Station("North Ealing", 51.5175, -0.2887));
		stations.put("North Greenwich", new Station("North Greenwich", 51.5005, 0.0039));
		stations.put("North Harrow", new Station("North Harrow", 51.5846, -0.3626));
		stations.put("North Wembley", new Station("North Wembley", 51.5621, -0.3034));
		stations.put("Notting Hill Gate", new Station("Notting Hill Gate", 51.5094, -0.1967));
		stations.put("Oakwood", new Station("Oakwood", 51.6476, -0.1318));
		stations.put("Old Street", new Station("Old Street", 51.5263, -0.0873));
		stations.put("Osterley", new Station("Osterley", 51.4813, -0.3522));
		stations.put("Oval", new Station("Oval", 51.4819, -0.113));
		stations.put("Oxford Circus", new Station("Oxford Circus", 51.515, -0.1415));
		stations.put("Paddington", new Station("Paddington", 51.5154, -0.1755));
		stations.put("Park Royal", new Station("Park Royal", 51.527, -0.2841));
		stations.put("Parsons Green", new Station("Parsons Green", 51.4753, -0.2011));
		stations.put("Perivale", new Station("Perivale", 51.5366, -0.3232));
		stations.put("Picadilly Circus", new Station("Picadilly Circus", 51.5098, -0.1342));
		stations.put("Pimlico", new Station("Pimlico", 51.4893, -0.1334));
		stations.put("Pinner", new Station("Pinner", 51.5926, -0.3805));
		stations.put("Plaistow", new Station("Plaistow", 51.5313, 0.0172));
		stations.put("Poplar", new Station("Poplar", 51.5077, -0.0173));
		stations.put("Preston Road", new Station("Preston Road", 51.572, -0.2954));
		stations.put("Prince Regent", new Station("Prince Regent", 51.5093, 0.0336));
		stations.put("Pudding Mill Lane", new Station("Pudding Mill Lane", 51.5343, -0.0139));
		stations.put("Putney Bridge", new Station("Putney Bridge", 51.4682, -0.2089));
		stations.put("Queen's Park", new Station("Queen's Park", 51.5341, -0.2047));
		stations.put("Queensbury", new Station("Queensbury", 51.5942, -0.2861));
		stations.put("Queensway", new Station("Queensway", 51.5107, -0.1877));
		stations.put("Ravenscourt Park", new Station("Ravenscourt Park", 51.4942, -0.2359));
		stations.put("Rayners Lane", new Station("Rayners Lane", 51.5753, -0.3714));
		stations.put("Redbridge", new Station("Redbridge", 51.5763, 0.0454));
		stations.put("Regent's Park", new Station("Regent's Park", 51.5234, -0.1466));
		stations.put("Richmond", new Station("Richmond", 51.4633, -0.3013));
		stations.put("Rickmansworth", new Station("Rickmansworth", 51.6404, -0.4733));
		stations.put("Roding Valley", new Station("Roding Valley", 51.6171, 0.0439));
		stations.put("Rotherhithe", new Station("Rotherhithe", 51.501, -0.0525));
		stations.put("Royal Albert", new Station("Royal Albert", 51.5084, 0.0465));
		stations.put("Royal Oak", new Station("Royal Oak", 51.519, -0.188));
		stations.put("Royal Victoria", new Station("Royal Victoria", 51.5091, 0.0181));
		stations.put("Ruislip", new Station("Ruislip", 51.5715, -0.4213));
		stations.put("Ruislip Gardens", new Station("Ruislip Gardens", 51.5606, -0.4103));
		stations.put("Ruislip Manor", new Station("Ruislip Manor", 51.5732, -0.4125));
		stations.put("Russell Square", new Station("Russell Square", 51.523, -0.1244));
		stations.put("Seven Sisters", new Station("Seven Sisters", 51.5822, -0.0749));
		stations.put("Shadwell", new Station("Shadwell", 51.5117, -0.056));
		stations.put("Shepherd's Bush (C)", new Station("Shepherd's Bush (C)", 51.5046, -0.2187));
		stations.put("Shepherd's Bush (H)", new Station("Shepherd's Bush (H)", 51.5058, -0.2265));
		stations.put("Shoreditch", new Station("Shoreditch", 51.5227, -0.0708));
		stations.put("Sloane Square", new Station("Sloane Square", 51.4924, -0.1565));
		stations.put("Snaresbrook", new Station("Snaresbrook", 51.5808, 0.0216));
		stations.put("Southfields", new Station("Southfields", 51.4454, -0.2066));
		stations.put("Southgate", new Station("Southgate", 51.6322, -0.128));
		stations.put("Southwark", new Station("Southwark", 51.501, -0.1052));
		stations.put("South Ealing", new Station("South Ealing", 51.5011, -0.3072));
		stations.put("South Harrow", new Station("South Harrow", 51.5646, -0.3521));
		stations.put("South Kensington", new Station("South Kensington", 51.4941, -0.1738));
		stations.put("South Kenton", new Station("South Kenton", 51.5701, -0.3081));
		stations.put("South Quay", new Station("South Quay", 51.5007, -0.0191));
		stations.put("South Ruislip", new Station("South Ruislip", 51.5569, -0.3988));
		stations.put("South Wimbledon", new Station("South Wimbledon", 51.4154, -0.1919));
		stations.put("South Woodford", new Station("South Woodford", 51.5917, 0.0275));
		stations.put("Stamford Brook", new Station("Stamford Brook", 51.495, -0.2459));
		stations.put("Stanmore", new Station("Stanmore", 51.6194, -0.3028));
		stations.put("Stepney Green", new Station("Stepney Green", 51.5221, -0.047));
		stations.put("Stockwell", new Station("Stockwell", 51.4723, -0.123));
		stations.put("Stonebridge Park", new Station("Stonebridge Park", 51.5439, -0.2759));
		stations.put("Stratford", new Station("Stratford", 51.5416, -0.0042));
		stations.put("St. James's Park", new Station("St. James's Park", 51.4994, -0.1335));
		stations.put("St. John's Wood", new Station("St. John's Wood", 51.5347, -0.174));
		stations.put("St. Paul's", new Station("St. Paul's", 51.5146, -0.0973));
		stations.put("Sudbury Hill", new Station("Sudbury Hill", 51.5569, -0.3366));
		stations.put("Sudbury Town", new Station("Sudbury Town", 51.5507, -0.3156));
		stations.put("Surrey Quays", new Station("Surrey Quays", 51.4933, -0.0478));
		stations.put("Swiss Cottage", new Station("Swiss Cottage", 51.5432, -0.1738));
		stations.put("Temple", new Station("Temple", 51.5111, -0.1141));
		stations.put("Theydon Bois", new Station("Theydon Bois", 51.6717, 0.1033));
		stations.put("Tooting Bec", new Station("Tooting Bec", 51.4361, -0.1598));
		stations.put("Tooting Broadway", new Station("Tooting Broadway", 51.4275, -0.168));
		stations.put("Tottenham Court Road", new Station("Tottenham Court Road", 51.5165, -0.131));
		stations.put("Tottenham Hale", new Station("Tottenham Hale", 51.5882, -0.0594));
		stations.put("Totteridge & Whetstone", new Station("Totteridge & Whetstone", 51.6302, -0.1791));
		stations.put("Tower Gateway", new Station("Tower Gateway", 51.5106, -0.0743));
		stations.put("Tower Hill", new Station("Tower Hill", 51.5098, -0.0766));
		stations.put("Tufnell Park", new Station("Tufnell Park", 51.5567, -0.1374));
		stations.put("Turnham Green", new Station("Turnham Green", 51.4951, -0.2547));
		stations.put("Turnpike Lane", new Station("Turnpike Lane", 51.5904, -0.1028));
		stations.put("Upminster", new Station("Upminster", 51.559, 0.251));
		stations.put("Upminster Bridge", new Station("Upminster Bridge", 51.5582, 0.2343));
		stations.put("Upney", new Station("Upney", 51.5385, 0.1014));
		stations.put("Upton Park", new Station("Upton Park", 51.5352, 0.0343));
		stations.put("Uxbridge", new Station("Uxbridge", 51.5463, -0.4786));
		stations.put("Vauxhall", new Station("Vauxhall", 51.4861, -0.1253));
		stations.put("Victoria", new Station("Victoria", 51.4965, -0.1447));
		stations.put("Walthamstow Central", new Station("Walthamstow Central", 51.583, -0.0195));
		stations.put("Wanstead", new Station("Wanstead", 51.5775, 0.0288));
		stations.put("Wapping", new Station("Wapping", 51.5043, -0.0558));
		stations.put("Warren Street", new Station("Warren Street", 51.5247, -0.1384));
		stations.put("Warwick Avenue", new Station("Warwick Avenue", 51.5235, -0.1835));
		stations.put("Waterloo", new Station("Waterloo", 51.5036, -0.1143));
		stations.put("Watford", new Station("Watford", 51.6573, -0.4177));
		stations.put("Wembley Central", new Station("Wembley Central", 51.5519, -0.2963));
		stations.put("Wembley Park", new Station("Wembley Park", 51.5635, -0.2795));
		stations.put("Westbourne Park", new Station("Westbourne Park", 51.521, -0.2011));
		stations.put("Westferry", new Station("Westferry", 51.5097, -0.0265));
		stations.put("Westminster", new Station("Westminster", 51.501, -0.1254));
		stations.put("West Acton", new Station("West Acton", 51.518, -0.2809));
		stations.put("West Brompton", new Station("West Brompton", 51.4872, -0.1953));
		stations.put("West Finchley", new Station("West Finchley", 51.6095, -0.1883));
		stations.put("West Ham", new Station("West Ham", 51.5287, 0.0056));
		stations.put("West Hampstead", new Station("West Hampstead", 51.5469, -0.1906));
		stations.put("West Harrow", new Station("West Harrow", 51.5795, -0.3533));
		stations.put("West India Quay", new Station("West India Quay", 51.507, -0.0203));
		stations.put("West Kensington", new Station("West Kensington", 51.4907, -0.2065));
		stations.put("West Ruislip", new Station("West Ruislip", 51.5696, -0.4376));
		stations.put("Whitechapel", new Station("Whitechapel", 51.5194, -0.0612));
		stations.put("White City", new Station("White City", 51.512, -0.2239));
		stations.put("Willesden Green", new Station("Willesden Green", 51.5492, -0.2215));
		stations.put("Willesden Junction", new Station("Willesden Junction", 51.5326, -0.2478));
		stations.put("Wimbledon", new Station("Wimbledon", 51.4214, -0.2064));
		stations.put("Wimbledon Park", new Station("Wimbledon Park", 51.4343, -0.1992));
		stations.put("Woodford", new Station("Woodford", 51.607, 0.0341));
		stations.put("Woodside Park", new Station("Woodside Park", 51.6179, -0.1856));
		stations.put("Wood Green", new Station("Wood Green", 51.5975, -0.1097));

		final Map<String, Set<String>> connections = new HashMap<>();
		connections.put("Acton Town", Stream.of("Chiswick Park", "Turnham Green", "Ealing Common", "South Ealing")
				.collect(Collectors.toSet()));
		connections.put("Aldgate", Stream.of("Tower Hill", "Liverpool Street").collect(Collectors.toSet()));
		connections.put("Aldgate East",
				Stream.of("Tower Hill", "Whitechapel", "Liverpool Street").collect(Collectors.toSet()));
		connections.put("All Saints", Stream.of("Devons Road", "Poplar").collect(Collectors.toSet()));
		connections.put("Alperton", Stream.of("Park Royal", "Sudbury Town").collect(Collectors.toSet()));
		connections.put("Amersham", Stream.of("Chalfont & Latimer").collect(Collectors.toSet()));
		connections.put("Angel", Stream.of("King's Cross St. Pancras", "Old Street").collect(Collectors.toSet()));
		connections.put("Archway", Stream.of("Tufnell Park", "Highgate").collect(Collectors.toSet()));
		connections.put("Arnos Grove", Stream.of("Southgate", "Bounds Green").collect(Collectors.toSet()));
		connections.put("Arsenal", Stream.of("Holloway Road", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Baker Street",
				Stream.of("Marylebone", "Regent's Park", "Edgware Road (C)", "Great Portland Street", "St. John's Wood",
						"Bond Street", "Finchley Road").collect(Collectors.toSet()));
		connections.put("Balham", Stream.of("Tooting Bec", "Clapham South").collect(Collectors.toSet()));
		connections.put("Bank",
				Stream.of("Shadwell", "Waterloo", "Moorgate", "St. Paul's", "Liverpool Street", "London Bridge")
						.collect(Collectors.toSet()));
		connections.put("Barbican", Stream.of("Moorgate", "Farringdon").collect(Collectors.toSet()));
		connections.put("Barking", Stream.of("Upney", "East Ham").collect(Collectors.toSet()));
		connections.put("Barkingside", Stream.of("Fairlop", "Newbury Park").collect(Collectors.toSet()));
		connections.put("Barons Court",
				Stream.of("West Kensington", "Earl's Court", "Hammersmith").collect(Collectors.toSet()));
		connections.put("Bayswater", Stream.of("Paddington", "Notting Hill Gate").collect(Collectors.toSet()));
		connections.put("Beckton", Stream.of("Gallions Reach").collect(Collectors.toSet()));
		connections.put("Beckton Park", Stream.of("Cyprus", "Royal Albert").collect(Collectors.toSet()));
		connections.put("Becontree", Stream.of("Dagenham Heathway", "Upney").collect(Collectors.toSet()));
		connections.put("Belsize Park", Stream.of("Chalk Farm", "Hampstead").collect(Collectors.toSet()));
		connections.put("Bermondsey", Stream.of("Canada Water", "London Bridge").collect(Collectors.toSet()));
		connections.put("Bethnal Green", Stream.of("Mile End", "Liverpool Street").collect(Collectors.toSet()));
		connections.put("Blackfriars", Stream.of("Mansion House", "Temple").collect(Collectors.toSet()));
		connections.put("Blackhorse Road",
				Stream.of("Walthamstow Central", "Tottenham Hale").collect(Collectors.toSet()));
		connections.put("Blackwall", Stream.of("Poplar", "East India").collect(Collectors.toSet()));
		connections.put("Bond Street",
				Stream.of("Marble Arch", "Baker Street", "Green Park", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Borough", Stream.of("Elephant & Castle", "London Bridge").collect(Collectors.toSet()));
		connections.put("Boston Manor", Stream.of("Northfields", "Osterley").collect(Collectors.toSet()));
		connections.put("Bounds Green", Stream.of("Arnos Grove", "Wood Green").collect(Collectors.toSet()));
		connections.put("Bow Church", Stream.of("Devons Road", "Pudding Mill Lane").collect(Collectors.toSet()));
		connections.put("Bow Road", Stream.of("Bromley-By-Bow", "Mile End").collect(Collectors.toSet()));
		connections.put("Brent Cross", Stream.of("Golders Green", "Hendon Central").collect(Collectors.toSet()));
		connections.put("Brixton", Stream.of("Stockwell").collect(Collectors.toSet()));
		connections.put("Bromley-By-Bow", Stream.of("West Ham", "Bow Road").collect(Collectors.toSet()));
		connections.put("Buckhurst Hill", Stream.of("Woodford", "Loughton").collect(Collectors.toSet()));
		connections.put("Burnt Oak", Stream.of("Edgware", "Colindale").collect(Collectors.toSet()));
		connections.put("Caledonian Road",
				Stream.of("Holloway Road", "King's Cross St. Pancras").collect(Collectors.toSet()));
		connections.put("Camden Town",
				Stream.of("Euston", "Mornington Crescent", "Kentish Town", "Chalk Farm").collect(Collectors.toSet()));
		connections.put("Canada Water",
				Stream.of("Rotherhithe", "Bermondsey", "Canary Wharf", "Surrey Quays").collect(Collectors.toSet()));
		connections.put("Canary Wharf", Stream.of("West India Quay", "North Greenwich", "Heron Quays", "Canada Water")
				.collect(Collectors.toSet()));
		connections.put("Canning Town",
				Stream.of("West Ham", "North Greenwich", "Royal Victoria", "East India").collect(Collectors.toSet()));
		connections.put("Cannon Street", Stream.of("Mansion House", "Monument").collect(Collectors.toSet()));
		connections.put("Canons Park", Stream.of("Stanmore", "Queensbury").collect(Collectors.toSet()));
		connections.put("Chalfont & Latimer",
				Stream.of("Chesham", "Chorleywood", "Amersham").collect(Collectors.toSet()));
		connections.put("Chalk Farm", Stream.of("Belsize Park", "Camden Town").collect(Collectors.toSet()));
		connections.put("Chancery Lane", Stream.of("St. Paul's", "Holborn").collect(Collectors.toSet()));
		connections.put("Charing Cross",
				Stream.of("Picadilly Circus", "Embankment", "Leicester Square").collect(Collectors.toSet()));
		connections.put("Chesham", Stream.of("Chalfont & Latimer").collect(Collectors.toSet()));
		connections.put("Chigwell", Stream.of("Roding Valley", "Grange Hill").collect(Collectors.toSet()));
		connections.put("Chiswick Park", Stream.of("Acton Town", "Turnham Green").collect(Collectors.toSet()));
		connections.put("Chorleywood", Stream.of("Rickmansworth", "Chalfont & Latimer").collect(Collectors.toSet()));
		connections.put("Clapham Common", Stream.of("Clapham North", "Clapham South").collect(Collectors.toSet()));
		connections.put("Clapham North", Stream.of("Stockwell", "Clapham Common").collect(Collectors.toSet()));
		connections.put("Clapham South", Stream.of("Clapham Common", "Balham").collect(Collectors.toSet()));
		connections.put("Cockfosters", Stream.of("Oakwood").collect(Collectors.toSet()));
		connections.put("Colindale", Stream.of("Burnt Oak", "Hendon Central").collect(Collectors.toSet()));
		connections.put("Colliers Wood", Stream.of("Tooting Broadway", "South Wimbledon").collect(Collectors.toSet()));
		connections.put("Covent Garden", Stream.of("Leicester Square", "Holborn").collect(Collectors.toSet()));
		connections.put("Crossharbour & London Arena", Stream.of("Mudchute", "South Quay").collect(Collectors.toSet()));
		connections.put("Croxley", Stream.of("Watford", "Moor Park").collect(Collectors.toSet()));
		connections.put("Custom House", Stream.of("Prince Regent", "Royal Victoria").collect(Collectors.toSet()));
		connections.put("Cutty Sark", Stream.of("Island Gardens", "Greenwich").collect(Collectors.toSet()));
		connections.put("Cyprus", Stream.of("Gallions Reach", "Beckton Park").collect(Collectors.toSet()));
		connections.put("Dagenham East", Stream.of("Dagenham Heathway", "Elm Park").collect(Collectors.toSet()));
		connections.put("Dagenham Heathway", Stream.of("Dagenham East", "Becontree").collect(Collectors.toSet()));
		connections.put("Debden", Stream.of("Loughton", "Theydon Bois").collect(Collectors.toSet()));
		connections.put("Deptford Bridge", Stream.of("Elverson Road", "Greenwich").collect(Collectors.toSet()));
		connections.put("Devons Road", Stream.of("Bow Church", "All Saints").collect(Collectors.toSet()));
		connections.put("Dollis Hill", Stream.of("Willesden Green", "Neasden").collect(Collectors.toSet()));
		connections.put("Ealing Broadway", Stream.of("Ealing Common", "West Acton").collect(Collectors.toSet()));
		connections.put("Ealing Common",
				Stream.of("Acton Town", "North Ealing", "Ealing Broadway").collect(Collectors.toSet()));
		connections.put("Earl's Court", Stream.of("Barons Court", "Gloucester Road", "West Kensington",
				"High Street Kensington", "Kensington (Olympia)", "West Brompton").collect(Collectors.toSet()));
		connections.put("Eastcote", Stream.of("Rayners Lane", "Ruislip Manor").collect(Collectors.toSet()));
		connections.put("East Acton", Stream.of("North Acton", "White City").collect(Collectors.toSet()));
		connections.put("East Finchley", Stream.of("Highgate", "Finchley Central").collect(Collectors.toSet()));
		connections.put("East Ham", Stream.of("Upton Park", "Barking").collect(Collectors.toSet()));
		connections.put("East India", Stream.of("Blackwall", "Canning Town").collect(Collectors.toSet()));
		connections.put("East Putney", Stream.of("Southfields", "Putney Bridge").collect(Collectors.toSet()));
		connections.put("Edgware", Stream.of("Burnt Oak").collect(Collectors.toSet()));
		connections.put("Edgware Road (B)", Stream.of("Paddington", "Marylebone").collect(Collectors.toSet()));
		connections.put("Edgware Road (C)", Stream.of("Paddington", "Baker Street").collect(Collectors.toSet()));
		connections.put("Elephant & Castle",
				Stream.of("Lambeth North", "Kennington", "Borough").collect(Collectors.toSet()));
		connections.put("Elm Park", Stream.of("Hornchurch", "Dagenham East").collect(Collectors.toSet()));
		connections.put("Elverson Road", Stream.of("Deptford Bridge", "Lewisham").collect(Collectors.toSet()));
		connections.put("Embankment",
				Stream.of("Charing Cross", "Waterloo", "Westminster", "Temple").collect(Collectors.toSet()));
		connections.put("Epping", Stream.of("Theydon Bois").collect(Collectors.toSet()));
		connections.put("Euston",
				Stream.of("King's Cross St. Pancras", "Warren Street", "Camden Town", "Mornington Crescent")
						.collect(Collectors.toSet()));
		connections.put("Euston Square",
				Stream.of("King's Cross St. Pancras", "Great Portland Street").collect(Collectors.toSet()));
		connections.put("Fairlop", Stream.of("Barkingside", "Hainault").collect(Collectors.toSet()));
		connections.put("Farringdon", Stream.of("King's Cross St. Pancras", "Barbican").collect(Collectors.toSet()));
		connections.put("Finchley Central",
				Stream.of("Mill Hill East", "East Finchley", "West Finchley").collect(Collectors.toSet()));
		connections.put("Finchley Road", Stream.of("West Hampstead", "Wembley Park", "Baker Street", "Swiss Cottage")
				.collect(Collectors.toSet()));
		connections.put("Finsbury Park", Stream.of("Manor House", "Arsenal", "Highbury & Islington", "Seven Sisters")
				.collect(Collectors.toSet()));
		connections.put("Fulham Broadway", Stream.of("Parsons Green", "West Brompton").collect(Collectors.toSet()));
		connections.put("Gallions Reach", Stream.of("Cyprus", "Beckton").collect(Collectors.toSet()));
		connections.put("Gants Hill", Stream.of("Redbridge", "Newbury Park").collect(Collectors.toSet()));
		connections.put("Gloucester Road",
				Stream.of("High Street Kensington", "Earl's Court", "South Kensington", "Knightsbridge")
						.collect(Collectors.toSet()));
		connections.put("Golders Green", Stream.of("Brent Cross", "Hampstead").collect(Collectors.toSet()));
		connections.put("Goldhawk Road", Stream.of("Shepherd's Bush (H)", "Hammersmith").collect(Collectors.toSet()));
		connections.put("Goodge Street",
				Stream.of("Tottenham Court Road", "Warren Street").collect(Collectors.toSet()));
		connections.put("Grange Hill", Stream.of("Chigwell", "Hainault").collect(Collectors.toSet()));
		connections.put("Great Portland Street",
				Stream.of("Euston Square", "Baker Street").collect(Collectors.toSet()));
		connections.put("Greenford", Stream.of("Northolt", "Perivale").collect(Collectors.toSet()));
		connections.put("Greenwich", Stream.of("Cutty Sark", "Deptford Bridge").collect(Collectors.toSet()));
		connections.put("Green Park", Stream
				.of("Victoria", "Picadilly Circus", "Hyde Park Corner", "Bond Street", "Westminster", "Oxford Circus")
				.collect(Collectors.toSet()));
		connections.put("Gunnersbury", Stream.of("Turnham Green", "Kew Gardens").collect(Collectors.toSet()));
		connections.put("Hainault", Stream.of("Grange Hill", "Fairlop").collect(Collectors.toSet()));
		connections.put("Hammersmith", Stream.of("Ravenscourt Park", "Barons Court", "Goldhawk Road", "Turnham Green")
				.collect(Collectors.toSet()));
		connections.put("Hampstead", Stream.of("Golders Green", "Belsize Park").collect(Collectors.toSet()));
		connections.put("Hanger Lane", Stream.of("Perivale", "North Acton").collect(Collectors.toSet()));
		connections.put("Harlesden", Stream.of("Stonebridge Park", "Willesden Junction").collect(Collectors.toSet()));
		connections.put("Harrow & Wealdston", Stream.of("Kenton").collect(Collectors.toSet()));
		connections.put("Harrow-on-the-Hill",
				Stream.of("Northwick Park", "West Harrow", "North Harrow").collect(Collectors.toSet()));
		connections.put("Hatton Cross", Stream.of("Hounslow West", "Heathrow Terminals 1, 2 & 3", "Heathrow Terminal 4")
				.collect(Collectors.toSet()));
		connections.put("Heathrow Terminals 1, 2 & 3",
				Stream.of("Hatton Cross", "Heathrow Terminal 4").collect(Collectors.toSet()));
		connections.put("Heathrow Terminal 4",
				Stream.of("Hatton Cross", "Heathrow Terminals 1, 2 & 3").collect(Collectors.toSet()));
		connections.put("Hendon Central", Stream.of("Brent Cross", "Colindale").collect(Collectors.toSet()));
		connections.put("Heron Quays", Stream.of("Canary Wharf", "South Quay").collect(Collectors.toSet()));
		connections.put("High Barnet", Stream.of("Totteridge & Whetstone").collect(Collectors.toSet()));
		connections.put("High Street Kensington",
				Stream.of("Gloucester Road", "Notting Hill Gate", "Earl's Court").collect(Collectors.toSet()));
		connections.put("Highbury & Islington",
				Stream.of("King's Cross St. Pancras", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Highgate", Stream.of("Archway", "East Finchley").collect(Collectors.toSet()));
		connections.put("Hillingdon", Stream.of("Ickenham", "Uxbridge").collect(Collectors.toSet()));
		connections.put("Holborn", Stream.of("Chancery Lane", "Tottenham Court Road", "Covent Garden", "Russell Square")
				.collect(Collectors.toSet()));
		connections.put("Holland Park",
				Stream.of("Shepherd's Bush (C)", "Notting Hill Gate").collect(Collectors.toSet()));
		connections.put("Holloway Road", Stream.of("Caledonian Road", "Arsenal").collect(Collectors.toSet()));
		connections.put("Hornchurch", Stream.of("Elm Park", "Upminster Bridge").collect(Collectors.toSet()));
		connections.put("Hounslow Central", Stream.of("Hounslow East", "Hounslow West").collect(Collectors.toSet()));
		connections.put("Hounslow East", Stream.of("Hounslow Central", "Osterley").collect(Collectors.toSet()));
		connections.put("Hounslow West", Stream.of("Hounslow Central", "Hatton Cross").collect(Collectors.toSet()));
		connections.put("Hyde Park Corner", Stream.of("Knightsbridge", "Green Park").collect(Collectors.toSet()));
		connections.put("Ickenham", Stream.of("Ruislip", "Hillingdon").collect(Collectors.toSet()));
		connections.put("Island Gardens", Stream.of("Cutty Sark", "Mudchute").collect(Collectors.toSet()));
		connections.put("Kennington", Stream.of("Elephant & Castle", "Waterloo", "Oval").collect(Collectors.toSet()));
		connections.put("Kensal Green", Stream.of("Willesden Junction", "Queen's Park").collect(Collectors.toSet()));
		connections.put("Kensington (Olympia)", Stream.of("Earl's Court").collect(Collectors.toSet()));
		connections.put("Kentish Town", Stream.of("Tufnell Park", "Camden Town").collect(Collectors.toSet()));
		connections.put("Kenton", Stream.of("Harrow & Wealdston", "South Kenton").collect(Collectors.toSet()));
		connections.put("Kew Gardens", Stream.of("Richmond", "Gunnersbury").collect(Collectors.toSet()));
		connections.put("Kilburn", Stream.of("West Hampstead", "Willesden Green").collect(Collectors.toSet()));
		connections.put("Kilburn Park", Stream.of("Queen's Park", "Maida Vale").collect(Collectors.toSet()));
		connections.put("Kingsbury", Stream.of("Wembley Park", "Queensbury").collect(Collectors.toSet()));
		connections.put("King's Cross St. Pancras", Stream.of("Angel", "Caledonian Road", "Euston", "Euston Square",
				"Highbury & Islington", "Farringdon", "Russell Square").collect(Collectors.toSet()));
		connections.put("Knightsbridge", Stream.of("Hyde Park Corner", "Gloucester Road").collect(Collectors.toSet()));
		connections.put("Ladbroke Grove", Stream.of("Latimer Road", "Westbourne Park").collect(Collectors.toSet()));
		connections.put("Lambeth North", Stream.of("Elephant & Castle", "Waterloo").collect(Collectors.toSet()));
		connections.put("Lancaster Gate", Stream.of("Marble Arch", "Queensway").collect(Collectors.toSet()));
		connections.put("Latimer Road", Stream.of("Shepherd's Bush (H)", "Ladbroke Grove").collect(Collectors.toSet()));
		connections.put("Leicester Square",
				Stream.of("Charing Cross", "Tottenham Court Road", "Picadilly Circus", "Covent Garden")
						.collect(Collectors.toSet()));
		connections.put("Lewisham", Stream.of("Elverson Road").collect(Collectors.toSet()));
		connections.put("Leyton", Stream.of("Stratford", "Leytonstone").collect(Collectors.toSet()));
		connections.put("Leytonstone", Stream.of("Wanstead", "Snaresbrook", "Leyton").collect(Collectors.toSet()));
		connections.put("Limehouse", Stream.of("Shadwell", "Westferry").collect(Collectors.toSet()));
		connections.put("Liverpool Street",
				Stream.of("Aldgate", "Aldgate East", "Moorgate", "Bethnal Green", "Bank").collect(Collectors.toSet()));
		connections.put("London Bridge",
				Stream.of("Bermondsey", "Southwark", "Bank", "Borough").collect(Collectors.toSet()));
		connections.put("Loughton", Stream.of("Debden", "Buckhurst Hill").collect(Collectors.toSet()));
		connections.put("Maida Vale", Stream.of("Warwick Avenue", "Kilburn Park").collect(Collectors.toSet()));
		connections.put("Manor House", Stream.of("Turnpike Lane", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Mansion House", Stream.of("Blackfriars", "Cannon Street").collect(Collectors.toSet()));
		connections.put("Marble Arch", Stream.of("Lancaster Gate", "Bond Street").collect(Collectors.toSet()));
		connections.put("Marylebone", Stream.of("Edgware Road (B)", "Baker Street").collect(Collectors.toSet()));
		connections.put("Mile End",
				Stream.of("Bow Road", "Stepney Green", "Stratford", "Bethnal Green").collect(Collectors.toSet()));
		connections.put("Mill Hill East", Stream.of("Finchley Central").collect(Collectors.toSet()));
		connections.put("Monument", Stream.of("Tower Hill", "Cannon Street").collect(Collectors.toSet()));
		connections.put("Moorgate",
				Stream.of("Liverpool Street", "Old Street", "Bank", "Barbican").collect(Collectors.toSet()));
		connections.put("Moor Park", Stream.of("Northwood", "Rickmansworth", "Croxley").collect(Collectors.toSet()));
		connections.put("Morden", Stream.of("South Wimbledon").collect(Collectors.toSet()));
		connections.put("Mornington Crescent", Stream.of("Camden Town", "Euston").collect(Collectors.toSet()));
		connections.put("Mudchute",
				Stream.of("Island Gardens", "Crossharbour & London Arena").collect(Collectors.toSet()));
		connections.put("Neasden", Stream.of("Dollis Hill", "Wembley Park").collect(Collectors.toSet()));
		connections.put("Newbury Park", Stream.of("Barkingside", "Gants Hill").collect(Collectors.toSet()));
		connections.put("New Cross", Stream.of("Surrey Quays").collect(Collectors.toSet()));
		connections.put("New Cross Gate", Stream.of("Surrey Quays").collect(Collectors.toSet()));
		connections.put("Northfields", Stream.of("South Ealing", "Boston Manor").collect(Collectors.toSet()));
		connections.put("Northolt", Stream.of("Greenford", "South Ruislip").collect(Collectors.toSet()));
		connections.put("Northwick Park", Stream.of("Harrow-on-the-Hill", "Preston Road").collect(Collectors.toSet()));
		connections.put("Northwood", Stream.of("Northwood Hills", "Moor Park").collect(Collectors.toSet()));
		connections.put("Northwood Hills", Stream.of("Northwood", "Pinner").collect(Collectors.toSet()));
		connections.put("North Acton",
				Stream.of("Hanger Lane", "East Acton", "West Acton").collect(Collectors.toSet()));
		connections.put("North Ealing", Stream.of("Park Royal", "Ealing Common").collect(Collectors.toSet()));
		connections.put("North Greenwich", Stream.of("Canary Wharf", "Canning Town").collect(Collectors.toSet()));
		connections.put("North Harrow", Stream.of("Harrow-on-the-Hill", "Pinner").collect(Collectors.toSet()));
		connections.put("North Wembley", Stream.of("Wembley Central", "South Kenton").collect(Collectors.toSet()));
		connections.put("Notting Hill Gate", Stream
				.of("Bayswater", "High Street Kensington", "Holland Park", "Queensway").collect(Collectors.toSet()));
		connections.put("Oakwood", Stream.of("Southgate", "Cockfosters").collect(Collectors.toSet()));
		connections.put("Old Street", Stream.of("Angel", "Moorgate").collect(Collectors.toSet()));
		connections.put("Osterley", Stream.of("Hounslow East", "Boston Manor").collect(Collectors.toSet()));
		connections.put("Oval", Stream.of("Stockwell", "Kennington").collect(Collectors.toSet()));
		connections.put("Oxford Circus", Stream.of("Tottenham Court Road", "Regent's Park", "Picadilly Circus",
				"Warren Street", "Green Park", "Bond Street").collect(Collectors.toSet()));
		connections.put("Paddington",
				Stream.of("Edgware Road (B)", "Bayswater", "Edgware Road (C)", "Warwick Avenue", "Royal Oak")
						.collect(Collectors.toSet()));
		connections.put("Park Royal", Stream.of("Alperton", "North Ealing").collect(Collectors.toSet()));
		connections.put("Parsons Green", Stream.of("Fulham Broadway", "Putney Bridge").collect(Collectors.toSet()));
		connections.put("Perivale", Stream.of("Hanger Lane", "Greenford").collect(Collectors.toSet()));
		connections.put("Picadilly Circus", Stream
				.of("Charing Cross", "Leicester Square", "Green Park", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Pimlico", Stream.of("Victoria", "Vauxhall").collect(Collectors.toSet()));
		connections.put("Pinner", Stream.of("Northwood Hills", "North Harrow").collect(Collectors.toSet()));
		connections.put("Plaistow", Stream.of("West Ham", "Upton Park").collect(Collectors.toSet()));
		connections.put("Poplar",
				Stream.of("West India Quay", "All Saints", "Blackwall", "Westferry").collect(Collectors.toSet()));
		connections.put("Preston Road", Stream.of("Northwick Park", "Wembley Park").collect(Collectors.toSet()));
		connections.put("Prince Regent", Stream.of("Royal Albert", "Custom House").collect(Collectors.toSet()));
		connections.put("Pudding Mill Lane", Stream.of("Bow Church", "Stratford").collect(Collectors.toSet()));
		connections.put("Putney Bridge", Stream.of("East Putney", "Parsons Green").collect(Collectors.toSet()));
		connections.put("Queen's Park", Stream.of("Kensal Green", "Kilburn Park").collect(Collectors.toSet()));
		connections.put("Queensbury", Stream.of("Kingsbury", "Canons Park").collect(Collectors.toSet()));
		connections.put("Queensway", Stream.of("Lancaster Gate", "Notting Hill Gate").collect(Collectors.toSet()));
		connections.put("Ravenscourt Park", Stream.of("Stamford Brook", "Hammersmith").collect(Collectors.toSet()));
		connections.put("Rayners Lane",
				Stream.of("West Harrow", "South Harrow", "Eastcote").collect(Collectors.toSet()));
		connections.put("Redbridge", Stream.of("Gants Hill", "Wanstead").collect(Collectors.toSet()));
		connections.put("Regent's Park", Stream.of("Baker Street", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Richmond", Stream.of("Kew Gardens").collect(Collectors.toSet()));
		connections.put("Rickmansworth", Stream.of("Chorleywood", "Moor Park").collect(Collectors.toSet()));
		connections.put("Roding Valley", Stream.of("Chigwell", "Woodford").collect(Collectors.toSet()));
		connections.put("Rotherhithe", Stream.of("Wapping", "Canada Water").collect(Collectors.toSet()));
		connections.put("Royal Albert", Stream.of("Beckton Park", "Prince Regent").collect(Collectors.toSet()));
		connections.put("Royal Oak", Stream.of("Paddington", "Westbourne Park").collect(Collectors.toSet()));
		connections.put("Royal Victoria", Stream.of("Canning Town", "Custom House").collect(Collectors.toSet()));
		connections.put("Ruislip", Stream.of("Ickenham", "Ruislip Manor").collect(Collectors.toSet()));
		connections.put("Ruislip Gardens", Stream.of("West Ruislip", "South Ruislip").collect(Collectors.toSet()));
		connections.put("Ruislip Manor", Stream.of("Eastcote", "Ruislip").collect(Collectors.toSet()));
		connections.put("Russell Square", Stream.of("King's Cross St. Pancras", "Holborn").collect(Collectors.toSet()));
		connections.put("Seven Sisters", Stream.of("Tottenham Hale", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Shadwell",
				Stream.of("Wapping", "Tower Gateway", "Whitechapel", "Limehouse", "Bank").collect(Collectors.toSet()));
		connections.put("Shepherd's Bush (C)", Stream.of("White City", "Holland Park").collect(Collectors.toSet()));
		connections.put("Shepherd's Bush (H)", Stream.of("Goldhawk Road", "Latimer Road").collect(Collectors.toSet()));
		connections.put("Shoreditch", Stream.of("Whitechapel").collect(Collectors.toSet()));
		connections.put("Sloane Square", Stream.of("Victoria", "South Kensington").collect(Collectors.toSet()));
		connections.put("Snaresbrook", Stream.of("South Woodford", "Leytonstone").collect(Collectors.toSet()));
		connections.put("Southfields", Stream.of("East Putney", "Wimbledon Park").collect(Collectors.toSet()));
		connections.put("Southgate", Stream.of("Arnos Grove", "Oakwood").collect(Collectors.toSet()));
		connections.put("Southwark", Stream.of("Waterloo", "London Bridge").collect(Collectors.toSet()));
		connections.put("South Ealing", Stream.of("Northfields", "Acton Town").collect(Collectors.toSet()));
		connections.put("South Harrow", Stream.of("Rayners Lane", "Sudbury Hill").collect(Collectors.toSet()));
		connections.put("South Kensington", Stream.of("Gloucester Road", "Sloane Square").collect(Collectors.toSet()));
		connections.put("South Kenton", Stream.of("North Wembley", "Kenton").collect(Collectors.toSet()));
		connections.put("South Quay",
				Stream.of("Heron Quays", "Crossharbour & London Arena").collect(Collectors.toSet()));
		connections.put("South Ruislip", Stream.of("Northolt", "Ruislip Gardens").collect(Collectors.toSet()));
		connections.put("South Wimbledon", Stream.of("Morden", "Colliers Wood").collect(Collectors.toSet()));
		connections.put("South Woodford", Stream.of("Snaresbrook", "Woodford").collect(Collectors.toSet()));
		connections.put("Stamford Brook", Stream.of("Ravenscourt Park", "Turnham Green").collect(Collectors.toSet()));
		connections.put("Stanmore", Stream.of("Canons Park").collect(Collectors.toSet()));
		connections.put("Stepney Green", Stream.of("Mile End", "Whitechapel").collect(Collectors.toSet()));
		connections.put("Stockwell",
				Stream.of("Brixton", "Clapham North", "Oval", "Vauxhall").collect(Collectors.toSet()));
		connections.put("Stonebridge Park", Stream.of("Harlesden", "Wembley Central").collect(Collectors.toSet()));
		connections.put("Stratford",
				Stream.of("West Ham", "Mile End", "Leyton", "Pudding Mill Lane").collect(Collectors.toSet()));
		connections.put("St. James's Park", Stream.of("Victoria", "Westminster").collect(Collectors.toSet()));
		connections.put("St. John's Wood", Stream.of("Baker Street", "Swiss Cottage").collect(Collectors.toSet()));
		connections.put("St. Paul's", Stream.of("Chancery Lane", "Bank").collect(Collectors.toSet()));
		connections.put("Sudbury Hill", Stream.of("South Harrow", "Sudbury Town").collect(Collectors.toSet()));
		connections.put("Sudbury Town", Stream.of("Alperton", "Sudbury Hill").collect(Collectors.toSet()));
		connections.put("Surrey Quays",
				Stream.of("Canada Water", "New Cross", "New Cross Gate").collect(Collectors.toSet()));
		connections.put("Swiss Cottage", Stream.of("St. John's Wood", "Finchley Road").collect(Collectors.toSet()));
		connections.put("Temple", Stream.of("Embankment", "Blackfriars").collect(Collectors.toSet()));
		connections.put("Theydon Bois", Stream.of("Debden", "Epping").collect(Collectors.toSet()));
		connections.put("Tooting Bec", Stream.of("Tooting Broadway", "Balham").collect(Collectors.toSet()));
		connections.put("Tooting Broadway", Stream.of("Tooting Bec", "Colliers Wood").collect(Collectors.toSet()));
		connections.put("Tottenham Court Road",
				Stream.of("Goodge Street", "Leicester Square", "Holborn", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Tottenham Hale", Stream.of("Blackhorse Road", "Seven Sisters").collect(Collectors.toSet()));
		connections.put("Totteridge & Whetstone",
				Stream.of("High Barnet", "Woodside Park").collect(Collectors.toSet()));
		connections.put("Tower Gateway", Stream.of("Shadwell").collect(Collectors.toSet()));
		connections.put("Tower Hill", Stream.of("Aldgate", "Aldgate East", "Monument").collect(Collectors.toSet()));
		connections.put("Tufnell Park", Stream.of("Archway", "Kentish Town").collect(Collectors.toSet()));
		connections.put("Turnham Green",
				Stream.of("Stamford Brook", "Acton Town", "Chiswick Park", "Gunnersbury", "Hammersmith")
						.collect(Collectors.toSet()));
		connections.put("Turnpike Lane", Stream.of("Manor House", "Wood Green").collect(Collectors.toSet()));
		connections.put("Upminster", Stream.of("Upminster Bridge").collect(Collectors.toSet()));
		connections.put("Upminster Bridge", Stream.of("Hornchurch", "Upminster").collect(Collectors.toSet()));
		connections.put("Upney", Stream.of("Becontree", "Barking").collect(Collectors.toSet()));
		connections.put("Upton Park", Stream.of("Plaistow", "East Ham").collect(Collectors.toSet()));
		connections.put("Uxbridge", Stream.of("Hillingdon").collect(Collectors.toSet()));
		connections.put("Vauxhall", Stream.of("Stockwell", "Pimlico").collect(Collectors.toSet()));
		connections.put("Victoria",
				Stream.of("Sloane Square", "Pimlico", "St. James's Park", "Green Park").collect(Collectors.toSet()));
		connections.put("Walthamstow Central", Stream.of("Blackhorse Road").collect(Collectors.toSet()));
		connections.put("Wanstead", Stream.of("Redbridge", "Leytonstone").collect(Collectors.toSet()));
		connections.put("Wapping", Stream.of("Shadwell", "Rotherhithe").collect(Collectors.toSet()));
		connections.put("Warren Street",
				Stream.of("Goodge Street", "Euston", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Warwick Avenue", Stream.of("Paddington", "Maida Vale").collect(Collectors.toSet()));
		connections.put("Waterloo",
				Stream.of("Lambeth North", "Embankment", "Southwark", "Kennington", "Westminster", "Bank")
						.collect(Collectors.toSet()));
		connections.put("Watford", Stream.of("Croxley").collect(Collectors.toSet()));
		connections.put("Wembley Central", Stream.of("Stonebridge Park", "North Wembley").collect(Collectors.toSet()));
		connections.put("Wembley Park",
				Stream.of("Kingsbury", "Preston Road", "Neasden", "Finchley Road").collect(Collectors.toSet()));
		connections.put("Westbourne Park", Stream.of("Ladbroke Grove", "Royal Oak").collect(Collectors.toSet()));
		connections.put("Westferry", Stream.of("West India Quay", "Poplar", "Limehouse").collect(Collectors.toSet()));
		connections.put("Westminster",
				Stream.of("Waterloo", "Embankment", "St. James's Park", "Green Park").collect(Collectors.toSet()));
		connections.put("West Acton", Stream.of("North Acton", "Ealing Broadway").collect(Collectors.toSet()));
		connections.put("West Brompton", Stream.of("Fulham Broadway", "Earl's Court").collect(Collectors.toSet()));
		connections.put("West Finchley", Stream.of("Finchley Central", "Woodside Park").collect(Collectors.toSet()));
		connections.put("West Ham",
				Stream.of("Bromley-By-Bow", "Stratford", "Plaistow", "Canning Town").collect(Collectors.toSet()));
		connections.put("West Hampstead", Stream.of("Finchley Road", "Kilburn").collect(Collectors.toSet()));
		connections.put("West Harrow", Stream.of("Rayners Lane", "Harrow-on-the-Hill").collect(Collectors.toSet()));
		connections.put("West India Quay",
				Stream.of("Poplar", "Canary Wharf", "Westferry").collect(Collectors.toSet()));
		connections.put("West Kensington", Stream.of("Barons Court", "Earl's Court").collect(Collectors.toSet()));
		connections.put("West Ruislip", Stream.of("Ruislip Gardens").collect(Collectors.toSet()));
		connections.put("Whitechapel",
				Stream.of("Shadwell", "Aldgate East", "Stepney Green", "Shoreditch").collect(Collectors.toSet()));
		connections.put("White City", Stream.of("Shepherd's Bush (C)", "East Acton").collect(Collectors.toSet()));
		connections.put("Willesden Green", Stream.of("Dollis Hill", "Kilburn").collect(Collectors.toSet()));
		connections.put("Willesden Junction", Stream.of("Harlesden", "Kensal Green").collect(Collectors.toSet()));
		connections.put("Wimbledon", Stream.of("Wimbledon Park").collect(Collectors.toSet()));
		connections.put("Wimbledon Park", Stream.of("Southfields", "Wimbledon").collect(Collectors.toSet()));
		connections.put("Woodford",
				Stream.of("South Woodford", "Buckhurst Hill", "Roding Valley").collect(Collectors.toSet()));
		connections.put("Woodside Park",
				Stream.of("Totteridge & Whetstone", "West Finchley").collect(Collectors.toSet()));
		connections.put("Wood Green", Stream.of("Turnpike Lane", "Bounds Green").collect(Collectors.toSet()));

		return buildGraph(stations, connections);
	}

	public static Map<String, GraphNode<String, Station>> getGraph2() {
		final Map<String, Station> stations = new HashMap<>();
		stations.put("Acton Town", new Station("Acton Town", 51.5028, -0.2801));
		stations.put("Aldgate", new Station("Aldgate", 51.5143, -0.0755));
		stations.put("Aldgate East", new Station("Aldgate East", 51.5154, -0.0726));
		stations.put("All Saints", new Station("All Saints", 51.5107, -0.013));
		stations.put("Alperton", new Station("Alperton", 51.5407, -0.2997));
		stations.put("Amersham", new Station("Amersham", 51.6736, -0.607));
		stations.put("Angel", new Station("Angel", 51.5322, -0.1058));
		stations.put("Archway", new Station("Archway", 51.5653, -0.1353));
		stations.put("Arnos Grove", new Station("Arnos Grove", 51.6164, -0.1331));
		stations.put("Arsenal", new Station("Arsenal", 51.5586, -0.1059));
		stations.put("Baker Street", new Station("Baker Street", 51.5226, -0.1571));
		stations.put("Balham", new Station("Balham", 51.4431, -0.1525));
		stations.put("Bank", new Station("Bank", 51.5133, -0.0886));
		stations.put("Barbican", new Station("Barbican", 51.5204, -0.0979));
		stations.put("Barking", new Station("Barking", 51.5396, 0.081));
		stations.put("Barkingside", new Station("Barkingside", 51.5856, 0.0887));
		stations.put("Barons Court", new Station("Barons Court", 51.4905, -0.2139));
		stations.put("Bayswater", new Station("Bayswater", 51.5121, -0.1879));
		stations.put("Beckton", new Station("Beckton", 51.5148, 0.0613));
		stations.put("Beckton Park", new Station("Beckton Park", 51.5087, 0.055));
		stations.put("Becontree", new Station("Becontree", 51.5403, 0.127));
		stations.put("Belsize Park", new Station("Belsize Park", 51.5504, -0.1642));
		stations.put("Bermondsey", new Station("Bermondsey", 51.4979, -0.0637));
		stations.put("Bethnal Green", new Station("Bethnal Green", 51.527, -0.0549));
		stations.put("Blackfriars", new Station("Blackfriars", 51.512, -0.1031));
		stations.put("Blackhorse Road", new Station("Blackhorse Road", 51.5867, -0.0417));
		stations.put("Blackwall", new Station("Blackwall", 51.5079, -0.0066));
		stations.put("Bond Street", new Station("Bond Street", 51.5142, -0.1494));
		stations.put("Borough", new Station("Borough", 51.5011, -0.0943));
		stations.put("Boston Manor", new Station("Boston Manor", 51.4956, -0.325));
		stations.put("Bounds Green", new Station("Bounds Green", 51.6071, -0.1243));
		stations.put("Bow Church", new Station("Bow Church", 51.5273, -0.0208));
		stations.put("Bow Road", new Station("Bow Road", 51.5269, -0.0247));
		stations.put("Brent Cross", new Station("Brent Cross", 51.5766, -0.2136));
		stations.put("Brixton", new Station("Brixton", 51.4627, -0.1145));
		stations.put("Bromley-By-Bow", new Station("Bromley-By-Bow", 51.5248, -0.0119));
		stations.put("Buckhurst Hill", new Station("Buckhurst Hill", 51.6266, 0.0471));
		stations.put("Burnt Oak", new Station("Burnt Oak", 51.6028, -0.2641));
		stations.put("Caledonian Road", new Station("Caledonian Road", 51.5481, -0.1188));
		stations.put("Camden Town", new Station("Camden Town", 51.5392, -0.1426));
		stations.put("Canada Water", new Station("Canada Water", 51.4982, -0.0502));
		stations.put("Canary Wharf", new Station("Canary Wharf", 51.5051, -0.0209));
		stations.put("Canning Town", new Station("Canning Town", 51.5147, 0.0082));
		stations.put("Cannon Street", new Station("Cannon Street", 51.5113, -0.0904));
		stations.put("Canons Park", new Station("Canons Park", 51.6078, -0.2947));
		stations.put("Chalfont & Latimer", new Station("Chalfont & Latimer", 51.6679, -0.561));
		stations.put("Chalk Farm", new Station("Chalk Farm", 51.5441, -0.1538));
		stations.put("Chancery Lane", new Station("Chancery Lane", 51.5185, -0.1111));
		stations.put("Charing Cross", new Station("Charing Cross", 51.508, -0.1247));
		stations.put("Chesham", new Station("Chesham", 51.7052, -0.611));
		stations.put("Chigwell", new Station("Chigwell", 51.6177, 0.0755));
		stations.put("Chiswick Park", new Station("Chiswick Park", 51.4946, -0.2678));
		stations.put("Chorleywood", new Station("Chorleywood", 51.6543, -0.5183));
		stations.put("Clapham Common", new Station("Clapham Common", 51.4618, -0.1384));
		stations.put("Clapham North", new Station("Clapham North", 51.4649, -0.1299));
		stations.put("Clapham South", new Station("Clapham South", 51.4527, -0.148));
		stations.put("Cockfosters", new Station("Cockfosters", 51.6517, -0.1496));
		stations.put("Colindale", new Station("Colindale", 51.5955, -0.2502));
		stations.put("Colliers Wood", new Station("Colliers Wood", 51.418, -0.1778));
		stations.put("Covent Garden", new Station("Covent Garden", 51.5129, -0.1243));
		stations.put("Crossharbour & London Arena", new Station("Crossharbour & London Arena", 51.4957, -0.0144));
		stations.put("Croxley", new Station("Croxley", 51.647, -0.4412));
		stations.put("Custom House", new Station("Custom House", 51.5095, 0.0276));
		stations.put("Cutty Sark", new Station("Cutty Sark", 51.4827, -0.0096));
		stations.put("Cyprus", new Station("Cyprus", 51.5085, 0.064));
		stations.put("Dagenham East", new Station("Dagenham East", 51.5443, 0.1655));
		stations.put("Dagenham Heathway", new Station("Dagenham Heathway", 51.5417, 0.1469));
		stations.put("Debden", new Station("Debden", 51.6455, 0.0838));
		stations.put("Deptford Bridge", new Station("Deptford Bridge", 51.474, -0.0216));
		stations.put("Devons Road", new Station("Devons Road", 51.5223, -0.0173));
		stations.put("Dollis Hill", new Station("Dollis Hill", 51.552, -0.2387));
		stations.put("Ealing Broadway", new Station("Ealing Broadway", 51.5152, -0.3017));
		stations.put("Ealing Common", new Station("Ealing Common", 51.5101, -0.2882));
		stations.put("Earl's Court", new Station("Earl's Court", 51.492, -0.1973));
		stations.put("Eastcote", new Station("Eastcote", 51.5765, -0.397));
		stations.put("East Acton", new Station("East Acton", 51.5168, -0.2474));
		stations.put("East Finchley", new Station("East Finchley", 51.5874, -0.165));
		stations.put("East Ham", new Station("East Ham", 51.5394, 0.0518));
		stations.put("East India", new Station("East India", 51.5093, -0.0021));
		stations.put("East Putney", new Station("East Putney", 51.4586, -0.2112));
		stations.put("Edgware", new Station("Edgware", 51.6137, -0.275));
		stations.put("Edgware Road (B)", new Station("Edgware Road (B)", 51.5199, -0.1679));
		stations.put("Edgware Road (C)", new Station("Edgware Road (C)", 51.5203, -0.17));
		stations.put("Elephant & Castle", new Station("Elephant & Castle", 51.4943, -0.1001));
		stations.put("Elm Park", new Station("Elm Park", 51.5496, 0.1977));
		stations.put("Elverson Road", new Station("Elverson Road", 51.4693, -0.0174));
		stations.put("Embankment", new Station("Embankment", 51.5074, -0.1223));
		stations.put("Epping", new Station("Epping", 51.6937, 0.1139));
		stations.put("Euston", new Station("Euston", 51.5282, -0.1337));
		stations.put("Euston Square", new Station("Euston Square", 51.526, -0.1359));
		stations.put("Fairlop", new Station("Fairlop", 51.596, 0.0912));
		stations.put("Farringdon", new Station("Farringdon", 51.5203, -0.1053));
		stations.put("Finchley Central", new Station("Finchley Central", 51.6012, -0.1932));
		stations.put("Finchley Road", new Station("Finchley Road", 51.5472, -0.1803));
		stations.put("Finsbury Park", new Station("Finsbury Park", 51.5642, -0.1065));
		stations.put("Fulham Broadway", new Station("Fulham Broadway", 51.4804, -0.195));
		stations.put("Gallions Reach", new Station("Gallions Reach", 51.5096, 0.0716));
		stations.put("Gants Hill", new Station("Gants Hill", 51.5765, 0.0663));
		stations.put("Gloucester Road", new Station("Gloucester Road", 51.4945, -0.1829));
		stations.put("Golders Green", new Station("Golders Green", 51.5724, -0.1941));
		stations.put("Goldhawk Road", new Station("Goldhawk Road", 51.5018, -0.2267));
		stations.put("Goodge Street", new Station("Goodge Street", 51.5205, -0.1347));
		stations.put("Grange Hill", new Station("Grange Hill", 51.6132, 0.0923));
		stations.put("Great Portland Street", new Station("Great Portland Street", 51.5238, -0.1439));
		stations.put("Greenford", new Station("Greenford", 51.5423, -0.3456));
		stations.put("Greenwich", new Station("Greenwich", 51.4781, -0.0149));
		stations.put("Green Park", new Station("Green Park", 51.5067, -0.1428));
		stations.put("Gunnersbury", new Station("Gunnersbury", 51.4915, -0.2754));
		stations.put("Hainault", new Station("Hainault", 51.603, 0.0933));
		stations.put("Hammersmith", new Station("Hammersmith", 51.4936, -0.2251));
		stations.put("Hampstead", new Station("Hampstead", 51.5568, -0.178));
		stations.put("Hanger Lane", new Station("Hanger Lane", 51.5302, -0.2933));
		stations.put("Harlesden", new Station("Harlesden", 51.5362, -0.2575));
		stations.put("Harrow & Wealdston", new Station("Harrow & Wealdston", 51.5925, -0.3351));
		stations.put("Harrow-on-the-Hill", new Station("Harrow-on-the-Hill", 51.5793, -0.3366));
		stations.put("Hatton Cross", new Station("Hatton Cross", 51.4669, -0.4227));
		stations.put("Heathrow Terminals 1, 2 & 3", new Station("Heathrow Terminals 1, 2 & 3", 51.4713, -0.4524));
		stations.put("Heathrow Terminal 4", new Station("Heathrow Terminal 4", 51.4598, -0.4476));
		stations.put("Hendon Central", new Station("Hendon Central", 51.5829, -0.2259));
		stations.put("Heron Quays", new Station("Heron Quays", 51.5033, -0.0215));
		stations.put("High Barnet", new Station("High Barnet", 51.6503, -0.1943));
		stations.put("High Street Kensington", new Station("High Street Kensington", 51.5009, -0.1925));
		stations.put("Highbury & Islington", new Station("Highbury & Islington", 51.546, -0.104));
		stations.put("Highgate", new Station("Highgate", 51.5777, -0.1458));
		stations.put("Hillingdon", new Station("Hillingdon", 51.5538, -0.4499));
		stations.put("Holborn", new Station("Holborn", 51.5174, -0.12));
		stations.put("Holland Park", new Station("Holland Park", 51.5075, -0.206));
		stations.put("Holloway Road", new Station("Holloway Road", 51.5526, -0.1132));
		stations.put("Hornchurch", new Station("Hornchurch", 51.5539, 0.2184));
		stations.put("Hounslow Central", new Station("Hounslow Central", 51.4713, -0.3665));
		stations.put("Hounslow East", new Station("Hounslow East", 51.4733, -0.3564));
		stations.put("Hounslow West", new Station("Hounslow West", 51.4734, -0.3855));
		stations.put("Hyde Park Corner", new Station("Hyde Park Corner", 51.5027, -0.1527));
		stations.put("Ickenham", new Station("Ickenham", 51.5619, -0.4421));
		stations.put("Island Gardens", new Station("Island Gardens", 51.4871, -0.0101));
		stations.put("Kennington", new Station("Kennington", 51.4884, -0.1053));
		stations.put("Kensal Green", new Station("Kensal Green", 51.5304, -0.225));
		stations.put("Kensington (Olympia)", new Station("Kensington (Olympia)", 51.4983, -0.2106));
		stations.put("Kentish Town", new Station("Kentish Town", 51.5507, -0.1402));
		stations.put("Kenton", new Station("Kenton", 51.5816, -0.3162));
		stations.put("Kew Gardens", new Station("Kew Gardens", 51.477, -0.285));
		stations.put("Kilburn", new Station("Kilburn", 51.5471, -0.2047));
		stations.put("Kilburn Park", new Station("Kilburn Park", 51.5351, -0.1939));
		stations.put("Kingsbury", new Station("Kingsbury", 51.5846, -0.2786));
		stations.put("King's Cross St. Pancras", new Station("King's Cross St. Pancras", 51.5308, -0.1238));
		stations.put("Knightsbridge", new Station("Knightsbridge", 51.5015, -0.1607));
		stations.put("Ladbroke Grove", new Station("Ladbroke Grove", 51.5172, -0.2107));
		stations.put("Lambeth North", new Station("Lambeth North", 51.4991, -0.1115));
		stations.put("Lancaster Gate", new Station("Lancaster Gate", 51.5119, -0.1756));
		stations.put("Latimer Road", new Station("Latimer Road", 51.5139, -0.2172));
		stations.put("Leicester Square", new Station("Leicester Square", 51.5113, -0.1281));
		stations.put("Lewisham", new Station("Lewisham", 51.4657, -0.0142));
		stations.put("Leyton", new Station("Leyton", 51.5566, -0.0053));
		stations.put("Leytonstone", new Station("Leytonstone", 51.5683, 0.0083));
		stations.put("Limehouse", new Station("Limehouse", 51.5123, -0.0396));
		stations.put("Liverpool Street", new Station("Liverpool Street", 51.5178, -0.0823));
		stations.put("London Bridge", new Station("London Bridge", 51.5052, -0.0864));
		stations.put("Loughton", new Station("Loughton", 51.6412, 0.0558));
		stations.put("Maida Vale", new Station("Maida Vale", 51.53, -0.1854));
		stations.put("Manor House", new Station("Manor House", 51.5712, -0.0958));
		stations.put("Mansion House", new Station("Mansion House", 51.5122, -0.094));
		stations.put("Marble Arch", new Station("Marble Arch", 51.5136, -0.1586));
		stations.put("Marylebone", new Station("Marylebone", 51.5225, -0.1631));
		stations.put("Mile End", new Station("Mile End", 51.5249, -0.0332));
		stations.put("Mill Hill East", new Station("Mill Hill East", 51.6082, -0.2103));
		stations.put("Monument", new Station("Monument", 51.5108, -0.0863));
		stations.put("Moorgate", new Station("Moorgate", 51.5186, -0.0886));
		stations.put("Moor Park", new Station("Moor Park", 51.6294, -0.432));
		stations.put("Morden", new Station("Morden", 51.4022, -0.1948));
		stations.put("Mornington Crescent", new Station("Mornington Crescent", 51.5342, -0.1387));
		stations.put("Mudchute", new Station("Mudchute", 51.4902, -0.0145));
		stations.put("Neasden", new Station("Neasden", 51.5542, -0.2503));
		stations.put("Newbury Park", new Station("Newbury Park", 51.5756, 0.0899));
		stations.put("New Cross", new Station("New Cross", 51.4767, -0.0327));
		stations.put("New Cross Gate", new Station("New Cross Gate", 51.4757, -0.0402));
		stations.put("Northfields", new Station("Northfields", 51.4995, -0.3142));
		stations.put("Northolt", new Station("Northolt", 51.5483, -0.3687));
		stations.put("Northwick Park", new Station("Northwick Park", 51.5784, -0.3184));
		stations.put("Northwood", new Station("Northwood", 51.6111, -0.424));
		stations.put("Northwood Hills", new Station("Northwood Hills", 51.6004, -0.4092));
		stations.put("North Acton", new Station("North Acton", 51.5237, -0.2597));
		stations.put("North Ealing", new Station("North Ealing", 51.5175, -0.2887));
		stations.put("North Greenwich", new Station("North Greenwich", 51.5005, 0.0039));
		stations.put("North Harrow", new Station("North Harrow", 51.5846, -0.3626));
		stations.put("North Wembley", new Station("North Wembley", 51.5621, -0.3034));
		stations.put("Notting Hill Gate", new Station("Notting Hill Gate", 51.5094, -0.1967));
		stations.put("Oakwood", new Station("Oakwood", 51.6476, -0.1318));
		stations.put("Old Street", new Station("Old Street", 51.5263, -0.0873));
		stations.put("Osterley", new Station("Osterley", 51.4813, -0.3522));
		stations.put("Oval", new Station("Oval", 51.4819, -0.113));
		stations.put("Oxford Circus", new Station("Oxford Circus", 51.515, -0.1415));
		stations.put("Paddington", new Station("Paddington", 51.5154, -0.1755));
		stations.put("Park Royal", new Station("Park Royal", 51.527, -0.2841));
		stations.put("Parsons Green", new Station("Parsons Green", 51.4753, -0.2011));
		stations.put("Perivale", new Station("Perivale", 51.5366, -0.3232));
		stations.put("Picadilly Circus", new Station("Picadilly Circus", 51.5098, -0.1342));
		stations.put("Pimlico", new Station("Pimlico", 51.4893, -0.1334));
		stations.put("Pinner", new Station("Pinner", 51.5926, -0.3805));
		stations.put("Plaistow", new Station("Plaistow", 51.5313, 0.0172));
		stations.put("Poplar", new Station("Poplar", 51.5077, -0.0173));
		stations.put("Preston Road", new Station("Preston Road", 51.572, -0.2954));
		stations.put("Prince Regent", new Station("Prince Regent", 51.5093, 0.0336));
		stations.put("Pudding Mill Lane", new Station("Pudding Mill Lane", 51.5343, -0.0139));
		stations.put("Putney Bridge", new Station("Putney Bridge", 51.4682, -0.2089));
		stations.put("Queen's Park", new Station("Queen's Park", 51.5341, -0.2047));
		stations.put("Queensbury", new Station("Queensbury", 51.5942, -0.2861));
		stations.put("Queensway", new Station("Queensway", 51.5107, -0.1877));
		stations.put("Ravenscourt Park", new Station("Ravenscourt Park", 51.4942, -0.2359));
		stations.put("Rayners Lane", new Station("Rayners Lane", 51.5753, -0.3714));
		stations.put("Redbridge", new Station("Redbridge", 51.5763, 0.0454));
		stations.put("Regent's Park", new Station("Regent's Park", 51.5234, -0.1466));
		stations.put("Richmond", new Station("Richmond", 51.4633, -0.3013));
		stations.put("Rickmansworth", new Station("Rickmansworth", 51.6404, -0.4733));
		stations.put("Roding Valley", new Station("Roding Valley", 51.6171, 0.0439));
		stations.put("Rotherhithe", new Station("Rotherhithe", 51.501, -0.0525));
		stations.put("Royal Albert", new Station("Royal Albert", 51.5084, 0.0465));
		stations.put("Royal Oak", new Station("Royal Oak", 51.519, -0.188));
		stations.put("Royal Victoria", new Station("Royal Victoria", 51.5091, 0.0181));
		stations.put("Ruislip", new Station("Ruislip", 51.5715, -0.4213));
		stations.put("Ruislip Gardens", new Station("Ruislip Gardens", 51.5606, -0.4103));
		stations.put("Ruislip Manor", new Station("Ruislip Manor", 51.5732, -0.4125));
		stations.put("Russell Square", new Station("Russell Square", 51.523, -0.1244));
		stations.put("Seven Sisters", new Station("Seven Sisters", 51.5822, -0.0749));
		stations.put("Shadwell", new Station("Shadwell", 51.5117, -0.056));
		stations.put("Shepherd's Bush (C)", new Station("Shepherd's Bush (C)", 51.5046, -0.2187));
		stations.put("Shepherd's Bush (H)", new Station("Shepherd's Bush (H)", 51.5058, -0.2265));
		stations.put("Shoreditch", new Station("Shoreditch", 51.5227, -0.0708));
		stations.put("Sloane Square", new Station("Sloane Square", 51.4924, -0.1565));
		stations.put("Snaresbrook", new Station("Snaresbrook", 51.5808, 0.0216));
		stations.put("Southfields", new Station("Southfields", 51.4454, -0.2066));
		stations.put("Southgate", new Station("Southgate", 51.6322, -0.128));
		stations.put("Southwark", new Station("Southwark", 51.501, -0.1052));
		stations.put("South Ealing", new Station("South Ealing", 51.5011, -0.3072));
		stations.put("South Harrow", new Station("South Harrow", 51.5646, -0.3521));
		stations.put("South Kensington", new Station("South Kensington", 51.4941, -0.1738));
		stations.put("South Kenton", new Station("South Kenton", 51.5701, -0.3081));
		stations.put("South Quay", new Station("South Quay", 51.5007, -0.0191));
		stations.put("South Ruislip", new Station("South Ruislip", 51.5569, -0.3988));
		stations.put("South Wimbledon", new Station("South Wimbledon", 51.4154, -0.1919));
		stations.put("South Woodford", new Station("South Woodford", 51.5917, 0.0275));
		stations.put("Stamford Brook", new Station("Stamford Brook", 51.495, -0.2459));
		stations.put("Stanmore", new Station("Stanmore", 51.6194, -0.3028));
		stations.put("Stepney Green", new Station("Stepney Green", 51.5221, -0.047));
		stations.put("Stockwell", new Station("Stockwell", 51.4723, -0.123));
		stations.put("Stonebridge Park", new Station("Stonebridge Park", 51.5439, -0.2759));
		stations.put("Stratford", new Station("Stratford", 51.5416, -0.0042));
		stations.put("St. James's Park", new Station("St. James's Park", 51.4994, -0.1335));
		stations.put("St. John's Wood", new Station("St. John's Wood", 51.5347, -0.174));
		stations.put("St. Paul's", new Station("St. Paul's", 51.5146, -0.0973));
		stations.put("Sudbury Hill", new Station("Sudbury Hill", 51.5569, -0.3366));
		stations.put("Sudbury Town", new Station("Sudbury Town", 51.5507, -0.3156));
		stations.put("Surrey Quays", new Station("Surrey Quays", 51.4933, -0.0478));
		stations.put("Swiss Cottage", new Station("Swiss Cottage", 51.5432, -0.1738));
		stations.put("Temple", new Station("Temple", 51.5111, -0.1141));
		stations.put("Theydon Bois", new Station("Theydon Bois", 51.6717, 0.1033));
		stations.put("Tooting Bec", new Station("Tooting Bec", 51.4361, -0.1598));
		stations.put("Tooting Broadway", new Station("Tooting Broadway", 51.4275, -0.168));
		stations.put("Tottenham Court Road", new Station("Tottenham Court Road", 51.5165, -0.131));
		stations.put("Tottenham Hale", new Station("Tottenham Hale", 51.5882, -0.0594));
		stations.put("Totteridge & Whetstone", new Station("Totteridge & Whetstone", 51.6302, -0.1791));
		stations.put("Tower Gateway", new Station("Tower Gateway", 51.5106, -0.0743));
		stations.put("Tower Hill", new Station("Tower Hill", 51.5098, -0.0766));
		stations.put("Tufnell Park", new Station("Tufnell Park", 51.5567, -0.1374));
		stations.put("Turnham Green", new Station("Turnham Green", 51.4951, -0.2547));
		stations.put("Turnpike Lane", new Station("Turnpike Lane", 51.5904, -0.1028));
		stations.put("Upminster", new Station("Upminster", 51.559, 0.251));
		stations.put("Upminster Bridge", new Station("Upminster Bridge", 51.5582, 0.2343));
		stations.put("Upney", new Station("Upney", 51.5385, 0.1014));
		stations.put("Upton Park", new Station("Upton Park", 51.5352, 0.0343));
		stations.put("Uxbridge", new Station("Uxbridge", 51.5463, -0.4786));
		stations.put("Vauxhall", new Station("Vauxhall", 51.4861, -0.1253));
		stations.put("Victoria", new Station("Victoria", 51.4965, -0.1447));
		stations.put("Walthamstow Central", new Station("Walthamstow Central", 51.583, -0.0195));
		stations.put("Wanstead", new Station("Wanstead", 51.5775, 0.0288));
		stations.put("Wapping", new Station("Wapping", 51.5043, -0.0558));
		stations.put("Warren Street", new Station("Warren Street", 51.5247, -0.1384));
		stations.put("Warwick Avenue", new Station("Warwick Avenue", 51.5235, -0.1835));
		stations.put("Waterloo", new Station("Waterloo", 51.5036, -0.1143));
		stations.put("Watford", new Station("Watford", 51.6573, -0.4177));
		stations.put("Wembley Central", new Station("Wembley Central", 51.5519, -0.2963));
		stations.put("Wembley Park", new Station("Wembley Park", 51.5635, -0.2795));
		stations.put("Westbourne Park", new Station("Westbourne Park", 51.521, -0.2011));
		stations.put("Westferry", new Station("Westferry", 51.5097, -0.0265));
		stations.put("Westminster", new Station("Westminster", 51.501, -0.1254));
		stations.put("West Acton", new Station("West Acton", 51.518, -0.2809));
		stations.put("West Brompton", new Station("West Brompton", 51.4872, -0.1953));
		stations.put("West Finchley", new Station("West Finchley", 51.6095, -0.1883));
		stations.put("West Ham", new Station("West Ham", 51.5287, 0.0056));
		stations.put("West Hampstead", new Station("West Hampstead", 51.5469, -0.1906));
		stations.put("West Harrow", new Station("West Harrow", 51.5795, -0.3533));
		stations.put("West India Quay", new Station("West India Quay", 51.507, -0.0203));
		stations.put("West Kensington", new Station("West Kensington", 51.4907, -0.2065));
		stations.put("West Ruislip", new Station("West Ruislip", 51.5696, -0.4376));
		stations.put("Whitechapel", new Station("Whitechapel", 51.5194, -0.0612));
		stations.put("White City", new Station("White City", 51.512, -0.2239));
		stations.put("Willesden Green", new Station("Willesden Green", 51.5492, -0.2215));
		stations.put("Willesden Junction", new Station("Willesden Junction", 51.5326, -0.2478));
		stations.put("Wimbledon", new Station("Wimbledon", 51.4214, -0.2064));
		stations.put("Wimbledon Park", new Station("Wimbledon Park", 51.4343, -0.1992));
		stations.put("Woodford", new Station("Woodford", 51.607, 0.0341));
		stations.put("Woodside Park", new Station("Woodside Park", 51.6179, -0.1856));
		stations.put("Wood Green", new Station("Wood Green", 51.5975, -0.1097));

		final Map<String, Set<String>> connections = new HashMap<>();
		connections.put("Acton Town", Stream.of("Chiswick Park", "Turnham Green", "Ealing Common", "South Ealing")
				.collect(Collectors.toSet()));
		connections.put("Aldgate", Stream.of("Tower Hill", "Liverpool Street").collect(Collectors.toSet()));
		connections.put("Aldgate East",
				Stream.of("Tower Hill", "Whitechapel", "Liverpool Street").collect(Collectors.toSet()));
		connections.put("All Saints", Stream.of("Devons Road", "Poplar").collect(Collectors.toSet()));
		connections.put("Alperton", Stream.of("Park Royal", "Sudbury Town").collect(Collectors.toSet()));
		connections.put("Amersham", Stream.of("Chalfont & Latimer").collect(Collectors.toSet()));
		connections.put("Angel", Stream.of("King's Cross St. Pancras", "Old Street").collect(Collectors.toSet()));
		connections.put("Archway", Stream.of("Tufnell Park", "Highgate").collect(Collectors.toSet()));
		connections.put("Arnos Grove", Stream.of("Southgate", "Bounds Green").collect(Collectors.toSet()));
		connections.put("Arsenal", Stream.of("Holloway Road", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Baker Street",
				Stream.of("Marylebone", "Regent's Park", "Edgware Road (C)", "Great Portland Street", "St. John's Wood",
						"Bond Street", "Finchley Road").collect(Collectors.toSet()));
		connections.put("Balham", Stream.of("Tooting Bec", "Clapham South").collect(Collectors.toSet()));
		connections.put("Bank",
				Stream.of("Shadwell", "Waterloo", "Moorgate", "St. Paul's", "Liverpool Street", "London Bridge")
						.collect(Collectors.toSet()));
		connections.put("Barbican", Stream.of("Moorgate", "Farringdon").collect(Collectors.toSet()));
		connections.put("Barking", Stream.of("Upney", "East Ham").collect(Collectors.toSet()));
		connections.put("Barkingside", Stream.of("Fairlop", "Newbury Park").collect(Collectors.toSet()));
		connections.put("Barons Court",
				Stream.of("West Kensington", "Earl's Court", "Hammersmith").collect(Collectors.toSet()));
		connections.put("Bayswater", Stream.of("Paddington", "Notting Hill Gate").collect(Collectors.toSet()));
		connections.put("Beckton", Stream.of("Gallions Reach").collect(Collectors.toSet()));
		connections.put("Beckton Park", Stream.of("Cyprus", "Royal Albert").collect(Collectors.toSet()));
		connections.put("Becontree", Stream.of("Dagenham Heathway", "Upney").collect(Collectors.toSet()));
		connections.put("Belsize Park", Stream.of("Chalk Farm", "Hampstead").collect(Collectors.toSet()));
		connections.put("Bermondsey", Stream.of("Canada Water", "London Bridge").collect(Collectors.toSet()));
		connections.put("Bethnal Green", Stream.of("Mile End", "Liverpool Street").collect(Collectors.toSet()));
		connections.put("Blackfriars", Stream.of("Mansion House", "Temple").collect(Collectors.toSet()));
		connections.put("Blackhorse Road",
				Stream.of("Walthamstow Central", "Tottenham Hale").collect(Collectors.toSet()));
		connections.put("Blackwall", Stream.of("Poplar", "East India").collect(Collectors.toSet()));
		connections.put("Bond Street",
				Stream.of("Marble Arch", "Baker Street", "Green Park", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Borough", Stream.of("Elephant & Castle", "London Bridge").collect(Collectors.toSet()));
		connections.put("Boston Manor", Stream.of("Northfields", "Osterley").collect(Collectors.toSet()));
		connections.put("Bounds Green", Stream.of("Arnos Grove", "Wood Green").collect(Collectors.toSet()));
		connections.put("Bow Church", Stream.of("Devons Road", "Pudding Mill Lane").collect(Collectors.toSet()));
		connections.put("Bow Road", Stream.of("Bromley-By-Bow", "Mile End").collect(Collectors.toSet()));
		connections.put("Brent Cross", Stream.of("Golders Green", "Hendon Central").collect(Collectors.toSet()));
		connections.put("Brixton", Stream.of("Stockwell").collect(Collectors.toSet()));
		connections.put("Bromley-By-Bow", Stream.of("West Ham", "Bow Road").collect(Collectors.toSet()));
		connections.put("Buckhurst Hill", Stream.of("Woodford", "Loughton").collect(Collectors.toSet()));
		connections.put("Burnt Oak", Stream.of("Edgware", "Colindale").collect(Collectors.toSet()));
		connections.put("Caledonian Road",
				Stream.of("Holloway Road", "King's Cross St. Pancras").collect(Collectors.toSet()));
		connections.put("Camden Town",
				Stream.of("Euston", "Mornington Crescent", "Kentish Town", "Chalk Farm").collect(Collectors.toSet()));
		connections.put("Canada Water",
				Stream.of("Rotherhithe", "Bermondsey", "Canary Wharf", "Surrey Quays").collect(Collectors.toSet()));
		connections.put("Canary Wharf", Stream.of("West India Quay", "North Greenwich", "Heron Quays", "Canada Water")
				.collect(Collectors.toSet()));
		connections.put("Canning Town",
				Stream.of("West Ham", "North Greenwich", "Royal Victoria", "East India").collect(Collectors.toSet()));
		connections.put("Cannon Street", Stream.of("Mansion House", "Monument").collect(Collectors.toSet()));
		connections.put("Canons Park", Stream.of("Stanmore", "Queensbury").collect(Collectors.toSet()));
		connections.put("Chalfont & Latimer",
				Stream.of("Chesham", "Chorleywood", "Amersham").collect(Collectors.toSet()));
		connections.put("Chalk Farm", Stream.of("Belsize Park", "Camden Town").collect(Collectors.toSet()));
		connections.put("Chancery Lane", Stream.of("St. Paul's", "Holborn").collect(Collectors.toSet()));
		connections.put("Charing Cross",
				Stream.of("Picadilly Circus", "Embankment", "Leicester Square").collect(Collectors.toSet()));
		connections.put("Chesham", Stream.of("Chalfont & Latimer").collect(Collectors.toSet()));
		connections.put("Chigwell", Stream.of("Roding Valley", "Grange Hill").collect(Collectors.toSet()));
		connections.put("Chiswick Park", Stream.of("Acton Town", "Turnham Green").collect(Collectors.toSet()));
		connections.put("Chorleywood", Stream.of("Rickmansworth", "Chalfont & Latimer").collect(Collectors.toSet()));
		connections.put("Clapham Common", Stream.of("Clapham North", "Clapham South").collect(Collectors.toSet()));
		connections.put("Clapham North", Stream.of("Stockwell", "Clapham Common").collect(Collectors.toSet()));
		connections.put("Clapham South", Stream.of("Clapham Common", "Balham").collect(Collectors.toSet()));
		connections.put("Cockfosters", Stream.of("Oakwood").collect(Collectors.toSet()));
		connections.put("Colindale", Stream.of("Burnt Oak", "Hendon Central").collect(Collectors.toSet()));
		connections.put("Colliers Wood", Stream.of("Tooting Broadway", "South Wimbledon").collect(Collectors.toSet()));
		connections.put("Covent Garden", Stream.of("Leicester Square", "Holborn").collect(Collectors.toSet()));
		connections.put("Crossharbour & London Arena", Stream.of("Mudchute", "South Quay").collect(Collectors.toSet()));
		connections.put("Croxley", Stream.of("Watford", "Moor Park").collect(Collectors.toSet()));
		connections.put("Custom House", Stream.of("Prince Regent", "Royal Victoria").collect(Collectors.toSet()));
		connections.put("Cutty Sark", Stream.of("Island Gardens", "Greenwich").collect(Collectors.toSet()));
		connections.put("Cyprus", Stream.of("Gallions Reach", "Beckton Park").collect(Collectors.toSet()));
		connections.put("Dagenham East", Stream.of("Dagenham Heathway", "Elm Park").collect(Collectors.toSet()));
		connections.put("Dagenham Heathway", Stream.of("Dagenham East", "Becontree").collect(Collectors.toSet()));
		connections.put("Debden", Stream.of("Loughton", "Theydon Bois").collect(Collectors.toSet()));
		connections.put("Deptford Bridge", Stream.of("Elverson Road", "Greenwich").collect(Collectors.toSet()));
		connections.put("Devons Road", Stream.of("Bow Church", "All Saints").collect(Collectors.toSet()));
		connections.put("Dollis Hill", Stream.of("Willesden Green", "Neasden").collect(Collectors.toSet()));
		connections.put("Ealing Broadway", Stream.of("Ealing Common", "West Acton").collect(Collectors.toSet()));
		connections.put("Ealing Common",
				Stream.of("Acton Town", "North Ealing", "Ealing Broadway").collect(Collectors.toSet()));
		connections.put("Earl's Court", Stream.of("Barons Court", "Gloucester Road", "West Kensington",
				"High Street Kensington", "Kensington (Olympia)", "West Brompton").collect(Collectors.toSet()));
		connections.put("Eastcote", Stream.of("Rayners Lane", "Ruislip Manor").collect(Collectors.toSet()));
		connections.put("East Acton", Stream.of("North Acton", "White City").collect(Collectors.toSet()));
		connections.put("East Finchley", Stream.of("Highgate", "Finchley Central").collect(Collectors.toSet()));
		connections.put("East Ham", Stream.of("Upton Park", "Barking").collect(Collectors.toSet()));
		connections.put("East India", Stream.of("Blackwall", "Canning Town").collect(Collectors.toSet()));
		connections.put("East Putney", Stream.of("Southfields", "Putney Bridge").collect(Collectors.toSet()));
		connections.put("Edgware", Stream.of("Burnt Oak").collect(Collectors.toSet()));
		connections.put("Edgware Road (B)", Stream.of("Paddington", "Marylebone").collect(Collectors.toSet()));
		connections.put("Edgware Road (C)", Stream.of("Paddington", "Baker Street").collect(Collectors.toSet()));
		connections.put("Elephant & Castle",
				Stream.of("Lambeth North", "Kennington", "Borough").collect(Collectors.toSet()));
		connections.put("Elm Park", Stream.of("Hornchurch", "Dagenham East").collect(Collectors.toSet()));
		connections.put("Elverson Road", Stream.of("Deptford Bridge", "Lewisham").collect(Collectors.toSet()));
		connections.put("Embankment",
				Stream.of("Charing Cross", "Waterloo", "Westminster", "Temple").collect(Collectors.toSet()));
		connections.put("Epping", Stream.of("Theydon Bois").collect(Collectors.toSet()));
		connections.put("Euston",
				Stream.of("King's Cross St. Pancras", "Warren Street", "Camden Town", "Mornington Crescent")
						.collect(Collectors.toSet()));
		connections.put("Euston Square",
				Stream.of("King's Cross St. Pancras", "Great Portland Street").collect(Collectors.toSet()));
		connections.put("Fairlop", Stream.of("Barkingside", "Hainault").collect(Collectors.toSet()));
		connections.put("Farringdon", Stream.of("King's Cross St. Pancras", "Barbican").collect(Collectors.toSet()));
		connections.put("Finchley Central",
				Stream.of("Mill Hill East", "East Finchley", "West Finchley").collect(Collectors.toSet()));
		connections.put("Finchley Road", Stream.of("West Hampstead", "Wembley Park", "Baker Street", "Swiss Cottage")
				.collect(Collectors.toSet()));
		connections.put("Finsbury Park", Stream.of("Manor House", "Arsenal", "Highbury & Islington", "Seven Sisters")
				.collect(Collectors.toSet()));
		connections.put("Fulham Broadway", Stream.of("Parsons Green", "West Brompton").collect(Collectors.toSet()));
		connections.put("Gallions Reach", Stream.of("Cyprus", "Beckton").collect(Collectors.toSet()));
		connections.put("Gants Hill", Stream.of("Redbridge", "Newbury Park").collect(Collectors.toSet()));
		connections.put("Gloucester Road",
				Stream.of("Knightsbridge", "High Street Kensington", "Earl's Court", "South Kensington")
						.collect(Collectors.toSet()));
		connections.put("Golders Green", Stream.of("Brent Cross", "Hampstead").collect(Collectors.toSet()));
		connections.put("Goldhawk Road", Stream.of("Shepherd's Bush (H)", "Hammersmith").collect(Collectors.toSet()));
		connections.put("Goodge Street",
				Stream.of("Tottenham Court Road", "Warren Street").collect(Collectors.toSet()));
		connections.put("Grange Hill", Stream.of("Chigwell", "Hainault").collect(Collectors.toSet()));
		connections.put("Great Portland Street",
				Stream.of("Euston Square", "Baker Street").collect(Collectors.toSet()));
		connections.put("Greenford", Stream.of("Northolt", "Perivale").collect(Collectors.toSet()));
		connections.put("Greenwich", Stream.of("Cutty Sark", "Deptford Bridge").collect(Collectors.toSet()));
		connections.put("Green Park", Stream
				.of("Victoria", "Picadilly Circus", "Hyde Park Corner", "Bond Street", "Westminster", "Oxford Circus")
				.collect(Collectors.toSet()));
		connections.put("Gunnersbury", Stream.of("Turnham Green", "Kew Gardens").collect(Collectors.toSet()));
		connections.put("Hainault", Stream.of("Grange Hill", "Fairlop").collect(Collectors.toSet()));
		connections.put("Hammersmith", Stream.of("Ravenscourt Park", "Barons Court", "Goldhawk Road", "Turnham Green")
				.collect(Collectors.toSet()));
		connections.put("Hampstead", Stream.of("Golders Green", "Belsize Park").collect(Collectors.toSet()));
		connections.put("Hanger Lane", Stream.of("Perivale", "North Acton").collect(Collectors.toSet()));
		connections.put("Harlesden", Stream.of("Stonebridge Park", "Willesden Junction").collect(Collectors.toSet()));
		connections.put("Harrow & Wealdston", Stream.of("Kenton").collect(Collectors.toSet()));
		connections.put("Harrow-on-the-Hill",
				Stream.of("Northwick Park", "West Harrow", "North Harrow").collect(Collectors.toSet()));
		connections.put("Hatton Cross", Stream.of("Hounslow West", "Heathrow Terminals 1, 2 & 3", "Heathrow Terminal 4")
				.collect(Collectors.toSet()));
		connections.put("Heathrow Terminals 1, 2 & 3",
				Stream.of("Hatton Cross", "Heathrow Terminal 4").collect(Collectors.toSet()));
		connections.put("Heathrow Terminal 4",
				Stream.of("Hatton Cross", "Heathrow Terminals 1, 2 & 3").collect(Collectors.toSet()));
		connections.put("Hendon Central", Stream.of("Brent Cross", "Colindale").collect(Collectors.toSet()));
		connections.put("Heron Quays", Stream.of("Canary Wharf", "South Quay").collect(Collectors.toSet()));
		connections.put("High Barnet", Stream.of("Totteridge & Whetstone").collect(Collectors.toSet()));
		connections.put("High Street Kensington",
				Stream.of("Gloucester Road", "Notting Hill Gate", "Earl's Court").collect(Collectors.toSet()));
		connections.put("Highbury & Islington",
				Stream.of("King's Cross St. Pancras", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Highgate", Stream.of("Archway", "East Finchley").collect(Collectors.toSet()));
		connections.put("Hillingdon", Stream.of("Ickenham", "Uxbridge").collect(Collectors.toSet()));
		connections.put("Holborn", Stream.of("Chancery Lane", "Tottenham Court Road", "Covent Garden", "Russell Square")
				.collect(Collectors.toSet()));
		connections.put("Holland Park",
				Stream.of("Shepherd's Bush (C)", "Notting Hill Gate").collect(Collectors.toSet()));
		connections.put("Holloway Road", Stream.of("Caledonian Road", "Arsenal").collect(Collectors.toSet()));
		connections.put("Hornchurch", Stream.of("Elm Park", "Upminster Bridge").collect(Collectors.toSet()));
		connections.put("Hounslow Central", Stream.of("Hounslow East", "Hounslow West").collect(Collectors.toSet()));
		connections.put("Hounslow East", Stream.of("Hounslow Central", "Osterley").collect(Collectors.toSet()));
		connections.put("Hounslow West", Stream.of("Hounslow Central", "Hatton Cross").collect(Collectors.toSet()));
		connections.put("Hyde Park Corner", Stream.of("Knightsbridge", "Green Park").collect(Collectors.toSet()));
		connections.put("Ickenham", Stream.of("Ruislip", "Hillingdon").collect(Collectors.toSet()));
		connections.put("Island Gardens", Stream.of("Cutty Sark", "Mudchute").collect(Collectors.toSet()));
		connections.put("Kennington", Stream.of("Elephant & Castle", "Waterloo", "Oval").collect(Collectors.toSet()));
		connections.put("Kensal Green", Stream.of("Willesden Junction", "Queen's Park").collect(Collectors.toSet()));
		connections.put("Kensington (Olympia)", Stream.of("Earl's Court").collect(Collectors.toSet()));
		connections.put("Kentish Town", Stream.of("Tufnell Park", "Camden Town").collect(Collectors.toSet()));
		connections.put("Kenton", Stream.of("Harrow & Wealdston", "South Kenton").collect(Collectors.toSet()));
		connections.put("Kew Gardens", Stream.of("Richmond", "Gunnersbury").collect(Collectors.toSet()));
		connections.put("Kilburn", Stream.of("West Hampstead", "Willesden Green").collect(Collectors.toSet()));
		connections.put("Kilburn Park", Stream.of("Queen's Park", "Maida Vale").collect(Collectors.toSet()));
		connections.put("Kingsbury", Stream.of("Wembley Park", "Queensbury").collect(Collectors.toSet()));
		connections.put("King's Cross St. Pancras", Stream.of("Angel", "Caledonian Road", "Euston", "Euston Square",
				"Highbury & Islington", "Farringdon", "Russell Square").collect(Collectors.toSet()));
		connections.put("Knightsbridge", Stream.of("Gloucester Road", "Hyde Park Corner").collect(Collectors.toSet()));
		connections.put("Ladbroke Grove", Stream.of("Latimer Road", "Westbourne Park").collect(Collectors.toSet()));
		connections.put("Lambeth North", Stream.of("Elephant & Castle", "Waterloo").collect(Collectors.toSet()));
		connections.put("Lancaster Gate", Stream.of("Marble Arch", "Queensway").collect(Collectors.toSet()));
		connections.put("Latimer Road", Stream.of("Shepherd's Bush (H)", "Ladbroke Grove").collect(Collectors.toSet()));
		connections.put("Leicester Square",
				Stream.of("Charing Cross", "Tottenham Court Road", "Picadilly Circus", "Covent Garden")
						.collect(Collectors.toSet()));
		connections.put("Lewisham", Stream.of("Elverson Road").collect(Collectors.toSet()));
		connections.put("Leyton", Stream.of("Stratford", "Leytonstone").collect(Collectors.toSet()));
		connections.put("Leytonstone", Stream.of("Wanstead", "Snaresbrook", "Leyton").collect(Collectors.toSet()));
		connections.put("Limehouse", Stream.of("Shadwell", "Westferry").collect(Collectors.toSet()));
		connections.put("Liverpool Street",
				Stream.of("Aldgate", "Aldgate East", "Moorgate", "Bethnal Green", "Bank").collect(Collectors.toSet()));
		connections.put("London Bridge",
				Stream.of("Bermondsey", "Southwark", "Bank", "Borough").collect(Collectors.toSet()));
		connections.put("Loughton", Stream.of("Debden", "Buckhurst Hill").collect(Collectors.toSet()));
		connections.put("Maida Vale", Stream.of("Warwick Avenue", "Kilburn Park").collect(Collectors.toSet()));
		connections.put("Manor House", Stream.of("Turnpike Lane", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Mansion House", Stream.of("Blackfriars", "Cannon Street").collect(Collectors.toSet()));
		connections.put("Marble Arch", Stream.of("Lancaster Gate", "Bond Street").collect(Collectors.toSet()));
		connections.put("Marylebone", Stream.of("Edgware Road (B)", "Baker Street").collect(Collectors.toSet()));
		connections.put("Mile End",
				Stream.of("Bow Road", "Stepney Green", "Stratford", "Bethnal Green").collect(Collectors.toSet()));
		connections.put("Mill Hill East", Stream.of("Finchley Central").collect(Collectors.toSet()));
		connections.put("Monument", Stream.of("Tower Hill", "Cannon Street").collect(Collectors.toSet()));
		connections.put("Moorgate",
				Stream.of("Liverpool Street", "Old Street", "Bank", "Barbican").collect(Collectors.toSet()));
		connections.put("Moor Park", Stream.of("Northwood", "Rickmansworth", "Croxley").collect(Collectors.toSet()));
		connections.put("Morden", Stream.of("South Wimbledon").collect(Collectors.toSet()));
		connections.put("Mornington Crescent", Stream.of("Camden Town", "Euston").collect(Collectors.toSet()));
		connections.put("Mudchute",
				Stream.of("Island Gardens", "Crossharbour & London Arena").collect(Collectors.toSet()));
		connections.put("Neasden", Stream.of("Dollis Hill", "Wembley Park").collect(Collectors.toSet()));
		connections.put("Newbury Park", Stream.of("Barkingside", "Gants Hill").collect(Collectors.toSet()));
		connections.put("New Cross", Stream.of("Surrey Quays").collect(Collectors.toSet()));
		connections.put("New Cross Gate", Stream.of("Surrey Quays").collect(Collectors.toSet()));
		connections.put("Northfields", Stream.of("South Ealing", "Boston Manor").collect(Collectors.toSet()));
		connections.put("Northolt", Stream.of("Greenford", "South Ruislip").collect(Collectors.toSet()));
		connections.put("Northwick Park", Stream.of("Harrow-on-the-Hill", "Preston Road").collect(Collectors.toSet()));
		connections.put("Northwood", Stream.of("Northwood Hills", "Moor Park").collect(Collectors.toSet()));
		connections.put("Northwood Hills", Stream.of("Northwood", "Pinner").collect(Collectors.toSet()));
		connections.put("North Acton",
				Stream.of("Hanger Lane", "East Acton", "West Acton").collect(Collectors.toSet()));
		connections.put("North Ealing", Stream.of("Park Royal", "Ealing Common").collect(Collectors.toSet()));
		connections.put("North Greenwich", Stream.of("Canary Wharf", "Canning Town").collect(Collectors.toSet()));
		connections.put("North Harrow", Stream.of("Harrow-on-the-Hill", "Pinner").collect(Collectors.toSet()));
		connections.put("North Wembley", Stream.of("Wembley Central", "South Kenton").collect(Collectors.toSet()));
		connections.put("Notting Hill Gate", Stream
				.of("Bayswater", "High Street Kensington", "Holland Park", "Queensway").collect(Collectors.toSet()));
		connections.put("Oakwood", Stream.of("Southgate", "Cockfosters").collect(Collectors.toSet()));
		connections.put("Old Street", Stream.of("Angel", "Moorgate").collect(Collectors.toSet()));
		connections.put("Osterley", Stream.of("Hounslow East", "Boston Manor").collect(Collectors.toSet()));
		connections.put("Oval", Stream.of("Stockwell", "Kennington").collect(Collectors.toSet()));
		connections.put("Oxford Circus", Stream.of("Tottenham Court Road", "Regent's Park", "Picadilly Circus",
				"Warren Street", "Green Park", "Bond Street").collect(Collectors.toSet()));
		connections.put("Paddington",
				Stream.of("Edgware Road (B)", "Bayswater", "Edgware Road (C)", "Warwick Avenue", "Royal Oak")
						.collect(Collectors.toSet()));
		connections.put("Park Royal", Stream.of("Alperton", "North Ealing").collect(Collectors.toSet()));
		connections.put("Parsons Green", Stream.of("Fulham Broadway", "Putney Bridge").collect(Collectors.toSet()));
		connections.put("Perivale", Stream.of("Hanger Lane", "Greenford").collect(Collectors.toSet()));
		connections.put("Picadilly Circus", Stream
				.of("Charing Cross", "Leicester Square", "Green Park", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Pimlico", Stream.of("Victoria", "Vauxhall").collect(Collectors.toSet()));
		connections.put("Pinner", Stream.of("Northwood Hills", "North Harrow").collect(Collectors.toSet()));
		connections.put("Plaistow", Stream.of("West Ham", "Upton Park").collect(Collectors.toSet()));
		connections.put("Poplar",
				Stream.of("West India Quay", "All Saints", "Blackwall", "Westferry").collect(Collectors.toSet()));
		connections.put("Preston Road", Stream.of("Northwick Park", "Wembley Park").collect(Collectors.toSet()));
		connections.put("Prince Regent", Stream.of("Royal Albert", "Custom House").collect(Collectors.toSet()));
		connections.put("Pudding Mill Lane", Stream.of("Bow Church", "Stratford").collect(Collectors.toSet()));
		connections.put("Putney Bridge", Stream.of("East Putney", "Parsons Green").collect(Collectors.toSet()));
		connections.put("Queen's Park", Stream.of("Kensal Green", "Kilburn Park").collect(Collectors.toSet()));
		connections.put("Queensbury", Stream.of("Kingsbury", "Canons Park").collect(Collectors.toSet()));
		connections.put("Queensway", Stream.of("Lancaster Gate", "Notting Hill Gate").collect(Collectors.toSet()));
		connections.put("Ravenscourt Park", Stream.of("Stamford Brook", "Hammersmith").collect(Collectors.toSet()));
		connections.put("Rayners Lane",
				Stream.of("West Harrow", "South Harrow", "Eastcote").collect(Collectors.toSet()));
		connections.put("Redbridge", Stream.of("Gants Hill", "Wanstead").collect(Collectors.toSet()));
		connections.put("Regent's Park", Stream.of("Baker Street", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Richmond", Stream.of("Kew Gardens").collect(Collectors.toSet()));
		connections.put("Rickmansworth", Stream.of("Chorleywood", "Moor Park").collect(Collectors.toSet()));
		connections.put("Roding Valley", Stream.of("Chigwell", "Woodford").collect(Collectors.toSet()));
		connections.put("Rotherhithe", Stream.of("Wapping", "Canada Water").collect(Collectors.toSet()));
		connections.put("Royal Albert", Stream.of("Beckton Park", "Prince Regent").collect(Collectors.toSet()));
		connections.put("Royal Oak", Stream.of("Paddington", "Westbourne Park").collect(Collectors.toSet()));
		connections.put("Royal Victoria", Stream.of("Canning Town", "Custom House").collect(Collectors.toSet()));
		connections.put("Ruislip", Stream.of("Ickenham", "Ruislip Manor").collect(Collectors.toSet()));
		connections.put("Ruislip Gardens", Stream.of("West Ruislip", "South Ruislip").collect(Collectors.toSet()));
		connections.put("Ruislip Manor", Stream.of("Eastcote", "Ruislip").collect(Collectors.toSet()));
		connections.put("Russell Square", Stream.of("King's Cross St. Pancras", "Holborn").collect(Collectors.toSet()));
		connections.put("Seven Sisters", Stream.of("Tottenham Hale", "Finsbury Park").collect(Collectors.toSet()));
		connections.put("Shadwell",
				Stream.of("Wapping", "Tower Gateway", "Whitechapel", "Limehouse", "Bank").collect(Collectors.toSet()));
		connections.put("Shepherd's Bush (C)", Stream.of("White City", "Holland Park").collect(Collectors.toSet()));
		connections.put("Shepherd's Bush (H)", Stream.of("Goldhawk Road", "Latimer Road").collect(Collectors.toSet()));
		connections.put("Shoreditch", Stream.of("Whitechapel").collect(Collectors.toSet()));
		connections.put("Sloane Square", Stream.of("Victoria", "South Kensington").collect(Collectors.toSet()));
		connections.put("Snaresbrook", Stream.of("South Woodford", "Leytonstone").collect(Collectors.toSet()));
		connections.put("Southfields", Stream.of("East Putney", "Wimbledon Park").collect(Collectors.toSet()));
		connections.put("Southgate", Stream.of("Arnos Grove", "Oakwood").collect(Collectors.toSet()));
		connections.put("Southwark", Stream.of("Waterloo", "London Bridge").collect(Collectors.toSet()));
		connections.put("South Ealing", Stream.of("Northfields", "Acton Town").collect(Collectors.toSet()));
		connections.put("South Harrow", Stream.of("Rayners Lane", "Sudbury Hill").collect(Collectors.toSet()));
		connections.put("South Kensington", Stream.of("Gloucester Road", "Sloane Square").collect(Collectors.toSet()));
		connections.put("South Kenton", Stream.of("North Wembley", "Kenton").collect(Collectors.toSet()));
		connections.put("South Quay",
				Stream.of("Heron Quays", "Crossharbour & London Arena").collect(Collectors.toSet()));
		connections.put("South Ruislip", Stream.of("Northolt", "Ruislip Gardens").collect(Collectors.toSet()));
		connections.put("South Wimbledon", Stream.of("Morden", "Colliers Wood").collect(Collectors.toSet()));
		connections.put("South Woodford", Stream.of("Snaresbrook", "Woodford").collect(Collectors.toSet()));
		connections.put("Stamford Brook", Stream.of("Ravenscourt Park", "Turnham Green").collect(Collectors.toSet()));
		connections.put("Stanmore", Stream.of("Canons Park").collect(Collectors.toSet()));
		connections.put("Stepney Green", Stream.of("Mile End", "Whitechapel").collect(Collectors.toSet()));
		connections.put("Stockwell",
				Stream.of("Brixton", "Clapham North", "Oval", "Vauxhall").collect(Collectors.toSet()));
		connections.put("Stonebridge Park", Stream.of("Harlesden", "Wembley Central").collect(Collectors.toSet()));
		connections.put("Stratford",
				Stream.of("West Ham", "Mile End", "Leyton", "Pudding Mill Lane").collect(Collectors.toSet()));
		connections.put("St. James's Park", Stream.of("Victoria", "Westminster").collect(Collectors.toSet()));
		connections.put("St. John's Wood", Stream.of("Baker Street", "Swiss Cottage").collect(Collectors.toSet()));
		connections.put("St. Paul's", Stream.of("Chancery Lane", "Bank").collect(Collectors.toSet()));
		connections.put("Sudbury Hill", Stream.of("South Harrow", "Sudbury Town").collect(Collectors.toSet()));
		connections.put("Sudbury Town", Stream.of("Alperton", "Sudbury Hill").collect(Collectors.toSet()));
		connections.put("Surrey Quays",
				Stream.of("Canada Water", "New Cross", "New Cross Gate").collect(Collectors.toSet()));
		connections.put("Swiss Cottage", Stream.of("St. John's Wood", "Finchley Road").collect(Collectors.toSet()));
		connections.put("Temple", Stream.of("Embankment", "Blackfriars").collect(Collectors.toSet()));
		connections.put("Theydon Bois", Stream.of("Debden", "Epping").collect(Collectors.toSet()));
		connections.put("Tooting Bec", Stream.of("Tooting Broadway", "Balham").collect(Collectors.toSet()));
		connections.put("Tooting Broadway", Stream.of("Tooting Bec", "Colliers Wood").collect(Collectors.toSet()));
		connections.put("Tottenham Court Road",
				Stream.of("Goodge Street", "Leicester Square", "Holborn", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Tottenham Hale", Stream.of("Blackhorse Road", "Seven Sisters").collect(Collectors.toSet()));
		connections.put("Totteridge & Whetstone",
				Stream.of("High Barnet", "Woodside Park").collect(Collectors.toSet()));
		connections.put("Tower Gateway", Stream.of("Shadwell").collect(Collectors.toSet()));
		connections.put("Tower Hill", Stream.of("Aldgate", "Aldgate East", "Monument").collect(Collectors.toSet()));
		connections.put("Tufnell Park", Stream.of("Archway", "Kentish Town").collect(Collectors.toSet()));
		connections.put("Turnham Green",
				Stream.of("Stamford Brook", "Acton Town", "Chiswick Park", "Gunnersbury", "Hammersmith")
						.collect(Collectors.toSet()));
		connections.put("Turnpike Lane", Stream.of("Manor House", "Wood Green").collect(Collectors.toSet()));
		connections.put("Upminster", Stream.of("Upminster Bridge").collect(Collectors.toSet()));
		connections.put("Upminster Bridge", Stream.of("Hornchurch", "Upminster").collect(Collectors.toSet()));
		connections.put("Upney", Stream.of("Becontree", "Barking").collect(Collectors.toSet()));
		connections.put("Upton Park", Stream.of("Plaistow", "East Ham").collect(Collectors.toSet()));
		connections.put("Uxbridge", Stream.of("Hillingdon").collect(Collectors.toSet()));
		connections.put("Vauxhall", Stream.of("Stockwell", "Pimlico").collect(Collectors.toSet()));
		connections.put("Victoria",
				Stream.of("Sloane Square", "Pimlico", "St. James's Park", "Green Park").collect(Collectors.toSet()));
		connections.put("Walthamstow Central", Stream.of("Blackhorse Road").collect(Collectors.toSet()));
		connections.put("Wanstead", Stream.of("Redbridge", "Leytonstone").collect(Collectors.toSet()));
		connections.put("Wapping", Stream.of("Shadwell", "Rotherhithe").collect(Collectors.toSet()));
		connections.put("Warren Street",
				Stream.of("Goodge Street", "Euston", "Oxford Circus").collect(Collectors.toSet()));
		connections.put("Warwick Avenue", Stream.of("Paddington", "Maida Vale").collect(Collectors.toSet()));
		connections.put("Waterloo",
				Stream.of("Lambeth North", "Embankment", "Southwark", "Kennington", "Westminster", "Bank")
						.collect(Collectors.toSet()));
		connections.put("Watford", Stream.of("Croxley").collect(Collectors.toSet()));
		connections.put("Wembley Central", Stream.of("Stonebridge Park", "North Wembley").collect(Collectors.toSet()));
		connections.put("Wembley Park",
				Stream.of("Kingsbury", "Preston Road", "Neasden", "Finchley Road").collect(Collectors.toSet()));
		connections.put("Westbourne Park", Stream.of("Ladbroke Grove", "Royal Oak").collect(Collectors.toSet()));
		connections.put("Westferry", Stream.of("West India Quay", "Poplar", "Limehouse").collect(Collectors.toSet()));
		connections.put("Westminster",
				Stream.of("Waterloo", "Embankment", "St. James's Park", "Green Park").collect(Collectors.toSet()));
		connections.put("West Acton", Stream.of("North Acton", "Ealing Broadway").collect(Collectors.toSet()));
		connections.put("West Brompton", Stream.of("Fulham Broadway", "Earl's Court").collect(Collectors.toSet()));
		connections.put("West Finchley", Stream.of("Finchley Central", "Woodside Park").collect(Collectors.toSet()));
		connections.put("West Ham",
				Stream.of("Bromley-By-Bow", "Stratford", "Plaistow", "Canning Town").collect(Collectors.toSet()));
		connections.put("West Hampstead", Stream.of("Finchley Road", "Kilburn").collect(Collectors.toSet()));
		connections.put("West Harrow", Stream.of("Rayners Lane", "Harrow-on-the-Hill").collect(Collectors.toSet()));
		connections.put("West India Quay",
				Stream.of("Poplar", "Canary Wharf", "Westferry").collect(Collectors.toSet()));
		connections.put("West Kensington", Stream.of("Barons Court", "Earl's Court").collect(Collectors.toSet()));
		connections.put("West Ruislip", Stream.of("Ruislip Gardens").collect(Collectors.toSet()));
		connections.put("Whitechapel",
				Stream.of("Shadwell", "Aldgate East", "Stepney Green", "Shoreditch").collect(Collectors.toSet()));
		connections.put("White City", Stream.of("Shepherd's Bush (C)", "East Acton").collect(Collectors.toSet()));
		connections.put("Willesden Green", Stream.of("Dollis Hill", "Kilburn").collect(Collectors.toSet()));
		connections.put("Willesden Junction", Stream.of("Harlesden", "Kensal Green").collect(Collectors.toSet()));
		connections.put("Wimbledon", Stream.of("Wimbledon Park").collect(Collectors.toSet()));
		connections.put("Wimbledon Park", Stream.of("Southfields", "Wimbledon").collect(Collectors.toSet()));
		connections.put("Woodford",
				Stream.of("South Woodford", "Buckhurst Hill", "Roding Valley").collect(Collectors.toSet()));
		connections.put("Woodside Park",
				Stream.of("Totteridge & Whetstone", "West Finchley").collect(Collectors.toSet()));
		connections.put("Wood Green", Stream.of("Turnpike Lane", "Bounds Green").collect(Collectors.toSet()));

		return buildGraph(stations, connections);
	}

	public static Map<String, GraphNode<String, Station>> buildGraph(Map<String, Station> stations,
			Map<String, Set<String>> connections) {
		final Map<String, GraphNode<String, Station>> all_nodes = new HashMap<>();

		for (Map.Entry<String, Set<String>> station_connections : connections.entrySet()) {
			String from_name = station_connections.getKey();
			Station from = stations.get(from_name);
			GraphNode<String, Station> from_node = all_nodes.computeIfAbsent(from.name(),
					s -> new GraphNode<>(from_name, from));

			for (String to_name : station_connections.getValue()) {
				Station to = stations.get(to_name);
				GraphNode<String, Station> to_node = all_nodes.computeIfAbsent(to.name(),
						s -> new GraphNode<>(to_name, to));

				from_node.addNeighbour(to_node, HaversineScorer.computeCost(from, to));
			}
		}

		return all_nodes;
	}

	/*-
	 * Data taken from:
	 * https://github.com/eugenp/tutorials/blob/master/algorithms-miscellaneous-2/src/test/java/com/baeldung/algorithms/astar/underground/RouteFinderIntegrationTest.java
	 * Corrections: Knightsbridge (146) goes to Hyde Park Corner (133) and Gloucester Road (99) (not South Kensington 235)
	 */
	private static class BaeldungDataSet {
		static void generateDataStructures() {
			Map<Integer, Station> stations = new HashMap<>();

			/*-
			 * Find: 'add\(new Station\("(\d+)", '
			 * Replace: 'put(Integer.valueOf($1), new Station('
			 */
			stations.put(Integer.valueOf(1), new Station("Acton Town", 51.5028, -0.2801));
			stations.put(Integer.valueOf(2), new Station("Aldgate", 51.5143, -0.0755));
			stations.put(Integer.valueOf(3), new Station("Aldgate East", 51.5154, -0.0726));
			stations.put(Integer.valueOf(4), new Station("All Saints", 51.5107, -0.013));
			stations.put(Integer.valueOf(5), new Station("Alperton", 51.5407, -0.2997));
			stations.put(Integer.valueOf(6), new Station("Amersham", 51.6736, -0.607));
			stations.put(Integer.valueOf(7), new Station("Angel", 51.5322, -0.1058));
			stations.put(Integer.valueOf(8), new Station("Archway", 51.5653, -0.1353));
			stations.put(Integer.valueOf(9), new Station("Arnos Grove", 51.6164, -0.1331));
			stations.put(Integer.valueOf(10), new Station("Arsenal", 51.5586, -0.1059));
			stations.put(Integer.valueOf(11), new Station("Baker Street", 51.5226, -0.1571));
			stations.put(Integer.valueOf(12), new Station("Balham", 51.4431, -0.1525));
			stations.put(Integer.valueOf(13), new Station("Bank", 51.5133, -0.0886));
			stations.put(Integer.valueOf(14), new Station("Barbican", 51.5204, -0.0979));
			stations.put(Integer.valueOf(15), new Station("Barking", 51.5396, 0.081));
			stations.put(Integer.valueOf(16), new Station("Barkingside", 51.5856, 0.0887));
			stations.put(Integer.valueOf(17), new Station("Barons Court", 51.4905, -0.2139));
			stations.put(Integer.valueOf(18), new Station("Bayswater", 51.5121, -0.1879));
			stations.put(Integer.valueOf(19), new Station("Beckton", 51.5148, 0.0613));
			stations.put(Integer.valueOf(20), new Station("Beckton Park", 51.5087, 0.055));
			stations.put(Integer.valueOf(21), new Station("Becontree", 51.5403, 0.127));
			stations.put(Integer.valueOf(22), new Station("Belsize Park", 51.5504, -0.1642));
			stations.put(Integer.valueOf(23), new Station("Bermondsey", 51.4979, -0.0637));
			stations.put(Integer.valueOf(24), new Station("Bethnal Green", 51.527, -0.0549));
			stations.put(Integer.valueOf(25), new Station("Blackfriars", 51.512, -0.1031));
			stations.put(Integer.valueOf(26), new Station("Blackhorse Road", 51.5867, -0.0417));
			stations.put(Integer.valueOf(27), new Station("Blackwall", 51.5079, -0.0066));
			stations.put(Integer.valueOf(28), new Station("Bond Street", 51.5142, -0.1494));
			stations.put(Integer.valueOf(29), new Station("Borough", 51.5011, -0.0943));
			stations.put(Integer.valueOf(30), new Station("Boston Manor", 51.4956, -0.325));
			stations.put(Integer.valueOf(31), new Station("Bounds Green", 51.6071, -0.1243));
			stations.put(Integer.valueOf(32), new Station("Bow Church", 51.5273, -0.0208));
			stations.put(Integer.valueOf(33), new Station("Bow Road", 51.5269, -0.0247));
			stations.put(Integer.valueOf(34), new Station("Brent Cross", 51.5766, -0.2136));
			stations.put(Integer.valueOf(35), new Station("Brixton", 51.4627, -0.1145));
			stations.put(Integer.valueOf(36), new Station("Bromley-By-Bow", 51.5248, -0.0119));
			stations.put(Integer.valueOf(37), new Station("Buckhurst Hill", 51.6266, 0.0471));
			stations.put(Integer.valueOf(38), new Station("Burnt Oak", 51.6028, -0.2641));
			stations.put(Integer.valueOf(39), new Station("Caledonian Road", 51.5481, -0.1188));
			stations.put(Integer.valueOf(40), new Station("Camden Town", 51.5392, -0.1426));
			stations.put(Integer.valueOf(41), new Station("Canada Water", 51.4982, -0.0502));
			stations.put(Integer.valueOf(42), new Station("Canary Wharf", 51.5051, -0.0209));
			stations.put(Integer.valueOf(43), new Station("Canning Town", 51.5147, 0.0082));
			stations.put(Integer.valueOf(44), new Station("Cannon Street", 51.5113, -0.0904));
			stations.put(Integer.valueOf(45), new Station("Canons Park", 51.6078, -0.2947));
			stations.put(Integer.valueOf(46), new Station("Chalfont & Latimer", 51.6679, -0.561));
			stations.put(Integer.valueOf(47), new Station("Chalk Farm", 51.5441, -0.1538));
			stations.put(Integer.valueOf(48), new Station("Chancery Lane", 51.5185, -0.1111));
			stations.put(Integer.valueOf(49), new Station("Charing Cross", 51.508, -0.1247));
			stations.put(Integer.valueOf(50), new Station("Chesham", 51.7052, -0.611));
			stations.put(Integer.valueOf(51), new Station("Chigwell", 51.6177, 0.0755));
			stations.put(Integer.valueOf(52), new Station("Chiswick Park", 51.4946, -0.2678));
			stations.put(Integer.valueOf(53), new Station("Chorleywood", 51.6543, -0.5183));
			stations.put(Integer.valueOf(54), new Station("Clapham Common", 51.4618, -0.1384));
			stations.put(Integer.valueOf(55), new Station("Clapham North", 51.4649, -0.1299));
			stations.put(Integer.valueOf(56), new Station("Clapham South", 51.4527, -0.148));
			stations.put(Integer.valueOf(57), new Station("Cockfosters", 51.6517, -0.1496));
			stations.put(Integer.valueOf(58), new Station("Colindale", 51.5955, -0.2502));
			stations.put(Integer.valueOf(59), new Station("Colliers Wood", 51.418, -0.1778));
			stations.put(Integer.valueOf(60), new Station("Covent Garden", 51.5129, -0.1243));
			stations.put(Integer.valueOf(61), new Station("Crossharbour & London Arena", 51.4957, -0.0144));
			stations.put(Integer.valueOf(62), new Station("Croxley", 51.647, -0.4412));
			stations.put(Integer.valueOf(63), new Station("Custom House", 51.5095, 0.0276));
			stations.put(Integer.valueOf(64), new Station("Cutty Sark", 51.4827, -0.0096));
			stations.put(Integer.valueOf(65), new Station("Cyprus", 51.5085, 0.064));
			stations.put(Integer.valueOf(66), new Station("Dagenham East", 51.5443, 0.1655));
			stations.put(Integer.valueOf(67), new Station("Dagenham Heathway", 51.5417, 0.1469));
			stations.put(Integer.valueOf(68), new Station("Debden", 51.6455, 0.0838));
			stations.put(Integer.valueOf(69), new Station("Deptford Bridge", 51.474, -0.0216));
			stations.put(Integer.valueOf(70), new Station("Devons Road", 51.5223, -0.0173));
			stations.put(Integer.valueOf(71), new Station("Dollis Hill", 51.552, -0.2387));
			stations.put(Integer.valueOf(72), new Station("Ealing Broadway", 51.5152, -0.3017));
			stations.put(Integer.valueOf(73), new Station("Ealing Common", 51.5101, -0.2882));
			stations.put(Integer.valueOf(74), new Station("Earl's Court", 51.492, -0.1973));
			stations.put(Integer.valueOf(75), new Station("Eastcote", 51.5765, -0.397));
			stations.put(Integer.valueOf(76), new Station("East Acton", 51.5168, -0.2474));
			stations.put(Integer.valueOf(77), new Station("East Finchley", 51.5874, -0.165));
			stations.put(Integer.valueOf(78), new Station("East Ham", 51.5394, 0.0518));
			stations.put(Integer.valueOf(79), new Station("East India", 51.5093, -0.0021));
			stations.put(Integer.valueOf(80), new Station("East Putney", 51.4586, -0.2112));
			stations.put(Integer.valueOf(81), new Station("Edgware", 51.6137, -0.275));
			stations.put(Integer.valueOf(82), new Station("Edgware Road (B)", 51.5199, -0.1679));
			stations.put(Integer.valueOf(83), new Station("Edgware Road (C)", 51.5203, -0.17));
			stations.put(Integer.valueOf(84), new Station("Elephant & Castle", 51.4943, -0.1001));
			stations.put(Integer.valueOf(85), new Station("Elm Park", 51.5496, 0.1977));
			stations.put(Integer.valueOf(86), new Station("Elverson Road", 51.4693, -0.0174));
			stations.put(Integer.valueOf(87), new Station("Embankment", 51.5074, -0.1223));
			stations.put(Integer.valueOf(88), new Station("Epping", 51.6937, 0.1139));
			stations.put(Integer.valueOf(89), new Station("Euston", 51.5282, -0.1337));
			stations.put(Integer.valueOf(90), new Station("Euston Square", 51.526, -0.1359));
			stations.put(Integer.valueOf(91), new Station("Fairlop", 51.596, 0.0912));
			stations.put(Integer.valueOf(92), new Station("Farringdon", 51.5203, -0.1053));
			stations.put(Integer.valueOf(93), new Station("Finchley Central", 51.6012, -0.1932));
			stations.put(Integer.valueOf(94), new Station("Finchley Road", 51.5472, -0.1803));
			stations.put(Integer.valueOf(95), new Station("Finsbury Park", 51.5642, -0.1065));
			stations.put(Integer.valueOf(96), new Station("Fulham Broadway", 51.4804, -0.195));
			stations.put(Integer.valueOf(97), new Station("Gallions Reach", 51.5096, 0.0716));
			stations.put(Integer.valueOf(98), new Station("Gants Hill", 51.5765, 0.0663));
			stations.put(Integer.valueOf(99), new Station("Gloucester Road", 51.4945, -0.1829));
			stations.put(Integer.valueOf(100), new Station("Golders Green", 51.5724, -0.1941));
			stations.put(Integer.valueOf(101), new Station("Goldhawk Road", 51.5018, -0.2267));
			stations.put(Integer.valueOf(102), new Station("Goodge Street", 51.5205, -0.1347));
			stations.put(Integer.valueOf(103), new Station("Grange Hill", 51.6132, 0.0923));
			stations.put(Integer.valueOf(104), new Station("Great Portland Street", 51.5238, -0.1439));
			stations.put(Integer.valueOf(105), new Station("Greenford", 51.5423, -0.3456));
			stations.put(Integer.valueOf(106), new Station("Greenwich", 51.4781, -0.0149));
			stations.put(Integer.valueOf(107), new Station("Green Park", 51.5067, -0.1428));
			stations.put(Integer.valueOf(108), new Station("Gunnersbury", 51.4915, -0.2754));
			stations.put(Integer.valueOf(109), new Station("Hainault", 51.603, 0.0933));
			stations.put(Integer.valueOf(110), new Station("Hammersmith", 51.4936, -0.2251));
			stations.put(Integer.valueOf(111), new Station("Hampstead", 51.5568, -0.178));
			stations.put(Integer.valueOf(112), new Station("Hanger Lane", 51.5302, -0.2933));
			stations.put(Integer.valueOf(113), new Station("Harlesden", 51.5362, -0.2575));
			stations.put(Integer.valueOf(114), new Station("Harrow & Wealdston", 51.5925, -0.3351));
			stations.put(Integer.valueOf(115), new Station("Harrow-on-the-Hill", 51.5793, -0.3366));
			stations.put(Integer.valueOf(116), new Station("Hatton Cross", 51.4669, -0.4227));
			stations.put(Integer.valueOf(117), new Station("Heathrow Terminals 1, 2 & 3", 51.4713, -0.4524));
			stations.put(Integer.valueOf(118), new Station("Heathrow Terminal 4", 51.4598, -0.4476));
			stations.put(Integer.valueOf(119), new Station("Hendon Central", 51.5829, -0.2259));
			stations.put(Integer.valueOf(120), new Station("Heron Quays", 51.5033, -0.0215));
			stations.put(Integer.valueOf(121), new Station("High Barnet", 51.6503, -0.1943));
			stations.put(Integer.valueOf(122), new Station("High Street Kensington", 51.5009, -0.1925));
			stations.put(Integer.valueOf(123), new Station("Highbury & Islington", 51.546, -0.104));
			stations.put(Integer.valueOf(124), new Station("Highgate", 51.5777, -0.1458));
			stations.put(Integer.valueOf(125), new Station("Hillingdon", 51.5538, -0.4499));
			stations.put(Integer.valueOf(126), new Station("Holborn", 51.5174, -0.12));
			stations.put(Integer.valueOf(127), new Station("Holland Park", 51.5075, -0.206));
			stations.put(Integer.valueOf(128), new Station("Holloway Road", 51.5526, -0.1132));
			stations.put(Integer.valueOf(129), new Station("Hornchurch", 51.5539, 0.2184));
			stations.put(Integer.valueOf(130), new Station("Hounslow Central", 51.4713, -0.3665));
			stations.put(Integer.valueOf(131), new Station("Hounslow East", 51.4733, -0.3564));
			stations.put(Integer.valueOf(132), new Station("Hounslow West", 51.4734, -0.3855));
			stations.put(Integer.valueOf(133), new Station("Hyde Park Corner", 51.5027, -0.1527));
			stations.put(Integer.valueOf(134), new Station("Ickenham", 51.5619, -0.4421));
			stations.put(Integer.valueOf(135), new Station("Island Gardens", 51.4871, -0.0101));
			stations.put(Integer.valueOf(136), new Station("Kennington", 51.4884, -0.1053));
			stations.put(Integer.valueOf(137), new Station("Kensal Green", 51.5304, -0.225));
			stations.put(Integer.valueOf(138), new Station("Kensington (Olympia)", 51.4983, -0.2106));
			stations.put(Integer.valueOf(139), new Station("Kentish Town", 51.5507, -0.1402));
			stations.put(Integer.valueOf(140), new Station("Kenton", 51.5816, -0.3162));
			stations.put(Integer.valueOf(141), new Station("Kew Gardens", 51.477, -0.285));
			stations.put(Integer.valueOf(142), new Station("Kilburn", 51.5471, -0.2047));
			stations.put(Integer.valueOf(143), new Station("Kilburn Park", 51.5351, -0.1939));
			stations.put(Integer.valueOf(144), new Station("Kingsbury", 51.5846, -0.2786));
			stations.put(Integer.valueOf(145), new Station("King's Cross St. Pancras", 51.5308, -0.1238));
			stations.put(Integer.valueOf(146), new Station("Knightsbridge", 51.5015, -0.1607));
			stations.put(Integer.valueOf(147), new Station("Ladbroke Grove", 51.5172, -0.2107));
			stations.put(Integer.valueOf(148), new Station("Lambeth North", 51.4991, -0.1115));
			stations.put(Integer.valueOf(149), new Station("Lancaster Gate", 51.5119, -0.1756));
			stations.put(Integer.valueOf(150), new Station("Latimer Road", 51.5139, -0.2172));
			stations.put(Integer.valueOf(151), new Station("Leicester Square", 51.5113, -0.1281));
			stations.put(Integer.valueOf(152), new Station("Lewisham", 51.4657, -0.0142));
			stations.put(Integer.valueOf(153), new Station("Leyton", 51.5566, -0.0053));
			stations.put(Integer.valueOf(154), new Station("Leytonstone", 51.5683, 0.0083));
			stations.put(Integer.valueOf(155), new Station("Limehouse", 51.5123, -0.0396));
			stations.put(Integer.valueOf(156), new Station("Liverpool Street", 51.5178, -0.0823));
			stations.put(Integer.valueOf(157), new Station("London Bridge", 51.5052, -0.0864));
			stations.put(Integer.valueOf(158), new Station("Loughton", 51.6412, 0.0558));
			stations.put(Integer.valueOf(159), new Station("Maida Vale", 51.53, -0.1854));
			stations.put(Integer.valueOf(160), new Station("Manor House", 51.5712, -0.0958));
			stations.put(Integer.valueOf(161), new Station("Mansion House", 51.5122, -0.094));
			stations.put(Integer.valueOf(162), new Station("Marble Arch", 51.5136, -0.1586));
			stations.put(Integer.valueOf(163), new Station("Marylebone", 51.5225, -0.1631));
			stations.put(Integer.valueOf(164), new Station("Mile End", 51.5249, -0.0332));
			stations.put(Integer.valueOf(165), new Station("Mill Hill East", 51.6082, -0.2103));
			stations.put(Integer.valueOf(166), new Station("Monument", 51.5108, -0.0863));
			stations.put(Integer.valueOf(167), new Station("Moorgate", 51.5186, -0.0886));
			stations.put(Integer.valueOf(168), new Station("Moor Park", 51.6294, -0.432));
			stations.put(Integer.valueOf(169), new Station("Morden", 51.4022, -0.1948));
			stations.put(Integer.valueOf(170), new Station("Mornington Crescent", 51.5342, -0.1387));
			stations.put(Integer.valueOf(171), new Station("Mudchute", 51.4902, -0.0145));
			stations.put(Integer.valueOf(172), new Station("Neasden", 51.5542, -0.2503));
			stations.put(Integer.valueOf(173), new Station("Newbury Park", 51.5756, 0.0899));
			stations.put(Integer.valueOf(174), new Station("New Cross", 51.4767, -0.0327));
			stations.put(Integer.valueOf(175), new Station("New Cross Gate", 51.4757, -0.0402));
			stations.put(Integer.valueOf(176), new Station("Northfields", 51.4995, -0.3142));
			stations.put(Integer.valueOf(177), new Station("Northolt", 51.5483, -0.3687));
			stations.put(Integer.valueOf(178), new Station("Northwick Park", 51.5784, -0.3184));
			stations.put(Integer.valueOf(179), new Station("Northwood", 51.6111, -0.424));
			stations.put(Integer.valueOf(180), new Station("Northwood Hills", 51.6004, -0.4092));
			stations.put(Integer.valueOf(181), new Station("North Acton", 51.5237, -0.2597));
			stations.put(Integer.valueOf(182), new Station("North Ealing", 51.5175, -0.2887));
			stations.put(Integer.valueOf(183), new Station("North Greenwich", 51.5005, 0.0039));
			stations.put(Integer.valueOf(184), new Station("North Harrow", 51.5846, -0.3626));
			stations.put(Integer.valueOf(185), new Station("North Wembley", 51.5621, -0.3034));
			stations.put(Integer.valueOf(186), new Station("Notting Hill Gate", 51.5094, -0.1967));
			stations.put(Integer.valueOf(187), new Station("Oakwood", 51.6476, -0.1318));
			stations.put(Integer.valueOf(188), new Station("Old Street", 51.5263, -0.0873));
			stations.put(Integer.valueOf(189), new Station("Osterley", 51.4813, -0.3522));
			stations.put(Integer.valueOf(190), new Station("Oval", 51.4819, -0.113));
			stations.put(Integer.valueOf(191), new Station("Oxford Circus", 51.515, -0.1415));
			stations.put(Integer.valueOf(192), new Station("Paddington", 51.5154, -0.1755));
			stations.put(Integer.valueOf(193), new Station("Park Royal", 51.527, -0.2841));
			stations.put(Integer.valueOf(194), new Station("Parsons Green", 51.4753, -0.2011));
			stations.put(Integer.valueOf(195), new Station("Perivale", 51.5366, -0.3232));
			stations.put(Integer.valueOf(196), new Station("Picadilly Circus", 51.5098, -0.1342));
			stations.put(Integer.valueOf(197), new Station("Pimlico", 51.4893, -0.1334));
			stations.put(Integer.valueOf(198), new Station("Pinner", 51.5926, -0.3805));
			stations.put(Integer.valueOf(199), new Station("Plaistow", 51.5313, 0.0172));
			stations.put(Integer.valueOf(200), new Station("Poplar", 51.5077, -0.0173));
			stations.put(Integer.valueOf(201), new Station("Preston Road", 51.572, -0.2954));
			stations.put(Integer.valueOf(202), new Station("Prince Regent", 51.5093, 0.0336));
			stations.put(Integer.valueOf(203), new Station("Pudding Mill Lane", 51.5343, -0.0139));
			stations.put(Integer.valueOf(204), new Station("Putney Bridge", 51.4682, -0.2089));
			stations.put(Integer.valueOf(205), new Station("Queen's Park", 51.5341, -0.2047));
			stations.put(Integer.valueOf(206), new Station("Queensbury", 51.5942, -0.2861));
			stations.put(Integer.valueOf(207), new Station("Queensway", 51.5107, -0.1877));
			stations.put(Integer.valueOf(208), new Station("Ravenscourt Park", 51.4942, -0.2359));
			stations.put(Integer.valueOf(209), new Station("Rayners Lane", 51.5753, -0.3714));
			stations.put(Integer.valueOf(210), new Station("Redbridge", 51.5763, 0.0454));
			stations.put(Integer.valueOf(211), new Station("Regent's Park", 51.5234, -0.1466));
			stations.put(Integer.valueOf(212), new Station("Richmond", 51.4633, -0.3013));
			stations.put(Integer.valueOf(213), new Station("Rickmansworth", 51.6404, -0.4733));
			stations.put(Integer.valueOf(214), new Station("Roding Valley", 51.6171, 0.0439));
			stations.put(Integer.valueOf(215), new Station("Rotherhithe", 51.501, -0.0525));
			stations.put(Integer.valueOf(216), new Station("Royal Albert", 51.5084, 0.0465));
			stations.put(Integer.valueOf(217), new Station("Royal Oak", 51.519, -0.188));
			stations.put(Integer.valueOf(218), new Station("Royal Victoria", 51.5091, 0.0181));
			stations.put(Integer.valueOf(219), new Station("Ruislip", 51.5715, -0.4213));
			stations.put(Integer.valueOf(220), new Station("Ruislip Gardens", 51.5606, -0.4103));
			stations.put(Integer.valueOf(221), new Station("Ruislip Manor", 51.5732, -0.4125));
			stations.put(Integer.valueOf(222), new Station("Russell Square", 51.523, -0.1244));
			stations.put(Integer.valueOf(223), new Station("Seven Sisters", 51.5822, -0.0749));
			stations.put(Integer.valueOf(224), new Station("Shadwell", 51.5117, -0.056));
			stations.put(Integer.valueOf(225), new Station("Shepherd's Bush (C)", 51.5046, -0.2187));
			stations.put(Integer.valueOf(226), new Station("Shepherd's Bush (H)", 51.5058, -0.2265));
			stations.put(Integer.valueOf(227), new Station("Shoreditch", 51.5227, -0.0708));
			stations.put(Integer.valueOf(228), new Station("Sloane Square", 51.4924, -0.1565));
			stations.put(Integer.valueOf(229), new Station("Snaresbrook", 51.5808, 0.0216));
			stations.put(Integer.valueOf(230), new Station("Southfields", 51.4454, -0.2066));
			stations.put(Integer.valueOf(231), new Station("Southgate", 51.6322, -0.128));
			stations.put(Integer.valueOf(232), new Station("Southwark", 51.501, -0.1052));
			stations.put(Integer.valueOf(233), new Station("South Ealing", 51.5011, -0.3072));
			stations.put(Integer.valueOf(234), new Station("South Harrow", 51.5646, -0.3521));
			stations.put(Integer.valueOf(235), new Station("South Kensington", 51.4941, -0.1738));
			stations.put(Integer.valueOf(236), new Station("South Kenton", 51.5701, -0.3081));
			stations.put(Integer.valueOf(237), new Station("South Quay", 51.5007, -0.0191));
			stations.put(Integer.valueOf(238), new Station("South Ruislip", 51.5569, -0.3988));
			stations.put(Integer.valueOf(239), new Station("South Wimbledon", 51.4154, -0.1919));
			stations.put(Integer.valueOf(240), new Station("South Woodford", 51.5917, 0.0275));
			stations.put(Integer.valueOf(241), new Station("Stamford Brook", 51.495, -0.2459));
			stations.put(Integer.valueOf(242), new Station("Stanmore", 51.6194, -0.3028));
			stations.put(Integer.valueOf(243), new Station("Stepney Green", 51.5221, -0.047));
			stations.put(Integer.valueOf(244), new Station("Stockwell", 51.4723, -0.123));
			stations.put(Integer.valueOf(245), new Station("Stonebridge Park", 51.5439, -0.2759));
			stations.put(Integer.valueOf(246), new Station("Stratford", 51.5416, -0.0042));
			stations.put(Integer.valueOf(247), new Station("St. James's Park", 51.4994, -0.1335));
			stations.put(Integer.valueOf(248), new Station("St. John's Wood", 51.5347, -0.174));
			stations.put(Integer.valueOf(249), new Station("St. Paul's", 51.5146, -0.0973));
			stations.put(Integer.valueOf(250), new Station("Sudbury Hill", 51.5569, -0.3366));
			stations.put(Integer.valueOf(251), new Station("Sudbury Town", 51.5507, -0.3156));
			stations.put(Integer.valueOf(252), new Station("Surrey Quays", 51.4933, -0.0478));
			stations.put(Integer.valueOf(253), new Station("Swiss Cottage", 51.5432, -0.1738));
			stations.put(Integer.valueOf(254), new Station("Temple", 51.5111, -0.1141));
			stations.put(Integer.valueOf(255), new Station("Theydon Bois", 51.6717, 0.1033));
			stations.put(Integer.valueOf(256), new Station("Tooting Bec", 51.4361, -0.1598));
			stations.put(Integer.valueOf(257), new Station("Tooting Broadway", 51.4275, -0.168));
			stations.put(Integer.valueOf(258), new Station("Tottenham Court Road", 51.5165, -0.131));
			stations.put(Integer.valueOf(259), new Station("Tottenham Hale", 51.5882, -0.0594));
			stations.put(Integer.valueOf(260), new Station("Totteridge & Whetstone", 51.6302, -0.1791));
			stations.put(Integer.valueOf(261), new Station("Tower Gateway", 51.5106, -0.0743));
			stations.put(Integer.valueOf(262), new Station("Tower Hill", 51.5098, -0.0766));
			stations.put(Integer.valueOf(263), new Station("Tufnell Park", 51.5567, -0.1374));
			stations.put(Integer.valueOf(264), new Station("Turnham Green", 51.4951, -0.2547));
			stations.put(Integer.valueOf(265), new Station("Turnpike Lane", 51.5904, -0.1028));
			stations.put(Integer.valueOf(266), new Station("Upminster", 51.559, 0.251));
			stations.put(Integer.valueOf(267), new Station("Upminster Bridge", 51.5582, 0.2343));
			stations.put(Integer.valueOf(268), new Station("Upney", 51.5385, 0.1014));
			stations.put(Integer.valueOf(269), new Station("Upton Park", 51.5352, 0.0343));
			stations.put(Integer.valueOf(270), new Station("Uxbridge", 51.5463, -0.4786));
			stations.put(Integer.valueOf(271), new Station("Vauxhall", 51.4861, -0.1253));
			stations.put(Integer.valueOf(272), new Station("Victoria", 51.4965, -0.1447));
			stations.put(Integer.valueOf(273), new Station("Walthamstow Central", 51.583, -0.0195));
			stations.put(Integer.valueOf(274), new Station("Wanstead", 51.5775, 0.0288));
			stations.put(Integer.valueOf(275), new Station("Wapping", 51.5043, -0.0558));
			stations.put(Integer.valueOf(276), new Station("Warren Street", 51.5247, -0.1384));
			stations.put(Integer.valueOf(277), new Station("Warwick Avenue", 51.5235, -0.1835));
			stations.put(Integer.valueOf(278), new Station("Waterloo", 51.5036, -0.1143));
			stations.put(Integer.valueOf(279), new Station("Watford", 51.6573, -0.4177));
			stations.put(Integer.valueOf(280), new Station("Wembley Central", 51.5519, -0.2963));
			stations.put(Integer.valueOf(281), new Station("Wembley Park", 51.5635, -0.2795));
			stations.put(Integer.valueOf(282), new Station("Westbourne Park", 51.521, -0.2011));
			stations.put(Integer.valueOf(283), new Station("Westferry", 51.5097, -0.0265));
			stations.put(Integer.valueOf(284), new Station("Westminster", 51.501, -0.1254));
			stations.put(Integer.valueOf(285), new Station("West Acton", 51.518, -0.2809));
			stations.put(Integer.valueOf(286), new Station("West Brompton", 51.4872, -0.1953));
			stations.put(Integer.valueOf(287), new Station("West Finchley", 51.6095, -0.1883));
			stations.put(Integer.valueOf(288), new Station("West Ham", 51.5287, 0.0056));
			stations.put(Integer.valueOf(289), new Station("West Hampstead", 51.5469, -0.1906));
			stations.put(Integer.valueOf(290), new Station("West Harrow", 51.5795, -0.3533));
			stations.put(Integer.valueOf(291), new Station("West India Quay", 51.507, -0.0203));
			stations.put(Integer.valueOf(292), new Station("West Kensington", 51.4907, -0.2065));
			stations.put(Integer.valueOf(293), new Station("West Ruislip", 51.5696, -0.4376));
			stations.put(Integer.valueOf(294), new Station("Whitechapel", 51.5194, -0.0612));
			stations.put(Integer.valueOf(295), new Station("White City", 51.512, -0.2239));
			stations.put(Integer.valueOf(296), new Station("Willesden Green", 51.5492, -0.2215));
			stations.put(Integer.valueOf(297), new Station("Willesden Junction", 51.5326, -0.2478));
			stations.put(Integer.valueOf(298), new Station("Wimbledon", 51.4214, -0.2064));
			stations.put(Integer.valueOf(299), new Station("Wimbledon Park", 51.4343, -0.1992));
			stations.put(Integer.valueOf(300), new Station("Woodford", 51.607, 0.0341));
			stations.put(Integer.valueOf(301), new Station("Woodside Park", 51.6179, -0.1856));
			stations.put(Integer.valueOf(302), new Station("Wood Green", 51.5975, -0.1097));

			Map<Integer, Set<Integer>> connections = new HashMap<>();
			/*-
			 * Find: 'connections\.put\("(\d+)",'
			 * Replace: 'connections\.put(Integer.valueOf($1),'
			 *
			 * Find: 'Stream\.of\("(\d+)"'
			 * Replace: 'IntStream.of($1'
			 *
			 * Find: '"(\d+)"'
			 * Replace: '$1'
			 *
			 * Find: '(\d+)\)\.collect\('
			 * Replace: '$1).mapToObj(Integer::valueOf).collect('
			 */
			connections.put(Integer.valueOf(1),
					IntStream.of(52, 73, 73, 233, 264).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(2),
					IntStream.of(156, 262, 156).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(3),
					IntStream.of(262, 294, 156, 294).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(4),
					IntStream.of(70, 200).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(5),
					IntStream.of(193, 251).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(6),
					IntStream.of(46).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(7),
					IntStream.of(145, 188).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(8),
					IntStream.of(124, 263).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(9),
					IntStream.of(31, 231).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(10),
					IntStream.of(95, 128).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(11), IntStream.of(163, 211, 83, 104, 83, 104, 28, 248, 94, 104)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(12),
					IntStream.of(56, 256).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(13),
					IntStream.of(156, 249, 224, 157, 167, 278).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(14),
					IntStream.of(92, 167, 92, 167, 92, 167).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(15),
					IntStream.of(78, 268, 78).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(16),
					IntStream.of(91, 173).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(17),
					IntStream.of(110, 292, 74, 110).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(18),
					IntStream.of(186, 192, 186, 192).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(19),
					IntStream.of(97).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(20),
					IntStream.of(65, 216).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(21),
					IntStream.of(67, 268).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(22),
					IntStream.of(47, 111).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(23),
					IntStream.of(41, 157).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(24),
					IntStream.of(156, 164).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(25),
					IntStream.of(161, 254, 161, 254).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(26),
					IntStream.of(259, 273).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(27),
					IntStream.of(79, 200).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(28),
					IntStream.of(162, 191, 11, 107).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(29),
					IntStream.of(84, 157).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(30),
					IntStream.of(176, 189).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(31),
					IntStream.of(9, 302).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(32),
					IntStream.of(70, 203).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(33),
					IntStream.of(36, 164, 36, 164).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(34),
					IntStream.of(100, 119).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(35),
					IntStream.of(244).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(36),
					IntStream.of(33, 288, 33, 288).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(37),
					IntStream.of(158, 300).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(38),
					IntStream.of(58, 81).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(39),
					IntStream.of(128, 145).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(40),
					IntStream.of(47, 89, 139, 170).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(41),
					IntStream.of(215, 252, 23, 42).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(42),
					IntStream.of(120, 291, 41, 183).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(43),
					IntStream.of(79, 218, 183, 288).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(44),
					IntStream.of(161, 166, 161, 166).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(45),
					IntStream.of(206, 242).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(46),
					IntStream.of(6, 50, 53).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(47),
					IntStream.of(22, 40).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(48),
					IntStream.of(126, 249).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(49),
					IntStream.of(87, 196, 87, 151).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(50),
					IntStream.of(46).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(51),
					IntStream.of(103, 214).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(52),
					IntStream.of(1, 264).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(53),
					IntStream.of(46, 213).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(54),
					IntStream.of(55, 56).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(55),
					IntStream.of(54, 244).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(56),
					IntStream.of(12, 54).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(57),
					IntStream.of(187).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(58),
					IntStream.of(38, 119).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(59),
					IntStream.of(239, 257).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(60),
					IntStream.of(126, 151).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(61),
					IntStream.of(171, 237).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(62),
					IntStream.of(168, 279).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(63),
					IntStream.of(202, 218).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(64),
					IntStream.of(106, 135).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(65),
					IntStream.of(20, 97).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(66),
					IntStream.of(67, 85).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(67),
					IntStream.of(21, 66).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(68),
					IntStream.of(158, 255).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(69),
					IntStream.of(86, 106).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(70),
					IntStream.of(4, 32).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(71),
					IntStream.of(172, 296).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(72),
					IntStream.of(285, 73).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(73),
					IntStream.of(72, 1, 1, 182).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(74), IntStream.of(99, 122, 138, 286, 292, 17, 99).mapToObj(Integer::valueOf)
					.collect(Collectors.toSet()));
			connections.put(Integer.valueOf(75),
					IntStream.of(209, 221, 209, 221).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(76),
					IntStream.of(181, 295).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(77),
					IntStream.of(93, 124).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(78),
					IntStream.of(15, 269, 15, 269).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(79),
					IntStream.of(27, 43).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(80),
					IntStream.of(204, 230).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(81),
					IntStream.of(38).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(82),
					IntStream.of(163, 192).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(83),
					IntStream.of(11, 192, 192, 11, 192).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(84),
					IntStream.of(148, 29, 136).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(85),
					IntStream.of(66, 129).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(86),
					IntStream.of(69, 152).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(87), IntStream.of(49, 278, 254, 284, 254, 284, 49, 278)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(88),
					IntStream.of(255).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(89),
					IntStream.of(40, 145, 170, 276, 145, 276).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(90),
					IntStream.of(104, 145, 104, 145, 104, 145).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(91),
					IntStream.of(16, 109).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(92),
					IntStream.of(14, 145, 14, 145, 14, 145).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(93),
					IntStream.of(77, 165, 287).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(94),
					IntStream.of(253, 289, 11, 281).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(95),
					IntStream.of(10, 160, 123, 223).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(96),
					IntStream.of(194, 286).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(97),
					IntStream.of(19, 65).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(98),
					IntStream.of(173, 210).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(99),
					IntStream.of(122, 235, 74, 235, 74, 235).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(100),
					IntStream.of(34, 111).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(101),
					IntStream.of(110, 226).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(102),
					IntStream.of(258, 276).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(103),
					IntStream.of(51, 109).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(104),
					IntStream.of(11, 90, 11, 90, 11, 90).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(105),
					IntStream.of(177, 195).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(106),
					IntStream.of(64, 69).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(107),
					IntStream.of(28, 284, 133, 196, 191, 272).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(108),
					IntStream.of(141, 264).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(109),
					IntStream.of(91, 103).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(110),
					IntStream.of(17, 208, 101, 17, 264).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(111),
					IntStream.of(22, 100).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(112),
					IntStream.of(181, 195).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(113),
					IntStream.of(245, 297).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(114),
					IntStream.of(140).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(115),
					IntStream.of(178, 184, 290).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(116),
					IntStream.of(117, 118, 132).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(117),
					IntStream.of(116, 118).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(118),
					IntStream.of(116, 117).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(119),
					IntStream.of(34, 58).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(120),
					IntStream.of(42, 237).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(121),
					IntStream.of(260).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(122),
					IntStream.of(99, 186, 74, 186).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(123),
					IntStream.of(95, 145).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(124),
					IntStream.of(8, 77).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(125),
					IntStream.of(134, 270, 134, 270).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(126),
					IntStream.of(48, 258, 60, 222).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(127),
					IntStream.of(186, 225).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(128),
					IntStream.of(10, 39).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(129),
					IntStream.of(85, 267).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(130),
					IntStream.of(131, 132).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(131),
					IntStream.of(130, 189).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(132),
					IntStream.of(116, 130).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(133),
					IntStream.of(107, 146).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(134),
					IntStream.of(125, 219, 125, 219).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(135),
					IntStream.of(64, 171).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(136),
					IntStream.of(84, 190, 278).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(137),
					IntStream.of(205, 297).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(138),
					IntStream.of(74).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(139),
					IntStream.of(40, 263).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(140),
					IntStream.of(114, 236).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(141),
					IntStream.of(108, 212).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(142),
					IntStream.of(289, 296).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(143),
					IntStream.of(159, 205).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(144),
					IntStream.of(206, 281).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(145), IntStream.of(90, 92, 90, 92, 90, 92, 7, 89, 39, 222, 89, 123)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(146),
					IntStream.of(133, 235).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(147),
					IntStream.of(150, 282).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(148),
					IntStream.of(84, 278).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(149),
					IntStream.of(162, 207).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(150),
					IntStream.of(147, 226).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(151),
					IntStream.of(49, 258, 60, 196).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(152),
					IntStream.of(86).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(153),
					IntStream.of(154, 246).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(154),
					IntStream.of(153, 229, 274).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(155),
					IntStream.of(224, 283).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(156), IntStream.of(13, 24, 2, 167, 3, 167, 2, 167)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(157),
					IntStream.of(23, 232, 13, 29).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(158),
					IntStream.of(37, 68).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(159),
					IntStream.of(143, 277).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(160),
					IntStream.of(95, 265).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(161),
					IntStream.of(25, 44, 25, 44).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(162),
					IntStream.of(28, 149).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(163),
					IntStream.of(11, 82).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(164),
					IntStream.of(24, 246, 33, 243, 33, 243).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(165),
					IntStream.of(93).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(166),
					IntStream.of(44, 262, 44, 262).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(167), IntStream.of(14, 156, 14, 156, 14, 156, 13, 188)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(168),
					IntStream.of(62, 179, 213).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(169),
					IntStream.of(239).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(170),
					IntStream.of(40, 89).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(171),
					IntStream.of(61, 135).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(172),
					IntStream.of(71, 281).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(173),
					IntStream.of(16, 98).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(174),
					IntStream.of(252).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(175),
					IntStream.of(252).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(176),
					IntStream.of(30, 233).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(177),
					IntStream.of(105, 238).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(178),
					IntStream.of(115, 201).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(179),
					IntStream.of(168, 180).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(180),
					IntStream.of(179, 198).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(181),
					IntStream.of(76, 112, 285).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(182),
					IntStream.of(73, 193).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(183),
					IntStream.of(42, 43).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(184),
					IntStream.of(115, 198).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(185),
					IntStream.of(236, 280).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(186),
					IntStream.of(127, 207, 18, 122, 18, 122).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(187),
					IntStream.of(57, 231).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(188),
					IntStream.of(7, 167).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(189),
					IntStream.of(30, 131).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(190),
					IntStream.of(136, 244).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(191),
					IntStream.of(196, 211, 28, 258, 107, 276).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(192), IntStream.of(82, 277, 18, 83, 18, 83, 83, 217)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(193),
					IntStream.of(5, 182).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(194),
					IntStream.of(96, 204).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(195),
					IntStream.of(105, 112).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(196),
					IntStream.of(49, 191, 107, 151).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(197),
					IntStream.of(271, 272).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(198),
					IntStream.of(180, 184).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(199),
					IntStream.of(269, 288, 269, 288).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(200),
					IntStream.of(4, 27, 283, 291).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(201),
					IntStream.of(178, 281).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(202),
					IntStream.of(63, 216).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(203),
					IntStream.of(32, 246).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(204),
					IntStream.of(80, 194).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(205),
					IntStream.of(137, 143).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(206),
					IntStream.of(45, 144).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(207),
					IntStream.of(149, 186).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(208),
					IntStream.of(110, 241).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(209),
					IntStream.of(75, 290, 75, 234).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(210),
					IntStream.of(98, 274).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(211),
					IntStream.of(11, 191).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(212),
					IntStream.of(141).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(213),
					IntStream.of(53, 168).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(214),
					IntStream.of(51, 300).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(215),
					IntStream.of(41, 275).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(216),
					IntStream.of(20, 202).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(217),
					IntStream.of(192, 282).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(218),
					IntStream.of(43, 63).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(219),
					IntStream.of(134, 221, 134, 221).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(220),
					IntStream.of(238, 293).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(221),
					IntStream.of(75, 219, 75, 219).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(222),
					IntStream.of(126, 145).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(223),
					IntStream.of(95, 259).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(224),
					IntStream.of(13, 155, 261, 275, 294).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(225),
					IntStream.of(127, 295).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(226),
					IntStream.of(101, 150).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(227),
					IntStream.of(294).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(228),
					IntStream.of(235, 272, 235, 272).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(229),
					IntStream.of(154, 240).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(230),
					IntStream.of(80, 299).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(231),
					IntStream.of(9, 187).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(232),
					IntStream.of(157, 278).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(233),
					IntStream.of(1, 176).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(234),
					IntStream.of(209, 250).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(235),
					IntStream.of(99, 228, 99, 228, 99, 146).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(236),
					IntStream.of(140, 185).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(237),
					IntStream.of(61, 120).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(238),
					IntStream.of(177, 220).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(239),
					IntStream.of(59, 169).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(240),
					IntStream.of(229, 300).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(241),
					IntStream.of(208, 264).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(242),
					IntStream.of(45).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(243),
					IntStream.of(164, 294, 164, 294).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(244),
					IntStream.of(55, 190, 35, 271).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(245),
					IntStream.of(113, 280).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(246),
					IntStream.of(153, 164, 203, 288).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(247),
					IntStream.of(272, 284, 272, 284).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(248),
					IntStream.of(11, 253).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(249),
					IntStream.of(13, 48).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(250),
					IntStream.of(234, 251).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(251),
					IntStream.of(5, 250).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(252),
					IntStream.of(41, 174, 175).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(253),
					IntStream.of(94, 248).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(254),
					IntStream.of(25, 87, 25, 87).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(255),
					IntStream.of(68, 88).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(256),
					IntStream.of(12, 257).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(257),
					IntStream.of(59, 256).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(258),
					IntStream.of(126, 191, 102, 151).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(259),
					IntStream.of(26, 223).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(260),
					IntStream.of(121, 301).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(261),
					IntStream.of(224).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(262),
					IntStream.of(2, 166, 3, 166).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(263),
					IntStream.of(8, 139).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(264),
					IntStream.of(52, 108, 241, 1, 110).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(265),
					IntStream.of(160, 302).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(266),
					IntStream.of(267).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(267),
					IntStream.of(129, 266).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(268),
					IntStream.of(15, 21).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(269),
					IntStream.of(78, 199, 78, 199).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(270),
					IntStream.of(125, 125).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(271),
					IntStream.of(197, 244).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(272),
					IntStream.of(228, 247, 228, 247, 107, 197).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(273),
					IntStream.of(26).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(274),
					IntStream.of(154, 210).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(275),
					IntStream.of(215, 224).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(276),
					IntStream.of(89, 102, 89, 191).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(277),
					IntStream.of(159, 192).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(278), IntStream.of(87, 148, 232, 284, 87, 136, 13)
					.mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(279),
					IntStream.of(62).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(280),
					IntStream.of(185, 245).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(281),
					IntStream.of(144, 172, 94, 201).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(282),
					IntStream.of(147, 217).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(283),
					IntStream.of(155, 200, 291).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(284),
					IntStream.of(87, 247, 87, 247, 107, 278).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(285),
					IntStream.of(72, 181).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(286),
					IntStream.of(74, 96).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(287),
					IntStream.of(93, 301).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(288),
					IntStream.of(36, 199, 36, 199, 43, 246).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(289),
					IntStream.of(94, 142).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(290),
					IntStream.of(115, 209).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(291),
					IntStream.of(42, 200, 283).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(292),
					IntStream.of(17, 74).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(293),
					IntStream.of(220).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(294),
					IntStream.of(3, 243, 224, 227, 3, 243).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(295),
					IntStream.of(76, 225).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(296),
					IntStream.of(71, 142).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(297),
					IntStream.of(113, 137).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(298),
					IntStream.of(299).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(299),
					IntStream.of(230, 298).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(300),
					IntStream.of(37, 214, 240).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(301),
					IntStream.of(260, 287).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			connections.put(Integer.valueOf(302),
					IntStream.of(31, 265).mapToObj(Integer::valueOf).collect(Collectors.toSet()));

			// Knightsbridge (146) goes to Gloucester Road (99) and Hyde Park Corner (133)
			// and not South Kensington (235)
			connections.put(Integer.valueOf(146),
					IntStream.of(99, 133).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			// South Kensington (235) goes to Gloucester Road (99) and Sloane Square (228)
			// and not Knightsbridge (146)
			connections.put(Integer.valueOf(235),
					IntStream.of(99, 228).mapToObj(Integer::valueOf).collect(Collectors.toSet()));
			// Gloucester Road (99) goes to High Street Kensington (122), Earl's Court (74),
			// South Kensington (235), and Knightsbridge (146) - Baeldung is missing
			// Knightsbridge (146)
			connections.put(Integer.valueOf(99),
					IntStream.of(122, 74, 235, 146).mapToObj(Integer::valueOf).collect(Collectors.toSet()));

			System.out.println("\tpublic static Map<String, GraphNode<String, Station>> getGraph2() {");
			System.out.println("\t\tfinal Map<String, Station> stations = new HashMap<>();");
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(4);
			for (Station station : stations.values()) {
				System.out.format("\t\tstations.put(\"%s\", new Station(\"%s\", %s, %s));%n", station.name(),
						station.name(), df.format(station.latitude()), df.format(station.longitude()));
			}
			System.out.println();

			System.out.println("\t\tfinal Map<String, Set<String>> connections = new HashMap<>();");
			for (Map.Entry<Integer, Set<Integer>> station_connections : connections.entrySet()) {
				System.out.format("\t\tconnections.put(\"%s\", Stream.of(%s).collect(Collectors.toSet()));%n",
						stations.get(station_connections.getKey()).name(), String.join(", ", station_connections
								.getValue().stream().map(i -> "\"" + stations.get(i).name() + "\"").toList()));
			}
			System.out.println();

			System.out.println("\t\treturn buildGraph(stations, connections);");
			System.out.println("\t}");
		}
	}
}
