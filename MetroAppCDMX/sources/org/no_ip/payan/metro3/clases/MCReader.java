package org.no_ip.payan.metro3.clases;

import android.content.Context;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;
import android.util.SparseArray;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MCReader {
    private static final String LOG_TAG = MCReader.class.getSimpleName();
    public static final String NO_DATA = "--------------------------------";
    public static final String NO_KEY = "------------";
    private int mFirstSector = 0;
    private SparseArray<byte[][]> mKeyMap = new SparseArray<>();
    private int mKeyMapStatus = 0;
    private ArrayList<byte[]> mKeysWithOrder;
    private int mLastSector = -1;
    private final MifareClassic mMFC;

    public enum Preference {
        AutoReconnect("auto_reconnect"),
        SaveLastUsedKeyFiles("save_last_used_key_files"),
        UseCustomSectorCount("use_custom_sector_count"),
        CustomSectorCount("custom_sector_count"),
        UseInternalStorage("use_internal_storage"),
        RetryAuthentication("retry_authentication");
        
        private final String text;

        private Preference(String text2) {
            this.text = text2;
        }

        public String toString() {
            return this.text;
        }
    }

    private MCReader(Tag tag) {
        try {
            this.mMFC = MifareClassic.get(tag);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not create MIFARE Classic reader for theprovided tag (even after patching it).");
            throw e;
        }
    }

    public static Tag patchTag(Tag tag) {
        IBinder tagService;
        if (tag == null) {
            return null;
        }
        String[] techList = tag.getTechList();
        Parcel oldParcel = Parcel.obtain();
        tag.writeToParcel(oldParcel, 0);
        oldParcel.setDataPosition(0);
        int len = oldParcel.readInt();
        byte[] id = new byte[0];
        if (len >= 0) {
            id = new byte[len];
            oldParcel.readByteArray(id);
        }
        int[] oldTechList = new int[oldParcel.readInt()];
        oldParcel.readIntArray(oldTechList);
        Bundle[] oldTechExtras = (Bundle[]) oldParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oldParcel.readInt();
        int isMock = oldParcel.readInt();
        if (isMock == 0) {
            tagService = oldParcel.readStrongBinder();
        } else {
            tagService = null;
        }
        oldParcel.recycle();
        int nfcaIdx = -1;
        int mcIdx = -1;
        short sak = 0;
        boolean isFirstSak = true;
        for (int i = 0; i < techList.length; i++) {
            if (techList[i].equals(NfcA.class.getName())) {
                if (nfcaIdx == -1) {
                    nfcaIdx = i;
                }
                if (oldTechExtras[i] != null && oldTechExtras[i].containsKey("sak")) {
                    sak = (short) (oldTechExtras[i].getShort("sak") | sak);
                    if (nfcaIdx == i) {
                        isFirstSak = true;
                    } else {
                        isFirstSak = false;
                    }
                }
            } else if (techList[i].equals(MifareClassic.class.getName())) {
                mcIdx = i;
            }
        }
        boolean modified = false;
        if (!isFirstSak) {
            oldTechExtras[nfcaIdx].putShort("sak", sak);
            modified = true;
        }
        if (!(nfcaIdx == -1 || mcIdx == -1 || oldTechExtras[mcIdx] != null)) {
            oldTechExtras[mcIdx] = oldTechExtras[nfcaIdx];
            modified = true;
        }
        if (!modified) {
            return tag;
        }
        Parcel newParcel = Parcel.obtain();
        newParcel.writeInt(id.length);
        newParcel.writeByteArray(id);
        newParcel.writeInt(oldTechList.length);
        newParcel.writeIntArray(oldTechList);
        newParcel.writeTypedArray(oldTechExtras, 0);
        newParcel.writeInt(serviceHandle);
        newParcel.writeInt(isMock);
        if (isMock == 0) {
            newParcel.writeStrongBinder(tagService);
        }
        newParcel.setDataPosition(0);
        newParcel.recycle();
        return (Tag) Tag.CREATOR.createFromParcel(newParcel);
    }

    public static MCReader get(Tag tag) {
        MCReader mcr = null;
        if (tag != null) {
            mcr = new MCReader(tag);
            if (!mcr.isMifareClassic()) {
                return null;
            }
        }
        return mcr;
    }

    public SparseArray<String[]> readAsMuchAsPossible(SparseArray<byte[][]> keyMap) {
        if (keyMap == null || keyMap.size() <= 0) {
            return null;
        }
        SparseArray<String[]> resultSparseArray = new SparseArray<>(keyMap.size());
        int i = 0;
        while (i < keyMap.size()) {
            String[][] results = new String[2][];
            try {
                if (keyMap.valueAt(i)[0] != null) {
                    close();
                    results[0] = readSector(keyMap.keyAt(i), keyMap.valueAt(i)[0], false);
                }
                if (keyMap.valueAt(i)[1] != null) {
                    close();
                    results[1] = readSector(keyMap.keyAt(i), keyMap.valueAt(i)[1], true);
                }
                if (results[0] != null || results[1] != null) {
                    resultSparseArray.put(keyMap.keyAt(i), mergeSectorData(results[0], results[1]));
                }
                i++;
            } catch (TagLostException e) {
                return null;
            }
        }
        return resultSparseArray;
    }

    public SparseArray<String[]> readAsMuchAsPossible() {
        this.mKeyMapStatus = getSectorCount();
        do {
        } while (buildNextKeyMapPart() < getSectorCount() - 1);
        close();
        return readAsMuchAsPossible(this.mKeyMap);
    }

    public String[] readSector(int sectorIndex, byte[] key, boolean useAsKeyB) throws TagLostException {
        if (!authenticate(sectorIndex, key, useAsKeyB)) {
            return null;
        }
        ArrayList<String> blocks = new ArrayList<>();
        int firstBlock = this.mMFC.sectorToBlock(sectorIndex);
        int lastBlock = firstBlock + 4;
        if (this.mMFC.getSize() == 4096 && sectorIndex > 31) {
            lastBlock = firstBlock + 16;
        }
        int i = firstBlock;
        while (i < lastBlock) {
            try {
                byte[] blockBytes = this.mMFC.readBlock(i);
                if (blockBytes.length < 16) {
                    throw new IOException();
                }
                if (blockBytes.length > 16) {
                    blockBytes = Arrays.copyOf(blockBytes, 16);
                }
                blocks.add(C0372util.byte2HexString(blockBytes));
                i++;
            } catch (TagLostException e) {
                throw e;
            } catch (IOException e2) {
                Log.d(LOG_TAG, "(Recoverable) Error while reading block " + i + " from tag.");
                blocks.add(NO_DATA);
                if (!this.mMFC.isConnected()) {
                    throw new TagLostException("Tag removed during readSector(...)");
                }
                authenticate(sectorIndex, key, useAsKeyB);
            }
        }
        String[] ret = (String[]) blocks.toArray(new String[blocks.size()]);
        int last = ret.length - 1;
        if (!useAsKeyB) {
            if (isKeyBReadable(C0372util.hexStringToByteArray(ret[last].substring(12, 20)))) {
                ret[last] = C0372util.byte2HexString(key) + ret[last].substring(12, 32);
                return ret;
            }
            ret[last] = C0372util.byte2HexString(key) + ret[last].substring(12, 20) + NO_KEY;
            return ret;
        } else if (ret[0].equals(NO_DATA)) {
            return null;
        } else {
            ret[last] = NO_KEY + ret[last].substring(12, 20) + C0372util.byte2HexString(key);
            return ret;
        }
    }

    public int writeBlock(int sectorIndex, int blockIndex, byte[] data, byte[] key, boolean useAsKeyB) {
        if (getSectorCount() - 1 < sectorIndex) {
            return 1;
        }
        if (this.mMFC.getBlockCountInSector(sectorIndex) - 1 < blockIndex) {
            return 2;
        }
        if (data.length != 16) {
            return 3;
        }
        if (!authenticate(sectorIndex, key, useAsKeyB)) {
            return 4;
        }
        try {
            this.mMFC.writeBlock(this.mMFC.sectorToBlock(sectorIndex) + blockIndex, data);
            return 0;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while writing block to tag.", e);
            return -1;
        }
    }

    public int writeValueBlock(int sectorIndex, int blockIndex, int value, boolean increment, byte[] key, boolean useAsKeyB) {
        if (getSectorCount() - 1 < sectorIndex) {
            return 1;
        }
        if (this.mMFC.getBlockCountInSector(sectorIndex) - 1 < blockIndex) {
            return 2;
        }
        if (!authenticate(sectorIndex, key, useAsKeyB)) {
            return 3;
        }
        int block = this.mMFC.sectorToBlock(sectorIndex) + blockIndex;
        if (increment) {
            try {
                this.mMFC.increment(block, value);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while writing Value Block to tag.", e);
                return -1;
            }
        } else {
            this.mMFC.decrement(block, value);
        }
        this.mMFC.transfer(block);
        return 0;
    }

    public int buildNextKeyMapPart() {
        boolean error = false;
        if (this.mKeysWithOrder == null || this.mLastSector == -1) {
            error = true;
        } else {
            if (this.mKeyMapStatus == this.mLastSector + 1) {
                this.mKeyMapStatus = this.mFirstSector;
                this.mKeyMap = new SparseArray<>();
            }
            byte[][] keys = new byte[2][];
            boolean[] foundKeys = {false, false};
            int i = 0;
            while (true) {
                if (i >= this.mKeysWithOrder.size()) {
                    break;
                }
                byte[] key = this.mKeysWithOrder.get(i);
                try {
                    if (!foundKeys[0]) {
                        boolean auth = this.mMFC.authenticateSectorWithKeyA(this.mKeyMapStatus, key);
                        if (1 != 0 && !auth) {
                            auth = this.mMFC.authenticateSectorWithKeyA(this.mKeyMapStatus, key);
                        }
                        if (auth) {
                            keys[0] = key;
                            foundKeys[0] = true;
                        }
                    }
                    if (!foundKeys[1]) {
                        boolean auth2 = this.mMFC.authenticateSectorWithKeyB(this.mKeyMapStatus, key);
                        if (1 != 0 && !auth2) {
                            auth2 = this.mMFC.authenticateSectorWithKeyB(this.mKeyMapStatus, key);
                        }
                        if (auth2) {
                            keys[1] = key;
                            foundKeys[1] = true;
                        }
                    }
                    if (foundKeys[0] && foundKeys[1]) {
                        break;
                    }
                    i++;
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Error while building next key map part");
                    if (1 == 0) {
                        error = true;
                        break;
                    }
                    Log.d(LOG_TAG, "Auto reconnect is enabled");
                    while (!isConnected()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                        }
                        try {
                            connect();
                        } catch (IOException e3) {
                        }
                    }
                }
            }
            if (!error && (foundKeys[0] || foundKeys[1])) {
                this.mKeyMap.put(this.mKeyMapStatus, keys);
                if (foundKeys[0]) {
                    this.mKeysWithOrder.remove(keys[0]);
                    this.mKeysWithOrder.add(0, keys[0]);
                }
                if (foundKeys[1]) {
                    this.mKeysWithOrder.remove(keys[1]);
                    this.mKeysWithOrder.add(0, keys[1]);
                }
            }
            this.mKeyMapStatus++;
        }
        if (!error) {
            return this.mKeyMapStatus - 1;
        }
        this.mKeyMapStatus = 0;
        this.mKeyMap = null;
        return -1;
    }

    public String[] mergeSectorData(String[] firstResult, String[] secondResult) {
        String[] ret = null;
        if (!(firstResult == null && secondResult == null)) {
            if (firstResult != null && secondResult != null && firstResult.length != secondResult.length) {
                return null;
            }
            int length = firstResult != null ? firstResult.length : secondResult.length;
            ArrayList<String> blocks = new ArrayList<>();
            for (int i = 0; i < length - 1; i++) {
                if (firstResult != null && firstResult[i] != null && !firstResult[i].equals(NO_DATA)) {
                    blocks.add(firstResult[i]);
                } else if (secondResult == null || secondResult[i] == null || secondResult[i].equals(NO_DATA)) {
                    blocks.add(NO_DATA);
                } else {
                    blocks.add(secondResult[i]);
                }
            }
            ret = (String[]) blocks.toArray(new String[(blocks.size() + 1)]);
            int last = length - 1;
            if (firstResult != null && firstResult[last] != null && !firstResult[last].equals(NO_DATA)) {
                ret[last] = firstResult[last];
                if (!(secondResult == null || secondResult[last] == null || secondResult[last].equals(NO_DATA))) {
                    ret[last] = ret[last].substring(0, 20) + secondResult[last].substring(20);
                }
            } else if (secondResult == null || secondResult[last] == null || secondResult[last].equals(NO_DATA)) {
                ret[last] = NO_DATA;
            } else {
                ret[last] = secondResult[last];
            }
        }
        return ret;
    }

    public boolean setKeys() {
        HashSet<byte[]> keys = new HashSet<>();
        String[] lines = {"425B188AD7CE", "52CCA5CACB6B", "A947D6D12BC6", "92E311E6C2AA", "B96FE33242BC", "63A410E793B7", "19D892CBBE28", "CBA0231B44A6", "36B89DE547B6", "EA9B17176057", "3B8A13FA8392", "9EF4B3AED7CD", "D6DA78408721", "D29C261D1E06", "65EAC2F341B3", "A0A1A2A3A4A5", "21AD4E57DF28", "382CABE7248F", "B3F3A0C5A1CC", "173CE6D6AE1A", "7E269EAC1DA5", "D664D3F0688E", "E3952D9CB2A9", "84C995317FA4", "42C737855417", "5AEBD132E93F", "B54850EC3BE4", "686973EB4840", "385DA4F7BB8A", "5C261C15D133", "CEB3DBB334D0", "97583397789B"};
        if (lines != null) {
            int length = lines.length;
            int i = 0;
            while (i < length) {
                try {
                    keys.add(C0372util.hexStringToByteArray(lines[i]));
                    i++;
                } catch (OutOfMemoryError e) {
                    return false;
                }
            }
        }
        if (keys.size() > 0) {
            this.mKeysWithOrder = new ArrayList<>(keys);
        }
        return true;
    }

    public boolean setKeyFile(File[] keyFiles, Context context) {
        HashSet<byte[]> keys = new HashSet<>();
        String[] lines = {"AAA", "AAA", "AAAA"};
        if (lines != null) {
            for (String line : lines) {
                if (!line.equals("") && line.length() == 12 && line.matches("[0-9A-Fa-f]+")) {
                    try {
                        keys.add(C0372util.hexStringToByteArray(line));
                    } catch (OutOfMemoryError e) {
                        return false;
                    }
                }
            }
        }
        if (keys.size() > 0) {
            this.mKeysWithOrder = new ArrayList<>(keys);
        }
        return true;
    }

    public boolean setMappingRange(int firstSector, int lastSector) {
        if (firstSector < 0 || lastSector >= getSectorCount() || firstSector > lastSector) {
            return false;
        }
        this.mFirstSector = firstSector;
        this.mLastSector = lastSector;
        this.mKeyMapStatus = lastSector + 1;
        return true;
    }

    private boolean authenticate(int sectorIndex, byte[] key, boolean useAsKeyB) {
        try {
            this.mMFC.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!useAsKeyB) {
            try {
                boolean ret = this.mMFC.authenticateSectorWithKeyA(sectorIndex, key);
                return (1 == 0 || ret) ? ret : this.mMFC.authenticateSectorWithKeyA(sectorIndex, key);
            } catch (IOException e2) {
                Log.d(LOG_TAG, "Error authenticating with tag.");
                return false;
            }
        } else {
            boolean ret2 = this.mMFC.authenticateSectorWithKeyB(sectorIndex, key);
            if (1 == 0 || ret2) {
                return ret2;
            }
            return this.mMFC.authenticateSectorWithKeyB(sectorIndex, key);
        }
    }

    public boolean authenticate2(int sectorIndex, byte[] key, boolean useAsKeyB) {
        try {
            this.mMFC.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!useAsKeyB) {
            try {
                boolean ret = this.mMFC.authenticateSectorWithKeyA(sectorIndex, key);
                return (1 == 0 || ret) ? ret : this.mMFC.authenticateSectorWithKeyA(sectorIndex, key);
            } catch (IOException e2) {
                Log.d(LOG_TAG, "Error authenticating with tag.");
                return false;
            }
        } else {
            boolean ret2 = this.mMFC.authenticateSectorWithKeyB(sectorIndex, key);
            if (1 == 0 || ret2) {
                return ret2;
            }
            return this.mMFC.authenticateSectorWithKeyB(sectorIndex, key);
        }
    }

    private boolean isKeyBReadable(byte[] ac) {
        byte c1 = (byte) ((ac[1] & 128) >>> 7);
        byte c2 = (byte) ((ac[2] & 8) >>> 3);
        byte c3 = (byte) ((ac[2] & 128) >>> 7);
        if (c1 == 0 && c2 == 0 && c3 == 0) {
            return true;
        }
        if (c2 == 1 && c3 == 0) {
            return true;
        }
        if (c2 == 0 && c3 == 1) {
            return true;
        }
        return false;
    }

    public SparseArray<byte[][]> getKeyMap() {
        return this.mKeyMap;
    }

    public boolean isMifareClassic() {
        return this.mMFC != null;
    }

    public int getSize() {
        return this.mMFC.getSize();
    }

    public int getSectorCount() {
        return this.mMFC.getSectorCount();
    }

    public int getBlockCount() {
        return this.mMFC.getBlockCount();
    }

    public int getBlockCountInSector(int sectorIndex) {
        return this.mMFC.getBlockCountInSector(sectorIndex);
    }

    public boolean isConnected() {
        return this.mMFC.isConnected();
    }

    public void connect() throws IOException {
        try {
            this.mMFC.connect();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error while connecting to tag.");
            throw e;
        }
    }

    public void close() {
        try {
            this.mMFC.close();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error on closing tag.");
        }
    }
}
