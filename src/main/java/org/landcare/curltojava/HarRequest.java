/*
 * © Copyright 2015
 * Landcare Research
 * 
 * Dual License with
 * 
 * GPL v3 - See http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Any derivative work needs to be contributed back to this project
 * unless otherwise agreed with Landcare Research, New Zealand.
 */
package org.landcare.curltojava;

import edu.umass.cs.benchlab.har.HarBrowser;
import edu.umass.cs.benchlab.har.HarEntries;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarLog;
import edu.umass.cs.benchlab.har.tools.HarFileReader;
import edu.umass.cs.benchlab.har.tools.HarFileWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author heuert@landcareresearch.co.nz
 */
public class HarRequest {

	public static void main(String[] args) {
		String fileName = "test.smap.landcareresearch.co.nz.har";
		File f = new File(fileName);
		HarFileReader r = new HarFileReader();
		//HarFileWriter w = new HarFileWriter();
		System.out.println("Reading " + fileName);
		HarLog log;
		try {
			log = r.readHarFile(f);
			// Access all elements as objects
			//HarBrowser browser = log.getBrowser();
			HarEntries entries = log.getEntries();
			List<HarEntry> hentries = entries.getEntries();
			for (HarEntry entry : hentries) {
				System.out.println("entry: "+ entry.getRequest());
			}
			//browser.
		} catch (IOException ex) {
			Logger.getLogger(HarRequest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
