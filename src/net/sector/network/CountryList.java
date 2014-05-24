package net.sector.network;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.sector.Constants;
import net.sector.util.Log;


/**
 * @author Ondřej Hruška (MightyPork)
 */
public class CountryList {
	private static final boolean DEBUG = Constants.LOG_COUNTRIES;

	private static boolean nextBig = false, nextSmall = false, nextFictional = false;

	/**
	 * Country list entry
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 */
	public static class Country implements Comparable<Country> {
		/** Tow-letter country code */
		public String code;
		/** English country name */
		public String name;

		public boolean isBig = false;
		public boolean isSmall = false;
		public boolean isFictional = false;

		/**
		 * Make country entry
		 * 
		 * @param code 2-letter country code
		 * @param name country name
		 */
		public Country(String code, String name) {
			this.code = code;
			this.name = name;
			isBig = CountryList.nextBig;
			isSmall = CountryList.nextSmall;
			isFictional = CountryList.nextFictional;

		}

		@Override
		public int compareTo(Country o) {
			return name.compareTo(o.name);
		}
	}



	/** Big, more important countries */
	public static ArrayList<Country> big = new ArrayList<CountryList.Country>();

	/** Small countries (< 250.000 internet users) */
	public static ArrayList<Country> small = new ArrayList<CountryList.Country>();

	/** Merged big and small lists */
	public static ArrayList<Country> all = new ArrayList<CountryList.Country>();

	public static HashMap<String, String> codeToName = new HashMap<String, String>();

	/**
	 * Get name for code
	 * 
	 * @param code code
	 * @return name for code
	 */
	public static String getName(String code) {
		String name = codeToName.get(code);
		if (name != null) return name;
		return "";
	}

	/**
	 * Initialize country list
	 */
	public static void init() {

		Log.f1("Initializing country list...");

		if (DEBUG) Log.f3("Adding fictional countries");
		nextFictional = true;
		// fictional
		small.add(new Country("~AT", "Atlantis"));
		small.add(new Country("~OZ", "The Land of Oz"));
		small.add(new Country("~NN", "Narnia"));
		small.add(new Country("~MI", "Middle-Earth"));
		small.add(new Country("~DW", "Discworld"));
		small.add(new Country("~LP", "Lilliput"));
		small.add(new Country("~BL", "Blefuscu"));
		small.add(new Country("~UT", "Utopia"));
		small.add(new Country("~SH", "Shangri-La"));
		small.add(new Country("~NV", "Neverland"));
		small.add(new Country("~NH", "Neverhood"));
		nextFictional = false;

		if (DEBUG) Log.f3("Adding big countries");
		// big
		nextBig = true;
		big.add(new Country("AF", "Afghanistan"));
		big.add(new Country("AL", "Albania"));
		big.add(new Country("DZ", "Algeria"));
		big.add(new Country("AO", "Angola"));
		big.add(new Country("AR", "Argentina"));
		big.add(new Country("AM", "Armenia"));
		big.add(new Country("AU", "Australia"));
		big.add(new Country("AT", "Austria"));
		big.add(new Country("AZ", "Azerbaijan"));
		big.add(new Country("BH", "Bahrain"));
		big.add(new Country("BD", "Bangladesh"));
		big.add(new Country("BY", "Belarus"));
		big.add(new Country("BE", "Belgium"));
		big.add(new Country("BJ", "Benin"));
		big.add(new Country("BO", "Bolivia"));
		big.add(new Country("BA", "Bosnia and Herzegovina"));
		big.add(new Country("BR", "Brazil"));
		big.add(new Country("BG", "Bulgaria"));
		big.add(new Country("BF", "Burkina Faso"));
		big.add(new Country("KH", "Cambodia"));
		big.add(new Country("CM", "Cameroon"));
		big.add(new Country("CA", "Canada"));
		big.add(new Country("CL", "Chile"));
		big.add(new Country("CN", "China"));
		big.add(new Country("CC", "Cocos Islands"));
		big.add(new Country("CO", "Colombia"));
		big.add(new Country("CG", "Congo"));
		big.add(new Country("CR", "Costa Rica"));
		big.add(new Country("HR", "Croatia"));
		big.add(new Country("CU", "Cuba"));
		big.add(new Country("CY", "Cyprus"));
		big.add(new Country("CZ", "Czech Republic"));
		big.add(new Country("DK", "Denmark"));
		big.add(new Country("DO", "Dominican Republic"));
		big.add(new Country("EC", "Ecuador"));
		big.add(new Country("EG", "Egypt"));
		big.add(new Country("SV", "El Salvador"));
		big.add(new Country("ER", "Eritrea"));
		big.add(new Country("EE", "Estonia"));
		big.add(new Country("ET", "Ethiopia"));
		big.add(new Country("FI", "Finland"));
		big.add(new Country("FR", "France"));
		big.add(new Country("GE", "Georgia"));
		big.add(new Country("DE", "Germany"));
		big.add(new Country("GH", "Ghana"));
		big.add(new Country("GR", "Greece"));
		big.add(new Country("GT", "Guatemala"));
		big.add(new Country("HT", "Haiti"));
		big.add(new Country("HN", "Honduras"));
		big.add(new Country("HK", "Hong Kong"));
		big.add(new Country("HU", "Hungary"));
		big.add(new Country("IS", "Iceland"));
		big.add(new Country("IN", "India"));
		big.add(new Country("ID", "Indonesia"));
		big.add(new Country("IR", "Iran"));
		big.add(new Country("IQ", "Iraq"));
		big.add(new Country("IE", "Ireland"));
		big.add(new Country("IL", "Israel"));
		big.add(new Country("IT", "Italy"));
		big.add(new Country("CI", "Ivory Coast"));
		big.add(new Country("JM", "Jamaica"));
		big.add(new Country("JP", "Japan"));
		big.add(new Country("JO", "Jordan"));
		big.add(new Country("KZ", "Kazakstan"));
		big.add(new Country("KE", "Kenya"));
		big.add(new Country("KW", "Kuwait"));
		big.add(new Country("KG", "Kyrgyzstan"));
		big.add(new Country("LA", "Laos"));
		big.add(new Country("LV", "Latvia"));
		big.add(new Country("LB", "Lebanon"));
		big.add(new Country("LT", "Lithuania"));
		big.add(new Country("LU", "Luxembourg"));
		big.add(new Country("MO", "Macao"));
		big.add(new Country("MK", "Macedonia"));
		big.add(new Country("MG", "Madagascar"));
		big.add(new Country("MW", "Malawi"));
		big.add(new Country("MY", "Malaysia"));
		big.add(new Country("ML", "Mali"));
		big.add(new Country("MQ", "Martinique"));
		big.add(new Country("MU", "Mauritius"));
		big.add(new Country("MX", "Mexico"));
		big.add(new Country("MD", "Moldova"));
		big.add(new Country("MN", "Mongolia"));
		big.add(new Country("MA", "Morocco"));
		big.add(new Country("MZ", "Mozambique"));
		big.add(new Country("MM", "Myanmar"));
		big.add(new Country("NA", "Namibia"));
		big.add(new Country("NP", "Nepal"));
		big.add(new Country("NL", "Netherlands"));
		big.add(new Country("NZ", "New Zealand"));
		big.add(new Country("NI", "Nicaragua"));
		big.add(new Country("NG", "Nigeria"));
		big.add(new Country("NO", "Norway"));
		big.add(new Country("OM", "Oman"));
		big.add(new Country("PK", "Pakistan"));
		big.add(new Country("PS", "Palestine"));
		big.add(new Country("PA", "Panama"));
		big.add(new Country("PY", "Paraguay"));
		big.add(new Country("PE", "Peru"));
		big.add(new Country("PH", "Philippines"));
		big.add(new Country("PL", "Poland"));
		big.add(new Country("PT", "Portugal"));
		big.add(new Country("PR", "Puerto Rico"));
		big.add(new Country("RO", "Romania"));
		big.add(new Country("RU", "Russian Federation"));
		big.add(new Country("RW", "Rwanda"));
		big.add(new Country("SA", "Saudi Arabia"));
		big.add(new Country("SN", "Senegal"));
		big.add(new Country("SL", "Sierra Leone"));
		big.add(new Country("SG", "Singapore"));
		big.add(new Country("SK", "Slovakia"));
		big.add(new Country("SI", "Slovenia"));
		big.add(new Country("ZA", "South Africa"));
		big.add(new Country("KR", "South Korea"));
		big.add(new Country("ES", "Spain"));
		big.add(new Country("LK", "Sri Lanka"));
		big.add(new Country("SD", "Sudan"));
		big.add(new Country("SE", "Sweden"));
		big.add(new Country("CH", "Switzerland"));
		big.add(new Country("SY", "Syria"));
		big.add(new Country("RS", "Serbia"));
		big.add(new Country("TW", "Taiwan"));
		big.add(new Country("TJ", "Tajikistan"));
		big.add(new Country("TZ", "Tanzania"));
		big.add(new Country("TH", "Thailand"));
		big.add(new Country("TN", "Tunisia"));
		big.add(new Country("TR", "Turkey"));
		big.add(new Country("TM", "Turkmenistan"));
		big.add(new Country("UG", "Uganda"));
		big.add(new Country("UA", "Ukraine"));
		big.add(new Country("AE", "United Arab Emirates"));
		big.add(new Country("GB", "United Kingdom"));
		big.add(new Country("US", "United States"));
		big.add(new Country("UY", "Uruguay"));
		big.add(new Country("UZ", "Uzbekistan"));
		big.add(new Country("VE", "Venezuela"));
		big.add(new Country("VN", "Vietnam"));
		big.add(new Country("EH", "Western Sahara"));
		big.add(new Country("YE", "Yemen"));
		big.add(new Country("ZM", "Zambia"));
		big.add(new Country("ZW", "Zimbabwe"));
		nextBig = false;


		if (DEBUG) Log.f3("Adding small countries");
		// small
		nextSmall = true;
		small.add(new Country("AQ", "Antarctica"));
		small.add(new Country("AS", "American Samoa"));
		small.add(new Country("AD", "Andorra"));
		small.add(new Country("AI", "Anguilla"));
		small.add(new Country("AG", "Antigua and Barbuda"));
		small.add(new Country("AW", "Aruba"));
		small.add(new Country("BS", "Bahamas"));
		small.add(new Country("BB", "Barbados"));
		small.add(new Country("BZ", "Belize"));
		small.add(new Country("BM", "Bermuda"));
		small.add(new Country("BT", "Bhutan"));
		small.add(new Country("BW", "Botswana"));
		small.add(new Country("BV", "Bouvet Island"));
		small.add(new Country("IO", "Chagos Islands"));
		small.add(new Country("BN", "Brunei"));
		small.add(new Country("BI", "Burundi"));
		small.add(new Country("CV", "Cape Verde"));
		small.add(new Country("KY", "Cayman Islands"));
		small.add(new Country("CF", "Central African Republic"));
		small.add(new Country("TD", "Chad"));
		small.add(new Country("CX", "Christmas Island"));
		small.add(new Country("KM", "Comoros"));
		small.add(new Country("CD", "Congo, Democratic Republic"));
		small.add(new Country("CK", "Cook Islands"));
		small.add(new Country("DJ", "Djibouti"));
		small.add(new Country("DM", "Dominica"));
		small.add(new Country("TP", "East Timor"));
		small.add(new Country("GQ", "Equatorial Guinea"));
		small.add(new Country("FK", "Falkland Islands"));
		small.add(new Country("FO", "Faroe Islands"));
		small.add(new Country("FJ", "Fiji Islands"));
		small.add(new Country("GF", "French Guiana"));
		small.add(new Country("PF", "French Polynesia"));
		small.add(new Country("GA", "Gabon"));
		small.add(new Country("GM", "Gambia"));
		small.add(new Country("GI", "Gibraltar"));
		small.add(new Country("GL", "Greenland"));
		small.add(new Country("GD", "Grenada"));
		small.add(new Country("GP", "Guadeloupe"));
		small.add(new Country("GU", "Guam"));
		small.add(new Country("GN", "Guinea"));
		small.add(new Country("GW", "Guinea-Bissau"));
		small.add(new Country("GY", "Guyana"));
		small.add(new Country("VA", "Vatican"));
		small.add(new Country("KI", "Kiribati"));
		small.add(new Country("LS", "Lesotho"));
		small.add(new Country("LR", "Liberia"));
		small.add(new Country("LY", "Libya"));
		small.add(new Country("LI", "Liechtenstein"));
		small.add(new Country("MV", "Maldives"));
		small.add(new Country("MT", "Malta"));
		small.add(new Country("MH", "Marshall Islands"));
		small.add(new Country("MR", "Mauritania"));
		small.add(new Country("YT", "Mayotte"));
		small.add(new Country("FM", "Micronesia"));
		small.add(new Country("MC", "Monaco"));
		small.add(new Country("MS", "Montserrat"));
		small.add(new Country("ME", "Montenegro"));
		small.add(new Country("NR", "Nauru"));
		small.add(new Country("AN", "Netherlands Antilles"));
		small.add(new Country("NC", "New Caledonia"));
		small.add(new Country("NE", "Niger"));
		small.add(new Country("NU", "Niue"));
		small.add(new Country("NF", "Norfolk Island"));
		small.add(new Country("KP", "North Korea"));
		small.add(new Country("MP", "Northern Mariana Islands"));
		small.add(new Country("PW", "Palau"));
		small.add(new Country("PG", "Papua New Guinea"));
		small.add(new Country("PN", "Pitcairn"));
		small.add(new Country("QA", "Qatar"));
		small.add(new Country("RE", "Réunion"));
		small.add(new Country("SH", "Saint Helena"));
		small.add(new Country("KN", "Saint Kitts and Nevis"));
		small.add(new Country("LC", "Saint Lucia"));
		small.add(new Country("PM", "Saint Pierre and Miquelon"));
		small.add(new Country("VC", "Saint Vincent"));
		small.add(new Country("WS", "Samoa"));
		small.add(new Country("SM", "San Marino"));
		small.add(new Country("ST", "Sao Tome and Principe"));
		small.add(new Country("SC", "Seychelles"));
		small.add(new Country("SB", "Solomon Islands"));
		small.add(new Country("SO", "Somalia"));
		small.add(new Country("GS", "South Georgia"));
		small.add(new Country("SR", "Suriname"));
		small.add(new Country("SJ", "Svalbard and Jan Mayen"));
		small.add(new Country("SZ", "Swaziland"));
		small.add(new Country("TG", "Togo"));
		small.add(new Country("TK", "Tokelau"));
		small.add(new Country("TO", "Tonga"));
		small.add(new Country("TT", "Trinidad and Tobago"));
		small.add(new Country("TC", "Turks and Caicos Islands"));
		small.add(new Country("TV", "Tuvalu"));
		small.add(new Country("VU", "Vanuatu"));
		small.add(new Country("VI", "Virgin Islands"));
		small.add(new Country("VG", "Virgin Islands, British"));
		small.add(new Country("WF", "Wallis and Futuna"));
		nextSmall = false;


		if (DEBUG) Log.f3("Merging and sorting country lists");
		all.addAll(big);
		all.addAll(small);

		Collections.sort(big);
		Collections.sort(small);
		Collections.sort(all);

		for (Country c : all) {
			codeToName.put(c.code, c.name);
		}
	}
}
