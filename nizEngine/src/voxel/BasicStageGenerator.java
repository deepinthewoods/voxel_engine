package voxel;

import com.niz.SimplexNoise;

public class BasicStageGenerator extends PerlinNoiseGenerator {
	
	public static void generateVoxels(VoxelWorld voxelWorld, int min, int max, int octaveCount) {		
		int idx = 0;
		for(int z = 0; z < voxelWorld.voxelsZ; z++) 
			for(int x = 0; x < voxelWorld.voxelsX; x++) {
				boolean flat = true;
				float noise = noise(x, z, .05f);
				if (noise(x,z+566, .02f) > -.62f)noise = 1;;
				if (noise > -.15f && noise < .15f) flat = false;
				voxelWorld.setColumn(x, 
						flat?1:2
								, z, flat?(byte) 10:1);
				
			}
	}

	private static float noise(int x, int z, float scale) {
		
		return SimplexNoise.noise(x*scale, z*scale);
	}
}
