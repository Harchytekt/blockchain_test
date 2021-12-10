class Block {
    String hash
    String previousHash
    private String data
    private long timeStamp
    private int nonce

    Block(String data, String previousHash) {
        this.data = data
        this.previousHash = previousHash
        this.timeStamp = new Date().toInstant().toEpochMilli()
        this.hash = calculateHash()
    }

    String getData() {
        return data
    }

    void setData(String data) {
        this.data = data
    }

    long getTimeStamp() {
        return timeStamp
    }

    int getNonce() {
        return nonce
    }

    String calculateHash() {
        return "$previousHash$timeStamp$nonce$data".digest('SHA-256')
    }

    void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0')
        while (hash.substring(0, difficulty) != target) {
            nonce++
            hash = calculateHash()
        }
        println "Block mined!"
    }
}