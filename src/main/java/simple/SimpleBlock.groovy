package simple

import utils.AbstractBlock
import utils.StringUtils

class SimpleBlock extends AbstractBlock {
    private String data

    SimpleBlock(String data, String previousHash) {
        super(previousHash)

        this.data = data
        this.hash = calculateHash()
    }

    String getData() {
        return data
    }

    void setData(String data) {
        this.data = data
    }

    String calculateHash() {
        return calculateHash("$previousHash$timeStamp$nonce$data")
    }

    @Override
    void mineBlock(int difficulty) {
        String target = StringUtils.getDifficultyString(difficulty)
        while (hash.substring(0, difficulty) != target) {
            nonce++
            hash = calculateHash()
        }
        println "Block mined!"
    }
}