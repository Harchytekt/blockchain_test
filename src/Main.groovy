import groovy.json.JsonOutput

class Main {
    static Deque<Block> blockchain = new LinkedList<>()
    static int difficulty = 1

    static void main(String[] args) {
        for (int i in 0..5) {
            String hash = i == 0 ? "0" : blockchain.last().hash
            blockchain.add(new Block("Block #$i", hash))
            println "Trying to mine block ${i + 1}"
            blockchain.get(i).mineBlock(difficulty)
        }

        println "\nBlockchain is valid: ${isChainValid()}"

        /*println "\nThe blockchain:"
        String json = JsonOutput.toJson(blockchain)
        println JsonOutput.prettyPrint(json)*/
    }

    static boolean isChainValid() {
        Block currentBlock
        String previousHash

        for (i in 0..<blockchain.size()) {
            currentBlock = blockchain.get(i)
            previousHash = i == 0 ? "0" : blockchain.get(i-1).hash

            if (currentBlock.hash != currentBlock.calculateHash()) {
                println "Current hashes not equal"
                return false
            }

            if (previousHash != currentBlock.previousHash) {
                println "Previous hashes not equal"
                return false
            }
        }
        return true
    }
}