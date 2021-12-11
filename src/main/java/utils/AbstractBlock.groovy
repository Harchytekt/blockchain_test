package utils

abstract class AbstractBlock {
    String hash
    String previousHash
    private long timeStamp
    private int nonce

    AbstractBlock(String previousHash) {
        this.previousHash = previousHash
        this.timeStamp = new Date().toInstant().toEpochMilli()
    }

    long getTimeStamp() {
        return timeStamp
    }

    int getNonce() {
        return nonce
    }

    void setNonce(int nonce) {
        this.nonce = nonce
    }

    static String calculateHash(String dataToHash) {
        return dataToHash.digest('SHA-256')
    }

    abstract void mineBlock(int difficulty)
}
