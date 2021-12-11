package coin

import org.bouncycastle.jce.provider.BouncyCastleProvider
import utils.AbstractTest
import utils.StringUtils

import java.security.Security

class CoinTest extends AbstractTest {
    static HashMap<String, TransactionOutput> UTXOs = new HashMap<>()
    static float minimumTransaction = 0.1f
    Wallet walletA
    Wallet walletB
    Transaction firstTransaction

    CoinTest() {
        this.blockchain = new LinkedList<BlockCoin>()
    }

    CoinTest(int difficulty) {
        this()
        this.difficulty = difficulty
    }

    @Override
    void launchTest() {
        // Setup Bouncey castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider())

        // Create the new wallets
        walletA = new Wallet()
        walletB = new Wallet()
        Wallet coinbase = new Wallet()

        // Create first transaction which will send 100 coins to walletA
        firstTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100, null)
        firstTransaction.generateSignature(coinbase.privateKey)
        firstTransaction.transactionId = "0"
        firstTransaction.outputs.add(new TransactionOutput(firstTransaction.recipient, firstTransaction.value, firstTransaction.transactionId))
        UTXOs.put(firstTransaction.outputs.get(0).id, firstTransaction.outputs.get(0))

        println "Creating and mining first block…"
        BlockCoin firstBlock = new BlockCoin("0")
        firstBlock.addTransaction(firstTransaction)
        addBlock(firstBlock)

        // Testing
        BlockCoin block1 = new BlockCoin(firstBlock.hash)
        println "walletA's balance is: ${walletA.balance}"
        println "walletA is attempting to send funds (42) to walletB"
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 42f))
        addBlock(block1)
        println "walletA's balance is: ${walletA.balance}"
        println "walletB's balance is: ${walletB.balance}"

        BlockCoin block2 = new BlockCoin(block1.hash)
        println "walletA is attempting to send more funds than it posses (1000)…"
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f))
        addBlock(block2)
        println "walletA's balance is: ${walletA.balance}"
        println "walletB's balance is: ${walletB.balance}"

        BlockCoin block3 = new BlockCoin(block2.hash)
        println "walletB is attempting to send funds (21) to walletA"
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 21f))
        addBlock(block3)
        println "walletA's balance is: ${walletA.balance}"
        println "walletB's balance is: ${walletB.balance}"

        isChainValid()
    }

    @Override
    boolean isChainValid() {
        BlockCoin currentBlock
        BlockCoin previousBlock
        String hashTarget = StringUtils.getDifficultyString(difficulty)
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>()
        tempUTXOs.put(firstTransaction.outputs.get(0).id, firstTransaction.outputs.get(0))

        for (i in 1..<blockchain.size()) {
            currentBlock = blockchain.get(i)
            previousBlock = blockchain.get(i - 1)

            if (currentBlock.hash != currentBlock.calculateHash() || previousBlock.hash != currentBlock.previousHash) {
                println "Hashes are not equal."
                if (currentBlock.hash != currentBlock.calculateHash()) {
                    println "CurrentBlock"
                }
                if (previousBlock.hash != currentBlock.previousHash) {
                    println "PreviousBlock: ${previousBlock.hash} -- ${currentBlock.previousHash}"
                }
                return false
            }

            if (currentBlock.hash.substring(0, difficulty) != hashTarget) {
                println "This block hasn't been mined."
                return false
            }

            TransactionOutput tempOutput
            for (t in 0..<currentBlock.transactions.size()) {
                Transaction currentTransaction = currentBlock.transactions.get(t)

                if (!currentTransaction.verifySignature()) {
                    println "Signature is invalid for Transaction($t)"
                    return false
                }

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    println "Inpts are not equal to outputs for Transaction($t)"
                    return false
                }

                currentTransaction.inputs.each { input ->
                    tempOutput = tempUTXOs.get(input.transactionOutputId)

                    if (tempOutput == null) {
                        println "Referenced input on Transaction($t) is missing"
                        return false
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        println "Referenced input Transaction($t) value is invalid"
                        return false
                    }

                    tempUTXOs.remove(input.transactionOutputId)
                }

                currentTransaction.outputs.each { output ->
                    tempUTXOs.put(output.id, output)
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    println "Transaction($t) output recipient is not who it should be"
                    return false
                }

                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    println "Transaction($t) output 'change' is not sender"
                    return false
                }
            }

        }

        println "Blockchain is valid"
        return true
    }
}