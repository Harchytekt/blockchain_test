import groovy.json.JsonOutput

class Main {
    static List<Block> blockchain = new ArrayList<>()
    static int difficulty = 1

    static void main(String[] args) {
        for (int i in 0..5) {
            String hash = i == 0 ? "0" : blockchain.last().hash
            blockchain.add(new Block("Block #$i", hash))
            println "Trying to mine block ${i + 1}"
            blockchain.get(i).mineBlock(difficulty)
        }

        //blockchain.get(0).setData("Hello!")

        println "\nBlockchain is valid: ${isChainValid()}"

        println "\nThe blockchain:"
        String json = JsonOutput.toJson(blockchain)
        println JsonOutput.prettyPrint(json)
    }

    static boolean isChainValid() {
        Block currentBlock
        Block previousBlock

        for (i in 0..<blockchain.size()) {
            currentBlock = blockchain.get(i)

            if (currentBlock.hash != currentBlock.calculateHash()) {
                println "Current hashes not equal"
                return false
            }

            if (i > 0) {
                previousBlock = blockchain.get(i - 1)
                if (previousBlock.hash != currentBlock.previousHash) {
                    println "Previous hashes not equal"
                    return false
                }
            }
        }
        return true
    }
}