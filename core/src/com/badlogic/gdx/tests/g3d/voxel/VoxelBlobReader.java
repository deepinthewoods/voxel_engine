package com.badlogic.gdx.tests.g3d.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by niz on 15/06/2014.
 * Reads bytes from a blob file
 */
public class VoxelBlobReader implements Pool.Poolable {

    private static final String FILE_EXTENSION = ".blob";
    private static final String FILE_ACCESS_STRING = "rw";
    public int x,y,z;
    public FileHandle handle;
    public static final int GRANULARITY_X = 4, GRANULARITY_Y = 4, GRANULARITY_Z = 4, SECTOR_SIZE = 4096
            , SECTOR_DATA_SIZE = 10//in bytes
            , SECTOR_DATA_TOTAL = GRANULARITY_X * GRANULARITY_Y * GRANULARITY_Z
            , INITIAL_SECTORS_TOTAL = ((SECTOR_DATA_TOTAL*SECTOR_DATA_SIZE)/SECTOR_SIZE)+1
            ;
    private byte[] sectorInfoBytes = new byte[SECTOR_DATA_TOTAL*SECTOR_DATA_SIZE];
    private long[] sectorOffsets = new long[SECTOR_DATA_TOTAL];
    private short[] sectorLengths = new short[SECTOR_DATA_TOTAL];

    //private BufferedInputStream is;
    //private BufferedOutputStream os;

    private RandomAccessFile file;


    public Object lock = new Object();

    public void init(int x, int y, int z, String path){
        this.x = x;
        this.y = y;
        this.z = z;
        handle = Gdx.files.external(path+x+"-"+y+"-"+z+FILE_EXTENSION);
        if (!handle.exists()){

            BufferedOutputStream os = (BufferedOutputStream) handle.write(false, SECTOR_SIZE);
            writeInitial(os);
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();

            }

        }

        try {
            file = new RandomAccessFile(handle.file(), FILE_ACCESS_STRING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }

        try {
            file.seek(0);
            file.read(sectorInfoBytes, 0, SECTOR_DATA_TOTAL);
        } catch (IOException e) {
            e.printStackTrace();

        }
        processSectorInfo(sectorInfoBytes);
    }

    private void writeInitial(BufferedOutputStream os) {

        try {
            for (int i = 0; i < INITIAL_SECTORS_TOTAL; i++)
                for (int b = 0; b < SECTOR_SIZE; b++)
                    os.write(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeNewSectorInfo(int index, long offset, short size){
        try {
            file.seek(index*SECTOR_DATA_SIZE);
            file.write(byteArrayFromLong(offset));
            file.write(byteArrayFromShort(size));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] byteArrayFromShort(short a) {
        shortRet[0] = (byte) ((a >> 8) & 0xFF);
        shortRet[1] = (byte) ((a >> 0) & 0xFF);

        return shortRet;
    }


    private void processSectorInfo(byte[] sectorInfoBytes) {
        for (int i = 0; i < SECTOR_DATA_TOTAL; i++){
            sectorOffsets[i] = longFromBytes(sectorInfoBytes, i*10);
            sectorLengths[i] = shortFromBytes(sectorInfoBytes, i*10+8);
        }
    }

    private short shortFromBytes(byte[] b, int off) {
        short val = 0;
        val |= (b[off] & 0xff);
        val |= (b[off+1] & 0xff) << 8;
        return val;
    }

    private long longFromBytes(byte[] b, int off) {
        long value = 0;
        for (int i = 0; i < 8; i++)
            value |= (b[off+i] & 0xff) << (i*8);
        return value;
    }

    byte[] longRet = new byte[8];
    byte[] shortRet = new byte[2];
    public byte[] byteArrayFromLong(long a)
    {
        longRet[0] = (byte) ((a >> 52) & 0xFF);
        longRet[1] = (byte) ((a >> 48) & 0xFF);
        longRet[2] = (byte) ((a >> 40) & 0xFF);
        longRet[3] = (byte) ((a >> 32) & 0xFF);

        longRet[4] = (byte) ((a >> 24) & 0xFF);
        longRet[5] = (byte) ((a >> 16) & 0xFF);
        longRet[6] = (byte) ((a >> 8) & 0xFF);
        longRet[7] = (byte) ((a >> 0) & 0xFF);


        return longRet;
    }

    private boolean inProgress;
    int currentIndex, progress, sectorTotal;
    //returns true if successful



    public boolean beginRead(VoxelChunk chunk){

        Vector3 offset = chunk.offset;
        int x = (int) offset.x;
        int y = (int) offset.y;
        int z = (int) offset.z;
        x /= chunk.width;
        y /= chunk.height;
        z /= chunk.depth;

        synchronized (lock){
            if (inProgress) return false;
            inProgress = true;

        }
        currentIndex = getIndex(x,y,z);
        progress = 0;
        sectorTotal = sectorLengths[currentIndex];
        try {

            file.seek(sectorOffsets[currentIndex]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }



    private int getIndex(int x, int y, int z) {
        return (x % GRANULARITY_X) + ((y % GRANULARITY_Y) * GRANULARITY_X)+ ((z % GRANULARITY_Z) * GRANULARITY_X * GRANULARITY_Y );
    }


    //returns true if finished
    public boolean processRead(Array<BlobByteArray> byteArrays){

        //read one buffer worth
        //read sectors
        BlobByteArray blobBytes = Pools.obtain(BlobByteArray.class);
        try {
            file.read(blobBytes.bytes);
            byteArrays.add(blobBytes);
        } catch (IOException e) {
            e.printStackTrace();

        }

        synchronized (lock) {
            progress++;
            if (progress == sectorTotal) {

                inProgress = false;
                return true;
            }


            return false;
        }
    }

    //returns true if successfuly started
    public boolean beginWrite(VoxelChunk chunk, Array<BlobByteArray> byteArrays){
        int x = (int) chunk.offset.x;
        int y = (int) chunk.offset.y;
        int z = (int) chunk.offset.z;
        x /= chunk.width;
        y /= chunk.height;
        z /= chunk.depth;
        synchronized (lock){
            if (inProgress) return false;
            inProgress = true;

        }
        currentIndex = getIndex(x,y,z);
        progress = 0;
        sectorTotal = sectorLengths[currentIndex];
        short sectors = (short) byteArrays.size;
        if (sectors > sectorTotal){
            //look for a free space and modify beginning of file
            long newOffset = getNewOffset(sectors);
            sectorLengths[currentIndex] = sectors;
            sectorOffsets[currentIndex] = newOffset;
            writeNewSectorInfo(currentIndex, newOffset, sectors);
            sectorTotal = sectors;

        }
        try {

            file.seek(sectorOffsets[currentIndex]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //true if finished
    public boolean processWrite(Array<BlobByteArray> byteArrays){
        BlobByteArray blobBytes = byteArrays.get(progress);
        try {
            file.write(blobBytes.bytes);
        } catch (IOException e) {
            e.printStackTrace();

        }

        synchronized (lock) {
            progress++;
            if (progress == sectorTotal) {

                inProgress = false;
                return true;
            }


            return false;
        }
    }

    private long getNewOffset(short sectors) {
        //just looking for last one. TODO make a better algo, this will cause fragmentation
        long off = INITIAL_SECTORS_TOTAL*SECTOR_SIZE;
        for (int i = 0; i < SECTOR_DATA_TOTAL; i++){
            off = Math.max(off, sectorOffsets[i]+sectorLengths[i]*SECTOR_SIZE);
        }

        return off;
    }


    public boolean containsChunk(VoxelChunk chunk) {
        synchronized (lock){
            Vector3 offset = chunk.offset;
            int x = (int) offset.x;
            int y = (int) offset.y;
            int z = (int) offset.z;
            x /= chunk.width;
            y /= chunk.height;
            z /= chunk.depth;


        }

        int index = getIndex(x,y,z);
        if (sectorOffsets[index] == 0)
            return false;
        return true;
    }

    public void reset() {
    }
}
