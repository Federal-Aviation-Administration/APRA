/*
 * Federal Aviation Administration (FAA) public work 
 * 
 * As a work of the United States Government, this project is in the 
 * public domain within the United States. Additionally, we waive copyright 
 * and related rights in the work worldwide 
 * through the Creative Commons 0 (CC0) 1.0 Universal public domain dedication
 * 
 * APRA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package gov.faa.ait.apra.bootstrap;

import java.util.ArrayList;
import java.util.List;

/** 
 * This is a convenience class to hold the TPP volumes and verify membership in the volume list
 * @author FAA
 *
 */
public class TPPVolume {
	
	// Make the list a static final so it becomes immutable and cannot be set by another class.
	// Items within the list can be added and removed by this class only
	private static final List <String> volumes = new ArrayList <> ();
	
	// A private null constructor to protect the volume list
	private TPPVolume () { } 
	
	static {
		TPPVolume.initVolumes();
	}
	
	/**
	 * Get the list TPP volumes contained within the reference data set
	 * @return a new list TPP volumes
	 */
	public static List <String> getVolumeList () {
		// We don't want to return our list here because it is static and we don't want someone adding
		// new items to the list which could cause a memory leak or other bugs. 
		return new ArrayList <> (volumes);
	}
	
	private static void initVolumes () {
		volumes.add("NE-1");
		volumes.add("NE-2");
		volumes.add("NE-3");
		volumes.add("NE-4");
		volumes.add("SE-1");
		volumes.add("SE-2");
		volumes.add("SE-3");
		volumes.add("SE-4");
		volumes.add("SC-1");
		volumes.add("SC-2");
		volumes.add("SC-3");
		volumes.add("SC-4");
		volumes.add("SC-5");
		volumes.add("EC-1");
		volumes.add("EC-2");
		volumes.add("EC-3");
		volumes.add("NC-1");
		volumes.add("NC-2");
		volumes.add("NC-3");
		volumes.add("SW-1");
		volumes.add("SW-2");
		volumes.add("SW-3");
		volumes.add("SW-4");
		volumes.add("NW-1");
		volumes.add("AK-1");
		volumes.add("PC-1");
	}
	/**
	 * Determine if a volume passed to this method is one of the valid TPP volumes
	 * @param volumeName the name of the volume to be checked and verified
	 * @return true if the volume is valid, false otherwise
	 */
	public static boolean isValidVolumeName (String volumeName) {
		return TPPVolume.volumes.contains(volumeName);
	}
}
