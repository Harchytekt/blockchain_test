package coin

class BlockCoin {
    String hash
    String previousHash
    String merkleRoot
    List<Transaction> transactions = new ArrayList<>()
    long timeStamp
    int nonce

    BlockCoin(String previousHash) {
        this.previousHash = previousHash
        this.timeStamp = new Date().toInstant().toEpochMilli()
        this.hash = calculateHash()
    }

    String calculateHash() {
        return "$previousHash$timeStamp$nonce$merkleRoot".digest('SHA-256')
    }

    void mineBlock(int difficulty) {
        merkleRoot = StringUtils.getMerkleRoot(transactions)
        String target = StringUtils.getDifficultyString(difficulty)
        while (hash.substring(0, difficulty) != target) {
            nonce++
            hash = calculateHash()
        }
        println "simple.Block mined!"
    }

    boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false
        }

        if (previousHash != "0" && !transaction.processTransaction()) {
            println "coin.Transaction failed to process. Discarded."
            return false
        }

        transactions.add(transaction)
        println "coin.Transaction successfully added to coin.BlockCoin."
        return true
    }
}