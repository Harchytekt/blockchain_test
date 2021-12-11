package coin

import utils.AbstractBlock
import utils.StringUtils

class BlockCoin extends AbstractBlock {
    String merkleRoot
    List<Transaction> transactions = new ArrayList<>()

    BlockCoin(String previousHash) {
        super(previousHash)

        this.hash = calculateHash()
    }

    String calculateHash() {
        return calculateHash("$previousHash$timeStamp$nonce$merkleRoot")
    }

    @Override
    void mineBlock(int difficulty) {
        merkleRoot = StringUtils.getMerkleRoot(transactions)
        String target = StringUtils.getDifficultyString(difficulty)
        while (hash.substring(0, difficulty) != target) {
            nonce++
            hash = calculateHash()
        }
        println "Block mined!"
    }

    boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false
        }

        if (previousHash != "0" && !transaction.processTransaction()) {
            println "Transaction failed to process. Discarded."
            return false
        }

        transactions.add(transaction)
        println "Transaction successfully added to BlockCoin."
        return true
    }
}