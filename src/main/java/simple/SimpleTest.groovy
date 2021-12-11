package simple

import groovy.json.JsonOutput
import utils.AbstractTest

class SimpleTest extends AbstractTest {
    private int blockchainSize

    SimpleTest(int blockchainSize) {
        this.blockchain = new LinkedList<SimpleBlock>()
        this.blockchainSize = blockchainSize
    }

    SimpleTest(int blockchainSize, int difficulty) {
        this(blockchainSize)
        this.difficulty = difficulty
    }

    @Override
    void launchTest() {
        for (int i in 0..blockchainSize - 1) {
            String hash = i == 0 ? "0" : blockchain.last().hash
            blockchain.add(new SimpleBlock("Block #$i", hash))
            println "Trying to mine block ${i + 1}"
            blockchain.get(i).mineBlock(difficulty)
        }

        isChainValid()

        println "\nThe blockchain:"
        String json = JsonOutput.toJson(blockchain)
        println JsonOutput.prettyPrint(json)
    }

    @Override
    boolean isChainValid() {
        SimpleBlock currentBlock
        String previousHash

        for (i in 0..<blockchain.size()) {
            currentBlock = blockchain.get(i)
            previousHash = i == 0 ? "0" : blockchain.get(i - 1).hash

            if (currentBlock.hash != currentBlock.calculateHash()) {
                println "Current hashes not equal"
                return false
            }

            if (previousHash != currentBlock.previousHash) {
                println "Previous hashes not equal"
                return false
            }
        }

        println "\nBlockchain is valid"
        return true
    }
}